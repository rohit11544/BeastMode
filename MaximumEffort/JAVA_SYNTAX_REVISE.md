# JAVA_SYNTAX_REVISE — 5 min morning skim

Daily: **5 min this file** + **10–15 min [`DSA/DSA_REVISE.md`](./DSA/DSA_REVISE.md)** ≈ 20 min.

---

## 1. Declarations

```java
String st = "";
int[] a1 = new int[n];
List<Integer> a2 = new ArrayList<>();
Map<Integer, String> m2 = new HashMap<>();
Map<Character, Integer> mp = new HashMap<>(); // no primitive keys/values — use wrappers
Set<Integer> s2 = new HashSet<>();
Deque<Integer> dq2 = new ArrayDeque<>();
Queue<Integer> minQ = new PriorityQueue<>();
Queue<Integer> maxQ = new PriorityQueue<>(Collections.reverseOrder());
```

`TreeMap` / `TreeSet` = same APIs as `HashMap` / `HashSet`, but keys/elements stay **sorted**. Swap `Hash` → `Tree`.

---

## 2. Add / Remove / Size

| Structure | Add | Remove | Size |
|-----------|-----|--------|------|
| `String` | prefer `StringBuilder` | — | `.length()` |
| `int[]` | `a1[i] = x` | — | `.length` |
| `ArrayList` | `.add(x)` | `.remove(Integer.valueOf(10))` | `.size()` |
| `HashMap` | `.put(k,v)` | `.remove(k)` | `.size()` |
| `HashSet` | `.add(x)` | `.remove(x)` | `.size()` |
| `Deque` | `.addLast` / `.addFirst` / `.add` | `.removeLast` / `.removeFirst` / `.remove` | `.size()` |
| `PriorityQueue` | `.add(x)` | `.poll()` | `.size()` |

Never do `st = st + "a"` in a loop → use `StringBuilder`.

---

## 3. StringBuilder (mutate / build)

```java
StringBuilder sb = new StringBuilder("hello");
sb.append(" world");       // amortized O(1)
sb.setCharAt(2, 'a');      // O(1)
sb.charAt(i);
sb.length();
sb.deleteCharAt(2);        // O(N)
sb.reverse();              // O(N)
String ans = sb.toString(); // O(N)
```

---

## 4. String (read-only)

```java
String st = "hello";
st.length();                         // O(1)
st.charAt(2);                        // O(1)
st.substring(1, 4);                  // O(N) — [1,4)
st.indexOf('e');                     // O(N) — -1 if missing
st.equals("hello");                  // O(N) — NEVER ==
st.equalsIgnoreCase("hello");

char[] ch = st.toCharArray();        // O(N)
String st2 = new String(ch);         // O(N)
int num = Integer.parseInt("123");
String st3 = String.valueOf(123);
```

---

## 5. Array / List / Map / Set / Deque / PQ

```java
// Array
Arrays.sort(a1);  // primitives: ascending only

// ArrayList
Collections.sort(a2);
Collections.sort(a2, Collections.reverseOrder());
a2.set(idx, value);
a2.contains(15);

// HashMap
m2.get(1);
m2.containsKey(1);
m2.containsValue("One");
mp.put(c, mp.getOrDefault(c, 0) + 1);
for (Map.Entry<Integer, String> e : m2.entrySet()) {
    // e.getKey(), e.getValue()
}

// HashSet
s2.contains(10);
for (int num : s2) { }

// Deque
dq2.peekFirst();
dq2.peekLast();
dq2.isEmpty();

// PriorityQueue
pq.peek();   // top without remove
pq.poll();   // top + remove

// max-heap of points by distance²
PriorityQueue<int[]> pq = new PriorityQueue<>(
    (a, b) -> (b[0]*b[0] + b[1]*b[1]) - (a[0]*a[0] + a[1]*a[1])
);
```

---

## 6. 2D ArrayList

```java
List<List<Integer>> mainList = new ArrayList<>();
List<Integer> sub1 = new ArrayList<>();
sub1.add(1); sub1.add(2);
mainList.add(sub1);
```

---

## 7. Sorting (comparators) — know cold

```java
// primitive int[] — ascending only, no comparator
Arrays.sort(intArr);

// Integer[] — comparator OK; use Integer.compare (no overflow)
Arrays.sort(integerArr, (x, y) -> Integer.compare(y, x)); // desc

// key-based (safe)
Arrays.sort(arr2D, Comparator.comparingInt(o -> o[1]));           // asc by col1
Arrays.sort(arr2D, Comparator.comparingInt((int[] o) -> o[1]).reversed()); // desc — need explicit type
Arrays.sort(arr2D, Comparator.comparingInt(o -> -o[1]));          // desc without .reversed()

// ratio / computed key
Arrays.sort(items, Comparator.comparingDouble((int[] o) -> (double) o[0] / o[1]).reversed());

// multi-key
Arrays.sort(intervals, Comparator.comparingInt((int[] o) -> o[1]).thenComparingInt(o -> o[0]));

// List
list.sort(Comparator.comparingInt(o -> o[1]));
```

**Gotchas**
- `int[]` alone: no comparator. `int[][]` / `Integer[]` / custom: yes
- Object sorts = stable (TimSort); primitive = unstable
- Avoid `(x - y)` → use `Integer.compare` / `comparingInt`
- Lambda alone → type inferred; inside `.reversed()` / `.thenComparing()` → **explicit type**

---

## 8. Tree node (interview style)

```java
class Node {
    int data;
    Node left, right;
    Node(int data) {
        this.data = data;
        this.left = this.right = null;
    }
}

class Solution {
    Node prev = null, head = null;

    void inorder(Node root) { /* ... */ }

    void problemName(Node root) {
        inorder(root);
    }
}
```

Only `Node` + `Solution` — no full Tree class boilerplate.

---

## 9. 30-second checklist

- [ ] `StringBuilder` in loops  
- [ ] `.equals` not `==` for strings  
- [ ] `.length` vs `.length()` vs `.size()`  
- [ ] `remove(Integer.valueOf(x))` not `remove(x)` when x is index-looking  
- [ ] PQ: `poll` removes, `peek` doesn’t  
- [ ] Comparator: `Integer.compare` / `comparingInt`, explicit type when chaining  
