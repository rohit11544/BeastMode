# DSA_REVISE — Daily Google L4 Sheet

**Morning (~20 min):** 5 min [`../JAVA_SYNTAX_REVISE.md`](../JAVA_SYNTAX_REVISE.md) → 10–15 min this file (+ code in [`DSA_revise_code.md`](./DSA_revise_code.md)).

Source: your [`DSA_topics.md`](./DSA_topics.md) (untouched).  
Format: **Problem — approach — Time — Space**

**How to use:** walk P0 → P1 each day; re-solve any you hesitate on. Add new problems at the bottom of the matching pattern.

---

## Priority legend (topics)

| Rank | Topic | Why |
|------|--------|-----|
| **P0** | Arrays & Strings | Highest Google frequency |
| **P0** | Graphs (BFS/DFS/DSU/Topo) | Extremely common |
| **P1** | Trees / BST / Heap / Trie | Very common |
| **P1** | Binary Search | Often nested inside array/graph |
| **P1** | Core DP | Mediums expected |
| **P2** | Greedy + Heap patterns | Frequent |
| **P2** | Linked List | Less often, but must be sharp |
| **P2** | Backtracking | Mediums + pruning talk |
| **P3** | Bit Manipulation | Occasional |
| **P3** | Advanced (SegTree, MST, Flow, Digit DP) | Rare at L4 — skim only |

Within each topic, patterns are also ordered high → low.

---

# P0 — Arrays & Strings

## 1. Two Pointers
- Valid Palindrome — L/R scan — O(n) — O(1)
- Remove Duplicates (sorted) — slow/fast write — O(n) — O(1)
- Move Zeroes — swap/write ptr — O(n) — O(1)
- Container With Most Water — L/R greedy — O(n) — O(1)
- 3Sum — sort + 2ptr — O(n²) — O(1)/O(log n)
- 4Sum — sort + 2ptr — O(n³) — O(1)
- Two Sum II (sorted) — L/R — O(n) — O(1)
- Trapping Rain Water (2ptr) — L/R max — O(n) — O(1)
- Longest Palindromic Substring — expand centers — O(n²) — O(1)
- Palindromic Substrings count — expand centers — O(n²) — O(1)

## 2. Sliding Window
- Max Sum Subarray Size K — fixed window — O(n) — O(K) window/map
- Longest Substring Without Repeating — expand/shrink + set — O(n) — O(|Σ|)
- Longest Substring with K Distinct — variable window — O(n) — O(K) distinct
- Minimum Window Substring — need/have counts — O(|s|+|t|) — O(|Σ|)
- Find All Anagrams — fixed window freq — O(n) — O(|Σ|)
- Permutation in String — window freq — O(n) — O(|Σ|)

> **K / |Σ|:** `K` = window size or allowed distinct count (problem param). `|Σ|` = alphabet / distinct chars in maps (e.g. ≤128 ASCII). Not a mystery constant.
## 3. Prefix Sum + HashMap
- Subarray Sum Equals K — prefix + map — O(n) — O(n)
- Subarray Sum 0 — prefix + set — O(n) — O(n)
- Contiguous Array (0/1) — prefix balance — O(n) — O(n)
- Subarray Sums Divisible by K — mod map (normalize remainder: `((sum % k) + k) % k`) — O(n) — O(k)
- Range Sum Query (immutable) — prefix — O(1) query — O(n)

## 4. Hashing / Frequency
- Two Sum — hashmap — O(n) — O(n)
- Valid Anagram — count — O(n) — O(1)
- Group Anagrams — sort key / count key — O(n·k log k) / O(n·k) — O(n·k)
- Longest Consecutive Sequence — set scan — O(n) — O(n)
- Top K Frequent (bucket) — freq buckets — O(n) — O(n)

## 5. Sorting + Logic
- Merge Intervals — sort + merge — O(n log n) — O(n)
- Insert Interval — scan/merge — O(n) — O(n)
- Meeting Rooms I — sort ends — O(n log n) — O(1)
- Meeting Rooms II — sort + heap/sweep — O(n log n) — O(n)
- Non-overlapping Intervals — sort end greedy — O(n log n) — O(1)
- K Closest Points — sort/heap — O(n log k) — O(k)

## 6. Binary Search (on array / on answer)
- First/Last Position — bisect lo/hi — O(log n) — O(1)
- Search Rotated Sorted Array — mid pivot — O(log n) — O(1)
- Find Min in Rotated — bisect — O(log n) — O(1)
- Koko Eating Bananas — BS on speed — O(n log M) — O(1)
- Min Days for Bouquets — BS on day — O(n log M) — O(1)
- Capacity To Ship Packages — BS on capacity — O(n log M) — O(1)
- Split Array Largest Sum — BS on answer — O(n log S) — O(1)
- Median of Two Sorted Arrays — BS partition — O(log(min(m,n))) — O(1)

