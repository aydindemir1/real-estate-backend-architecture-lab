# Day 7 — Exact Implementation Plan

**Milestone:** Polyglot Persistence & Architecture Foundations  
**Implementation branch:** `day/07-polyglot-persistence`  
**Status:** Ready for implementation after this plan is accepted as source of truth.

## 1. Day 7 objective

Day 7'nin amacı end-state business capability'lerin tamamını geliştirmek değildir.

Amaç, beş farklı service Architecture stilini ve altı persistence capability'sini minimum fakat gerçek çalışan vertical path'lerle kurmak ve sonraki Day'lerin güvenli temelini oluşturmaktır.

Bu milestone sonunda:

- AgentService -> MySQL + Clean Architecture
- BuyerService -> Couchbase + Hexagonal Architecture
- SellerService -> Cassandra + Onion Architecture
- PropertyService -> MongoDB + Vertical Slice Architecture
- SearchService -> Elasticsearch + Vertical Slice/CQRS Query Side foundation
- Redis -> shared infrastructure foundation

çalışır ve automated integration test ile doğrulanmış olmalıdır.

## 2. Explicitly out of scope

Day 7'de uygulanmayacak:

- Keycloak / OAuth2 / OIDC -> Day 8
- gRPC -> Day 9
- GraphQL -> Day 9
- Kafka -> Day 10
- Spring Cloud Stream / Function -> Day 10
- Outbox / Inbox / reliable cross-service dispatch -> Day 11
- CQRS event projection -> Day 12
- Saga -> Day 13
- advanced resilience -> Day 14
- Vault / Bus -> Day 15
- full test hardening -> Day 16+
- OpenTelemetry stack -> Day 18
- reindex/reconciliation jobs -> Day 19

RabbitMQ baseline korunur; SellerService -> PropertyService yeni command flow Day 7'de gerçek cross-service publish olarak aktive edilmez.

## 3. Repository-level changes

### 3.1 settings.gradle

Yeni module:

```gradle
include 'SearchService'
```

Mevcut module'ler korunur.

### 3.2 dependencies.gradle

Yeni dependency alias candidate'ları:

- Spring Boot Data Couchbase
  - `org.springframework.boot:spring-boot-starter-data-couchbase`
- Spring Boot Data Cassandra
  - `org.springframework.boot:spring-boot-starter-data-cassandra`
- Spring Boot Data MongoDB
  - `org.springframework.boot:spring-boot-starter-data-mongodb`
- Spring Boot Data Elasticsearch
  - `org.springframework.boot:spring-boot-starter-data-elasticsearch`
- Spring Boot Data Redis
  - `org.springframework.boot:spring-boot-starter-data-redis`
- MySQL JDBC Driver
  - `com.mysql:mysql-connector-j`
- Testcontainers JUnit Jupiter
  - `org.testcontainers:junit-jupiter`
- datastore-specific Testcontainers support where official module/container support is appropriate

Spring-managed dependency versions mümkün olduğunca BOM üzerinden yönetilir; random version pin yapılmaz.

### 3.3 root build.gradle audit

Mevcut root build bütün subproject'lere şunları zorunlu enjekte ediyor:

- Web
- Swagger
- MapStruct
- Auth0 JWT
- OpenFeign

Day 7 implementation sırasında bu yaklaşım yeniden değerlendirilecek.

Hedef:
- truly-common dependency root'ta,
- service-specific dependency module'de.

Özellikle `java-jwt` ve OpenFeign her service için gerçekten gerekli değilse root common dependency olmaktan çıkarılmalıdır.

Bu refactor Day 7 build değişikliğinin parçası olabilir; ancak baseline'ı kırmamak için build/test ile adım adım yapılacaktır.

## 4. Configuration hygiene

Current main branch compose file'da credential'lar hard-coded.

Day 7 ile:

- DB password'lar repository'de literal tutulmayacak.
- RabbitMQ credential literal tutulmayacak.
- Docker Compose environment interpolation kullanılacak.
- `.env` gitignored olacak.
- `.env.example` fake/example values ile eklenecek.

Vault Day 15'e kadar local secret injection environment/.env üzerinden yapılabilir.

