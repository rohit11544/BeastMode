# LLD — Pub-Sub Messaging Queue (Google L4–sized, Kafka Inspired)

Slim interview version: topics, partitions, offsets, consumer groups, rebalance.  
No disk segments / `StorageEngine` / `PollRequest` / `TopicManager` (mention those only if asked to scale up).

---

## 1. Requirement & Scope Clarification (00:00 – 00:05)

### Functional Requirements

- **Topics & Partitions:** Publish to named topics; each topic has one or more partitions for parallel throughput.
- **Partition Strategies:** Pluggable `PartitionStrategy` (ship `HashPartitionStrategy`; Round Robin / Sticky as extensions).
- **Broker (Kafka cluster):** registry of topics + **`Map<groupId, ConsumerGroup>`**; delegates offsets/rebalance to **`ConsumerGroup`** (`createConsumerGroup`, `joinConsumerGroup`, `rebalance(groupId)`).
- **Broker data plane:** `publish()`, `poll()` on topic/partition.
- **Producer / Consumer (client apps):** `Producer.send(...)`; `Consumer.poll(broker, groupId, batchSize)` then `broker.commitOffset(...)`.
- **Pull Polling:** Consumers **pull** `Message` batches via `poll` over their `assignedPartitions` (not push / Observer).

### Non-Functional Requirements

- **In-memory log:** Each `Partition` stores messages in a `List` with an `AtomicLong` next offset.
- **Thread safety:** `ReentrantReadWriteLock` per partition (shared reads, exclusive appends).
- **Broker registry:** Topics in a `ConcurrentHashMap`.
- **Immutability:** `Message` fields are final (`key`, `payload`, `offset`).

---

## 2. Core Architecture & Component Topology (00:05 – 00:12)

```
   Producer (client)              Consumer (client app)
         │ send                          │ poll(groupId)
         ▼                               ▼
    ┌─────────────────────────────────────────────┐
    │                    Broker                    │
    │  topics · partitions · consumer groups       │
    │  offsets (__consumer_offsets) · rebalance()  │
    └─────────────────────────────────────────────┘
         Topic → PartitionStrategy → Partition log
```

### Publish / Consume Flow (Google L4–sized)

**Producer**

```text
Producer
  ↓
Broker.publish()
  ↓
Topic
  ↓
Partition Strategy
  ↓
Partition (append message)
```

**Consumer**

```text
Consumer.poll(broker, groupId)
  ↓
Broker.getCommittedOffset(groupId, partition)
  ↓
Broker.poll(topic, partition, offset)
  ↓
process messages
  ↓
Broker.commitOffset(groupId, partition)
```

### Component details

| Component | Holds / does |
|-----------|----------------|
| **Message** | `[Key, Payload, Offset]` |
| **Partition** | `[List<Message>, nextOffset]` |
| **Topic** | `[List<Partition>, PartitionStrategy]` |
| **Partition Strategy** | `hash(key) % numPartitions` |
| **Broker** | topics + **consumer groups** (topic, members, offsets) + `rebalance(groupId)` |
| **Producer** | client → `Broker.publish(...)` |
| **Consumer** | client → assigned partitions (from rebalance) + `poll(groupId)` |

*Scale-up (only if asked): Partition → StorageEngine → Segments; durable offset store; smarter assignors.*

---

## 3. Code (one file)

```
PubSubMessagingQueue/
  README.md
  code/Main.java
```

```bash
cd MaximumEffort/PubSubMessagingQueue/code
javac Main.java && java Main
```

---

## 4. Interview Follow-ups (keep short)

**“Is this Observer?”**  
No — consumers **pull** with `poll`. Observer would **push**/notify subscribers.

**“Why no ACKS_ALL / replication?”**  
Single in-memory broker. Multi-broker would wait for quorum replication before ack.

**“Why is Partition.read O(N)?”**  
`ArrayList` scan from the start for offset ≥ cursor. Fine for LLD; production uses segment indexes / binary search.

**“Where do consumer groups live?”**  
On the **broker** (like Kafka): group metadata, offsets, and rebalance — not a separate deployable service.

**“How would you make this production-ready?”**  
Persist partition logs (segments), durable offset commits, richer rebalance (sticky/range), optional multi-broker replication.

---

## 5. Design Patterns

### Strategy Pattern (Behavioral)

`PartitionStrategy` encapsulates partition selection. Plug in Hash / Round Robin / Sticky without modifying `Topic`.

### Facade Pattern (Structural)

`Broker` exposes `createTopic()`, `publish()`, `poll()` while hiding topics, partitions, and routing.

### Producer–Consumer Pattern (Behavioral / Concurrency)

Producers append; consumers independently poll through the broker.

**Not Observer** — pull, not push.

### Broker as coordinator (production-shaped)

`Broker.rebalance(groupId)` assigns partitions — models Kafka’s **group coordinator** on the broker.

---

## 6. SOLID Principles

### S — Single Responsibility Principle (SRP)

| Class | Responsibility |
|-------|----------------|
| `Producer` | Only publishes messages |
| `Broker` | Topics, groups, offsets, rebalance, publish/poll routing |
| `Topic` | Only manages partitions |
| `Partition` | Only stores and retrieves messages |
| `Consumer` | Client poll loop over broker-assigned partitions |
| `HashPartitionStrategy` | Only decides which partition to use |

### O — Open/Closed Principle (OCP)

Add `RoundRobinPartitionStrategy` / `StickyPartitionStrategy` without modifying `Topic` / `Broker`.

### L — Liskov Substitution Principle (LSP)

Any `PartitionStrategy` implementation can replace another where the interface is expected.

### I — Interface Segregation Principle (ISP)

`PartitionStrategy` exposes only `getPartition(...)`.

### D — Dependency Inversion Principle (DIP)

`Topic` depends on `PartitionStrategy`, not concrete `HashPartitionStrategy`.
