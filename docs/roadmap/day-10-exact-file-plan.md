# Day 10 — Kesin File / Class / Commit Planı

## Gerçek implementation notları

Bu dosya Day 10 başlamadan önce kilitlenen exact plan'ı korur. Actual implementation ile plan arasındaki kontrollü farklar:

- Lokal Cassandra authentication kullanılmadığı için `SELLER_CASSANDRA_USERNAME/PASSWORD` final config'te tutulmadı.
- Plan skeleton'ındaki `domain/event`, `domain/service` ve `application/port` için gerçek ihtiyaç oluşmadığından boş package/class üretilmedi.
- Gerçek package yapısı için `SellerService/docs/PACKAGE-DESIGN.md` source of truth'tur.
- Runtime acceptance sırasında explicit REST parameter binding düzeltmesi yapıldı ve tekrar doğrulandı.
- Cassandra Admin lokal inspection aracı Day 10 sırasında eklendi; persistence source of truth olarak kullanılmadı.

## 0. Kapsam

Day 10 yalnızca SellerService içindir.

Hedef:
- PostgreSQL/JPA -> Cassandra
- Onion Architecture
- Seller
- ListingSubmission
- query-first Cassandra model
- REST API
- unit/application/integration/architecture tests

Day 10 içinde:
- RabbitMQ cross-service reliable publish yok
- Kafka yok
- pending offer projection yok
- Saga yok
- seller activity timeline yok

## Görev 1 — Mevcut SellerService source audit

Doğrula:
- SellerService/build.gradle
- SellerService/src/main/resources/application.yml
- mevcut bootstrap class/package

Hedef base package: com.aydindemir.seller

Mevcut package farklıysa controlled refactor yapılır.

Commit: refactor(seller): align application base package

## Görev 2 — Build dependency'leri

Değiştir: SellerService/build.gradle

Kaldır:
- Spring Data JPA
- PostgreSQL driver

Koru:
- Eureka Client
- Config Client
- Actuator
- tracing baseline
- Web MVC
- korunuyorsa OpenAPI

Ekle:
- Spring Data Cassandra
- Testcontainers JUnit Jupiter
- Cassandra Testcontainers support/module as appropriate
- common değilse ArchUnit

Commit: build(seller): switch persistence dependencies to Cassandra

## Görev 3 — Konfigürasyon

Değiştir: SellerService/src/main/resources/application.yml

Bootstrap'ta koru:
- application name
- Config Server import
- Config Server URL

Harici non-secret config:
- contact points / host
- port
- keyspace name
- local datacenter
- schema action policy
- request timeout
- yalnız explicit seçildiyse consistency level
- actuator baseline

Secret'lar:
- SELLER_CASSANDRA_USERNAME
- SELLER_CASSANDRA_PASSWORD

Commit: config(seller): add Cassandra connection configuration

## Görev 4 — Onion package iskeleti

Hedef tree:

SellerService/src/main/java/com/aydindemir/seller/
- SellerServiceApplication.java
- domain/model/
- domain/event/
- domain/repository/
- domain/service/
- domain/exception/
- application/command/
- application/query/
- application/service/
- application/port/
- infrastructure/cassandra/table/
- infrastructure/cassandra/repository/
- infrastructure/cassandra/mapper/
- infrastructure/cassandra/adapter/
- infrastructure/configuration/
- presentation/rest/request/
- presentation/rest/response/
- presentation/rest/mapper/

No empty RabbitMQ/Kafka implementation classes on Day 10.

Commit: refactor(seller): establish Onion Architecture package boundaries

## Görev 5 — SellerId and UserId

Oluştur:
- domain/model/SellerId.java
- domain/model/UserId.java

Immutable UUID wrappers.

## Görev 6 — SellerStatus

Oluştur: domain/model/SellerStatus.java

Values:
- ACTIVE
- SUSPENDED
- INACTIVE

## Görev 7 — Seller Aggregate

Oluştur: domain/model/Seller.java

