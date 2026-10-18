# HLD_revise

Prep for HLD interviews: **16 problems** in `../hldfinal/`, one speak framework, pattern extraction — not 100 memorized designs.

## Start here

| File | Purpose |
|------|---------|
| [../HANDBOOK.md](../HANDBOOK.md) | **HLD prep order** table (study sequence + strike-first) |
| [12_HLD_Questions.md](./12_HLD_Questions.md) | Canonical 12 + patterns, checklist |
| [HLD_Interview_Flow.md](./HLD_Interview_Flow.md) | Speak order (use Payment `hldfinal` doc as structure template) |
| [RateLimiter.md](./RateLimiter.md) | Extra speak guide for rate limiter (#14 in prep order) |

## Prep order

See full table in [../HANDBOOK.md](../HANDBOOK.md) § **HLD — prep order (16)**. Doc names: `../hldfinal/<Topic> — HLD.md`.

## How to practice one problem (~45 min)

1. Open flow + problem section in `12_HLD_Questions.md` (or dedicated `.md` if exists)  
2. Speak steps **1→14** out loud  
3. Spend real time on **Deep Dive** (step 12)  
4. End with **Trade-offs** (step 14)  

## Overlaps with LLD (do HLD speak after LLD code)

| HLD | LLD folder |
|-----|------------|
| Rate Limiter | `RateLimiter/` |
| Notification | `NotificationSystem/` |
| Chat | `SocialChat/` |
| Uber | `RideSharing/` |
| KV / cache (related) | `RedisKeyValueStore/`, `InMemoryCache/` |
| Message queue | `PubSubMessagingQueue/` |

## Rule

Master **patterns** from the 12 (fan-out, geo, Kafka/retry, booking locks, …).  
Do **not** grind 50–200 full HLD writeups.
