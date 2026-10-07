# Real Estate Backend Architecture Lab — Backend Roadmap

## Kapsam

`main` branch'indeki Day 1–6 stabil baseline olarak kabul edilir.

Day 7 sonrası çalışma ilkesi:

- Bir Day mümkün olduğunca **tek service** veya **tek ana engineering konusu** içerir.
- İçerik azaltılmaz; büyük milestone'lar daha küçük Day'lere bölünür.
- Küçük, anlamlı commit'ler kullanılır.
- Her Day build + test + docs ile kapanır.
- Önkoşul tamamlanmadan sonraki Day'e geçilmez.

## Plan belgeleri ve süre ilkesi

[Day 7–45 ayrıntılı plan dizini](docs/roadmap/README.md) güncel kesin planlara bağlantı verir. Day 1–6 mevcut stabil baseline olarak korunur. Day 34–45 için ana konular önceden tanımlanmıştır; ayrıntılı dosya/adapter, görev, commit ve doğrulama planları artık `docs/roadmap` altında bulunur.

45 Day, 45 milestone anlamındadır; tek milestone gerektiğinde birden fazla takvim gününde tamamlanabilir. Ek kapsam ilgili güne bağlanır; başka bir ihtiyacı karşılayan mevcut teknoloji sırf görseldeki alternatif araç için değiştirilmez. Bu güncelleme backend kapsamındadır; DevOps programı ayrıca ele alınır.

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
- Ek öğrenme/uygulama: Veri seçimi, arama API tasarımı ve latency/throughput.
- Ayrıntılı ek görevler, commit sırası ve kabul ölçütleri: [Day 12 kesin planı](docs/roadmap/day-12-exact-file-plan.md).
- explicit mapping
- SearchProperties
- text/filter/range
- query-side architecture tests
- no Kafka projection yet

### Day 13 — Redis Infrastructure Foundation
Branch: `day/13-redis-foundation`
- Ek öğrenme/uygulama: Redis cache stratejileri ve bir read use-case.
- Ayrıntılı ek görevler, commit sırası ve kabul ölçütleri: [Day 13 kesin planı](docs/roadmap/day-13-exact-file-plan.md).
- typed config
- serializer
- key/TTL conventions
- connectivity tests
- Redis remains non-canonical

### Day 14 — Spring Security + OAuth2/OIDC + Keycloak
Branch: `day/14-security-keycloak`
- Ek öğrenme/uygulama: Secure API Design ve şifreleme sorumlulukları.
- Ayrıntılı ek görevler, commit sırası ve kabul ölçütleri: [Day 14 kesin planı](docs/roadmap/day-14-exact-file-plan.md).
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
- Ek öğrenme/uygulama: CAP ve consistency modellerinin proje üzerinden öğrenilmesi.
- Ayrıntılı ek görevler, commit sırası ve kabul ölçütleri: [Day 21 kesin planı](docs/roadmap/day-21-exact-file-plan.md).
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
- Ek öğrenme/uygulama: Load balancing algoritmaları ve abuse prevention.
- Ayrıntılı ek görevler, commit sırası ve kabul ölçütleri: [Day 23 kesin planı](docs/roadmap/day-23-exact-file-plan.md).
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
- Ek öğrenme/uygulama: Sınırlı chaos engineering deneyi.
- Ayrıntılı ek görevler, commit sırası ve kabul ölçütleri: [Day 28 kesin planı](docs/roadmap/day-28-exact-file-plan.md).
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
- Ek öğrenme/uygulama: System design süreci ve mimari trade-off değerlendirmesi.
- Ayrıntılı ek görevler, commit sırası ve kabul ölçütleri: [Day 32 kesin planı](docs/roadmap/day-32-exact-file-plan.md).
- ArchUnit consolidation
- dependency cycles/drift
- build/config/contract/persistence drift
- ADR reconciliation
- API/gRPC/GraphQL/messaging/security/observability docs audit
- Eureka vs Consul vs ZooKeeper comparison

### Day 33 — E2E + Recovery + Backend Completion
Branch: `day/33-backend-completion`
- Ek öğrenme/uygulama: Canonical datastore backup/restore doğrulaması.
- Ayrıntılı ek görevler, commit sırası ve kabul ölçütleri: [Day 33 kesin planı](docs/roadmap/day-33-exact-file-plan.md).
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

## Day 34–45 — Extended Architecture Scope

Day 33 backend completion'ı baseline sistem olarak kabul edilir. Day 34+ kapsamı, aynı Real Estate domain'i içinde henüz kullanılmamış architecture style, pattern ve technology'leri yalnız gerçek business ihtiyacına bağlayarak ekler.