Alanlar:
- SellerId sellerId
- UserId userId
- displayName
- SellerStatus status
- createdAt
- updatedAt

Behavior:
- create
- changeStatus
- assertCanSubmitListing or equivalent domain rule

Kural:
Yalnızca ACTIVE seller listing submit edebilir.

Testler:
- SellerTest.java

Commit: feat(seller): add Seller domain model

## Görev 8 — ListingSubmissionId

Oluştur: domain/model/ListingSubmissionId.java

## Görev 9 — PropertyDraftData

Oluştur: domain/model/PropertyDraftData.java

Minimum Day 10 alanları gelecekteki Property creation contract ile uyumlu olmalı; ancak full Property Aggregate karmaşıklığını kopyalamamalıdır.

Aday:
- title
- description
- propertyType
- city
- district
- address line candidate
- price amount
- currency
- area
- roomCount

Do not embed Property domain Aggregate.

## Görev 10 — ListingSubmissionStatus

Oluştur: domain/model/ListingSubmissionStatus.java

Values:
- CREATED
- SUBMITTED
- PROPERTY_CREATED
- REJECTED
- FAILED

## Görev 11 — ListingSubmission Aggregate/Entity

Oluştur: domain/model/ListingSubmission.java

Alanlar:
- submissionId
- sellerId
- propertyDraftData
- status
- createdAt
- updatedAt

Behavior:
- create
- submit
- markPropertyCreated
- reject
- fail

Day 10 yalnızca create/submit local state akışını aktif olarak kullanır.

Testler:
- ListingSubmissionTest.java

Commit: feat(seller): add ListingSubmission state model

## Görev 12 — Domain exceptions

Yalnızca kullanılan exception'ları oluştur:
- SellerNotFoundException
- SellerNotActiveException
- ListingSubmissionNotFoundException
- InvalidListingSubmissionStateException

Commit aggregate'lerle birleştirilebilir.

## Görev 13 — Domain repository contracts

Oluştur:
- domain/repository/SellerRepository.java
- domain/repository/ListingSubmissionRepository.java

SellerRepository minimum:
- save(Seller)
- Optional<Seller> findById(SellerId)
- boolean existsByUserId(UserId)

ListingSubmissionRepository minimum:
- save(ListingSubmission)
- Optional<ListingSubmission> findById(...) if needed
- listBySellerAndMonth(SellerId, YearMonth, page/paging abstraction)

Önemli:
Domain içinde Cassandra Page, Slice, PagingState veya table class'larını expose etme.

Commit: feat(seller): add domain repository contracts

## Görev 14 — Application commands

Oluştur:
- application/command/CreateSellerCommand.java
- application/command/CreateListingSubmissionCommand.java
- application/command/SubmitListingCommand.java

Henüz AcceptOffer/RejectOffer command'larını oluşturma.

## Görev 15 — Application queries

Oluştur:
- application/query/GetSellerQuery.java
- application/query/ListSellerSubmissionsQuery.java

List query sellerId + YearMonth + bounded page size içermelidir.

## Görev 16 — Application results

Oluştur:
- SellerResult.java
- ListingSubmissionResult.java
- ListingSubmissionPageResult.java or equivalent

Commit: feat(seller): add application commands queries and results

## Görev 17 — Application service'leri

Oluştur:
- application/service/SellerApplicationService.java
- application/service/ListingSubmissionApplicationService.java

Flows:

CreateSeller:
1. validate duplicate user if rule exists
2. create Seller
3. save
4. return result

CreateListingSubmission:
1. load seller
2. require ACTIVE
3. create ListingSubmission
4. save
5. return result

SubmitListing:
1. load seller
2. require ACTIVE
3. load submission
4. call submit()
5. save local SUBMITTED state
6. do NOT publish RabbitMQ command yet

ListSellerSubmissions:
1. validate bounded month/page request
2. repository query by exact partition
3. return results

Commit: feat(seller): implement Seller application services

## Görev 18 — Cassandra physical model: seller_by_id

Oluştur:
- infrastructure/cassandra/table/SellerByIdTable.java

