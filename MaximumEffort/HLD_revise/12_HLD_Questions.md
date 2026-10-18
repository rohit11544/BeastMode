# The 12 HLD Questions (Master List)

**Goal:** Master these **12** — not 50/100/200. Extract **patterns**, then practice **variations**.

Don’t memorize architectures. After these 12 you cover most HLD building blocks interviewers reuse.

---

## Pattern map (what each problem teaches)

| # | HLD Problem | Main concept you learn |
|---|-------------|------------------------|
| 1 | URL Shortener | Basic scalable architecture |
| 2 | Rate Limiter | Distributed state / atomicity |
| 3 | Notification | Kafka / async / retry |
| 4 | News Feed | Fan-out |
| 5 | Chat | WebSockets / ordering |
| 6 | Uber | Geospatial / real-time |
| 7 | Food Delivery | Event-driven architecture |
| 8 | Ticket Booking | Concurrency / consistency |
| 9 | Job Scheduler | Distributed workers |
| 10 | Stock Alerts | Streaming |
| 11 | Search | Search indexing |
| 12 | File Storage | Object storage / large data |

**Practice order:** use [HLD_Interview_Flow.md](./HLD_Interview_Flow.md) (incl. **Deep Dive** + **Trade-offs**).

---

## Phase plan

### Phase 1 — Master first
1. URL Shortener  
2. Rate Limiter  
3. Notification  
4. News Feed / Instagram Feed  

### Phase 2 — Master next
5. Chat / WhatsApp  
6. Uber / Ride Matching  
7. Food Delivery *(Swiggy leverage)*  
8. Ticket Booking  

### Phase 3 — Master last
9. Distributed Job Scheduler  
10. Stock Price Alert / Price Tracker  
11. Search / Restaurant Search  
12. Distributed File Storage (Drive / Dropbox)  

Then: **random variations** (not new memorized systems).

---

## Tier 1 — MUST KNOW

### 1. Design URL Shortener
**Foundation problem.** Flow emphasis:

```
Requirements → Capacity → APIs → Data model → Architecture → DB → Cache
→ ID generation → Replication → Consistency → Failure handling → Monitoring
```

**Be able to explain:**
- Short-code generation  
- Collision avoidance  
- Read-heavy workload  
- Redis caching  
- DB choice + read replicas  
- Hot URLs  
- Expiration  
- Analytics  

**Why:** teaches fundamentals.

**Deep dive:** ID generation strategies (counter+base62 vs hash), collisions, hot key.

---

### 2. Design Rate Limiter
**Extremely important.** Algorithms + distributed state.

**Learn:**
- Fixed window · Sliding window · Token bucket · Leaky bucket  
- Redis · Atomic ops · Distributed rate limiting  
- API Gateway integration  
- Fail-open vs fail-closed  

**Architecture:**
```
Client → API Gateway → Rate Limiter → Redis → Backend Service
```

**Why:** distributed state + consistency + atomicity.

**Guide:** [RateLimiter.md](./RateLimiter.md)

**Deep dive:** `INCR`/Lua races, multi-server global limit, Redis down.

---

### 3. Design Notification System
**Very high ROI.**

```
Event → Kafka → Notification Service
                    ├── Push
                    ├── SMS
                    └── Email
```

**Learn:**
- Kafka · Async · Retry · DLQ · Idempotency  
- Priority · Rate limiting · Template service  
- User preferences · Delivery status · At-least-once  

**Why:** reusable async building blocks for many designs.

**Deep dive:** retry/DLQ, idempotent send, preference fan-out.

---

### 4. Design News Feed / Instagram Feed
**Especially valuable for Meta.**

**Key problem:** Fan-out on **write** vs fan-out on **read**.

```
                 Post
                  |
          +-------+-------+
          |               |
    Fan-out Write    Fan-out Read
          |               |
       Followers          Feed
```

**Learn:**
- Feed generation · Celebrity problem · Push vs pull · Hybrid  
- Caching · Ranking · Pagination · Cursor-based pagination  
- Sharding · Replication  

**Why:** Meta E4 product-facing / feed-style prep.

**Deep dive:** celebrity fan-out, hybrid push/pull, cursor pagination.

---

## Tier 2 — VERY IMPORTANT

### 5. Design Chat / WhatsApp
**Best for real-time distributed systems.**

**Learn:**
- WebSockets · Connection management  
- Message ordering · Message IDs · Idempotency  
- Offline users · Delivery / read receipts · Presence  
- Fan-out · Partitioning · Multi-device sync  

**Deep dive:** per-user inbox partitions, ordering guarantees, multi-device.

---

### 6. Design Uber / Ride Matching
**Critical for target list (Uber L4).** Domain-specific, not generic textbook only.

**Ride flow:**
```
Rider → Ride Request → Matching → Nearby Drivers → Offer → Accept → Trip
```

**Location flow:**
```
Driver → GPS updates → Kafka → Location Service → Geo Index
```

