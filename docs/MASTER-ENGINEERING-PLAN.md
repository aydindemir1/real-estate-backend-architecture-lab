# Master Engineering Plan

Bu doküman implementation öncesi ana kontrol noktasıdır. Roadmap, Architecture, business workflow, contract, service design ve engineering standard'larını tek çerçevede birleştirir.

## 1. Genel hedef

Bu proje yalnızca çok sayıda teknoloji kullanan bir demo değildir.

Hedef aynı anda iki ekseni geliştirmektir:

1. Technology breadth & depth
2. Engineering maturity

Implementation her iki eksende de quality gate'leri karşılamalıdır.

## 2. Şu ana kadar kilitlenen kararlar

### Architecture
- Database per Service
- Polyglot Persistence
- Clean Architecture
- Hexagonal Architecture
- Onion Architecture
- Vertical Slice Architecture
- CQRS Query Side
- Event-Driven Architecture
- Saga Choreography
- Eventual Consistency


### Extended Architecture (Day 34–45)
- Anti-Corruption Layer for ExternalMLS integration through `ListingSyncService`
- Strangler Fig only when a real legacy/existing MLS import path exists and is migrated incrementally
- Event Sourcing limited to BuyerService Offer write-side; Couchbase event stream is authoritative write history
- Offer state machine retained as transition guard over event-rebuilt current state
- Saga Orchestration for Property Purchase Completion, parallel to Day 22 Saga Choreography
- Reactive Architecture with a dedicated `PropertyWatchService`, Spring WebFlux and Project Reactor
- SSE for one-way real-time property notifications; WebSocket only if a genuine bidirectional requirement appears
- Backend-for-Frontend with separate `WebBffService` and `MobileBffService`
- GraphQL Federation across Property, Search and Agent read capabilities without changing canonical ownership
- Spring Batch for market statistics reporting and stale-listing detection/candidate processing
- Multi-Tenancy with Agency as the business tenant and row-level isolation as the first implementation strategy
- Schema Evolution Governance with Avro + Schema Registry for Kafka events; Protobuf remains on gRPC boundaries
- Sharding/Partitioning remains design-only until a real scale requirement exists

These capabilities extend the existing architecture; they do not replace existing ownership, communication or reliability decisions. Each capability is activated only in its own milestone and only after its business scenario is defined.

### Persistence
- Auth/UserProfile -> PostgreSQL
- Agent -> MySQL
- Buyer -> Couchbase
- Seller -> Cassandra
- Property -> MongoDB
- Search -> Elasticsearch
- Redis -> cache/idempotency/rate limit/ephemeral state

### Communication
- REST -> public/business HTTP API
- OpenFeign -> mevcut internal REST integration
- gRPC -> BuyerService -> AgentService availability
- GraphQL -> flexible read/query
- RabbitMQ -> Command / Work Queue
- Kafka -> Domain Event Streaming / CQRS / Saga

### Security
- Spring Security
- OAuth2 / OIDC
- Keycloak
- RBAC + scope
- ownership checks
- service-to-service authentication
- least privilege

### Reliability
- Idempotent Consumer
- Outbox/Inbox
- Retry
- DLQ/DLT
- Optimistic Concurrency
- failure isolation

### Observability
- structured logs
- metrics
- traces
- correlation/causation
- OpenTelemetry
- Prometheus
- Grafana
- Loki
- Tempo

## 3. Engineering Standards

Tamamlanan standard'lar:

1. Engineering Principles
2. Design Patterns
3. API Design
4. Domain Modeling
5. Application Layer
6. Persistence
7. Messaging
8. Security
9. Testing
10. Observability
11. Performance & Resilience
12. Architecture Fitness & Governance

Bu standard'lar implementation sırasında Definition of Done'ın parçasıdır.

## 4. Audit sonucu — güçlü taraflar

- Business ownership ile datastore ownership büyük ölçüde tutarlı.
- Service'ler aynı Architecture template'ine zorlanmıyor.
- RabbitMQ/Kafka semantic ayrımı net.
- Property source of truth ile Search Projection ayrımı net.
- Domain model ile persistence model ayrımı özellikle Clean/Hexagonal/Onion service'lerde net.
- API/error/security/testing/observability için ortak sistem standardı mevcut.
- Architecture drift'i ArchUnit ve governance ile kontrol etme planı mevcut.

## 5. Audit sonucu — düzeltilen tutarsızlıklar