## 7. String Parsing / Simulation
- Valid Parentheses — stack — O(n) — O(n)
- Decode String — stack / recursion — O(n) — O(n)
- String Compression — 2ptr write — O(n) — O(1)
- Compare Version Numbers — split/scan — O(n) — O(1)
- Basic Calculator II — stack ops — O(n) — O(n)

## 8. Greedy on Strings
- Remove K Digits — mono stack — O(n) — O(n)
- Partition Labels — last index greedy — O(n) — O(26)
- Reorganize String — max-heap / count — O(n log 26) — O(26)
- Remove Duplicate Letters — mono stack + last — O(n) — O(26)

## 9. Monotonic Stack
- Daily Temperatures — next greater — O(n) — O(n)
- Next Greater Element — mono stack — O(n) — O(n)
- Largest Rectangle in Histogram — mono stack — O(n) — O(n)
- Stock Span — mono stack — O(n) — O(n)

> **Trapping Rain Water:** use **Two Pointers** (O(1) space) — see §1. Stack version exists but skip for revise.
## 10. Matrix / 2D
- Rotate Image — transpose + reverse — O(n²) — O(1)
- Set Matrix Zeroes — marker row/col — O(mn) — O(1)
- Spiral Matrix — boundary walk — O(mn) — O(1)
- Search 2D Matrix — BS treat as 1D — O(log(mn)) — O(1)
- Search 2D Matrix II — staircase — O(m+n) — O(1)
- Number of Islands — see Graphs —

---

# P0 — Graphs

## 1. BFS (unweighted shortest / levels)
- Number of Islands — BFS/DFS flood — O(mn) — O(mn)
- Rotting Oranges — multi-source BFS — O(mn) — O(mn)
- Shortest Path in Binary Matrix — grid BFS — O(n²) — O(n²)
- Open the Lock — state BFS — O(10⁴·d) — O(10⁴)
- Word Ladder — implicit BFS — O(N·L²) — O(N·L)
- Walls and Gates — multi-source BFS — O(mn) — O(mn)
- Clone Graph — BFS + map — O(V+E) — O(V)
- Find if Path Exists — BFS/DFS — O(V+E) — O(V)

## 2. DFS (components / paths / cycles)
- Max Area of Island — DFS size — O(mn) — O(mn)
- Surrounded Regions — border DFS — O(mn) — O(mn)
- Pacific Atlantic Water Flow — multi-source DFS — O(mn) — O(mn)

## 3. Union Find (DSU)
- Number of Provinces — union count — O(n² α(n)) — O(n)
- Connected Components — union edges — O(E α(V)) — O(V)
- Redundant Connection — cycle detect — O(n α(n)) — O(n)
- Accounts Merge — union emails — O(N·K log N) — O(N·K)
- Number of Islands II — online DSU — O(k α(mn)) — O(mn)

> **Longest Consecutive Sequence (128):** HashSet, not DSU — see Hashing / Frequency.

## 4. Topological Sort
- Course Schedule I — cycle / Kahn (or DFS cycle) — O(V+E) — O(V+E)
- Course Schedule II — order Kahn/DFS — O(V+E) — O(V+E)
- Alien Dictionary — build + topo — O(C) — O(1)/O(C)
- Parallel Courses — longest path topo — O(V+E) — O(V+E)
- Minimum Height Trees — peel leaves — O(V+E) — O(V)

## 5. Dijkstra / weighted shortest (P1–P2)
- Network Delay Time — Dijkstra — O((V+E) log V) — O(V+E)
- Cheapest Flights Within K Stops — Dijkstra/Bellman — O(E·K) — O(V·K)
- Path with Maximum Probability — max-heap Dijkstra — O((V+E) log V) — O(V+E)
- Path With Minimum Effort — Dijkstra / BS+BFS — O(mn log mn) — O(mn) — *minimax: minimize the maximum edge height difference*
- Swim in Rising Water — Dijkstra — O(n² log n) — O(n²) — *minimax: minimize the maximum elevation on the path*

---

# P1 — Trees / BST / Heap / Trie

## 1. Binary Tree DFS / recursion
- Max Depth — DFS — O(n) — O(h)
- Balanced Binary Tree — DFS height — O(n) — O(h)
- Symmetric Tree — mirror DFS — O(n) — O(h)
- Subtree of Another Tree — match DFS — O(n·m) — O(h)
- Same Tree — DFS — O(n) — O(h)
- Path Sum / II — DFS backtrack — O(n) — O(h)
- Lowest Common Ancestor (BT) — DFS — O(n) — O(h)
- Serialize / Deserialize BT — BFS/DFS — O(n) — O(n)