**Activation principle:** Bir capability yalnız kendi milestone'ındaki business problem gerçekten tanımlandıysa aktive edilir. Pattern göstermek için yapay domain ihtiyacı, yapay trafik veya yapay veri hacmi üretilmez.

### Day 34 — Anti-Corruption Layer + Strangler Fig
Branch: `day/34-acl-strangler-fig`
- yeni `ListingSyncService` ile ExternalMLS entegrasyonu
- ExternalMLS model -> internal canonical command/model translation için Anti-Corruption Layer
- external vocabulary'nin Property domain modeline sızmaması
- translation/version mismatch testleri
- Strangler Fig yalnız gerçekten mevcut/legacy bir MLS import path varsa aktive edilir
- sıfırdan yazılan yeni adapter tek başına Strangler Fig olarak adlandırılmaz
- legacy path varsa routing/cutover kademeli olarak `ListingSyncService` tarafına taşınır

### Day 35 — Event Sourcing Foundation: Offer Write Side
Branch: `day/35-event-sourcing-foundation`
- kapsam yalnız BuyerService içindeki Offer Aggregate write-side
- Couchbase üzerinde append-only Offer event stream
- stream version + optimistic concurrency
- Offer state'in event stream'den rebuild edilmesi
- Day 22 Offer state machine korunur; rebuild edilen current state üzerinde transition guard görevi taşır
- internal Event Sourcing domain events ile Kafka integration events ayrı contract/vocabulary olarak ele alınır
- `OfferCountered` yalnız gerçek negotiation requirement oluşursa eklenir
- yeni EventStoreDB eklenmez

### Day 36 — Event Sourcing Replay, Audit & Snapshot
Branch: `day/36-event-sourcing-replay`
- full replay ve aggregate rebuild verification
- audit/history read API; raw event store public API olarak expose edilmez
- corruption/rebuild consistency testleri
- snapshot capability optional optimization olarak değerlendirilir; mevcut Offer stream uzunluğu gerektirmiyorsa production default kabul edilmez
- snapshot ile full replay sonucu tutarlılığı test edilir
- Event Store append -> best-effort Kafka send yapılmaz; mevcut reliable outbound strategy korunur

### Day 37 — Saga Orchestration: Property Purchase Completion
Branch: `day/37-saga-orchestration`
- Day 22 Offer/Reservation Saga Choreography korunur; bu milestone onun yerine geçmez
- yeni workflow: accepted Offer -> Contract -> Escrow -> Title Transfer -> Property SOLD -> Agent Commission
- yeni `PurchaseProcessService` saga orchestrator/process manager; yalnız process/saga state owner
- yeni `ContractService` contract lifecycle owner
- yeni `EscrowService` escrow/payment lifecycle owner
- yeni `TitleTransferService` legal ownership transfer lifecycle owner
- Property canonical lifecycle ownership `PropertyService`'te kalır
- Agent commission entitlement/record ownership `AgentService`'te kalır
- compensatable, retryable ve irreversible/point-of-no-return step'ler ayrı modellenir
- title transfer sonrası commission failure gibi durumlarda otomatik global rollback yerine retry/reconciliation uygulanabilir
- sırf çeşitlilik için yeni broker, datastore veya workflow engine eklenmez
- Choreography vs Orchestration karşılaştırmalı ADR

### Day 38 — Reactive Foundation: PropertyWatchService
Branch: `day/38-reactive-webflux`
- yeni `PropertyWatchService`
- Spring WebFlux + Project Reactor
- canonical ownership yalnız WatchSubscription capability'si; Property state ownership PropertyService'te kalır
- Property price/status lifecycle events Kafka üzerinden consume edilir
- reactive processing pipeline
- roadmap'te R2DBC zorunlu değildir; gerçek ihtiyaçla seçilen bir reactive datastore adapter kullanılır
- reactive request/processing path içinde blocking JDBC, blocking OpenFeign, `block()`, `blockFirst()`, `Thread.sleep()` kullanılmaz
- SearchService imperative baseline korunur; reactive'e çevrilmez
- blocking vs non-blocking karşılaştırmalı load test
- virtual threads learning/comparison konusu olabilir, bu Day'in implementation scope'una zorla eklenmez

