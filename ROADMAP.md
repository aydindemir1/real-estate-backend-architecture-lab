# Real Estate Backend Architecture Lab — Backend Roadmap

## Scope

Day 1-6 on `main` is the stable baseline. The roadmap below starts at Day 7 and targets backend completion around Day 20. A Day is a milestone, not necessarily one calendar day.

## Guiding principles

- Do not reimplement technologies already proven in Day 1-6.
- Preserve AuthService and UserProfileService PostgreSQL flows.
- Use polyglot persistence only where the datastore teaches a distinct data-model or distributed-systems problem.
- Keep every Day on its own branch.
- Update code, tests, service design docs and roadmap status together in the same Day branch.
- Prefer short focused documents over one very large document.

## Service / datastore / architecture matrix

| Service | Primary datastore | Spring data stack | Architecture |
|---|---|---|---|
| AuthService | PostgreSQL | Spring Data JPA + Hibernate | Existing N-Layer baseline |
| UserProfileService | PostgreSQL | Spring Data JPA + Hibernate | Existing N-Layer baseline |
| AgentService | MySQL | Spring Data JPA + Hibernate | Clean Architecture |
| BuyerService | Couchbase | Spring Data Couchbase | Hexagonal Architecture |
| SellerService | Cassandra | Spring Data Cassandra | Onion Architecture |
| PropertyService | MongoDB | Spring Data MongoDB | Vertical Slice Architecture |
| SearchService | Elasticsearch | Spring Data Elasticsearch | Vertical Slice + CQRS Query Side |
| Shared infrastructure | Redis | Spring Data Redis | Cache / idempotency / rate-limit / ephemeral state |

## Day 7-20

### Day 7 — Polyglot Persistence & Architecture Foundations
Branch: `day/07-polyglot-persistence`

Introduce MySQL, Couchbase, Cassandra, MongoDB, Elasticsearch and Redis foundations; establish Clean, Hexagonal, Onion and Vertical Slice boundaries. Create SearchService foundation.

### Day 8 — Spring Security + OAuth2 + OIDC + Keycloak
Branch: `day/08-security-keycloak`

Add Keycloak-backed authentication/authorization, RBAC and service-to-service authentication. Keep legacy custom JWT only as an educational comparison where useful.

### Day 9 — REST + gRPC + GraphQL
Branch: `day/09-grpc-graphql`

Keep REST as public baseline, add gRPC for internal service-to-service use cases and GraphQL for flexible read/query use cases.

### Day 10 — Kafka + Spring Cloud Stream + Spring Cloud Function
Branch: `day/10-kafka-stream-function`

Keep RabbitMQ for command/task messaging and add Kafka for domain-event streaming.

### Day 11 — Outbox + Inbox + Idempotency + Retry + DLQ
Branch: `day/11-outbox-inbox-idempotency`

Implement reliable messaging patterns without pretending Cassandra offers the same transactional semantics as an RDBMS.

### Day 12 — CQRS + Elasticsearch Search Projection
Branch: `day/12-cqrs-search`

PropertyService/MongoDB is source of truth; Kafka projects property events into SearchService/Elasticsearch.

### Day 13 — Saga + Eventual Consistency
Branch: `day/13-saga`

Implement a property offer/reservation workflow with choreography and compensating actions.

### Day 14 — Advanced Resilience
Branch: `day/14-resilience`

Extend existing Circuit Breaker work with Retry, TimeLimiter, Bulkhead and Redis-backed rate limiting.

### Day 15 — Spring Cloud Bus + Vault
Branch: `day/15-bus-vault`

Use Config for normal configuration, Vault for secrets and Bus for change propagation.

### Day 16 — Unit + Integration + Testcontainers
Branch: `day/16-testing`

Build datastore, messaging, security and architecture-oriented automated tests.

### Day 17 — Contract + Failure-Path Testing
Branch: `day/17-contract-testing`

Add contract verification and systematic negative/failure testing.

### Day 18 — OpenTelemetry + Prometheus + Grafana + Loki + Tempo
Branch: `day/18-observability`

Extend the existing Micrometer/Zipkin baseline into logs, metrics and traces with correlation.

### Day 19 — Spring Cloud Task + Reindex / Reconciliation
Branch: `day/19-cloud-task`

Add short-lived maintenance jobs such as Elasticsearch reindex and reconciliation from MongoDB.

### Day 20 — Architecture Fitness + E2E + Backend Completion
Branch: `day/20-backend-completion`

Add ArchUnit fitness rules, end-to-end validation, final ADRs and backend completion documentation.

## Spring Cloud coverage

Already present: Gateway, Config, Eureka, OpenFeign, LoadBalancer, Circuit Breaker.

Planned: Stream, Function, Bus, Vault, Contract, Task.

After backend completion: Spring Cloud Kubernetes.

Documented comparison: Eureka vs Consul vs ZooKeeper.

## Post-backend phase

Docker hardening → Kubernetes native fundamentals → Spring Cloud Kubernetes → Jenkins → SonarQube → Nexus → Harbor → Argo CD / GitOps.
