import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Jira-like Issue Tracker — ONE FILE
 * Patterns: Facade · workflow graph · Strategy guards · Observer · per-issue lock
 * Lombok (interview): @Getter @Setter @RequiredArgsConstructor on domain — accessors omitted here
 *
 *   javac Main.java && java Main
 */

// ========== DOMAIN ==========

enum IssueType { EPIC, STORY, BUG, TASK, SUBTASK }

class Status {
    final String name;
    Status(String name) { this.name = name.toUpperCase(); }
    @Override public boolean equals(Object o) {
        if (!(o instanceof Status)) return false;
        return name.equals(((Status) o).name);
    }
    @Override public int hashCode() { return name.hashCode(); }
    @Override public String toString() { return name; }
}

class User {
    final String userId;
    final String name;
    User(String userId, String name) { this.userId = userId; this.name = name; }
}

class AuditLog {
    final String issueId, eventType, detail, actorUserId;
    final Status fromStatus, toStatus;

    private AuditLog(String issueId, String eventType, Status from, Status to, String detail, String actor) {
        this.issueId = issueId;
        this.eventType = eventType;
        this.fromStatus = from;
        this.toStatus = to;
        this.detail = detail;
        this.actorUserId = actor;
    }

    static AuditLog transition(String issueId, Status from, Status to, String actor) {
        return new AuditLog(issueId, "TRANSITION", from, to, from + " -> " + to, actor);
    }

    static AuditLog fieldUpdate(String issueId, String detail, String actor) {
        return new AuditLog(issueId, "FIELD_UPDATE", null, null, detail, actor);
    }
}

class Issue {
    final String issueId;
    final IssueType type;
    final User reporter;
    final String parentIssueId;
    final List<String> childIssueIds = new CopyOnWriteArrayList<>();
    final Map<String, Object> customFields = new ConcurrentHashMap<>();
    final ReentrantLock lock = new ReentrantLock();

    String summary;
    volatile Status status;
    volatile User assignee;

    Issue(String issueId, IssueType type, String summary, User reporter,
          Status initialStatus, String parentIssueId) {
        this.issueId = issueId;
        this.type = type;
        this.summary = summary;
        this.reporter = reporter;
        this.status = initialStatus;
        this.parentIssueId = parentIssueId;
    }
}

// ========== OBSERVER ==========

interface IssueEventListener {
    void onTransitioned(Issue issue, Status from, Status to, User actor);
}

class TransitionNotifierListener implements IssueEventListener {
    @Override
    public void onTransitioned(Issue issue, Status from, Status to, User actor) {
        System.out.println("[Observer] notify: " + issue.issueId + " " + from + " -> " + to);
    }
}

// ========== WORKFLOW ==========

interface TransitionGuard {
    boolean validate(Issue issue, User actor, Status targetStatus);
}

class MustHaveAssigneeGuard implements TransitionGuard {
    @Override
    public boolean validate(Issue issue, User actor, Status targetStatus) {
        return issue.assignee != null;
    }
}

class MustHaveResolutionGuard implements TransitionGuard {
    @Override
    public boolean validate(Issue issue, User actor, Status targetStatus) {
        return issue.customFields.containsKey("resolution");
    }
}

class Transition {
    final Status fromStatus, toStatus;
    final List<TransitionGuard> guards = new ArrayList<>();

    Transition(Status from, Status to) {
        this.fromStatus = from;
        this.toStatus = to;
    }

    Transition addGuard(TransitionGuard guard) {
        guards.add(guard);
        return this;
    }

    boolean canExecute(Issue issue, User actor) {
        for (TransitionGuard g : guards) {
            if (!g.validate(issue, actor, toStatus)) return false;
        }
        return true;
    }
}

class Workflow {
    final Status initialStatus;
    final Map<Status, Map<Status, Transition>> graph = new HashMap<>();

    Workflow(Status initialStatus) { this.initialStatus = initialStatus; }

    Workflow addTransition(Transition t) {
        graph.computeIfAbsent(t.fromStatus, k -> new HashMap<>()).put(t.toStatus, t);
        return this;
    }

    Transition getTransition(Status from, Status to) {
        Map<Status, Transition> edges = graph.get(from);
        return edges != null ? edges.get(to) : null;
    }
}

class WorkflowRegistry {
    final Map<IssueType, Workflow> byType = new ConcurrentHashMap<>();

    void register(IssueType type, Workflow wf) { byType.put(type, wf); }

    Workflow get(IssueType type) {
        Workflow wf = byType.get(type);
        if (wf == null) throw new IllegalStateException("No workflow for " + type);
        return wf;
    }
}

// ========== FACADE ==========

class IssueService {
    final WorkflowRegistry registry;
    final Map<String, Issue> store = new ConcurrentHashMap<>();
    final List<AuditLog> auditLogs = new CopyOnWriteArrayList<>();
    final List<IssueEventListener> listeners = new CopyOnWriteArrayList<>();
    final AtomicInteger seq = new AtomicInteger(100);

    IssueService(WorkflowRegistry registry) { this.registry = registry; }

    void addListener(IssueEventListener l) { listeners.add(l); }