## 5. Local Docker Compose target

Existing PostgreSQL Auth/UserProfile ve RabbitMQ baseline korunur.

Day 7 ile eski temporary PostgreSQL service'leri kaldırılır/değiştirilir:

- agent-postgres -> MySQL
- buyer-postgres -> Couchbase
- seller-postgres -> Cassandra
- property-postgres -> MongoDB

Yeni:

- Elasticsearch
- Redis

### Version policy

- image tag explicit
- `latest` yok
- healthcheck mümkün olan datastore'larda
- named volume
- unique local port
- container name tutarlı

### Local startup strategy

Resource-heavy stack nedeniyle compose profile veya selective startup değerlendirilecektir.

Önerilen gruplar:
- core
- day7-data
- search

Ama complexity yaratıyorsa tek compose ile başlayıp documented selective startup kullanılabilir.

## 6. AgentService — Clean Architecture + MySQL

### 6.1 Day 7 use-case scope

Minimum gerçek vertical path:

- CreateAgent
- GetAgent
- ChangeAvailability

Bunlar Architecture katmanlarını kanıtlamak için yeterlidir.

### 6.2 Domain

Implement:

- Agent
- AgentId
- UserId
- LicenseNumber
- AgencyInfo
- AgentStatus
- AvailabilityStatus

Rules:

- license number non-blank
- SUSPENDED/INACTIVE agent AVAILABLE olamaz
- availability change aggregate behavior üzerinden
- no public setters

### 6.3 Application

- CreateAgentUseCase
- GetAgentUseCase
- ChangeAvailabilityUseCase
- corresponding Command/Query/Result
- AgentApplicationService

### 6.4 Infrastructure

- AgentJpaEntity
- SpringDataAgentRepository
- AgentPersistenceMapper
- AgentRepositoryAdapter

MySQL schema:

`agents`

Minimum fields:
- id
- user_id
- license_number
- agency_name
- agency_reg_number
- office_phone
- status
- availability_status
- created_at
- updated_at
- version

Constraints:
- PK id
- UNIQUE user_id
- UNIQUE license_number
- NOT NULL critical fields

### 6.5 Migration

MySQL schema migration tool kullanılmalıdır.

Mevcut project standardıyla uyumlu tek migration tool seçilir; Day 7 içinde aynı relational service için `ddl-auto=update` kullanılmaz.

### 6.6 REST proof

Minimum endpoints:
- POST /agents
- GET /agents/{agentId}
- PATCH /agents/{agentId}/availability

Public Gateway versioning sonraki integration'da `/api/v1` ile korunur.

### 6.7 Agent tests

Unit:
- active agent availability change
- suspended agent cannot become available
- LicenseNumber invalid

Integration/Testcontainers:
- persist/load
- unique license
- unique user
- optimistic version baseline if implemented now

Architecture:
- domain must not depend on JPA/Spring/infrastructure
- presentation must not access persistence adapter directly

## 7. BuyerService — Hexagonal Architecture + Couchbase

### 7.1 Day 7 use-case scope

Offers/Saga henüz yok.

Minimum vertical path:

- CreateOrUpdateBuyerPreferences
- GetBuyerPreferences
- AddSavedSearch

### 7.2 Domain

- BuyerPreferences
- BuyerId
- PriceRange
- RoomRange
- AreaRange
- LocationPreference
- NotificationSettings
- SavedSearch

Rules:
- price range valid
- area/room range valid
- immutable nested Value Object
- internal collections defensively copied

### 7.3 Inbound ports

- UpdateBuyerPreferencesUseCase
- GetBuyerPreferencesUseCase
- AddSavedSearchUseCase

### 7.4 Outbound ports

- SaveBuyerPreferencesPort
- LoadBuyerPreferencesPort

Day 7'de:
- AgentAvailabilityPort interface target design'da kalabilir fakat gRPC adapter Day 9'a kadar implement edilmez.
- PublishOfferEventPort Day 10/13 flow'una kadar active implementation almaz.
- IdempotencyPort Redis usage offer creation ile birlikte sonraki Day'de aktive edilir.

### 7.5 Couchbase adapter

Document key:

`buyer-preferences::{buyerId}`

Document minimum:
- type
- buyerId
- budget
- preferredLocations
- propertyTypes
- roomRange
- areaRange
- preferredFeatures
- notificationSettings
- savedSearches
- createdAt
- updatedAt

Unbounded savedSearch list risk dokümante edilir; Day 7 scope'ta bounded validation konabilir.

### 7.6 REST proof

- PUT /buyers/{buyerId}/preferences
- GET /buyers/{buyerId}/preferences
- POST /buyers/{buyerId}/saved-searches

### 7.7 Buyer tests

Unit/application:
- valid range
- invalid range
- defensive copy
- inbound service with fake outbound port

Integration:
- deterministic document key
- save/load
- update
- not-found behavior

Architecture:
- application/domain must not depend on adapter
- inbound adapter must not call outbound adapter directly

## 8. SellerService — Onion Architecture + Cassandra

### 8.1 Day 7 use-case scope

Minimum:

- CreateSeller
- GetSeller
- CreateListingSubmission
- ListSellerSubmissions

No RabbitMQ cross-service publication yet.

`SubmitListing` can persist local `SUBMITTED` state only if implementation needs state-machine proof, but external command dispatch is explicitly deferred until reliability strategy is designed.

### 8.2 Domain

- Seller
- SellerId
- UserId
- SellerStatus
- ListingSubmission
- ListingSubmissionId
- PropertyDraftData
- ListingSubmissionStatus

Rules:
- only ACTIVE seller can create/submit listing
- state transition controlled by behavior

### 8.3 Cassandra query-first tables

Use the already-audited physical model:

#### seller_by_id
Partition key:
- seller_id

#### listing_submissions_by_seller_and_month
Partition key:
- seller_id
- year_month

Clustering:
- created_at DESC
- submission_id

Day 7 does not create all future seller projection tables unless exercised by current use-case.

Deferred:
- pending_offers_by_seller
- offers_by_seller_and_month
- seller_activity_by_seller_and_month

unless needed to prove an immediate Day 7 behavior.

### 8.4 Cassandra rules

- no ALLOW FILTERING
- no JPA relationship thinking
- no cross-table transactional assumption
- bounded partition through year_month bucket

### 8.5 REST proof

- POST /sellers
- GET /sellers/{sellerId}
- POST /sellers/{sellerId}/listing-submissions
- GET /sellers/{sellerId}/listing-submissions

The POST listing endpoint Day 7 response represents local submission creation, not guaranteed Property creation.

### 8.6 Seller tests

Domain:
- inactive seller cannot submit
- state transitions

Integration:
- seller_by_id
- monthly submission partition
- clustering order newest-first
- no cross-partition filtering requirement

Architecture:
- domain must not depend on Cassandra/Spring
- application must not depend on presentation

## 9. PropertyService — Vertical Slice + MongoDB

### 9.1 Day 7 scope

Target business workflow says canonical Property normally comes from listing command.

Because reliable Seller->Property command dispatch is deferred, Day 7 will not invent a public `POST /properties` contract that contradicts the target workflow.

Minimum proof:

- shared Property Aggregate
- Mongo persistence adapter
- GetProperty slice
- UpdatePropertyDetails or PublishProperty slice only if seeded/test-created Property exists

Persistence creation is exercised through integration test fixture/repository adapter.

### 9.2 Domain

Implement baseline Property fields/value objects required by current design.

Status:
- DRAFT
- PUBLISHED
- ON_HOLD
- RESERVED
- SOLD
- WITHDRAWN

Day 7 behavior minimum:
- construct valid DRAFT
- publish()
- updateDetails()
- changePrice() candidate if needed

No Saga hold/reserve behavior integration yet, although domain methods may exist if fully tested and independent.

### 9.3 Mongo document

Collection:
`properties`

Indexes created only for current/query-near-future need:
- sellerId + createdAt
- status candidate

Agent/geo indexes can wait until actual use-case.

### 9.4 Optimistic concurrency

`@Version` on persistence document.

Concurrent update behavior integration tested if feasible in Day 7.

### 9.5 Vertical Slice proof

Implement:

`getbyid`
- GetPropertyQuery
- GetPropertyHandler
- PropertyQueryController

and one write slice:

`publish`
- PublishPropertyCommand
- PublishPropertyHandler
- PublishPropertyController

Test setup can insert a DRAFT via persistence fixture.

### 9.6 Property tests

Domain:
- DRAFT -> PUBLISHED
- invalid republish
- positive price

Integration:
- Mongo save/load
- version behavior
- index presence where practical

Architecture:
- slice must not depend on another slice internal class
- shared contains only genuinely cross-slice components

## 10. SearchService — Elasticsearch foundation

### 10.1 New module

Add:
- SearchService/build.gradle
- SearchService application class
- resources/application.yml
- DESIGN/ROADMAP/PACKAGE-DESIGN already exist in docs branch and will be carried to implementation branch

### 10.2 Day 7 scope

No Kafka event consumer yet.

Minimum:
- PropertySearchDocument
- explicit index mapping
- Elasticsearch repository/adapter
- SearchProperties query vertical slice
- basic exact/filter/text query sufficient to verify integration

Deferred:
- Kafka projection
- autocomplete
- facets
- fuzzy
- geo
- GraphQL
- reindex/reconciliation

### 10.3 Search document

Minimum fields:
- propertyId
- title
- description
- propertyType
- city
- district
- price
- currency
- roomCount
- features
- status
- publishedAt
- updatedAt

Mapping:
- title/description text
- exact filters keyword
- numbers numeric
- dates date

### 10.4 Index local settings

Learning/local:
- 1 primary shard
- 0 replica

### 10.5 Search tests

Integration:
- index document
- full-text title/description
- exact status/type filter
- price range candidate
- delete/cleanup

Architecture:
- SearchService must not contain canonical Property write Aggregate
- no Mongo dependency

## 11. Redis foundation

Day 7 Redis is infrastructure foundation only.

Implement:
- dependency
- typed configuration
- connectivity smoke/integration test
- documented key naming/TTL conventions

Do not prematurely implement:
- Offer idempotency
- Rate Limiting
- Saga state
- Property cache

Those capabilities activate on their relevant Days.

## 12. Config Server changes

Remote/local config source must receive datastore-specific non-secret config for:

- agent-service -> MySQL
- buyer-service -> Couchbase
- seller-service -> Cassandra
- property-service -> MongoDB
- search-service -> Elasticsearch

Secrets are environment placeholders, not committed literal values.

Configuration groups should be typed where application-specific values exist.

## 13. Existing PostgreSQL cleanup

AuthService and UserProfileService PostgreSQL remain unchanged.

Remove Day 1-6 temporary PostgreSQL runtime dependency/config/infrastructure for:

- AgentService
- BuyerService
- SellerService
- PropertyService

No migration of meaningful business data is required if those stores only contain training/bootstrap data. If non-disposable data exists before implementation, migration decision must be revisited.

## 14. Migration/bootstrap strategy by datastore

### MySQL
Versioned schema migration.

### Couchbase
Bucket/scope/collection + required indexes bootstrap in documented infrastructure setup.

### Cassandra
CQL schema version-controlled in repository; tables created from explicit scripts/migration strategy.

### MongoDB
Collection can be created lazily, but indexes must be defined explicitly and verified.

### Elasticsearch
Index/mapping explicit bootstrap; do not rely blindly on dynamic mapping.

### Redis
No schema; key namespace/TTL contract documented.

## 15. Day 7 testing matrix

| Service | Unit | Integration/Testcontainers | Architecture |
|---|---|---|---|
| Agent | domain/use-case | MySQL | Clean dependency rules |
| Buyer | domain/application ports | Couchbase | Hexagonal rules |
| Seller | domain/state | Cassandra | Onion rules |
| Property | aggregate/handler | MongoDB | Vertical Slice rules |
| Search | query handler | Elasticsearch | CQRS query-side rules |
| Redis | n/a/minimal | connectivity/TTL | role/source-of-truth guard |

## 16. Failure-path tests

Minimum Day 7 failure coverage:

Agent:
- duplicate license
- inactive/suspended availability rule

Buyer:
- missing preferences
- invalid range

