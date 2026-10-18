/**
 * Vending Machine — Interview PSEUDOCODE (revise this)
 *
 * How you'll use it in interview:
 *   1. FR / NFR (speak)
 *   2. Entities + state diagram (speak / draw)
 *   3. Write THIS style of code
 *   4. Patterns + SOLID + concurrency (speak)
 *
 * Patterns: State · Command · Chain of Responsibility · Facade
 * Lombok: @Getter / @Setter / @AllArgsConstructor — skip writing accessors
 * Compile: NOT meant to compile — interview sketch only.
 *
 * Lifecycle: Idle → HasMoney → Dispensing → Idle
 *            HasMoney → refund → Idle
 */

// ============================================================
// DOMAIN + HARDWARE
// ============================================================

enum Coin {
    QUARTER(0.25),
    ONE(1.00),
    FIVE(5.00);

    private final double value;
    Coin(double value) { this.value = value; }
    double getValue() { return value; }
}

@Getter
@AllArgsConstructor
class Item {
    private String code;   // A1, B2
    private String name;
    private double price;
}

@Getter
@AllArgsConstructor
class ItemSlot {
    private Item item;
    private int quantity;

    // synchronized in real code — last Coke race
    boolean dispense() {
        if (quantity <= 0) return false;
        quantity--;
        return true;
    }

    void restock(int qty) { quantity += qty; }
}

class ItemMotor {
    void releaseItem(String slotCode) { print("Dispensing " + slotCode); }
}

class CoinReturnTray {
    void returnCoins(double amount) { print("Returned $" + amount); }
}

class DisplayScreen {
    void show(String msg) { print(msg); }
}

// ============================================================
// CHANGE — Chain of Responsibility ($5 → $1 → $0.25)
// canMakeChange = dry-run (no mutate) · dispenseChange = commit notes
// ============================================================

interface ChangeDispenser {
    boolean canMakeChange(double amount);   // dry-run — inventory unchanged
    boolean dispenseChange(double amount);  // commit — only after canMake
    void setNext(ChangeDispenser next);
}

@Getter
@Setter
abstract class NoteDispenser implements ChangeDispenser {
    private ChangeDispenser next;
    private double coinValue;
    private int count;

    NoteDispenser(double coinValue, int count) {
        this.coinValue = coinValue;
        this.count = count;
    }

    // Pass 1: can we make exact amount? Do NOT decrement count.
    boolean canMakeChange(double amount) {
        amount = round2(amount);
        if (amount == 0) return true;

        int use = Math.min((int) (amount / coinValue), count);
        double remaining = round2(amount - use * coinValue);

        if (remaining == 0) return true;
        if (next == null) return false;
        return next.canMakeChange(remaining);
    }

    // Pass 2: same greedy plan, now decrement. Call only after canMakeChange.
    boolean dispenseChange(double amount) {
        amount = round2(amount);
        if (amount == 0) return true;

        int use = Math.min((int) (amount / coinValue), count);
        double remaining = round2(amount - use * coinValue);

        if (remaining > 0) {
            if (next == null || !next.dispenseChange(remaining)) {
                return false;
            }
        }
        count -= use;
        return true;
    }
}

class FiveDollarDispenser extends NoteDispenser {
    FiveDollarDispenser(int count) { super(5.00, count); }
}

class OneDollarDispenser extends NoteDispenser {
    OneDollarDispenser(int count) { super(1.00, count); }
}

class QuarterDispenser extends NoteDispenser {
    QuarterDispenser(int count) { super(0.25, count); }
}

// Wire: five.setNext(one); one.setNext(quarter);

// ============================================================
// COMMAND — one business action each
// ============================================================

interface Transaction {
    boolean execute(VendingMachine machine);
}

@AllArgsConstructor
class PurchaseTransaction implements Transaction {
    private String slotCode;

