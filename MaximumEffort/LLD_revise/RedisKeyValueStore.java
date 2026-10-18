/**
 * Redis-Like KV Store — Interview PSEUDOCODE (revise this)
 *
 * How you'll use it in interview:
 *   1. FR / NFR (speak)
 *   2. Entities + dual TTL eviction (speak / draw)
 *   3. Write THIS style of code
 *   4. Patterns + SOLID + concurrency (speak)
 *
 * Patterns: Facade · Dual eviction (lazy + DelayQueue) — NOT Strategy
 * Lombok: @Getter optional — skip accessors in interview
 * Compile: NOT meant to compile — interview sketch only.
 */

// ============================================================
// INTERFACE + MODELS
// ============================================================

interface KeyValueStore<K, V> {
    V get(K key);
    void put(K key, V value);
    void put(K key, V value, long ttlMillis);
    boolean delete(K key);
}

class ValueNode<V> {
    private final V value;
    private final Long expiryTimestamp; // absolute ms; null = forever

    ValueNode(V value, Long ttlMillis) {
        this.value = value;
        this.expiryTimestamp = (ttlMillis != null && ttlMillis > 0)
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

    ExpiryEntry(K key, long ttlMillis) {
        this.key = key;
        this.expiryTimeMs = System.currentTimeMillis() + ttlMillis;
    }

    K getKey() { return key; }

    long getDelay(TimeUnit unit) {
        return unit.convert(expiryTimeMs - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    int compareTo(Delayed other) {
        return Long.compare(this.expiryTimeMs, ((ExpiryEntry<?>) other).expiryTimeMs);
    }
}

// ============================================================
// STORE — Facade
// ============================================================

class RedisKeyValueStore<K, V> implements KeyValueStore<K, V> {
    private final Map<K, ValueNode<V>> store = new HashMap<>();
    private final DelayQueue<ExpiryEntry<K>> delayQueue = new DelayQueue<>();
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    // cleaner thread: delayQueue.take() then purge if still expired

    RedisKeyValueStore() {
        // start daemon: processActiveEviction()
    }

    V get(K key) {
        // 1) readLock — fast path
        //    node missing → null
        //    !expired → return value
        // 2) if expired: writeLock — lazy remove, return null
        return null; // sketch
    }

    void put(K key, V value) {
        put(key, value, -1);
    }

    void put(K key, V value, long ttlMillis) {
        // writeLock:
        //   store.put(key, new ValueNode(value, ttl > 0 ? ttl : null))
        //   if ttl > 0 → delayQueue.put(new ExpiryEntry(key, ttl))
    }

    boolean delete(K key) {
        // writeLock → store.remove(key)
        return false;
    }

    void processActiveEviction() {
        // while running:
        //   entry = delayQueue.take()          // blocks — 0% CPU idle
        //   writeLock:
        //     node = store.get(entry.key)
        //     if node != null && still expired → store.remove
        //     (overwrite with new TTL must NOT delete)
    }

    void shutdown() { /* stop cleaner */ }
}

// ============================================================
// MAIN
// ============================================================

class Main {
    void demo() {
        RedisKeyValueStore<String, String> redis = new RedisKeyValueStore<>();
        redis.put("user:100", "Alice");
        redis.get("user:100");
        redis.put("session:1", "data", 1000); // TTL
        // sleep → get returns null (lazy and/or active eviction)
        redis.shutdown();
    }
}

// ============================================================
// INTERVIEW SPEAK — Requirements · Patterns · SOLID
// ============================================================
//
// REQUIREMENTS
// FR: get / put / put(ttl) / delete; expired keys never returned
// NFR: thread-safe; DelayQueue cleaner blocks (no busy scan); O(1) CRUD
//
// DESIGN PATTERNS
// Facade        — client only get/put/delete
// Dual eviction — passive on get + active DelayQueue daemon (both always on)
//
// SOLID
// S — ValueNode=data · ExpiryEntry=schedule · Store=CRUD+locks+cleaner
// O — new KeyValueStore impl without changing clients
// L — any KeyValueStore substitutable for CRUD
// I — interface = CRUD only (no begin/commit here)
// D — clients depend on KeyValueStore abstraction
//
// CONCURRENCY
// ReentrantReadWriteLock — shared get, exclusive put/delete/evict
// put+TTL = O(log N) heap; get/delete O(1)
//
// FOLLOW-UPS
// Overwrite TTL → cleaner checks isExpired on current node
// Transactions → see TransactionalKVStore.java
// Production → WAL / maxmemory LRU (separate LLD)
//
// ============================================================
// SKELETON — blank for interview coding
// ============================================================
/*
interface KeyValueStore<K, V> { }

class ValueNode<V> { }
class ExpiryEntry<K> implements Delayed { }

class RedisKeyValueStore<K, V> implements KeyValueStore<K, V> { }

class Main { }
*/
