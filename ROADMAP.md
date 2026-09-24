# Real Estate Backend Architecture Lab — Backend Roadmap

## Kapsam

`main` branch'indeki Day 1–6 stabil baseline olarak kabul edilir.

Day 7 sonrası çalışma ilkesi:

- Bir Day mümkün olduğunca **tek service** veya **tek ana engineering konusu** içerir.
- İçerik azaltılmaz; büyük milestone'lar daha küçük Day'lere bölünür.
- Küçük, anlamlı commit'ler kullanılır.
- Her Day build + test + docs ile kapanır.
- Önkoşul tamamlanmadan sonraki Day'e geçilmez.

## Day 7–33

### Day 7 — Build & Local Data Infrastructure Foundation
Branch: `day/07-build-data-infra`
- datastore dependency catalog
- SearchService module foundation
- Docker Compose secret hygiene
- MySQL/Couchbase/Cassandra/MongoDB/Elasticsearch/Redis local infra
- temporary service PostgreSQL cleanup

### Day 8 — AgentService: MySQL + Clean Architecture
Branch: `day/08-agent-mysql-clean`
- Agent domain
- Create/Get/ChangeAvailability
- MySQL/JPA adapter
- migration
- unit/integration/architecture tests

### Day 9 — BuyerService: Couchbase + Hexagonal Architecture
Branch: `day/09-buyer-couchbase-hexagonal`
- BuyerPreferences
- SavedSearch
- inbound/outbound ports
- Couchbase adapter
- tests

### Day 10 — SellerService: Cassandra + Onion Architecture
Branch: `day/10-seller-cassandra-onion`
- Seller
- ListingSubmission
- query-first Cassandra model
- tests
- no reliable cross-service publish yet

### Day 11 — PropertyService: MongoDB + Vertical Slice Architecture
Branch: `day/11-property-mongodb-vertical-slice`
- canonical Property Aggregate
- GetProperty
- PublishProperty
- optimistic concurrency
- Mongo tests

### Day 12 — SearchService: Elasticsearch + CQRS Query Side Foundation
Branch: `day/12-search-elasticsearch-cqrs`
- explicit mapping
- SearchProperties
- text/filter/range
- query-side architecture tests
- no Kafka projection yet

### Day 13 — Redis Infrastructure Foundation
Branch: `day/13-redis-foundation`
- typed config
- serializer
- key/TTL conventions
- connectivity tests
- Redis remains non-canonical

### Day 14 — Spring Security + OAuth2/OIDC + Keycloak
Branch: `day/14-security-keycloak`
- Keycloak realm/client/roles/scopes
- Gateway + downstream Resource Server
- ownership
- Client Credentials
- AuthService migration boundary

### Day 15 — gRPC Internal Communication
Branch: `day/15-grpc`
- AgentAvailability protobuf contract
- AgentService gRPC server adapter
- BuyerService outbound port/client adapter
- deadline/auth/error mapping
- integration tests

### Day 16 — GraphQL Read API
Branch: `day/16-graphql`
- SearchService GraphQL schema
- resolver over existing SearchPropertiesHandler
- authorization
- bounded query/page cost
- GraphQL tests
- REST remains primary baseline

### Day 17 — Kafka + Spring Cloud Stream + Spring Cloud Function
Branch: `day/17-kafka-stream-function`
- Kafka local infra
- event envelope
- property.events foundation
- Stream bindings
- Function consumer model
- partition/group/order tests
- **no business event publication bypassing reliability layer**

Offer event contracts/publishing are activated with the real Offer workflow, not prematurely.

### Day 18 — Outbox + Inbox + Idempotent Consumer
Branch: `day/18-outbox-inbox`
- Property Mongo Outbox
- Inbox/Processed Message
- duplicate-safe consumer
- atomic local side-effect strategy
- Outbox polling
- crash-window tests
- reliable-publication design for Couchbase and Cassandra documented

### Day 19 — Retry + DLT/DLQ + Replay Safety
Branch: `day/19-retry-dlt-dlq`
- retryable/non-retryable classification
- Kafka bounded retry/DLT
- RabbitMQ DLX/DLQ
- poison-message policy
- replay metadata/runbook
- replay-safety tests

### Day 20 — Cassandra Reliable Outbound Messaging
Branch: `day/20-cassandra-reliable-outbound`
- Cassandra-friendly pending outbound message design
- Seller listing command reliable dispatch to RabbitMQ
- reusable reliable outbound foundation for later Seller Kafka decisions
- publisher confirms
- Property listing-command inbox/dedup
- Property creation from listing command
- reliability ADR/tests

### Day 21 — CQRS + Elasticsearch Event Projection
Branch: `day/21-cqrs-search-projection`
- Property lifecycle events via reliable publication path
- Search consumer group
- idempotent projection
- stale-event guard
- projection freshness
- eventual consistency
- E2E publish-to-search

### Day 22 — Saga + Offer / Reservation Workflow
Branch: `day/22-saga-offer-reservation`
- Offer Aggregate/state machine
- durable CreateOffer idempotency
- OfferRequested
- Property hold
- Seller pending-offer projection
- accept/reject
- reserve/release compensation
- concurrency/E2E

