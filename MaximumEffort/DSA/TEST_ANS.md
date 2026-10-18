# TEST_ANS — Answers

Format: **Pattern** · Approach · **T** · **S**

Check after each **set of 20** in [`TEST.md`](./TEST.md).

---

## Set 1 (Q1–Q20)

**Q1. Valid Palindrome**  
Two Pointers · L/R scan, skip non-alnum, compare · **T:** O(n) · **S:** O(1)

**Q2. Remove Duplicates (sorted)**  
Two Pointers · slow/fast write unique · **T:** O(n) · **S:** O(1)

**Q3. Move Zeroes**  
Two Pointers · write non-zeros forward, fill rest 0 · **T:** O(n) · **S:** O(1)

**Q4. Two Sum II (sorted)**  
Two Pointers · L/R move by sum vs target · **T:** O(n) · **S:** O(1)

**Q5. 3Sum**  
Two Pointers · sort + fix i + 2ptr · **T:** O(n²) · **S:** O(1)/O(log n)

**Q6. 4Sum**  
Two Pointers · sort + fix i,j + 2ptr · **T:** O(n³) · **S:** O(1)

**Q7. Container With Most Water**  
Two Pointers · L/R greedy, advance shorter · **T:** O(n) · **S:** O(1)

**Q8. Trapping Rain Water**  
Two Pointers · L/R max bounds, advance shorter · **T:** O(n) · **S:** O(1)

**Q9. Longest Palindromic Substring**  
Two Pointers / expand centers · odd+even expand, track best · **T:** O(n²) · **S:** O(1)

**Q10. Palindromic Substrings (count)**  
Two Pointers / expand centers · each expand step = 1 palindrome · **T:** O(n²) · **S:** O(1)

---

**Q11. Max Sum Subarray Size K**  
Sliding Window · fixed window sum · **T:** O(n) · **S:** O(K) or O(1)

**Q12. Longest Substring Without Repeating**  
Sliding Window · expand/shrink + set/map · **T:** O(n) · **S:** O(|Σ|)

**Q13. Longest Substring K Distinct**  
Sliding Window · variable window + freq map · **T:** O(n) · **S:** O(K)

**Q14. Minimum Window Substring**  
Sliding Window · need/have counts · **T:** O(|s|+|t|) · **S:** O(|Σ|)

**Q15. Find All Anagrams**  
Sliding Window · fixed window freq · **T:** O(n) · **S:** O(|Σ|)

**Q16. Permutation in String**  
Sliding Window · window freq match · **T:** O(n) · **S:** O(|Σ|)

**Q17. Subarray Sum Equals K**  
Prefix + HashMap · prefix count of `sum-k` · **T:** O(n) · **S:** O(n)

**Q18. Subarray Sum 0**  
Prefix + HashSet · seen prefix · **T:** O(n) · **S:** O(n)

**Q19. Contiguous Array (0/1)**  
Prefix + HashMap · treat 0 as −1, first index of balance · **T:** O(n) · **S:** O(n)

**Q20. Subarray Sums Divisible by K**  
Prefix + HashMap · rem `((sum%k)+k)%k` · **T:** O(n) · **S:** O(k)

---

---

## Set 2 (Q21–Q40)

**Q21. Range Sum Query (immutable)**  
Prefix Sum · `prefix[r+1]-prefix[l]` · **T:** O(1) query / O(n) build · **S:** O(n)

**Q22. Two Sum**  
Hashing · map value→index · **T:** O(n) · **S:** O(n)

**Q23. Valid Anagram**  
Hashing / count · freq[26] · **T:** O(n) · **S:** O(1)

**Q24. Group Anagrams**  
Hashing · sort key or count key · **T:** O(n·k log k)/O(n·k) · **S:** O(n·k)

**Q25. Longest Consecutive Sequence**  
Hashing · set, only start chains · **T:** O(n) · **S:** O(n)

**Q26. Top K Frequent (bucket)**  
Hashing / bucket · freq → buckets · **T:** O(n) · **S:** O(n)

**Q27. Merge Intervals**  
Sorting · sort start, merge overlap · **T:** O(n log n) · **S:** O(n)

**Q28. Insert Interval**  
Sorting / scan · merge new into list · **T:** O(n) · **S:** O(n)

**Q29. Meeting Rooms I**  
Sorting · sort starts/ends, check overlap · **T:** O(n log n) · **S:** O(1)