Partition key:
- seller_id

Alanlar:
- seller_id
- user_id
- display_name
- status
- created_at
- updated_at

Primary query:
Get seller by seller_id.

Do not model relational joins.

## Görev 19 — Cassandra physical model: listing submissions

Oluştur:
- infrastructure/cassandra/table/ListingSubmissionBySellerMonthTable.java

Partition key:
- seller_id
- year_month

Clustering keys:
- created_at DESC
- submission_id

Alanlar ListingSubmission'ı yeniden oluşturmak için gereken draft snapshot'ı içerir.

Primary query:
Bir seller ve bir ay için listing submission'ları newest-first sırayla listele.

Commit: db(seller): add Cassandra query-first table models

## Görev 20 — Cassandra Spring Data repository'leri

Oluştur:
- infrastructure/cassandra/repository/SpringDataSellerByIdRepository.java
- infrastructure/cassandra/repository/SpringDataListingSubmissionRepository.java

Repository query partition key ile eşleşmelidir.

No ALLOW FILTERING.

Commit: feat(seller): add Cassandra repositories

## Görev 21 — Cassandra mappers

Oluştur:
- infrastructure/cassandra/mapper/SellerCassandraMapper.java
- infrastructure/cassandra/mapper/ListingSubmissionCassandraMapper.java

Mappings:
- domain -> table
- table -> domain

Explicit mapping preferred due denormalized physical model.

Commit: feat(seller): add Cassandra persistence mapping

## Görev 22 — Cassandra adapters

Oluştur:
- infrastructure/cassandra/adapter/CassandraSellerRepositoryAdapter.java
- infrastructure/cassandra/adapter/CassandraListingSubmissionRepositoryAdapter.java

Adapters implement domain repository contracts.

Do not leak Cassandra types.

Commit: feat(seller): add Cassandra repository adapters

## Görev 23 — CQL schema

Oluştur:
- SellerService/src/main/resources/cassandra/schema/V1__seller_tables.cql

Content:
- keyspace creation yalnız local bootstrap policy application-independent schema script'e izin veriyorsa
- seller_by_id
- listing_submissions_by_seller_and_month

Table definition clustering order'ı encode etmelidir.

Henüz future table'ları oluşturma:
- pending_offers_by_seller
- offers_by_seller_and_month
- seller_activity_by_seller_and_month

Commit: db(seller): add Cassandra query-first schema

## Görev 24 — Schema bootstrap strategy

Decide one approach:
- explicit cqlsh bootstrap script
- container init üzerinden test bootstrap
- Spring Data schema action yalnız gerekçelendiriliyorsa test/local için

Production-like tercih: uncontrolled auto-create yerine explicit version-controlled schema.

Document exact command.

Commit: infra(seller): document Cassandra schema bootstrap

## Görev 25 — REST requests

Oluştur:
- presentation/rest/request/CreateSellerRequest.java
- presentation/rest/request/CreateListingSubmissionRequest.java
- `presentation/rest/request/SubmitListingRequest.java` yalnız submit body gerektiriyorsa; aksi halde action endpoint body içermeyebilir

Validation:
- userId zorunlu
- displayName zorunlu
- draft syntactic validation

Commit: feat(seller): add REST request contracts

## Görev 26 — REST responses

Oluştur:
- presentation/rest/response/SellerResponse.java
- presentation/rest/response/ListingSubmissionResponse.java
- presentation/rest/response/ListingSubmissionPageResponse.java

## Görev 27 — REST mappers

Oluştur:
- presentation/rest/mapper/SellerRestMapper.java
- presentation/rest/mapper/ListingSubmissionRestMapper.java

Mapper içinde business rule bulunmaz.

Commit: feat(seller): add REST response and mapping

## Görev 28 — SellerController

Oluştur: presentation/rest/SellerController.java

Endpoints:
- POST /sellers
- GET /sellers/{sellerId}

POST -> 201
GET -> 200 / 404

## Görev 29 — ListingSubmissionController

Oluştur: presentation/rest/ListingSubmissionController.java

