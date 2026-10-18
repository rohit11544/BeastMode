/**
 * Pub-Sub Messaging Queue (Kafka L4–sized) — Interview PSEUDOCODE
 *
 * How you'll use it in interview:
 *   1. FR / NFR (speak)
 *   2. Entities + publish/consume flow (speak / draw)
 *   3. Write THIS style of code
 *   4. Patterns + SOLID + concurrency (speak)
 *
 * Patterns: Strategy · Facade · Producer–Consumer (pull) · Coordinator
 * NOT Observer — consumers poll; broker does not push
 * Lombok: @Getter / @Setter / @AllArgsConstructor — skip accessors
 * Compile: NOT meant to compile — interview sketch only.
 */

// ============================================================
// DOMAIN
// ============================================================

@Getter
@AllArgsConstructor
class Message {
    private final String key;
    private final String payload;
    private final long offset;
}

// ============================================================
// STRATEGY — which partition gets this key
// ============================================================

interface PartitionStrategy {
    int getPartition(String key, int totalPartitions);
}

class HashPartitionStrategy implements PartitionStrategy {
    int getPartition(String key, int totalPartitions) {
        if (key == null) return 0;
        return Math.abs(key.hashCode()) % totalPartitions;
    }
}

// Optional extensions (OCP — don't rewrite Topic):
// class RoundRobinPartitionStrategy implements PartitionStrategy { }
// class StickyPartitionStrategy implements PartitionStrategy { }

// ============================================================
// PARTITION — in-memory log
// ============================================================

class Partition {
    private final int id;
    private final List<Message> messages = new ArrayList<>();
    private final AtomicLong nextOffset = new AtomicLong();
    // ReentrantReadWriteLock: shared reads, exclusive append

    Partition(int id) { this.id = id; }

    int getId() { return id; }

    void append(String key, String payload) {
        // writeLock
        messages.add(new Message(key, payload, nextOffset.getAndIncrement()));
    }

    List<Message> read(long offset, int batchSize) {
        // readLock
        List<Message> batch = new ArrayList<>();
        for (Message m : messages) {
            if (m.getOffset() >= offset) {
                batch.add(m);
                if (batch.size() == batchSize) break;
            }
        }
        return batch;
    }
}

// ============================================================
// TOPIC
// ============================================================

@Getter
class Topic {
    private final String name;
    private final List<Partition> partitions = new ArrayList<>();
    private final PartitionStrategy strategy;

    Topic(String name, int partitionCount, PartitionStrategy strategy) {
        this.name = name;
        this.strategy = strategy;
        for (int i = 0; i < partitionCount; i++) {
            partitions.add(new Partition(i));
        }
    }

    Partition getPartition(String key) {
        int idx = strategy.getPartition(key, partitions.size());
        return partitions.get(idx);
    }
}

// ============================================================
// BROKER — Facade
// ============================================================

class Broker {
    private final Map<String, Topic> topics = new ConcurrentHashMap<>();

    void createTopic(String name, int partitions) {
        topics.put(name, new Topic(name, partitions, new HashPartitionStrategy()));
    }

    void publish(String topic, String key, String payload) {
        topics.get(topic).getPartition(key).append(key, payload);
    }

    List<Message> poll(String topic, int partition, long offset, int batchSize) {
        return topics.get(topic).getPartitions().get(partition).read(offset, batchSize);
    }

    Topic getTopic(String name) { return topics.get(name); }
}

// ============================================================
// PRODUCER / CONSUMER
// ============================================================

@AllArgsConstructor
class Producer {
    private final Broker broker;

    void send(String topic, String key, String payload) {
        broker.publish(topic, key, payload);
    }
}

class Consumer {
    private final String id;
    private final Set<Integer> assignedPartitions = ConcurrentHashMap.newKeySet();

    Consumer(String id) { this.id = id; }

    String getId() { return id; }

    void assign(int partition) { assignedPartitions.add(partition); }
    void clearAssignments() { assignedPartitions.clear(); }

