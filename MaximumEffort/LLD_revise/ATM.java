/**
 * ATM — Interview PSEUDOCODE (revise this)
 *
 * How you'll use it in interview:
 *   1. FR / NFR (speak)
 *   2. Entities + state diagram (speak / draw)
 *   3. Write THIS style of code
 *   4. Patterns + SOLID + concurrency (speak)
 *
 * Patterns: State · Chain of Responsibility · Facade · Strategy/Command (txns)
 * Lombok: @Getter / @Setter / @AllArgsConstructor — skip writing accessors
 * Compile: NOT meant to compile — interview sketch only.
 */

// ============================================================
// DOMAIN
// ============================================================

@Getter
@Setter
@AllArgsConstructor
class Card {
    private int accountNumber;
    private int pin;

    // Mock-only. Real world: bank validates PIN.
    boolean validatePin(int inputPin) {
        return pin == inputPin;
    }
}

@Getter
@Setter
@AllArgsConstructor
class Account {
    private int accountNumber;
    private Card card;
    private int balance;

    void credit(int amount) {
        balance = balance + amount;
    }

    // Only call AFTER cash successfully dispensed.
    void debit(int amount) {
        balance = balance - amount;
    }

    boolean hasSufficientBalance(int amount) {
        return balance >= amount;
    }
}

enum TransactionType {
    CREDIT,   // deposit
    DEBIT,    // withdraw
    BALANCE
}

@Getter
@Setter
@AllArgsConstructor
class Transaction {
    private TransactionType type;
    private int amount;
}

// ============================================================
// BANK
// ============================================================

interface BankService {
    boolean authenticate(int accountNumber, int inputPin);
    int checkBalance(int accountNumber);
    void credit(int accountNumber, int amount);
    boolean debit(int accountNumber, int amount); // true if money taken from account
}

@Getter
@Setter
class MockBankService implements BankService {
    private Map<Integer, Account> accountByNumber;
    // Note: dispenser lives on ATM, NOT on bank.
    // Bank only owns money; ATM owns cash hardware.

    boolean authenticate(int accountNumber, int inputPin) {
        Account account = accountByNumber.get(accountNumber);
        if (account == null) return false;
        return account.getCard().validatePin(inputPin);
    }

    int checkBalance(int accountNumber) {
        return accountByNumber.get(accountNumber).getBalance();
    }

    void credit(int accountNumber, int amount) {
        accountByNumber.get(accountNumber).credit(amount);
    }

    // Atomicity talk track:
    // ATM should call dispenser FIRST, then debit.
    // Or: debit + compensate if dispense fails.
    // Preferred interview line: "dispense success → then debit bank".
    boolean debit(int accountNumber, int amount) {
        Account account = accountByNumber.get(accountNumber);
        if (!account.hasSufficientBalance(amount)) {
            return false;
        }
        account.debit(amount);
        return true;
    }
}

// ============================================================
// CASH DISPENSER — Chain of Responsibility ($50 → $20 → $10)
// ============================================================

interface CashDispenser {
    boolean dispense(int amount);
    void setNext(CashDispenser next);
}

@Getter
@Setter
abstract class NoteDispenser implements CashDispenser {
    private CashDispenser next;
    private int noteValue;
    private int numberOfNotes;

    NoteDispenser(int noteValue, int numberOfNotes) {
        this.noteValue = noteValue;
        this.numberOfNotes = numberOfNotes;
    }

    boolean dispense(int amount) {
        int notesNeeded = amount / noteValue;
        int notesToUse = Math.min(numberOfNotes, notesNeeded);
        int remaining = amount - (notesToUse * noteValue);

        // Tentative use; only commit notes if full amount can be made.
        // Interview: mention 2-pass (canMake?) then deduct, or rollback.

        if (remaining == 0) {
            numberOfNotes -= notesToUse;
            return true;
        }

        if (next != null && next.dispense(remaining)) {
            numberOfNotes -= notesToUse;
            return true;
        }

        // Cannot make exact amount.
        return false;
    }
}

class FiftyCashDispenser extends NoteDispenser {
    FiftyCashDispenser(int numberOfNotes) { super(50, numberOfNotes); }
}

class TwentyCashDispenser extends NoteDispenser {
    TwentyCashDispenser(int numberOfNotes) { super(20, numberOfNotes); }
}

class TenCashDispenser extends NoteDispenser {
    TenCashDispenser(int numberOfNotes) { super(10, numberOfNotes); }
}

// Wire: fifty.setNext(twenty); twenty.setNext(ten);

// ============================================================
// STATE MACHINE
// Idle → CardInserted → Authenticated → (txn) → back toward Idle via eject
// ============================================================

interface ATMState {
    void insertCard(Card card);
    void authenticate(int inputPin);
    void execute(Transaction transaction);
    void ejectCard();
}

@AllArgsConstructor
class IdleState implements ATMState {
    private ATM atm;

    void insertCard(Card card) {
        print("Card inserted");
        atm.setCurrentCard(card);
        atm.setState(new CardInsertedState(atm));
    }

    void authenticate(int inputPin) {
        print("Insert card first");
    }

    void execute(Transaction transaction) {
        print("Insert card first");
    }

    void ejectCard() {
        print("No card to eject");
    }
}

@AllArgsConstructor
class CardInsertedState implements ATMState {
    private ATM atm;

    void insertCard(Card card) {
        print("Card already inserted");
    }