**Q30. Meeting Rooms II**  
Sorting + Heap/sweep · min rooms = max overlap · **T:** O(n log n) · **S:** O(n)

---

**Q31. Non-overlapping Intervals**  
Interval greedy · sort by end, count removals · **T:** O(n log n) · **S:** O(1)

**Q32. K Closest Points**  
Sorting / Heap · sort or max-heap k · **T:** O(n log k) · **S:** O(k)

**Q33. First/Last Position**  
Binary Search · bisect lo / hi · **T:** O(log n) · **S:** O(1)

**Q34. Search Rotated Sorted Array**  
Binary Search · mid decides sorted half · **T:** O(log n) · **S:** O(1)

**Q35. Find Min in Rotated**  
Binary Search · bisect toward unsorted · **T:** O(log n) · **S:** O(1)

**Q36. Koko Eating Bananas**  
Binary Search on answer · speed mid feasible? · **T:** O(n log M) · **S:** O(1)

**Q37. Min Days for Bouquets**  
Binary Search on answer · day mid feasible? · **T:** O(n log M) · **S:** O(1)

**Q38. Capacity To Ship Packages**  
Binary Search on answer · capacity mid feasible? · **T:** O(n log M) · **S:** O(1)

**Q39. Split Array Largest Sum**  
Binary Search on answer · max-sum mid, m splits · **T:** O(n log S) · **S:** O(1)

**Q40. Median of Two Sorted Arrays**  
Binary Search · partition both arrays · **T:** O(log(min(m,n))) · **S:** O(1)

---

---

## Set 3 (Q41–Q60)

**Q41. Valid Parentheses**  
Stack · push open, match close · **T:** O(n) · **S:** O(n)

**Q42. Decode String**  
Stack / recursion · nest counts + strings · **T:** O(n) · **S:** O(n)

**Q43. String Compression**  
Two Pointers · write run-length in place · **T:** O(n) · **S:** O(1)

**Q44. Compare Version Numbers**  
String parsing · split/scan parts · **T:** O(n) · **S:** O(1)

**Q45. Basic Calculator II**  
Stack · apply * / immediately · **T:** O(n) · **S:** O(n)

**Q46. Remove K Digits**  
Mono stack greedy · pop larger digits · **T:** O(n) · **S:** O(n)

**Q47. Partition Labels**  
Greedy · last index of char · **T:** O(n) · **S:** O(26)

**Q48. Reorganize String**  
Greedy / max-heap · no two same adjacent · **T:** O(n log 26) · **S:** O(26)

**Q49. Remove Duplicate Letters**  
Mono stack + last index · lex smallest unique · **T:** O(n) · **S:** O(26)

**Q50. Daily Temperatures**  
Monotonic stack · next greater · **T:** O(n) · **S:** O(n)

---

**Q51. Next Greater Element**  
Monotonic stack · next greater map · **T:** O(n) · **S:** O(n)

**Q52. Largest Rectangle in Histogram**  
Monotonic stack · nearest smaller L/R · **T:** O(n) · **S:** O(n)

**Q53. Stock Span**  
Monotonic stack · previous greater · **T:** O(n) · **S:** O(n)

**Q54. Rotate Image**  
Matrix · transpose + reverse rows · **T:** O(n²) · **S:** O(1)

**Q55. Set Matrix Zeroes**  
Matrix · marker first row/col · **T:** O(mn) · **S:** O(1)

**Q56. Spiral Matrix**  
Matrix · boundary walk · **T:** O(mn) · **S:** O(1)

**Q57. Search 2D Matrix**  
Binary Search · treat as 1D · **T:** O(log(mn)) · **S:** O(1)

**Q58. Search 2D Matrix II**  
Two Pointers / staircase · start top-right · **T:** O(m+n) · **S:** O(1)

**Q59. Number of Islands**  
BFS/DFS/DSU · flood components · **T:** O(mn) · **S:** O(mn)

**Q60. Rotting Oranges**  
Multi-source BFS · minutes = levels · **T:** O(mn) · **S:** O(mn)

---

---

## Set 4 (Q61–Q80)

**Q61. Shortest Path in Binary Matrix**  
Grid BFS · 8-dir unweighted · **T:** O(n²) · **S:** O(n²)