## 2. Binary Tree BFS
- Level Order Traversal — queue — O(n) — O(n)
- Binary Tree Right Side View — level BFS — O(n) — O(n)
- Zigzag Level Order — deque — O(n) — O(n)
- Average of Levels — BFS — O(n) — O(n)

## 3. BST
- Validate BST — bounds / inorder — O(n) — O(h)
- LCA of BST — walk compare — O(h) — O(1)
- Insert / Delete / Search BST — walk — O(h) — O(1)
- Kth Smallest in BST — inorder — O(h+k) — O(h) — *follow-up: subtree sizes → O(h) walk*
- Convert Sorted Array → BST — mid root — O(n) — O(log n)
- Min Absolute Diff in BST — inorder — O(n) — O(h)

## 4. Heap / Priority Queue
- Kth Largest Element — min-heap k — O(n log k) — O(k)
- Top K Frequent Elements — heap/bucket — O(n log k) / O(n) — O(n)
- K Closest Points — max-heap k — O(n log k) — O(k)
- Merge K Sorted Lists — min-heap — O(N log k) — O(k)
- Find Median from Data Stream — two heaps — O(log n)/query — O(n)
- Sliding Window Median — two heaps + lazy — O(n log n) — O(n)
- Task Scheduler — max-heap + cool — O(n log k) — O(k)
- Connect Ropes Min Cost — min-heap — O(n log n) — O(n)

## 5. Trie
- Implement Trie — children map — O(L) — O(Σ)
- Word Search II — Trie + DFS — O(mn·4ᴸ) — O(words)
- Design Add/Search Words — Trie + DFS `.` — O(26ᴸ) — O(Σ)
- Longest Word / Replace Words — Trie — O(N·L) — O(Σ)

---

# P1 — Dynamic Programming (core)

## 1. 1D DP
- Climbing Stairs — fib DP — O(n) — O(1)
- House Robber — take/skip — O(n) — O(1)
- House Robber II — two ranges — O(n) — O(1)
- Min Cost Climbing Stairs — 1D — O(n) — O(1)
- Decode Ways — digit DP-lite — O(n) — O(1)
- Maximum Subarray (Kadane) — running max — O(n) — O(1)
- Jump Game (DP/greedy) — reach — O(n) — O(1)

## 2. Grid DP
- Unique Paths — dp[i][j] — O(mn) — O(n)
- Unique Paths II — obstacles — O(mn) — O(n)
- Minimum Path Sum — grid DP — O(mn) — O(n)
- Triangle Min Path — bottom-up — O(n²) — O(n)

## 3. Knapsack family
- Subset Sum — 0/1 knapsack — O(nS) — O(S)
- Partition Equal Subset Sum — subset — O(nS) — O(S)
- Target Sum — ± knapsack — O(nS) — O(S)
- Last Stone Weight II — partition — O(nS) — O(S)
- Coin Change (min) — unbounded — O(n·amount) — O(amount)
- Coin Change II (ways) — unbounded — O(n·amount) — O(amount)
- Integer Break — unbounded-ish — O(n²) — O(n)

## 4. String DP
- Longest Common Subsequence — 2D DP — O(mn) — O(mn)/O(min)
- Edit Distance — 2D DP — O(mn) — O(mn)
- Longest Palindromic Subsequence — LCS trick — O(n²) — O(n²)
- Word Break — DP / memo — O(n²·k) — O(n)

## 5. Subsequence DP
- LIS (DP) — O(n²) — O(n)
- LIS (patience / BS) — O(n log n) — O(n)
- Russian Doll Envelopes — sort + LIS — O(n log n) — O(n)

## 6. Tree DP (high value)
- House Robber III — rob/skip node — O(n) — O(h)
- Binary Tree Max Path Sum — tree DP — O(n) — O(h)
- Diameter of Binary Tree — tree DP — O(n) — O(h)

---

# P2 — Greedy (+ overlap with Heap)

## 1. Interval greedy
- Non-overlapping Intervals — **known** (Arrays → Sorting) — O(n log n) — O(1)
- Meeting Rooms II — **known** (Arrays → Sorting) — O(n log n) — O(n)
- Minimum Platforms — **known** (sort+2ptr; ≈ Meeting Rooms II) — O(n log n) — O(1)
- Insert / Merge Intervals — **known** (Arrays → Sorting) —

## 2. Array greedy
- Jump Game I — **known** (DP → 1D) — O(n) — O(1)
- Jump Game II — **known** — O(n) — O(1)
- Gas Station — net tank — O(n) — O(1)
- Candy — two-pass slope — O(n) — O(n)
- Assign Cookies — sort+2ptr — O(n log n) — O(1)
- Boats to Save People — sort+2ptr — O(n log n) — O(1)

## 3. Freq / scheduling greedy
- Majority Element — Boyer-Moore — O(n) — O(1)
- Task Scheduler — **known** (Heap) — O(n) — O(1)/O(k)
- Reorganize String — **known** (Greedy on Strings) — O(n log 26) — O(26)