    void authenticate(int inputPin) {
        Card card = atm.getCurrentCard();
        boolean ok = atm.getBankService()
                .authenticate(card.getAccountNumber(), inputPin);

        if (ok) {
            print("Authenticated");
            atm.setState(new AuthenticatedState(atm));
        } else {
            print("Wrong PIN — ejecting");
            atm.getCardEjector().eject();
            atm.setCurrentCard(null);
            atm.setState(new IdleState(atm));
        }
    }

    void execute(Transaction transaction) {
        print("Authenticate first");
    }

    void ejectCard() {
        atm.getCardEjector().eject();
        atm.setCurrentCard(null);
        atm.setState(new IdleState(atm));
    }
}

@AllArgsConstructor
class AuthenticatedState implements ATMState {
    private ATM atm;

    void insertCard(Card card) {
        print("Card already inserted");
    }

    void authenticate(int inputPin) {
        print("Already authenticated");
    }

    void execute(Transaction transaction) {
        int accountNumber = atm.getCurrentCard().getAccountNumber();
        BankService bank = atm.getBankService();

        if (transaction.getType() == TransactionType.BALANCE) {
            print("Balance = " + bank.checkBalance(accountNumber));
            // stay authenticated OR force eject — say your choice
            return;
        }

        if (transaction.getType() == TransactionType.CREDIT) {
            bank.credit(accountNumber, transaction.getAmount());
            print("Deposited " + transaction.getAmount());
            atm.setState(new EjectState(atm));
            return;
        }

        if (transaction.getType() == TransactionType.DEBIT) {
            // 1) bank has money?
            if (bank.checkBalance(accountNumber) < transaction.getAmount()) {
                print("Insufficient account balance");
                return;
            }

            // 2) ATM can make exact cash?
            boolean dispensed = atm.getCashDispenser().dispense(transaction.getAmount());
            if (!dispensed) {
                print("Cannot dispense exact amount / ATM out of notes");
                return; // do NOT debit bank
            }

            // 3) only now debit bank
            bank.debit(accountNumber, transaction.getAmount());
            print("Withdrawn " + transaction.getAmount());
            atm.setState(new EjectState(atm));
            return;
        }
    }

    void ejectCard() {
        atm.getCardEjector().eject();
        atm.setCurrentCard(null);
        atm.setState(new IdleState(atm));
    }
}

@AllArgsConstructor
class EjectState implements ATMState {
    private ATM atm;

    void insertCard(Card card) {
        print("Eject current card first");
    }

    void authenticate(int inputPin) {
        print("Transaction done — eject card");
    }

    void execute(Transaction transaction) {
        print("Transaction done — eject card");
    }

    void ejectCard() {
        atm.getCardEjector().eject();
        atm.setCurrentCard(null);
        atm.setState(new IdleState(atm));
    }
}

// ============================================================
// HARDWARE STUBS
// ============================================================

class CardEjector {
    void eject() { print("Card ejected"); }
}

// ============================================================
// ATM FACADE
// ============================================================

@Getter
@Setter
class ATM {
    private ATMState state;
    private BankService bankService;
    private CashDispenser cashDispenser; // head of CoR: 50 → 20 → 10
    private CardEjector cardEjector;
    private Card currentCard;

    ATM(BankService bankService, CashDispenser cashDispenser, CardEjector cardEjector) {
        this.bankService = bankService;
        this.cashDispenser = cashDispenser;
        this.cardEjector = cardEjector;
        this.state = new IdleState(this);
    }

    // Facade API — interview writes these 4
    void insertCard(Card card) { state.insertCard(card); }
    void authenticate(int inputPin) { state.authenticate(inputPin); }
    void execute(Transaction transaction) { state.execute(transaction); }
    void ejectCard() { state.ejectCard(); }
}

// ============================================================
// MAIN (happy path sketch)
// ============================================================

class Main {
    void demo() {
        // Build CoR: 50 → 20 → 10
        FiftyCashDispenser fifty = new FiftyCashDispenser(10);
        TwentyCashDispenser twenty = new TwentyCashDispenser(20);
        TenCashDispenser ten = new TenCashDispenser(50);
        fifty.setNext(twenty);
        twenty.setNext(ten);

        ATM atm = new ATM(new MockBankService(), fifty, new CardEjector());

        Card card = /* loaded card */;
        atm.insertCard(card);
        atm.authenticate(1234);
        atm.execute(new Transaction(TransactionType.DEBIT, 70)); // 50+20
        atm.ejectCard();
    }
}

// ============================================================
// TALK TRACK (memorize)
// ============================================================
//
// FR: insert card, PIN auth, withdraw/deposit/balance, eject, exact cash
// NFR: thread-safe state; no bank debit unless cash dispensed
//
// State: Idle → CardInserted → Authenticated → Eject → Idle
// CoR:   $50 → $20 → $10 (exact amount or fail)
//
// SOLID:
//   S — State owns flow, Bank owns money, Dispenser owns cash
//   O — add $100 dispenser / new state without rewriting ATM
//   L — any ATMState / CashDispenser plugs in
//   I — small state + bank APIs
//   D — ATM depends on BankService + CashDispenser abstractions
//
// Concurrency: synchronize setState + park currentCard;
//              bank account methods synchronized / DB row lock
//
// Interview tip: say "I'll use Lombok @Getter/@Setter/@AllArgsConstructor
//                 to skip boilerplate — focus on behavior."
//