Endpoints:
- POST /sellers/{sellerId}/listing-submissions
- POST /sellers/{sellerId}/listing-submissions/{submissionId}/submit
- GET /sellers/{sellerId}/listing-submissions?yearMonth=YYYY-MM&pageSize=N&pageState=...

Önemli:
Day 10'da POST create/submission yalnız local SellerService state anlamına gelir.
It does NOT guarantee Property creation.

Commit: feat(seller): expose seller and listing submission APIs

## Görev 30 — Error mapping

Stable mappings:
- SellerNotFoundException -> 404 SELLER_NOT_FOUND
- ListingSubmissionNotFoundException -> 404 LISTING_SUBMISSION_NOT_FOUND
- SellerNotActiveException -> 422 SELLER_NOT_ACTIVE
- InvalidListingSubmissionStateException -> 409 INVALID_LISTING_SUBMISSION_STATE
- duplicate user -> 409 if enforced

No raw Cassandra exception exposed.

Commit: feat(seller): map Seller domain failures to API errors

## Görev 31 — Domain testleri

Oluştur:
- SellerTest.java
- ListingSubmissionTest.java
- PropertyDraftDataTest.java if meaningful

Senaryolar:
- ACTIVE seller izinli
- SUSPENDED/INACTIVE rejected
- CREATED -> SUBMITTED
- invalid resubmit
- implement edildiği yerlerde reject/fail state kuralları

Commit: test(seller): add domain and state-machine tests

## Görev 32 — Application testleri

Oluştur:
- SellerApplicationServiceTest.java
- ListingSubmissionApplicationServiceTest.java

Domain repository contract'ları için mock/fake kullan.

Senaryolar:
- seller oluşturma
- duplicate user
- get seller/not-found
- ACTIVE seller ile submission oluşturma
- inactive seller rejected
- submit yalnız state değiştirir
- list passes exact seller/month partition criteria

Commit: test(seller): add application service tests

## Görev 33 — Cassandra Testcontainers temeli

Test desteğini oluştur:
- SellerCassandraContainerTestBase.java or equivalent

Sorumluluklar:
- Cassandra container
- wait strategy
- keyspace/schema bootstrap
- dynamic properties

Cassandra startup'ın yavaş olduğunu unutma; bounded wait strategy gereklidir.

Commit: test(seller): add Cassandra Testcontainers temeli

## Görev 34 — Seller persistence integration tests

Oluştur:
- CassandraSellerRepositoryAdapterIntegrationTest.java

Senaryolar:
- save/load seller
- not found
- user uniqueness yalnız explicit table/query design destekliyorsa

Önemli:
Cassandra does not naturally enforce arbitrary uniqueness like RDBMS.
If userId uniqueness is required globally, it needs explicit table/LWT/ownership design.
Do not pretend existsByUserId alone gives race-safe uniqueness.

Any such rule must be explicitly designed or deferred.

Commit: test(seller): add seller Cassandra integration tests

## Görev 35 — Listing submission integration tests

Oluştur:
- CassandraListingSubmissionRepositoryAdapterIntegrationTest.java

Senaryolar:
- save submission
- list by seller+month
- newest-first clustering
- paging within partition
- empty partition result

Commit: test(seller): add listing submission Cassandra tests

## Görev 36 — Query design guard

Doğrula:
- no ALLOW FILTERING
- no cross-partition scan
- no relational join expectation
- no repository method requiring unsupported ad-hoc query

Document with test/code review.

## Görev 37 — REST slice tests

Oluştur:
- SellerControllerTest.java
- ListingSubmissionControllerTest.java

Senaryolar:
- POST seller 201
- GET seller 200/404
- create listing submission
- inactive seller error
- submit action
- list month validation

Commit: test(seller): add REST adapter tests

## Görev 38 — Onion Architecture tests

Oluştur:
- architecture/SellerOnionArchitectureTest.java

Rules:
- domain depends on nothing outer
- application may depend on domain, not infrastructure/presentation
- infrastructure depends inward
- presentation depends on application
- domain must not depend on Cassandra/Spring/Kafka/RabbitMQ
- no cycles