### Day 39 — Real-Time Property Notifications with SSE
Branch: `day/39-realtime-notification`
- property watch için primary client delivery mechanism Server-Sent Events
- WebSocket yalnız ileride gerçek bidirectional real-time requirement oluşursa değerlendirilir
- internal Kafka events doğrudan client'a expose edilmez; client-facing notification contract'a map edilir
- authenticated Buyer yalnız kendi WatchSubscription bildirimlerini alabilir
- connect/disconnect/reconnect/stream cleanup
- bounded slow-consumer/backpressure policy
- duplicate upstream event safety
- `Last-Event-ID`/reconnect semantics değerlendirilir; sırf feature göstermek için ayrı notification-history datastore'u eklenmez

### Day 40 — Backend-for-Frontend
Branch: `day/40-bff`
- iki ayrı deployable BFF: `WebBffService` ve `MobileBffService`
- Web BFF: Agent/Seller dashboard composition
- Mobile BFF: Buyer-oriented lightweight composition
- API Gateway edge/platform concern'lerini taşır; BFF client-specific application composition yapar
- BFF canonical state, aggregate veya domain invariant owner değildir
- BFF'ler mümkün olduğunca stateless kalır; yeni canonical datastore eklenmez
- bağımsız downstream çağrılar parallel yürütülür
- required vs optional dependency ayrımı + partial degradation semantics
- Day 40 mevcut service contracts ile çalışır; GraphQL Federation Day 41'e bırakılır

### Day 41 — GraphQL Federation
Branch: `day/41-graphql-federation`
- Day 16 SearchService GraphQL API kaldırılmaz; federation-ready Search subgraph'a evrilir
- PropertyService ve AgentService gerekli read capability'leri için kendi subgraph adapter'larını sunar
- Property canonical ownership PropertyService'te kalır
- SearchService yalnız Elasticsearch projection/query-side owner'dır
- Federated Router yalnız schema composition, entity resolution ve query planning yapar; business logic/canonical state owner değildir
- WebBffService/MobileBffService korunur; yalnız uygun composition-heavy read use-case'lerde federated graph kullanabilir
- REST/gRPC baseline kaldırılmaz
- N+1, batching/DataLoader, query complexity/depth, auth propagation, timeout/fan-out ve partial failure testleri
- federation runtime için preferred option milestone başında compatibility doğrulanarak seçilir; technology version şimdiden hard-code edilmez

### Day 42 — Spring Batch: Reporting & Stale Listing Detection
Branch: `day/42-spring-batch`
- ana business use-case: market statistics reporting
- gerçek Spring Batch semantics: Job/Step, ItemReader/ItemProcessor/ItemWriter, chunk processing
- JobRepository/execution metadata
- restartability/checkpoint
- bounded retry + skip policy
- job parameters + idempotent rerun
- report source/output canonical ownership ve consumer ihtiyacına göre seçilir
- `stale listing cleanup` doğrudan otomatik domain-state mutation değildir; önce stale listing detection/candidate processing yapılır
- otomatik withdraw yalnız açık business policy varsa eklenir
- Spring Batch processing semantics ile Spring Cloud Task short-lived execution/orchestration sorumlulukları ayrı tutulur
- sırf çeşitlilik için yeni scheduler, datastore veya analytics platform eklenmez

### Day 43 — Multi-Tenancy: Agency Isolation
Branch: `day/43-multi-tenancy`
- gerçek business tenant `Agency`
- `Agency`, başlangıçta AgentService bounded context'i içinde domain entity; ayrı AgencyService açılmaz
- Agent başlangıçta tek Agency'ye bağlıdır
- tenant context client'ın serbestçe verdiği header'dan değil authenticated identity + Agent/Agency membership üzerinden resolve edilir
- ilk implementation row-level tenant isolation: application authorization + tenant-scoped persistence query
- schema-per-tenant ve database-per-tenant ADR'de karşılaştırılır, uygulanmaz
- tüm servisler zorla tenant-aware yapılmaz; yalnız Agency-owned/scoped use-case'ler tenant boundary taşır
- AgencyAdmin own-tenant only; cross-tenant System/Admin erişimi explicit privilege gerektirir
- cross-tenant leakage, IDOR, cache-key namespace, BFF/GraphQL propagation ve background job boundary testleri

### Day 44 — Schema Registry + Avro Event Evolution
Branch: `day/44-schema-registry`
- Kafka integration/domain event serialization için Avro + Schema Registry
- Protobuf gRPC contract'larında kalır
- preferred registry: Apicurio Registry; exact version/integration compatibility milestone başında doğrulanır
- JSON -> Avro migration aynı topic üzerinde ani serializer swap ile yapılmaz
- versioned migration path ve gerektiğinde geçici dual publishing
- dual publishing mevcut reliable outbound/outbox mekanizmasını bypass etmez
- `PropertyPublished` üzerinde compatible additive evolution ve deliberately incompatible change
- SearchService gerçek migration consumer'ı
- business event contract version ile Registry schema version aynı kavram değildir
- backward/forward/full compatibility semantics test edilir; production compatibility policy Registry semantics'i doğrulandıktan sonra kilitlenir
- old-schema replay, unknown schema, malformed message ve Registry outage davranışları test edilir

