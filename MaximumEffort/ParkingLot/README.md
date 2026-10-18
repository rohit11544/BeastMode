# LLD — Parking Lot System

Google L4–sized: multi-floor lot, vehicle/spot fit, ticket + Strategy pricing, Observer display.  
Simulation via `park` / `unpark` (say “gate threads take a lot lock” if asked).

---

## 1. Requirement & Scope (00:00 – 00:05)

### Functional Requirements

- **Multi-floor** lot with numbered spots.
- **Vehicles:** MOTORCYCLE, CAR, TRUCK.
- **Spots:** MOTORCYCLE, COMPACT, LARGE (smallest that fits).
- **Enter:** find spot → occupy → issue ticket.
- **Exit:** ticket → fee (pricing Strategy) → free spot.
- **Full lot:** park returns null / “FULL”; entrance board updates.

### Out of scope

- Reservations, valet, EV charging, payments gateway.

### Non-Functional Requirements

- Thread-safe `park` / `unpark` (`ReentrantLock` on the lot).
- Find spot without scanning every occupied stall (free queues per type per floor).
- Swap pricing without changing gate flow (Strategy).

---

## 2. Architecture (00:05 – 00:12)

```
           ParkingLotFacade
                  │
     ┌────────────┼────────────┐
     ▼            ▼            ▼
  Floor[]    PricingStrategy   DisplayBoard (Observer)
     │
     ▼
 ParkingSpot  ←  Vehicle + Ticket
```

### Fit rules (say this)

| Vehicle     | Can use |
|-------------|---------|
| MOTORCYCLE  | MOTORCYCLE → COMPACT → LARGE |
| CAR         | COMPACT → LARGE |
| TRUCK       | LARGE only |

**Nearest:** scan floors in order, then preferred spot types in order. First free spot wins.

### End-to-end flow

```text
ParkingLotFacade(2 floors, hourly $10)
        │
        ▼
park(CAR)
        │
        ▼
findSpot: floor 1 COMPACT free?
        │
       Yes → occupy → Ticket(id, vehicle, spot, inTime)
        │
        ▼
notify DisplayBoard  (occupied/capacity)

═══════════════════════════════════════════════════
  EXIT
═══════════════════════════════════════════════════

unpark(ticketId)
        │
        ▼
fee = pricing.calculate(inTime, now)
        │
        ▼
spot.leave() → free queue
        │
        ▼
notify DisplayBoard
```

---

## 3. Code (one file)

```
ParkingLot/
  README.md
  code/Main.java
```

```bash
cd MaximumEffort/ParkingLot/code
javac Main.java && java Main
```

---

## 4. Complexity

| Operation | Time |
|-----------|------|
| Find + park | O(F · T) T = spot types tried (≤ 3) |
| Unpark | O(1) ticket map |
| Display update | O(observers) |

Free spots live in per-floor `Queue`s — not a full scan of occupied stalls.

---

## 5. Design Patterns

| Pattern | How used |
|---------|----------|
| **Facade** | `ParkingLotFacade` — park / unpark / status |
| **Strategy** | `PricingStrategy` — hourly vs flat |
| **Factory** | `SpotFactory` — create typed spots |
| **Observer** | `DisplayBoard` on occupancy change |
| **Enum** | `VehicleType`, `SpotType` |

---

## 6. SOLID

| | How |
|--|-----|
| **S** | Spot = occupy · Floor = free queues · Lot = assign + tickets · Pricing = fee |
| **O** | New pricing / new spot type without rewriting gates |
| **L** | Any `PricingStrategy` plugs in |
| **I** | Pricing = one `calculate`; Observer = one `onOccupancyChanged` |
| **D** | Facade depends on pricing + observer abstractions |

---

## 7. Interview Q&A

**“Lot is full?”**  
`findSpot` returns null → gate shows FULL. Display already has occupied == capacity.

**“Nearest to entrance?”**  
Floors and spots are ordered from the gate. First match is nearest. Scale: keep a min-heap of free spots by distance.

**“Hourly vs weekend?”**  
New `WeekendPricing implements PricingStrategy` — lot constructor / setter only.

**“Two cars at two gates?”**  
One `ReentrantLock` on park/unpark so two threads cannot take the same spot. Per-floor locks if asked to scale.
