# LLD Interview Flow (Record / Practice Template)

Use this order for every LLD video (~25–40 min speak).  
Mirror of your HLD flow — **thinner**: classes + behavior, not capacity/Kafka.

```
0. PROBLEM UNDERSTANDING
   ├── Restate problem
   ├── Clarifying questions
   └── Assumptions / scope

1. REQUIREMENTS
   ├── Functional
   └── Non-functional

2. CORE ENTITIES + RELATIONSHIPS
   └── (optional quick ER / box diagram)

3. BEHAVIOR / STATE / FLOW
   ├── Happy path
   ├── Key edge cases
   └── State diagram (if State Pattern) OR status transitions (if enum)

4. CLASS DESIGN
   ├── Interfaces / abstract types
   ├── Classes + responsibilities
   └── Public APIs (what client calls)

5. PSEUDOCODE (interview-code the core)
   └── Facade + critical paths only — not every getter

6. DESIGN PATTERNS (name + why)

7. SOLID (map each letter to a class/decision)

8. CONCURRENCY / THREAD SAFETY
   └── What is locked, why, what race you prevent

9. DB SCHEMA (if asked / multi-instance)
   └── Tables + 1–2 key queries / locks (FOR UPDATE)

10. TRADE-OFFS
    └── Enum vs State, in-memory vs DB, etc.

11. FOLLOW-UP QUESTIONS
    └── Extensions interviewer usually asks
```

**Skip unless asked:** capacity estimation, CDN, Kafka topology, full HLD box farm.  
**Always include:** FR/NFR → classes → behavior → patterns/SOLID → concurrency → schema (light).

---

## How to use (video / mock)

1. Timer on — speak in order **0 → 11**.
2. Steps **4–5** = most of the screen time (draw + code).
3. Steps **6–7** = ~60–90 sec talk track (memorize).
4. Step **11** = end with 2–3 follow-ups you *ask yourself* if interviewer is quiet.

---

## Step cheat sheet (what to say)

### 0. Problem understanding
- Restate in 1–2 lines  
- Ask: actors? single machine vs fleet? sync API? payment?  
- Scope out: UI, real hardware drivers, multi-region  

### 1. Requirements
- **FR:** user actions (insert coin, select item…)  
- **NFR:** thread-safety, exact change, no silent partial sale, latency of `allow()`  

### 2. Core entities
- Nouns → classes: `Item`, `ItemSlot`, `Coin`, `VendingMachine`…  
- Relationships: machine **has** slots; slot **has** item + qty  

### 3. Behavior / flow
- Happy path sequence  
- Failures: out of stock, can’t make change, refund  
- Draw state machine **only if** each state owns different behavior (Vending/ATM)  

### 4. Class design
- Facade entry points  
- Who owns stock / money / change / state  
- Thin interfaces (`VendingMachineState`, `Transaction`, `ChangeDispenser`)  

### 5. Pseudocode
- Write like `LLD_revise/*.java` — Lombok OK to skip boilerplate  
- Code the **hard path** (purchase atomicity, CoR, state transitions)  

### 6. Design patterns
For each: **name → where → why not if-else**

| Pattern | Typical LLD use |
|---------|-----------------|
| Facade | Single entry (`insertCoin`, `selectItem`) |
| State | Idle / HasMoney / Dispensing |
| Strategy | Fare, discount, rate-limit algorithm |
| Command | Purchase / Refund as actions |
| CoR | Change / cash dispense denominations |
| Observer | Elevator display, parking board |
| Factory | Create spots / handlers |

### 7. SOLID (60 sec)
- **S** — who owns what  
- **O** — add state / txn / denomination without rewrite  
- **L/I** — swap implementations via thin APIs  
- **D** — depend on abstractions (`ChangeDispenser`, not raw coins)  

### 8. Concurrency
- Synchronize state + credit  
- Per-slot / per-account lock  
- “In DB: `SELECT … FOR UPDATE`”  

### 9. DB schema
- 3–5 tables max  
- PK/FK + status enums  
- One locking sentence  

### 10. Trade-offs
- State Pattern vs enum status  
- Dry-run change vs item-first  
- In-memory demo vs shared DB for fleet  

### 11. Follow-ups (prep 3–5 per problem)
Examples — Vending:
- Can’t make exact change?  
- Motor jam after debit?  
- `MaintenanceState` / restock?  
- Card payment Command?  
- Multi-machine shared inventory?

---

## Blank worksheet (copy per LLD video)

```text
Problem: _______________________

0. Understanding / scope:

1. FR:
   NFR:

2. Entities:

3. Flow / states:

4. Classes + APIs:

5. Pseudocode focus (what I'll write):

6. Patterns:

7. SOLID one-liners:

8. Concurrency:

9. Schema:

10. Trade-offs:

11. Follow-ups:
    -
    -
    -
```

---

## HLD vs LLD (don’t mix on camera)

| | HLD | LLD |
|--|-----|-----|
| Goal | Scale, services, data flow | Classes, behavior, correctness |
| “Deep dive” | Cache, Kafka, shard, CAP | Patterns, locks, state, schema |
| Code | Tiny Redis/API sketch | Real class pseudocode |
| Time | ~40–45 min | ~25–40 min |

---

## Your locked plan (reminder)

- **Alt days:** LLD video ↔ HLD video (~20 days)  
- **Every day:** DSA practice + revise question list  
- LLD guides: `LLD_revise/` (`ATM.java`, `VendingMachine.java`, …)  
- HLD guides: `HLD_revise/` + `HLD_Interview_Flow.md`