- PropertyListingSubmitted artık Kafka event olarak modellenmiyor; listing submission -> PropertyService akışı RabbitMQ Command olarak tanımlanıyor.
- ListingSubmission içindeki gereksiz ACCEPTED ara state kaldırıldı.
- OfferAccepted/OfferRejected başlangıç event setinden çıkarıldı; gerçek consumer ihtiyacı oluşursa eklenecek.
- API versioning ile endpoint catalog arasındaki boundary açıklığa kavuşturuldu.
- 400 / 409 / 422 error semantics kesinleştirildi.
- Seller domain event isimleri integration event vocabulary ile hizalandı.

## 6. Implementation öncesi eksik kalan engineering standard'ları

12 temel standard tamamlanmış olsa da Senior Java/Spring hedefi için aşağıdaki ek standard'lar tamamlanmadan Day 7 implementation başlamamalıdır.

### STEP 13 — Java 21 Engineering Standard
Kapsam:
- records
- sealed classes/interfaces
- enums
- Optional
- Stream API
- collection immutability
- equals/hashCode
- BigDecimal
- java.time
- exception design
- generics
- virtual threads değerlendirmesi
- concurrency primitives
- CompletableFuture policy
- null-safety
- serialization boundaries
- Lombok kullanım politikası

### STEP 14 — Spring Boot Engineering Standard
Kapsam:
- constructor injection
- bean lifecycle
- @ConfigurationProperties
- validation
- profile kullanımı
- auto-configuration awareness
- @Transactional
- Spring Data boundaries
- Spring MVC exception handling
- Actuator
- Spring Security integration
- test slices
- proxy/self-invocation caveat
- configuration class discipline

### STEP 15 — Spring Cloud Engineering Standard
Kapsam:
- Gateway
- Config
- Eureka
- OpenFeign
- LoadBalancer
- Circuit Breaker
- Stream
- Bus
- Vault
- Function
- Task
- Contract
- Kubernetes
- timeout/retry ownership
- service discovery vs platform discovery
- config/secrets boundary

### STEP 16 — Build, Dependency & Configuration Governance
Kapsam:
- Gradle multi-module conventions
- dependency management/BOM
- dependency version alignment
- reproducible build
- dependency locking candidate
- plugin version governance
- configuration hierarchy
- environment variables
- Config Server
- secrets
- local/dev/test profile
- build cache candidate
- wrapper policy

### STEP 17 — Quality Attributes & Non-Functional Requirements
Kapsam:
- availability
- latency
- consistency
- scalability
- security
- maintainability
- testability
- observability
- recoverability
- deployability
- data freshness

Exact numeric SLO'lar benchmark sonrası konabilir; fakat quality attribute scenario'ları implementation öncesi tanımlanmalıdır.

### STEP 18 — Operational Readiness & Runbook Standard
Kapsam:
- startup/shutdown
- readiness/liveness
- migration failure
- DLQ/DLT handling
- replay/reindex
- incident debugging
- rollback/forward-fix
- dependency outage
- backup/recovery awareness
- local developer troubleshooting

## 7. Açık Architecture kararları

Aşağıdaki konular henüz final değildir ve ilgili milestone'dan önce ADR veya design decision gerektirir.

### 7.1 Reliable outbound publication by datastore

Critical broker publication cannot be a best-effort `save -> send` sequence.

- Property/MongoDB -> Outbox
- Seller/Cassandra -> Cassandra-friendly durable pending outbound message + dispatcher/reconciliation
- Buyer/Couchbase -> durable reliable-publication strategy must be finalized before OfferRequested is wired

Exact guarantees are documented in the relevant reliability Days; relational Transactional Outbox semantics are not blindly forced onto Cassandra/Couchbase.

### 7.2 Identity mapping
Keycloak subject -> UserProfile -> Buyer/Seller/Agent identity mapping açık bir design olarak Day 8 öncesi finalize edilmelidir.

### 7.3 Event schema governance
JSON başlangıç formatıdır. Avro/Protobuf/Schema Registry yalnızca gerçek evolution ihtiyacı ortaya çıkarsa değerlendirilecektir.

### 7.4 Search projection rebuild

Kafka replay ile source-of-truth reindex iki farklı recovery yoludur. Final operational rebuild/reconciliation strategy Day 31 Spring Cloud Task milestone'ında uygulanır.

### 7.5 Offer idempotency durability

Redis, Offer creation correctness için tek source of truth olamaz. `Idempotency-Key` -> request hash -> Offer/result ilişkisi BuyerService'in durable Couchbase state'i içinde korunmalıdır. Redis yalnız acceleration/cache rolü oynayabilir.


### 7.6 Sharding / Partitioning status
Cassandra and MongoDB scaling strategy is documented as design-only. No artificial data volume or multi-node sharding cluster is introduced merely to demonstrate the capability.