    Issue createIssue(IssueType type, String summary, User reporter, String parentId) {
        Workflow wf = registry.get(type);
        String id = type.name() + "-" + seq.incrementAndGet();
        Issue issue = new Issue(id, type, summary, reporter, wf.initialStatus, parentId);
        store.put(id, issue);
        if (parentId != null) {
            Issue parent = store.get(parentId);
            if (parent != null) parent.childIssueIds.add(id);
        }
        System.out.println("[IssueService] Created " + id + " [" + type + "] status=" + wf.initialStatus);
        return issue;
    }

    void assignIssue(String issueId, User assignee, User actor) {
        Issue issue = require(issueId);
        issue.lock.lock();
        try {
            issue.assignee = assignee;
            auditLogs.add(AuditLog.fieldUpdate(issueId, "assignee=" + assignee.name, actor.userId));
            System.out.println("[Field] " + issueId + " assignee -> " + assignee.name);
        } finally {
            issue.lock.unlock();
        }
    }

    void setCustomField(String issueId, String key, Object value, User actor) {
        Issue issue = require(issueId);
        issue.lock.lock();
        try {
            issue.customFields.put(key, value);
            auditLogs.add(AuditLog.fieldUpdate(issueId, key + "=" + value, actor.userId));
            System.out.println("[Field] " + issueId + " " + key + " -> " + value);
        } finally {
            issue.lock.unlock();
        }
    }

    boolean transitionIssue(String issueId, Status target, User actor) {
        Issue issue = require(issueId);
        issue.lock.lock();
        Status from;
        try {
            from = issue.status;
            Transition edge = registry.get(issue.type).getTransition(from, target);
            if (edge == null) {
                System.out.println("[Rejected] No edge " + from + " -> " + target + " for " + issueId);
                return false;
            }
            if (!edge.canExecute(issue, actor)) {
                System.out.println("[Guard Failed] " + actor.name + " cannot move " + issueId + " -> " + target);
                return false;
            }
            issue.status = target;
            auditLogs.add(AuditLog.transition(issueId, from, target, actor.userId));
            System.out.println("[Transition] " + issueId + ": " + from + " -> " + target + " by " + actor.name);
        } finally {
            issue.lock.unlock();
        }
        for (IssueEventListener l : listeners) {
            l.onTransitioned(issue, from, target, actor);
        }
        return true;
    }

    private Issue require(String issueId) {
        Issue issue = store.get(issueId);
        if (issue == null) throw new IllegalArgumentException("Issue not found: " + issueId);
        return issue;
    }
}

// ========== MAIN ==========

public class Main {
    public static void main(String[] args) {
        Status open = new Status("OPEN"), inProgress = new Status("IN_PROGRESS");
        Status inReview = new Status("IN_REVIEW"), closed = new Status("CLOSED"), done = new Status("DONE");
        User alice = new User("U1", "Alice"), bob = new User("U2", "Bob");

        TransitionGuard assigneeGuard = new MustHaveAssigneeGuard();
        TransitionGuard resolutionGuard = new MustHaveResolutionGuard();

        Workflow bugWf = new Workflow(open);
        bugWf.addTransition(new Transition(open, inProgress).addGuard(assigneeGuard))
            .addTransition(new Transition(inProgress, inReview))
            .addTransition(new Transition(inReview, closed).addGuard(resolutionGuard));

        Workflow storyWf = new Workflow(open);
        storyWf.addTransition(new Transition(open, inProgress).addGuard(assigneeGuard))
            .addTransition(new Transition(inProgress, done));

        WorkflowRegistry registry = new WorkflowRegistry();
        registry.register(IssueType.BUG, bugWf);
        registry.register(IssueType.STORY, storyWf);
        registry.register(IssueType.EPIC, storyWf);

        IssueService service = new IssueService(registry);
        service.addListener(new TransitionNotifierListener());

        System.out.println("=== TEST 1: GUARD FAIL (NO ASSIGNEE) ===");
        Issue bug1 = service.createIssue(IssueType.BUG, "NPE on Login", alice, null);
        service.transitionIssue(bug1.issueId, inProgress, alice);

        System.out.println("\n=== TEST 2: BUG HAPPY PATH ===");
        service.assignIssue(bug1.issueId, alice, alice);
        service.transitionIssue(bug1.issueId, inProgress, alice);
        service.transitionIssue(bug1.issueId, inReview, alice);

        System.out.println("\n=== TEST 3: GUARD FAIL (NO RESOLUTION) ===");
        service.transitionIssue(bug1.issueId, closed, bob);

        System.out.println("\n=== TEST 4: CLOSE WITH RESOLUTION ===");
        service.setCustomField(bug1.issueId, "resolution", "FIXED", bob);
        service.transitionIssue(bug1.issueId, closed, bob);

        System.out.println("\n=== TEST 5: EPIC → STORY ===");
        Issue epic = service.createIssue(IssueType.EPIC, "Checkout redesign", alice, null);
        Issue story = service.createIssue(IssueType.STORY, "Pay button", bob, epic.issueId);
        System.out.println("Parent " + epic.issueId + " children=" + epic.childIssueIds);
        System.out.println("Audit events: " + service.auditLogs.size());
    }
}