Commit: test(seller): enforce Onion Architecture boundaries

## Görev 39 — Runtime smoke

Start:
- Config Server
- Eureka
- Cassandra
- SellerService

Doğrula:
- service starts/registers
- seller create/get
- listing create
- local submit
- monthly list

No RabbitMQ publish expected.

## Görev 40 — Dokümantasyon

Değiştir:
- SellerService/docs/DESIGN.md
- SellerService/docs/PACKAGE-DESIGN.md
- SellerService/ROADMAP.md
- docs/roadmap/day-10-seller-cassandra-onion.md

Record actual:
- keyspace
- table definitions
- partition/clustering keys
- paging semantics
- exact config keys
- local-only submit behavior
- reliable RabbitMQ dispatch explicitly deferred to Day 17

Commit: docs(seller): finalize Cassandra Onion implementation

## Önerilen Commit Sırası

1. refactor(seller): align application base package
2. build(seller): switch persistence dependencies to Cassandra
3. config(seller): add Cassandra connection configuration
4. refactor(seller): establish Onion Architecture package boundaries
5. feat(seller): add Seller domain model
6. feat(seller): add ListingSubmission state model
7. feat(seller): add domain repository contracts
8. feat(seller): add application commands queries and results
9. feat(seller): implement Seller application services
10. db(seller): add Cassandra query-first table models
11. feat(seller): add Cassandra repositories
12. feat(seller): add Cassandra persistence mapping
13. feat(seller): add Cassandra repository adapters
14. db(seller): add Cassandra query-first schema
15. infra(seller): document Cassandra schema bootstrap
16. feat(seller): add REST request contracts
17. feat(seller): add REST response and mapping
18. feat(seller): expose seller and listing submission APIs
19. feat(seller): map Seller domain failures to API errors
20. test(seller): add domain and state-machine tests
21. test(seller): add application service tests
22. test(seller): add Cassandra Testcontainers temeli
23. test(seller): add seller Cassandra integration tests
24. test(seller): add listing submission Cassandra tests
25. test(seller): add REST adapter tests
26. test(seller): enforce Onion Architecture boundaries
27. docs(seller): finalize Cassandra Onion implementation

Adjacent tiny commits can be combined when they represent one coherent change. Cassandra schema/model/query changes should remain separately reviewable from REST and documentation.

## Explicitly Deferred from Day 10

Do not implement:
- RabbitMQ SubmitPropertyListingCommand publication
- publisher confirms
- reliable Cassandra-to-RabbitMQ dispatch
- pending offer projections
- SellerAccepted/SellerRejected events
- Kafka
- Saga
- seller activity timeline
- offer history tables

## Critical Cassandra Design Note

Do not import RDBMS assumptions into Cassandra.

Especially:
- uniqueness is not automatically provided
- arbitrary query is not a repository method away
- secondary index is not default answer
- denormalization is normal
- table is designed for a query
- partition size must be bounded

Global userId uniqueness for Seller should be implemented only if an explicit Cassandra-safe design is chosen. Otherwise Day 10 can rely on upstream identity/profile ownership and document that local Cassandra uniqueness is not a strong invariant yet.

## Day 10 Final Gate

Day 10 closes only if:
- SellerService no longer uses JPA/PostgreSQL
- Cassandra connection/config works
- Seller domain is framework-free
- ListingSubmission state machine works
- only ACTIVE seller can submit
- seller_by_id table serves exact seller lookup
- listing_submissions_by_seller_and_month uses bounded partition
- newest-first clustering works
- no ALLOW FILTERING
- no cross-partition scan in Day 10 flow
- Cassandra table modelleri are separate from domain
- REST seller/listing endpoints work
- raw Cassandra exception does not leak
- Testcontainers verifies real Cassandra semantics
- Onion Architecture rules are automated
- no RabbitMQ reliable dispatch/Kafka/Saga implementation leaks into Day 10
- docs match actual implementation