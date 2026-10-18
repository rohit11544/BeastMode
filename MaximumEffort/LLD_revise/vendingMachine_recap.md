# Vending Machine — LLD Recap

## 0. Problem Understanding
Design a vending machine that accepts coins, lets a user select a slot (A1, B2), dispenses the item with correct change, and supports refund before purchase.
Clarify: coin denominations, exact-change-only vs make change, card payment, multi-machine fleet, restock/maintenance mode, analytics.
Assumptions: single machine for core design; coins are QUARTER / ONE / FIVE; mappings of slot→item are configured; change must be exact or cancel; no UI/hardware drivers in scope.

## 1. Requirements
Functional: insert coin and accumulate credit; select slot; dispense item + change; refund credit; view/restock inventory (maintenance).
Non-functional: thread-safe state/credit/stock/change inventory; no silent partial sale; clear user messages; durable inventory if fleet/DB; observability of failed purchases and change jams.

## 2. Core Entities + Relationships
```
VendingMachine 1──* ItemSlot ──1 Item
VendingMachine 1──1 ChangeDispenser chain ($5→$1→$0.25)
VendingMachine 1──1 current VendingMachineState
VendingMachine holds insertedCredit

Coin · DisplayScreen · ItemMotor · CoinReturnTray   (hardware stubs)
Transaction: PurchaseTransaction | RefundTransaction
```
ItemSlot owns quantity. Change chain owns denomination counts. State owns what insert/select/refund mean right now.

## 3. Behavior / State / Flow
Lifecycle: `Idle → HasMoney → Dispensing → Idle` · `HasMoney → refund → Idle`

Happy path: insert coins → select slot → validate → commit payment → dispense change → release item → Idle.

Edge cases: insert with no money selected → Idle rejects select; insufficient credit; out of stock; cannot make exact change; refund while HasMoney; concurrent last-item race.

```
                    insertCoin
     Idle ──────────────────────► HasMoney
      ▲          selectItem            │
      │     ┌──────────────────────────┘
      │     ▼
      │  Dispensing ──purchase ok / fail+refund──► Idle
      │
      └──────── refund (from HasMoney)
```

Why State Pattern (not enum): each state owns different behavior for the same API (`insertCoin` / `selectItem` / `refund`).

## 4. Class Design
Public facade APIs: `insertCoin(Coin)` · `selectItem(slotCode)` · `requestRefund()`  
Internal: `dispenseItem(slotCode)` called from HasMoney after transition to Dispensing.

| Type | Responsibility |
|------|----------------|
| `VendingMachine` | Facade/context: state, credit, inventory, peripherals |
| `VendingMachineState` | Idle / HasMoney / Dispensing lifecycle |
| `Item` / `ItemSlot` | Catalog + synchronized quantity |
| `ChangeDispenser` | CoR: `canMakeChange` (dry-run) + `dispenseChange` (commit) |
| `Transaction` | Command: Purchase / Refund |
| Hardware stubs | Motor, screen, coin tray only |

## 5. Pseudocode (core — purchase atomicity)
```
PurchaseTransaction.execute(machine):
  1) VALIDATE
     stock > 0, credit >= price
     if changeDue > 0 and !canMakeChange(changeDue) → fail (refund later; stock unchanged)

  2) COMMIT PAYMENT
     resetCredit()                    // money accepted for this sale

  3) DISPENSE CHANGE FIRST
     if changeDue > 0 and !dispenseChange(changeDue) → restore credit, fail
     // item still behind flap / not released

  4) RELEASE ITEM LAST
     slot.dispense() + itemMotor.release(slotCode)
     if stock race fails → restore credit / alert; never software-"restock" a taken product

  5) any fail before motor → DispensingState runs RefundTransaction; stock unchanged
```
`selectItem` in HasMoney: `setState(Dispensing)` then `dispenseItem(slot)` — Main does not call dispense directly.

Change CoR: `$5 → $1 → $0.25`; dry-run then commit; fail if remainder and no next.

## 6. Design Patterns
| Pattern | Where | Why |
|---------|-------|-----|
| **Facade** | `VendingMachine` | Client only sees insert/select/refund |
| **State** | Idle / HasMoney / Dispensing | Avoid if-else on status; each state owns behavior |
| **Command** | Purchase / Refund | One action each; add CardPayment later without rewriting states |
| **Chain of Responsibility** | Change dispensers | Each denomination takes what it can, passes remainder |

## 7. SOLID
- **S:** Machine orchestrates; slot = stock; command = one action; peripherals = hardware only.
- **O:** Add `MaintenanceState`, new coin denomination, or CardPayment command without core rewrite.
- **L/I:** Any `VendingMachineState` / `Transaction` / `ChangeDispenser` plugs in; state API stays thin.
- **D:** Purchase depends on `ChangeDispenser` + slot abstractions, not raw coin arrays.

## 8. Concurrency / Thread Safety
Synchronize `setState` / `addCredit` / `resetCredit` so credit and lifecycle do not tear.
Synchronize `ItemSlot.dispense()` so two buyers cannot both take the last Coke.
Change inventory: synchronize CoR or lock denomination rows so dry-run + commit stay consistent.
Multi-machine / DB: `SELECT quantity FROM item_slots WHERE slot_code=? FOR UPDATE` before decrement; same idea for `change_inventory`.

## 9. DB Schema
```sql
item_slots(slot_code PK, item_name, price, quantity, updated_at)

change_inventory(vending_machine_id, coin_denomination, coin_count,
                 PRIMARY KEY (vending_machine_id, coin_denomination))

vending_transactions(transaction_id PK, slot_code, amount_paid,
                     change_dispensed, status, created_at)
-- status: SUCCESS | REFUNDED | FAILED
```
In-memory demo for interview code; schema when they ask fleet / restock service / audit.

## 10. Trade-Offs
State Pattern vs enum: use State when behavior differs per status (here); enum is enough when status is only a label (cart/ride).
Change-first vs item-first: change-first + dry-run is honest hardware — customer never holds product while we “software restock.”
Exact change vs approximate: exact keeps CoR simple; approximate needs more denominations / cashier logic.
Command for purchase/refund vs logic inside states: Command keeps states thin and extends payment types cleanly.
Single-machine sync vs shared DB: sync is fine for one JVM; fleet needs row locks and shared change inventory.

## 11. Follow-Up Answers
Cannot make exact change: `canMakeChange` fails before motor; full refund; stock unchanged; optionally message “exact change only.”
Motor jam after payment/change: treat as ops incident — refund or write-off; do not pretend physical return; alert + MaintenanceState.
Maintenance / restock: `MaintenanceState` rejects purchase; allow restock APIs on slots and change counts.
Card payment: new `CardPaymentTransaction` (Command); states unchanged — OCP.
Multi-machine fleet: shared `item_slots` + per-machine `change_inventory`; `FOR UPDATE` on purchase; transaction log for audit.
Partial coin return after cancel: RefundTransaction returns full `insertedCredit` via tray; no item released.
Idempotent purchase retries: transaction_id + status; do not double-dispense if client retries after success ACK lost.

---

**Record tip:** Speak 0→5 with diagram + code on screen; 6–7 in ~90 seconds; 8–9 briefly; close with 10–11.
**Code file:** `LLD_revise/VendingMachine.java`