**Q62. Open the Lock**  
State BFS · 4 wheels, avoid deadends · **T:** O(10⁴·d) · **S:** O(10⁴)

**Q63. Word Ladder**  
BFS · change 1 char × 26 letters · **T:** O(N · L · 26) · **S:** O(N · L)

**Q64. Walls and Gates**  
Multi-source BFS · from all gates · **T:** O(mn) · **S:** O(mn)

**Q65. Clone Graph**  
BFS + map · old→new nodes · **T:** O(V+E) · **S:** O(V)

**Q66. Find if Path Exists**  
BFS/DFS/DSU · reachability · **T:** O(V+E) · **S:** O(V)

**Q67. Max Area of Island**  
DFS · size flood · **T:** O(mn) · **S:** O(mn)

**Q68. Surrounded Regions**  
DFS · border-safe mark, flip rest · **T:** O(mn) · **S:** O(mn)

**Q69. Pacific Atlantic Water Flow**  
Multi-source DFS/BFS · both oceans · **T:** O(mn) · **S:** O(mn)

**Q70. Number of Provinces**  
DSU (or DFS) · union count · **T:** O(n² α(n)) · **S:** O(n)

---

**Q71. Redundant Connection**  
DSU · edge that forms cycle · **T:** O(n α(n)) · **S:** O(n)

**Q72. Accounts Merge**  
DSU · union emails · **T:** O(N·K log N) · **S:** O(N·K)

**Q73. Number of Islands II**  
Online DSU · add land ops · **T:** O(k α(mn)) · **S:** O(mn)

**Q74. Course Schedule I**  
Topo / Kahn · cycle detect · **T:** O(V+E) · **S:** O(V+E)

**Q75. Course Schedule II**  
Topo · Kahn/DFS order · **T:** O(V+E) · **S:** O(V+E)

**Q76. Alien Dictionary**  
Topo · build graph + order · **T:** O(C) · **S:** O(C)

**Q77. Parallel Courses**  
Topo · longest path in DAG · **T:** O(V+E) · **S:** O(V+E)

**Q78. Minimum Height Trees**  
Topo peel · trim leaves · **T:** O(V+E) · **S:** O(V)

**Q79. Network Delay Time**  
Dijkstra · max dist from k · **T:** O((V+E) log V) · **S:** O(V+E)

**Q80. Cheapest Flights Within K Stops**  
Dijkstra / Bellman · ≤K stops · **T:** O(E·K) · **S:** O(V·K)

---

---

## Set 5 (Q81–Q100)

**Q81. Path with Maximum Probability**  
Dijkstra (max-heap) · maximize product · **T:** O((V+E) log V) · **S:** O(V+E)

**Q82. Path With Minimum Effort**  
Dijkstra / BS+BFS · minimax edge diff · **T:** O(mn log mn) · **S:** O(mn)

**Q83. Swim in Rising Water**  
Dijkstra · minimax elevation · **T:** O(n² log n) · **S:** O(n²)

**Q84. Max Depth**  
Tree DFS · 1+max(L,R) · **T:** O(n) · **S:** O(h)

**Q85. Balanced Binary Tree**  
Tree DFS · height or −1 · **T:** O(n) · **S:** O(h)

**Q86. Symmetric Tree**  
Tree DFS · mirror compare · **T:** O(n) · **S:** O(h)

**Q87. Same Tree**  
Tree DFS · both null or equal · **T:** O(n) · **S:** O(h)

**Q88. Subtree of Another Tree**  
Tree DFS · match DFS · **T:** O(n·m) · **S:** O(h)

**Q89. Path Sum / II**  
Tree DFS backtrack · root→leaf sum · **T:** O(n) · **S:** O(h)

**Q90. LCA (BT)**  
Tree DFS · both sides / found · **T:** O(n) · **S:** O(h)

---

**Q91. Serialize / Deserialize BT**  
BFS/DFS · encode nulls · **T:** O(n) · **S:** O(n)

**Q92. Level Order**  
Tree BFS · queue by level · **T:** O(n) · **S:** O(n)

**Q93. Right Side View**  
Tree BFS · last of each level · **T:** O(n) · **S:** O(n)

**Q94. Zigzag Level Order**  
Tree BFS · deque alternate · **T:** O(n) · **S:** O(n)

**Q95. Average of Levels**  
Tree BFS · sum/count · **T:** O(n) · **S:** O(n)

