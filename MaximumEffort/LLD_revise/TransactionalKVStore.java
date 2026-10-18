/**
 * Transactional KV Store (+ TTL) — Interview PSEUDOCODE (revise this)
 *
 * How you'll use it in interview:
 *   1. FR / NFR (speak)
 *   2. Global store vs ThreadLocal txn stack (speak / draw)
 *   3. Write THIS style of code
 *   4. Patterns + SOLID + concurrency (speak)
 *
 * Patterns: Facade · Decorator-like wrap · ThreadLocal stack · Tombstone
 * Builds on same TTL ideas as RedisKeyValueStore
 * Compile: NOT meant to compile — interview sketch only.
 */

// ============================================================
// INTERFACE + MODELS (same TTL building blocks)
// ============================================================

interface KeyValueStore<K, V> {
    V get(K key);
    void put(K key, V value, Long ttlMillis);
    void remove(K key);
}

class ValueNode<V> {
    private final V value;
    private final Long expiryTimestamp;

    ValueNode(V value, Long ttlMillis) {
        this.value = value;
        this.expiryTimestamp = (ttlMillis != null)
                ? System.currentTimeMillis() + ttlMillis
                : null;
    }

    V getValue() { return value; }
    Long getExpiryTimestamp() { return expiryTimestamp; }
    boolean isExpired() {
        return expiryTimestamp != null && System.currentTimeMillis() >= expiryTimestamp;
    }
}

class ExpiryEntry<K> implements Delayed {
    private final K key;
    private final long expiryTimeMs;

    ExpiryEntry(K key, long ttlMillis) { /* set expiryTimeMs */ this.key = key; this.expiryTimeMs = 0; }
    K getKey() { return key; }
    long getDelay(TimeUnit unit) { return 0; }
    int compareTo(Delayed other) { return 0; }
}

// ============================================================
// GLOBAL ENGINE — map + RWLock + DelayQueue TTL
// ============================================================

class InMemoryKeyValueStore<K, V> implements KeyValueStore<K, V> {
    private final Map<K, ValueNode<V>> store = new HashMap<>();
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final DelayQueue<ExpiryEntry<K>> delayQueue = new DelayQueue<>();

    InMemoryKeyValueStore() {
        // start cleaner: take() → expireIfStillDue(key)
    }

    V get(K key) {
        // readLock → null / expired → null else value
        // (lazy expiry check)
        return null;
    }

    void put(K key, V value, Long ttlMillis) {
        // writeLock → store.put ValueNode; if ttl → delayQueue.put
    }

    void remove(K key) {
        // writeLock → store.remove
    }

    ReentrantReadWriteLock getLock() { return rwLock; }
    void shutdown() { }
}

// ============================================================
// TRANSACTION MANAGER — Facade over stack + global
// ============================================================

class TransactionManager<K, V> implements KeyValueStore<K, V> {
    private static final Object TOMBSTONE = new Object();

    private final InMemoryKeyValueStore<K, V> globalEngine;
    private final ThreadLocal<Deque<Map<K, Object>>> txnStack =
            ThreadLocal.withInitial(ArrayDeque::new);

    TransactionManager(InMemoryKeyValueStore<K, V> globalEngine) {
        this.globalEngine = globalEngine;
    }

    boolean isInTransaction() {
        return !txnStack.get().isEmpty();
    }

    void begin() {
        txnStack.get().push(new HashMap<>());
    }

    V get(K key) {
        if (!isInTransaction()) return globalEngine.get(key);

        // top → bottom layers
        for (Map<K, Object> layer : txnStack.get()) {
            if (!layer.containsKey(key)) continue;
            Object val = layer.get(key);
            if (val == TOMBSTONE) return null;
            ValueNode<V> node = (ValueNode<V>) val;
            return node.isExpired() ? null : node.getValue();
        }
        return globalEngine.get(key);
    }

    void put(K key, V value, Long ttlMillis) {
        if (!isInTransaction()) {
            globalEngine.put(key, value, ttlMillis);
            return;
        }
        txnStack.get().peek().put(key, new ValueNode<>(value, ttlMillis));
    }

    void remove(K key) {
        if (!isInTransaction()) {
            globalEngine.remove(key);
            return;
        }
        txnStack.get().peek().put(key, TOMBSTONE); // mask lower layers
    }

    void commit() {
        // pop current
        // if nested → parent.putAll(current)
        // if root → ONE writeLock:
        //   for each entry:
        //     TOMBSTONE → global.remove
        //     else → global.put (+ DelayQueue if TTL)
        // atomic multi-key flush
    }

    void rollback() {
        // pop and discard top layer
    }
}

// ============================================================
// MAIN
// ============================================================

class Main {
    void demo() {
        InMemoryKeyValueStore<String, String> engine = new InMemoryKeyValueStore<>();
        TransactionManager<String, String> store = new TransactionManager<>(engine);

        store.put("key1", "Initial", null);

        store.begin();
        store.put("key1", "Txn1", null);
        store.begin();
        store.remove("key1");          // tombstone in inner
        store.rollback();              // undo remove
        store.commit();                // root → global

        engine.shutdown();
    }
}

// ============================================================
// INTERVIEW SPEAK — Requirements · Patterns · SOLID
// ============================================================
//
// REQUIREMENTS
// FR: CRUD + TTL; begin / commit / rollback; nested txns; tombstone delete
// NFR: root commit atomic (one write lock); per-thread isolation; DelayQueue TTL
//
// DESIGN PATTERNS
// Facade           — TransactionManager API
// Decorator-like   — wraps InMemoryKeyValueStore, adds txn semantics
// Thread-per-context — ThreadLocal<Deque<Map>> stack
// Tombstone        — remove in txn without touching global yet
//
// SOLID
// S — Store=map+TTL · TxnManager=nesting · Node/Entry=data only
// O — new KeyValueStore backend; txn can wrap it
// L — both implement CRUD KeyValueStore
// I — begin/commit/rollback ONLY on TransactionManager (not on plain store)
// D — txn depends on store abstraction
//
// FLOW
// begin → push map
// put/remove → top layer only
// get → top→bottom then global
// nested commit → merge into parent
// root commit → writeLock flush all keys atomically
// rollback → pop discard
//
// FOLLOW-UPS
// Why not CHM alone? multi-key atomic commit needs one exclusive flush
// Scale: MVCC (versions + snapshot) instead of global write lock
// Prod one-liner: WAL for durability; Raft if multi-node (names only at L4)
// vs Redis KV: that = immediate writes; this = isolation + multi-key atomic
//
// ============================================================
// SKELETON — blank for interview coding
// ============================================================
/*
interface KeyValueStore<K, V> { }

class ValueNode<V> { }
class ExpiryEntry<K> implements Delayed { }

class InMemoryKeyValueStore<K, V> implements KeyValueStore<K, V> { }

class TransactionManager<K, V> implements KeyValueStore<K, V> { }

class Main { }
*/