    boolean execute(VendingMachine machine) {
        ItemSlot slot = machine.getInventory().get(slotCode);
        Item item = slot == null ? null : slot.getItem();
        double credit = machine.getInsertedCredit();
        double changeDue = item == null ? 0 : round2(credit - item.getPrice());
        ChangeDispenser change = machine.getChangeDispenser();

        // ----- 1) VALIDATE (nothing released yet) -----
        if (slot == null || slot.getQuantity() == 0) {
            machine.getScreen().show("Out of stock");
            return false; // caller → full refund, stock unchanged
        }
        if (credit < item.getPrice()) {
            machine.getScreen().show("Insufficient credit");
            return false;
        }
        if (changeDue > 0 && !change.canMakeChange(changeDue)) {
            machine.getScreen().show("Exact change not available");
            return false; // full refund; item still in machine
        }

        // ----- 2) COMMIT PAYMENT (credit cleared; sale locked in) -----
        // Interview: money accepted for price; change path proven by dry-run.
        machine.resetCredit();

        // ----- 3) DISPENSE CHANGE FIRST (item still behind flap / not released) -----
        if (changeDue > 0 && !change.dispenseChange(changeDue)) {
            // Rare: dry-run passed, commit failed (race / jam).
            // Refund FULL original credit; stock still unchanged; motor never ran.
            machine.addCredit(credit);
            machine.getScreen().show("Change jam — cancelling");
            return false;
        }

        // ----- 4) ONLY NOW release item -----
        if (!slot.dispense()) {
            // Stock raced to 0 after validate — refund price+change already paid out?
            // Interview: refund original credit; if change already out, ops loss / alert.
            machine.addCredit(credit);
            if (changeDue > 0) {
                // change already given → customer got change for a sale that didn't happen
                // real world: alert + accept loss OR don't give change until motor confirms
                machine.getScreen().show("Stock race — alert ops");
            }
            machine.getScreen().show("Dispense failed");
            return false;
        }
        machine.getItemMotor().releaseItem(slotCode);

        // ----- 5) success: if we failed before motor, DispensingState refunds -----
        machine.getScreen().show("Enjoy " + item.getName());
        return true;
    }
}

class RefundTransaction implements Transaction {
    boolean execute(VendingMachine machine) {
        double credit = machine.getInsertedCredit();
        if (credit <= 0) return false;
        machine.getCoinTray().returnCoins(credit);
        machine.resetCredit();
        machine.getScreen().show("Refunded");
        return true;
    }
}

// ============================================================
// STATE MACHINE
// Idle → HasMoney → Dispensing → Idle
// ============================================================

interface VendingMachineState {
    void insertCoin(VendingMachine machine, Coin coin);
    void selectItem(VendingMachine machine, String slotCode);
    void dispenseItem(VendingMachine machine, String slotCode);
    void refund(VendingMachine machine);
}

class IdleState implements VendingMachineState {
    void insertCoin(VendingMachine machine, Coin coin) {
        machine.addCredit(coin.getValue());
        machine.setState(new HasMoneyState());
        machine.getScreen().show("Credit: $" + machine.getInsertedCredit());
    }

    void selectItem(VendingMachine machine, String slotCode) {
        machine.getScreen().show("Insert coins first");
    }

    void dispenseItem(VendingMachine machine, String slotCode) {
        machine.getScreen().show("Insert coins first");
    }

    void refund(VendingMachine machine) {
        machine.getScreen().show("No credit");
    }
}

class HasMoneyState implements VendingMachineState {
    void insertCoin(VendingMachine machine, Coin coin) {
        machine.addCredit(coin.getValue());
        machine.getScreen().show("Credit: $" + machine.getInsertedCredit());
    }

    void selectItem(VendingMachine machine, String slotCode) {
        machine.setState(new DispensingState());
        machine.dispenseItem(slotCode); // enter dispense path
    }

    void dispenseItem(VendingMachine machine, String slotCode) {
        machine.getScreen().show("Select item first");
    }

    void refund(VendingMachine machine) {
        new RefundTransaction().execute(machine);
        machine.setState(new IdleState());
    }
}

class DispensingState implements VendingMachineState {
    void insertCoin(VendingMachine machine, Coin coin) {
        machine.getScreen().show("Please wait");
    }

    void selectItem(VendingMachine machine, String slotCode) {
        machine.getScreen().show("Please wait");
    }

    void dispenseItem(VendingMachine machine, String slotCode) {
        boolean ok = new PurchaseTransaction(slotCode).execute(machine);
        if (!ok) {
            // stock fail OR change fail → full refund
            new RefundTransaction().execute(machine);
        }
        machine.setState(new IdleState());
    }

    void refund(VendingMachine machine) {
        machine.getScreen().show("Cannot refund while dispensing");
    }
}