### Sharding / Partitioning — Design Only
Bu konu ayrı implementation Day'i değildir.
- Cassandra: partition key vs clustering key, query-first modeling, partition growth, hot partition, tombstone/token-distribution trade-off'ları
- MongoDB: shard-key cardinality/frequency, ranged vs hashed strategy, hotspot, balancing ve resharding
- gerçek multi-node sharding cluster kurulmaz
- yapay veri hacmiyle ihtiyaç oluşturulmaz
- durum: `Design Only` / `Not Implemented` / `Not Integrated` / `Not Verified at Scale`
- gerçek scale requirement oluşursa ayrı roadmap kararıyla aktive edilir

### Day 45 — Extended Architecture Fitness & Completion Audit
Branch: `day/45-extended-architecture-audit`
- Day 34–44 architecture conformance audit
- ownership boundary, security, failure behavior ve integration correctness doğrulaması
- ArchUnit/static architecture rules yeni modüllere genişletilir
- ACL external-model leakage kontrolü
- Event Sourcing scope ve domain-event/integration-event ayrımı kontrolü
- Saga Orchestration ownership/compensation/irreversible-step kontrolü
- reactive path blocking-call kontrolü
- SSE auth/backpressure/reconnect kontrolü
- BFF business-logic drift kontrolü
- GraphQL Federation canonical ownership + fan-out/N+1 kontrolü
- Spring Batch domain-mutation boundary kontrolü
- Multi-Tenancy cross-tenant leakage kontrolü
- Schema Registry migration/compatibility kontrolü
- ADR'ler actual implementation ile reconcile edilir
- Knowledge Base impact review ve canonical docs update
- README/roadmap completion status
- capability status matrix: `Infrastructure Ready`, `Implemented`, `Integrated`, `Verified`, `Design Only`
- doğrulanmamış capability `Verified` işaretlenmez; Sharding/Partitioning `Design Only` kalır

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


### Extended scope activation
- Day 34–45 capability'leri kendi gerçek domain senaryosu tanımlanmadan implementation'a başlamaz.
- Technology/pattern seçimi business requirement'tan sonra gelir.
- Composition katmanları canonical ownership'i devralmaz.
- Yeni technology sırf portfolio breadth için eklenmez.
- Event Sourcing internal domain events ile Kafka integration events ayrı kavramlardır.
- Day 37 Saga Orchestration, Day 22 Saga Choreography'nin yerine geçmez.
- Day 38 Reactive Foundation, Day 39 real-time client delivery'den ayrıdır.
- Day 40 BFF ile Day 41 GraphQL Federation aynı responsibility değildir ve birbirinin yerine geçmez.
- Sharding/Partitioning gerçek scale ihtiyacı oluşmadan implementation status'üne geçirilmez.

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

## Backend sonrası — DevOps Day 46–85

Day 1–45 backend/architecture sırası korunur. Aşağıdaki 40 milestone ücretli cloud/SaaS gerektirmeyen yerel uygulama ve öğrenme planıdır; tek takvim günü zorunluluğu yoktur. Durum **Planlandı**; runtime kanıtı olmadan Verified işaretlenmez.

Ortak araç kararları ve sahiplik: [DevOps Engineering Plan](docs/DEVOPS-ENGINEERING-PLAN.md). Ayrıntılı görev/commit/kanıt planları: [gün dizini](docs/roadmap/README.md).

