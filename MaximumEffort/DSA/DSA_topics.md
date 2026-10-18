# **🔥** Graph 

Graph representation 

- 2 D matrix  
- Adi list   
- Edge List

BFS & DFS

- For all representations 

Dijkstra algo 

- Priority queue  
- Set 

Prims

- Queue  
- Min heap 

Kruskal 

- Both methods as prims

Bellmanford 

- Edglist   
- Adj list

Floyd warshall algo 

- Adj matrix

Topological sort

- Dfs  
- Visited \+ stack 

Floyd Falkerson 

- Normal   
- Edmonds karp algo 

Strongly connected components 

- Dfs

## **1\. Graph Representation**

**Concept:** Know how to build graphs from input

**Problems:**

* [133\. Clone Graph](https://leetcode.com/problems/clone-graph/) \- Build adj list [Clone Graph - Depth First Search - Leetcode 133](https://www.youtube.com/watch?v=mQeF6bN8hMk&t=248s)  
* [1971\. Find if Path Exists in Graph](https://leetcode.com/problems/find-if-path-exists-in-graph/) \- Basic representation [Find if Path Exists in Graph - Leetcode 1971 - Graphs (Python)](https://www.youtube.com/watch?v=knLFe7hEp3Y)

---

## **2\. BFS (Breadth-First Search) ⭐⭐⭐**

**Pattern:** Level-order traversal, shortest path in unweighted graphs [G-5. Breadth-First Search (BFS) | C++ and Java | Traversal Technique in Graphs](https://www.youtube.com/watch?v=-tgVpUgsQ5k)

**Must Solve:**

* [200\. Number of Islands](https://leetcode.com/problems/number-of-islands/) \- Classic BFS/DFS [G-8. Number of Islands | Number of Connected Components in Matrix | C++ | Java](https://www.youtube.com/watch?v=muncqlKJrH0)  
* [994\. Rotting Oranges](https://leetcode.com/problems/rotting-oranges/) \- Multi-source BFS [G-10. Rotten Oranges | C++ | Java](https://www.youtube.com/watch?v=yf3oUhkvqA0&t=40s)  
* [752\. Open the Lock](https://leetcode.com/problems/open-the-lock/) \- BFS with state space [Open the Lock - Leetcode 752 - Python](https://www.youtube.com/watch?v=Pzg3bCDY87w)  
* [1091\. Shortest Path in Binary Matrix](https://leetcode.com/problems/shortest-path-in-binary-matrix/) \- Grid BFS [Shortest Path in a Binary Matrix - Leetcode 1091 - Python](https://www.youtube.com/watch?v=YnxUdAO7TAo)

**Important:**

* [127\. Word Ladder](https://leetcode.com/problems/word-ladder/) \- BFS on implicit graph   
  * [G-29. Word Ladder - I | Shortest Paths](https://www.youtube.com/watch?v=tRPda0rcf8E)\[perfect clinical sol\]  
  * [Word Ladder - Breadth First Search - Leetcode 127 - Python](https://www.youtube.com/watch?v=h9iTnkgv05E)\[smart but kinda off for coding\]  
* [286\. Walls and Gates](https://leetcode.com/problems/walls-and-gates/) \- Multi-source BFS (Premium) [Walls and Gates - Multi-Source BFS - Leetcode 286 - Python](https://www.youtube.com/watch?v=e69C6xhiSQE)

---

## **3\. DFS (Depth-First Search) ⭐⭐⭐**

**Pattern:** Explore all paths, backtracking, cycle detection [G-6. Depth-First Search (DFS) | C++ and Java | Traversal Technique in Graphs](https://www.youtube.com/watch?v=Qzf1a--rhp8&t=9s)

**Must Solve:**

* [200\. Number of Islands](https://leetcode.com/problems/number-of-islands/) \- DFS variant [NUMBER OF ISLANDS - Leetcode 200 - Python](https://www.youtube.com/watch?v=pV2kpPD66nE)  
* [695\. Max Area of Island](https://leetcode.com/problems/max-area-of-island/) \- Connected components [Max Area of Island - Leetcode 695 - Python](https://www.youtube.com/watch?v=iJGr1OtmH0c)  
* [130\. Surrounded Regions](https://leetcode.com/problems/surrounded-regions/) \- Boundary DFS [Surrounded Regions - Graph - Leetcode 130 - Python](https://www.youtube.com/watch?v=9z2BunfoZ5Y&t=1s)  
* [417\. Pacific Atlantic Water Flow](https://leetcode.com/problems/pacific-atlantic-water-flow/) \- Multi-source DFS [Pacific Atlantic Water Flow - Leetcode 417 - Python](https://www.youtube.com/watch?v=s-VkcjHqkGI)

**Important:**

* [207\. Course Schedule](https://leetcode.com/problems/course-schedule/) \- Cycle detection [Course Schedule - Graph Adjacency List - Leetcode 207](https://www.youtube.com/watch?v=EgI5nU9etnU)  
* [547\. Number of Provinces](https://leetcode.com/problems/number-of-provinces/) \- Connected components [G-7. Number of Provinces | C++ | Java | Connected Components](https://www.youtube.com/watch?v=ACzkVtewUYA)

---

## **4\. Union Find (DSU) ⭐⭐⭐ CRITICAL** 

* **Path compression** flattens paths during `find()` incrementally — you can't pre-compress, and it makes rank approximate but rank is only used on roots which are never re-parented so correctness holds.  
* **New node insertion** just initializes `parent[node]=node, rank[node]=0` — rank never increments on insertion, only during union on a tie.  
* **Rank increments only on tie** during union. Size always updates to true node count regardless of tie.  
* **Path compression doesn't destroy component membership** — `find(A)==find(B)` still works. It only destroys internal tree structure which Union-Find never needed anyway.  
* **Rank vs Size** — same time complexity, but size is more useful when the problem needs component size info (largest component, merge smaller into larger). Rank is fine for pure connectivity checks.  
* [G-46. Disjoint Set | Union by Rank | Union by Size | Path Compression](https://www.youtube.com/watch?v=aBxjDBC4M1U&t=2141s)

**Pattern:** Dynamic connectivity, cycle detection, grouping

**Must Solve:**

* [200\. Number of Islands](https://leetcode.com/problems/number-of-islands/) \- Can solve with Union Find  
  * **Initialize** — treat every land cell (`1`) as its own island. `islands = total count of 1s`. Each cell maps to a node ID `(row * cols + col)`.  
  * **Union neighbors** — for every land cell, check right and down neighbors. If neighbor is also land and in a **different component** → `union()` them and `islands--`. Final `islands` \= answer.  
* [547\. Number of Provinces](https://leetcode.com/problems/number-of-provinces/) \- Classic Union Find  
  * Same as Number of Islands but simpler — grid is already given as an adjacency matrix.  
  * **Initialize** — every node (city) is its own province. `provinces = n`.  
  * **Union** — iterate the adjacency matrix, for every `isConnected[i][j] == 1` where `i != j`, if i and j are in **different components** → `union()` them and `provinces--`. Final `provinces` \= answer.  
* [323\. Number of Connected Components in an Undirected Graph](https://leetcode.com/problems/number-of-connected-components-in-an-undirected-graph/) \- Premium [Number of Connected Components in an Undirected Graph - Union Find - Leetcode 323 - Python](https://www.youtube.com/watch?v=8f1XPm4WOUc)  
  * **DFS/BFS:** O(V \+ E) time, O(V) space  
  * **Union-Find:** O(E · α(V)) time, O(V) space  
  * For **sparse graphs** (E ≈ V) → both are roughly O(V), similar.  
  * For **dense graphs** (E ≈ V²) → DFS/BFS is O(V²), Union-Find is also O(V²·α(V)) — actually **slightly worse** due to α(V) overhead.  
  * **So DFS/BFS is generally better or equal for this specific problem.** Union-Find shines in different scenarios:  
    * **Dynamic connectivity** — edges added one by one, check connectivity after each (DFS would need full restart)  
    * **Cycle detection**  
    * **Kruskal's MST**

* [684\. Redundant Connection](https://leetcode.com/problems/redundant-connection/) \- Cycle detection [Redundant Connection - Leetcode 684 - Python](https://www.youtube.com/watch?v=1lNK80tOTfc)

**Important:**

* [721\. Accounts Merge](https://leetcode.com/problems/accounts-merge/) \- Union Find application [G-50. Accounts Merge - DSU](https://www.youtube.com/watch?v=FMwpt_aQOGw)  
* [128\. Longest Consecutive Sequence](https://leetcode.com/problems/longest-consecutive-sequence/) \- Creative use [Longest Consecutive Sequence | Google Interview Question | Brute Better Optimal](https://www.youtube.com/watch?v=oO5uLE7EUlM)  
* [305\. Number of Islands II](https://leetcode.com/problems/number-of-islands-ii/) \- Dynamic connectivity (Premium) [G-51. Number of Islands - II - Online Queries - DSU](https://www.youtube.com/watch?v=Rn6B-Q4SNyA)

---

## **5\. Dijkstra's Algorithm ⭐⭐⭐**

**Pattern:** Shortest path in weighted graphs (non-negative weights)

[G-32. Dijkstra's Algorithm - Using Priority Queue - C++ and Java - Part 1](https://www.youtube.com/watch?v=V6H1qAeB-l4)

[G-33. Dijkstra's Algorithm - Using Set - Part 2](https://www.youtube.com/watch?v=PATgNiuTP20)

[G-34. Dijkstra's Algorithm - Why PQ and not Q, Intuition, Time Complexity Derivation - Part 3](https://www.youtube.com/watch?v=3dINsjyfooY)

* **Priority Queue is the default choice** — it always picks the globally smallest distance node next, naturally avoiding unnecessary path explorations. Stale entries are handled with a simple `if (dist > d[node]) continue` check. Time: O((V+E) log V).  
* **Set allows true deletion** — unlike PQ, when a shorter path is found you can `erase` the old entry and `reinsert` the updated one, meaning zero stale entries. But the tree overhead makes it slightly slower than PQ in practice.  
* **PQ vs Set same complexity, different constants** — both are O((V+E) log V) but PQ wins in practice due to cache-friendly heap operations vs pointer-based tree in set.  
* **Use Set over PQ when** the graph has very frequent edge relaxations, causing the PQ to get bloated with too many stale entries that waste memory and slow down extraction.  
* **The core greedy guarantee** — regardless of PQ or Set, Dijkstra works because once a node is popped with distance `d`, no future path can improve it. This only holds for **non-negative edge weights** — for negative weights you need Bellman-Ford instead.

A plain (FIFO) queue **only works for unweighted graphs**, where it acts as a Breadth-First Search (BFS) to find the shortest path in time. 

For weighted graphs, a plain queue will not work correctly with Dijkstra's algorithm. Dijkstra's requires a Priority Queue (Min-Heap) to always process the node with the smallest known distance first. 

**Why Plain Queue Fails on Weighted Graphs**

Wrong Processing Order: A FIFO queue processes nodes based on arrival time, not distance. A distant node might be added to the queue before a closer node, leading to incorrect calculations.

Failure to Find Shortest Path: Dijkstra's assumes that once a node is removed from the queue, its shortest path is finalized. If a plain queue is used, a node might be "finalized" via a longer path, and a shorter path discovered later will be ignored.

Corrective Action Required: To make a plain queue work on weighted graphs, you would have to re-insert nodes into the queue every time a shorter path is found, which destroys the efficiency of the algorithm. 

![][image1]

**Must Solve:**

* [743\. Network Delay Time](https://leetcode.com/problems/network-delay-time/) \- Basic Dijkstra [Network Delay Time - Dijkstra's algorithm - Leetcode 743](https://www.youtube.com/watch?v=EaphyqKU4PQ)  
* [787\. Cheapest Flights Within K Stops](https://leetcode.com/problems/cheapest-flights-within-k-stops/) \- Dijkstra variant [G-38. Cheapest Flights Within K Stops](https://www.youtube.com/watch?v=9XybHVqTHcQ&t=1s)(the trade off is stops instead of distance)  
* [1514\. Path with Maximum Probability](https://leetcode.com/problems/path-with-maximum-probability/) \- Modified Dijkstra [Path with Maximum Probability - Leetcode 1514 - Python](https://www.youtube.com/watch?v=kPsDTGcrzGM)

**Important:**

* [1631\. Path With Minimum Effort](https://leetcode.com/problems/path-with-minimum-effort/) \- Binary search \+ BFS or Dijkstra [G-37. Path With Minimum Effort](https://www.youtube.com/watch?v=0ytpZyiZFhA)(the trade off is max diff instead of distance)  
* [778\. Swim in Rising Water](https://leetcode.com/problems/swim-in-rising-water/) \- Dijkstra on grid [Swim in Rising Water - Dijkstra's Algorithm - Leetcode 778 - Python](https://www.youtube.com/watch?v=amvrKlMLuGY)(the trade off is min height instead of distance)

---

## **6\. Topological Sort ⭐⭐⭐**

[G-19. Detect cycle in a directed graph using DFS | Java | C++](https://www.youtube.com/watch?v=9twcmtQj4DU)  
[G-21. Topological Sort Algorithm | DFS](https://www.youtube.com/watch?v=5lZ0iJMrUMk)\[DFS works  only for DAG but cannot identify the cycle in graph\]  
[G-22. Kahn's Algorithm | Topological Sort Algorithm | BFS](https://www.youtube.com/watch?v=73sneFXuTEg)\[VIMP \- Works to detect cycle\]

**Pattern:** Order tasks with dependencies, detect cycles in directed graphs

**Must Solve:**

* [207\. Course Schedule](https://leetcode.com/problems/course-schedule/) \- Detect cycle [G-24. Course Schedule I and II | Pre-requisite Tasks | Topological Sort](https://www.youtube.com/watch?v=WAOfKpxYHR8)  
* [210\. Course Schedule II](https://leetcode.com/problems/course-schedule-ii/) \- Topological order  
* [269\. Alien Dictionary](https://leetcode.com/problems/alien-dictionary/) \- Build graph \+ topo sort (Premium) [G-26. Alien Dictionary - Topological Sort](https://www.youtube.com/watch?v=U3N_je7tWAs)

**Important:**

* [310\. Minimum Height Trees](https://leetcode.com/problems/minimum-height-trees/) \- Reverse thinking [Minimum Height Trees - Leetcode 310 - Python](https://www.youtube.com/watch?v=wQGQnyv_9hI)[310\. Minimum Height Trees | BFS | Topological Sort | Graphs](https://www.youtube.com/watch?v=tJAwo2hgMU8&t=116s)  
  **Core Approach (Leaf Trimming / BFS Inward)**  
* Repeatedly remove leaf nodes (degree \= 1\) layer by layer  
* Reduce neighbor's degree by 1; if it becomes 1, it's the new leaf  
* Stop when 1 or 2 nodes remain — those are the MHT roots  
* Intuition: the answer is always the **center of the longest path**, peeling from outside in closes in on it  
* At most **2 nodes** can ever be the answer  
  ---

  **What is Topological Sort (Kahn's Algorithm)?**  
* Works on **directed graphs**  
* Repeatedly removes nodes with **indegree \= 0** (no dependencies left)  
* Gives a **linear ordering** respecting dependencies  
* Goal: **ordering nodes**  
  ---

  **How are they related?**  
* Both use the same **BFS peeling pattern** — process boundary nodes first, reduce neighbor counts, enqueue newly eligible nodes  
* Leaf Trimming \= Topological Sort adapted for **undirected graphs**  
* indegree \= 0 (topo) → degree \= 1 (leaf trimming)  
  ---

  **Why is Leaf Trimming loosely called Topological Sort?**  
* Same underlying **queue-based iterative removal** mechanism  
* Both are **O(V \+ E)**  
* But they are NOT the same — different graph types, different goals, different removal conditions. The name is a **pattern analogy**, not an exact match.

* [1136\. Parallel Courses](https://leetcode.com/problems/parallel-courses/) \- Longest path (Premium) 

---

## **7\. Prim's Algorithm (MST) ⭐⭐**

**Pattern:** Minimum Spanning Tree (greedy, grow tree from one node)

[G-45. Prim's Algorithm - Minimum Spanning Tree - C++ and Java](https://www.youtube.com/watch?v=mJcZjjKzeqk&t=165s)

**Problems:**

* [1135\. Connecting Cities With Minimum Cost](https://leetcode.com/problems/connecting-cities-with-minimum-cost/) \- Classic MST (Premium)   
* [1584\. Min Cost to Connect All Points](https://leetcode.com/problems/min-cost-to-connect-all-points/) \- Prim's or Kruskal [Prim's Algorithm - Minimum Spanning Tree - Min Cost to Connect all Points - Leetcode 1584 - Python](https://www.youtube.com/watch?v=f7JOBJIC-NA)

---

## **8\. Kruskal's Algorithm (MST) ⭐⭐**

**Pattern:** Minimum Spanning Tree (sort edges, use Union Find) [G-47. Kruskal's Algorithm - Minimum Spanning Tree - C++ and Java](https://www.youtube.com/watch?v=DMnDM_sxVig&t=365s)

**Problems:**

* [1584\. Min Cost to Connect All Points](https://leetcode.com/problems/min-cost-to-connect-all-points/) \- Kruskal with Union Find \[same as prims but using krushal’s algo with union find\]  
* [1489\. Find Critical and Pseudo-Critical Edges in Minimum Spanning Tree](https://leetcode.com/problems/find-critical-and-pseudo-critical-edges-in-minimum-spanning-tree/description/) **\[Kruskal's only\]**  
* [1579\. Remove Max Number of Edges to Keep Graph Fully Traversable](https://leetcode.com/problems/remove-max-number-of-edges-to-keep-graph-fully-traversable/description/) \[**Kruskal's only**\]  
* [1135\. Connecting Cities With Minimum Cost](https://leetcode.com/problems/connecting-cities-with-minimum-cost/) \- Premium \[classic MST same as above\]

---

## **9\. Bellman-Ford ⭐**

**Pattern:** Shortest path with negative weights, detect negative cycles [G-41. Bellman Ford Algorithm](https://www.youtube.com/watch?v=0vVofAhAYjc&t=61s)

**Problems:**

* [787\. Cheapest Flights Within K Stops](https://leetcode.com/problems/cheapest-flights-within-k-stops/) \- Can use Bellman-Ford [Bellman-Ford - Cheapest Flights within K Stops - Leetcode 787 - Python](https://www.youtube.com/watch?v=5eIK3zUdYmE)(here we run the relaxation only k times instead of v times)  
* [743\. Network Delay Time](https://leetcode.com/problems/network-delay-time/) \- Can use (Dijkstra better) \[straight forward\]

[1334\. Find the City With the Smallest Number of Neighbors at a Threshold Distance](https://leetcode.com/problems/find-the-city-with-the-smallest-number-of-neighbors-at-a-threshold-distance/description/)

[1514\. Path with Maximum Probability](https://leetcode.com/problems/path-with-maximum-probability/description/)

[1631\. Path With Minimum Effort](https://leetcode.com/problems/path-with-minimum-effort/description/)

[675\. Cut Off Trees for Golf Event](https://leetcode.com/problems/cut-off-trees-for-golf-event/description/)

**1334** → pure Bellman-Ford / Floyd-Warshall

**1514** → modified Bellman-Ford with probabilities

**1631** → modified with min-max weight

**675** → hardest, BFS \+ Bellman-Ford combo

**Note:** Rarely the expected solution, usually Dijkstra or BFS preferred

---

## **10\. Strongly Connected Components (SCC) ⭐**

**[G-54. Strongly Connected Components - Kosaraju's Algorithm](https://www.youtube.com/watch?v=R6uoSjZ2imo)**

**Pattern:** Find maximal strongly connected subgraphs (Kosaraju's/Tarjan's)

**Problems:**

* [1192\. Critical Connections in a Network](https://leetcode.com/problems/critical-connections-in-a-network/) \- Tarjan's algorithm (bridges) [G-55. Bridges in Graph - Using Tarjan's Algorithm of time in and low time](https://www.youtube.com/watch?v=qrAub5z8FeA)  
* [323\. Number of Connected Components](https://leetcode.com/problems/number-of-connected-components-in-an-undirected-graph/) \- Undirected version [Number of Connected Components in an Undirected Graph - Union Find - Leetcode 323 - Python](https://www.youtube.com/watch?v=8f1XPm4WOUc&t=184s)\[union find\]

## **11\. Floyd-Warshall (All-Pairs Shortest Path)**

[G-42. Floyd Warshall Algorithm](https://www.youtube.com/watch?v=YbY8cVwWAvw)

**Problems:**

* [1334\. Find the City With the Smallest Number of Neighbors at a Threshold Distance](https://leetcode.com/problems/find-the-city-with-the-smallest-number-of-neighbors-at-a-threshold-distance/) [G-43. Find the City With the Smallest Number of Neighbours at a Threshold Distance](https://www.youtube.com/watch?v=PwMVNSJ5SLI)  
* [1463\. Cherry Pickup II](https://leetcode.com/problems/cherry-pickup-ii/) (DP, not typical Floyd-Warshall but can use concept) [DP 13. Cherry Pickup II | 3D DP Made Easy | DP On Grids](https://www.youtube.com/watch?v=QGfn7JeXK54)  
* [399\. Evaluate Division](https://leetcode.com/problems/evaluate-division/) (can use Floyd-Warshall or DFS/Union Find) [Evaluate Division - Leetcode 399 - Python](https://www.youtube.com/watch?v=Uei1fwDoyKk)\[we can use floyd-warshall to calc all shorts paths that is essentially xi/yi where xi and yi are all the vertices when asked yi/xi just return the inverse of xi/yi\]  
* [1568\. Minimum Number of Days to Disconnect Island](https://leetcode.com/problems/minimum-number-of-days-to-disconnect-island/) (advanced) [Minimum Number of Days to Disconnect Island - Leetcode 1568 - Python](https://www.youtube.com/watch?v=aO-QbJ5eZwU)\[not exactly floyd warshall\]

---

## **12\. Max Flow (Ford-Fulkerson / Edmonds-Karp)**

[Ford Fulkerson Algorithm Edmonds Karp Algorithm For Max Flow](https://www.youtube.com/watch?v=GiN3jRdgxU4&t=1288s)

**Problems:**

* [1579\. Remove Max Number of Edges to Keep Graph Fully Traversable](https://leetcode.com/problems/remove-max-number-of-edges-to-keep-graph-fully-traversable/) (can model as flow, but **Union Find is better)** [Remove Max Number of Edges to Keep Graph Fully Traversable - Leetcode 1579 - Python](https://www.youtube.com/watch?v=booGwg5wYm4)  
* [2123\. Minimum Operations to Remove Adjacent Ones in Matrix](https://leetcode.com/problems/minimum-operations-to-remove-adjacent-ones-in-matrix/) (bipartite matching, Premium) [G-17. Bipartite Graph | BFS | C++ | Java](https://www.youtube.com/watch?v=-vu34sct1g8)  
* [1595\. Minimum Cost to Connect Two Groups of Points](https://leetcode.com/problems/minimum-cost-to-connect-two-groups-of-points/) (can use flow, but DP better)

# **🔥** Trees

## **Fundamentals (Common for All Trees)**

* Tree terminology and properties (root, leaf, height, depth, degree, balanced)  
* Tree representations in code  
  * Linked (Node with pointers)  
  * Array (for special cases like heaps)  
* Basic tree traversals (applies to all trees)  
  * DFS: Preorder, Inorder, Postorder  
  * BFS: Level-order

## **1\. General Trees (N-ary Trees)**

* Definition and representation  
* Traversal algorithms (DFS, BFS)  
* Basic tree problems and applications

**Easy:**

* [589\. N-ary Tree Preorder Traversal](https://leetcode.com/problems/n-ary-tree-preorder-traversal/)  
* [590\. N-ary Tree Postorder Traversal](https://leetcode.com/problems/n-ary-tree-postorder-traversal/)  
* [429\. N-ary Tree Level Order Traversal](https://leetcode.com/problems/n-ary-tree-level-order-traversal/)

**Medium:**

* [559\. Maximum Depth of N-ary Tree](https://leetcode.com/problems/maximum-depth-of-n-ary-tree/)  
  * [Breadth First Search & Depth First Search | Maximum Depth of N-ary Tree | LeetCode 559.](https://www.youtube.com/watch?v=TtAflDtqwVg)  
* [1490\. Clone N-ary Tree](https://leetcode.com/problems/clone-n-ary-tree/)  
  * [LeetCode 1490. Clone N-ary Tree - Interview Prep Ep 110](https://www.youtube.com/watch?v=3Wnja3Bxeos)  
* [431\. Encode N-ary Tree to Binary Tree](https://leetcode.com/problems/encode-n-ary-tree-to-binary-tree/)  
  * https://www.youtube.com/watch?v=Rf4fAg4LD4Y  
* [428\. Serialize and Deserialize N-ary Tree](https://leetcode.com/problems/serialize-and-deserialize-n-ary-tree/)  
  * [L36. Serialize and De-serialize Binary Tree | C++ | Java](https://www.youtube.com/watch?v=-YbXySKJsX8)  
  * https://www.youtube.com/watch?v=EIfDP5GH7Vs  
  * [Leetcode 428. Serialize and Deserialize N-Ary Tree Explained using DFS pre-order traversal](https://www.youtube.com/watch?v=u0J17e8477U)

[10.2  B Trees and B+ Trees. How they are useful in Databases](https://www.youtube.com/watch?v=aZjYr87r1b8) (While B-trees and B+ trees share characteristics with general trees (such as having multiple children per node), they are not generally classified simply as "general trees." They are more accurately described as specialized, self-balancing, multi-way search trees)

---

## **2\. Binary Trees**

* Algorithms:  
  * **Insert**  
  * Use BFS (queue) to traverse level by level  
  * Find first node with empty left or right child  
  * Insert new node there

```java
void insert(TreeNode root, int val) {
    if(root == null) {
        root = new TreeNode(val);
        return;
    }
    
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    
    while(!queue.isEmpty()) {
        TreeNode node = queue.poll();
        
        if(node.left == null) {
            node.left = new TreeNode(val);
            return;
        } else {
            queue.offer(node.left);
        }
        
        if(node.right == null) {
            node.right = new TreeNode(val);
            return;
        } else {
            queue.offer(node.right);
        }
    }
}
```

  * **Delete**  
  * Find the node to delete  
  * Find the deepest rightmost node (last in level-order)  
  * Replace deleted node's value with deepest node's value  
  * Delete the deepest node

```java
void delete(TreeNode root, int key) {
    if(root == null) return;
    
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    
    TreeNode target = null;
    TreeNode last = null;
    TreeNode parent = null;
    
    // Find target node and last node
    while(!queue.isEmpty()) {
        last = queue.poll();
        
        if(last.val == key) target = last;
        
        if(last.left != null) {
            parent = last;
            queue.offer(last.left);
        }
        if(last.right != null) {
            parent = last;
            queue.offer(last.right);
        }
    }
    
    if(target != null) {
        target.val = last.val;  // Replace with last node
        
        // Delete last node
        if(parent.right == last) {
            parent.right = null;
        } else {
            parent.left = null;
        }
    }
}

```

  * **Search** (simple binary tree) : use bfs / dfs and traverse all nodes  
  * Tree diameter, height calculation  
    * [L16. Diameter of Binary Tree | C++ | Java](https://www.youtube.com/watch?v=Rezetez59Nk&t=398s)  
  * Symmetric check, subtree check, balanced tree check  
    * [L25. Check for Symmetrical Binary Trees | C++ | Java](https://www.youtube.com/watch?v=nKggNAiEpBE)  
    * [Subtree of Another Tree (LeetCode 572) | Full solution with examples | Study Algorithms](https://www.youtube.com/watch?v=GZ8g8KdavUo)  
    * [Subtree of Another Tree - Leetcode 572 - Python](https://www.youtube.com/watch?v=E36O5SWp-LE)  
    * [L15. Check for Balanced Binary Tree | C++ | Java](https://www.youtube.com/watch?v=Yt50Jfbd8Po)  
  * Lowest Common Ancestor (LCA) approaches  
    * [L26. Print Root to Node Path in Binary Tree | C++ | Java](https://www.youtube.com/watch?v=fmflMqVOC7k)  
    * [Lowest Common Ancestor Binary Tree](https://www.youtube.com/watch?v=13m9ZCB8gjw)  
  * Serialization and deserialization of binary tree  
    * [L36. Serialize and De-serialize Binary Tree | C++ | Java](https://www.youtube.com/watch?v=-YbXySKJsX8)

---

## **3\. Binary Search Trees (BST)**

* BST properties, insertion, deletion, and searching  
* Algorithms specific to BST:  
  * Find min, max, predecessor, successor  
    * [https://leetcode.com/problems/minimum-absolute-difference-in-bst/description/](https://leetcode.com/problems/minimum-absolute-difference-in-bst/description/)  
    * [L49. Inorder Successor/Predecessor in BST | 3 Methods](https://www.youtube.com/watch?v=SXKAD2svfmI)  
  * Validate BST  
    * [L46. Check if a tree is a BST or BT | Validate a BST](https://www.youtube.com/watch?v=f-sj7I5oXEI&t=3s)  
  * LCA in BST  
    * [L47. LCA in Binary Search Tree](https://www.youtube.com/watch?v=cX_kPV_foZc)  
  * BST Traversals revisited with ordered output  
  * Convert sorted array to BST, BST to sorted doubly linked list  
    * [Convert Sorted Array to Binary Search Tree - Leetcode 108 - Python](https://www.youtube.com/watch?v=0K0uCMYq5ng)  
    * flatten binary tree to LL / DLL  
      * [Flatten Binary Tree to Linked List - Leetcode 114 - Python](https://www.youtube.com/watch?v=rKnD7rLT0lI)  
      * [L38. Flatten a Binary Tree to Linked List | 3 Approaches | C++ | Java](https://www.youtube.com/watch?v=sWf7k1x9XR4)  
      * [L37. Morris Traversal | Preorder | Inorder | C++ | Java](https://www.youtube.com/watch?v=80Zug6D1_r4)  
      * [L38. Flatten a Binary Tree to Linked List | 3 Approaches | C++ | Java](https://www.youtube.com/watch?v=sWf7k1x9XR4)  
      * [Binary Tree to DLL | Flatten Binary Tree to Doubly Linked List | DSA-One Course \#62](https://www.youtube.com/watch?v=nGNoTdav5bQ)

---

## **4\. Balanced Binary Search Trees**

* Motivation for balancing and balanced tree properties

## **AVL Tree** 

* Rotations (left, right, left-right, right-left)  
* Insertion, deletion with balancing  
  *  [10.1 AVL Tree - Insertion and Rotations](https://www.youtube.com/watch?v=jDM6_TnYIqE)  
  * [5.15 AVL Tree Deletion in Data structures | with Example | DSA Tutorials](https://www.youtube.com/watch?v=LXdi_4kSd1o) (small correction is that we only calculate balance factor for the nodes along the path of the deletion node so it is logN)  
* Height balancing concept and complexity

## **Red-Black Tree**

* Properties and color rules [5.17 Red Black Tree Insertion | Insertion Algorithm | Data Structure Tutorials](https://www.youtube.com/watch?v=qA02XWRTBdw) (high level knowledge of )  
* Rotations and re-coloring for balancing  
* Insertion, deletion operations

### AVL and Red-Black Tree \- Interview Summary

---

#### Key Differences

| Property | AVL | Red-Black |
| ----- | ----- | ----- |
| **Balance** | Stricter (height diff ≤ 1\) | Looser (black-height balanced) |
| **Height** | ≤ 1.44 log(n) | ≤ 2 log(n) |
| **Rotations** | More (slower insert/delete) | Fewer (faster insert/delete) |
| **Best for** | Lookup-heavy | Insert/delete-heavy |
| **Used in** | Some databases | Java TreeMap/TreeSet, C++ map/set |

---

### When to Use Balanced BST

Use TreeMap/TreeSet (Red-Black) when you need:

* **O(log n)** guaranteed operations (vs O(n) worst case in unbalanced BST)  
* **Sorted order** maintained automatically  
* **Range queries**: floor, ceiling, subMap operations  
* **Sorted iteration**: in-order traversal

**TreeMap vs HashMap:**

* HashMap: O(1) average, no order  
* TreeMap: O(log n), sorted order

---

### Red-Black Properties (5 Rules)

1. Every node is red or black  
2. Root is always black  
3. All leaves (NULL) are black  
4. Red node → both children must be black (no consecutive reds)  
5. All paths from node to leaves have same number of black nodes

---

### AVL Balance Property

Balance factor \= `height(left) - height(right)` must be in {-1, 0, 1}

---

### Complexities (Both Trees)

| Operation | Time |
| ----- | ----- |
| Search | O(log n) |
| Insert | O(log n) |
| Delete | O(log n) |
| Space | O(n) |

---

### Interview Questions You Should Know

**Q: AVL vs Red-Black \- which to use?**

* AVL: More lookups than modifications  
* Red-Black: More modifications (Java's choice for general use)

**Q: Why does Java use Red-Black?**

* Fewer rotations \= faster insert/delete for general-purpose collections

**Q: When to use TreeMap over HashMap?**

* Need sorted order or range queries

**Q: What's the height guarantee?**

* AVL: ≤ 1.44 log(n)  
* Red-Black: ≤ 2 log(n)

---

### LeetCode Problems (Using TreeMap)

* [729\. My Calendar I](https://leetcode.com/problems/my-calendar-i/) [My Calendar I - Leetcode 729 - Python](https://www.youtube.com/watch?v=fIxck3tlId4)  
* [731\. My Calendar II](https://leetcode.com/problems/my-calendar-ii/) [My Calendar II - Leetcode 731 - Python](https://www.youtube.com/watch?v=7utL5cTDcnA)  
* [732\. My Calendar III](https://leetcode.com/problems/my-calendar-iii/) [My Calendar III | Leetcode 732 | Line Sweep Technique Concepts & Questions - 7 | codestorywithMIK](https://www.youtube.com/watch?v=zcMYtmwQNMg)  
* [352\. Data Stream as Disjoint Intervals](https://leetcode.com/problems/data-stream-as-disjoint-intervals/) [Data Stream as Disjoint Intervals - Leetcode 352 - Python](https://www.youtube.com/watch?v=FavoZjPIWpo&t=220s)  
* [715\. Range Module](https://leetcode.com/problems/range-module/)

---

## **5\. Heaps (Binary Heap)**

* Binary heap properties and storage in arrays  
* Min-heap, Max-heap  
* Heap operations: insert, extract-min/max, heapify  
* Applications (priority queues, heap sort)

---

## **6\. Trie (Prefix Tree)**

* Trie structure and representation  
* Insert and search operations  
* Applications: autocomplete, prefix matching, dictionary problems  
  * Word break II [Word Break 2 | Leetcode \#140](https://www.youtube.com/watch?v=fNVs1J2KCyo)  
  * [DSA\_topics](https://docs.google.com/document/d/1ttCR02KHcJ94y7JTGNVUMmAJKntIhY6ijjPjp8rBKfM/edit?tab=t.0#heading=h.87pluudhcvz)DP \+ backtracking \+ trie

### **Must Solve:**

1. [208\. Implement Trie (Prefix Tree)](https://leetcode.com/problems/implement-trie-prefix-tree/)  
2. [211\. Design Add and Search Words Data Structure](https://leetcode.com/problems/design-add-and-search-words-data-structure/) [L2. Implement Trie-2 | INSERT | countWordsEqualTo() | countWordsStartingWith() | C++ | Java](https://www.youtube.com/watch?v=K5pcpkEMCN0&list=PLgUwDviBIf0pcIDCZnxhv0LkHf5KzG9zp&index=2)  
3. [Longest Valid Word with All Prefixes](https://www.geeksforgeeks.org/problems/longest-valid-word-with-all-prefixes/1) [L3. Longest Word With All Prefixes | Complete String | Trie](https://www.youtube.com/watch?v=AWnBa91lThI&list=PLgUwDviBIf0pcIDCZnxhv0LkHf5KzG9zp&index=3)  
4. [**Count of distinct substrings**](https://www.geeksforgeeks.org/problems/count-of-distinct-substrings/1) [L4. Number of Distinct Substrings in a String | Trie | C++ | Java](https://www.youtube.com/watch?v=RV0QeTyHZxo&list=PLgUwDviBIf0pcIDCZnxhv0LkHf5KzG9zp&index=4)  
5. [421\. Maximum XOR of Two Numbers in an Array](https://leetcode.com/problems/maximum-xor-of-two-numbers-in-an-array/description/) [L6. Maximum XOR of Two Numbers in an Array | C++ | Java](https://www.youtube.com/watch?v=EIhAwfHubE8&list=PLgUwDviBIf0pcIDCZnxhv0LkHf5KzG9zp&index=6)  
6. [1707\. Maximum XOR With an Element From Array](https://leetcode.com/problems/maximum-xor-with-an-element-from-array/description/) [L7. Maximum XOR With an Element From Array | Queries | C++ | Java](https://www.youtube.com/watch?v=Q8LhG9Pi5KM&list=PLgUwDviBIf0pcIDCZnxhv0LkHf5KzG9zp&index=7)  
7. [212\. Word Search II](https://leetcode.com/problems/word-search-ii/) (word search 1 is prerequisite)  
   1. [Word Search - Backtracking - Leetcode 79 - Python](https://www.youtube.com/watch?v=pfiQ_PS1g8E)  
   2. [Word Search II - Backtracking Trie - Leetcode 212 - Python](https://www.youtube.com/watch?v=asbcE9mZz_U)

### **Should Solve:**

4. [140\. Word Break II](https://leetcode.com/problems/word-break-ii/)  
   1. [Word Break - Dynamic Programming - Leetcode 139 - Python](https://www.youtube.com/watch?v=Sx9NNgInc3A)(Word break 1 is done using only brackTracking \+ memo \+ DP)  
   2. [Word Break II - Leetcode 140 - Python](https://www.youtube.com/watch?v=QgLKdluDo08)(using backtracking \+ dp)  
   3. [Word Break 2 | Leetcode \#140](https://www.youtube.com/watch?v=fNVs1J2KCyo&t=288s)(backtracking with Trie approach)  
5. [472\. Concatenated Words](https://leetcode.com/problems/concatenated-words/)  
   1. ​​[Concatenated Words - Leetcode 472 - Python](https://www.youtube.com/watch?v=iHp7fjw1R28)(recursion \+ dp approach \- variation of word break)  
   2. [Leetcode 472 Concatenated Words (Java)](https://www.youtube.com/watch?v=mMDLbRJxCMk)(trie solution)

### **Good to Know:**

6. [1268\. Search Suggestions System](https://leetcode.com/problems/search-suggestions-system/)  
7. [648\. Replace Words](https://leetcode.com/problems/replace-words/)

---

## **7\. Segment Tree & Fenwick Tree (Binary Indexed Tree)**

* Segment Tree:  
  * Construction, update, and query for range problems  
    * [Segment Tree: Build and Query | Live Coding..](https://www.youtube.com/watch?v=-dUiRtJ8ot0&t=932s) \[creation for range query for max / min value\]  
    * [Range Sum Query - Mutable | Leetcode 307 | Segment tree construction and update](https://www.youtube.com/watch?v=2bSS8rtFym4) \[update operation for range query for sum value (mutable)\] \- Note segment tree is used only with updates are frequent else we use array approach.   
  * Lazy propagation for range updates [Segment Tree - Range Queries with Lazy Updates](https://www.youtube.com/watch?v=CN0N1ddJ9hA)   
* Fenwick Tree:  
  * Construction, update, query operations  
  * Differences from Segment Tree, when to use which

### Segment Tree Problems:

#### Pattern 1: Range Query \+ Point Update

1. [307\. Range Sum Query \- Mutable](https://leetcode.com/problems/range-sum-query-mutable/) [Range Sum Query - Mutable | Leetcode 307 | Segment tree construction and update](https://www.youtube.com/watch?v=2bSS8rtFym4)

#### Pattern 2: Range Min/Max Query

2. Range Minimum Query (RMQ) \- Not on LeetCode as standalone, covered in:  
   * [2407\. Longest Increasing Subsequence II](https://leetcode.com/problems/longest-increasing-subsequence-ii/) [2407\. Longest Increasing Subsequence II | Leetcode Weekly Contest 310 | LeetCode 2407](https://www.youtube.com/watch?v=H3IsFJgKNtA)

#### Pattern 3: Range Update \+ Range Query (Lazy Propagation)

3. Range Add Query \- Not directly on LeetCode, but concept tested in:  
   * [370\. Range Addition](https://leetcode.com/problems/range-addition/) (Premium) [Leetcode Range Addition | Solving AlgoPrep 151 | Nishant Chahar](https://www.youtube.com/watch?v=P8Ggr-B81YM)  
   * [732\. My Calendar III](https://leetcode.com/problems/my-calendar-iii/) (alternative approach for line sweep is segment tree below video has the editorial) [My Calendar III - Leetcode 732 - Road to FAANG \#20](https://www.youtube.com/watch?v=wZAY3hM4AUg)

#### Pattern 4: Counting/Frequency Tree

4. [315\. Count of Smaller Numbers After Self](https://leetcode.com/problems/count-of-smaller-numbers-after-self/)  
   1. [315\. Count of Smaller Numbers After Self / Segment Tree](https://www.youtube.com/watch?v=leJu3HWGpfk)  
   2. [Segment Tree Data Structure - Min Max Queries - Java source code](https://www.youtube.com/watch?v=xztU7lmDLv8)

#### Pattern 5: Optional Advanced

5. [218\. The Skyline Problem](https://leetcode.com/problems/the-skyline-problem/)

### Fenwick Tree (BIT) Problems:

#### Pattern 1: Prefix Sum \+ Point Update

1. [307\. Range Sum Query \- Mutable](https://leetcode.com/problems/range-sum-query-mutable/) [Fenwick Tree or Binary Indexed Tree](https://www.youtube.com/watch?v=CWDQJGaN1gY) (prefix\[r\] \- prefix\[l-1\])

#### Pattern 2: Inversion Counting

2. [493\. Reverse Pairs](https://leetcode.com/problems/reverse-pairs/) (similar concept) [Reverse Pairs | Hard Interview Question](https://www.youtube.com/watch?v=0e4bZaP3MDI) (try doing with fenwick tree similar to count of smaller number after self)  
3. Count Inversions \- Not directly on LeetCode, practice on:  
   * [GeeksforGeeks \- Count Inversions](https://www.geeksforgeeks.org/problems/inversion-of-array-1587115620/1) [Count Inversions in an Array | Brute and Optimal](https://www.youtube.com/watch?v=AseUmwVNaoY) (try doing with fenwick tree)

#### Pattern 3: Count Smaller After Self

4. [315\. Count of Smaller Numbers After Self](https://leetcode.com/problems/count-of-smaller-numbers-after-self/) [315\. Count of Smaller Numbers After Self | Day 024 | Fenwick Tree | Merge Sort](https://www.youtube.com/watch?v=WpttRpfw28U) (similar to inversion count)

#### Pattern 4: Order Statistics (Optional)

5. [2158\. Amount of New Area Painted Each Day](https://leetcode.com/problems/amount-of-new-area-painted-each-day/) (Premium, advanced)

### Fenwick Tree vs Segment Tree \- Concise Notes

---

#### Point Update vs Range Update

**Point Update:** Update a **single index**

arr\[3\] \= 10  // Change one element

**Range Update:** Update **multiple indices at once**

Add 5 to arr\[2..5\]  // Change arr\[2\], arr\[3\], arr\[4\], arr\[5\] all at once

arr\[2\] \+= 5, arr\[3\] \+= 5, arr\[4\] \+= 5, arr\[5\] \+= 5

---

#### 1\. Fenwick Tree (Binary Indexed Tree)

Core Concept:

* Stores **prefix sums** internally  
* Range \[l, r\] \= `prefix[r] - prefix[l-1]` (uses subtraction trick)

Operations & Complexity:

| Operation | Time |
| ----- | ----- |
| Build | O(n log n) |
| Point update | O(log n) |
| Range query \[l, r\] | O(log n) |
| Space | O(n) |

What It Can Do:

✅ Range sum queries  
 ✅ XOR queries  
 ✅ Any **invertible** operation (has inverse: sum→subtract, XOR→XOR)

What It Cannot Do:

❌ Range min/max (no inverse)  
 ❌ Range GCD  
 ❌ Range updates (bulk updates)

When to Use:

* Problem needs **sum/XOR only**  
* Only **point updates**  
* Want **simple, fast code**

---

#### 2\. Segment Tree

Core Concept:

* Stores **range information** directly (not prefix)  
* Directly queries range \[l, r\] without subtraction

Operations & Complexity:

| Operation | Time |
| ----- | ----- |
| Build | O(n) |
| Point update | O(log n) |
| Range query \[l, r\] | O(log n) |
| Range update | O(log n) (with lazy propagation) |
| Space | O(4n) |

What It Can Do:

✅ Range sum, min, max, GCD, XOR (any **associative** operation)  
 ✅ Point updates  
 ✅ Range updates (with lazy propagation)  
 ✅ Complex merge operations

When to Use:

* Need **min/max/GCD** queries  
* Need **range updates**  
* Operation is **not invertible**

---

#### 3\. Key Differences

| Aspect | Fenwick | Segment Tree |
| ----- | ----- | ----- |
| **Query method** | `prefix[r] - prefix[l-1]` | Direct range query |
| **Operations** | Invertible only (sum, XOR) | Any associative (min, max, sum, GCD) |
| **Range updates** | ❌ No | ✅ Yes (lazy propagation) |
| **Code complexity** | \~20 lines | \~60 lines |
| **Implementation time** | 3-5 min | 10-15 min |

---

#### 4\. Decision Guide

Need range queries?

│

├─ SUM/XOR only \+ point updates? → Fenwick Tree

│

├─ MIN/MAX/GCD queries? → Segment Tree

│

└─ Need range updates? → Segment Tree (lazy propagation)

---

#### 5\. Time Complexity Summary

**Both trees:**

* Point update: O(log n)  
* Range query: O(log n)

**Key difference:**

* Fenwick: 2 queries for range (`query(r) - query(l-1)`)  
* Segment: 1 direct query for range

---

#### 6\. Interview Strategy

**If problem only needs sum/XOR:**  
 → Use Fenwick (simpler, faster to code)

**If problem needs min/max/GCD or range updates:**  
 → Must use Segment Tree

**If both work:**  
 → Prefer Fenwick (less error-prone in 45-minute interview)

---

## **8\. Specialized Trees (Overview, advanced topics)**

* B-trees and B+ trees (database indexing) [10.2  B Trees and B+ Trees. How they are useful in Databases](https://www.youtube.com/watch?v=aZjYr87r1b8) (no prob / impl is required)  
* Suffix Trees and Suffix Arrays (string algorithms) [Suffix Tree using Ukkonen's algorithm](https://www.youtube.com/watch?v=aPRqocoBsFQ) \[watch when you get time\]  
  * the below prob are imp and can be solved using the suffix tree but it over kills so use the below alternative methods to solve these problems which are expected by the interviewers. 

### **Use Rolling Hash / Binary Search:**

* [1044\. Longest Duplicate Substring](https://leetcode.com/problems/longest-duplicate-substring/)  
* [1062\. Longest Repeating Substring](https://leetcode.com/problems/longest-repeating-substring/)  
* [1316\. Distinct Echo Substrings](https://leetcode.com/problems/distinct-echo-substrings/)

### **Use Trie / Set / Rolling Hash:**

* [1698\. Number of Distinct Substrings in a String](https://leetcode.com/problems/number-of-distinct-substrings-in-a-string/)

### **Use KMP / Rolling Hash:**

* [28\. Find the Index of the First Occurrence in a String](https://leetcode.com/problems/find-the-index-of-the-first-occurrence-in-a-string/)  
* [214\. Shortest Palindrome](https://leetcode.com/problems/shortest-palindrome/)

### **Use DP / Rolling Hash:**

* [718\. Maximum Length of Repeated Subarray](https://leetcode.com/problems/maximum-length-of-repeated-subarray/)

# **🔥** Dynamic Programming

***VVIMP \- DP is used to say if a path exists but not to generate the path.*** 

## **0\. DP Fundamentals (Common for All DP)**

### **Core Concepts**

* What is DP (overlapping subproblems \+ optimal substructure)

* Memoization (Top-Down)

* Tabulation (Bottom-Up)

* State definition

* Transition function

* Base cases

* Time & space optimization

### **Patterns**

* Recursion → Memo → Tabulation → Space optimized

* Choice diagrams

* Index-based DP

* Take / Not-take decisions

---

## **1\. 1D DP (Linear DP)**

### **State Type**

* `dp[i]` → answer till index `i`

### **Problems**

* Fibonacci

* Climbing stairs

* House Robber (I, II)

* Maximum subarray (Kadane as optimized DP) \-\>   
  *Kadane’s is a space-optimized DP where the subproblem is "the max subarray ending at index ." The DP logic lies in using the previous result to decide whether to extend the current subarray or start a new one, reducing an  array into a single current\_sum variable.*

* Min cost climbing stairs

* Decode ways

### **Optimizations**

* Space optimized DP

* Greedy crossover cases

---

## **2\. 2D DP (Grid / Matrix DP)**

### **State Type**

* `dp[i][j]`

### **Grid Problems**

* Unique paths

* Unique paths with obstacles

* Minimum path sum

* Max path sum

* Triangle minimum path sum

### **Advanced**

* Cherry Pickup

* Dungeon Game

---

## **3\. Knapsack Pattern (MOST IMPORTANT)**

### **3.1 0/1 Knapsack**

* Weight constraint

* Pick or not pick

**Problems**

* Subset sum

* Equal sum partition

* Count subsets with sum K

* Target sum

* Last stone weight II

---

### **3.2 Unbounded Knapsack**

* Infinite picks allowed

**Problems**

* Coin change (min coins)

* Coin change (count ways)

* Rod cutting

* Integer break

---

### **3.3 Bounded Knapsack**

* Limited frequency items

---

## **4\. DP on Strings**

### **State Type**

* `dp[i][j]` → prefix of string1 & string2

### **Core Problems**

* Longest Common Subsequence (LCS)

* Longest Common Substring (this is diff from pattern matching where we use the kmp and karf robin algo but for LCS we need to use dp)

* Edit Distance

* Delete operations for two strings

### **Palindrome DP**

* Longest Palindromic Subsequence

* Longest Palindromic Substring ([algo](https://www.youtube.com/watch?v=XYQecbcd6_c), [dp](https://www.youtube.com/watch?v=UflHuQj6MVA), [manachers](https://www.youtube.com/watch?v=V-sEwsca1ak), [manacherCodeInDepth](https://www.youtube.com/watch?v=ei7qghJEj4Y))

* Palindrome partitioning [Palindrome Partitioning II | Dynamic Programming | 132 LeetCode | Day 07](https://www.youtube.com/watch?v=ot5Q9dD5rWM)  
* Count palindromic substrings   
  * O(n^3) \- time, O(1) space \- brute force (find all substrings and check pal)  
  * O(n^2) \- time, O(n^2) \- DP  
  * O(n^2) \- time, O(1) \- 2 pointer 

---

## **5\. DP on Subsequences**

### **Patterns**

* Increasing / Decreasing subsequences

### **Problems**

* LIS (O(n²) DP)  
  * [DP 41. Longest Increasing Subsequence | Memoization](https://www.youtube.com/watch?v=ekcwMsSIzVc)  
  * [DP 42. Printing Longest Increasing Subsequence | Tabulation | Algorithm](https://www.youtube.com/watch?v=IFfYfonAFGc)

* LIS (Binary Search optimization) [DP 43. Longest Increasing Subsequence | Binary Search | Intuition](https://www.youtube.com/watch?v=on2hvxBXJH4)

* Longest Bitonic Subsequence [DP 46. Longest Bitonic Subsequence | LIS](https://www.youtube.com/watch?v=y4vN0WNdrlg)

* Russian Doll Envelopes

---

## **6\. DP on Trees**

### **State Type**

* `dp[node][state]`

### **Problems**

* Tree Diameter

* Maximum path sum

* House Robber III

* Binary tree coloring

* Subtree DP

### **Patterns**

* Postorder traversal

* Return multiple values from recursion

---

## **7\. DP on Graphs**

### **Types**

* DAG DP

* Topological order DP

### **Problems**

* Longest path in DAG

* Shortest path in DAG

* Course schedule variants

---

## 8\. DP with Bitmasking (apart from Traveling Salesman none is important)

### **State Type**

* `dp[mask][i]`

### **Problems**

* Traveling Salesman Problem (TSP)

* Assignment problem

* Counting subsets with constraints

---

## **9\. Digit DP (Advanced / Rare)**

### **Used For**

* Counting numbers with constraints in a range

### **Problems**

* Count numbers with no consecutive digits digit dp sol belo w)

prob [Link](https://cses.fi/problemset/task/2220)

[L5. Power Exponentiation | Maths Playlist](https://www.youtube.com/watch?v=hFWckDXE-K8) (this is using math formula)

[Introduction to Digit DP for Competitive Programming. (like never before)](https://www.youtube.com/watch?v=gIJczEQoSmM&list=PLN2s9sIPkFC9HAF_8Os7pIbE8ZAwlA-uF&index=1) (exact digit dp question)

```java
public class NoConsecutiveDigitDP {

    static char[] digits;
    static long[][][][] dp;

    public static long countNoConsecutive(long X) {
        digits = Long.toString(X).toCharArray();

        int n = digits.length;
        // dp[pos][prev+1][tight][started]
        dp = new long[n][11][2][2];

        // initialize with -1 (means uncomputed)
        for (int i = 0; i < n; i++) {
            for (int p = 0; p < 11; p++) {
                for (int t = 0; t < 2; t++) {
                    for (int s = 0; s < 2; s++) {
                        dp[i][p][t][s] = -1;
                    }
                }
            }
        }

        // subtract 1 to exclude number 0
        return dfs(0, -1, 1, 0) - 1;
    }

    private static long dfs(int pos, int prev, int tight, int started) {
        // Base case: all digits processed
        if (pos == digits.length) {
            return 1;
        }

        long cached = dp[pos][prev + 1][tight][started];
        if (cached != -1) {
            return cached;
        }

        int limit = (tight == 1) ? digits[pos] - '0' : 9;
        long res = 0;

        for (int d = 0; d <= limit; d++) {

            int newStarted = (started == 1 || d != 0) ? 1 : 0;

            // Only check consecutive digits if number has started
            if (newStarted == 1 && started == 1 && d == prev) {
                continue;
            }

            int newPrev = (newStarted == 1) ? d : -1;
            int newTight = (tight == 1 && d == limit) ? 1 : 0;

            res += dfs(pos + 1, newPrev, newTight, newStarted);
        }

        dp[pos][prev + 1][tight][started] = res;
        return res;
    }

    public static void main(String[] args) {
        System.out.println(countNoConsecutive(345)); // example
        System.out.println(countNoConsecutive(9));   // 9
        System.out.println(countNoConsecutive(100)); // test leading zeros
    }
}
```

* Count numbers with sum of digits \= K (digit dp sol below) [probLink](https://leetcode.com/problems/count-of-integers/description/)  
  [L-06. Digit DP | Count of Integers | Leetcode 2719 | Recursive + Memoization | Intuition + Code](https://www.youtube.com/watch?v=vl5xBcP4OZg&list=PLN2s9sIPkFC9HAF_8Os7pIbE8ZAwlA-uF&index=6)

```java
public class DigitSumDigitDP {

    static char[] digits;
    static long[][][][] dp;
    static int K;

    public static long countDigitSum(long X, int targetSum) {
        digits = Long.toString(X).toCharArray();
        K = targetSum;

        int n = digits.length;
        dp = new long[n][K + 1][2][2];

        for (int i = 0; i < n; i++)
            for (int s = 0; s <= K; s++)
                for (int t = 0; t < 2; t++)
                    for (int st = 0; st < 2; st++)
                        dp[i][s][t][st] = -1;

        return dfs(0, 0, 1, 0);
    }

    private static long dfs(int pos, int sum, int tight, int started) {

        if (sum > K) return 0;

        if (pos == digits.length) {
            return (started == 1 && sum == K) ? 1 : 0;
        }

        if (dp[pos][sum][tight][started] != -1) {
            return dp[pos][sum][tight][started];
        }

        int limit = (tight == 1) ? digits[pos] - '0' : 9;
        long res = 0;

        for (int d = 0; d <= limit; d++) {

            int newStarted = (started == 1 || d != 0) ? 1 : 0;
            int newSum = sum + (newStarted == 1 ? d : 0);
            int newTight = (tight == 1 && d == limit) ? 1 : 0;

            res += dfs(pos + 1, newSum, newTight, newStarted);
        }

        dp[pos][sum][tight][started] = res;
        return res;
    }

    public static void main(String[] args) {
        System.out.println(countDigitSum(500, 5)); // example
    }
}
```

* Count numbers with restricted digits (digit dp sol below) [probLink](https://leetcode.com/problems/numbers-at-most-n-given-digit-set/description/)[L-05. Digit DP | Numbers At Most N Given Digit Set | Leetcode 902 | Recursive + Memoization](https://www.youtube.com/watch?v=AgPrlThfF8c&list=PLN2s9sIPkFC9HAF_8Os7pIbE8ZAwlA-uF&index=5)

```java
import java.util.Set;

public class RestrictedDigitsDigitDP {

    static char[] digits;
    static long[][][] dp;
    static Set<Integer> allowed;

    public static long countUpToX(long X, Set<Integer> allowedDigits) {
        digits = Long.toString(X).toCharArray();
        allowed = allowedDigits;

        int n = digits.length;
        dp = new long[n][2][2];

        for (int i = 0; i < n; i++)
            for (int t = 0; t < 2; t++)
                for (int s = 0; s < 2; s++)
                    dp[i][t][s] = -1;

        return dfs(0, 1, 0);
    }

    private static long dfs(int pos, int tight, int started) {

        if (pos == digits.length) {
            return started == 1 ? 1 : 0;
        }

        if (dp[pos][tight][started] != -1) {
            return dp[pos][tight][started];
        }

        int limit = (tight == 1) ? digits[pos] - '0' : 9;
        long res = 0;

        for (int d = 0; d <= limit; d++) {

            int newTight = (tight == 1 && d == limit) ? 1 : 0;

            if (started == 0) {
                if (d == 0) {
                    res += dfs(pos + 1, newTight, 0);
                } else if (allowed.contains(d)) {
                    res += dfs(pos + 1, newTight, 1);
                }
            } else {
                if (allowed.contains(d)) {
                    res += dfs(pos + 1, newTight, 1);
                }
            }
        }

        dp[pos][tight][started] = res;
        return res;
    }

    public static void main(String[] args) {
        Set<Integer> allowed = Set.of(0, 1, 5);
        System.out.println(countUpToX(200, allowed));
    }
}


```

---

## **10\. Interval DP**

### **State Type**

* `dp[l][r]`

### **Problems**

* Matrix Chain Multiplication [DP 48. Matrix Chain Multiplication | MCM | Partition DP Starts 🔥](https://www.youtube.com/watch?v=vRVfmbCFW7Y)

* Burst Balloons [DP 51. Burst Balloons | Partition DP | Interactive G-Meet Session Update](https://www.youtube.com/watch?v=Yz4LlDSlkns)

* Palindrome partitioning (min cuts) [DP 53. Palindrome Partitioning - II | Front Partition 🔥](https://www.youtube.com/watch?v=_H8V5hJUGd0)

* [Optimal BST](https://www.geeksforgeeks.org/problems/optimal-binary-search-tree2214/1) [Dynamic Programming: Optimal Binary Search Trees Part 2](https://www.youtube.com/watch?v=Bn7b6Blj8hE)[Dynamic Programming: Optimal Binary Search Trees Part 3](https://www.youtube.com/watch?v=H69FkILSLsU)[Dynamic Programming: Optimal Binary Search Trees Part 4](https://www.youtube.com/watch?v=Xt8kuygspOk) [dp solution code](https://www.geeksforgeeks.org/dsa/optimal-binary-search-tree-dp-24/)

---

## **11\. DP on Probability / Expected Value**

### **Problems**

* Dice throw problems [Number of Dice Rolls with Target Sum - Leetcode 1155 - Python](https://www.youtube.com/watch?v=hfUxjdjVQN4) [code for dp sol](https://www.geeksforgeeks.org/dsa/dice-throw-dp-30/)

* Probability of heads in coin tosses [que](https://www.geeksforgeeks.org/dsa/toss-strange-coins-probability/) [Dynamic Programming Solutions - DPV 6.10 Coin Toss with Heads](https://www.youtube.com/watch?v=usm8PjAvQgU) 

* Random walk problems

---

## **12\. DP Optimization Techniques**

### **Must-Know**

* Space optimization

* Prefix max/min

* Binary search \+ DP

* Monotonic queue optimization

* Convex Hull Trick (Very advanced)

---

## **13\. When NOT to Use DP (Important Insight)**

* When greedy works (Kadane, interval scheduling)

* When BFS is better (shortest path in unweighted graph)

* When recursion causes exponential blowup without overlap

# **🔥 GREEDY & HEAP (Priority Queue)**

---

## **A. Greedy – Fundamentals**

### **Core Concepts**

* Greedy choice property

* Local optimal → global optimal

* Proof via:

  * Exchange argument

  * Cut property

* When greedy fails → DP

---

## **B. Classic Greedy Patterns**

### **1\. Interval Scheduling / Activity Selection**

* Sort by:

  * End time (most common)

  * Start time (variants)

**Problems**

* Activity selection

* Meeting rooms I / II [L10. Minimum number of platforms required in a railway station](https://www.youtube.com/watch?v=AsGzwR_FWok) (brute force is incorrect but explains the intuition between sorting of start and end times)

* Minimum number of platforms

* Non-overlapping intervals

---

### **2\. Greedy \+ Sorting**

* Sort \+ decision

**Problems**

* Job sequencing with deadlines[L6. Job Sequencing Problem | Greedy Algorithm Playlist](https://www.youtube.com/watch?v=QbwltemZbRg) (try disjoint set to optimise the inner loop)

* Fractional knapsack [L13. Fractional Knapsack Algorithm](https://www.youtube.com/watch?v=1ibsQrnuEEg)

* Candy distribution [L12. Candy | Slope Approach Intuition Based](https://www.youtube.com/watch?v=IIqVFvKE6RY)

* Assign cookies

* Gas station

---

### **3\. Greedy \+ Two Pointers**

* Matching problems

**Problems**

* Boats to save people

* Pair sum problems (2sum, [2sum ii](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/description/))

* Minimize max difference ([Minimize the Maximum Difference of Pairs - Leetcode 2616 - Python](https://www.youtube.com/watch?v=lf1Pxg7IrzQ)) \[binary search on answer\]

---

### **4\. Greedy on Arrays**

* Jump decisions

**Problems**

* Jump Game I

* Jump Game II

* Can reach end

---

### **5\. Greedy with Frequency / Counting**

* Majority logic

**Problems**

* Majority element (Moore’s Voting)

* Task scheduler [Task Scheduler - Leetcode 621 - Python](https://www.youtube.com/watch?v=s8p8ukTyA2I) \[PQ \+ Q\]  
* Reorganize string [Reorganize String - Tesla Interview Question - Leetcode 767 - Python](https://www.youtube.com/watch?v=2g_b1aYTHeg&t=429s)

---

### **6\. Greedy on Strings**

* Lexicographic decisions

**Problems**

* Remove K digits [Remove K Digits - Leetcode 402 - Python](https://www.youtube.com/watch?v=cFabMOnJaq0)

* Smallest subsequence [Remove Duplicate Letters | Leetcode \#316](https://www.youtube.com/watch?v=j313ttNJjo0)

* Partition labels  
  * my approach to convert to merge interval prob   
  * another approach [Partition Labels - Leetcode 763 - Python](https://www.youtube.com/watch?v=WQZdwxZJAhY)

---

### **7\. Greedy with Stack**

* Monotonic decisions

**Problems**

* Remove K digits

* Stock span [L15. Stock Span Problem | Stack and Queue Playlist](https://www.youtube.com/watch?v=eay-zoSRkVc)

* Next greater element (greedy flavor)  
  * [L5. Next Greater Element | Stack and Queue Playlist](https://www.youtube.com/watch?v=e7XQLtOQM3I)  
  * [L6. Next Greater Element - II | Stack and Queue Playlist](https://www.youtube.com/watch?v=7PrncD7v9YQ)  
  * [Next Permutation - Intuition in Detail 🔥 | Brute to Optimal](https://www.youtube.com/watch?v=JDOXKqF60RQ)

---

---

## **C. Heap / Priority Queue (Often Combined with Greedy)**

---

### **1\. Heap Fundamentals**

* Min Heap

* Max Heap

* Heapify

* Insert

* Extract min / max

All are covered in [2.6.3 Heap - Heap Sort - Heapify - Priority Queues](https://www.youtube.com/watch?v=HqPJF2L5h9U&t=2826s)

---

### **2\. Greedy \+ Heap Patterns**

---

#### **a. Top-K Problems**

* K largest / smallest elements

* K closest points to origin

* K frequent elements [Top K Frequent Elements - Bucket Sort - Leetcode 347 - Python](https://www.youtube.com/watch?v=YPTqKIgVk-k) \<- this is bucket sort approach that has linear time. 

---

#### **b. Scheduling with Heap**

* Task scheduling

* CPU scheduling

* Meeting rooms II

* Minimum platforms

---

#### **c. Two Heaps Pattern**

* One min heap \+ one max heap

**Problems**

* Median from data stream [Find Median from Data Stream - Heap & Priority Queue - Leetcode 295](https://www.youtube.com/watch?v=itmhHWaHupI)

* Sliding window median [SLIDING WINDOW MEDIAN| LEETCODE 480 | PYTHON TWO-HEAP SOLUTION](https://www.youtube.com/watch?v=oKwmSnmEFpY) \- lazy deletion approach. 

---

#### **d. Merge Patterns**

* Merge K sorted arrays 

* Merge K sorted linked lists [L25. Merge K Sorted Lists | Multiple Approaches](https://www.youtube.com/watch?v=1zktEppsdig)

---

#### **e. Continuous Greedy Decisions**

* Always take best current option

**Problems**

* IPO problem [IPO - Leetcode 502 - Python](https://www.youtube.com/watch?v=1IUzNJ6TPEM)

* Connect ropes with minimum cost

* Huffman coding minHeap \+ binary tree construction \+ pre order traversal (gfg editorial) [Huffman Coding | GeeksforGeeks](https://www.youtube.com/watch?v=0kNXhFIEd_w&t=10s)

---

### **3\. Heap \+ Graph (Important Overlap)**

* Dijkstra (min heap)

* Prim’s MST (min heap)

---

## **D. Greedy vs Heap vs DP (Interview Insight)**

| Scenario | Approach |
| ----- | ----- |
| Optimal choice at every step | Greedy |
| Need best among many choices | Heap |
| Overlapping subproblems | DP |

# **🔥 BACKTRACKING**

---

## **A. Backtracking – Fundamentals**

### **Core Concepts**

* Decision tree

* Choose → Explore → Unchoose

* Pruning

* Recursion depth control

### **Template**

`choose`

`recurse`

`unchoose`

---

## **B. Core Backtracking Patterns**

---

### **1\. Subset Generation**

* Include / Exclude

**Problems**

* Subsets

* Subsets II (with duplicates) [L11. Subset Sum II | Leetcode | Recursion](https://www.youtube.com/watch?v=RIn3gOkbhQE) \- power set

* Power set

---

### **2\. Combination Patterns**

* Choose K elements

**Problems**

* Combinations [Combinations - Leetcode 77 - Python](https://www.youtube.com/watch?v=q0s6m7AiM7o)

* Combination Sum I [Combination Sum - Backtracking - Leetcode 39 - Python](https://www.youtube.com/watch?v=GBKI9VSKdGg)

* Combination Sum II

* Combination Sum III [Ep14- Combination Sum 3 |  Medium level Recursion Problem | DSA  | Codes available in description](https://www.youtube.com/watch?v=xNeLP5RDY-c)

---

### **3\. Permutation Patterns**

* Order matters

**Problems**

* Permutations [L12. Print all Permutations of a String/Array | Recursion | Approach - 1](https://www.youtube.com/watch?v=YK78FU5Ffjw)  
  * [L12. Print all Permutations of a String/Array | Recursion | Approach - 1](https://www.youtube.com/watch?v=YK78FU5Ffjw)  
  * [L13. Print all Permutations of a String/Array | Recursion | Approach - 2](https://www.youtube.com/watch?v=f2ic2Rsc9pU)

* Permutations II (duplicates) [Permutations II - Backtracking - Leetcode 47](https://www.youtube.com/watch?v=qhBVWf0YafA)

* Next permutation (greedy \+ backtracking idea)

---

### **4\. String Partitioning**

* Cut at every valid position

**Problems**

* Palindrome partitioning

* Restore IP addresses [Restore IP Addresses - Leetcode 93 - Python](https://www.youtube.com/watch?v=61tN4YEdiTM)

* Word break (backtracking \+ DP)  
  * [Word Break (LeetCode 139) | Full Solution using memoization and dynamic programming](https://www.youtube.com/watch?v=hK6Git1o42c)

---

### **5\. Grid Backtracking**

* Move in directions

**Problems**

* Word search [Word Search - Backtracking - Leetcode 79 - Python](https://www.youtube.com/watch?v=pfiQ_PS1g8E)

* Rat in a maze [L19. Rat in A Maze | Backtracking](https://www.youtube.com/watch?v=bLGZhJlt4y0)

* Unique paths III  
  * [Unique Paths II - Leetcode 63 - Python](https://www.youtube.com/watch?v=d3UOz7zdE4I)  
  * [unique paths iii |  unique paths iii leetcode | leetcode 980 | dfs + backtrack](https://www.youtube.com/watch?v=XNKCkX_tHhM)

---

### **6\. Constraint Satisfaction Problems (VERY IMPORTANT)**

**Problems**

* N-Queens [L14. N-Queens | Leetcode Hard | Backtracking](https://www.youtube.com/watch?v=i05Ju7AftcM)  
  * find prev queen in O(1) using hash map

* Sudoku solver [L15. Sudoko Solver | Backtracking](https://www.youtube.com/watch?v=FWAIf_EVUKE)

* Graph coloring [L16. M-Coloring Problem | Backtracking](https://www.youtube.com/watch?v=wuVwUK25Rfc)

* Knight’s tour [KNIGHTS TOUR Problem - Backtracking | Leetcode 2596](https://www.youtube.com/watch?v=Sp1jzttFVdE)

---

### **7\. Backtracking \+ Pruning**

* Early stopping

**Examples**

* Skip duplicates

* Stop when sum exceeds target

* Use visited arrays

---

## **C. Backtracking with Optimizations**

---

### **1\. Backtracking \+ Bitmask**

* Faster visited tracking

**Problems**

* N-Queens optimized

* Subsets using bits [L4. Power Set | Bit Manipulation](https://www.youtube.com/watch?v=LqKaUv1G3_I)

---

### **2\. Backtracking \+ DP (Memoization)**

* Avoid recomputation

**Problems**

* Word break II [Word Break 2 | Leetcode \#140](https://www.youtube.com/watch?v=fNVs1J2KCyo&t=285s)

* Palindrome partitioning II [DP 53. Palindrome Partitioning - II | Front Partition 🔥](https://www.youtube.com/watch?v=_H8V5hJUGd0)

---

### **3\. Backtracking \+ Trie**

* Multiple word search

**Problems**

* Word Search II

* Auto-complete problems

---

## **D. Time Complexity Patterns (Interview Expectation)**

| Problem Type | Complexity |
| ----- | ----- |
| Subsets | O(2ⁿ) |
| Permutations | O(n\!) |
| Combinations | O(C(n,k)) |
| N-Queens | O(n\!) |

Interviewers care more about **pruning logic** than raw complexity.

# **🔥 Bit Manipulation** 

## **0️⃣ Absolute Fundamentals (Non-negotiable)**

### **Binary basics**

* Binary representation of signed integers

* Two’s complement

* Left shift `<<`, right shift `>>`, unsigned `>>>`

* Bit positions (0-indexed)

### **Core identities**

* `x & 1` → odd/even

* `x << 1` → multiply by 2

* `x >> 1` → divide by 2

* `x ^ x = 0`

* `x ^ 0 = x`

### Binary & Two's Complement Essentials

**Integer Limits (32-bit signed int)**

* Max: 2,147,483,647 (2³¹ \- 1\)  
* Min: \-2,147,483,648 (-2³¹)  
* Formula: Sum of n bits \= 2ⁿ \- 1  
  * Example: 2⁰ \+ 2¹ \+ ... \+ 2³⁰ \= 2³¹ \- 1

**Two's Complement (How Negatives Work)**

* To get \-x: Flip all bits, then add 1  
* Example: \+5 \= `00000101` → flip → `11111010` → \+1 → `11111011` \= \-5  
* Leftmost bit indicates sign (0=positive, 1=negative)  
* All n bits contribute to value (leftmost has weight \-2ⁿ⁻¹)

**Why Two's Complement?**

* Addition/subtraction use same circuit (just add, always)  
* Only one zero (not \+0 and \-0)  
* Example: 5 \+ (-3) \= `0101` \+ `1101` \= `0010` \= 2 ✓

**Binary Addition**

* Rules: 0+0=0, 0+1=1, 1+1=10 (write 0, carry 1\)  
* Carry from rightmost: propagates left

**Carry vs Overflow**

* **Carry**: Bit goes beyond n bits (might be fine)  
* **Overflow**: Result is WRONG (doesn't fit range)  
* Overflow occurs when:  
  * Positive \+ Positive \= Negative ✗  
  * Negative \+ Negative \= Positive ✗  
  * Positive \+ Negative \= Never overflows ✓

### Summary Table

| Type | Leftmost bit | Range (8-bit) | All bits used? |
| ----- | ----- | ----- | ----- |
| **signed int** | Sign indicator | \-128 to 127 | Yes (two's complement) |
| **unsigned int** | Regular value bit | 0 to 255 | Yes (pure binary) |

---

## **1️⃣ XOR Pattern (MOST IMPORTANT)**

> **If you know only ONE thing in bit manipulation, know XOR**

### **Properties**

* XOR is associative & commutative

* Self-canceling

### **Problems**

* **Single Number**

* Single Number II / III   
  * [L6. Single Number II | Bit Manipulation](https://www.youtube.com/watch?v=5Bb2nqA40JY)  
  * [L7. Single Number III | Bit Manipulation](https://www.youtube.com/watch?v=UA5JnV1J2sI)

* Missing number  
  * [Missing Number - Blind 75 - Leetcode 268 - Python](https://www.youtube.com/watch?v=WnPLSRLSANE)

* Find the odd occurring element

* XOR of array range  
  * [XOR of numbers in a range \[L, R\] | XOR operation in an array (LeetCode 1486) | Innovative](https://www.youtube.com/watch?v=YlBeQMWw5PA)

### **Google expectation**

> Reduce problem → cancellation logic → O(n), O(1)

---

## **2️⃣ Set / Clear / Toggle Bits**

### **Operations**

* Set bit: `x | (1 << i)`

* Clear bit: `x & ~(1 << i)`

* Toggle bit: `x ^ (1 << i)`

* Check bit: `(x & (1 << i)) != 0`

### **Problems**

#### 1\. Bit Mask Validation

* [338\. Counting Bits](https://leetcode.com/problems/counting-bits/)

  #### 2\. Flags / State Tracking

* [78\. Subsets](https://leetcode.com/problems/subsets/) [L4. Power Set | Bit Manipulation](https://www.youtube.com/watch?v=LqKaUv1G3_I&t=634s)  
* [847\. Shortest Path Visiting All Nodes](https://leetcode.com/problems/shortest-path-visiting-all-nodes/) [Shortest Path Visiting All Nodes | Leetcode 847 | Live coding session 🔥🔥 | BFS + Bit Manipulation](https://www.youtube.com/watch?v=1XkMFNvkouo)

  #### 3\. Permission-Style Problems

* [1356\. Sort Integers by The Number of 1 Bits](https://leetcode.com/problems/sort-integers-by-the-number-of-1-bits/) [LC: 1356 | Sort Integers by the number of 1 Bits](https://www.youtube.com/watch?v=mzw8UjjQRyA)

---

## **3️⃣ Counting Bits (VERY POPULAR)**

### **Algorithms**

* Brian Kernighan’s algorithm

* DP counting bits

### **Problems**

* Number of 1 bits

* Counting bits (0 → n)

* Hamming distance [Hamming Distance | Leetcode \#461](https://www.youtube.com/watch?v=ocIK9LUHWoQ)

* Total Hamming distance [LC 477: Total Hamming Distance, Part 1](https://www.youtube.com/watch?v=o6vNBteS6k0)

Google likes:

> Optimization from naïve → bit trick

---

## **4️⃣ Power of Two / Three / Four Checks**

### **Concepts**

* Single bit set

### **Conditions**

* `n > 0 && (n & (n - 1)) == 0`

### **Problems**

* Power of two [Power of Two - Leetcode 231 - Python](https://www.youtube.com/watch?v=H2bjttEV4Vc)

* Power of four [342\. Power of Four | Leetcode Daily - Python](https://www.youtube.com/watch?v=a2x5XnxcYis)

* Ugly numbers (variant)  
  *  [Ugly Number - Leetcode 263 - Python](https://www.youtube.com/watch?v=M0Zay1Qr9ws)  
  * [Ugly Number II - Leetcode 264 - Python](https://www.youtube.com/watch?v=1pj2a5bmziY&t=638s)

---

## **5️⃣ Bit Masking (State Compression)**

> **Used when constraints are small (≤ 20\)**

### **Concepts**

* Represent subsets using bits

* Track visited states

* Encode choices

### **Problems**

* Subsets

* Subsets with constraints

* DP \+ bitmask (intro level)  
* [526\. Beautiful Arrangement](https://leetcode.com/problems/beautiful-arrangement/) [LeetCode 526: Beautiful Arrangement](https://www.youtube.com/watch?v=SYGdAJQT35k)  
* [698\. Partition to K Equal Sum Subsets](https://leetcode.com/problems/partition-to-k-equal-sum-subsets/) [Partition to K Equal Sum Subsets - Backtracking - Leetcode 698 - Python](https://www.youtube.com/watch?v=mBk4I0X46oI)(pls optimize this using dp with bitmasking)  
* [980\. Unique Paths III](https://leetcode.com/problems/unique-paths-iii/)   
  * https://leetcode.com/problems/unique-paths-iii/submissions/1918565154/  
* [847\. Shortest Path Visiting All Nodes](https://leetcode.com/problems/shortest-path-visiting-all-nodes/)

Google SWE-3 level:

* Small mask

* Clean state explanation

* No heavy TSP-level DP

---

## **6️⃣ AND / OR Logic on Subarrays**

### **Patterns**

* Prefix AND / OR

* Sliding window \+ bitmask

## **AND / OR Logic on Subarrays**

**Key Properties:**

**OR is non-decreasing:**

* `a | b | c >= a | b >= a`  
* Adding elements only adds bits (never removes)

**AND is non-increasing:**

* `a & b & c <= a & b <= a`  
* Adding elements only removes bits (never adds)

---

**Prefix OR:**

arr \= \[5, 3, 7, 2\] \= \[0101, 0011, 0111, 0010\]

prefixOR\[0\] \= 5         \= 0101

prefixOR\[1\] \= 5 | 3     \= 0111

prefixOR\[2\] \= 5 | 3 | 7 \= 0111

prefixOR\[3\] \= 5|3|7|2   \= 0111

**Prefix AND:**

arr \= \[7, 3, 6, 2\] \= \[0111, 0011, 0110, 0010\]

prefixAND\[0\] \= 7         \= 0111

prefixAND\[1\] \= 7 & 3     \= 0011

prefixAND\[2\] \= 7 & 3 & 6 \= 0010

prefixAND\[3\] \= 7&3&6&2   \= 0010

**Key Insight:** At most 32 distinct OR/AND values when expanding from fixed start (32 bits max)

### **Problems**

* Subarray AND equals K [Leetcode 1521. Find a Value of a Mysterious Function Closest to Target](https://www.youtube.com/watch?v=PDbdmrt46Mc)

* Subarray OR problems [898\. Bitwise ORs of Subarrays | Leetcode Daily - Python](https://www.youtube.com/watch?v=fMXTARyx7nU)

* Minimum OR value window [Shortest Subarray With OR at Least K II - Leetcode 3097 - Python](https://www.youtube.com/watch?v=Bq_BEsgBQOs)

Google loves:

> Combining bit logic with array patterns

---

## **7️⃣ Bit Manipulation \+ Math**

### **Concepts**

* Extract lowest set bit

* Remove lowest set bit

### **Tricks**

* `x & (-x)` → lowest set bit

* `x & (x - 1)` → remove lowest set bit  
  ![][image2]

### **Problems**

* Divide two integers [L9. Divide Two Integers without using Multiplication and Division Operators | Bit Manipulation](https://www.youtube.com/watch?v=pBD4B1tzgVc)

* Sum of two integers (no `+`) [Sum of Two Integers - Leetcode 371 - Java](https://www.youtube.com/watch?v=gVUrDV4tZfY)

* Reverse bits [Reverse Bits - Binary - Leetcode 190 - Python](https://www.youtube.com/watch?v=UcoN6UjAI64)

---

## **8️⃣ Signed vs Unsigned (Google Favorite Edge Case)**

### **Concepts**

* Overflow behavior

* Right shift vs unsigned right shift

* 32-bit integer boundaries

![][image3]

### **Problems**

* Reverse bits

* Integer overflow checks

* Divide without operators

[Sum of Two Integers - Leetcode 371 - Java](https://www.youtube.com/watch?v=gVUrDV4tZfY)

# **🔥 ARRAYS & STRINGS**

> Arrays & Strings are **THE MOST IMPORTANT** topic at Google.

---

## **A️⃣ Absolute Core Patterns (Non-Negotiable)**

### **1️⃣ Two Pointers (★★★★★)**

**Google LOVES this**

**Variants**

* Left–right pointers

* Slow–fast pointers

* Same direction pointers

**Must-know problems**

* Valid palindrome (with constraints)

* Remove duplicates (sorted array)

* Move zeros

* Container with most water

* 3Sum / 4Sum (sorting \+ two pointers) [4 Sum | Brute - Better - Optimal with Codes](https://www.youtube.com/watch?v=eD95WRfh81c)

**Google expectation**

> Maintain invariant, explain pointer movement clearly

---

### **2️⃣ Sliding Window (★★★★★)**

**Types**

* Fixed window

* Variable window (expand / shrink)

* At-most / Exactly-K windows

**Must-know problems**

* Longest substring without repeating characters

* Longest substring with K distinct characters

* Minimum window substring

* Max sum subarray of size K

**Google expectation**

> Window invariant explanation \> code

---

### **3️⃣ Prefix Sum \+ HashMap (★★★★★)**

**Patterns**

* Prefix sum array

* Prefix sum \+ frequency map

**Must-know problems**

* Subarray sum \= K

* Subarray with 0 sum

* Continuous subarray divisible by K \[Not so intuitive\]

* Range sum queries

---

## **B️⃣ Sorting \+ Searching Patterns**

### **4️⃣ Sorting \+ Logic (★★★★☆)**

**Used to simplify problems**

**Must-know**

* Sort \+ scan

* Sort \+ greedy

* Sort \+ two pointers

**Problems**

* Merge intervals

* Insert interval

* Meeting rooms

* K closest points

---

### **5️⃣ Binary Search (★★★★☆)**

**Variants**

* On sorted arrays

* On answer (parametric search)

**Must-know problems**

* First/last occurrence

* Search in rotated array

* Koko eating bananas (binary search on answer)

* Min days to make bouquets (binary search on answer)

**Google expectation**

> Clear monotonic condition

---

## **C️⃣ String-Specific Google Favorites**

### **6️⃣ Character Frequency / Hashing (★★★★★)**

**Patterns**

* Frequency array (26 / 128 / 256\)

* HashMap

**Must-know problems**

* Anagram check

* Group anagrams

* Permutation in string

* Find all anagrams

---

### **7️⃣ String Parsing / Simulation (★★★★☆)**

**Problems**

* String compression

* Valid parentheses

* Decode string

* Compare version numbers

---

### **8️⃣ Greedy on Strings (★★★★☆)**

**Problems**

* Remove K digits

* Partition labels

* Smallest subsequence

* Reorganize string

---

## **D️⃣ Advanced but Frequently Seen**

### **9️⃣ Monotonic Stack (★★★★☆)**

**Patterns**

* Next greater / smaller

* Increasing / decreasing stack

**Problems**

* Daily temperatures

* Largest rectangle in histogram

* Trapping rain water

---

### **🔟 Matrix / 2D Array Patterns (★★★★☆)**

**Problems**

* Rotate matrix

* Set matrix zeroes

* Spiral traversal

* Search in sorted matrix

# **🔥 LINKED LIST**

> Google doesn’t ask many LL problems, but **expects mastery when they do**.

---

## **A️⃣ Core Linked List Patterns**

### **1️⃣ Fast & Slow Pointers (★★★★★)**

**Must-know**

* Detect cycle [Linked List Cycle - Floyd's Tortoise and Hare - Leetcode 141 - Python](https://www.youtube.com/watch?v=gBTe7lFR3vc)

* Find cycle start [L17. Find the starting point of the Loop/Cycle in LinkedList | Multiple Approaches](https://www.youtube.com/watch?v=2Kd0KKmmHFc&t=7s)

* Middle of linked list

* Happy number (conceptually) [Happy Number (LeetCode 202) | Full solution with with easy explanation | Study Algorithms](https://www.youtube.com/watch?v=LkD0D0Xy-ro)

---

### **2️⃣ Reversal Patterns (★★★★★)**

**Must-know**

* Reverse linked list

* Reverse between left & right

* Reverse in K groups [Reverse Nodes in K-Group - Linked List - Leetcode 25](https://www.youtube.com/watch?v=1UOPsfP85V4)

**Google expectation**

> Pointer discipline \+ no memory leaks

---

### **3️⃣ Merge Patterns (★★★★☆)**

**Problems**

* Merge two sorted lists

* Merge K sorted lists (heap) [L25. Merge K Sorted Lists | Multiple Approaches](https://www.youtube.com/watch?v=1zktEppsdig&t=1554s)

* Sort linked list (merge sort) [L26. Sort a Linked List | Merge Sort and Brute Force](https://www.youtube.com/watch?v=8ocB7a_c-Cc)

---

## **B️⃣ Structural Transformations**

### **4️⃣ Reorder / Rearrangement (★★★★☆)**

**Problems**

* Reorder list [Linkedin Interview Question - Reorder List - Leetcode 143 - Python](https://www.youtube.com/watch?v=S5bfdUTrKLM)

* Odd–even linked list

* Rotate list

---

### **5️⃣ Clone / Copy Patterns (★★★★☆)**

**Problems**

* Copy list with random pointer [L27. Clone a LinkedList with Next and Random Pointers | Copy List with Random Pointers](https://www.youtube.com/watch?v=q570bKdrnlw)

**Google expectation**

> O(1) space explanation

---

## **C️⃣ Arithmetic & Edge Cases**

### **6️⃣ Arithmetic on Linked List (★★★☆☆)**

**Problems**

* Add two numbers

* Plus one list [L11. Add 1 to a number represented by LinkedList](https://www.youtube.com/watch?v=aXQWhbvT3w0)

---

### **7️⃣ Intersection & Removal (★★★★☆)**

**Problems**

* Intersection  of two linked lists [L12. Find the intersection point of Y LinkedList](https://www.youtube.com/watch?v=0DYoPz2Tpt4)

* Remove Nth node from end [L8. Remove Nth Node from the end of the LinkedList | Multiple Approaches](https://www.youtube.com/watch?v=3kMKYQ2wNIU)

* Delete node in linked list [Delete Node in a Linked List | Can you solve it ?](https://www.youtube.com/watch?v=icnp4FJdZ_c)

# Appendix

## **🔷 Graphs**

1. Graph Representation (2D matrix, adjacency list, edge list)  
2. BFS (Breadth-First Search)  
3. DFS (Depth-First Search)  
4. Union Find / DSU (Disjoint Set Union)  
5. Dijkstra's Algorithm  
6. Topological Sort (DFS \+ Kahn's BFS)  
7. Prim's Algorithm (MST)  
8. Kruskal's Algorithm (MST)  
9. Bellman-Ford  
10. Strongly Connected Components (Kosaraju's / Tarjan's)  
11. Floyd-Warshall (All-Pairs Shortest Path)  
12. Max Flow (Ford-Fulkerson / Edmonds-Karp)

---

## **🔷 Trees**

1. General Trees (N-ary Trees)  
2. Binary Trees (insert, delete, search, diameter, LCA, serialization)  
3. Binary Search Trees (BST)  
4. Balanced BSTs — AVL Tree & Red-Black Tree  
5. Heaps (Binary Heap / Priority Queue)  
6. Trie (Prefix Tree)  
7. Segment Tree & Fenwick Tree (BIT)  
8. Specialized Trees — B-Trees, Suffix Trees (overview)

---

## **🔷 Dynamic Programming**

1. DP Fundamentals (memoization, tabulation, state definition)  
2. 1D DP (Linear DP)  
3. 2D DP (Grid / Matrix DP)  
4. Knapsack Pattern — 0/1, Unbounded, Bounded  
5. DP on Strings (LCS, Edit Distance, Palindrome DP)  
6. DP on Subsequences (LIS variants)  
7. DP on Trees  
8. DP on Graphs (DAG DP, Topological order DP)  
9. DP with Bitmasking (TSP and variants)  
10. Digit DP  
11. Interval DP (Matrix Chain, Burst Balloons, Optimal BST)  
12. DP on Probability / Expected Value  
13. DP Optimization Techniques (space optimization, Convex Hull Trick)

---

## **🔷 Greedy & Heap**

1. Greedy Fundamentals  
2. Interval Scheduling / Activity Selection  
3. Greedy \+ Sorting (job sequencing, fractional knapsack)  
4. Greedy \+ Two Pointers  
5. Greedy on Arrays (Jump Game)  
6. Greedy with Frequency / Counting (majority element, task scheduler)  
7. Greedy on Strings (Remove K Digits, partition labels)  
8. Greedy with Stack (monotonic decisions)  
9. Heap Fundamentals (min/max heap, heapify)  
10. Top-K Problems  
11. Scheduling with Heap  
12. Two Heaps Pattern (median, sliding window median)  
13. Merge K Sorted Patterns  
14. Continuous Greedy with Heap (IPO, Huffman coding)

---

## **🔷 Backtracking**

1. Backtracking Fundamentals (choose → explore → unchoose)  
2. Subset Generation  
3. Combination Patterns  
4. Permutation Patterns  
5. String Partitioning (palindrome, restore IP, word break)  
6. Grid Backtracking (word search, rat in a maze)  
7. Constraint Satisfaction (N-Queens, Sudoku, graph coloring, Knight's tour)  
8. Backtracking \+ Pruning  
9. Backtracking \+ Bitmask  
10. Backtracking \+ DP (memoization)  
11. Backtracking \+ Trie

---

## **🔷 Bit Manipulation**

1. Binary Basics & Two's Complement  
2. XOR Pattern (single number, missing number)  
3. Set / Clear / Toggle Bits  
4. Counting Bits (Brian Kernighan's algorithm)  
5. Power of Two / Three / Four Checks  
6. Bit Masking (State Compression)  
7. AND / OR Logic on Subarrays  
8. Bit Manipulation \+ Math (lowest set bit tricks, divide without operators)  
9. Signed vs Unsigned Edge Cases

---

## **🔷 Arrays & Strings**

1. Two Pointers  
2. Sliding Window (fixed, variable, at-most-K)  
3. Prefix Sum \+ HashMap  
4. Sorting \+ Logic (merge/insert intervals)  
5. Binary Search (on arrays and on answer)  
6. Character Frequency / Hashing  
7. String Parsing / Simulation  
8. Greedy on Strings  
9. Monotonic Stack  
10. Matrix / 2D Array Patterns

---

## **🔷 Linked List**

1. Fast & Slow Pointers (cycle detection, middle node)  
2. Reversal Patterns (full, partial, K-groups)  
3. Merge Patterns (merge sorted lists, sort LL)  
4. Reorder / Rearrangement  
5. Clone / Copy Patterns (random pointer)  
6. Arithmetic on Linked List (add two numbers)  
7. Intersection & Removal

[image1]: <data:image/png;base64,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>

[image2]: <data:image/png;base64,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>

[image3]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAloAAADVCAYAAACPIl7OAAAzSElEQVR4Xu3d538V1boH8PvH3CO9B0ggEEoIEnoLcEIXCFWpAtKkIwrSRFBEEDyCCgioFFFUmqBIEbAACkqT3tXzZm5+i/sMa541O3VPSPm9+H72alOy96zZz6xZs/M/Tz31vx4RERERxd//6AIiIiIiig8GWkREREQRYaBFREREFBEGWkREREQRYaBFREREFBEGWkREREQRYaBFREREFBEGWkREREQRYaBFREREFJEyG2j9/dd9r2+fXk553boJ3rZPt3r37932Pt+13ZR99dUX3n//eWiMGzfGWYaIiIgoTFwDrYSEmn5AsnPHp355xYrlTRmCm3pJdZ3l4kG2W79eojF16mS/7JOPtwTaTp48wa+zy0//csops9sd+f5bp56IiIgolrgGWiCBSbly//LLxowZFXmAEhY4weJFC0z5zZtXA+WzZ83wypd/ys/XqVPLtJsze0bM9SKAa9SoobMNIiIiojCRBVq6LCWlgdMW3njjde/7w4e8Tp3a+2W4PWfT5Y0bpzjrCdtuTnUImOyyy5d+N/mFC+YFti3LIt2rZ/fAOsL2Xez6bLs3atQIM8qHNnbdrJnTvf3793hTpkx0lpPRNqQRoG7f9rGXnv600w7Wr/uPt3fvl16LFmlOHco2f7TBW7/+Xa9ChXKBOuzTy3PnmJG+adMmO8sSERFRfEQaaA3o39cJcMSSJQsDdToYknxPK7hZsWJ5zPXp5W2rV680dV98viPQFrcCkcftTCl7f/173po1q3xSjvTEieNM+1j7jluk8+e95Of7PdMnsF/J9ZMCy129eil0PXD2zM/eq/NfDiwvbt26Frqc5P96eM8EUkhnZnYzdSkpj0bidNudO7Z5P/14IrB+IiIiio9IAy28fv31F04b3LJDHUaRpOydd94ODR4OHNjrl929e9MbPXqksz69XU2CjTt3bgTa2yNNC159xZRVqVIp1/XG2vfdX+w0+e7d/23ymFCflpYac12JibVN3h4R020QMCHfunV6oM2zzw4NXQajXGH7e/36FT+NQMyuZ6BFREQUjcgDLcCtKrvN7+d/9es0ucWI2132ugCT6fX2wrartWnT0tRhlMhuX5hAKxbUS6CFEa28LIfgSLeRfJ/ePU0ewSLyEyaMC9Rret02XY/A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>