Seller:
- inactive seller submission
- missing seller
- empty monthly result

Property:
- not found
- invalid state publish
- optimistic conflict if concurrency baseline included

Search:
- no result
- invalid filter/pagination boundary

Infrastructure:
- invalid/missing critical config should fail startup or integration bootstrap predictably

## 17. Observability baseline for Day 7

Day 18 full observability is deferred, but Day 7 must preserve baseline:

- Actuator
- existing tracing/Zipkin where already present
- structured/contextual logging conventions
- datastore connection failure visible
- no sensitive credential log

No custom high-cardinality metrics yet unless necessary.

## 18. Security scope

Day 8 owns Keycloak.

Day 7:
- do not expand insecure custom auth into new architecture
- new endpoints follow existing baseline only as necessary for development
- no secrets committed
- input validation present
- mass assignment avoided through Request DTOs

## 19. Performance / resilience scope

No premature optimization.

Required:
- bounded pagination where list exists
- explicit DB/query indexes tied to current query
- datastore/client connection timeout where configuration exposes it
- no unbounded collections
- no retry loops

## 20. Exact file/change families

Expected change families:

Root:
- settings.gradle
- dependencies.gradle
- build.gradle only where common-dependency cleanup is justified
- docker-compose.yml
- .gitignore
- .env.example

AgentService:
- build.gradle
- application/config
- domain/application/infrastructure/presentation packages
- migrations
- tests

BuyerService:
- build.gradle
- application/config
- domain/application/adapter packages
- tests

SellerService:
- build.gradle
- application/config
- domain/application/infrastructure/presentation
- Cassandra schema
- tests

PropertyService:
- build.gradle
- application/config
- slices/shared persistence/domain
- tests

SearchService:
- entire new module runtime/build/config/source/test foundation

Docs:
- Day 7 roadmap status
- service DESIGN/ROADMAP if implementation differs
- ADR only if new trade-off decision appears

## 21. Commit strategy

Suggested logical commits:

1. build: add Day 7 datastore dependencies and SearchService module
2. infra: replace temporary service databases in compose
3. feat(agent): implement MySQL Clean Architecture foundation
4. feat(buyer): implement Couchbase Hexagonal foundation
5. feat(seller): implement Cassandra Onion foundation
6. feat(property): implement Mongo Vertical Slice foundation
7. feat(search): add Elasticsearch query-side foundation
8. feat(redis): add Redis infrastructure foundation
9. test: add Day 7 datastore and architecture integration tests
10. docs: finalize Day 7 implementation documentation

Commits can be further split if a change becomes large.

## 22. Day 7 Definition of Ready

Day 7 is Ready when:

- this exact scope is accepted
- datastore image/version choices are confirmed during implementation
- dependency compatibility resolves under current Boot/Cloud BOM
- no hidden business data migration is required
- local machine resources can run required subset of containers
- no new unresolved ADR blocker appears

## 23. Day 7 Definition of Done

All required:

- build green via Gradle Wrapper
- Java 21 toolchain preserved
- SearchService included and starts
- AgentService works against MySQL
- BuyerService works against Couchbase
- SellerService works against Cassandra
- PropertyService works against MongoDB
- SearchService works against Elasticsearch
- Redis connectivity verified
- Auth/UserProfile existing PostgreSQL baseline not broken
- Eureka/Config/Actuator baseline not broken
- no service-specific old PostgreSQL dependency remains in four migrated modules
- no committed real/local credential literal in compose/application config
- selected domain rules unit tested
- each datastore has real integration test
- Architecture rule tests exist for Day 7 boundaries or are explicitly queued for Day 20 only if current build constraints require it
- no cross-service datastore access
- Search remains derived/query-side only
- Redis remains non-canonical
- docs match implementation
- no end-state capability is falsely claimed as complete

## 24. Final audit verdict

No architecture blocker remains for starting Day 7 foundation after this exact plan.

The intentionally unresolved Cassandra -> RabbitMQ reliable dispatch decision does not block Day 7 because cross-service publication is explicitly out of scope until reliability work.

Day 7 should now be implemented incrementally and validated after each service, rather than changing all datastores in one unverified batch.