| Gün | Konu | Ayrıntılı plan |
|---|---|---|
| Day 46 | Linux, process ve network temeli | [Plan](docs/roadmap/day-46-exact-file-plan.md) |
| Day 47 | Dockerfile ve multi-stage Java build | [Plan](docs/roadmap/day-47-exact-file-plan.md) |
| Day 48 | BuildKit, cache ve build secrets | [Plan](docs/roadmap/day-48-exact-file-plan.md) |
| Day 49 | Container güvenliği ve kaynak sınırları | [Plan](docs/roadmap/day-49-exact-file-plan.md) |
| Day 50 | Compose ortamı ve troubleshooting | [Plan](docs/roadmap/day-50-exact-file-plan.md) |
| Day 51 | Minikube ve cluster mimarisi | [Plan](docs/roadmap/day-51-exact-file-plan.md) |
| Day 52 | Deployment, ReplicaSet ve rollout temeli | [Plan](docs/roadmap/day-52-exact-file-plan.md) |
| Day 53 | Service, DNS ve internal communication | [Plan](docs/roadmap/day-53-exact-file-plan.md) |
| Day 54 | ConfigMap, Secrets ve Vault entegrasyonu | [Plan](docs/roadmap/day-54-exact-file-plan.md) |
| Day 55 | Probes, rollout ve graceful shutdown | [Plan](docs/roadmap/day-55-exact-file-plan.md) |
| Day 56 | Kaynak yönetimi, JVM ve scheduling | [Plan](docs/roadmap/day-56-exact-file-plan.md) |
| Day 57 | StatefulSet, storage ve veri yaşam döngüsü | [Plan](docs/roadmap/day-57-exact-file-plan.md) |
| Day 58 | RBAC, ServiceAccount ve Pod Security | [Plan](docs/roadmap/day-58-exact-file-plan.md) |
| Day 59 | Calico ve NetworkPolicy | [Plan](docs/roadmap/day-59-exact-file-plan.md) |
| Day 60 | Gateway API, Traefik ve TLS lifecycle | [Plan](docs/roadmap/day-60-exact-file-plan.md) |
| Day 61 | Helm ve Kustomize ile deployment sahipliği | [Plan](docs/roadmap/day-61-exact-file-plan.md) |
| Day 62 | Spring Cloud Kubernetes ve discovery/config sınırları | [Plan](docs/roadmap/day-62-exact-file-plan.md) |
| Day 63 | Ansible ile yerel host konfigürasyonu | [Plan](docs/roadmap/day-63-exact-file-plan.md) |
| Day 64 | Terraform ile somut yerel kaynak provisioning | [Plan](docs/roadmap/day-64-exact-file-plan.md) |
| Day 65 | IaC drift, state ve lab yeniden oluşturma | [Plan](docs/roadmap/day-65-exact-file-plan.md) |
| Day 66 | Jenkins Pipeline-as-Code temeli | [Plan](docs/roadmap/day-66-exact-file-plan.md) |
| Day 67 | Jenkins agent ve credential isolation | [Plan](docs/roadmap/day-67-exact-file-plan.md) |
| Day 68 | Test pipeline ve raporlama | [Plan](docs/roadmap/day-68-exact-file-plan.md) |
| Day 69 | SonarQube Community Build ve JaCoCo | [Plan](docs/roadmap/day-69-exact-file-plan.md) |
| Day 70 | Nexus ile Java artifact ve dependency yönetimi | [Plan](docs/roadmap/day-70-exact-file-plan.md) |
| Day 71 | Harbor ve OCI image lifecycle | [Plan](docs/roadmap/day-71-exact-file-plan.md) |
| Day 72 | Trivy, SBOM, Cosign ve provenance | [Plan](docs/roadmap/day-72-exact-file-plan.md) |
| Day 73 | Build once ve release contract | [Plan](docs/roadmap/day-73-exact-file-plan.md) |
| Day 74 | Argo CD ve GitOps temeli | [Plan](docs/roadmap/day-74-exact-file-plan.md) |
| Day 75 | Ortam izolasyonu, drift ve reconciliation | [Plan](docs/roadmap/day-75-exact-file-plan.md) |
| Day 76 | Staging doğrulaması ve release promotion | [Plan](docs/roadmap/day-76-exact-file-plan.md) |
| Day 77 | Canary, blue-green ve Argo Rollouts | [Plan](docs/roadmap/day-77-exact-file-plan.md) |
| Day 78 | Migration uyumluluğu ve rollback/forward-fix | [Plan](docs/roadmap/day-78-exact-file-plan.md) |
| Day 79 | Istio, service identity ve mTLS | [Plan](docs/roadmap/day-79-exact-file-plan.md) |
| Day 80 | HPA ve capacity doğrulaması | [Plan](docs/roadmap/day-80-exact-file-plan.md) |
| Day 81 | Kubernetes observability, SLI/SLO ve alerting | [Plan](docs/roadmap/day-81-exact-file-plan.md) |
| Day 82 | k6 ile performans ve release doğrulaması | [Plan](docs/roadmap/day-82-exact-file-plan.md) |
| Day 83 | Backup/restore ve veri recovery tatbikatı | [Plan](docs/roadmap/day-83-exact-file-plan.md) |
| Day 84 | Platform upgrade ve yeniden kurulum | [Plan](docs/roadmap/day-84-exact-file-plan.md) |
| Day 85 | Failure tatbikatı ve DevOps final audit | [Plan](docs/roadmap/day-85-exact-file-plan.md) |
