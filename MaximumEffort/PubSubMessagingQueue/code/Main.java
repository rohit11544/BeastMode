import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Google L4 Sized Pub-Sub (Kafka Inspired) — ONE FILE
 * Patterns: Strategy + Facade (Broker) + Producer-Consumer
 *
 * Production shape: broker owns topics, consumer groups, offsets, and rebalance
 * (like Kafka broker + group coordinator). Producer/Consumer are client apps.
 *
 *   javac Main.java && java Main
 */

// ========== DOMAIN ==========

class Message {
    private final String key;
    private final String payload;
    private final long offset;

    Message(String key, String payload, long offset) {
        this.key = key;
        this.payload = payload;
        this.offset = offset;
    }

    String getKey() { return key; }
    String getPayload() { return payload; }
    long getOffset() { return offset; }
}

// ========== STRATEGY ==========

interface PartitionStrategy {
    int getPartition(String key, int totalPartitions);
}

class HashPartitionStrategy implements PartitionStrategy {
    @Override
    public int getPartition(String key, int totalPartitions) {
        if (key == null) return 0;
        return Math.abs(key.hashCode()) % totalPartitions;
    }
}

// ========== PARTITION ==========

class Partition {
    private final int id;
    private final List<Message> messages = new ArrayList<>();
    private final AtomicLong nextOffset = new AtomicLong();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    Partition(int id) {
        this.id = id;
    }

    int getId() { return id; }

    void append(String key, String payload) {
        lock.writeLock().lock();
        try {
            messages.add(new Message(key, payload, nextOffset.getAndIncrement()));
        } finally {
            lock.writeLock().unlock();
        }
    }

    List<Message> read(long offset, int batchSize) {
        lock.readLock().lock();
        try {
            List<Message> ans = new ArrayList<>();
            for (Message m : messages) {
                if (m.getOffset() >= offset) {
                    ans.add(m);
                    if (ans.size() == batchSize) break;
                }
            }
            return ans;
        } finally {
            lock.readLock().unlock();
        }
    }
}

// ========== TOPIC ==========

class Topic {
    private final String name;
    private final List<Partition> partitions = new ArrayList<>();
    private final PartitionStrategy strategy;

    Topic(String name, int count, PartitionStrategy strategy) {
        this.name = name;
        this.strategy = strategy;
        for (int i = 0; i < count; i++) {
            partitions.add(new Partition(i));
        }
    }

    String getName() { return name; }

    List<Partition> getPartitions() { return partitions; }

    Partition getPartition(String key) {
        int idx = strategy.getPartition(key, partitions.size());
        return partitions.get(idx);
    }
}

// ========== CONSUMER (client app) — before ConsumerGroup (rebalance assigns consumers) ==========

class Consumer {
    private final String id;
    private final Set<Integer> assignedPartitions = ConcurrentHashMap.newKeySet();

    Consumer(String id) {
        this.id = id;
    }

    String getId() { return id; }

    void assignPartition(int partition) {
        assignedPartitions.add(partition);
    }

    void clearAssignments() {
        assignedPartitions.clear();
    }

    Set<Integer> getAssignedPartitions() {
        return assignedPartitions;
    }

    void poll(Broker broker, String groupId, int batchSize) {
        String topic = broker.getTopicForGroup(groupId);
        for (int partition : assignedPartitions) {
            long offset = broker.getCommittedOffset(groupId, partition);
            List<Message> batch = broker.poll(topic, partition, offset, batchSize);

            for (Message m : batch) {
                System.out.println(id + " processed : " + m.getPayload());
            }

            broker.commitOffset(groupId, partition, offset + batch.size());
        }
    }
}

/**
 * Consumer group state stored on the broker (Kafka: group id + __consumer_offsets).
 * Members are client Consumer instances; offsets are per partition for the whole group.
 */
class ConsumerGroup {
    private final String topic;
    private final List<Consumer> members = new ArrayList<>();
    private final Map<Integer, AtomicLong> offsets = new ConcurrentHashMap<>();

    ConsumerGroup(String topic) {
        this.topic = topic;
    }

    String getTopic() { return topic; }

    void addMember(Consumer consumer) {
        members.add(consumer);
    }

    long getCommittedOffset(int partition) {
        return offsets.computeIfAbsent(partition, k -> new AtomicLong()).get();
    }

    void commitOffset(int partition, long offset) {
        offsets.computeIfAbsent(partition, k -> new AtomicLong()).set(offset);
    }

    void rebalance(List<Partition> partitions) {
        if (members.isEmpty()) {
            throw new IllegalStateException("No consumers in group for topic: " + topic);
        }
        for (Consumer c : members) {
            c.clearAssignments();
        }
        for (int i = 0; i < partitions.size(); i++) {
            members.get(i % members.size()).assignPartition(partitions.get(i).getId());
        }
    }
}

// ========== BROKER (registry + routing; groups owned here, logic in ConsumerGroup) ==========

class Broker {
    private final Map<String, Topic> topics = new ConcurrentHashMap<>();
    private final Map<String, ConsumerGroup> consumerGroups = new ConcurrentHashMap<>();

    void createTopic(String name, int partitions) {
        topics.put(name, new Topic(name, partitions, new HashPartitionStrategy()));
    }

    void createConsumerGroup(String groupId, String topicName) {
        if (!topics.containsKey(topicName)) {
            throw new IllegalArgumentException("Unknown topic: " + topicName);
        }
        consumerGroups.put(groupId, new ConsumerGroup(topicName));
    }

    void joinConsumerGroup(String groupId, Consumer consumer) {
        requireGroup(groupId).addMember(consumer);
    }

    void rebalance(String groupId) {
        ConsumerGroup group = requireGroup(groupId);
        group.rebalance(topics.get(group.getTopic()).getPartitions());
    }

    void publish(String topic, String key, String payload) {
        topics.get(topic).getPartition(key).append(key, payload);
    }

    List<Message> poll(String topic, int partition, long offset, int batchSize) {
        return topics.get(topic).getPartitions().get(partition).read(offset, batchSize);
    }

    long getCommittedOffset(String groupId, int partition) {
        return requireGroup(groupId).getCommittedOffset(partition);
    }

    void commitOffset(String groupId, int partition, long offset) {
        requireGroup(groupId).commitOffset(partition, offset);
    }

    String getTopicForGroup(String groupId) {
        return requireGroup(groupId).getTopic();
    }

    private ConsumerGroup requireGroup(String groupId) {
        ConsumerGroup group = consumerGroups.get(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Unknown consumer group: " + groupId);
        }
        return group;
    }
}

// ========== PRODUCER (client app) ==========

class Producer {
    private final Broker broker;

    Producer(Broker broker) {
        this.broker = broker;
    }

    void send(String topic, String key, String payload) {
        broker.publish(topic, key, payload);
    }
}

// ========== DRIVER ==========

public class Main {
    public static void main(String[] args) {
        Broker broker = new Broker();
        broker.createTopic("orders", 2);

        Producer producer = new Producer(broker);
        producer.send("orders", "user1", "Order Created");
        producer.send("orders", "user2", "Order Paid");
        producer.send("orders", "user1", "Order Delivered");

        String groupId = "payment-workers";
        broker.createConsumerGroup(groupId, "orders");

        Consumer c1 = new Consumer("Worker-1");
        Consumer c2 = new Consumer("Worker-2");
        broker.joinConsumerGroup(groupId, c1);
        broker.joinConsumerGroup(groupId, c2);
        broker.rebalance(groupId);

        c1.poll(broker, groupId, 10);
        c2.poll(broker, groupId, 10);
    }
}