    // PULL — not Observer push
    void poll(Broker broker, ConsumerGroup group, int batchSize) {
        for (int partition : assignedPartitions) {
            long offset = group.getOffset(partition);
            List<Message> batch = broker.poll(group.getTopic(), partition, offset, batchSize);

            for (Message m : batch) {
                print(id + " processed: " + m.getPayload());
            }

            // commit after process (at-least-once story if crash before commit)
            group.commitOffset(partition, offset + batch.size());
        }
    }
}

// ============================================================
// CONSUMER GROUP + COORDINATOR
// ============================================================

class ConsumerGroup {
    private final String topic;
    private final List<Consumer> consumers = new ArrayList<>();
    private final Map<Integer, AtomicLong> offsets = new ConcurrentHashMap<>();

    ConsumerGroup(String topic) { this.topic = topic; }

    String getTopic() { return topic; }
    List<Consumer> getConsumers() { return consumers; }

    void addConsumer(Consumer c) { consumers.add(c); }

    long getOffset(int partition) {
        return offsets.computeIfAbsent(partition, k -> new AtomicLong()).get();
    }

    void commitOffset(int partition, long offset) {
        offsets.computeIfAbsent(partition, k -> new AtomicLong()).set(offset);
    }
}

class GroupCoordinator {
    // Round-robin assign partitions across consumers in the group
    void rebalance(ConsumerGroup group, Topic topic) {
        List<Consumer> consumers = group.getConsumers();
        for (Consumer c : consumers) c.clearAssignments();

        List<Partition> partitions = topic.getPartitions();
        for (int i = 0; i < partitions.size(); i++) {
            Consumer c = consumers.get(i % consumers.size());
            c.assign(partitions.get(i).getId());
        }
    }
}

// ============================================================
// MAIN (happy path sketch)
// ============================================================

class Main {
    void demo() {
        Broker broker = new Broker();
        broker.createTopic("orders", 2);

        Producer producer = new Producer(broker);
        producer.send("orders", "user1", "Order Created");
        producer.send("orders", "user2", "Order Paid");
        producer.send("orders", "user1", "Order Delivered"); // same key → same partition

        ConsumerGroup group = new ConsumerGroup("orders");
        Consumer c1 = new Consumer("Worker-1");
        Consumer c2 = new Consumer("Worker-2");
        group.addConsumer(c1);
        group.addConsumer(c2);

        new GroupCoordinator().rebalance(group, broker.getTopic("orders"));

        c1.poll(broker, group, 10);
        c2.poll(broker, group, 10);
    }
}

// ============================================================
// INTERVIEW SPEAK — Requirements · Patterns · SOLID
// ============================================================
//
// REQUIREMENTS
// FR: create topic; publish(key,payload); consumer group poll; commit offset; rebalance
// NFR: thread-safe partition log; in-memory OK for LLD; same key → same partition
//
// DESIGN PATTERNS
// Facade     — Broker (createTopic / publish / poll)
// Strategy   — PartitionStrategy (Hash; add RoundRobin without editing Topic)
// Producer–Consumer — append vs pull poll
// Coordinator — GroupCoordinator.rebalance()
// NOT Observer — consumer explicitly polls (pull, not push)
//
// SOLID
// S — Producer=publish · Broker=route · Topic=partitions · Partition=log
//     ConsumerGroup=offsets · GroupCoordinator=rebalance · Strategy=pick partition
// O — new PartitionStrategy without rewriting Topic/Broker
// L — any PartitionStrategy plugs into Topic
// I — Strategy only getPartition(key, n)
// D — Topic depends on PartitionStrategy, not HashPartitionStrategy
//
// FLOW (speak)
// Publish:  Producer → Broker.publish → Topic → Strategy → Partition.append
// Consume:  Consumer.poll → Group.getOffset → Broker.poll → process → commitOffset
//
// FOLLOW-UPS
// Crash before commit → at-least-once (may reprocess)
// Production: disk segments, durable offsets, multi-broker quorum ack
// Partition.read O(N) ok for LLD; prod uses indexes
//
// ============================================================
// SKELETON — blank for interview coding
// ============================================================
/*
class Message { }

interface PartitionStrategy { }
class HashPartitionStrategy implements PartitionStrategy { }

class Partition { }
class Topic { }
class Broker { }

class Producer { }
class Consumer { }
class ConsumerGroup { }
class GroupCoordinator { }

class Main { }
*/