---

# P2 — Linked List

## 1. Fast & Slow
- Linked List Cycle — **known** — O(n) — O(1)
- Linked List Cycle II — Floyd entry — O(n) — O(1)
- Middle of Linked List — **known** — O(n) — O(1)
- Happy Number — Floyd / set — O(log n) — O(1)/O(log n)
- Remove Nth From End — gap pointers — O(n) — O(1)

## 2. Reversal
- Reverse Linked List — **known** — O(n) — O(1)
- Reverse Linked List II — segment reverse — O(n) — O(1)
- Reverse Nodes in K-Group — block reverse — O(n) — O(1)

## 3. Merge
- Merge Two Sorted Lists — dummy — O(n+m) — O(1)
- Merge K Sorted Lists — **known** (Heap) — O(N log k) — O(k)
- Sort List — bottom-up merge sort — O(n log n) — O(1)

## 4. Structure / copy
- Reorder List — mid + reverse + merge — O(n) — O(1)
- Rotate List — length + join — O(n) — O(1)
- Odd Even Linked List — relink — O(n) — O(1)
- Copy List with Random Pointer — map / weave — O(n) — O(n)/O(1)
- Intersection of Two Lists — length align — O(n) — O(1)
- Add Two Numbers — digit carry — O(max(n,m)) — O(1)

---

# P2 — Backtracking

## 1. Subsets / Combos / Perms
- Subsets — **known** — O(n·2ⁿ) — O(n)
- Subsets II — skip dups — O(n·2ⁿ) — O(n)
- Combinations — choose k — O(C(n,k)·k) — O(k)
- Combination Sum — **known** — O(2ᵗ) — O(t)
- Combination Sum II — sorted skip — O(2ⁿ) — O(t)
- Permutations — **known** — O(n·n!) — O(n)
- Permutations II — skip dups — O(n·n!) — O(n)

## 2. Partition / grid
- Palindrome Partitioning — cut + check — O(n·2ⁿ) — O(n)
- Restore IP Addresses — 4 cuts — O(1) — O(1)
- Word Search — DFS board — O(mn·4ᴸ) — O(L)

## 3. Constraint problems
- N-Queens — place + prune — O(n!) — O(n)
- Sudoku Solver — cell DFS — O(9^{empty}) — O(1)
- Generate Parentheses — balance BT — O(4ⁿ/√n) — O(n)

---

# P3 — Bit Manipulation (occasional)

## XOR (most important)
- Single Number — **known** — O(n) — O(1)
- Single Number II/III — bits/XOR groups — O(n) — O(1)
- Missing Number — XOR / sum — O(n) — O(1)
- Find the Duplicate (bit/Floyd) — — O(n) — O(1)

## Mask / tricks
- Number of 1 Bits — Brian Kernighan — O(1) — O(1)
- Counting Bits — DP bits — O(n) — O(n)
- Reverse Bits — shift build — O(1) — O(1)
- Sum of Two Integers — XOR+AND carry — O(1) — O(1)
- Power of Two — `n & (n-1)` — O(1) — O(1)

---

# P3 — Advanced (L4 rare — don’t daily grind)

## MST / advanced graph
> Explicit edge list → Kruskal · Points / complete graph → Prim
- Min Cost to Connect All Points — array Prim (complete/dense; heap worse O(n² log n)) — O(n²) — O(n)
- Connecting Cities Min Cost — Prim dense / Kruskal sparse — O(n²) / O(E log E) — O(n) / O(V)
- Critical Connections (bridges) — Tarjan — O(V+E) — O(V+E)

## Segment / Fenwick (know idea)
- Range Sum Query Mutable — BIT/SegTree — O(log n) — O(n)
- Count of Smaller After Self — BIT/merge — O(n log n) — O(n)

## Hard DP (optional)
- Burst Balloons — interval DP — O(n³) — O(n²)
- Cherry Pickup II — 3D DP / bottom-up roll — O(r·c²) — O(r·c²)/O(c²)
- Count of Integers (LC 2719) — digit DP sum in range — problem-dependent
- Numbers At Most N Given Digit Set (LC 902) — digit DP restricted digits — O(log N · |D|) — O(log N)

---

# Daily checklist (15–40 min)

0. **5 min** — skim [`../JAVA_SYNTAX_REVISE.md`](../JAVA_SYNTAX_REVISE.md)  
1. Pick **1 P0 pattern** → re-state approach + complexities out loud  
2. Blind-code **1–2 problems** from that pattern (timer on)  
3. Add any **new** problem you practice under the matching section above  
4. Once/week: 1 full timed mock (2 problems, 45–60 min)

---

# New problems log

| Date | Problem | Pattern | Approach | T | S | Notes |
|------|---------|---------|----------|---|---|-------|
| | | | | | | |
