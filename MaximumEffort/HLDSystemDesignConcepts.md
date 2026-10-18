**System Design Interview**

**Complete Study Plan \+ Google Interview Questions**

*Optimized for Google SWE3 | Phases 1–7 with Practice Walkthrough Questions*

  ▸ Green \= Basic      ▸ Orange \= Intermediate      ▸ Red \= Advanced      ▸ Purple italic \= Google Interview Questions


# **How To Use This Document**

For each subtopic, study the concept first, then speak your answer out loud to each Google Interview Question below it. Time yourself — aim for 2–3 minutes per question. For Phase 7 problems, use the Walkthrough Questions as your interview script: go through them in order, out loud, as if an interviewer is sitting across from you.

*The goal is not to memorize answers — it is to train yourself to think out loud, drive the conversation, and reason through tradeoffs under pressure.*

# **PHASE 1 — FOUNDATION (Must Master First)**

## **1\) Computer Architecture**

**▸ Basic**

* CPU vs Memory vs Disk — understand access speed hierarchy

* Latency vs Throughput — what's the difference and why it matters

* Cache hierarchy: L1, L2, L3 → RAM → Disk (each 10–100x slower)

* I/O basics — how data moves in and out of a system

1. **What is I/O**  
   1. Data movement between CPU ↔ Memory ↔ Disk ↔ Network  
   2. Handled by I/O Controller (contains disk, network, usb controller) (hardware on motherboard)  
2. **Data Flow**  
   1. Device → I/O Controller → Memory → CPU processes → Output  
   2. Example: Read disk file → I/O Controller → RAM → CPU  
3. **Why It Matters**  
   1. I/O is 1,000,000x slower than CPU (bottleneck)  
   2. Disk: 10ms, Network: 100ms vs CPU: nanoseconds  
4. **Two Approaches**  
   1. Polling: CPU keeps checking (wastes CPU cycles) ❌  
   2. Interrupts: Device signals when ready (CPU does other work) ✅  
      1.  Interrupts are the hardware mechanism. Async/callbacks are the software pattern built on top of interrupts.  
5. **How to Optimize (KEY for HLD)**  
   1. **Caching:** Keep frequently accessed data in Memory/Redis  
   2. **Buffering:** Load bulk data to reduce repeated I/O hits  
   3. **Async I/O:** Don't block waiting for I/O  
   4. Goal: Minimize I/O Controller usage  
6. **Design Pattern**  
   1. Design around I/O bottleneck  
   2. Plan caching/buffering strategy upfront  
   3. Use async wherever possible  
7. **Real Example**  
   1. Cache DB queries in Redis instead of hitting DB every time  
   2. Buffer file reads instead of reading byte-by-byte

**▸ Intermediate**

* Memory access costs — how cache misses kill performance

* Disk vs SSD vs RAM — when to use which for storage decisions

* Multi-core processing — parallelism and shared memory

Thread Pools (Spring-style)

| Term | Meaning |
| :---- | :---- |
| **Core Pool Size** | Minimum threads the pool keeps for that executor; baseline parallelism for that feature. |
| **Max Pool Size** | Hard cap on concurrent worker threads for that pool. |
| **Queue Size** | Tasks waiting when all threads are busy; full queue → rejection / caller-runs (depends on policy). |

Key Points

* "Core" in properties usually means thread-pool core, not CPU cores—unless the doc explicitly says machine CPU.  
* **CPU-bound work**: Many threads \> CPU cores → contention, context switching; tune toward \~cores (or small multiple) per process.  
* **I/O-bound work (HTTP, DB, Redis)**: Higher concurrency can help; bounded by downstream limits, memory, and pool/connection limits.  
* **Shared memory:** One JVM \= one heap; threads share it (synchronize for correctness). Different JVMs don't share heap—coordinate via DB, Redis, queues, APIs.

JVM, Instance, Pod (Typical Microservices)

| Idea | Practical Rule & Notes |
| :---- | :---- |
| **JVM** | 1 JVM ≈ 1 pod. Standard pattern: one container per pod, one Java main → one JVM. |
| **Replica / Instance** | One running copy of the service → one pod in K8s terms. |
| **Edge Cases** | Sidecar containers (e.g. envoy) mean one pod, multiple containers, but your app JVM is usually one per app container. |

Nodes & Cluster (Kubernetes)

| Term | Meaning |
| :---- | :---- |
| **Node** | A machine (VM or bare metal) in the cluster: provides CPU, RAM, disk to run pods. |
| **Pod** | Smallest deployable unit; scheduled onto a node. |
| **Cluster** | Many nodes \+ control plane that orchestrates workloads. |

Key Insight  
Pods on the same node share CPU/RAM (with limits/requests). DB/Redis/kafka/SQS are separate services with their own URLs—all pods hit the same endpoints regardless of node location.

What the Cluster Does (High Level)

* Schedules pods  
* Scaling (more/fewer replicas)  
* Rolling updates  
* Health checks  
* Service discovery  
* Load balancing to pods (via Service/Ingress)  
* Config/secrets management

**NOT:** "DB lives on the node"

**Notes: Kubernetes → pods → sidecars → mesh (short)**

Kubernetes (K8s)

* Orchestrator that runs containers on machines: schedules work, restarts failures, scales replicas, exposes services via DNS/load balancing.

Cluster

* Whole K8s deployment: control plane \+ many worker machines.

Node

* One machine (VM or bare metal) in the cluster that runs pods.

Pod

