# Infra notes

- **Pod** = **instance** = **one main app process** (= one application server) = **one application instance**.

## Hierarchy

```text
Kubernetes cluster (all services can live here; can split across clusters if you want)
├── Control plane (K8s core — scheduler, API; coordinates the cluster)
├── (optional) Istio control plane — pods in cluster; pushes config to Envoy proxy sidecars
├── (optional) Ingress / Gateway — pods in cluster; entry point for outside traffic into services
└── Worker nodes (= EC2 machines where your app runs; “worker” = not control plane)
      ├── Node A
      │     ├── Pod: service-X #1
      │     │     └── Container (running Docker image → JVM / app process)
      │     ├── Pod: service-X #2
      │     │     └── Container (running Docker image → JVM / app process)
      │     └── Pod: service-Y #1
      │           └── Container (running Docker image → JVM / app process)
      ├── Node B
      │     ├── Pod: service-X #3
      │     │     └── Container (running Docker image → JVM / app process)
      │     └── Pod: service-Y #2
      │           └── Container (running Docker image → JVM / app process)
      └── Node C
            ├── Pod: service-Z #1
            │     └── Container (running Docker image → JVM / app process)
            └── Pod: service-Z #2
                  └── Container (running Docker image → JVM / app process)
```

- **Orchestrator** — the Kubernetes **control plane** (API server, scheduler, controllers, etcd, …) **is** the orchestrator of the cluster: it matches **desired state** to what actually runs on worker nodes.
- **ZooKeeper** — separate product (not part of default K8s); tiny replicated **cluster brain** for locks, leader election, and membership (same family as **etcd**). Optional **pods** in the cluster (often its own StatefulSet); apps that need it connect as **clients**.
  - **Under the hood:** a small **replicated tree** of znodes (path + tiny payload) kept consistent across the ZK ensemble (3/5 nodes).
  - **Role:** **coordinate** that metadata — who is leader, who is alive, locks/config — so many app nodes agree without each talking to every peer.
- **Docker image** = immutable **package** (OS + runtime + your jar/app — not “just a jar”).
- **Container** = that image **running** (one live instance).
- **Per pod:** usually **one main container** (one image). Extra containers in the same pod = **sidecars** — not multiple copies of your app.

### Sidecars (all optional; a pod can have 0, 1, or several)

| Type | On API request path? | Role |
|------|----------------------|------|
| **Proxy** (e.g. Envoy, often installed via Istio) | Yes, when wired | Routes traffic, TLS, retries, limits — **proxy executes** retries |
| **Logging** | No | Ships logs to central storage |
| **Config reloader** | No | Refreshes mounted config files when Secrets change |

- **Istio** ≠ sidecar. **Istio control plane** (pods in cluster, e.g. `istio-system`) **pushes policy** to **Envoy proxy sidecars** on worker nodes; Envoy **executes** it (Istio does not retry for you — it configures the proxy).
  - **TLS / mTLS** (encrypted service-to-service traffic; mutual TLS = both sides prove identity).
  - **Retries** (network layer: timeouts, connection reset, 503 — not business failures like “payment declined”; those stay in app code).
  - **Limits** (rate / concurrency limits on traffic).
- **Pods** run **on** worker nodes (people don’t usually say “worker pods” — they mean **pods scheduled on worker nodes**). **Containers** run **inside** a pod.
- **Ingress / Gateway** = cluster **entry** for external clients — still **inside** the cluster (pods on nodes), not “outside Kubernetes.”
  - **L7 edge routing / LB**: host, path, headers, TLS termination → forward to the right **Service** (not a specific pod).
  - After that, **Kubernetes Service** picks **one healthy pod replica** (endpoint); traffic goes to that pod on its worker node (kube-proxy / CNI — not “pod ID” in the request).

## End-to-end traffic & communication

Connects **computer networking / HLD** vocabulary (DNS, LB, app servers, Redis, DB) with **Kubernetes** above. Detail on DNS steps, TTL, L4/L7 algorithms, TLS → separate networking notes.

### North–south (user → system)

1. **DNS** — name → IP(s) or **CNAME** (often to CDN); geo / weighted / multi-A; **TTL** controls cache (not the pod).
2. **CDN** (optional) — edge cache, TLS, DDoS; dynamic traffic to **origin** (usually LB).
3. **Global / regional load balancer** (L4 or L7) — health checks, picks **region / cluster pool**; public IP is **edge/LB/CDN**, not a pod IP.
4. **Cluster Ingress / Gateway** (L7, on clusters that receive that traffic) — host, path, TLS → **Service** (e.g. `order-service`).
5. **Service → pod** — one healthy replica; app handles request (stateless pod).
6. **Redis / DB** — same cluster **Service** or **managed** endpoint (ElastiCache, RDS); still **hostname + network + protocol**.
7. **Response** — same HTTP path back (pod → Ingress → LB/CDN → user).

Not every cluster needs public Ingress — some are **internal only** (Kafka, batch, payment in another VPC); user traffic hits **edge** clusters only.

### East–west (service ↔ service)

- **Same cluster:** caller uses **Service DNS** (`payment-service`, or `*.svc.cluster.local`) → **HTTP/gRPC/Kafka/Redis/DB wire** over cluster network (same node or different node — same model).
- **Different cluster:** no default shared Service DNS — **HTTPS URL**, private link / peering, API gateway, or **async** (Kafka). Still **network + protocol**, not the orchestrator routing each call.