- Cassandra focus: partition-key/query-first modeling, partition growth, hot-partition risk, tombstone and token-distribution trade-offs
- MongoDB focus: shard-key cardinality/frequency, ranged vs hashed strategy, hotspot, balancing and resharding
- Status remains `Design Only / Not Implemented / Not Integrated / Not Verified at Scale` until a genuine scale requirement exists

### 7.7 Event Sourcing and Offer state-machine relationship
Day 35–36 Event Sourcing is limited to BuyerService Offer write-side.

- Couchbase stores the append-only Offer event stream
- Offer current state is rebuilt from events
- the Day 22 state machine remains as transition/invariant guard over the rebuilt state
- internal Event Sourcing domain events and Kafka integration events are separate contracts/vocabularies
- `OfferCountered` is not introduced unless a real negotiation requirement exists
- replay/audit are implemented; snapshots remain an optional optimization unless stream length/performance justifies them
- Event Store append must not be followed by unprotected best-effort Kafka send; existing reliable-publication rules remain in force

### 7.8 Saga Orchestration and Choreography relationship
Day 37 Property Purchase Completion is a new workflow and does not replace Day 22 Offer/Reservation choreography.

Ownership:
- `PurchaseProcessService` -> saga/process state only
- `ContractService` -> contract lifecycle
- `EscrowService` -> escrow/payment lifecycle
- `TitleTransferService` -> legal ownership transfer lifecycle
- `PropertyService` -> canonical Property lifecycle
- `AgentService` -> commission entitlement/record

Compensatable, retryable and irreversible steps are modeled separately. Post-transfer failures may require retry/reconciliation instead of global rollback. No new broker, datastore or workflow engine is added solely for variety.

### 7.9 Reactive and real-time boundary
Day 38 and Day 39 intentionally solve different concerns.

- Day 38 creates `PropertyWatchService` with Spring WebFlux + Project Reactor and a genuinely reactive datastore adapter chosen from the persistence need
- SearchService remains on its imperative baseline
- blocking JDBC/OpenFeign/`block()` calls are not allowed in the reactive processing path
- Day 39 adds SSE as the primary one-way client delivery mechanism
- WebSocket is deferred unless a true bidirectional requirement appears
- Kafka contracts are mapped to client-facing notification contracts rather than exposed directly

### 7.10 BFF and GraphQL Federation boundary
Day 40 and Day 41 are complementary, not interchangeable.

- `WebBffService` and `MobileBffService` provide client-specific composition and remain stateless where practical
- API Gateway retains edge/platform responsibilities
- BFFs do not own canonical state or domain invariants
- Day 41 evolves the Day 16 Search GraphQL API into a Search subgraph and adds Property/Agent subgraphs
- the federated router handles schema composition/entity resolution/query planning only
- canonical ownership remains with the owning services; REST and gRPC remain valid boundaries

### 7.11 Spring Batch boundary
Day 42 uses Spring Batch for real batch semantics rather than as a scheduler substitute.

- primary use-case: market statistics reporting
- stale listing handling begins as detection/candidate processing, not automatic state mutation
- automatic withdrawal requires an explicit business policy
- Spring Batch owns Job/Step/chunk/restart/skip/retry semantics
- Spring Cloud Task remains the short-lived task execution/orchestration capability

### 7.12 Multi-Tenancy boundary
Day 43 introduces `Agency` as the business tenant inside AgentService.

- Agent initially belongs to exactly one Agency
- tenant context is resolved from authenticated identity + membership, not an arbitrary client-supplied tenant header
- first implementation uses row-level tenant isolation with application authorization + tenant-scoped persistence queries
- schema-per-tenant and database-per-tenant are comparison alternatives documented in ADR, not implemented
- only Agency-owned/scoped use-cases become tenant-aware
- cross-tenant leakage, IDOR, cache namespace, BFF/GraphQL propagation and admin exceptions are explicit test concerns

### 7.13 Event schema governance decision
The earlier "evaluate if needed" decision becomes active at Day 44 because a real schema-evolution scenario is introduced.

- Kafka event serialization -> Avro + Schema Registry
- gRPC contracts -> Protobuf
- preferred local/open-source Registry -> Apicurio Registry, exact compatible version verified at milestone start
- JSON -> Avro migration uses a versioned migration path, not an in-place serializer switch
- temporary dual publishing, if required, must use the reliable outbound/outbox path
- SearchService is the real migration consumer
- business event contract version and Registry schema version are distinct concepts
- compatible additive and deliberately incompatible changes are both tested


## 8. Target Design vs Milestone Scope

Dokümanlardaki end-state design ile Day implementation scope birbirine karıştırılmamalıdır.

