# Handbook (crisp triggers — imagine the rest)

Daily revision anchor. Expand in brain from `hldfinal/Payment System — HLD.md` etc.

---

## LLD — any question (4 steps)

1. **Model** — inputs/outputs, main flow; **classes + enums**; who calls whom to produce the result (draw or list before code).
2. **Creational** — how objects are born: plain `new`, **factory**, **singleton** (only if truly one), DI/wiring — no accidental global state.
3. **Structural** — how pieces compose: **Facade** entry, **Adapter** for external APIs, **Composite** if tree; Decorator/Proxy only if prompt needs wrap/proxy.
4. **Behavioral + SOLID + run** — **Strategy / State / Observer / Command** for changing behavior; name Facade; implement thin classes; **compile + one happy-path demo** in `main`.

**Rule:** New problem = same 4 steps; patterns are labels after the model, not step 1.

---

## HLD — any question (4 steps)

1. **Pattern bucket** — which shape? *(collab doc / game → **WebSocket** + ordering/OT|CRDT; feed/social → read cache + fan-out; e-commerce/order → **sync API + async events**; payments → **orchestrator + PSP + idempotency**; market tick → streams + low latency; upload/video → object store + CDN)* — not one pattern for all.
2. **Requirements** — functional (core path only); NFR: QPS, p99, **consistency vs availability**, durability, geo; state **assumptions** aloud.
3. **High-level design** — user → DNS/CDN/LB → services (K8s: Ingress → Service → pods); **APIs + data model**; sync path vs **Kafka/queue**; SQL vs NoSQL vs cache vs object store **per entity**; draw one diagram.
4. **Deep dive (they will drill here)** — concurrency (**locks, idempotency, sharding key**); failures (**Redis/DB/PSP down** → fail open vs closed); sagas (**paid but order lag → retry event, don’t double-charge**); hot keys; monitoring. Answer **what breaks + what user sees + how you recover**.

**Rule:** Steps 1–3 in ~15 min; step 4 is the interview — prep one failure story per store you put on the diagram.

---

## LLD 21 + Redis — one line each (strike first · then also)

1. **File system** — **Composite** · Facade · locks  
2. **Transactional KV** — **Txn + locks** · Facade · ThreadLocal  
3. **Rate limiter** — **Strategy** (token / sliding window) · per-key locks  
4. **Delayed scheduler** — **Queue + worker** · Command · Condition  
5. **Pub-sub** — **Queue + worker** · Facade · Strategy (partition)  
6. **Parking lot** — **Facade** · Strategy (pricing) · Observer (board) · fit-order  
7. **Elevator** — **Strategy** (LOOK / dispatch) · heaps  
8. **ATM / vending** — **State** · CoR (denom chain) · Facade  
9. **Ride sharing** — **Strategy** (match + pricing) · geo index · tryLock  
10. **Reservation** — **State** · TTL locks · intervals  
11. **Splitwise** — **Strategy** (equal / % / exact) · simplify debts  
12. **Logger** — **Queue + worker** · CoR · Facade  
13. **Notification** — **Queue + worker** · Strategy (channel) · Facade · Adapter (provider)  
14. **Jira** — **State / workflow** · Strategy (guards) · Observer  
15. **Payment gateway** — **Adapter** · idempotency · Facade  
16. **LRU / LFU cache** — **Strategy** (eviction) · HashMap + structure  
17. **RBAC** — **Permission model** (roles → ACL) · Facade  
18. **Meeting scheduler** — **Intervals + lock order** · Strategy (room) · Facade  
19. **Shopping cart** — **State** · per-SKU locks · TTL · Strategy (discount)  
20. **Social chat** — **Observer** (sessions) · offline buffer  
21. **Ledger / wallet** — **Double-entry** · Facade · locks · compensating reverse  
22. **Redis KV** *(warm-up)* — **TTL eviction** · DelayQueue worker · Facade  

**Strike-first buckets:** Queue+worker → 4,5,12,13 · State → 8,10,14,19 · Strategy → 3,6,7,9,11,16 · Adapter → 15 · Composite → 1 · Observer → 6,14,20 · CoR → 8,12 · Txn/locks/intervals → 2,10,18,21  

---

## HLD — prep order (14)

*Docs: `hldfinal/`. Study **#1–12** first, then **#13–14**. Structure template: `Payment System — HLD.md` (requirements → API flows → architecture → data model → deep dives).*

| # | Group | Topic | Strike-first |
|---|--------|--------|----------------|
| 1 | — | **Payment** *(done)* | **orch+PSP** · idem · ledger |
| 2 | Primitive | **Notification** | **Kafka+workers** · retry/DLQ · idempotent send |
| 3 | Primitive | **Search** | **ES index** · event indexer · hydrate |
| 4 | Primitive | **Chat** | **WebSocket** · inbox partition · offline |
| 5 | Vertical | **Amazon** | **checkout sync** · inventory · Kafka after PAID |
| 6 | Vertical | **Swiggy** | **SQL→Kafka** · order SM · dispatch |
| 7 | Vertical | **Ticket booking** | **hold+TTL** · no oversell · pay saga |
| 8 | Vertical | **Uber** | **geo index** · location stream · atomic assign |
| 9 | Specialized | **News feed** | **fan-out** write/read hybrid |
| 10 | Specialized | **Broker (Groww)** | **ticks→match** · orders/GTT · wallet · T+1 |
| 11 | Specialized | **Google Docs** | **WS+oplog** · OT\|CRDT |
| 12 | Specialized | **YouTube** | **pre-signed S3** · CDN · transcode async |
| 13 | Fundamental | **URL shortener** | **cache-aside** · hash shard |
| 14 | Fundamental | **Rate limiter** | **Redis atomic** · fail-open/closed |

**Why this order:** Payment is the shared money slice for verticals. **Notification, Search, Chat** are building blocks reused in Amazon, Swiggy, Ticket, and Uber so you learn them once. **Verticals** compose those primitives plus payment. **Specialized** topics are common drill-down interviews. **URL + rate limiter** are small fundamental HLDs.