### Networking notes ↔ this model

| Networking / HLD | Kubernetes / infra |
|------------------|-------------------|
| App server (stateless) | **Pod** replica behind a **Service** |
| Load balancer | **Cloud LB** (+ optional **Ingress** inside cluster) |
| Multiple server IPs (DNS) | Multiple **regional LB / Ingress** frontends |
| Geo DNS → Asia / US DC | Geo DNS → **cluster in that region** |
| Weighted DNS canary | Weight to **new region / LB pool** (coarse); finer canary → L7 / flags |
| Redis / database | **Service** to in-cluster pods or **managed** outside cluster |

### One picture (multi-region + in-cluster)

```text
                              ┌──────────┐
                              │   User   │
                              └────┬─────┘
                                   │ HTTPS (hostname)
                                   ▼
                         ┌─────────────────┐
                         │ DNS (+ TTL,     │
                         │  geo, weighted) │
                         └────────┬────────┘
                                  ▼
                         ┌─────────────────┐
                         │ CDN (optional)  │
                         └────────┬────────┘
                                  ▼
                    ┌─────────────────────────────┐
                    │ Global / regional LB        │
                    │ (L4/L7, health checks)    │
                    └──────────┬──────────┬───────┘
                               │          │
              ┌────────────────▼──┐    ┌───▼────────────────┐
              │ Cluster — Asia    │    │ Cluster — US       │
              │ (order replicas)  │    │ (order replicas)   │
              └────────┬──────────┘    └────────┬───────────┘
                       │ Ingress/GW              │ Ingress/GW
                       ▼                         ▼
                 order-Service              order-Service
                       │                         │
                       ▼                         ▼
                  order pod(s)               order pod(s)
                       │
         ┌─────────────┼─────────────┬──────────────────────────┐
         │             │             │                          │
         ▼             ▼             ▼                          ▼
  payment-Service  Redis Svc    Kafka brokers          other cluster:
  (same cluster)   or managed   (Service or            payment via
         │         cache        managed)               HTTPS / private API
         ▼
   payment pod(s)

  (Clusters with no path from global LB = internal-only — no user Ingress required.)
```

**Example — place order:** User → DNS/LB/CDN → **order** Ingress → order pod → **payment** (`payment-service` or cross-cluster URL) / **Kafka** / **Redis** as designed → response from order (or BFF) to user.

## Inside one container (zoom-in)

```text
Container (running image)
└── One main app process (e.g. one JVM for order-service)
      ├── Thread pool: HTTP / API requests  → many threads
      ├── Thread pool: async / background jobs  → many threads
      └── (optional) other pools per task type — not one pool for the whole company, one service picks its own pools
```

Threads and thread pools are **inside the process** — not a K8s concept; K8s only sees the pod/container.

## Example — one cluster, multiple replicas

e.g. If the system has **3 order-service** replicas and **2 payment-service** replicas, **worker nodes** in one prod cluster can look like this (control plane is separate):

```text
Kubernetes cluster (prod)
├── Control plane (EC2 — orchestration only, not your app pods)
└── Worker nodes
      ├── Node A (EC2)
      │     ├── Pod: order-service #1
      │     │     └── Container (order-service image running)
      │     ├── Pod: order-service #2
      │     │     └── Container (order-service image running)
      │     └── Pod: payment-service #1
      │           └── Container (payment-service image running)
      └── Node B (EC2)
            ├── Pod: order-service #3
            │     └── Container (order-service image running)
            └── Pod: payment-service #2
                  └── Container (payment-service image running)
```

## What runs as an image vs managed storage

**Not everything is a Docker/OCI image you run in a pod.**

### App services (order, payment, …)

- **On Kubernetes (usual):** deploy a **container image**; inside the container, the process runs your build (e.g. JVM + **jar**, or a native binary). New release = **new image tag**, not “drop jar on the node.”
- **Without K8s:** VM/bare metal → `java -jar app.jar` (or equivalent) is still valid — same app, different packaging.

### Data & messaging (Postgres, Redis, Kafka, …)

- **Managed (common at scale):** RDS, ElastiCache, MSK, DynamoDB, **S3** — cloud provider operates them; apps use **endpoint + API + credentials**. You don’t ship a “DB jar”; often **no pod you own** for that tier.
- **Self-hosted on K8s:** Postgres / Redis / Kafka **can** run as **images** in pods (e.g. StatefulSet) — still storage/messaging **roles**, but packaged as containers if **you** operate them.

### Quick map

| Layer | Role | Typical deploy |
|-------|------|----------------|
| **App** | Compute + business logic | **Image → pod** (jar/binary **inside** image) or jar on VM |
| **Cache / queue / DB (managed)** | Persist or serve data | **AWS/GCP/Azure service** (network access) |
| **Cache / queue / DB (self-hosted)** | Same roles | **Image → pod** or VMs you manage |
| **Object store (S3, etc.)** | Durable objects | **API only** — not your container |

**One line:** Apps = **processes you build and ship**; databases and object stores = **storage/messaging layer** — usually **managed cloud**, sometimes **DB/Kafka images** on the cluster if you run them yourself.