// Optional interview extension:
// class MaintenanceState — reject purchase, allow restock

// ============================================================
// FACADE / CONTEXT
// ============================================================

@Getter
@Setter
class VendingMachine {
    private VendingMachineState state;
    private double insertedCredit;
    private DisplayScreen screen;
    private ItemMotor itemMotor;
    private CoinReturnTray coinTray;
    private ChangeDispenser changeDispenser; // head of CoR
    private Map<String, ItemSlot> inventory;

    VendingMachine(ChangeDispenser changeDispenser, Map<String, ItemSlot> inventory) {
        this.changeDispenser = changeDispenser;
        this.inventory = inventory;
        this.screen = new DisplayScreen();
        this.itemMotor = new ItemMotor();
        this.coinTray = new CoinReturnTray();
        this.state = new IdleState();
        this.insertedCredit = 0;
    }

    // synchronized in interview talk: state + credit
    void addCredit(double amount) { insertedCredit += amount; }
    void resetCredit() { insertedCredit = 0; }

    // Facade API — interview writes these 4
    void insertCoin(Coin coin) { state.insertCoin(this, coin); }
    void selectItem(String slotCode) { state.selectItem(this, slotCode); }
    void dispenseItem(String slotCode) { state.dispenseItem(this, slotCode); }
    void requestRefund() { state.refund(this); }
}

// ============================================================
// MAIN (happy path sketch)
// ============================================================

class Main {
    void demo() {
        FiveDollarDispenser five = new FiveDollarDispenser(10);
        OneDollarDispenser one = new OneDollarDispenser(20);
        QuarterDispenser quarter = new QuarterDispenser(30);
        five.setNext(one);
        one.setNext(quarter);

        Map<String, ItemSlot> inv = new HashMap<>();
        inv.put("A1", new ItemSlot(new Item("A1", "Coke", 1.50), 5));
        inv.put("B2", new ItemSlot(new Item("B2", "Chips", 1.25), 3));

        VendingMachine vm = new VendingMachine(five, inv);

        // success: $2 → Coke $1.50 → change $0.50 (2 quarters)
        vm.insertCoin(Coin.ONE);
        vm.insertCoin(Coin.ONE);
        vm.selectItem("A1");

        // refund path
        vm.insertCoin(Coin.FIVE);
        vm.requestRefund();
    }
}

// ============================================================
// INTERVIEW SPEAK — Requirements · Patterns · SOLID
// ============================================================
//
// REQUIREMENTS
// FR: insert coin, select slot, dispense item + change, refund
// NFR: thread-safe state/credit/qty/change; no sale if change fails
//
// DESIGN PATTERNS
// Facade  — VendingMachine (insertCoin / selectItem / requestRefund)
// State   — Idle → HasMoney → Dispensing → Idle
// Command — PurchaseTransaction / RefundTransaction
// CoR     — $5 → $1 → $0.25 change (canMakeChange then dispenseChange)
//
// SOLID
// S — State = flow, Slot = stock, Command = one action, peripherals = hardware
// O — add MaintenanceState / CardPayment / $10 dispenser without rewrite
// L — any State / Transaction / ChangeDispenser plugs in
// I — thin state API: insertCoin / selectItem / dispenseItem / refund
// D — machine depends on ChangeDispenser + Transaction abstractions
//
// ============================================================
// SKELETON — blank for interview coding
// ============================================================
/*
// DOMAIN + HARDWARE
enum Coin { }

class Item { }
class ItemSlot { }
class ItemMotor { }
class CoinReturnTray { }
class DisplayScreen { }

// CHANGE — CoR
interface ChangeDispenser { }
abstract class NoteDispenser implements ChangeDispenser { }
class FiveDollarDispenser extends NoteDispenser { }
class OneDollarDispenser extends NoteDispenser { }
class QuarterDispenser extends NoteDispenser { }

// COMMAND
interface Transaction { }
class PurchaseTransaction implements Transaction { }
class RefundTransaction implements Transaction { }

// STATE
interface VendingMachineState { }
class IdleState implements VendingMachineState { }
class HasMoneyState implements VendingMachineState { }
class DispensingState implements VendingMachineState { }

// FACADE
class VendingMachine { }

class Main { }
*/