**Q96. Validate BST**  
BST · bounds or inorder · **T:** O(n) · **S:** O(h)

**Q97. LCA of BST**  
BST walk · compare vals · **T:** O(h) · **S:** O(1)

**Q98. Insert / Delete / Search BST**  
BST walk · standard BST ops · **T:** O(h) · **S:** O(1)

**Q99. Kth Smallest in BST**  
BST inorder · count to k · **T:** O(h+k) · **S:** O(h)

**Q100. Sorted Array → BST**  
BST · mid as root recurse · **T:** O(n) · **S:** O(log n)

---

---

## Set 6 (Q101–Q120)

**Q101. Min Absolute Diff in BST**  
BST inorder · track prev · **T:** O(n) · **S:** O(h)

**Q102. Kth Largest Element**  
Heap · min-heap size k · **T:** O(n log k) · **S:** O(k)

**Q103. Top K Frequent (heap)**  
Heap · freq + min-heap k · **T:** O(n log k) · **S:** O(n)

**Q104. K Closest Points (heap)**  
Heap · max-heap k · **T:** O(n log k) · **S:** O(k)

**Q105. Merge K Sorted Lists**  
Heap · min-heap of heads · **T:** O(N log k) · **S:** O(k)

**Q106. Median from Data Stream**  
Two heaps · max-left / min-right · **T:** O(log n) add · **S:** O(n)

**Q107. Sliding Window Median**  
Two heaps + lazy delete · **T:** O(n log n) · **S:** O(n)

**Q108. Task Scheduler**  
Heap / math · cooldown · **T:** O(n log k) · **S:** O(k)

**Q109. Connect Ropes Min Cost**  
Min-heap · always merge two smallest · **T:** O(n log n) · **S:** O(n)

**Q110. Implement Trie**  
Trie · children[26] + isEnd · **T:** O(L) · **S:** O(Σ)

---

**Q111. Word Search II**  
Trie + DFS · board prune · **T:** O(mn·4ᴸ) · **S:** O(words)

**Q112. Design Add/Search Words**  
Trie + DFS · `.` branches · **T:** O(26ᴸ) worst · **S:** O(Σ)

**Q113. Longest Word in Dictionary**  
Trie DFS · only walk isWord · **T:** O(N·L) · **S:** O(Σ)

**Q114. Replace Words**  
Trie · first isWord root · **T:** O(N·L) · **S:** O(Σ)

**Q115. Climbing Stairs**  
1D DP · fib roll · **T:** O(n) · **S:** O(1)

**Q116. House Robber**  
1D DP · take/skip · **T:** O(n) · **S:** O(1)

**Q117. House Robber II**  
1D DP · two ranges · **T:** O(n) · **S:** O(1)

**Q118. Min Cost Climbing Stairs**  
1D DP · min of two prev + cost · **T:** O(n) · **S:** O(1)

**Q119. Decode Ways**  
1D DP · 1/2 digit valid · **T:** O(n) · **S:** O(1)

**Q120. Maximum Subarray**  
Kadane · extend or restart · **T:** O(n) · **S:** O(1)

---

---

## Set 7 (Q121–Q140)

**Q121. Jump Game**  
Greedy / DP · max reach · **T:** O(n) · **S:** O(1)

**Q122. Unique Paths**  
Grid DP · 1D roll · **T:** O(mn) · **S:** O(n)

**Q123. Unique Paths II**  
Grid DP · obstacle → 0 · **T:** O(mn) · **S:** O(n)

**Q124. Minimum Path Sum**  
Grid DP · min(up,left)+cell · **T:** O(mn) · **S:** O(n)

**Q125. Triangle Min Path**  
Grid DP · bottom-up · **T:** O(n²) · **S:** O(n)

**Q126. Subset Sum**  
0/1 Knapsack · bool DP backward · **T:** O(nS) · **S:** O(S)

**Q127. Partition Equal Subset Sum**  
0/1 Knapsack · subset to sum/2 · **T:** O(nS) · **S:** O(S)

**Q128. Target Sum**  
0/1 Knapsack · ways to (total+target)/2 · **T:** O(nS) · **S:** O(S)

**Q129. Last Stone Weight II**  
Partition knapsack · min leftover · **T:** O(nS) · **S:** O(S)