Örnek:
SearchService target design'ında autocomplete/facet/geo/reindex bulunabilir; fakat Day 12 yalnız temel Elasticsearch query-side capability'sini, Day 21 event projection'ı, Day 31 reindex/reconciliation'ı içerir.

Aynı kural tüm roadmap için geçerlidir: target design erken tanımlanabilir, fakat capability yalnız kendi milestone'ında aktive edilir.

## 9. Pre-Day Implementation Gate

Her Day başlamadan önce:

1. Scope
2. Business use-case
3. Architecture impact
4. API/contract impact
5. Data model
6. Security
7. Failure modes
8. Testing plan
9. Observability
10. Performance/resilience
11. Migration/config impact
12. Definition of Done

kontrol edilir.

## 10. Definition of Ready

Bir Day implementation'a hazır sayılırsa:

- business flow net
- service ownership net
- API/command/event contract yeterince net
- datastore/query model net
- architecture/package boundary net
- unresolved blocker ADR yok
- test plan var
- failure path var
- security impact biliniyor
- observability impact biliniyor
- required infrastructure listesi net
- rollback/cleanup yaklaşımı biliniyor

## 11. Definition of Done

Bir feature yalnızca çalıştığı için bitmiş sayılmaz.

Gerekli olanlar:
- architecture boundary korunmuş
- Clean Code / OOP / SOLID standardına uygun
- API standardına uygun
- domain invariant doğru yerde
- persistence/query design doğru
- security ownership uygulanmış
- happy + failure path test edilmiş
- integration semantics gerçek infrastructure ile doğrulanmış
- logs/metrics/traces düşünülmüş
- timeout/retry/idempotency/concurrency değerlendirilmiş
- ArchUnit/static analysis kabul edilebilir
- docs/ADR güncellenmiş
- build green

## 12. Implementation sırası

Engineering Standards 1–18 tamamlandı.

Final audit sonrası implementation cadence yeniden düzenlenmiştir:

- Bir Day mümkün olduğunca tek service veya tek ana infrastructure konusu içerir.
- Eski Day 7 içinde planlanan beş service tek milestone'da uygulanmayacaktır.
- Backend roadmap final audit sonrası Day 33'e kadar genişletilmiştir.
- İçerik azaltılmamış, yalnız daha küçük milestone'lara ayrılmıştır.
- Küçük ve anlamlı commit'ler zorunlu çalışma prensibidir.

Detaylı güncel sıra root `ROADMAP.md` dosyasındadır. Day 15 sonrası eski birleşik milestone numaraları superseded kabul edilir.

Implementation'a hâlâ plan hazır olmadan geçilmez.



### Extended Architecture completion rule

Day 45 is not documentation-only cleanup. It is the final conformance and completion audit for Day 34–44 capabilities.

The audit verifies:
- architecture and ownership boundaries
- security and cross-tenant isolation
- failure/retry/compensation behavior
- reactive-path blocking violations
- SSE reconnect/backpressure/auth behavior
- BFF business-logic drift
- GraphQL Federation N+1/fan-out/canonical-ownership correctness
- Spring Batch domain-mutation boundary
- Schema Registry migration/compatibility behavior
- ADR and Knowledge Base reconciliation

A capability status matrix must distinguish `Infrastructure Ready`, `Implemented`, `Integrated`, `Verified` and `Design Only`. Nothing is marked `Verified` without evidence; Sharding/Partitioning remains `Design Only`.

## 13. Knowledge Base Maintenance

Proje boyunca öğrenilen ve uygulanan Architecture, Approach, Principle, Pattern, Technology ve Protocol bilgileri `docs/knowledge-base` altında kalıcı bilgi bankası olarak tutulur.

Kurallar:
- Her kavram için tek canonical doküman bulunur.
- Project-specific architecture dokümanları Knowledge Base ile karıştırılmaz.
- Her Day sonunda Knowledge Base impact review yapılır.
- Yeni kavram varsa canonical dosya oluşturulur.
- Mevcut kavram anlamlı biçimde genişlediyse canonical dosya güncellenir.
- `by-day` dosyaları içerik tekrar etmez; canonical dokümanlara link verir.
- "Infrastructure Ready" ile "Implemented/Integrated/Verified" durumları birbirinden ayrılır.

Her Day Definition of Done maddelerine şu kontrol eklenmiştir:

> Knowledge Base impact reviewed and updated.

Detaylı standard: `docs/standards/knowledge-base.md`.

Knowledge Base ilk olarak Day 1–7 kapsamı ile başlatılmıştır ve sonraki her Day sonunda büyütülecektir.