**Correctness rule:** Redis alone is not the correctness source for Offer idempotency. Durable idempotency ownership lives with BuyerService/Couchbase; Redis may only accelerate it.

### Day 23 — Advanced Resilience
Branch: `day/23-resilience`
- timeout budget
- retry ownership
- Circuit Breaker
- TimeLimiter only where non-redundant
- Bulkhead
- Redis-backed Gateway Rate Limiting
- resilience tests/metrics

### Day 24 — Spring Cloud Vault
Branch: `day/24-vault`
- secret inventory
- Config vs Secret ownership
- Vault local infra
- per-service secret paths/policies
- secret migration
- fail-fast/redaction
- rotation/outage test

### Day 25 — Spring Cloud Bus
Branch: `day/25-bus`
- Bus over RabbitMQ
- refresh-safe property inventory
- targeted RefreshScope
- protected busrefresh
- distributed refresh tests
- control-plane/data-plane separation

### Day 26 — Unit + Integration + Testcontainers Hardening
Branch: `day/26-testing`
- test inventory/taxonomy
- Gradle test lifecycle
- Testcontainers governance
- coverage gaps
- concurrency/idempotency/security hardening
- deterministic/flaky-test policy

### Day 27 — Contract Testing
Branch: `day/27-contract-testing`
- Spring Cloud Contract for selected REST boundaries
- provider verification
- stubs/consumer tests
- Kafka event compatibility fixtures
- protobuf compatibility

### Day 28 — Failure-Path Testing
Branch: `day/28-failure-path-testing`
- datastore/broker/config/Vault/downstream outages
- poison messages
- DLT/DLQ replay safety
- fail-fast configuration tests
- stable error semantics

### Day 29 — OpenTelemetry Instrumentation
Branch: `day/29-opentelemetry`
- OTel/Micrometer tracing
- structured logs
- traceId/spanId/correlationId
- HTTP/gRPC/Kafka/RabbitMQ propagation
- core infrastructure/business metrics
- low-cardinality guard
- telemetry failure isolation

### Day 30 — Prometheus + Grafana + Loki + Tempo
Branch: `day/30-observability-stack`
- Prometheus
- Grafana
- Loki
- Tempo
- supported log shipper
- dashboards
- sample actionable alerts
- observability Compose profile

### Day 31 — Spring Cloud Task + Reindex / Reconciliation
Branch: `day/31-cloud-task`
- Task metadata
- single/full reindex
- versioned index + stable alias
- checkpoint/resume
- reconciliation report/repair
- rollback/runbooks

### Day 32 — Architecture Fitness + Documentation Audit
Branch: `day/32-architecture-audit`
- ArchUnit consolidation
- dependency cycles/drift
- build/config/contract/persistence drift
- ADR reconciliation
- API/gRPC/GraphQL/messaging/security/observability docs audit
- Eureka vs Consul vs ZooKeeper comparison

### Day 33 — E2E + Recovery + Backend Completion
Branch: `day/33-backend-completion`
- Registration/Profile E2E
- Seller Listing E2E
- Property→Search E2E
- Offer accept/reject/concurrency E2E
- ownership/service-identity E2E
- Kafka/RabbitMQ/Search recovery exercises
- controlled replay
- final Gradle/Compose verification
- backend completion report
- README/roadmap completion status

## Cross-cutting correctness decisions

### Reliable outbound messaging
- Property/MongoDB uses Outbox.
- Seller/Cassandra uses Cassandra-friendly durable pending outbound messages + dispatcher/reconciliation.
- Buyer/Couchbase must have an explicit durable reliable-publication strategy before OfferRequested is considered reliable.
- No service may perform a state change and then rely on an unprotected best-effort broker send for a critical workflow.

### Offer idempotency
- HTTP `Idempotency-Key` correctness must survive Redis loss/restart.
- BuyerService durable datastore owns the idempotency record/result association.
- Redis may be used as an accelerator/cache, never the sole correctness store.

### Event activation
- Contracts can be designed early.
- Producers/consumers are wired to business workflows only when the corresponding aggregate/use-case exists.
- Day 17 event-streaming foundation must not bypass Day 18–20 reliability rules.

## Commit ilkesi
Her Day küçük, anlamlı ve review edilebilir commit'lere bölünür. Unrelated architecture layers tek commit'te birleştirilmez.

## Spring Cloud kapsamı

Mevcut:
- Spring Boot
- Gateway
- Config
- Netflix Eureka
- OpenFeign
- LoadBalancer
- Circuit Breaker baseline

Roadmap:
- Stream
- Function
- Vault
- Bus
- Contract
- Task

Backend sonrası:
- Spring Cloud Kubernetes

Comparison:
- Eureka vs Consul vs ZooKeeper

## Backend sonrası

Docker hardening
→ Kubernetes native fundamentals
→ Spring Cloud Kubernetes
→ Jenkins
→ SonarQube
→ Nexus
→ Harbor
→ Argo CD / GitOps