**Q130. Coin Change (min)**  
Unbounded knapsack · min coins · **T:** O(n·amount) · **S:** O(amount)

---

**Q131. Coin Change II**  
Unbounded knapsack · ways, outer coins · **T:** O(n·amount) · **S:** O(amount)

**Q132. Integer Break**  
DP · max product splits · **T:** O(n²) · **S:** O(n)

**Q133. Longest Common Subsequence**  
String DP · 2D / 1D roll · **T:** O(mn) · **S:** O(min(m,n))

**Q134. Edit Distance**  
String DP · insert/delete/replace · **T:** O(mn) · **S:** O(n)

**Q135. Longest Palindromic Subsequence**  
String DP · LCS(s, reverse(s)) · **T:** O(n²) · **S:** O(n)

**Q136. Word Break**  
String DP · prefix segmentable · **T:** O(n²·k) · **S:** O(n)

**Q137. LIS (DP)**  
Subsequence DP · dp[i] ending at i · **T:** O(n²) · **S:** O(n)

**Q138. LIS (patience)**  
Patience + BS · tails array · **T:** O(n log n) · **S:** O(n)

**Q139. Russian Doll Envelopes**  
Sort + LIS · w↑ h↓ then LIS on h · **T:** O(n log n) · **S:** O(n)

**Q140. House Robber III**  
Tree DP · (rob, skip) pair · **T:** O(n) · **S:** O(h)

---

---

## Set 8 (Q141–Q160)

**Q141. Binary Tree Max Path Sum**  
Tree DP · gain + global path · **T:** O(n) · **S:** O(h)

**Q142. Diameter of Binary Tree**  
Tree DP · L+R heights · **T:** O(n) · **S:** O(h)

**Q143. Gas Station**  
Array greedy · net tank, restart · **T:** O(n) · **S:** O(1)

**Q144. Candy**  
Array greedy · two-pass slope · **T:** O(n) · **S:** O(n)

**Q145. Assign Cookies**  
Greedy 2ptr · sort both · **T:** O(n log n) · **S:** O(1)

**Q146. Boats to Save People**  
Greedy 2ptr · heavy+light · **T:** O(n log n) · **S:** O(1)

**Q147. Majority Element**  
Boyer-Moore · cancel pairs · **T:** O(n) · **S:** O(1)

**Q148. Jump Game II**  
Greedy · window jumps · **T:** O(n) · **S:** O(1)

**Q149. Linked List Cycle**  
Floyd · slow/fast meet · **T:** O(n) · **S:** O(1)

**Q150. Linked List Cycle II**  
Floyd · reset to head → entry · **T:** O(n) · **S:** O(1)

---

**Q151. Middle of Linked List**  
Fast & Slow · slow at mid · **T:** O(n) · **S:** O(1)

**Q152. Happy Number**  
Floyd / set · digit-square cycle · **T:** O(log n) · **S:** O(1)/O(log n)

**Q153. Remove Nth From End**  
Two pointers · gap of n · **T:** O(n) · **S:** O(1)

**Q154. Reverse Linked List**  
3ptr · prev/curr/next · **T:** O(n) · **S:** O(1)

**Q155. Reverse Linked List II**  
Segment reverse · front-insert · **T:** O(n) · **S:** O(1)

**Q156. Reverse Nodes in K-Group**  
Block reverse · full groups only · **T:** O(n) · **S:** O(1)

**Q157. Merge Two Sorted Lists**  
Merge · dummy + pick smaller · **T:** O(n+m) · **S:** O(1)

**Q158. Sort List**  
Bottom-up merge sort · **T:** O(n log n) · **S:** O(1)

**Q159. Reorder List**  
Mid + reverse + merge · **T:** O(n) · **S:** O(1)

**Q160. Rotate List**  
Length + circle + break · **T:** O(n) · **S:** O(1)

---

---

## Set 9 (Q161–Q180)

**Q161. Odd Even Linked List**  
Relink · odds then evens · **T:** O(n) · **S:** O(1)

**Q162. Copy List with Random Pointer**  
Hash map / weave · old→new · **T:** O(n) · **S:** O(n)/O(1)

**Q163. Intersection of Two Lists**  
Two pointers · switch heads · **T:** O(n+m) · **S:** O(1)

**Q164. Add Two Numbers**  
Simulation · digit + carry · **T:** O(max(n,m)) · **S:** O(1)

