# HLD — Rate Limiter (Interview Speak Guide)

**Today’s goal:** Same problem as LLD `RateLimiter/`, but **system design** — not classes/SOLID.

| Already done (LLD) | Do here (HLD) |
|--------------------|---------------|
| Strategy: Token Bucket / Sliding Window | Pick one + say why for production |
| In-memory map, thread safety | **Shared Redis** across N app servers |
| `allowRequest(userId) → bool` | **429 + headers**, tiers, fail open/closed |
| Pseudocode classes | Tiny Redis algorithm only |

**Timer:** 45 min · speak answers to Q1–Q8 out loud.

---

## 0. One-line opener (10 sec)

> “I’ll design a distributed rate limiter: enforce N requests per window per user across many API servers, return 429 when over limit, keep checks O(1) and race-safe.”

---

## Q1. Clarify (2–3 min)

Ask / assume:

| Question | Good default for interview |
|----------|----------------------------|
| Per user / IP / API key? | **Per user** if authenticated; IP for anonymous |
| Limit? | **100 req / min** |
| On exceed — reject or queue? | **Reject** (429). Queue = different product (leaky bucket shaping) |
| Soft vs hard limit? | Hard reject |
| Same limit for all users? | Start same; mention **tiers** later (Q7) |

Say out loud:
> “Per authenticated user, 100/min, reject with 429. Anonymous → per IP. Paid tiers later.”

---

## Q2. Estimate (3–5 min)

Example numbers (adjust if interviewer gives different):

```
Users at peak:        10M
Limit:                100 req/min/user
Worst-case checks:    10M × 100 / 60 ≈ 16.7M checks/sec  (upper bound)

Realistic:
  Active users / sec much smaller
  Every API request = 1 rate-limit check
  Target: sub-ms check, Redis INCR ~0.1–1ms
```

Talk track:
> “Every request hits the limiter once. Peak QPS on the limiter ≈ peak API QPS. I’ll size Redis for that QPS and keep the hot path a single atomic INCR.”

---

## Q3. Algorithm + data (5 min) — reuse LLD, don’t re-code

### Pick for interview (simple → scale)

**Start: Fixed window counter in Redis** (easy to explain), then upgrade:

| Algorithm | Pros | Cons | When |
|-----------|------|------|------|
| Fixed window | Simple INCR + TTL | Boundary burst (2× limit near edges) | First draft |
| Sliding window | Accurate | Heavier (ZSET or approx) | Production accuracy |
| Token bucket | Bursts OK, smooth refill | Need tokens + lastRefill in Redis/Lua | APIs that allow bursts |
| Leaky bucket | Smooths traffic | No burst | Traffic shaping |

**Token bucket fields** (same as LLD, now in Redis hash or Lua):

```
key:   rl:tb:{userId}
fields:
  tokens      — current tokens
  lastRefill  — epoch ms
config:
  capacity, refillRatePerSec
```

**Fixed window (doc default — speak this first):**

```
key:   rate_limit:{userId}     // or rate_limit:{userId}:{windowId}
value: count
TTL:   60 seconds
```

---

## Q4. Where does state live? (5 min)

```
Client → LB → API Server × N
                 │
                 ▼
            Redis (shared counters)
```

**Why not local memory on each server?**
> “10 servers each allowing 100/min ⇒ effective 1000/min. Limit must be **global**. Redis = single source of truth for counts.”

**Optional:** Redis Cluster / multiple shards by `userId` hash if one Redis can’t take the QPS.

---

## Q5. Race conditions (5 min)

Problem:
> Two requests read count=99, both allow → 101. Local locks don’t help across servers.

Solution:
> **Atomic Redis ops** — `INCR` is atomic. Or **Lua script** for token bucket (read-modify-write in one Redis execution).

```
on_request(userId):
  key   = "rate_limit:" + userId
  count = REDIS.INCR(key)
  if count == 1:
      REDIS.EXPIRE(key, 60)    // start window
  if count > 100:
      return 429
  return 200
```

Token bucket → Lua: refill + take token in one script (no TOCTOU).

---

## Q6. Client response (3 min)

```
HTTP/1.1 429 Too Many Requests
Retry-After: 60
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 0
X-RateLimit-Reset: <unix>

{ "error": "rate_limit_exceeded", "retry_after": 60 }
```

Talk track:
> “Never silent drop or 500. 429 + Retry-After lets clients back off correctly. Remaining/Reset help SDKs.”

---

## Q7. Tiers (3 min)

```
free:   100 / min   →  rl:free:{userId}   or config service lookup
paid:  10k / min   →  rl:paid:{userId}
```

Flow:
1. Auth → resolve **tier** (user service / JWT claim / config)
2. Load limit config for tier
3. Same Redis path with different `max` / key prefix

> “Limiter stays dumb; tier config is data. No new code path per plan.”

---

## Q8. Redis down — fail open vs closed (5 min)

| Mode | Behavior | Use when |
|------|----------|----------|
| **Fail open** | Allow request if Redis errors | Prefer availability (public site) |
| **Fail closed** | Reject if Redis errors | Prefer safety (payments, login abuse) |

Talk track:
> “I’d **fail open** for a read-heavy product API with monitoring/alerts, and **fail closed** for auth/payment. Also: Redis replica / multi-AZ, circuit breaker, metric on limiter errors.”

---

## Diagram to draw on whiteboard

```
┌────────┐    ┌────┐    ┌─────────────┐
│ Client │───▶│ LB │───▶│ API × N     │
└────────┘    └────┘    └──────┬──────┘
                               │ INCR / Lua
                               ▼
                        ┌─────────────┐
                        │ Redis       │
                        │ counters    │
                        └─────────────┘

Allow → 200 + X-RateLimit-*
Deny  → 429 + Retry-After
```

---

## 2-min algorithm cheat (if they dig in)

**Token vs Fixed window** (from concepts doc):
- Token bucket: refill rate, **allows burst** up to capacity
- Fixed window: reset at boundary, **boundary attack** (100 at T=59 + 100 at T=60)
- Sliding window: rolling accuracy, prefer for strict fairness

**Per-user vs IP vs API key:**
- Auth → user  
- Anon → IP (NAT caveat)  
- Commercial → API key / plan  

---

## Closing checklist (speak before “done”)

- [ ] Clarified key + limit + reject behavior  
- [ ] Rough QPS  
- [ ] Algorithm named + Redis key shape  
- [ ] Why shared Redis  
- [ ] Atomic INCR / Lua  
- [ ] 429 + headers  
- [ ] Tiers  
- [ ] Fail open/closed  

---

## After the interview block

- Don’t rewrite LLD classes today.  
- If weak on Lua/token bucket in Redis: skim `RateLimiter/README.md` § Distributed only.  
- Next overlap guide: Key-Value Store (`RedisKeyValueStore`).
