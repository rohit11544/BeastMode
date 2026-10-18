# Java Syntax — DSA Cheat Sheet

Quick reference for coding interviews.

---

## Collections overview

TreeMap and TreeSet are the same as HashMap and HashSet, respectively, except they maintain sorted order of keys or elements. Just replace `Hash` with `Tree` when implementing.

---

## Declaration

```java
String st = "";
int[] a1 = new int[n];
List<Integer> a2 = new ArrayList<>();
Map<Integer, String> m2 = new HashMap<>();
Map<Character, Integer> mp = new HashMap<>(); // Java does not support primitive types like char or int as type args
Set<Integer> s2 = new HashSet<>();
Deque<Integer> dq2 = new ArrayDeque<>();
Queue<Integer> q = new PriorityQueue<>();
Queue<Integer> q = new PriorityQueue<>(Collections.reverseOrder());
```

---

## Add

```java
st + " World";
a1[0] = 10;
a2.add(10);
m2.put(1, "One");
s2.add(10);
dq2.addLast(15); // OR dq2.add(10);
dq2.addFirst(5);
q.add(10);
```

---

## Remove

```java
a2.remove(Integer.valueOf(10));
m2.remove(1);
s2.remove(10);
dq2.removeLast(); // OR dq2.remove();
dq2.removeFirst();
q.poll();
```

---

## Length / size

| Type | Method |
|------|--------|
| String | `.length()` |
| int[] | `.length` |
| ArrayList\<Integer\> | `.size()` |
| HashMap\<Integer, Integer\> | `.size()` |
| HashSet\<String\> | `.size()` |
| ArrayDeque\<Integer\> | `.size()` |
| PriorityQueue\<Integer\> | `.size()` |

---

## String

### 1. StringBuilder (for modifying / building)

```java
StringBuilder sb = new StringBuilder("hello");
sb.append(" world");          // O(1) amortized — NEVER use st = st + "a" in a loop
sb.setCharAt(2, 'a');         // O(1) — update char at specific index
sb.reverse();                 // O(N) — reverses in-place
sb.deleteCharAt(2);           // O(N)
sb.charAt(i);
sb.length();
String ans = sb.toString();   // O(N) — convert back to String when done
```

### 2. String (read-only operations)

```java
String st = "hello";
int len = st.length();              // O(1)
char c = st.charAt(2);              // O(1)
String sub = st.substring(1, 4);    // O(N) — "ell" (index 1 up to 3)
int idx = st.indexOf('e');          // O(N) — first occurrence, -1 if not found
boolean same = st.equals("hello");  // O(N) — NEVER use ==
boolean same2 = st.equalsIgnoreCase("hello"); // O(N)
```

### 3. Conversions & iteration

```java
// String <-> Char Array
char[] chArr = st.toCharArray(); // O(N)
for (char ch : chArr) {
    // Process ch
}
String st2 = new String(chArr);  // O(N)

// String <-> Integer
int num = Integer.parseInt("123");
String st3 = String.valueOf(123);
```

---

## Array

```java
Arrays.sort(a1);
Arrays.sort(a2, Collections.reverseOrder()); // a2 must be Integer[]
```

---

## ArrayList

```java
Collections.sort(a2);
Collections.sort(a2, Collections.reverseOrder());
a2.set(idx, value);
boolean contains = a2.contains(15);
```

---

## HashMap

```java
String value = m2.get(1);
boolean hasKey = m2.containsKey(1);
boolean hasValue = m2.containsValue("One");
m2.remove(1);
mp.put(c, mp.getOrDefault(c, 0) + 1);

for (Map.Entry<Integer, String> entry : m2.entrySet()) {
    System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
}
```

---

## HashSet

```java
boolean contains = s2.contains(10);
for (int num : s2) {
    System.out.println(num);
}
```

---

## Deque

```java
dq2.peekFirst(); // get first
dq2.peekLast();  // get last
dq2.isEmpty();
```

---

## PriorityQueue

```java
int top = pq.peek(); // smallest/largest depending on heap type

// max heap — largest distance at top
PriorityQueue<int[]> pq = new PriorityQueue<>(
    (a, b) -> (b[0]*b[0] + b[1]*b[1]) - (a[0]*a[0] + a[1]*a[1])
);
```

---

## 2D ArrayList

```java
List<List<Integer>> mainList = new ArrayList<>();

List<Integer> subList1 = new ArrayList<>();
subList1.add(1);
subList1.add(2);

List<Integer> subList2 = new ArrayList<>();
subList2.add(3);
subList2.add(4);

mainList.add(subList1);
mainList.add(subList2);
```

---

## Tree questions

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
    Node prev = null;
    Node head = null;

    void inorder(Node root) {
        // Implementation
    }

    void problemStatementName(Node root) {
        inorder(root);
    }
}
```

Prefer implementing only the required solution + helpers in `Solution` — avoid a full Tree class with unused boilerplate.

---

## Sorting (interview — only these 4)

Always use `Integer.compare(x, y)` — never `x - y` (overflow).

### 1. Normal ascending

```java
Arrays.sort(a);              // int[]
Collections.sort(list);      // List<Integer>
// list.sort(null);          // same
```

### 2. Descending (boxed only)

```java
// int[] has NO reverse comparator — use Integer[] / List<Integer>
Arrays.sort(integerArr, Collections.reverseOrder());
Collections.sort(list, Collections.reverseOrder());
// OR
Arrays.sort(integerArr, (x, y) -> Integer.compare(y, x));
```

### 3. 2D / pairs — multi-key priority (col0, then col1, then col2)

```java
// n x 3 (or n x 2 — drop last compare)
Arrays.sort(arr, (a, b) -> {
    if (a[0] != b[0]) return Integer.compare(a[0], b[0]);
    if (a[1] != b[1]) return Integer.compare(a[1], b[1]);
    return Integer.compare(a[2], b[2]);
});

// length-2 pair example: sort by start, then end
Arrays.sort(intervals, (a, b) -> {
    if (a[0] != b[0]) return Integer.compare(a[0], b[0]);
    return Integer.compare(a[1], b[1]);
});
```

### 4. Custom key (distance, ratio, etc.)

```java
// sort points by distance from origin
Arrays.sort(points, (a, b) -> {
    int da = a[0] * a[0] + a[1] * a[1];
    int db = b[0] * b[0] + b[1] * b[1];
    return Integer.compare(da, db);
});
```

Same `(a, b) ->` style works for `List` via `list.sort((a, b) -> ...)`.