**Q165. Subsets**  
Backtracking · include/exclude · **T:** O(n·2ⁿ) · **S:** O(n)

**Q166. Subsets II**  
Backtracking · sort, skip dups · **T:** O(n·2ⁿ) · **S:** O(n)

**Q167. Combinations**  
Backtracking · choose k · **T:** O(C(n,k)·k) · **S:** O(k)

**Q168. Combination Sum**  
Backtracking · unbounded pick · **T:** O(2ᵗ) · **S:** O(t)

**Q169. Combination Sum II**  
Backtracking · sort, use once, skip dups · **T:** O(2ⁿ) · **S:** O(t)

**Q170. Permutations**  
Backtracking · swap/used · **T:** O(n·n!) · **S:** O(n)

---

**Q171. Permutations II**  
Backtracking · sort + skip same-level dups · **T:** O(n·n!) · **S:** O(n)

**Q172. Palindrome Partitioning**  
Backtracking · cut if palindrome · **T:** O(n·2ⁿ) · **S:** O(n)

**Q173. Restore IP Addresses**  
Backtracking · 4 parts, 1–3 digits · **T:** O(1) · **S:** O(1)

**Q174. Word Search**  
Backtracking DFS · board mark · **T:** O(mn·4ᴸ) · **S:** O(L)

**Q175. N-Queens**  
Constraint BT · col/diag sets · **T:** O(n!) · **S:** O(n)

**Q176. Sudoku Solver**  
Constraint BT · row/col/box · **T:** O(9^{empty}) · **S:** O(1)

**Q177. Generate Parentheses**  
Balance BT · open/close counts · **T:** O(4ⁿ/√n) · **S:** O(n)

**Q178. Single Number**  
XOR · XOR all · **T:** O(n) · **S:** O(1)

**Q179. Single Number II**  
Bits · count mod 3 · **T:** O(n) · **S:** O(1)

**Q180. Single Number III**  
XOR groups · split by diff bit · **T:** O(n) · **S:** O(1)

---

---

## Set 10 (Q181–Q197)

**Q181. Missing Number**  
XOR · 0..n xor nums · **T:** O(n) · **S:** O(1)

**Q182. Find the Duplicate**  
Floyd / bit · cycle in array · **T:** O(n) · **S:** O(1)

**Q183. Number of 1 Bits**  
Bit · count / Brian Kernighan · **T:** O(1) · **S:** O(1)

**Q184. Counting Bits**  
DP bits · `ans[i>>1]+(i&1)` · **T:** O(n) · **S:** O(n)

**Q185. Reverse Bits**  
Bit · shift build 32 · **T:** O(1) · **S:** O(1)

**Q186. Sum of Two Integers**  
Bit · XOR + carry loop · **T:** O(1) · **S:** O(1)

**Q187. Power of Two**  
Bit · `n>0 && (n&(n-1))==0` · **T:** O(1) · **S:** O(1)

**Q188. Min Cost to Connect All Points**  
Prim (dense) · Manhattan MST · **T:** O(n²) · **S:** O(n)

**Q189. Connecting Cities Min Cost**  
Prim dense / Kruskal sparse · **T:** O(n²)/O(E log E) · **S:** O(n)

**Q190. Critical Connections**  
Tarjan bridges · `low[v]>disc[u]` · **T:** O(V+E) · **S:** O(V+E)

---

**Q191. Range Sum Query Mutable**  
Segment Tree / BIT · update+range sum · **T:** O(log n) · **S:** O(n)

**Q192. Count of Smaller After Self**  
Fenwick / merge · right→left insert · **T:** O(n log n) · **S:** O(n)

**Q193. Burst Balloons**  
Interval DP · last burst k · **T:** O(n³) · **S:** O(n²)

**Q194. Cherry Pickup II**  
3D DP · two robots down · **T:** O(r·c²) · **S:** O(c²) rolled

**Q195. Count of Integers (LC 2719)**  
Digit DP · count ≤bound, digit-sum range · **T:** O(L·maxSum·10) · **S:** O(L·maxSum)

**Q196. Numbers At Most N Given Digit Set**  
Digit DP · restricted digits + tight · **T:** O(L·|D|) · **S:** O(L)

**Q197. Connected Components**  
DSU · union edges, count roots · **T:** O(E α(V)) · **S:** O(V)
