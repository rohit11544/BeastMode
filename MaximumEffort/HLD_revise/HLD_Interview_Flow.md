# HLD Interview Flow (Practice Template)

Use this order for every problem. Speak out loud, ~40–45 min.

**Do not memorize 12 architectures.** Extract **patterns** (see [12_HLD_Questions.md](./12_HLD_Questions.md)).

```
1. Requirements / Scope
          ↓
2. Scale / Capacity Estimation
          ↓
3. Core APIs
          ↓
4. Core Entities / Data Model
          ↓
5. High-Level Architecture
          ↓
6. Primary Data Store
          ↓
7. Caching
          ↓
8. Async Processing / Kafka
          ↓
9. Scaling
   ├── Partitioning / Sharding
   └── Replication
          ↓
10. Consistency
          ↓
11. Reliability / Failure Handling
          ↓
12. Deep Dive   ← interviewer usually pushes here
          ↓
13. Monitoring / Observability
          ↓
14. Trade-offs
```

---

## How to practice

1. Pick a problem from [12_HLD_Questions.md](./12_HLD_Questions.md).
2. Timer **45 min**.
3. Walk **1 → 14**. Skip a step only if N/A — **say why**.
4. Don’t jump to Kafka/sharding before requirements + numbers.
5. Save **~10–15 min** for **Deep Dive** (step 12) — that’s what separates memorized answers.

**Rule:** Steps 1–11 = clear structure. Step 12 = depth. Step 14 = show judgment.

---

## Step cheat sheet

### 1. Requirements / Scope
- Functional: core user actions
- Non-functional: QPS, latency, availability, consistency needs
- Out of scope: what you **won’t** build today
- Clarify actors, mobile/web, must-haves vs nice-to-haves

### 2. Scale / Capacity Estimation
- DAU / peak QPS, read:write
- Storage over 1–5 years
- Bandwidth if media
- One back-of-envelope; show units

### 3. Core APIs
- 4–8 endpoints
- Method + path + key fields
- Auth / idempotency where it matters

### 4. Core Entities / Data Model
- Main entities + relationships
- PKs / unique keys
- Hot vs cold data
- Stored vs computed

### 5. High-Level Architecture
- Client → LB → services → stores
- **2–5** services only
- Sync vs async paths
- Name the bottleneck

### 6. Primary Data Store
- SQL vs NoSQL for **this** access pattern
- Transactions needed?

### 7. Caching
- What / pattern (cache-aside…) / TTL / invalidation
- Stampede one-liner if hot keys

### 8. Async Processing / Kafka
- What is async; why queue; consumers; at-least-once
- Skip if pure sync — say so

### 9. Scaling
- **Sharding:** key, hotspots, consistent hashing if needed
- **Replication:** leader–follower, read replicas, multi-region only if asked

### 10. Consistency
- Strong vs eventual **per critical flow**
- CAP in one sentence for this product
- Idempotency / dedupe on retries

### 11. Reliability / Failure Handling
- Service / DB / Redis / queue lag
- Timeouts, retries + backoff, circuit breaker
- Fail open vs fail closed
- Graceful degradation

### 12. Deep Dive (critical)
Interviewer pushes on the **hard core** of the problem. Go deep:

| Problem | Typical deep dive |
|---------|-------------------|
| URL Shortener | ID generation, collisions, hot URLs |
| Rate Limiter | Redis atomicity, distributed state |
| Notification | Kafka retry / DLQ / idempotency |
| News Feed | Fan-out write vs read, celebrity |
| Chat | WebSocket, ordering, multi-device |
| Uber | Geohash → nearby drivers → atomic assign |
| Food Delivery | Order state machine, dispatch |
| Ticket Booking | Holds, TTL, double-booking |
| Job Scheduler | Leader election, exactly-once vs at-least-once |
| Stock Alerts | Stream matching subscriptions |
| Search | Index, ranking, geo filters |
| File Storage | Chunking, resumable upload |

Example — Uber after architecture:
> “How exactly do you find nearby drivers?”  
> GPS → Geohash → geo index → nearby cells → candidates → rank → **atomic assignment**

### 13. Monitoring / Observability
- QPS, p99, error rate, queue depth
- Alerts on SLO burn
- Traces on hot path

### 14. Trade-offs
- What you chose vs alternative (1–2 big calls)
- What you’d change at 10× scale
- What you’d cut for MVP

---

## Blank worksheet (copy per problem)

```text
Problem: _______________________

1. Requirements / Scope
   FR:
   NFR:
   Out of scope:

2. Capacity
   QPS / storage:

3. APIs
   -

4. Data model
   Entities / keys:

5. Architecture
   Boxes:

6. Primary store: ________  why:

7. Cache:

8. Async / Kafka:

9. Scaling
   Shard key:
   Replication:

10. Consistency (per flow):

11. Failures:

12. Deep dive topic: ________
    Detail:

13. Monitoring:

14. Trade-offs:
```

---

## Per-problem guides

| Problem | Guide |
|---------|--------|
| Full list + phases | [12_HLD_Questions.md](./12_HLD_Questions.md) |
| Rate Limiter | [RateLimiter.md](./RateLimiter.md) |