**Learn:**
- Geohashing · Geospatial index · Redis GEO  
- Location ingestion · Kafka · Matching  
- Double assignment · Distributed locking · Driver timeout  
- Stale locations · Surge · Eventual consistency  

**Uber L4 emphasis:** geospatial indexing, real-time location streams, matching + races.

**Deep dive:** geohash → nearby cells → rank → **atomic driver assignment**.

**Variations to practice (don’t only memorize “Design Uber”):**
- Driver location tracking only  
- Driver matching service only  
- Surge pricing  
- Food delivery tracking  

---

### 7. Design Food Delivery
**High relevance — Swiggy background.** Know this extremely well; use real tradeoffs from experience.

```
Customer → Order → Restaurant → Driver Assignment → Delivery
```

**Services to connect:**
- Order · Restaurant · Payment · Dispatch · Location · Notification  

**Discuss:**
- Order state machine · Driver assignment · Restaurant acceptance  
- Payment · Cancellation · ETA · Location tracking  
- Kafka · Idempotency · Eventual consistency  

**Deep dive:** order states + dispatch races + cancel/payment edge cases.

---

### 8. Design Ticket Booking
(BookMyShow / Ticketmaster / flights / hotels)

**Core problem:** prevent **double booking**.

**Learn:**
- Inventory · Reservation · Temporary holds · TTL  
- Distributed locking · Optimistic locking · Transactions  
- Payment · Idempotency · Expiration · Race conditions  

**Why:** interviewers can drill forever on concurrency.

**Deep dive:** hold + TTL + payment callback + two users same seat.

---

### 9. Design Distributed Job Scheduler

```
User → Job API → Job Store → Scheduler → Kafka → Workers
```

**Learn:**
- Delayed jobs · Scheduling · Worker pools  
- Retries · DLQ · Idempotency · Leader election  
- Partitioning · Failure recovery  
- Exactly-once vs at-least-once  

**Deep dive:** who schedules (leader), missed jobs, duplicate workers.

---

## Tier 3 — HIGH-VALUE SPECIALIZED

### 10. Design Stock Price Alert / Price Tracker
Variations show up in real interviews (incl. Uber L4 reports).

```
Stock events → Kafka → Price processor → User subscriptions
 → Matching engine → Notification
```

**Learn:**
- Streaming · Sliding windows · Pub/Sub  
- Subscription indexing · Efficient matching  
- Kafka partitioning · Notification fan-out  

**Deep dive:** match many users to one tick without O(users) scan.

---

### 11. Design Search / Restaurant Search
Useful for Uber, Swiggy/Zomato-style, Meta, Amazon.

```
Query → Search Service → Search Index → Ranking → Results
```

**Learn:**
- Elasticsearch/OpenSearch · Inverted index · Autocomplete  
- Ranking · Geo filtering · Filters · Pagination · Caching  
- Index updates · Eventual consistency  

**Deep dive:** index freshness vs query latency, geo + text filters.

---

### 12. Design Distributed File Storage
(Google Drive / Dropbox) — different workload from feeds/real-time.

**Learn:**
- Chunking · Metadata service · Object storage  
- Upload/download · Resumable uploads  
- Replication · Deduplication · CDN  
- Consistency · Large-file handling  

**Deep dive:** chunk upload, metadata vs blob store, conflict on sync.

---

## After the 12 — practice variations

| Instead of memorizing | Ask yourself |
|-----------------------|--------------|
| Design Uber | Design driver location tracking / matching / surge |
| Design Notification | Design only email pipeline with retries |
| Design Feed | Design celebrity-only fan-out |
| Design Food Delivery | Design live order tracking only |

Especially for **Uber**: questions can be **domain slices**, not full “Design Uber.”

---

## Tracking checklist

| # | Problem | Phase | Done speak? | Deep dive solid? |
|---|---------|-------|-------------|------------------|
| 1 | URL Shortener | 1 | ☐ | ☐ |
| 2 | Rate Limiter | 1 | ☐ | ☐ |
| 3 | Notification | 1 | ☐ | ☐ |
| 4 | News Feed | 1 | ☐ | ☐ |
| 5 | Chat | 2 | ☐ | ☐ |
| 6 | Uber | 2 | ☐ | ☐ |
| 7 | Food Delivery | 2 | ☐ | ☐ |
| 8 | Ticket Booking | 2 | ☐ | ☐ |
| 9 | Job Scheduler | 3 | ☐ | ☐ |
| 10 | Stock Alerts | 3 | ☐ | ☐ |
| 11 | Search | 3 | ☐ | ☐ |
| 12 | File Storage | 3 | ☐ | ☐ |

---

## Related files

- [HLD_Interview_Flow.md](./HLD_Interview_Flow.md) — speak order (1–14)  
- [RateLimiter.md](./RateLimiter.md) — problem #2 guide  
- `../HLDSystemDesignConcepts.md` — concepts + Phase 7 walkthroughs  