* Smallest deployable unit: usually one main app container, plus optional extra containers in the same pod (shared network namespace, [localhost](http://localhost) between them).

Replica / workload

* Same app image scaled to multiple pods for availability and throughput.

Sidecar pattern

* Helper container alongside the main app in the same pod (extra process, not merged into one binary). Common jobs: proxy, logs, metrics, secrets, tracing.

Envoy

* A proxy program (often used as the service-mesh data plane sidecar). Not “the name of sidecar” in general—but often the sidecar is Envoy when Istio/other meshes inject it.

Where Envoy sits (typical service mesh)

* One Envoy sidecar per pod that participates in the mesh (not one per cluster, not one per node—per meshed workload pod). Ingress gateways are separate pods at the edge.

Service mesh

* Layer on top of K8s services: handles service-to-service traffic rules, retries, timeouts, observability—often via proxies on the path.

mTLS

* Services authenticate each other with certificates (TLS between services), often enforced/terminated by proxies.

Your mental model (pod) Pod ≈ main app container(s) \+ optional sidecars (e.g. Envoy, log shipper, metrics agent, secrets/certs helpers, DB proxy)—only where your platform/chart opts in, not universally required for every pod.

**▸ Advanced**

* How CPU/memory architecture affects distributed system design

* Identifying hardware bottlenecks in large-scale systems

**🎯 Google Interview Questions — speak these out loud:**

* *Why is RAM so much faster than disk? How does this affect your database design choices?* 

*use caching*

* Cache (Redis in RAM): App → Network call → Redis (data in RAM) → instant

  * Database (on disk): App → Network call → DB → disk I/O → slow

* *If a service has high CPU but low memory usage, what does that tell you about the bottleneck?*

  **What "high CPU, low memory" actually means:**

* CPU is **heavily utilized** (100%, burning cycles)  
* RAM usage is **low** (plenty of free RAM)  
  **What this tells you:**  
* **Bottleneck \= CPU, not memory**  
* The service is doing **heavy computation** (not I/O waiting)  
* Examples: Data processing, calculations, JSON parsing  
  **Solution:**  
* Add more CPU cores (scale up)  
* Optimize the algorithm (reduce CPU work)  
* NOT memory — you have plenty

* *You have a cache hit rate of 60%. Is that good? What would you do to improve it?*

* *Explain cache hierarchy to a non-engineer. Why do L1/L2 caches exist at all?*

  ***Why L1/L2 exist:***

  *CPU works at **nanoseconds** (billionths of a second).*

  *Fetching from **RAM takes \~100 nanoseconds** (way too slow for CPU).*

  *L1/L2 caches keep the most-needed data **within 1-10 nanoseconds** so CPU doesn't waste time waiting.*

  ***Without L1/L2:** CPU would constantly stall waiting for RAM. With them: CPU stays busy.*


  ***What happens:***

1. *Thread A runs on Core 1 → uses Core 1's L1/L2 cache*  
2. *Context switch → Thread B runs on Core 1*  
3. *Thread A's data from L1/L2 → stays in L1/L2 (not necessarily moved to RAM)*  
4. *Thread B's data → loaded into Core 1's L1/L2 from RAM/L3*

   ***Key difference from what you said:***

* *Thread A's cache data **doesn't get moved to RAM** during context switch*  
* *L1/L2 cache simply gets **overwritten** with Thread B's data*  
* *Thread A's cached data might already be in L3 or RAM if it was evicted*  
* *When Thread A resumes later on Core 1, it might get cache hits (if data still there) or cache misses (if overwritten)*

  ***The problem:***

* *Each context switch causes **cache misses** because the new thread doesn't find its data in L1/L2*  
* *This is why **frequent context switching kills performance***  
* *More threads than cores \= more context switching \= more cache misses*

  ***Visual:***

  * *Time 0: Thread A on Core 1*

  * *Core 1 L1/L2: \[A's data\]*

  * 

  * *Time 1: Context switch to Thread B*

  * *Core 1 L1/L2: \[B's data\] ← overwrites A's data (not saved to RAM)*

  * 

  * *Time 2: Context switch back to Thread A*

  * *Core 1 L1/L2: \[A's data\] ← might be a CACHE MISS if overwritten*

                              *has to reload from L3/RAM (slow)*

* *How does multi-core processing change how you design a concurrent web server?*

  ***For I/O-bound (HTTP, DB calls):***

* *Use **HIGH concurrency** (more threads than cores)*  
* *Why: Threads wait on I/O, so other threads can run*  
* *Thread pool: `corePoolSize=4, maxPoolSize=200`*  
* *While Thread 1 waits for DB, Threads 2-200 handle requests*  
  ***For CPU-bound (calculations):***  
* *Use **LOW concurrency** (≈ number of cores)*  
* *Why: All threads compete for CPU, more threads \= context switching overhead*  
* *Thread pool: `corePoolSize=4, maxPoolSize=8` (if 4 cores)*  
* *More threads \= contention, cache misses, slower*

## **2\) Operating Systems**

**▸ Basic**

* Processes vs Threads — isolation, memory sharing, overhead

* Memory management — heap vs stack, virtual memory

* Paging vs Segmentation — how OS organizes memory

1\. Processes vs Threads

**Process:**

* One running instance of program  
* Own isolated memory (heap, stack, code)  
* Heavy overhead  
* Cannot access other process's memory

**Thread:**

* Lightweight inside process  
* Share same process's heap  
* Low overhead  
* Need synchronization when accessing shared heap

**In your service:**

Pod → ONE Process → Multiple Threads (all share Heap, each has own Stack)

---

2\. Memory Management

**Stack:**

* Local variables, function parameters  
* Per-thread (each thread has own)  
* Auto freed when function returns  
* Thread-safe (no locks needed)

**Heap:**

* Dynamic objects (created with `new`)  
* Shared by all threads in process  
* Slower, large size  
* **Need synchronization** when multiple threads access

**Virtual Memory:**

* OS uses disk as RAM when physical RAM full  
* Program sees unlimited memory but very slow (disk I/O)  
* Avoid it → design with proper memory sizing, caching, streaming

---

3\. Paging vs Segmentation

**Both \= techniques to map Virtual Memory → Physical RAM**

* **Paging (Modern):** Fixed 4KB blocks, simple, efficient  
* **Segmentation (Old):** Variable blocks, complex, rarely used

**For HLD:** OS maps virtual addresses → physical RAM. You don't care about details, just know virtual memory \= disk swap \= slow.

---

Key Takeaways

1. Each process isolated → multiple services \= multiple processes safe  
2. Threads share Heap → use locks for shared data  
3. Stack is thread-safe → local vars safe  
4. Virtual Memory \= disk swap → monitor memory, avoid it  
5. Synchronize shared Heap data → thread safety

**▸ Intermediate**

* Context switching — cost, when it happens, how it affects throughput

* Concurrency — race conditions, deadlocks, critical sections

* Synchronization — mutexes, semaphores, locks

**Q: Cost of context switching?** A: "CPU cycles saving/loading state \+ cache misses (10-100x slower reload from RAM). For CPU-bound work, limit threads to \~cores to minimize switching."

**Q: Race condition example?** A: "`counter++` is 3 ops (read, increment, write). Two threads do this → wrong result. Solution: synchronize."

**Q: Deadlock example?** A: "Thread A has lock1, waiting for lock2. Thread B has lock2, waiting for lock1. Both stuck forever. Solution: acquire locks in same order."

**Q: Mutex vs Semaphore?** A: "Mutex \= 1 thread only. Semaphore \= N threads. Use semaphore for resource pools (DB connections)."

**▸ Advanced**

* Distributed OS concepts — clocks, coordination, consensus

***Logical Clocks (Coordination)***

**Problem:** Multiple servers have different clocks → messages arrive out of order → transactions fail.

**Solution:** Lamport timestamps ensure causal ordering.

When receiving message with timestamp T:

your\_clock \= max(your\_clock, T) \+ 1

Example: Pay ($100, ts=1) → Ship (ts=2) → Log (ts=3)

All services process in order 1→2→3 (consistent)

**Why:** Guarantees distributed transactions execute in correct order.

---

***Consensus***

**Problem:** Multiple servers (no single master) need to agree on decision.

**Solution:** Consensus algorithm (Raft).

Node A proposes: "I am leader"

Node B, C agree ✓

Write "X=10": Leader decides, followers agree

Committed (safe even if nodes fail)

**When to use:** Distributed databases, leader election, multi-master replication.

* Fault tolerance at OS level — process isolation and recovery

* Resource scheduling — how OS prioritizes work

**Q: How does the OS schedule threads?**

A: "OS uses scheduling algorithms:

* **Round Robin:** Fair, each thread gets time slice  
* **Priority Queue:** Urgent tasks first (with aging to prevent starvation)

  **Thread Type:**

* Interactive (UI, user input) → High priority  
* Background (logging, cleanup) → Low priority  
* I/O-bound (waits on DB) → Medium priority  
* CPU-bound (calculations) → Low priority   
* **Shortest Job First:** Maximize throughput  
* Choice depends on workload (interactive vs batch)"

**Q: What about distributed systems?**

A: "Distributed systems need:

* **Synchronized clocks** (NTP) for ordering events  
* **Consensus algorithms** (Raft) for multi-server agreement  
* **Process isolation** so one failure doesn't cascade  
* **Health checks** to detect and restart failed processes"

**Q: Fault tolerance?**

A: "Isolate processes. If one crashes, restart it independently. Others unaffected. In K8s, health checks detect failures and restart pods."

**🎯 Google Interview Questions — speak these out loud:**

* *When would you prefer threads over processes? What are the tradeoffs?*

Use **Threads** when: Same app, need to share data, lightweight (web server handling 1000 requests).

Use **Processes** when: Separate apps, need isolation, one crash shouldn't affect others (microservices).

**Tradeoff:** Threads are cheap but share memory (need locks). Processes are expensive but isolated (safe).

* *What is a race condition? Give a real-world system design example where one could occur.*

* *How does context switching overhead affect a high-concurrency server design?*

* *Your service is deadlocking under load. How do you diagnose and fix it?*

* *How does virtual memory allow a server to handle more connections than RAM physically allows?*

# **PHASE 2 — CORE SYSTEM DESIGN PRINCIPLES**

## **3\) Scalability**

**▸ Basic**

* What is scalability — handling more load without redesigning

* Vertical scaling — bigger machine (CPU, RAM). Fast but has a ceiling

* Horizontal scaling — more machines. Unlimited but complex

* When to scale vertically vs horizontally — cost vs complexity tradeoff

**When to Use:**

| Scenario | Use |
| ----- | ----- |
| Need quick scaling | Vertical (fast, simple) |
| Long-term growth | Horizontal (unlimited, cheaper) |
| Can't redesign code | Vertical |
| High availability needed | Horizontal (redundancy) |
| Database bottleneck | Horizontal (sharding, replicas) |

**Why Vertical \= Quick (No Redesign)**

✓ Upgrade machine specs (CPU/RAM) 

✓ Restart app 

✓ Done (no code changes)

---

**Why Horizontal \= Slow (Needs Redesign)**

✓ Load Balancer — route requests to multiple servers 

✓ Database Replication — data shared across servers 

✓ Shared Cache — for sessions \+ caching

✓ Health Checks — detect/restart failed servers 

**▸ Intermediate**

* Stateless services — why they scale horizontally so easily

* Replication — copies of data/services for redundancy and read scaling

* Load distribution — spreading traffic evenly across nodes

* Read vs Write scaling — they often need different strategies

* Adding replicas — read replicas for DB, standby for failover

**Stateless Services**

No local state stored → any server handles any request → easy horizontal scale.

Request 1 → Server A \-\> forget

Request 2 → Server B \-\> forget

Request 3 → Server C \-\> forget

(No affinity needed)

**Replication**

Multiple copies of data for redundancy \+ read scaling.

Master (writes) → Replica 1, 2, 3 (reads)

Reads distributed across replicas ✓

Master fails → Failover to replica ✓

**Load Distribution**

Load balancer spreads traffic evenly.

1000 requests → Server A (333), Server B (333), Server C (334)

No server overloaded ✓

**Read vs Write Scaling**

**Reads:** Add replicas (easy) **Writes:** Add shards (hard, complex routing)

**Read Replicas vs Standby**

**Read Replica:** Handle read load (active) **Standby:** Backup for failover (passive)

**▸ Advanced**

* Auto-scaling — rules-based and predictive, scaling in/out automatically

  *       		**Rule-based:** CPU \> 80% → add server. CPU \< 20% → remove server.

    *             	**Predictive:** ML learns patterns → scale before spike hits

* Sharding databases — horizontal partitioning of data across machines

* Handling millions of users — stateless app tier \+ sharded DB \+ caching

  Load Balancer

      ↓

  Stateless Servers (A, B, C) ← No local session

      ↓

  Redis (shared sessions \+ cache)

      ↓

  Sharded Database (Shard 1, 2, 3\)

* Handling traffic spikes — pre-scaling, queue buffers, shedding load

  **Pre-scaling (Time-based):**

  Black Friday 12PM → traffic spike expected

  At 11AM: Auto-scale (add 10 servers beforehand)

  At 12PM: Ready to handle spike ✓


  **Queue Buffers:**

  10K requests arrive suddenly

  Servers can only handle 1K/sec


  Queue (buffer): Hold 9K requests

  Servers process 1K/sec from queue

  Requests don't fail, just wait ✓


  Example: Job queue, message broker (Kafka, RabbitMQ)


  **Load Shedding (Reject low-priority requests):**

  Server capacity: 10K requests/sec

  Actual: 15K requests arrive


  Strategy: Reject lowest priority requests

  ├─ Critical (payment): Accept

  ├─ Normal (view page): Accept

  └─ Non-critical (analytics): REJECT


  Result: System stays up, some requests rejected ✓





* Scaling databases — read replicas, CQRS, eventual consistency

  **CQRS (Command Query Responsibility Segregation)**

  **Separate read and write databases.**

  Write DB (optimized for writes):

  ├─ Normalized schema

  ├─ ACID compliance

  └─ Master only


  Read DB (optimized for reads):

  ├─ Denormalized (pre-computed)

  ├─ Fast queries

  └─ Multiple replicas


  Example:

  User posts comment → Write to Write DB

  User reads comments → Read from Read DB (denormalized, fast)


  Sync: Write DB pushes changes to Read DB (asynchronous)

  **Why:** Different DB structures for different needs.

  ***Eventual Consistency (CAP therom) \- AP***

  **Accept temporary inconsistency for speed.**

  User posts comment:

  ├─ Write to DB immediately ✓

  ├─ Read DB updates after 1 second (async)

  User refreshes immediately: Might not see comment yet (eventual\!)

  User refreshes after 2 seconds: Sees comment ✓

  Trade-off: Speed over instant consistency

  **Alternative: Strong Consistency (slow but instant) *CAP therom) \- CP***

  User posts comment:

  ├─ Write to DB

  ├─ Wait for all replicas to update

  ├─ Then confirm to user

  Result: User always sees latest, but slower ❌

**ALWAYS have P (partition tolerance)  in distributed systems.**

**You must choose:**

* **AP:** Available (respond fast) \+ accept stale data  
* **CP:** Consistent (correct data) \+ might be unavailable

**You CANNOT choose:**

* **CA:** Doesn't exist in real distributed systems  
* **No P:** Network failures will break your system

**🎯 Google Interview Questions — speak these out loud:**

* *Your service handles 10k RPS today. How would you design it to handle 10M RPS?*

  ***App Tier:** Stateless design \+ 1000 servers with load balancer (from 1 server).*

  ***Database:***

* *Read scaling: Add 100 read replicas*  
* *Write scaling: Shard database into 1000 shards by user\_id*

  ***Caching:** Redis for hot data (user profiles, posts) → 95% cache hit, reduce DB load.*

  ***Message Queue:** Kafka buffers write surges → prevents DB overload.*

  ***Auto-scaling:** Monitor CPU/memory/QPS → scale up/down automatically.*

  *Result: Distributed load, each component handles manageable traffic."*

* *What's the difference between scaling reads and scaling writes? Give a concrete strategy for each.*

* *A viral post causes a 100x traffic spike in 30 seconds. What breaks first and how do you protect against it?*

  *"Timeline:*

* *5s: Load balancer connection limit reached → new requests rejected*  
* *10s: App server CPU maxed (100%) → requests timeout*  
* *15s: DB connection pool exhausted → queries queue up*  
* *20s: Memory fills, garbage collection pauses → latency spikes*

  ***Protection layers:***

1. *Pre-scaling: Scale at 11AM if spike expected at 12PM*  
2. *Queue buffers (Kafka): Absorb spike, process at normal rate*  
3. *Load shedding: Reject non-critical requests (analytics, logs) → keep critical ones (payments)*  
4. *Rate limiting: 50k RPS per client → prevent single user killing system*  
5. *Circuit breaker: If DB latency \> 5s, stop new queries → fail fast*

   *Result: System absorbs 100x spike without crashing."*

* *You have a stateful service that stores sessions in memory. How does this limit scalability and how do you fix it?*

* *When would you NOT horizontally scale? What are the limits of throwing more machines at a problem?*

  ***Write bottleneck:** Single master DB. Adding 1000 app servers doesn't help (all writes hit 1 master). Fix: Shard database.*

  ***Shared resources:** All servers hit same Redis (maxes at 100k RPS). Fix: Shard Redis (Redis Cluster).*

  ***Stateful services:** Session stored locally. Each user tied to specific server. Fix: Move sessions to Redis (stateless).*

  ***Distributed consensus:** Raft leader handles all writes (followers just replicate). More followers don't increase throughput. Fix: Multiple consensus groups (very complex).*

  ***Message ordering:** Kafka 1 partition \= 1 consumer only (maintain order). Adding servers breaks order. Fix: Shard by key (per-entity order, lose global order).*


  *"**Problem:** 1 partition \= 1 consumer (bottleneck). Multiple consumers break order.*

  ***3 Ways to Handle:***

  ***1\. Single Partition (Maintain global order)***

  *1 partition → 1 consumer instance*

  *├─ Order preserved ✓*

  *├─ Throughput: 100k events/sec (bottleneck) ❌*

  *├─ Use when: Payment processing (critical order needed)*

  ***2\. Shard by Entity (Per-entity order, lose global order)***

  *Partition by user\_id:*

  *├─ Partition 1: User A (1 consumer)*

  *├─ Partition 2: User B (1 consumer)*

  *├─ Partition 3: User C (1 consumer)*


  *Result:*

  *├─ User A's events ordered ✓*

  *├─ User B's events ordered ✓*

  *├─ Global order lost (User B event may process before User A)*

  *├─ Throughput: 1M+ events/sec ✓*

  *├─ Use when: User activity (per-user order matters, global doesn't)*

  ***3\. Async \+ Compensating Transactions (Eventual Consistency)***

  *Accept out-of-order events, fix later:*

  *├─ Event arrives out of order → process immediately*

  *├─ System temporarily inconsistent*

  *├─ Background reconciliation job fixes inconsistencies*

  *├─ Eventually consistent ✓*


  *Example: Bank transfer*

  *├─ Credit event arrives first (+$100)*

  *├─ Debit event arrives second (-$100)*

  *├─ Temporarily: Money imbalanced*

  *├─ Reconciliation: Matches credit+debit, confirms transfer*

  *├─ Eventually: Consistent ✓*


  *Result:*

  *├─ Throughput: 1M+ events/sec ✓*

  *├─ Order ignored, eventual consistency ✓*

  *├─ Use when: Finance, payments (correctness matters, order can be eventual)*

  ***Decision Tree:***

  *Does global order matter?*

  *├─ YES → 1 partition, 1 consumer (slow, ordered)*

  *└─ NO:*

     *├─ Per-entity order needed?* 

     *│  ├─ YES → Shard by key (per-entity ordered)*

     *│  └─ NO → Async \+ compensating (eventually consistent)*

  ***Real World:***

* ***Payments:** Async \+ compensating (Stripe, PayPal)*  
* ***User activity:** Shard by user\_id*  
* ***Critical ledger:** 1 partition (slow but ordered)"*

* *Your database is the bottleneck at 500k users. Walk me through your scaling strategy step by step.*

  *"**Step 1: Diagnose***

* *QPS: 100k/sec (near limit)*  
* *Latency: 100ms (slow)*  
* *Bottleneck: Write-heavy workload, single master*

  ***Step 2: Read scaling***

* *Add 5 read replicas*  
* *Route 80% reads to replicas, 20% to master*  
* *Reduce master load: 100k → 20k QPS*  
* *Latency: 100ms → 40ms*

  ***Step 3: Caching***

* *Redis for hot data (90% of reads)*  
* *10% hit database*  
* *Latency: 40ms → 5ms*

  ***Step 4: Database sharding (Critical for writes)***

* *Shard by user\_id into 5 shards:*  
  * *Shard 1: Users 0-100k (Master 1\)*  
  * *Shard 2: Users 100k-200k (Master 2\)*  
  * *... etc*  
* *Each master: 100k users, 20k QPS (manageable)*  
* *Middleware (shard router) routes: User 150k → Shard 2*

  ***Step 5: Replication per shard***

* *Each shard: 1 Master \+ 2-3 Replicas*  
* *Total: 5 shards × 3 replicas \= 15 DB instances*  
* *Each instance handles light load*

  ***Step 6: Write optimization***

* *Batch 1000 writes together (20x throughput gain)*  
* *Async writes for non-critical data (analytics)*

  ***Step 7: Auto-scaling***

* *If shard QPS \> 50k → add replica*  
* *If latency \> 20ms → shard further*  
* *Monitor replication lag*

  ***Result:***

* *Latency: 100ms → 5ms ✓*  
* *QPS distributed: 100k → 5 shards × 20k each ✓*  
* *Can scale to 5M users (add more shards) ✓"*

## **4\) Availability**

**▸ Basic**

* What is availability — percentage of time a system is operational

* Uptime vs downtime — 99.9% \= \~8.7 hrs/year, 99.99% \= \~52 min/year

* Single point of failure (SPOF) — any component whose failure kills the system

**▸ Intermediate**

* Redundancy — running duplicates so one failure doesn't cause downtime

  Running duplicate copies of critical components (DB replicas, multiple app servers, load balancers). If one fails, others keep system running.

  Single Master DB → Service down if fails

  Master \+ Replica → Replica takes over, no downtime

* Failover mechanisms — automatic switch to backup on primary failure

  Master DB fails → Health check detects → Automatically promote Replica to Master

  No manual intervention needed.

* Failover systems — hot standby (instant), warm (minutes), cold (hours)

  **Failover Systems (3 Types)**

  **Hot Standby (Instant \- \<1 second)**

  Primary actively running

  Standby actively syncing (real-time)

  ├─ Primary fails → switch to Standby immediately

  ├─ Data loss: 0 (fully synced)

  ├─ Cost: 2x resources (both running)

  ├─ Use: Critical systems (payments, banking)

  **Warm Standby (Minutes \- 2-5 min)**

  Primary actively running

  Standby idle (receives backups every 5 min)

  ├─ Primary fails → boot Standby, apply last backup

  ├─ Data loss: Up to 5 minutes

  ├─ Cost: 1.5x resources (standby warming)

  ├─ Use: Medium-importance systems

  **Cold Standby (Hours \- 30 min to 2 hours)**

  Primary actively running

  Standby offline (backups stored on disk)

  ├─ Primary fails → boot Standby, restore from backup

  ├─ Data loss: Hours of data

  ├─ Cost: 1x resources (just storage)

  ├─ Use: Non-critical systems

  **Comparison:**

  System fails 10:00 AM

  ├─ Hot:  10:00:01 \- Back up (1 sec downtime)

  ├─ Warm: 10:05:00 \- Back up (5 min downtime and 5min data loss)

  └─ Cold: 11:00:00 \- Back up (1 hour downtime and 1 hr data loss)

**▸ Advanced**

* Multi-region deployment — geographic redundancy against data center failure

* Active-active vs active-passive — both serving vs one on standby

  **Active-Active vs Active-Passive**

  Active-Passive: DC1 serves traffic, DC2 idle standby. Failover \= downtime.

  Active-Active: DC1 \+ DC2 both serve traffic. One fails → other handles load seamlessly. Zero downtime but complex (distributed writes, consistency).

* High Availability architecture — N+1 redundancy at every layer

  **High Availability (HA) \- N+1 Redundancy**

  For every N components needed, deploy N+1 (1 spare).

  App servers needed: 5 → Deploy 6

  Master DB: 1 → Deploy 1 Master \+ 2 Replicas

  Load balancer: 1 → Deploy 2 (active-active)

  Cache: 1 → Deploy Redis Cluster (multiple nodes)

  Message Queue (Kafka):4 brokers (3 needed, 1 spare)

  Result: Any single component fails, system still runs. No SPOF.

* Disaster recovery planning — RTO (how fast) and RPO (how much data loss)

  **RTO (Recovery Time Objective):** How fast to recover.

  System down 10:00 AM

  RTO \= 5 min → Must be up by 10:05 AM

  **RPO (Recovery Point Objective):** How much data loss acceptable.

  Last backup 9:00 AM

  RPO \= 1 hour → OK to lose 1 hour of data

  RPO \= 5 min → Backup every 5 min (more cost)

  **Trade-off:**

* Hot standby: RTO \= 1 sec, RPO \= 0, Cost \= High  
* Warm standby: RTO \= 5 min, RPO \= 5 min, Cost \= Medium  
* Cold standby: RTO \= 1 hour, RPO \= 1 hour, Cost \= Low

  Pick based on business need.


**🎯 Google Interview Questions — speak these out loud:**

* *What is a SPOF in a typical 3-tier web app? How do you eliminate each one?*

  *  		N+1 redundancy at every layer

* *Explain the difference between active-active and active-passive. When would you choose each?*

		***Active-Passive:***

* *DC1 serves 100% traffic, DC2 idle*  
* *DC1 fails → manual/automatic failover to DC2 (downtime)*  
* *Use: Budget-limited, acceptable downtime*

  ***Active-Active:***

* *DC1 \+ DC2 both serve 50% traffic each*  
* *DC1 fails → DC2 handles 100%, zero downtime ✓*  
* *Use: Mission-critical, high availability needed*  
* *Trade-off: Complex (distributed writes, consistency)*

* *Your system needs 99.99% uptime. What does that mean in terms of allowed downtime per year, and how do you architect for it?*

  *99.99% \= 52.6 minutes downtime/year (365 × 24 × 60 × (1 \- 0.9999) \= 52.6 min)* 

  Architecture: 

  ├─ Hot standby at every layer (RTO \= seconds) 

  ├─ Active-active deployment across 2 DCs 

  ├─ Automated failover (no manual intervention) 

  ├─ Health checks every 10 seconds 

  ├─ Load balancer instant traffic switch 

  └─ Database replication \<100ms lag

  Result: Can afford only \~1 min unplanned downtime/month


* *A data center goes offline. Walk me through how your system should respond automatically.*

  *Timeline:*

  *├─ 0 sec: DC1 master DB stops responding*

  *├─ 10 sec: Health check detects failure*

  *├─ 15 sec: Automatic failover triggered*

  *│  ├─ Promote DC2 replica to master*

  *│  ├─ Update DNS: clients redirected to DC2*

  *│  ├─ Stop writes to old master*

  *├─ 20 sec: Clients see failover (may see stale data \<100ms)*

  *├─ 30 sec: DC2 accepts writes, system back to normal*


  *Automatic (no human needed):*

  *├─ Health checks monitor both DCs*

  *├─ Raft/Paxos consensus: DC2 elected new master*

  *├─ DNS updated automatically*

  *├─ App servers auto-redirect to DC2*


  *Result: \~30 second downtime, fully automatic ✓*

* *What is RTO vs RPO? If a company says they can lose at most 1 hour of data, what does that tell you about your backup strategy?*

  *"Can lose at most 1 hour of data" \= RPO \= 1 hour*


  *What it tells you:*


  *RPO \= 1 hour:*

  *├─ Must backup database every 1 hour (max)*

  *├─ If system fails at 10:59 AM → lose only 59 min data*

  *├─ More frequent backups \= higher cost*


  *RTO not specified:*

  *├─ Assume downtime acceptable (could be hours)*

  *├─ Use cold or warm standby*


  *Backup Strategy:*

  *├─ Automated backup every 1 hour (cron job)*

  *├─ Store on separate DC (disaster recovery)*

  *├─ Test restoration regularly*

  *├─ Monitor replication lag (\<1 hour)*


  *Example:*

  *Last backup: 9:00 AM*

  *System fails: 10:59 AM*

  *├─ Data loss: 1 hour 59 minutes (violates RPO\!)*

  *├─ Solution: Backup every 30 min instead*

## **5\) Reliability**

**▸ Basic**

* What is reliability — system performs correctly over time under expected conditions

* Availability vs Reliability — a system can be available but unreliable (returns wrong data)

```java
// System is UP (available) but returns WRONG data (unreliable)
@GetMapping("/balance")
public double getBalance(String userId) {
    return 0;  // Always returns 0, even if user has $1000
}
// Available: ✓ (returns 200)
// Reliable: ❌ (returns wrong data)
```

**▸ Intermediate**

* Retries \+ timeouts — don't wait forever, retry with exponential backoff

```java
// ❌ BAD: Wait forever
call downstream service
// If slow, entire system hangs
// ✅ GOOD: Timeout + Retry
int maxRetries = 3;
int timeout = 5000;  // 5 seconds
int backoffDelay = 1000;  // 1 second
for (int i = 0; i < maxRetries; i++) {
    try {
        return callWithTimeout(downstream, timeout);
    } catch (TimeoutException e) {
        if (i < maxRetries - 1) {
            Thread.sleep(backoffDelay * (2 ^ i));  // Exponential backoff
        }
    }
}
throw new ServiceUnavailableException();
```

**Exponential Backoff:**

```
Retry 1: Wait 1 second
Retry 2: Wait 2 seconds
Retry 3: Wait 4 seconds
Retry 4: Wait 8 seconds
// Reduces load on failing service ✓
```

* Graceful degradation — serve partial functionality when components fail

**When a component fails, serve partial functionality instead of failing completely.**

```java
// ❌ BAD: All or nothing
public UserProfile getProfile(String userId) {
    User user = userService.getUser(userId);  // If fails, entire page fails
    List<Orders> orders = orderService.getOrders(userId);  // If fails, entire page fails
    return new UserProfile(user, orders);
}
// ✅ GOOD: Graceful degradation
public UserProfile getProfile(String userId) {
    User user = null;
    List<Orders> orders = null;
    try {
        user = userService.getUser(userId);
    } catch (Exception e) {
        user = new User("Unknown");  // Fallback
        logger.error("User service down, using fallback");
    }
    try {
        orders = orderService.getOrders(userId);
    } catch (Exception e) {
        orders = new ArrayList<>();  // Empty list fallback
        logger.error("Order service down, using empty list");
    }
    return new UserProfile(user, orders);  // Return partial data ✓
}
// Result: User sees profile with basic info, orders empty
// Better than showing error page ✓
```

* Fault tolerance — system continues despite component failures

**The system continues working despite component failures.**

```java
// Single point of failure ❌
Database → Your Service → Client
// If DB down, entire service down
// Fault tolerant ✓
Database 1 → Your Service → Client
Database 2 ↗
// If DB1 down, use DB2
@Service
public class UserService {
    private List<UserRepository> replicas;  // Multiple replicas
    public User getUser(String id) {
        for (UserRepository replica : replicas) {
            try {
                return replica.findById(id);
            } catch (Exception e) {
                continue;  // Try next replica
            }
        }
        throw new UserNotFoundException();
    }
}
```

**▸ Advanced**

* Circuit breakers — stop calling a failing service to prevent cascade failure

**java**

```java
// States
enum CircuitState {
    CLOSED,      // Normal, requests go through
    OPEN,        // Failing, requests rejected immediately
    HALF_OPEN    // Testing, allow one request to check if recovered
}
```

* Chaos engineering — intentionally break things to find weaknesses (Netflix's approach)

```java
// Netflix's approach: Chaos Monkey
// Randomly kills instances to test fault tolerance
@EnableChaosMonkey
public class ChaosConfig {
    @Bean
    public ChaosMonkey chaosMonkey() {
        // Kill random instances during business hours
        // See if system handles it gracefully
        return new ChaosMonkey();
    }
}
// Why use it?
// - Find failures BEFORE customers do
// - Test if circuit breakers work
// - Test if failover works
// - Build confidence in reliability
```

* Bulkhead pattern — isolate resources so one failure can't starve others

**Isolate resources so one failure can't starve others.**

```java
// ❌ BAD: Shared thread pool
ExecutorService shared = Executors.newFixedThreadPool(10);
// PaymentService uses all 10 threads
// NotificationService waits (starved) ❌
// ✅ GOOD: Isolated thread pools (Bulkhead)
ExecutorService paymentThreads = Executors.newFixedThreadPool(5);
ExecutorService notificationThreads = Executors.newFixedThreadPool(5);
// PaymentService uses 5 threads
// NotificationService uses 5 threads
// Even if payment slow, notifications still work ✓
@Service
public class PaymentService {
    private ExecutorService executor = Executors.newFixedThreadPool(5);
    public void processPayment(Payment payment) {
        executor.submit(() -> {
            // Process payment
        });
    }
}
@Service
public class NotificationService {
    private ExecutorService executor = Executors.newFixedThreadPool(5);
    public void sendEmail(String email) {
        executor.submit(() -> {
            // Send email
        });
    }
}
// Even if PaymentService overloaded, NotificationService still responsive ✓
```

* Monitoring failures — alert before users notice, not after

**Alert BEFORE users notice, not after.** 

**Prometheus monitors → Alertmanager alerts → On-call engineer responds.**

```java
// ❌ BAD: React after customers complain
// Customer: "Your service is slow"
// You: "Oh, we didn't know"
// ✅ GOOD: Proactive monitoring
@Component
public class HealthMonitor {
    @Scheduled(fixedDelay = 60000)  // Every minute
    public void checkHealth() {
        // Check latency
        if (latency > 1000) {  // > 1 second
            alert("High latency detected");
        }
        // Check error rate
        if (errorRate > 5%) {
            alert("Error rate above threshold");
        }
        // Check disk space
        if (diskUsage > 90%) {
            alert("Disk almost full");
        }
    }
}
// Alert before customers see problems ✓
```

**🎯 Google Interview Questions — speak these out loud:**

* *What is the difference between availability and reliability? Give an example where a system has one but not the other.*

* *A downstream microservice starts timing out. Walk me through what your service should do.***:**

```
1. Set timeout (don't wait forever)
2. Catch TimeoutException
3. Retry with exponential backoff
4. If all retries fail, use circuit breaker
5. Return cached data or gracefully degrade
6. Alert monitoring
```

* *What is a circuit breaker? Draw the state machine — closed, open, half-open. When does each transition happen?*

```
         onSuccess()
            ↓
    ┌────────────────┐
    │    CLOSED      │ ← Normal, requests go through
    │  (working)     │
    └────────────────┘
           ↓ (5 failures)
    ┌────────────────┐
    │     OPEN       │ ← Failing, reject immediately
    │  (failing)     │
    └────────────────┘
           ↓ (timeout 60s)
    ┌────────────────┐
    │   HALF_OPEN    │ ← Testing, allow one request
    │  (recovering)  │
    └────────────────┘
         ↓      ↓
    success   failure
        ↓        ↓
     CLOSED    OPEN
```

**When transitions:**

* **CLOSED → OPEN: 5 consecutive failures**  
* **OPEN → HALF\_OPEN: Timeout (60 seconds)**  
* **HALF\_OPEN → CLOSED: Success (back to normal)**  
* **HALF\_OPEN → OPEN: Failure (still broken)**

* *What is chaos engineering and why does Google/Netflix invest in it? Would you use it in production?*

Intentionally break things to find weaknesses before customers do.

Netflix's approach: Chaos Monkey kills random instances daily.

**Benefits:**

* **Find failures early**  
* **Test circuit breakers work**  
* **Test failover works**  
* **Build confidence in reliability**

**Would I use it in production? Yes, but carefully:**

```
- Use during off-peak hours
- Have kill switches ready
- Start with non-critical services
- Gradually increase blast radius
```

* *Your payment service calls 4 downstream services. One of them is slow. How do you prevent that from making all payments fail?*

**Use Bulkhead Pattern (isolate resources).**

**java**

```java
// ❌ BAD: All share same thread pool
ExecutorService shared = Executors.newFixedThreadPool(10);
// If one service slow, all starve
// ✅ GOOD: Separate thread pools (Bulkhead)
ExecutorService userService = Executors.newFixedThreadPool(3);
ExecutorService inventoryService = Executors.newFixedThreadPool(3);
ExecutorService shippingService = Executors.newFixedThreadPool(2);
ExecutorService paymentService = Executors.newFixedThreadPool(2);
// Each service isolated
// If shipping slow, payment still works ✓
```

**Also use:**

* **Timeouts: Don't wait forever**  
* **Circuit breaker: Stop calling failing service**  
* **Retry logic: Exponential backoff**  
* **Graceful degradation: Return partial data**

## **6\) CAP Theorem**

**▸ Basic**

* Consistency — every read returns the most recent write

```java
// Consistent
Account A: $900 (after $100 withdrawal)
Account B: $1100 (after $100 deposit)
All readers see: A=$900, B=$1100 ✓
// Inconsistent
Some readers see: A=$900, B=$1100
Other readers see: A=$1000, B=$1000 (stale)
```

* Availability — every request gets a response (not an error)

```java
// Available
Your request → System responds (200 OK or data) ✓
// Unavailable
Your request → System responds (500 Error) ❌
```

* Partition Tolerance — system works even if network splits into isolated segments

```
Partition (network down between regions):

US Node A ←── NETWORK DOWN ──→ EU Node B

Partition Tolerant:
- US Node A serves US users ✓
- EU Node B serves EU users ✓
- System survives ✓

NOT Partition Tolerant:
- Entire system crashes ❌
```

* You can only guarantee 2 of 3 simultaneously in a distributed system

**In a distributed system, you can only guarantee 2 out of 3 simultaneously.**

```
Pick 2, lose 1:

C + A = NO P (breaks if partition)
C + P = NO A (becomes unavailable during partition)
A + P = NO C (might be inconsistent during partition)
```

**▸ Intermediate**

* CP systems — choose consistency over availability during partition (e.g. HBase, Zookeeper)

**In distributed systems with multiple nodes across regions:**

* **Network failures WILL happen**  
* **You cannot ignore partitions**  
* **So the real choice is: C vs A (given P always exists)**

```
C vs A during partition:

CP = Consistent but NOT Available
    (sacrifice availability, maintain consistency)

AP = Available but NOT Consistent
    (sacrifice consistency, maintain availability)
```

CP Systems (Consistency Over Availability)

**Choose consistency during partition. Example: HBase, Zookeeper, MongoDB**

**java**

```java
// MongoDB Replica Set (CP)
Primary A (US) ← Writes here
  ↓
Secondary B (EU) ← Read-only
  ↓
Secondary C (Asia) ← Read-only
Normal operation (no partition):
Write to Primary A
Replicates to Secondaries B, C
All consistent ✓
Network partition (A ↔ {B,C} can't talk):
Primary A: "I"m authoritative, accept writes" ✓
Secondaries B,C: "Can"t reach primary, REJECT writes" ❌
Result during partition:
- US region: Writes work ✓ (Primary A)
- EU/Asia: Writes fail ❌ (Secondaries reject)
- Consistent ✓ (only Primary writes)
- NOT Available everywhere ❌
When partition heals:
- Secondaries sync from Primary ✓
- All consistent again ✓
```

**Why CP? Bank ledger needs consistency \> availability.**

* AP systems — choose availability over consistency during partition (e.g. Cassandra, CouchDB)

AP Systems (Availability Over Consistency)

**Choose availability during partition. Example: Cassandra, CouchDB**

**java**

```java
// Cassandra (AP)
Node A (US) ← Master, can write
Node B (EU) ← Master, can write
Node C (Asia) ← Master, can write
Normal operation (no partition):
Write to any node
Replicates to others
All consistent ✓
Network partition ({A,B} vs {C}):
Segment {A,B}:
- Accept writes ✓
- Data: "user withdrew $100" → balance = $900
Segment {C}:
- Accept writes ✓ (doesn"t know about partition!)
- Data: "user deposited $100" → balance = $1100
Result during partition:
- Both segments: Writes work ✓ (Available)
- Data: INCONSISTENT ❌
  - {A,B} has: balance = $900
  - {C} has: balance = $1100
When partition heals (network restored):
- Nodes communicate
- Conflict detected: Two different values
- Resolution by: Last write wins, vector clocks, or merge
- Eventually consistent ✓
Timeline:
10:00:00 - Partition starts
10:00:00-10:05:00 - Nodes operate independently (available, inconsistent)
10:05:00 - Partition heals
10:05:01 - Read-repair kicks in (conflict detected)
10:05:02 - Conflicting values resolved
10:05:03 - Both nodes consistent again ✓
```

**Why AP? Twitter feed needs availability \> consistency.**

* In practice, partition tolerance is non-negotiable in distributed systems — so the real choice is C vs A

**▸ Advanced**

* MySQL/PostgreSQL — CA (single node; no partition tolerance by design)

Database Classifications

MySQL/PostgreSQL \- CA (Single Node)

```java
// Design: Single data center, no partition tolerance
Server A (MySQL) ← Your App
  ↓
Database
Single server = no network partition possible
(Can"t partition if only one machine)
Result:
- C = Consistent ✓ (one copy of data)
- A = Available ✓ (responds to queries)
- P = Not Partition Tolerant ❌ (no redundancy)
When you add replication:
Master (MySQL)
  ↓
Slave (Replica)
Partition possible now, but MySQL doesn"t handle it.
Still considered CA (not designed for P).
Use when: Startup, single data center, no multi-region needs
```

* MongoDB — CP (primary consistency, may reject writes during partition)

MongoDB \- CP (Single Primary)

**java**

```java
// Design: Distributed but single primary
Primary A (US) ← All writes here
  ↓
Secondary B (EU) ← Read-only
  ↓
Secondary C (Asia) ← Read-only
Key insight:
- Secondaries ARE slaves (read-only only)
- ALL writes must go to Primary (even from EU/Asia)
- Users in EU write: Request redirected to Primary in US (slow)
During partition:
- Primary A (US): Accepts writes ✓
- Secondaries B,C (EU/Asia): Reject writes ❌
- Consistent but not available everywhere
Multi-Master option (Sharded):
Shard 1: Primary A1 (US) + Secondaries
Shard 2: Primary A2 (EU) + Secondaries
Shard 3: Primary A3 (Asia) + Secondaries
Each shard has own primary. Still CP within shard.
NOT common because complex + conflicts possible.
Use when: Need consistency > availability
Example: Banking, payments, ledgers
```

* Cassandra — AP (always available, eventual consistency)

Cassandra \- AP (All Equal Masters)

**java**

```java
// Design: All nodes are equal masters
Node A (US) ← Can write
Node B (EU) ← Can write
Node C (Asia) ← Can write
Key insight:
- ALL nodes have COMPLETE data (no sharding)
- Every node can accept writes independently
- No replication lag in normal operation
During partition:
- All segments accept writes ✓
- Conflicts expected (different writes to same data)
Conflict resolution after partition heals:
Method 1: Last Write Wins (LWW)
- Timestamp comparison
- Latest timestamp wins
- Problem: Can lose data if concurrent writes
Method 2: Vector Clocks
- Track causality (which write happened before which)
- Detect concurrent (conflicting) writes
- App resolves conflicts
Method 3: Read-Repair
- During reads, detect inconsistencies
- Fix by copying latest value
- Automatic repair ✓
Example flow:
10:00:00 - Partition
10:00:05 - User in US: "withdraw $100" → balance=$900 (Node A)
10:00:07 - User in EU: "deposit $100" → balance=$1100 (Node B)
10:05:00 - Partition heals, nodes sync
10:05:01 - Read-repair detects: balance=$900 vs $1100
10:05:02 - Resolve: "last write wins" → balance=$1100
10:05:03 - Both nodes consistent ✓
Use when: Need availability > consistency
Example: Twitter feed, recommendations, social media
```

* Real design: choose based on business requirement — bank ledger needs C, social feed can tolerate AP

Real Design: Choose Based on Business Requirement

```
Bank Ledger:
- Need: Strong consistency (CP)
- Use: MongoDB
- Reason: Can't tolerate inconsistent transactions

Social Feed:
- Need: Always available (AP)
- Use: Cassandra
- Reason: Can show stale tweets for 5 seconds

E-commerce Search:
- Need: Available + fast reads (AP)
- Use: Elasticsearch + Cassandra
- Reason: Can show outdated products temporarily
```

* PACELC extension — even without partitions, latency vs consistency tradeoff exists

PACELC Extension

**Even without partitions, another tradeoff exists.**

```
P = Partition (when it happens, C vs A)
A = Availability (always true, given P)
C = Consistency
E = Else (no partition, normal operation)
L = Latency

PACELC theorem:
- During partition: C vs A (CAP)
- Else (no partition): C vs L (consistency vs latency)
```

**Example: Cassandra (AP/EL)**

**java**

```java
// No partition (normal case): AEL
Write request:
1. Write to local node (memory) → FAST ✓
2. Async replicate to other nodes → LATER
User gets confirmation immediately (fast, latency low).
Other nodes sync asynchronously (might be stale, eventual consistency).
Trade: Latency ↓ vs Consistency ↓
```

**Example: MySQL (CA)**

**java**

```java
// No partition (normal case): CA
Write request:
1. Write to primary (disk) → Wait
2. Sync to slave (disk) → Wait
3. Confirm to user → SLOW
User waits for consistency (high latency, consistent).
Trade: Latency ↑ vs Consistency ✓
```

Corrected Decision Tree

```
Q1: How many data centers?
├─ One
│  └─ Use MySQL/PostgreSQL (CA)
│     (Single region, simple)
│
└─ Multiple (global distribution)
   ├─ Q2: Consistency critical?
   │  ├─ YES (banking, payments)
   │  │  ├─ Option 1: MySQL Master + Read Replicas (CP-like)
   │  │  │  └─ Master in US, Replicas in EU/Asia
   │  │  │     EU/Asia users: Slow writes (redirected to US)
   │  │  │     ✓ Consistent, ❌ Not available for writes
   │  │  │
   │  │  ├─ Option 2: MongoDB Replica Set (CP)
   │  │  │  └─ Primary in US, Secondaries in EU/Asia
   │  │  │     Same as MySQL
   │  │  │
   │  │  └─ Option 3: CockroachDB/Spanner (SQL + CP) [arch is same as cassandra but takes C over A, cassandra takes A over C.]
   │  │     └─ Multiple regions, multiple primaries
   │  │        EU users: Fast writes ✓ (local primary)
   │  │
   │  └─ NO (social media, recommendations)
   │     └─ Use Cassandra (AP)
   │        All regions: All can write ✓
```

### Summary Table

| DB | Type | Masters | Partition | During Partition | Use Case |
| ----- | ----- | ----- | ----- | ----- | ----- |
| **MySQL** | **SQL** | **1** | **NO** | **Breaks ❌** | **Single DC** |
| **MongoDB** | **NoSQL** | **1** | **Partial (CP)** | **Reject writes ❌** | **Banking** |
| **Cassandra** | **NoSQL** | **All equal** | **YES (AP)** | **Accept writes ✓** | **Twitter** |
| **CockroachDB** | **SQL** | **Multiple** | **YES (CP)** | **Reject writes ❌** | **Distributed banking** |
| **Google Spanner** | **SQL** | **Multiple** | **YES (CP)** | **Reject writes ❌** | **Global transactions** |

**🎯 Google Interview Questions — speak these out loud:**

* *Explain CAP theorem in plain English. Why can't you have all three?*

**CAP theorem says in a distributed system, you can only guarantee 2 out of 3:**

* **Consistency: every read returns latest data**  
* **Availability: system always responds**  
* **Partition Tolerance: system works if network splits**

**Why can't you have all three?**

**When network partition happens (nodes can't talk):**

```
Option 1: Be Consistent (C)
- Stop accepting writes (wait for network to heal)
- Lose Availability (❌ A)

Option 2: Stay Available (A)
- Keep accepting writes in both segments
- Lose Consistency (❌ C, conflicting data)

Option 3: Ignore partition (ignore P)
- Doesn't work in real systems (partitions happen)
```

**Real world: Partition WILL happen, so you choose C or A.**

* *You're designing a banking system. What CAP choice do you make and why?*

**CP (Consistency over Availability)**

```
Banking requirement: 
- User A transfers $100 to User B
- MUST guarantee: User A's account decremented, User B's incremented
- CANNOT have: Both happen in User A's view, only one in User B's view

Choose CP:
- During partition: Reject writes ❌ (unavailable)
- But: GUARANTEE consistent data ✓

Example:
User A in US: "Transfer $100" → Primary accepts ✓
User B in EU: "Check balance" → Secondary rejects ❌ (partition)

Better than:
User A thinks: "Transfer done" ($100 deducted)
User B thinks: "Transfer failed" (balance same)
Both think different things = disaster ❌

Trade-off: Brief unavailability > data inconsistency ✓
```

**Implementation: MongoDB (CP) or CockroachDB (SQL \+ CP)**

* *You're designing a social media feed. What CAP choice do you make and why? How does that differ from the bank?*

**AP (Availability over Consistency)**

```
Social feed requirement:
- User posts tweet
- Followers see it eventually (within seconds OK)
- CANNOT have: System down (users can't post)

Choose AP:
- During partition: Accept writes everywhere ✓
- Users post always ✓
- Some inconsistency for 5-10 seconds ⚠️ (acceptable)

Example:
User in US: Posts tweet → Node A writes ✓
User in EU: Sees tweet → Node B replicates ✓
User in Asia: Sees tweet → Node C replicates ✓

But during partition:
US segment: Tweet exists
Asia segment: Tweet doesn't exist yet (hasn't synced)

After partition heals: Tweet syncs to Asia ✓ (eventual consistency)

Trade-off: Temporary inconsistency < downtime ✓
```

**Difference from bank:**

```
BANK (CP):
- Consistency critical (money loss = disaster)
- Can tolerate brief unavailability
- Reject writes during partition

SOCIAL MEDIA (AP):
- Availability critical (users expect always-on)
- Can tolerate stale feed for seconds
- Accept writes during partition

Key: Business requirement determines CAP choice.
```

**Implementation: Cassandra (AP)**

* *Cassandra is an AP system. What does that mean practically — what can go wrong and how do you handle it?*

**AP means:**

* **Always Available ✓ (accepts writes everywhere)**  
* **Eventually Consistent (might be inconsistent temporarily)**

**What can go wrong:**

```
Scenario: Account balance

Normal (no partition):
Master A: balance = $1000
Master B: balance = $1000
Master C: balance = $1000

Partition ({A,B} vs {C}):

Segment {A,B}:
User withdraws $500
balance = $500 ✓

Segment {C}:
User deposits $300
balance = $1300 ✓

Both accepted! But different values.

After partition heals:
Conflict: balance = $500 vs $1300

Problem:
- User thinks balance is $500 (withdrew)
- Another copy says $1300 (deposited)
- Who is right? Both are! ❌
```

**How to handle it:**

**Method 1: Last Write Wins (Automatic)**

**java**

```java
// Cassandra picks by timestamp
balance = $500 (timestamp: 10:00:07)
balance = $1300 (timestamp: 10:00:05)

Winner: $500 (latest timestamp)

Problem: Might lose legitimate transaction ❌
```

**Method 2: Read-Repair (Automatic)**

**java**

```java
// During reads, detect and fix

User reads balance:
- From A: $500
- From B: $1300
- Cassandra detects mismatch
- Picks winner (by timestamp or version)
- Writes to loser: $500
- Next read: both have $500 ✓

Automatic repair (eventual consistency) ✓
```

**Method 3: Application Logic (Manual)**

**java**

```java
// Store both versions, app resolves

balance_A = $500 (user withdrew)
balance_B = $1300 (user deposited)

Application: "Both transactions are real"
Real balance = $1000 - $500 + $300 = $800

OR: "Manual review needed"
System: Alert support team
Support reviews transaction log
Confirms both, fixes balance

Manual resolution ✓
```

**Practical impact:**

```
FOR SOCIAL MEDIA (Cassandra OK):
- Like count: $500 vs $1300 inconsistency
- "Last write wins" acceptable ✓
- Users see eventual count after seconds ✓

FOR BANKING (Cassandra BAD):
- Balance: $500 vs $1300 inconsistency
- User loses money or gains fake money ❌
- Need CP (MongoDB) instead ✓
```

* *A network partition splits your cluster. In a CP system, what happens to requests? In an AP system?*

CP System (MongoDB)

```
Setup:
Primary A (US)
Secondary B (EU)
Secondary C (Asia)

Partition: {A} vs {B,C}

US requests:
- Write to Primary A ✓
- Succeeds
- Replicates to B, C (but blocked by partition)

EU/Asia requests:
- Try to write to Secondary B/C
- Secondaries reject ❌
- Error: "Primary unavailable"

User experience:
US: "Your write succeeded" ✓
EU: "System temporarily unavailable" ❌

Result:
- Consistent ✓ (only Primary writes)
- NOT Available (EU/Asia down) ❌
```

AP System (Cassandra)

```
Setup:
Node A (US) - equal master
Node B (EU) - equal master
Node C (Asia) - equal master

Partition: {A,B} vs {C}

US/EU requests:
- Write to A or B ✓
- Succeeds
- Replicates within {A,B} segment

Asia requests:
- Write to C ✓
- Succeeds
- Replicates within {C} segment

User experience:
US/EU: "Your write succeeded" ✓
Asia: "Your write succeeded" ✓

After partition heals:
A-B data: "user withdrew $100"
C data: "user deposited $200"
Conflict detected, resolved (read-repair)

Result:
- Available ✓ (all regions work)
- NOT Consistent (conflicting writes) ❌ (temporarily)
- Eventual Consistency ✓ (after heal)
```

**Key Difference:**

```
CP (MongoDB):
Partition → Some regions unavailable ❌

AP (Cassandra):
Partition → All regions available ✓, conflicts later
```

* *What is PACELC and why is it considered more realistic than CAP?*

**PACELC \= PArtition / eLse / Latency vs Consistency**

```
CAP only covers: When partition happens (C vs A)
PACELC covers: Both cases:
1. During partition: C vs A
2. No partition: C vs L (Latency)
```

**Why more realistic?**

**CAP assumes:**

```
Choose 2 out of 3.
Everything else optimal.

But reality:
- Partitions rare (maybe 1% of time)
- 99% of time: no partition

In that 99%: Still tradeoff!
- Consistency vs Latency (speed)
```

**Example: Cassandra (AP/EL)**

```
99% of time (no partition):

Write request:
1. Write to node in memory ✓ (FAST, latency ↓)
2. Async replicate (LATER, might be stale, consistency ↓)

User gets response immediately (quick).
But other nodes might be stale for milliseconds.

PACELC choice: Else (no partition) → L (latency wins)
Trade: Fast writes > consistency

CAP didn't explain this!
```

**Example: MySQL (CA)**

```
99% of time (no partition):

Write request:
1. Write to primary (disk) → WAIT
2. Confirm to slave (disk) → WAIT
3. Return to user → SLOW

User waits for consistency.
But writes are SLOW (high latency).

PACELC choice: Else (no partition) → C (consistency wins)
Trade: Consistency > fast writes

CAP didn't explain this!
```

**PACELC Summary:**

```
CAP: "Pick 2 out of C, A, P"
PACELC: "Pick 2 out of C, A, P DURING partition,
         AND pick 1 out of C, L when NO partition"

More complete picture ✓
```

**Real-world implication:**

```
When choosing database, ask:

1. Partitions critical? (P always assume yes)
   → CP or AP?

2. If CP: What about latency? (consistency vs speed)
3. If AP: What about latency? (availability vs speed)

PACELC makes you think about BOTH scenarios.
```

**CockroachDB/Spanner vs Cassandra \- How They Prioritize C over A**

---

CockroachDB/Spanner (CP \- Prioritizes Consistency)

```
During partition: {A,B} vs {C}
Write to B:
1. Check: "Can I reach quorum (majority)?" 
2. A reachable? YES ✓ [basically we have A and B (2) out of 3 in this broken partition]
3. Quorum available (2/3)? YES ✓
4. Accept write ✓
Write to C:
1. Check: "Can I reach quorum?"
2. A,B reachable? NO ❌ (partition blocks)
3. Quorum available (1/3)? NO ❌ [only C in this broken partetion]
4. REJECT write ❌
Result: C sacrifices availability to stay consistent
```

---

Cassandra (AP \- Prioritizes Availability)

```
During partition: {A,B} vs {C}
Write to B:
1. Check anything? NO
2. Just accept ✓
Write to C:
1. Check anything? NO
2. Just accept ✓
Result: C accepts writes, but creates conflicts
```

---

Simple Answer

```
CockroachDB: "Can I verify this is safe? 
              No → REJECT (lose availability)"

Cassandra: "Just accept it! 
            Fix conflicts later (lose consistency)"
```

## **7\) Back-of-the-Envelope Calculations**

**▸ Basic**

* Estimate DAU (Daily Active Users) → RPS \= DAU × avg\_requests / 86400

1\. RPS (Requests Per Second)

**What: How many requests hit your server per second?**

**Why: Tells if one server is enough or need 100 servers.**

```
1K RPS = One server ✓
1M RPS = Need 1000 servers ❌
```

**How to estimate:**

```
RPS = (Daily Active Users × avg requests per user) / 86400 seconds

Example: Twitter
- 300M users active today
- Each user reads 100 tweets
- Total: 300M × 100 = 30B requests
- Per second: 30B / 86400 = 347k RPS

Meaning: 347,000 requests hit Twitter servers EVERY SECOND
```

* Key numbers: 1M RPS \= serious scale, 1K RPS \= manageable single server

* Storage: 1 tweet \= \~280 bytes, 1 photo \= \~200KB, 1 video minute \= \~50MB

2\. Storage (How much hard disk?)

**What: Total data you need to store.**

**Why: Know if 1TB or 100PB needed.**

```
1 tweet = 280 bytes (very small)
1 photo = 200 KB (medium)
1 video min = 50 MB (large)
```

**Example:**

```
If 1M users each upload 1 photo:
1M × 200KB = 200GB needed

If 1B users each upload 1 photo:
1B × 200KB = 200PB needed

That's HUGE!
```

**▸ Intermediate**

* Bandwidth \= RPS × avg\_response\_size (e.g. 10k RPS × 10KB \= 100MB/s)

3\. Bandwidth (How fast network?)

**What: Speed of data flowing through network pipes.**

**Why: Network can be bottleneck (limit RPS).**

```
Bandwidth = RPS × response_size

Example:
- 10k RPS (requests per second)
- Each response: 10 KB

Total bandwidth: 10k × 10KB = 100 MB/s

Can your network handle 100MB/s?
- Typical network card: 10 Gbps = 1.25 GB/s ✓ (yes, easily)
- If you need 10 Tbps: Need CDN or compression ❌
```

* Storage estimation \= daily\_writes × avg\_size × retention\_years

* Latency targets: \<100ms for reads, \<500ms for writes at p99

4\. Latency (How slow?)

**What: Time for one request to complete.**

**Why: Users expect fast responses.**

```
Target: Read <100ms, Write <500ms

If response takes 5 seconds: Users angry ❌
If response takes 50ms: Users happy ✓
```

**▸ Advanced**

* Capacity planning — how many servers, DB nodes, cache nodes do you need?

5\. Capacity Planning (How many servers?)

**What: Count servers/databases/caches needed.**

**Why: Know infrastructure cost.**

```
1 server can handle 1000 RPS

If you need 100k RPS:
100k / 1000 = 100 servers needed

Cost: 100 × $500/month = $50k/month
```

* Bottleneck prediction — is it CPU, memory, disk I/O, or network?

* Key ratios: 80/20 read-write rule, hot key concentration, peak-to-average ratio (often 2–5x)

6\. Peak Ratio (Traffic spikes)

**What: Max traffic / average traffic.**

**Why: Can't provision for average (system crashes at peak).**

```
Average: 20k RPS
Peak (Super Bowl): 100k RPS

Peak ratio: 100k / 20k = 5x

Must provision for 100k RPS, not 20k.
```

Now Practice: One Full Example

**Scenario: YouTube**

```
Question: YouTube has 100M daily users.
Each watches 10 videos per day.
Each video response is 5KB.
Estimate RPS and bandwidth.

Step 1: Calculate RPS
RPS = (100M × 10) / 86400
    = 1B / 86400
    = 11,574 RPS ≈ 12k RPS

Meaning: 12,000 video requests per second hit YouTube

Step 2: Calculate Bandwidth
Bandwidth = 12k RPS × 5KB
          = 60,000 KB/s
          = 60 MB/s

Meaning: 60 MB of data flows per second

Step 3: Reality check
60 MB/s × 86400 sec/day = 5.2 TB/day
YouTube serves 5.2 TB daily (reasonable ✓)

Network card can handle 1.25 GB/s, so 60 MB/s is fine ✓
```

**🎯 Google Interview Questions — speak these out loud:**

* *Twitter has 300M DAU. Each user reads 100 tweets and writes 0.1 tweets per day. Estimate the read and write RPS.***:**

```
Read RPS:
RPS = (300M DAU × 100 reads) / 86400
    = 30B / 86400
    = 347,222 RPS ≈ 350k RPS

Write RPS:
RPS = (300M DAU × 0.1 writes) / 86400
    = 30M / 86400
    = 347 RPS ≈ 350 RPS

Result:
- Read: 350k RPS
- Write: 350 RPS
- Ratio: 1000:1 (extremely read-heavy)

What this means:
- 350k read requests/second = need heavy caching + CDN
- 350 writes/second = 1 database can handle easily
- Design: Cache-first architecture ✓
```

* *You're storing 5 years of user photos for 50M users, each uploading 2 photos/day at 200KB each. How much storage?*

```
Daily photo uploads:
50M users × 2 photos/day = 100M photos/day

Total photos over 5 years:
100M photos/day × 365 days × 5 years = 182.5B photos

Storage needed:
182.5B photos × 200 KB/photo = 36.5 PB (Petabytes)

Convert to TB:
36.5 PB = 36,500 TB

What this means:
- 36.5 PB = HUGE storage needed
- Cost: ~$730K/month on AWS S3
- Solution: Compress images (50%) → 18.25 PB ✓
```

* *Your read RPS is 500k. Each response is 20KB. What bandwidth does your CDN need to handle?***:**

```
Bandwidth = RPS × response_size
          = 500k RPS × 20 KB
          = 10,000,000 KB/s
          = 10,000 MB/s
          = 10 GB/s
          = 80 Gbps

Reality check:
- Single network card: ~10 Gbps
- You need: 80 Gbps / 10 Gbps = 8 network cards per server ❌

This is TOO MUCH. Solutions:

1. Compression:
   - 20 KB → 5 KB (75% reduction)
   - Bandwidth: 20 Gbps (manageable) ✓

2. CDN:
   - Cache at edge (don't hit origin server for every request)
   - Only 20% reach origin server
   - Origin bandwidth: 20 Gbps × 20% = 4 Gbps ✓

3. Combined:
   - Compress + CDN = 4 Gbps at origin
   - Manageable with 1 server having multiple network cards ✓

Answer: Need CDN + compression to handle 500k RPS at 20KB response.
```

* *A single MySQL instance handles \~5k writes/second. At what user scale do you need to shard?***:**

```
MySQL capacity: 5k writes/second

Formula:
RPS = DAU / 86400
5000 = DAU / 86400
DAU = 5000 × 86400 = 432M users

Assumptions:
- 1 write per user per day (conservative)

Answer: Single MySQL instance works up to ~432M DAU

Beyond 432M DAU: Need to shard

Sharding strategy:
- Shard by user_id
- 2 shards: 5k / 2 = 2500 writes per shard ✓
- 4 shards: 5k / 4 = 1250 writes per shard ✓

Example:
- Users 1-216M: Shard 1 (Master + Replicas)
- Users 216M-432M: Shard 2 (Master + Replicas)
```

* *You expect 10x traffic spikes during events. Your baseline is 20k RPS. How many servers do you provision and why?*

```
Baseline RPS: 20k
Peak RPS: 20k × 10 = 200k

Server capacity: 1000 RPS per server

Servers needed for baseline:
20k / 1000 = 20 servers

Servers needed for peak:
200k / 1000 = 200 servers

Two approaches:

Approach 1: Provision for peak (200 servers)
- Cost: 200 × $500/month = $100K/month
- Benefit: Never crashes ✓
- Downside: Waste money 95% of time ❌

Approach 2: Provision for baseline + auto-scale
- Base: 20 servers
- Auto-scale to 200 during peak
- Cost: Lower, scales dynamically ✓
- Downside: Might crash if scale takes 5+ min ⚠️

Best answer:

"Provision 20 base servers + auto-scale up to 200.
For known events (Super Bowl, New Year): Pre-scale 30 min before.
This balances cost and reliability."

Why:
- Auto-scaling takes 5-10 minutes
- For predicted events, pre-scale (0 downtime) ✓
- For unpredicted spikes, auto-scale catches it ✓
```

# **PHASE 3 — NETWORK & TRAFFIC MANAGEMENT**

## **8\) DNS** *[DNS\_notes](https://docs.google.com/document/d/1JCkj_ea1Q33RbAW3Q4RNe6h-oUeq5QwriU6HrxdzBBo/edit?tab=t.0)*

**▸ Basic**

* DNS translates domain names (google.com) into IP addresses

* Recursive resolver → root nameserver → TLD nameserver → authoritative nameserver

**▸ Intermediate**

* DNS caching — TTL controls how long records are cached (lower TTL \= faster failover, more queries)

* Resolution chain: browser cache → OS cache → ISP resolver → root → authoritative

**▸ Advanced**

* Geo-based routing — route users to nearest data center via DNS (latency-based)

* DNS load balancing — return multiple A records; client picks one (simple round-robin)

* Weighted routing — send 10% to new region for canary deployments

**🎯 Google Interview Questions — speak these out loud:**

* *Walk me through what happens from the moment a user types google.com to when they see the page.*

* *Your TTL is set to 86400 (24 hours). You need to do an emergency failover. What's the problem?*

* *How can DNS be used as a primitive load balancer? What are its limitations compared to a real load balancer?*

* *You want users in Asia to hit your Asia data center and users in the US to hit your US data center. How do you achieve this at the DNS level?*

## **9\) Load Balancers [ComputerNetworking\_Load Balancing-CompleteNotes](https://docs.google.com/document/d/1OWpG6ZK3yyOcYr4dmBBvaL7k4mCXG2rSNkrcWHoER8Q/edit?tab=t.0)**

**▸ Basic**

* Distributes incoming requests across multiple servers to prevent overload

* Round-robin: each server gets requests in turn. Simple, no state needed

**▸ Intermediate**

* Layer 4 LB — routes by TCP/IP (fast, no content inspection)

* Layer 7 LB — routes by HTTP content, headers, URL (smarter, more features)

* Sticky sessions — route same user to same server (needed for stateful apps, problematic for scaling)

**▸ Advanced**

* Consistent hashing — when a node is added/removed, only K/N keys need remapping

* Health checks — LB pings servers; removes unhealthy nodes from rotation

* Failover routing — if primary region is unhealthy, route to secondary

**🎯 Google Interview Questions — speak these out loud:**

* *What is the difference between L4 and L7 load balancing? When would you choose one over the other?*

* *What is consistent hashing? Why is it better than modular hashing when scaling out servers?*

* *You need sticky sessions for your app. What problem does this create for scaling, and how do you mitigate it?*

* *Your load balancer itself becomes a SPOF. How do you make the load balancer highly available?*

* *A health check takes 30 seconds to detect a downed server. What happens to user traffic during those 30 seconds?*

# **PHASE 4 — DATA & STORAGE**

## **10\) Databases — SQL**

**▸ Basic**

* Tables, rows, columns — structured relational data

* Primary keys — unique row identifier; Foreign keys — link between tables

* Indexes — B-tree structures that speed up reads at the cost of slower writes

**Indexes (B-Tree Structure)**

```
Problem: Query "SELECT * FROM users WHERE email = 'ali@test.com'"
Without index: Scan all 1 billion rows ❌ (slow)

Solution: Create index on email column using B-Tree

B-Tree structure:
        [M-Z]
       /     \
     [A-L]   [N-Z]
     /   \    /   \
   [A]  [L] [N]  [Z]

B-Tree organizes data hierarchically (tree structure).
Binary search through tree: O(log N) instead of O(N).

Speed up reads:
- Index on email: O(log N) lookup ✓
- Find ali@test.com in 30 steps instead of 1 billion

Why slow for writes?

Write operation: INSERT new user

Without index:
1. Insert row at end of table
2. Done ✓ (1 operation)

With index:
1. Insert row at end of table
2. Update B-Tree (find correct position in tree)
3. Rebalance tree (might need to shift nodes)
4. Update multiple tree nodes
5. Done (5+ operations)

Cost: INSERT now takes 5-10x longer.

Trade-off:
✓ Reads: 1000x faster (1B → log B)
✗ Writes: 5-10x slower
✓ Net: Worth it for read-heavy workloads (95% reads)
```

**▸ Intermediate**

* Joins — combine rows from multiple tables (expensive at scale) 

  joins multiply and hence become expensive 

* ACID transactions — Atomicity, Consistency, Isolation, Durability

**ACID Transactions**

```
Atomicity: All or nothing (entire transaction or nothing)
Consistency: DB rules maintained (constraints, foreign keys)
Isolation: One transaction doesn't affect another
Durability: Committed data persists (survives crash)

Example:
Transfer $100 from Ali's account to Bob's
Debit Ali: -$100
Credit Bob: +$100

If crash between them:
✓ ACID: Both or neither (not one without other) ✓
✗ No ACID: Ali loses $100, Bob gets nothing ❌
```

* Indexing strategy — index columns you filter/sort on; over-indexing slows writes

**Indexing Strategy**

```
Index columns you filter/sort on.
SELECT * FROM users WHERE country = 'India' → Index on country
SELECT * FROM orders ORDER BY created_at → Index on created_at

Over-indexing problem:
- 1 index: Reads faster, writes ~5x slower ✓
- 10 indexes: Reads much faster, writes 50x slower ❌
- Every index must be updated on write

Rule: Index ~2-5 columns, not 20
```

**▸ Advanced**

* Read replicas — copy of primary DB for read traffic; may have replication lag

* Master-slave replication — primary accepts writes, replicas get changes asynchronously

* When SQL breaks down: joins across shards, schema migrations at scale, write throughput limits

**When SQL Breaks Down**

```
1. Joins across shards:
   Shards: User data split by user_id (Shard 1: users 1-1M, Shard 2: users 1M-2M)
   Problem: Query "get all orders for user X" requires joining shards
   Solution: Denormalize or use NoSQL

2. Schema migrations at scale:
   Migrate 1 billion rows in production
   ALTER TABLE users ADD COLUMN age INT
   Problem: Locks table, downtime
   Solution: Copy data offline, switch gradually

3. Write throughput limits:
   SQL: Single master bottleneck (~5k writes/sec)
   Problem: Millions of writes/sec needed (Twitter tweets)
   Solution: Use NoSQL (Cassandra: millions writes/sec)
```

**🎯 Google Interview Questions — speak these out loud:**

* *Your SQL query is slow. Walk me through how you would diagnose and fix it.*

```
Steps:
1. EXPLAIN query: See execution plan
   EXPLAIN SELECT * FROM users WHERE country = 'India' ORDER BY created_at
   
2. Check: Is country indexed? Is created_at indexed?
   If not → Add indexes
   
3. Check: Is there a full table scan (expensive)?
   EXPLAIN shows: "Seq Scan on users" ❌ (bad)
   
4. Check: Are you joining unnecessarily?
   If 3 JOINs, can you reduce to 1?
   
5. Solutions:
   ✓ Add index on filter columns
   ✓ Add index on sort columns
   ✓ Remove unnecessary JOINs
   ✓ Partition data (if very large)
   ✓ Add caching (Redis)
   
6. Test: Verify query now runs in <100ms

Real example:
Slow: SELECT * FROM orders WHERE user_id = 1 AND created_at > NOW() - INTERVAL 7 DAY
      → Full table scan (10B rows)
      
Fast: INDEX on (user_id, created_at)
      → Binary search in tree (1000 rows)
      → 10,000x faster
```

* *What does ACID mean? Which of the four properties is hardest to maintain in a distributed system and why?*

```
ACID = Four properties:

A - Atomicity: All or nothing
    Example: Money transfer (debit + credit both succeed or both fail)
    
C - Consistency: Rules maintained
    Example: user_id unique constraint
    
I - Isolation: One transaction doesn't see other's partial work
    Example: Transaction 1 writes, Transaction 2 reads only after commit
    
D - Durability: Persists after commit
    Example: Committed data survives power loss

Hardest in distributed system: ISOLATION

Why?
- Single server: Easy (use locks)
- Distributed: Two servers, one transaction spans both
  Server-1: Writes user data
  Server-2: Writes order data
  
  What if Server-1 crashes mid-transaction?
  Server-2 already wrote, but Server-1's changes lost
  Inconsistency!

Solution: 2-Phase Commit (complex, slow, not used in modern systems)
          Use eventual consistency instead (NoSQL)

Answer: Isolation is hardest. Requires coordination across nodes.
```

* *You have a read-heavy workload (95% reads). How do you scale your SQL database?*

```
Approach 1: Read Replicas (Best for SQL)
Master: Accepts writes
Replicas: Read-1, Read-2, Read-3, Read-4, Read-5 (5 copies for reads)

Master: 100 writes/sec
Each replica: 1000 reads/sec
Total capacity: 100 writes + 5000 reads ✓

Cost: Storage (5 copies)
Trade-off: Replication lag (100ms old data acceptable for 95% reads)

Implementation:
- MySQL Master-Slave replication
- Application sends: Writes → Master, Reads → Any Replica

Approach 2: Caching (If reads are repeated)
Cache hot data in Redis
- User profile reads: 90% from cache (instant)
- Write updates: Invalidate cache, update DB
- Result: 100x faster ✓

Combination:
- Cache hot data (Redis)
- Read replicas for warm data
- Master for writes
= Highly scalable read-heavy system ✓
```

* *What is replication lag and why is it dangerous? Give an example where it causes a real bug.*

```
Replication lag: Delay between master write and replica read

Master: user.name = "Ali"
Network delay: 100ms
Replica: user.name = "old_name" (not yet updated)

Real bug example:

User changes password:
1. User submits password change
2. Master receives: UPDATE users SET password_hash = 'new_hash' WHERE user_id = 1
3. Master confirms: "Password changed" ✓
4. Meanwhile, replica still has old password_hash (100ms delay)

5. User tries to login immediately:
   - Login server reads from replica
   - Replica: password_hash = 'old_hash'
   - User's new password doesn't match
   - Login fails! ❌ (But password was just changed)

User thinks: "Password change didn't work!"
Actually: Replication lag caused stale read

Solution:
- Read password from MASTER (not replica) after write
- Or add: "After write, wait 200ms before reading" (bad UX)
- Or use: Strong consistency (wait for replica to catch up)
```

* *At what point does SQL stop being the right choice? What signals tell you it's time to move to NoSQL or shard?*

```
Signals to switch to NoSQL or shard:

Signal 1: Write throughput
SQL limit: ~5,000 writes/sec per master
Needed: 1 million writes/sec
Example: Twitter tweets, Facebook posts
Solution: NoSQL (Cassandra does 1M writes/sec)

Signal 2: Scale beyond 1TB
SQL: Scaling gets complex (sharding)
NoSQL: Built for horizontal scale
Solution: Switch to NoSQL or shard

Signal 3: Complex queries decrease
SQL benefit: Powerful JOINs across tables
If queries are simple key-value lookups
Example: Get user profile by user_id (no JOINs needed)
Solution: NoSQL is simpler, faster

Signal 4: Flexible schema needed
SQL: Fixed schema (ALTER TABLE is slow)
NoSQL: Add fields anytime
Example: A/B testing (some users have feature X, some don't)
Solution: NoSQL handles this easily

Decision:
✓ Use SQL: < 1 billion rows, complex queries, < 10k writes/sec, ACID critical
✗ Use NoSQL: > 10k writes/sec, simple queries, flexible schema, massive scale
```

* *Explain the difference between clustered and non-clustered indexes.*

```
Clustered Index: The actual data is sorted by this index
- One per table (data itself is the index)
- Usually: Primary key (user_id)
- Entire row is stored in sorted order by clustered index

Example:
Clustered on user_id:
[user_id: 1, name: Ali, email: ali@test.com]
[user_id: 2, name: Bob, email: bob@test.com]
[user_id: 3, name: Carol, email: carol@test.com]

Data is physically sorted by user_id ✓

Non-Clustered Index: Separate index pointing to data
- Many per table (don't affect data storage)
- Example: Index on email
- Index stores: (email, pointer_to_user_id)

Email index:
ali@test.com → user_id: 1
bob@test.com → user_id: 2
carol@test.com → user_id: 3

Then look up user_id in clustered index (extra step)

Speed:
Query by user_id: Clustered index (fastest, data is sorted)
Query by email: Non-clustered index (fast, points to user_id, then to data)

Cost:
Clustered: No extra storage cost (data is index)
Non-clustered: Extra storage (copy of data pointers)

Best practice:
- Clustered: Primary key (user_id)
- Non-clustered: Filter columns (country, email)
```

## **11\) Databases — NoSQL**

**▸ Basic**

* When to use NoSQL: flexible schema, massive scale, simple access patterns, high write throughput

```
Problem SQL solves: Complex structured data with JOINs
Cost: Single master bottleneck, limited writes

NoSQL solves: Massive scale, simple access patterns

Flexible Schema:
SQL: Must define schema upfront
     ALTER TABLE users ADD COLUMN age INT (locks table, slow)
     
NoSQL: Add fields anytime
       {user_id: 1, name: "Ali", age: 25} (schema-less)
       Later: {user_id: 2, name: "Bob"} (no age field, OK)
       
Benefit: Iterate fast, A/B testing easy ✓

Massive Scale:
SQL: Single master writes ~5k/sec
NoSQL: Distributed writes across 1000 nodes = 1M/sec

How?
- Partitioning: Data split across many nodes
- No master bottleneck: Any node accepts writes
- Eventual consistency: Tradeoff strong consistency for speed

Simple Access Patterns:
NoSQL assumes: You don't need complex JOINs
Just key-value lookups
Example: Get user profile by user_id (no JOINs)

If you need JOINs:
SQL: Easy (JOIN syntax)
NoSQL: Hard (must denormalize or query app-side)

High Write Throughput:
SQL: Replication lag + complex writes = slow
NoSQL: Optimized for writes
- Cassandra: Append-only writes (no update-in-place)
- DynamoDB: Distributed writes across partitions
```

**▸ Intermediate**

* Key-value stores (Redis, DynamoDB) — fastest, simplest, no query flexibility

```
Fastest, simplest storage.

Redis: In-memory, single machine
- GET key → O(1) instant
- SET key value → O(1) instant
- No querying: Only exact key lookups
- Use: Caching, leaderboards, sessions

DynamoDB: Distributed key-value (AWS)
- GET user_id:1 → Instant
- Partition key (user_id) determines shard
- No complex queries (no SQL)
- Use: User profiles, session storage
```

* Document stores (MongoDB) — JSON documents, flexible schema, decent query support

```
Store JSON documents instead of rows.

SQL:
users table: [user_id, name, email, age]

MongoDB:
{
  _id: 1,
  name: "Ali",
  email: "ali@test.com",
  age: 25,
  preferences: {
    theme: "dark",
    notifications: true
  },
  tags: ["developer", "gamer"]
}

Flexibility: Add preferences anytime (no schema migration)
Query: db.users.find({name: "Ali"})

Cost: Less efficient than SQL for JOINs (must denormalize)
```

* Wide-column (Cassandra, HBase) — optimized for time-series and heavy writes

```
Optimized for time-series and writes.

Structure:
Row: user_id:1
Columns: 2024-01-01 → {value: 100}, 2024-01-02 → {value: 150}, ...

Benefit: Millions of writes/sec (append-only)
Use: Metrics, logs, events

Trade-off: Read queries must specify exact column (no flexible scanning)
```

* Graph DBs (Neo4j) — optimized for relationship traversal

```
Optimized for relationships.

Social graph: Alice → follows → Bob → follows → Carol

Query: "Find all people Bob follows who also follow Alice" (instant)

SQL: Complex JOINs (slow)
Graph: Native traversal (fast)

Trade-off: Only useful if relationships are main concern
```

**▸ Advanced**

* Eventual consistency — reads may return stale data; acceptable for many use cases

```
Strong consistency: Every read returns latest write immediately
SQL default: Every read consistent

Eventual consistency: Reads may be stale temporarily
NoSQL default: For speed

Example:
Write: Alice sends $100 to Bob
Master: Alice -$100, Bob +$100

Alice checks balance immediately: -$100 ✓ (strong)
Bob checks balance in 100ms: Still +$0 ❌ (stale, not yet replicated)
Bob checks balance in 1sec: +$100 ✓ (eventually consistent)

Where NoSQL wins:
"Get user's tweets" - OK if 100ms stale ✓
"Get account balance" - NOT OK to be stale ❌

Real impact:
99.9% of queries: Eventual consistency OK
0.1% of queries: Need strong consistency (use Redis, or wait)
```

* Partition tolerance — NoSQL systems are designed to scale horizontally across many nodes

```
Remember: CAP = Consistency, Availability, Partition
Only 2 of 3 possible. NoSQL chooses: Availability + Partition

What this means:
- NoSQL is Availability + Partition (AP)
- During network partition: All nodes continue accepting writes
- Result: Consistency delayed (eventual)

Contrast SQL (CP):
- Network partition: Master rejects writes (stay consistent)
- Result: Unavailable ❌

NoSQL is built for AP:
✓ Always available
✓ Survives network split
✗ Consistency delayed
```

* Tunable consistency in Cassandra — QUORUM reads/writes for stronger guarantees

```
Cassandra default: Write to any node, replicate later (AP)
Risk: Strong consistency lost

Solution: QUORUM reads/writes

Write with QUORUM:
- Write to majority of replicas before confirming
- Example: 5 replicas, need 3 to confirm write
- Now: Write survives 2 node failures ✓

Read with QUORUM:
- Read from majority
- Now: Always get latest write (strong consistency!) ✓

Cost: Slower (must coordinate with multiple nodes)
Benefit: Strong consistency when needed

Trade-off:
Loose consistency: Faster (any node)
QUORUM consistency: Slower but safer (majority)
```

**🎯 Google Interview Questions — speak these out loud:**

* *You need to store 10 billion user events per day. Would you use SQL or NoSQL? Which specific NoSQL type and why?*

```
10 billion events/day:
= 115,000 events/second (RPS)

SQL capacity: ~5,000 writes/sec (even with optimization)
NoSQL capacity: 1,000,000+ writes/sec ✓

Answer: NOSQL (clearly)

Which type?

Option 1: Cassandra (Wide-Column)
✓ Millions writes/sec
✓ Time-series optimized (events have timestamp)
✓ Append-only (no updates, pure writes)
Use: Facebook, Netflix for events/metrics

Option 2: DynamoDB (Key-Value)
✓ Millions writes/sec
✓ Partition key: (user_id, timestamp)
✓ Simple lookups
Use: Events stored by user_id

Option 3: MongoDB (Document)
✗ Can handle 115k writes/sec but not ideal for pure events
✗ Better for flexible schema, not high-volume time-series

Best choice: Cassandra (designed exactly for this)

Implementation:
CREATE TABLE events (
  user_id UUID,
  timestamp TIMESTAMP,
  event_type TEXT,
  data TEXT,
  PRIMARY KEY (user_id, timestamp)
);

INSERT 115k/sec: Cassandra handles easily ✓
```

* *What is eventual consistency? Give an example where a user would actually notice it in a product.*

```
Eventual consistency: Reads may see stale data temporarily

Where user NOTICES:

Bad example (user notices):
1. User updates profile picture
2. User refreshes page immediately
3. Sees OLD picture (replica lag) ❌ Confusing!

Good example (user doesn't notice):
1. User posts tweet
2. Friend sees tweet after 100ms (eventual consistency)
3. User doesn't know/care ✓

Examples where user NOTICES:
- Update profile → Immediately refresh → See old data ❌
- Add money to account → Check balance → See old balance ❌
- Delete comment → See it still there ❌

Solutions:
- Write to cache, read from cache immediately (no DB read) ✓
- Or write to master, read from master (strong consistency)
- Or wait 200ms before displaying to account for replication

Real impact:
Most queries: Eventual consistency fine
Money/critical: Use master read or caching
```

* *Compare DynamoDB and Cassandra. When would you choose one over the other?*

```
DynamoDB (AWS Managed):
✓ Managed (AWS handles replication, failover)
✓ Auto-scaling (pay for throughput)
✓ Simple key-value queries
✗ Expensive at scale ($millions/month)
✗ Limited querying (only by key)
Use: Startup with small data

Cassandra (Open Source):
✓ Cheap (run on your hardware)
✓ Millions writes/sec (proven at scale)
✓ Flexible querying (secondary indexes)
✓ Multi-region (data replicated across datacenters)
✗ Complex to operate (need DevOps)
Use: Netflix, Facebook, massive scale

Decision:
Start: DynamoDB (managed, simple)
Scale: Cassandra (cheaper, more control)
```

* *MongoDB uses documents instead of rows. What does that gain you? What does it cost you?*

```
Gain 1: Flexible Schema
SQL: {user_id, name, email, age} (fixed)
MongoDB: Add/remove fields anytime (no migration)

Gain 2: Nested Data
SQL: Requires JOINs
     users table + addresses table + join
MongoDB: One document {name, addresses: [...]}
         No join needed

Gain 3: Fast Development
SQL: ALTER TABLE slow (locks table)
MongoDB: Just add field (instant)

Cost 1: Querying is less powerful
SQL: WHERE address.country = 'India' (easy join)
MongoDB: Same query possible but less efficient

Cost 2: Consistency harder
SQL: FOREIGN KEY constraint (automatic)
MongoDB: Manual (must enforce in app)

Cost 3: Storage less efficient
SQL: Normalized (no duplication)
MongoDB: Often denormalized (duplication for query speed)

Example:
Order document in MongoDB:
{
  order_id: 1,
  user: {name: "Ali", email: "ali@test.com"}, (denormalized)
  items: [{product_id: 1, price: 50}, ...],
  total: 100
}

User name duplicated in every order (denormalization)!
If user changes name: Must update all orders ❌
But queries are fast ✓

Conclusion:
✓ MongoDB: Simple queries, flexible schema
✗ MongoDB: Complex queries, consistency
```

* *When would you use a graph database? What would be terrible about using SQL for social graph queries?*

```
Perfect use case: Social graph

Query: "Friends of my friends" (people I might know)
Depth: 3 levels deep

SQL:
SELECT users.* FROM users
JOIN friendships AS f1 ON f1.user2_id = users.user_id
JOIN friendships AS f2 ON f2.user1_id = f1.user1_id
JOIN friendships AS f3 ON f3.user2_id = f2.user1_id
WHERE f3.user1_id = my_user_id

Query is complex, expensive ❌

Graph DB (Neo4j):
MATCH (me:User)-[:FRIEND]->(friend)-[:FRIEND]->(fof)
RETURN fof

Instant, elegant ✓

Terrible to use SQL for:
- Social networks (millions of relationships)
- Recommendation engines (product A similar to B similar to C)
- Knowledge graphs (entity relationships)

But:
If relationships not main query: Don't use graph DB
Example: Bank accounts (not about relationships)
SQL is fine ✓
```

Summary Table: SQL vs NoSQL

```
Aspect           SQL              NoSQL
─────────────────────────────────────────────────
Writes/sec       5k               1M+
Schema           Fixed            Flexible
Queries          Complex          Simple
Consistency      Strong           Eventual
Scale            Vertical         Horizontal
Replication      Master-Slave     Distributed
Best for         Structured data  Massive scale
Transactions     ACID             None (or eventual)
```

## **12\) Partitioning [PartitioningSharding\_Complete\_Notes](https://docs.google.com/document/d/1l09rVW1rU_D1y7VoohgnpbVC6vJ6h7_C52-wun-QN8c/edit?tab=t.0#heading=h.gke12j70lqyt)**

**▸ Basic**

* Partitioning \= splitting data across multiple storage nodes to scale beyond one machine

**▸ Intermediate**

* Horizontal partitioning (sharding) — split rows across nodes by key range or hash

* Vertical partitioning — split columns; put hot columns on fast storage

* Directory-based partitioning — a lookup service maps keys to nodes

**▸ Advanced**

* Consistent hashing — minimizes data movement when adding/removing nodes

* Hot partition problem — one shard gets disproportionate traffic (e.g. celebrity user)

* Rebalancing — moving data when nodes join/leave; must be done without downtime

**🎯 Google Interview Questions — speak these out loud:**

* *What is the difference between horizontal and vertical partitioning? Give a concrete example of each.*

* *You shard a user database by user\_id mod 10\. Then you need to add a 11th shard. What happens to all your data?*

* *What is consistent hashing and how does it solve the resharding problem?*

* *A shard holding celebrity user data gets 1000x more traffic than other shards. How do you fix this hot partition?*

* *What are the downsides of partitioning? What queries become very hard or impossible?*

## **13\) Sharding**

**▸ Basic**

* Sharding \= horizontal scaling of a database by distributing rows across multiple DB instances

**▸ Intermediate**

* Range-based sharding — users A-M on shard 1, N-Z on shard 2\. Simple but can cause hot spots

* Hash-based sharding — hash(key) % num\_shards. More even distribution

* Directory-based sharding — lookup table maps keys to shards. Most flexible

**▸ Advanced**

* Cross-shard queries — JOINs across shards are very expensive or impossible

* Rebalancing shards — adding a shard requires moving data; use consistent hashing to minimize

* Shard key choice is critical — bad shard key causes hotspots or uneven distribution

**🎯 Google Interview Questions — speak these out loud:**

* *How would you shard a user table with 1 billion rows? What would you use as the shard key?*

* *A user wants to query 'all orders for user X'. With sharding by order\_id, why is this hard? How do you fix it?*

* *What happens when you need to go from 10 shards to 15 shards? Walk me through the process.*

* *When is range sharding better than hash sharding? When is hash sharding better?*

* *Cross-shard transactions don't have ACID guarantees. How do large-scale systems like Google handle this?*

## **14\) Caching**

**▸ Basic**

* Cache \= fast temporary storage that avoids re-computing or re-fetching expensive data

* Cache hit \= data found in cache. Cache miss \= must go to the source

**▸ Intermediate**

* LRU (Least Recently Used) — evict the item not accessed for the longest time

* LFU (Least Frequently Used) — evict the item accessed the fewest times

* Cache-aside (lazy loading) — app checks cache, falls back to DB, populates cache

**▸ Advanced**

* Cache invalidation — the hardest problem. When does cached data become stale?

Cache Invalidation

```
Problem: When does cached data become stale?

Example:
User updates name: "Ali" → "Ahmed"
Cache still has: "Ali" (outdated)
User sees old name ❌

Why hard?
- Cache doesn't know when data changed
- Must manually invalidate or set TTL (time to live)
- Too short TTL: Cache useless (always miss)
- Too long TTL: Stale data (user notices)

Solution:
- TTL: Expire after 5 minutes (balance)
- Invalidate on write: When user updates, delete cache
- Event-based: DB change triggers cache delete
```

#### 

* Write-through — write to cache and DB simultaneously. Always consistent, slower writes

Write-Through

```
Write to cache AND DB simultaneously.

Flow:
1. App writes to cache
2. App writes to DB
3. Both succeed, return to user

Consistency: Always consistent ✓ (cache = DB always)
Speed: SLOW (wait for both writes)
Safety: Safe (DB has data even if cache dies)

Use: Critical data (financial systems, passwords)
```

#### 

* Write-back — write to cache only, flush to DB later. Faster writes, risk of data loss

Write-Back (Write-Behind)

```
Write to cache only, flush to DB later.

Flow:
1. App writes to cache (instant)
2. Return success to user immediately
3. Background job: Flush cache to DB (eventually)

Consistency: Eventually consistent ❌ (cache ≠ DB briefly)
Speed: FAST ✓ (cache write only)
Safety: Risky (if cache crashes before flush, data lost)

Use: Social feeds, metrics, non-critical data
```

#### 

* Thundering herd — cache expires, all requests hit DB simultaneously. Use jitter on TTL

Thundering Herd

```
Problem: Cache expires → ALL requests hit DB simultaneously

Scenario:
Cache TTL: 3600 seconds (1 hour)
1M requests cached
T=3600: Cache expires, ALL 1M requests → DB in 1 second ❌
DB overloaded!

Solution 1: Jitter on TTL
TTL: 3600 + random(0-600) seconds
Spreads expiration over 10 minutes
Requests hit DB gradually ✓

Solution 2: Refresh before expiry
When cache hit drops to 10%: Pre-fetch from DB
Refresh cache proactively, no thundering ✓

Solution 3: Fallback cache
Primary cache expires
Secondary (stale) cache still serves
Buy time to fetch fresh data
```

**🎯 Google Interview Questions — speak these out loud:**

* *Phil Karlton said 'There are only two hard problems in computer science: cache invalidation and naming things.' Why is cache invalidation hard? Give an example.*

```
Problem: Data changes, but cache doesn't know.

Example:
User updates name: Ali → Ahmed
Cache: Still has "Ali"
User sees outdated info ❌

Why hard?
- Cache is separate storage (DB doesn't notify it)
- Must choose: Delete immediately OR let it expire
- Delete immediately: Extra latency (write-through)
- Let expire: Stale data (user notices)

No perfect solution, only tradeoffs ❌
```

* *What is the thundering herd problem in caching? How do you prevent it?*

```
Cache expires at T=1000 (1M users cached)
All 1M requests hit DB in 1 second ❌

Prevention:

1. Jitter TTL:
   TTL = 3600 + random(0-600)
   Expiration spread over 10 minutes
   Requests trickle to DB ✓

2. Pre-refresh:
   Monitor cache hit rate
   If drops below 80%: Refresh cache early
   Avoid thundering ✓

3. Lock + single refresh:
   Multiple requests arrive after expire
   Only 1 request fetches from DB (locked)
   Others wait for result, use it
   Mutex prevents N requests to DB ✓
```

* *Compare write-through vs write-back caching. Which would you use for a financial system? For a social feed?*

```
Write-Through:
- Write cache AND DB
- Always consistent ✓
- Slow ❌ (wait for both)
- Safe ✓

Write-Back:
- Write cache only
- Fast ✓
- Risky ❌ (cache crash = data loss)
- Eventually consistent

Financial system (banks):
→ Write-through (consistency critical, speed OK)

Social feed (tweets):
→ Write-back (speed critical, eventual consistency OK)
```

* *Your cache hit rate drops from 95% to 60% overnight. How do you diagnose what happened?*

```
Diagnose:

1. Check what changed:
   - Did data size increase? (cache too small now)
   - Did query pattern change? (different users?)
   - Did cache expire? (TTL set wrong?)

2. Check cache metrics:
   - Eviction rate: If high, cache full, evicting too much
   - Hit/miss ratio: Drops from 95% to 60%
   - Memory used: Did something allocate huge space?

3. Check code:
   - Was cache invalidation logic added? (invalidating too much)
   - Was query changed? (now fetching different data)

4. Check infrastructure:
   - Cache server restarted? (lost all data)
   - Cache server crashed? (failover slower?)

Likely causes:
- Cache full (memory limit hit, evicting data)
- Data size grew (1B users now, cache designed for 100M)
- Cache server failover (lost warm cache)

Fix:
- Increase cache size
- Adjust TTL
- Adjust eviction policy (LRU vs LFU)
```

* *You have a cached user profile. The user updates their name. How and when does the cache get updated?*

```
Scenario:
Cache: {user_id: 1, name: "Ali"}
User updates: name → "Ahmed"

When/how to update cache?

Option 1: Write-through (consistent)
1. Update cache: name = "Ahmed"
2. Update DB: name = "Ahmed"
3. Return success
Result: Cache = DB always ✓

Option 2: Invalidate cache (simple)
1. Update DB: name = "Ahmed"
2. Delete cache entry
3. Return success
Next request: Cache miss, fetch fresh from DB
Result: Takes 1 request to sync, then consistent ✓

Option 3: Write-back (risky)
1. Update cache only: name = "Ahmed"
2. Return success
3. Background: Flush to DB eventually
Result: Risky if cache crashes before flush ❌

Best: Option 2 (invalidate)
- Simple: Just delete cache entry
- Fast: No extra write latency
- Safe: DB always correct
- Next request refills cache fresh
```

* *What is a cache stampede? How does a mutex lock or probabilistic early expiration help?*

```
Cache stampede = Same as thundering herd

Multiple requests arrive when cache expires
All hit DB simultaneously

Solution 1: Mutex Lock
```

**Request 1: Cache miss, acquires lock Request 1: Fetches from DB Request 2,3,4,5: Waiting for lock Request 1: Returns, releases lock Requests 2,3,4,5: Use result from Request 1 Result: Only 1 DB hit, others wait ✓**

```

Solution 2: Probabilistic Early Expiration
```

**Actual TTL: 3600 seconds Soft TTL: 3000 seconds**

**When access happens between 3000-3600:**

* **Probability: 10% of requests trigger refresh**  
* **90% use stale cache**  
* **Refresh happens gradually, not all at once ✓ Result: No thundering, no mutex needed**

```

Which better?
- Mutex: Guaranteed consistency, simple
- Probabilistic: No locks, always returns quickly, slightly stale
- Use both: Mutex for critical data, probabilistic for high-volume
```

---

Summary Table

```
Concept              What                       When Use
────────────────────────────────────────────────────────────
Cache Invalidation   Data becomes stale        Every write
Write-Through        Write cache + DB          Financial
Write-Back           Write cache only          Social media
Thundering Herd      All hit DB on expire      High-volume
TTL Jitter           Spread expiration         Prevent stampede
Mutex Lock           Single refresh request    Consistency
Probabilistic        Gradual refresh           Performance
```

## **15\) Disaster Recovery**

**▸ Basic**

* Backups — regular snapshots of data stored separately from production

```
Backups = Regular snapshots stored separately from production.

Why separate?
- If production burns, backups survive ✓
```

**▸ Intermediate**

* Multi-region replication — data exists in 2+ geographic regions

Multi-Region Replication

```
Data exists in 2+ geographic regions.

Master (us-east-1): Primary writes + reads
Replica (us-west-2): Copy of data, read-only
Replica (eu-west-1): Copy of data, read-only

Benefits:
- Disaster recovery (if us-east burns, use us-west)
- Lower latency (users read locally)

Tradeoff:
- Replication lag: Replicas are 100ms stale
- Consistency: Eventually consistent
- Cost: 3x storage
```

* RTO (Recovery Time Objective) — how long can you be down?

* RPO (Recovery Point Objective) — how much data can you afford to lose?

RTO vs RPO

```
RTO (Recovery Time Objective) = How long can you be DOWN?
- Example: 1 hour (service offline for max 1 hour)

RPO (Recovery Point Objective) = How much DATA can you LOSE?
- Example: 15 minutes (lose last 15 min of data)

Scenario: Company says RTO=1hr, RPO=15min
Backup strategy:
- Backups every 15 minutes (to meet RPO)
- When disaster hits:
  - Take 1 hour to restore (RTO)
  - Lose at most 15 minutes of data (RPO)

Cost relationship:
Lower RTO/RPO = More expensive (more frequent backups, faster failover)
Higher RTO/RPO = Cheaper but longer downtime
```

#### 

**▸ Advanced**

* Failover strategy — automated vs manual, which systems need instant failover vs tolerate delay

Failover Strategy

```
Automated:
- Primary DC fails
- Health check detects (5-30 seconds)
- Automatically switch to replica
- No human involvement ✓
- Best for: Critical services (payments, auth)

Manual:
- Primary DC fails
- Team runs runbook
- Switches to replica manually
- Slower but human oversight ✓
- Best for: Non-critical services (internal tools)

Hybrid:
- Auto-failover for data (DNS switch)
- Manual confirmation for app (human checks)
```

#### 

* Backup testing — backups that are never tested are not backups

Backup Testing

```
"Backups that are never tested are not backups."

Why test?
- Backup corrupt? Useless ❌
- Backup incomplete? Data missing ❌
- Backup restore takes 2 hours instead of 1? RTO wrong ❌

Testing:
- Monthly: Restore backup to staging
- Verify: Data integrity, restore time
- Document: Any issues

Without testing: Disaster hits, backup fails, catastrophe ❌
```

#### 

* Runbooks — documented step-by-step recovery procedures for each failure scenario

Runbooks

```
Runbook = Step-by-step documented procedure for recovery.

Example runbook:
1. Detect: Monitoring alerts primary DC down
2. Verify: Check is really down (not false alarm)
3. Switch: Update DNS to point to replica
4. Validate: Run smoke tests on replica
5. Communicate: Notify customers
6. Remediate: Root cause analysis later

Without runbook: Team panics, guesses wrong, longer recovery ❌
With runbook: Clear steps, faster recovery ✓
```

**🎯 Google Interview Questions — speak these out loud:**

* *What is RTO vs RPO? If a company says their RTO is 1 hour and RPO is 15 minutes, what does their backup strategy look like?*

```
RTO = 1 hour (can be down max 1 hour)
RPO = 15 min (can lose max 15 min data)

Strategy:
- Backup every 15 minutes (sync to remote)
- Master + 2 replicas (one regional, one distant)
- If disaster: Switch to replica (5 min)
- Restore last backup (55 min remaining)
- Total: 1 hour downtime, max 15 min data loss ✓

Cost: High (frequent backups, multiple replicas)
```

* *Your primary US data center has a fire. Walk me through what happens to your service.*

```
T=0: Fire destroys us-east-1
     - Users hitting primary DC get timeout ❌

T=5 sec: Monitoring detects failure
         - Health checks fail
         - Alerts trigger

T=10 sec: Auto-failover kicks in
          - DNS updated: us-east-1 → us-west-2
          - Traffic redirected to replica

T=30 sec: Users hitting replica (us-west-2)
          - Slightly stale data (replication lag 100ms) ⚠️
          - Service working again ✓

T=1+ hour: Restore from backup
           - Full consistency recovered
           - Replication lag cleared

Impact:
- 5-30 seconds outage (DNS propagation + failover)
- Max 15-minute data loss (last backup)
- Service recovers from replica quickly ✓
```

* *How do you ensure a multi-region deployment stays consistent? What tradeoffs are you making?*

```
Consistency wanted: All regions same data instantly
Replication lag reality: 100ms stale in replicas

Tradeoff:

Strong consistency:
- Master waits for replicas to acknowledge
- Slower writes (wait for all regions)
- But always consistent ✓

Eventual consistency:
- Master writes immediately
- Replicas sync asynchronously
- Faster writes ✓
- Briefly inconsistent ⚠️

Choice:
- Money transfer: Strong (consistency critical)
- Social media: Eventual (speed critical)

Most systems: Eventual + strong reads for critical paths
```

* *You take backups every 6 hours. At what point in a 6-hour window does a disaster hurt the most?*

```
Backup at: T=0, T=6, T=12, T=18, T=24

Worst time: T=5:59 (just before backup)
- All changes since T=0 (nearly 6 hours) lost ❌

Best time: T=0:01 (just after backup)
- Only 1 minute of data lost ✓

RPO for 6-hour backups: 6 hours
(Can lose up to 6 hours of data)

To reduce loss: More frequent backups (hourly → 1 hour RPO)
Cost: More backup operations
```

# **PHASE 5 — DISTRIBUTED SYSTEMS**

## **16\) Key-Value Stores**

**▸ Basic**

* Simplest data model: store and retrieve values by key. O(1) reads and writes

* Examples: Redis (in-memory), DynamoDB (distributed), Memcached (cache only)

```
Key-Value Store: GET/SET O(1) operations via hash table.

Examples:
- Redis: In-memory, persistent, fast
- DynamoDB: Distributed, managed, durable
- Memcached: In-memory cache only
```

### 

**▸ Intermediate**

* Redis: supports strings, hashes, lists, sets, sorted sets, pub/sub

* Use cases: session storage, rate limiting counters, leaderboards, caching

```
Redis Data Types:
Strings, Hashes, Lists, Sets, Sorted Sets, Pub/Sub

Use Cases:
- Session storage (SETEX key value TTL)
- Rate limiting (INCR counter)
- Leaderboards (ZADD scores)
- Caching (expire stale data)
```

### 

**▸ Advanced**

* TTL policies — auto-expire keys to prevent stale data and manage memory

* Distributed caching — multiple Redis nodes with consistent hashing

* Redis Cluster — sharded Redis for horizontal scale \+ replication per shard

```
TTL Policies: Auto-expire keys via lazy deletion + background cleanup.

Distributed Caching: Multiple Redis nodes, consistent hashing.

Redis Cluster:
- Sharded (data split across nodes)
- Replicated (each shard has primary + replica)
- Failover: Replica becomes primary if crash
```

### 

**🎯 Google Interview Questions — speak these out loud:**

* *Design a distributed key-value store like Redis. What are the core operations and how do you scale it?*

```
Core operations:
GET key, SET key value, DELETE key, EXPIRE key TTL

Scale:
1. Sharding: hash(key) % num_shards → distribute data
2. Replication: Each shard (primary + 2 replicas)
3. Failover: Replica promoted if primary crashes

Redis Cluster handles all automatically.
```

### 

* *How does Redis handle expiration of keys? What happens at the moment a key expires?*

```
Lazy deletion: Deleted only on access (GET expired key).
Active deletion: Background job scans randomly, deletes expired keys.

Both combined = reliable expiration.

Memory full: Eviction policy (LRU/LFU) deletes keys.
```

### 

* *You're using Redis for session storage. Your Redis node crashes. What happens to all logged-in users?*

```
Without replication:
- All sessions lost, 1M users logged out ❌

With Redis Cluster:
- Session data replicated across 2+ nodes
- Primary crashes → Replica becomes primary (1-2 sec failover)
- Users stay logged in ✓
```

### 

* *Compare Redis vs Memcached. When would you choose each?*

```
Redis:
- Persistent, multiple data types, replication
- Use: Sessions, leaderboards, caching

Memcached:
- Cache-only (no persistence), strings only
- Use: Simple temporary cache

Choose Redis: Need persistence + data types
Choose Memcached: Simple cache, can lose data
```

### 

* *How do you handle hot keys in a distributed key-value store? (One key getting millions of reads/second)*

```
Problem: Single key bottleneck (all requests to one shard).

Solutions:
1. Local caching: Cache on client-side
2. Read replicas: Distribute key across multiple nodes
3. Denormalization: Store value in multiple shards
4. Rate limiting: Limit requests per client

Combine cache + replicas for best performance.
```

### 

## **17\) CDN (Content Delivery Network)**

**▸ Basic**

* CDN \= network of geographically distributed servers that cache and serve static content closer to users

```
CDN = Geographically distributed servers caching static content near users.
Benefit: 20ms (edge) vs 200ms (origin) = 10x faster.
```

**▸ Intermediate**

* Edge caching — serve images, JS, CSS, videos from servers near the user

* Origin server — your actual server; CDN pulls from origin on cache miss (pull CDN)

* Push CDN — you push content to CDN proactively (better for predictable content)

**Edge Caching**

```
Cache images, JS, CSS, videos at edge servers worldwide.
User in Tokyo → Tokyo edge (instant)
vs Origin in US (200ms latency).
```

**Pull CDN (Lazy)**

```
On-demand caching.
Miss: Edge fetches from origin, caches, returns.
First user slow, subsequent users fast.
Use: Unpredictable content (website images).
```

**Push CDN (Proactive)**

```
You push content to all edges before users request.
All users hit cache instantly.
Use: Predictable content (movie release, live event).
```

**▸ Advanced**

* Cache invalidation — how to purge stale CDN content immediately across all edge nodes

* Geo-distribution — users automatically served by nearest edge server

* Dynamic content at edge — modern CDNs can run compute at edge (Cloudflare Workers)

**Cache Invalidation**

```
Content stale, need immediate update.

Solutions:
1. Manual purge: API call deletes from all edges (instant)
2. TTL expiration: Auto-delete after time (delayed)
3. Versioning: New URL (video-v2.mp4) for new version
4. Cache tags: Tag content, invalidate all with tag

Best: Manual purge (instant control).
```

**Geo-Distribution & Routing**

```
DNS-based routing:
- User's IP → Geolocation lookup
- Return nearest edge IP
- User routed to nearest edge automatically

Example:
User in Tokyo (IP 123.45.67.89) → Tokyo edge
User in London (IP 200.100.50.25) → London edge
```

**Dynamic Content at Edge**

```
Modern CDNs (Cloudflare Workers, CloudFront Functions):
Run code at edge (not origin).

Example: Personalized profile
- Traditional CDN: Can't serve (dynamic) → origin (200ms)
- Edge compute: Run function at edge → instant ✓

Use cases: A/B testing, personalization, geolocation-based content.
```

---

**🎯 Google Interview Questions — speak these out loud:**

* *You're designing YouTube. What content goes in the CDN and what stays at origin?*

```
CDN:
✓ All static: Images, thumbnails, JS, CSS, video files
✓ All video content (cached at edges)

Origin:
✓ Dynamic: User profile, watch history, recommendations
✓ Comments, likes (user-generated, fresh)

Architecture:
User in Tokyo requests video → CDN edge (instant)
User logs in → Origin (authentication)
Video plays → CDN cached video
Comments load → Origin (fresh data)
```

* *A piece of content was cached with a 24-hour TTL. You need to update it immediately. How?*

```
Solution: Manual purge

CDN API: PURGE /content.mp4
All 50 edges delete cache instantly ✓

Alternative:
- Versioning: Serve /content-v2.mp4 (new URL)
- Users get new version on next request

Don't wait 24 hours (manual purge is instant).
```

* *What is the difference between a pull CDN and push CDN? When would you use each?*

```
Pull CDN:
- User requests → edge checks cache
- Miss: Fetch from origin, cache
- First user slow, then fast
- Use: Website (unpredictable access)

Push CDN:
- You proactively distribute to edges
- All users hit cache (instant)
- Use: Movie release, live event (known content)

Choose Pull: General content
Choose Push: Major releases
```

* *Your CDN serves content from 50 edge locations. How does it know which edge to route a user to?*

```
DNS resolution:

User types: cdn.example.com
DNS queries CDN's geo-resolver
- User's IP: 123.45.67.89
- Geolocation: Tokyo
- Return Tokyo edge IP
- User routed to Tokyo edge ✓

Same system routes:
London user → London edge
Sydney user → Sydney edge
Automatic geolocation routing.
```

## **18\) Stateful vs Stateless**

**▸ Basic**

* Stateless — server doesn't remember anything between requests. Each request is self-contained

* Stateful — server stores client context between requests (e.g. shopping cart in memory)

```
Stateless: Server forgets client between requests (self-contained).
Stateful: Server remembers client context in memory.
```

**▸ Intermediate**

* Why stateless scales better — any server can handle any request; add servers freely

* JWT (JSON Web Token) — client holds their own session data, signed by server

* Server-side sessions — session stored on server, client has session ID cookie

**Why Stateless Scales**

```
Any server handles any request.
Request 1 → Server 1, Request 2 → Server 5, Request 3 → Server 2.
Add servers freely (no session migration).
Stateful: User must hit same server (sticky) → hard to scale.
```

**JWT (JSON Web Token)**

```
Client holds data, server validates signature.

Flow:
1. Login: Server creates JWT {user_id: 1, exp: 2025-12-31}, signs it
2. Client stores JWT
3. Next request: Client sends JWT, server verifies signature + decodes
4. No server lookup needed ✓

Benefit: Stateless, scalable
Trade-off: Can't revoke (until expiry)
```

**Server-Side Sessions**

```
Server stores session, client holds session ID.

Flow:
1. Login: Server creates session_abc = {user_id: 1}, stores in memory/Redis
2. Client stores session_abc cookie
3. Next request: Client sends session_abc, server looks up in store
4. No signature verification needed ✓

Benefit: Instant revocation (delete session_abc)
Trade-off: Requires server storage, session affinity
```

**▸ Advanced**

* Distributed session management — store sessions in Redis so any server can read them

* JWT tradeoffs — can't be revoked without extra infrastructure (token blacklist)

* Sticky sessions — workaround for stateful servers, but hurts scalability

**Distributed Session Management**

```
Store sessions in shared Redis (not server memory).

Server 1 → Redis ← Server 2 ← Server 3

Flow:
1. User logs in on Server 1
2. Server 1 stores session in Redis
3. User's next request hits Server 3
4. Server 3 looks up session in Redis ✓

Benefit: Any server can handle any request + survives crash
```

**JWT Revocation**

```
Problem: Can't revoke JWT before expiry (stateless).

Solution: Token Blacklist (Redis)
1. User logs out: Store JWT in blacklist (expires at token expiry)
2. User tries revoked JWT: Server checks blacklist → reject

Cost: Extra Redis lookup (small latency)
```

**Sticky Sessions**

```
Workaround for stateful servers.
User A → always Server 1, User B → always Server 2.

Problem: Doesn't scale (new servers get no users)
Not a real solution, just temporary fix.
```

**🎯 Google Interview Questions — speak these out loud:**

* *What makes a service stateless? Why does statelessness matter for horizontal scaling?*

```
Stateless = Server doesn't store request context.
Each request self-contained (client provides all info).

Why matters:
- Any server handles any request
- No session migration needed
- Load balancer can round-robin
- Add 100 servers instantly ✓

Stateful breaks:
- User must hit same server (sticky)
- Add servers → users stuck on old servers
- Can't scale freely ❌
```

* *Compare JWT and server-side sessions. What are the security and scalability tradeoffs?*

```
JWT:
- Client holds {user_id: 1, role: admin} signed
- Server: Verify signature, decode, process
- Stateless ✓, scales ✓, can't revoke ❌

Server-Side:
- Server stores {session_id: {user_id: 1, role: admin}}
- Client holds session_id only
- Revocable ✓, requires storage ❌, session affinity ❌

Choice:
- JWT: Microservices, mobile, APIs
- Server-Side: Traditional web, banking (need revocation)
```

* *A user's JWT is stolen. How do you invalidate it before it expires? What does this require architecturally?*

```
Token Blacklist in Redis:

1. Detect theft: User reports
2. Immediately: ADD to blacklist
   Redis: blacklist:token_xyz = true (expires at token expiry)
3. Next request with stolen token:
   Server checks: Is it blacklisted? YES → Reject ✓

Architecture requirement:
- Redis (distributed store)
- Lookup blacklist on every request
- Clean up expired tokens automatically

Cost: Small latency (Redis lookup)
Benefit: Instant revocation ✓
```

* *You have a stateful gaming server. How would you make it horizontally scalable?*

```
Current: Server 1 holds game state (player position, health, inventory)
Player must stay on Server 1 (sticky).
Scale impossible.

Make stateless:

Option 1: Redis for game state
- Player 1 position → Redis
- Player 1 health → Redis
- Player 1 inventory → Redis
- Any server reads/writes from Redis ✓
- Player can switch servers anytime

Option 2: Client-side state validation
- Player sends: {position: x, y, action: move}
- Server validates (cheat detection)
- Updates Redis
- Next server reads from Redis

Architecture:
Player ← Load Balancer → Server 1 (process) → Redis
                      → Server 2 (process) → Redis
                      → Server 3 (process) → Redis

Result:
- Any server processes any player ✓
- Add servers instantly ✓
- State survives crash ✓
- Scale horizontally ✓
```

## **19\) Monitoring & Alerts**

**▸ Basic**

* The four golden signals: Latency, Traffic, Errors, Saturation (LTES)

* CPU, memory, disk I/O, network — the fundamental server metrics

**Four Golden Signals (LTES)**

```
Latency: Request time (alert P99 > threshold, not average)
Traffic: Requests/sec (alert on sudden spike or drop)
Errors: Failure rate (alert > 1%)
Saturation: Resource fullness (CPU > 80%, Memory > 90%, Disk > 90%)
```

**Fundamental Metrics**

```
CPU: Processor usage (high = overloaded)
Memory: RAM usage (high = memory leak or normal load)
Disk I/O: Read/write speed (high = DB slow or disk failing)
Network: Bandwidth in/out (high = large responses or congestion)
```

**▸ Intermediate**

* Structured logging — JSON logs with consistent fields for easy querying

* Alert thresholds — alert on p99 latency, error rate, not just averages

* SLI/SLO/SLA — Service Level Indicator, Objective, Agreement

**Structured Logging**

```
JSON format with fields: timestamp, service, level, user_id, error_code, latency_ms, request_id

Benefits: Parse automatically, query easily, correlate across services
Tools: ELK Stack (Elasticsearch, Logstash, Kibana)
```

**Alert Thresholds**

```
Alert on P99 latency (not average):
- Average hides outliers
- P99 catches worst 1% of users
- Example: Alert if P99 > 500ms

Error rate threshold:
- Alert if > 1% (unacceptable)

Resource thresholds:
- CPU > 80%, Memory > 90%, Disk > 90%
```

**SLI / SLO / SLA**

```
SLI (Indicator): Actual metric - "P99 latency was 150ms"
SLO (Objective): Target - "We aim for P99 < 200ms"
SLA (Agreement): Legal contract - "99.9% uptime or refund"

Example:
- SLI: Uptime = 99.95% (measured)
- SLO: Uptime > 99.9% (internal goal)
- SLA: Uptime >= 99.9% (customer promise, penalty if breach)
```

**▸ Advanced**

* Distributed tracing — trace a single request across 20 microservices (Jaeger, Zipkin)

* Prometheus \+ Grafana — metrics collection \+ visualization, industry standard

* Anomaly detection — alert when metrics deviate from baseline, not just exceed thresholds

**Distributed Tracing**

```
Problem: 50 microservices, which one is slow?
Solution: Trace request with trace_id across all services

Visualization (Jaeger/Zipkin):
Request (trace_xyz)
├─ Service-1: 10ms ✓
├─ Service-2: 50ms ✓
├─ Service-3: 4000ms ⚠️ (bottleneck)
└─ Service-4: 100ms ✓

Instant answer: Service-3 is slow
```

**Prometheus \+ Grafana**

```
Prometheus: Collects metrics every 15 seconds, time-series storage
Grafana: Visualizes metrics, creates dashboards, real-time alerts

Example:
- Prometheus scrapes cpu_usage, memory, requests, errors, latency
- Grafana shows trends over 24 hours
- Alert: If cpu > 80% for 5 min → "Scale servers"

Industry standard: Netflix, Google, Amazon
```

**Anomaly Detection**

```
Problem: Fixed thresholds don't account for normal patterns
- Normal P99 at 3am: 150ms
- Normal P99 at 3pm: 500ms (peak hours)
- Alert if deviates from baseline, not exceeds fixed threshold

Algorithm: Learn normal pattern, alert on deviation (mean ± 2*stddev)
Result: Catch real problems, fewer false alarms
```

**🎯 Google Interview Questions — speak these out loud:**

* *What are the four golden signals? For a URL shortener, what would you monitor for each?*

```
Latency (P99):
- Normal: 100ms, Alert if: > 500ms

Traffic:
- Normal: 100k RPS, Alert if: Spike to 1M (DDoS?) or drop to 10k (broken?)

Errors:
- Normal: 0.1%, Alert if: > 1%

Saturation:
- CPU: Alert if > 80%
- Memory: Alert if > 90%
- Database connections: Alert if > 90%
- Disk: Alert if > 90%
```

* *What is the difference between an SLI, SLO, and SLA? Give a concrete example.*

```
SLI = METRIC: "Our P99 was 150ms" (actual measurement)
SLO = TARGET: "We aim for P99 < 200ms" (internal goal)
SLA = CONTRACT: "Guarantee 99.9% uptime or refund 10%" (legal promise)

Example:
- SLI: Uptime = 99.95%
- SLO: Uptime > 99.9%
- SLA: Uptime >= 99.9% (breach = compensation)
```

* *Why is monitoring p99 latency more important than average latency?*

```
Example: 1000 requests
- 990 requests: 100ms (fast)
- 10 requests: 1900ms (slow)
- Average: 200ms ✓

Alert on average: Seems healthy, miss 1% slowness ❌
Alert on P99: Catch worst-case users experiencing 19x latency ✓

P99 > 500ms = worst 1% affected, fix urgently
```

* *You have 50 microservices. A user complains their request was slow. How do you find which service caused the slowdown?*

```
Use distributed trace with trace_id:

Request (trace_id: req_abc)
├─ API Gateway: 10ms
├─ Auth: 50ms
├─ User Service: 100ms
├─ Payment Service: 4000ms ⚠️ (BOTTLENECK)
└─ Notification: 200ms

Instant: Payment Service is slow (4 seconds out of 4.4 total)
Next: Check Payment Service logs, DB latency, external API calls
```

* *Your alert fires at 2am. It says error rate exceeded 5%. How do you investigate without a runbook?*

```
1. Check what changed (recent deployment?)
   - Git log: Code deployed in last 1 hour? ✓
   - Compare old vs new version

2. Check logs (what errors?)
   - Grep error logs for 2am timeframe
   - Look for patterns (all same error?)
   - Example: "Cannot connect to database"

3. Check metrics (which service?)
   - Use distributed tracing (see Q4)
   - Check latency in specific service
   - Check CPU/memory (saturated?)

4. Check dependencies (external service down?)
   - Payment gateway? Database? Third-party API?
   - Check their status page

5. Check rate limiting (traffic surge?)
   - Unusual spike at 2am?
   - DDoS attack?

6. Quick fixes:
   - Rollback deployment
   - Scale up servers
   - Restart service
   - Fix configuration

With runbook: Steps 1-6 pre-documented, instant diagnosis ✓
Without runbook: Guessing, trial & error, hours to fix ❌
```

## **20\) Messaging Queues**

**▸ Basic**

* Message queue \= buffer between producer and consumer so they don't need to be in sync

* Benefits: decoupling, async processing, load leveling, retry on failure

```
Message Queue: Buffer between producer and consumer (async decoupling).

Benefits:
- Decoupling: Services independent
- Async: Producer doesn't wait for consumer
- Load leveling: Buffer handles traffic spikes
- Retry: Messages persist, retry on failure
```

**▸ Intermediate**

* Kafka — distributed log, high throughput, replay-able, ordered within partition

* RabbitMQ — traditional MQ, routing rules, good for task queues

* At-least-once vs exactly-once delivery — hard to get exactly-once

**Kafka**

```
Distributed log, high throughput, persistent, replay-able.

Characteristics:
- Partitioned: Data split for parallelism
- Ordered within partition (not across)
- Persistent: Messages on disk
- Replay-able: Read from offset again
- Use: Event streaming, analytics, audit logs
```

**RabbitMQ**

```
Traditional message broker, task queues, complex routing.

Characteristics:
- TTL: Messages auto-expire
- Routing rules: Flexible message routing
- Acknowledgment: Consumer confirms processing
- Less throughput than Kafka
- Use: Background jobs, email, retries
```

**At-Least-Once vs Exactly-Once**

```
At-least-once: Message guaranteed delivered, may process TWICE.

Bug example:
- Consumer processes order → charge card ✓
- Crashes before acknowledging
- Message retried → charge card AGAIN ❌

Fix (idempotent processing):
- Database UNIQUE constraint on order_id
- Second insert fails, caught, skipped ✓

Exactly-once: Very hard (requires distributed transactions)
Most systems: At-least-once + idempotent ✓
```

**▸ Advanced**

* Event-driven architecture — services emit events, others subscribe and react

* Consumer groups — multiple consumers read from same Kafka topic in parallel

* Dead letter queues — failed messages go here for inspection and retry

**Event-Driven Architecture**

```
Services emit events, others subscribe and react (loosely coupled).

Flow:
Order Service → Emits "order.created" event
                ↓
         Message Queue
         /    |    \
    Inventory  Payment  Notification
    (subscribe) (subscribe) (subscribe)

Benefits:
- Decoupled (services don't know each other)
- Resilient (one slow service doesn't block others)
- Observable (event log is audit trail)
```

**Consumer Groups (Kafka)**

```
Multiple consumers read same topic in parallel.

Setup:
- Topic "orders" has 10 partitions
- Consumer Group "processors"
  - Consumer-1: Partitions 0-3
  - Consumer-2: Partitions 4-7
  - Consumer-3: Partitions 8-9

Result: 3x throughput, auto-rebalance on crash
```

**Dead Letter Queue (DLQ)**

```
Failed messages go to DLQ for inspection and retry.

Flow:
Message fails → Retry 3 times → Move to DLQ

In DLQ:
- Message stored with error details
- Operator inspects: "Why failed?"
- Fix error → Requeue → Retry ✓

Critical for production:
- Prevents infinite loops
- Enables manual recovery
- System stays healthy despite failures
```

**🎯 Google Interview Questions — speak these out loud:**

* *Why would you put a message queue between two services? What problems does it solve?*

```
Without queue:
Service A → Service B (direct call)
- If B slow: A waits ❌
- If B down: A fails ❌
- No load leveling ❌

With queue:
Service A → Queue → Service B (async)
- A returns instantly ✓
- B process at own pace ✓
- Queue buffers spikes ✓

Example:
E-commerce without queue: User waits 5 seconds (Inventory + Payment + Email)
With queue: "Order received!" instant, process async
```

* *Kafka vs RabbitMQ — when would you choose each?*

```
Choose Kafka:
- High throughput (1M+ msg/sec)
- Event sourcing needed
- Replay-ability critical
- Real-time analytics
Example: Netflix events, Twitter firehose

Choose RabbitMQ:
- Task queues (background jobs)
- Complex routing rules
- TTL needed
- Lower throughput acceptable
Example: Email sending, image processing
```

* *What does 'at-least-once delivery' mean? What bug can it cause and how do you handle it?*

```
Bug: Message processed twice

Example:
Order arrives, charge card ✓
Consumer crashes before acknowledging
Message retried, charge card again ❌

Fix: Idempotent processing
Database UNIQUE(order_id)
Second insert fails, handled gracefully ✓
```

* *Your queue has 10 million unprocessed messages and consumers can't keep up. What do you do?*

```
Solutions:

1. Add consumers (easiest):
   - 1 consumer × 1000/sec = 1000/sec
   - Add 9 consumers = 10,000/sec
   - Catch up in 1000 sec ✓

2. Optimize code:
   - Faster processing = higher throughput

3. Batch processing:
   - Process 100 messages in parallel

4. Scale resources:
   - More CPU/memory for consumers

Best: Add consumers (scales linearly)
```

* *What is a dead letter queue and why is it critical for production reliability?*

```
Without DLQ:
Message fails → Retry forever → Infinite loop
- CPU wasted, queue grows, performance degrades ❌

With DLQ:
Message fails → Retry 3 times → Move to DLQ
- Main queue clears ✓
- Operator fixes → requeue ✓
- System healthy despite failures ✓

Critical for production:
- Prevents infinite loops
- Audit trail of failures
- Manual recovery path
```

## **21\) Rate Limiting**

**▸ Basic**

* Rate limiting \= capping how many requests a user/IP/API key can make in a time window

* Prevents abuse, DDoS mitigation, ensures fair usage

```
Rate Limiting: Cap requests per user/IP/API-key in time window.

Benefits:
- Prevent abuse (malicious users)
- DDoS mitigation (limit impact)
- Fair usage (all users get share)
```

**▸ Intermediate**

* Token bucket — user has a bucket of tokens; each request costs 1 token; tokens refill at fixed rate

* Leaky bucket — requests enter a queue and are processed at fixed rate (smooths bursts)

* Fixed window — count requests in a fixed time window; vulnerable to boundary attacks

* Sliding window — more accurate, counts over a rolling window

**Token Bucket**

```
Bucket fills at fixed rate. Each request costs 1 token.

Setup:
- Capacity: 100 tokens
- Refill: 100/min (1.67/sec)

Allows bursts: If bucket full, can send 100 instantly.
Then limited by refill rate ✓
```

**Leaky Bucket**

```
Requests queue, drain at fixed rate (smooths bursts).

Setup:
- Queue capacity: 100
- Drain rate: 100/min (1.67/sec)

No bursts: Excess requests rejected.
Fair queueing ✓
```

**Fixed Window**

```
Count requests in time window (e.g., 0-60 sec), reset at boundary.

Problem: Boundary attack
- T=55 sec: 100 requests (limit)
- T=60 sec: Reset to 0
- T=60.1 sec: 100 more requests (200 in 5 sec!) ❌
```

**Sliding Window**

```
Count over rolling 60-second window (more accurate).

No boundary attack:
- Old requests age out gradually
- Can't burst at boundary ✓

Use: Production systems (accurate)
```

**▸ Advanced**

* Distributed rate limiting — state must be shared across all servers (use Redis counters)

* Graceful response — return 429 with Retry-After header, not a silent drop

* Per-user vs per-IP vs per-API-key — different strategies for different threat models

**Distributed Rate Limiting**

```
Problem: 10 servers, each thinks it's at 50/100 (total 150) ❌

Solution: Shared Redis counter

Algorithm:
```

**on\_request(user\_id):** 

**key \= f"rate\_limit:{user\_id}" count \= redis.increment(key)** 

**// All servers query same Redis**

```
if count == 1:
    redis.expire(key, 60)  // Reset window

if count > 100:
    return 429
else:
    return 200
```

```

Result: All servers see same counter ✓
```

**Graceful Response**

```
Bad: Silent drop or 500 error
Good: HTTP 429 with headers

Response:
HTTP/1.1 429 Too Many Requests
Retry-After: 60
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 0
X-RateLimit-Reset: 1778836400

{
  "error": "rate_limit_exceeded",
  "retry_after": 60
}
```

**Per-User vs Per-IP vs Per-API-Key**

```
Per-User: Best (fair, authenticated)
Per-IP: Simple (but NAT shared)
Per-API-Key: Flexible (monetization, different tiers)

Strategy:
- Authenticated → Per-user
- Anonymous → Per-IP
- Commercial API → Per-API-key
```

**🎯 Google Interview Questions — speak these out loud:**

* *Design a rate limiter that allows 100 requests per minute per user. Walk me through the data structures and logic.*

```
Data: Redis counter
Key: rate_limit:{user_id}

Algorithm:
1. redis.increment(key)
2. If count == 1: expire(key, 60)
3. If count > 100: return 429
4. Else: return 200

Headers: X-RateLimit-Limit, X-RateLimit-Remaining, Retry-After
```

* *What is the token bucket algorithm? How is it different from a fixed window counter?*

```
Token bucket:
- Fills at rate (1.67/sec)
- Allows bursts
- Use: Bursty traffic

Fixed window:
- Reset at boundary (vulnerable to attack)
- Simple
- Use: Teaching examples

Token bucket safer ✓
```

* *Your rate limiter runs on 10 servers. How do you make sure the limit is enforced globally, not per-server?*

```
Solution: Shared Redis

All 10 servers increment SAME counter in Redis.
No per-server state ✓

Cost: Redis latency (1-5ms acceptable)
```

* *What is the fixed window boundary attack? How does sliding window solve it?*

```
Fixed window attack:
T=55: 100 req (limit)
T=60: Reset
T=60.1: 100 more req (200 total!) ❌

Sliding window:
- Window rolls, old requests age out
- No reset at boundary
- Can't burst at T=60 ✓
```

* *A user is rate limited. How should your API respond? What headers should you send?*

```
HTTP 429 Too Many Requests

Headers (MUST):
- Retry-After: 60
- X-RateLimit-Limit: 100
- X-RateLimit-Remaining: 0
- X-RateLimit-Reset: timestamp

Body: JSON with error message

Why:
- Client knows to stop ✓
- Client knows when to retry ✓
```

# **PHASE 6 — SYSTEM DESIGN PATTERNS**

**Load Balancer**

Distributes incoming traffic across multiple servers so no single node is overloaded.

**Appears in:** *Every distributed design in Phase 7*

**Cache-Aside**

App reads from cache first; on miss, reads from DB and populates cache (lazy loading).

**Appears in:** *URL shortener, News feed, YouTube, Twitter, Key-value store, Instagram, Amazon*

**Write-Behind**

Writes go to cache or queue first, then are written to DB asynchronously to reduce write latency.

**Appears in:** *Twitter, WhatsApp, Uber, Amazon*

**Event Sourcing**

Store state as a log of events; current state is derived by replaying the event log.

**Appears in:** *Uber, WhatsApp, Amazon*

**CQRS**

Separate read and write models (and often storage) so each can be scaled and optimized independently.

**Appears in:** *Twitter, Instagram, YouTube*

**Circuit Breaker**

Stops calling a failing dependency for a period to avoid cascading failures and allow recovery.

**Appears in:** *Uber, Amazon, any multi-service design*

**Bulkhead**

Isolates resources (e.g. thread pools, connections) so one failing component cannot starve others.

**Appears in:** *Amazon, Uber*

**Leader-Follower**

One replica (leader) handles writes; others (followers) replicate and serve reads.

**Appears in:** *URL shortener, WhatsApp, any DB scaling*

**Shard-per-User**

Partition data by user ID so each user's data lives in a dedicated shard for predictable load.

**Appears in:** *WhatsApp, Instagram, Twitter, Uber*

**🎯 Google Interview Questions — speak these out loud:**

* *You're designing Twitter. Where would you apply CQRS and why?*

* *Explain the circuit breaker pattern. Draw the three states. What causes each transition?*

* *Your Amazon checkout service calls 8 downstream services. One is slow. How does the bulkhead pattern help?*

* *What is event sourcing? What are its advantages over a traditional database? What are its costs?*

* *When would you use Write-Behind caching? What can go wrong and how do you handle it?*

# **PHASE 7 — PRACTICE SYSTEM DESIGN PROBLEMS**

*For each problem below, set a 45-minute timer and answer the Walkthrough Questions out loud, in order, as if an interviewer is asking them. This builds the speaking fluency and structured thinking Google actually tests.*

## **BASIC PROBLEMS**

### **1\. Design a Rate Limiter**

*Patterns: Token bucket, sliding window, Redis counters, distributed state*

**Q1.** Clarify: Is this per user, per IP, or per API key? What's the rate — 100 req/min? What should happen when the limit is hit — reject or queue?

**Q2.** Estimate: If we have 10M users each making 100 req/min at peak, how many limit-check operations per second does our system need to handle?

**Q3.** What data structure stores the request count? Walk me through the token bucket — what fields does each entry have?

**Q4.** Where does this state live? Why does it need to be in Redis instead of each server's local memory?

**Q5.** How do you handle a race condition when two requests arrive simultaneously and both see 'limit not exceeded'?

**Q6.** What response do you return to a rate-limited user? What headers? Why does the Retry-After header matter?

**Q7.** How would the design change if limits vary by user tier — free users get 100/min, paid users get 10k/min?

**Q8.** Your Redis node for rate limiting goes down. What happens? How do you fail open vs fail closed, and which do you choose?

### **2\. Design URL Shortener (TinyURL)**

*Patterns: Cache-Aside, Leader-Follower, Consistent Hashing*

**Q1.** Clarify: Do we need custom short codes? Analytics (click tracking)? Expiration? How short must the URL be?

**Q2.** Estimate: 100M URLs created per day, 10B redirects per day. What's the read/write ratio? How much storage do we need over 5 years?

**Q3.** How do you generate the short code? Compare: random hash, auto-increment ID \+ base62 encoding, MD5 of long URL. What are the collision risks?

**Q4.** Walk me through the read path — user visits tinyurl.com/abc123 — what happens at each layer?

**Q5.** The redirect path is the hot path (10B/day). How do you cache short→long mappings? What is the cache hit rate and what happens on a miss?

**Q6.** How do you store the mappings? SQL or NoSQL? What does the schema look like?

**Q7.** A URL has been shared on Twitter and gets 10M hits in an hour. How does your system handle this spike?

**Q8.** How would you add click analytics — track country, device, referrer — without slowing down the redirect?

### **3\. Design a Key-Value Store (Redis-like)**

*Patterns: Consistent Hashing, Leader-Follower, Write-Behind*

**Q1.** Clarify: In-memory only or persistent? Single-node or distributed? What consistency guarantees?

**Q2.** What data structures do you need internally — hash map for O(1) lookups, linked list for LRU tracking?

**Q3.** How does LRU eviction work? Walk me through what happens when the cache is full and a new key arrives.

**Q4.** How do you shard keys across multiple nodes? Walk me through consistent hashing.

**Q5.** If a node goes down, what happens to its keys? How do you replicate data for fault tolerance?

**Q6.** How do you handle TTL expiration — active polling vs lazy deletion vs combination?

**Q7.** What is a hot key and how do you handle it? (One key getting 100k reads/second)

**Q8.** Walk me through a write. Does it go to one replica? All replicas? What are the consistency options?

### **4\. Design a Notification System**

*Patterns: Message Queue, Event-Driven, Rate Limiting*

**Q1.** Clarify: What channels — push, email, SMS? Real-time or batch? How many notifications per day? User preferences and opt-outs?

**Q2.** Estimate: 1B notifications per day across channels. What's that in messages per second? How does it differ by channel (email is slow, push is fast)?

**Q3.** Walk me through the flow when an event triggers a notification — from the event source to the user's device.

**Q4.** Why does this need a message queue? What happens if you skip it and call notification services directly?

**Q5.** How do you handle delivery failures? A user's push token is expired. An email bounces. What do you do?

**Q6.** How do you prevent spamming a user with 500 notifications in one minute? Where does rate limiting sit in this pipeline?

**Q7.** How do you handle user notification preferences — user X wants email but not SMS, user Y wants neither on weekends?

**Q8.** How do you guarantee a notification is delivered exactly once?

### **5\. Design Pastebin**

*Patterns: Cache-Aside, Object Storage, Expiration*

**Q1.** Clarify: Max paste size? Expiration? Private pastes? Syntax highlighting? Do we need search?

**Q2.** Estimate: 10M pastes created/day, 100M reads/day. Average paste \= 10KB. Storage needed over 3 years?

**Q3.** Where do you store the paste content — database or object storage (S3-like)? Why?

**Q4.** How do you generate the unique paste ID? What's the collision risk at 10M/day?

**Q5.** Hot pastes get millions of reads. How do you cache them? Where does the cache sit?

**Q6.** A paste is set to expire in 7 days. How do you implement expiration — cron job, TTL, lazy deletion?

**Q7.** How do you handle private pastes that should only be accessible via a secret link?

## **INTERMEDIATE PROBLEMS**

### **6\. Design WhatsApp / Chat System**

*Patterns: Event Sourcing, Write-Behind, Shard-per-User, Leader-Follower*

**Q1.** Clarify: 1-to-1 chat only or also group chat? Max group size? Message persistence? Media (images/video)? Read receipts?

**Q2.** Estimate: 2B users, 100B messages/day. What's the write RPS? How much storage for messages over 5 years if avg message \= 100 bytes?

**Q3.** How does a message get from User A's phone to User B's phone? Walk through every hop — client → server → delivery → client.

**Q4.** What protocol connects clients to servers — WebSocket, long polling, or short polling? Compare each.

**Q5.** User B is offline when User A sends a message. How do you store and deliver it when B comes online?

**Q6.** How do you shard the message storage? By user\_id? By conversation\_id? What are the tradeoffs?

**Q7.** How do read receipts work? When does the 'delivered' tick appear vs the 'read' tick?

**Q8.** A group chat has 1000 members. User X sends a message. How many DB writes does that trigger? How do you scale this?

**Q9.** How do you handle media files (photos, videos)? Do they go through your chat servers or straight to object storage?

### **7\. Design Twitter / News Feed (Timeline)**

*Patterns: CQRS, Cache-Aside, Write-Behind, Shard-per-User, Fanout*

**Q1.** Clarify: Home timeline or user timeline? Ranking (algorithmic or chronological)? Tweet character limit? Retweets?

**Q2.** Estimate: 300M DAU, each reads 100 tweets, each posts 1 tweet/day. Read RPS? Write RPS? Read/write ratio?

**Q3.** Walk me through what happens when a user posts a tweet — from POST request to appearing in followers' feeds.

**Q4.** What is the fanout problem? If a celebrity has 10M followers and tweets, how do you update 10M timelines?

**Q5.** Compare fanout-on-write vs fanout-on-read. What does Twitter actually use and for which users?

**Q6.** How do you store the home timeline for each user? As a pre-computed list in Redis or computed on the fly?

**Q7.** A 'followed' user has 50M followers and tweets 10 times a day. How do you handle this celebrity problem?

**Q8.** How do you rank tweets in a timeline — chronological is easy, but what about algorithmic ranking at scale?

**Q9.** How do you handle the search feature — finding tweets by keyword?

### **8\. Design YouTube**

*Patterns: CDN, CQRS, Cache-Aside, Event-Driven, Object Storage*

**Q1.** Clarify: Focus on upload+playback or also recommendations, comments, live streaming? Scale target?

**Q2.** Estimate: 500M DAU, 500 hours of video uploaded per minute. Storage for 1 year if avg video \= 500MB? CDN bandwidth for 1B views/day?

**Q3.** Walk me through what happens after a user clicks 'Upload video' — from client to the video appearing on the platform.

**Q4.** Video needs to be transcoded into multiple resolutions (1080p, 720p, 480p, 360p). How do you do this at scale?

**Q5.** How does video streaming work — does the client download the whole file or stream it in chunks? What protocol?

**Q6.** Where does the video actually live after processing? How does the CDN serve it globally with low latency?

**Q7.** How do you count views accurately? What problems arise with distributed view counting at 1B views/day?

**Q8.** How do you build the recommendation system — what signals do you use and how do you serve recommendations fast?

**Q9.** A video goes viral and gets 100M views in an hour. What happens to your CDN and origin servers?

### **9\. Design Uber (Ride-Sharing)**

*Patterns: Event Sourcing, Write-Behind, Circuit Breaker, Bulkhead, Geo-Sharding*

**Q1.** Clarify: Focus on matching, location tracking, or pricing? Global or single city? Real-time ETA?

**Q2.** Estimate: 20M rides/day globally. Peak \= 5x average. How many location updates per second if each driver updates every 5 seconds with 5M active drivers?

**Q3.** Walk me through a ride from request to completion — every major step and service involved.

**Q4.** How do you track driver location in real time and find the nearest available driver to a rider?

**Q5.** What data store do you use for driver locations? Why is this not a standard SQL database?

**Q6.** How do you implement matching — nearest driver wins, or something more complex?

**Q7.** How does surge pricing work? What signals trigger it and how do you compute it in real time?

**Q8.** A city's cell towers go down. Drivers lose connectivity. How does your system handle this gracefully?

**Q9.** How do you handle the two-sided marketplace problem — too many drivers and no riders, or too many riders and no drivers?

### **10\. Design Google Drive / Dropbox**

*Patterns: Event Sourcing, Leader-Follower, Cache-Aside, Object Storage*

**Q1.** Clarify: Individual file storage, real-time collaboration, or both? Max file size? Sharing/permissions? Versioning?

**Q2.** Estimate: 1B users, avg 15GB storage each. Total storage? Write RPS if 1% of users upload 1 file/day at 1MB each?

**Q3.** Walk me through what happens when a user uploads a 10GB file.

**Q4.** How do you split large files into chunks? Why is chunking important for reliability and resumability?

**Q5.** How does file sync work? User edits a file on laptop — how does it appear on their phone 2 seconds later?

**Q6.** How do you detect and upload only the changed parts of a modified file (delta sync)?

**Q7.** How do you implement file versioning? How much storage does it consume and how do you limit it?

**Q8.** User A shares a folder with User B. How do permissions work? What happens when User A revokes access?

**Q9.** How do you implement real-time collaborative editing (like Google Docs)? What is the hardest technical problem?

### **11\. Design Autocomplete / Typeahead**

*Patterns: Trie, Cache-Aside, Pre-computation, CQRS*

**Q1.** Clarify: Global suggestions or personalized? How many suggestions to return? What's the latency requirement (must be \<50ms)?

**Q2.** Estimate: 5B searches/day. 20% are typeahead prefix queries. Each user types 5 characters average before selecting. What's the RPS for suggestion queries?

**Q3.** What data structure stores prefix-to-suggestion mappings efficiently? Describe a Trie.

**Q4.** How do you rank suggestions — by popularity, recency, personalization? Where does this ranking data come from?

**Q5.** A Trie for all Google search terms would be huge. How do you fit it in memory? What do you cache?

**Q6.** How do you update suggestions in real time as new trending queries appear? Can you update the Trie live?

**Q7.** How does this work across multiple servers? Does every server hold the full Trie?

**Q8.** How do you handle different languages and character sets?

## **ADVANCED PROBLEMS**

### **12\. Design Google Search (Web Crawler \+ Index)**

*Patterns: Event-Driven, Distributed Queue, MapReduce-style, CQRS*

**Q1.** Clarify: Full Google-scale search or a simplified version? Focus on crawling, indexing, or query serving?

**Q2.** Estimate: 5B web pages, each 1MB average. Storage just for raw pages? If you recrawl everything monthly, what's your crawl RPS?

**Q3.** Walk me through the full pipeline from 'page exists on the web' to 'user finds it via search'.

**Q4.** How does a web crawler work? How do you avoid crawling the same page twice? How do you handle circular links?

**Q5.** How do you build an inverted index — map from word to list of pages containing that word?

**Q6.** How do you rank pages? Explain PageRank at a high level and why it's hard to compute at scale.

**Q7.** A user searches 'best coffee NYC'. How do you go from query to ranked results in under 200ms?

**Q8.** The web has duplicate content and spam. How do you detect and filter it?

**Q9.** How do you keep the index fresh — pages update constantly. How quickly should new content appear in search?

### **13\. Design Google Docs (Collaborative Editing)**

*Patterns: Event Sourcing, Operational Transform / CRDT, WebSocket, Leader-Follower*

**Q1.** Clarify: How many concurrent editors per doc? Real-time or near-real-time sync? Rich text or plain text?

**Q2.** Estimate: 1B docs, 100M concurrent editors. What's the WebSocket connection count? What's the message rate if each user sends 1 keystroke per 200ms?

**Q3.** Walk me through what happens when two users edit the same paragraph at the same time.

**Q4.** What is Operational Transform (OT)? How does it resolve conflicting edits? Give a concrete example.

**Q5.** What is a CRDT? How is it different from OT and why are some teams moving to it?

**Q6.** How do you persist the document? Do you store the full document or a log of operations?

**Q7.** How do you show cursor positions of other editors in real time?

**Q8.** User A has a slow internet connection and falls 30 seconds behind. What happens when they reconnect?

**Q9.** How do you implement version history — user can see the document as it was 3 days ago?

### **14\. Design Google Maps / Location Service**

*Patterns: Geo-Sharding, Event-Driven, Cache-Aside, Graph Algorithms*

**Q1.** Clarify: Focus on map rendering, routing, real-time traffic, or nearby search? Scale — global?

**Q2.** Estimate: 1B map tile requests/day. Each tile \= 50KB. CDN bandwidth? If 100M users request routes/day, what's route computation RPS?

**Q3.** How do you store and serve map tiles efficiently? What data format, what storage, how does CDN help?

**Q4.** How do you represent the road network for routing algorithms? What data structure?

**Q5.** Walk me through how you compute the fastest route from A to B across a road network with millions of nodes.

**Q6.** How do you incorporate real-time traffic into routing? What's the data pipeline from traffic sensor to route update?

**Q7.** How do you handle 'nearby search' — find all restaurants within 5km of a location? What data structure helps?

**Q8.** How do you geo-shard your data? Why can't you just use standard database sharding for location data?

**Q9.** A natural disaster changes road conditions. How quickly can you update routing for 1B users?

### **15\. Design a Distributed Message Queue (Kafka-like)**

*Patterns: Leader-Follower, Consistent Hashing, Event Sourcing, Write-Behind*

**Q1.** Clarify: Ordering guarantees? At-least-once or exactly-once delivery? Persistence? Replay support?

**Q2.** Estimate: 10M messages/second across all topics. Each message \= 1KB. Storage per day?

**Q3.** What is a topic, partition, and consumer group in Kafka? How do they relate?

**Q4.** How do you guarantee ordering? Within a partition or globally? What's the tradeoff?

**Q5.** Walk me through the write path — producer sends message to partition leader.

**Q6.** Walk me through the read path — consumer reads from a partition.

**Q7.** A partition leader crashes. How do you elect a new leader without losing messages?

**Q8.** How does Kafka achieve high throughput? What disk and networking optimizations does it use?

**Q9.** How do you implement exactly-once delivery? Why is it so hard and what does it cost?

### **16\. Design Amazon (E-commerce Platform)**

*Patterns: Circuit Breaker, Bulkhead, CQRS, Event Sourcing, Cache-Aside, Write-Behind*

**Q1.** Clarify: Focus on product catalog, shopping cart, checkout, inventory, or order management?

**Q2.** Estimate: 300M products, 2B users, 10M orders/day. Write RPS for inventory updates? Read RPS for product pages?

**Q3.** Walk me through the full checkout flow — from user clicking 'Buy' to order confirmed. Name every service touched.

**Q4.** How do you handle inventory — user adds item to cart, but stock runs out before checkout. What happens?

**Q5.** How do you prevent overselling? 10 users try to buy the last item simultaneously. How do you ensure only 1 succeeds?

**Q6.** Your checkout calls: inventory, payment, fraud, shipping, email services. Payment is slow. How do Circuit Breaker and Bulkhead protect the rest of checkout?

**Q7.** How do you handle a flash sale — 1M users hitting 'Buy' at exactly 9:00am? Walk through your queue and rate limiting strategy.

**Q8.** How does product search work at Amazon scale — 300M products, millions of facets (price, ratings, category)?

**Q9.** An order is placed but payment fails 10 minutes later. How do you handle this with eventual consistency and event sourcing?

### **17\. Design a Key-Value Store at Scale (Dynamo-style)**

*Patterns: Consistent Hashing, Vector Clocks, Quorum, Gossip Protocol, Leader-Follower*

**Q1.** Clarify: Strong consistency or eventual? Single region or multi-region? Read-heavy or write-heavy?

**Q2.** Estimate: 100M keys, each 100 bytes. With 3x replication, total storage? At 1M writes/second, how many nodes do you need?

**Q3.** How do you distribute keys across nodes using consistent hashing? What is a virtual node and why does it help?

**Q4.** How do you handle a node failure — reads and writes must still work. What is quorum (N, W, R)?

**Q5.** Walk me through a write with N=3, W=2. Which nodes receive the write? What happens if one is unavailable?

**Q6.** Two clients write to the same key simultaneously on different replicas. You now have conflicting versions. How do you resolve it?

**Q7.** What is a vector clock? How does it help detect and resolve conflicts?

**Q8.** How do you detect that a node has failed and that another node has joined? What is the gossip protocol?

**Q9.** How does anti-entropy / Merkle tree reconciliation keep replicas in sync?

# **Practice Framework for Every Session**

Use this structure every time you practice a Phase 7 problem out loud:

* Step 1 — Clarify requirements (2–3 min): Ask the walkthrough questions in Q1 out loud. Don't assume. Drive this.

* Step 2 — Estimate scale (2–3 min): Do the math out loud. Write numbers. Anchor your design in reality.

* Step 3 — High-level design (5 min): Draw the major components. API → LB → App servers → Cache → DB. Speak each choice.

* Step 4 — Deep dive (15–20 min): Go through the remaining walkthrough questions. For each one, name the problem, propose a solution, state the tradeoff.

* Step 5 — Identify bottlenecks (3–5 min): Ask yourself — what breaks at 10x scale? Where is the SPOF? What have I not solved?

* Step 6 — Summarize tradeoffs (2 min): Quickly recap the 2–3 biggest design decisions you made and why.

**After each session, ask yourself:**

* Did I drive the conversation or wait to be led?

* Did I state a tradeoff for every major decision?

* Did I handle the interviewer's curveball (the 'what if X fails' questions)?

* Was I speaking clearly or just listing bullet points?

