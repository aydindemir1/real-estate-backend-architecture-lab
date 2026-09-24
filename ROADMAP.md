# Real Estate Backend Architecture Lab — Backend Roadmap

## Kapsam

`main` branch'indeki Day 1–6 stabil baseline olarak kabul edilir.

Bu roadmap Day 7 sonrasında **küçük ve odaklı milestone** yaklaşımına geçer.

## Temel çalışma ilkesi

- Bir Day gerçek takvim günü olmak zorunda değildir.
- Bir Day mümkün olduğunca **tek service** veya **tek ana infrastructure/architecture konusu** içerir.
- Aynı Day içinde 4–5 service birden implement edilmez.
- İçerik azaltılmaz; yalnızca daha fazla Day'e bölünür.
- Küçük, anlamlı commit'ler kullanılır.
- Her Day kendi branch'inde geliştirilebilir.
- Her Day build + test + docs ile tamamlanır.
- Bir Day bitmeden sonraki service implementation'ına geçilmez.

## Day 7–26

### Day 7 — Build & Local Data Infrastructure Foundation
Branch: `day/07-build-data-infra`

Amaç:
- Day 7+ datastore dependency alias'larını hazırlamak
- SearchService module registration foundation
- Docker Compose credential hygiene
- .env / .env.example
- MySQL, Couchbase, Cassandra, MongoDB, Elasticsearch, Redis container foundation
- eski temporary PostgreSQL service'lerini kontrollü temizlemek

Business service implementation yapılmaz.

### Day 8 — AgentService: MySQL + Clean Architecture
Branch: `day/08-agent-mysql-clean`

- AgentService PostgreSQL -> MySQL
- Clean Architecture
- CreateAgent
- GetAgent
- ChangeAvailability
- JPA adapter
- migration
- MySQL Testcontainers
- architecture tests

### Day 9 — BuyerService: Couchbase + Hexagonal Architecture
Branch: `day/09-buyer-couchbase-hexagonal`

- BuyerService PostgreSQL/JPA -> Couchbase
- Hexagonal Architecture
- BuyerPreferences
- SavedSearch foundation
- inbound/outbound ports
- Couchbase adapter
- integration + architecture tests

### Day 10 — SellerService: Cassandra + Onion Architecture
Branch: `day/10-seller-cassandra-onion`

- SellerService -> Cassandra
- Onion Architecture
- Seller
- ListingSubmission
- query-first table design
- partition/clustering
- Cassandra Testcontainers
- RabbitMQ cross-service reliable dispatch henüz yok

### Day 11 — PropertyService: MongoDB + Vertical Slice Architecture
Branch: `day/11-property-mongodb-vertical-slice`

- Property canonical Aggregate
- MongoDB
- Vertical Slice
- GetProperty
- PublishProperty foundation
- optimistic concurrency
- Mongo integration tests

### Day 12 — SearchService: Elasticsearch + CQRS Query Side Foundation
Branch: `day/12-search-elasticsearch-cqrs`

- SearchService runtime module
- Elasticsearch
- explicit mapping
- SearchProperties
- basic text/filter/range query
- Vertical Slice + CQRS Query Side
- Elasticsearch integration tests

Kafka projection henüz yok.

### Day 13 — Redis Infrastructure Foundation
Branch: `day/13-redis-foundation`

- Spring Data Redis
- typed configuration
- connectivity
- key naming
- TTL conventions
- integration tests

Offer idempotency, Rate Limiting ve Saga state daha sonraki ilgili Day'lerde aktive edilir.

### Day 14 — Spring Security + OAuth2 + OIDC + Keycloak
Branch: `day/14-security-keycloak`

- Spring Security
- Keycloak
- OAuth2/OIDC
- RBAC
- scopes
- ownership
- service-to-service authentication
- identity mapping

### Day 15 — REST + gRPC + GraphQL
Branch: `day/15-grpc-graphql`

- REST baseline
- BuyerService -> AgentService gRPC availability
- GraphQL flexible read/query
- protocol boundaries
- contract tests

### Day 16 — Kafka + Spring Cloud Stream + Spring Cloud Function
Branch: `day/16-kafka-stream-function`

- Kafka
- property.events
- offer.events foundation
- Stream
- Function
- partition key / consumer group / offset
- RabbitMQ command role korunur

### Day 17 — Outbox + Inbox + Idempotency + Retry + DLQ/DLT
Branch: `day/17-reliable-messaging`

- reliable messaging
- Outbox
- Inbox
- Idempotent Consumer
- retry
- poison message
- DLQ/DLT
- SellerService Cassandra -> RabbitMQ reliable dispatch strategy finalize

### Day 18 — CQRS + Elasticsearch Event Projection
Branch: `day/18-cqrs-search-projection`

- Property Mongo source of truth
- Kafka property events
- Search Elasticsearch projection
- Eventual Consistency
- projection freshness

### Day 19 — Saga + Offer / Reservation Workflow
Branch: `day/19-saga-offer-reservation`

- Offer lifecycle
- Property hold
- Seller decision
- reserve/release
- Saga Choreography
- compensation
- concurrency rules

### Day 20 — Advanced Resilience
Branch: `day/20-resilience`

- Retry
- Circuit Breaker
- TimeLimiter
- Bulkhead
- Redis-backed Rate Limiting
- timeout budgets
- failure isolation

### Day 21 — Spring Cloud Bus + Vault
Branch: `day/21-bus-vault`

- Config vs Secret separation
- Vault
- Bus
- secret lifecycle
- config refresh governance

### Day 22 — Unit + Integration + Testcontainers Hardening
Branch: `day/22-testing`

- Unit
- Application
- Integration
- Testcontainers
- datastore/messaging/security tests
- concurrency/idempotency hardening

### Day 23 — Contract + Failure-Path Testing
Branch: `day/23-contract-failure-testing`

- Spring Cloud Contract
- REST contracts
- failure-path
- dependency outage
- DLQ/DLT
- resilience negative tests

### Day 24 — OpenTelemetry + Prometheus + Grafana + Loki + Tempo
Branch: `day/24-observability`

- logs
- metrics
- traces
- OpenTelemetry
- Prometheus
- Grafana
- Loki
- Tempo
- existing Zipkin comparison

### Day 25 — Spring Cloud Task + Reindex / Reconciliation
Branch: `day/25-cloud-task`

- Spring Cloud Task
- Elasticsearch reindex
- reconciliation
- rerunnable/idempotent maintenance job
- recovery workflow

### Day 26 — Architecture Fitness + E2E + Backend Completion
Branch: `day/26-backend-completion`

- ArchUnit
- architecture fitness
- E2E critical flows
- final ADR review
- final documentation
- runbook verification
- backend completion audit

## Commit ilkesi

Her Day küçük ve anlamlı commit'lere bölünür.

Örnek service Day'i:

1. `build(...): add datastore dependency`
2. `refactor(...): establish architecture packages`
3. `feat(...): add domain model`
4. `feat(...): add application use case`
5. `feat(...): add persistence adapter`
6. `feat(...): expose minimal API`
7. `test(...): add unit and integration tests`
8. `docs(...): update design and roadmap`

Tek devasa commit yapılmaz.

## Spring Cloud kapsamı

Mevcut:
- Gateway
- Config
- Eureka
- OpenFeign
- LoadBalancer
- Circuit Breaker

Planlanan:
- Stream
- Function
- Bus
- Vault
- Contract
- Task

Backend sonrasında:
- Spring Cloud Kubernetes

Karşılaştırma:
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
