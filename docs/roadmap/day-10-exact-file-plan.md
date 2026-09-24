# Day 10 — Exact File / Class / Commit Plan

## 0. Scope

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

## Task 1 — Existing SellerService source audit

Verify:
- SellerService/build.gradle
- SellerService/src/main/resources/application.yml
- mevcut bootstrap class/package

Target base package: com.aydindemir.seller

Mevcut package farklıysa controlled refactor yapılır.

Commit: refactor(seller): align application base package

## Task 2 — Build dependencies

Modify: SellerService/build.gradle

Remove:
- Spring Data JPA
- PostgreSQL driver

Keep:
- Eureka Client
- Config Client
- Actuator
- tracing baseline
- Web MVC
- OpenAPI if retained

Add:
- Spring Data Cassandra
- Testcontainers JUnit Jupiter
- Cassandra Testcontainers support/module as appropriate
- ArchUnit if not common

Commit: build(seller): switch persistence dependencies to Cassandra

## Task 3 — Configuration

Modify: SellerService/src/main/resources/application.yml

Keep bootstrap:
- application name
- Config Server import
- Config Server URL

External non-secret config:
- contact points / host
- port
- keyspace name
- local datacenter
- schema action policy
- request timeout
- consistency level only if explicitly chosen
- actuator baseline

Secrets:
- SELLER_CASSANDRA_USERNAME
- SELLER_CASSANDRA_PASSWORD

Commit: config(seller): add Cassandra connection configuration

## Task 4 — Onion package skeleton

Target tree:

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

## Task 5 — SellerId and UserId

Create:
- domain/model/SellerId.java
- domain/model/UserId.java

Immutable UUID wrappers.

## Task 6 — SellerStatus

Create: domain/model/SellerStatus.java

Values:
- ACTIVE
- SUSPENDED
- INACTIVE

## Task 7 — Seller Aggregate

Create: domain/model/Seller.java

Fields:
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

Rule:
Only ACTIVE seller can submit listing.

Tests:
- SellerTest.java

Commit: feat(seller): add Seller domain model

## Task 8 — ListingSubmissionId

Create: domain/model/ListingSubmissionId.java

## Task 9 — PropertyDraftData

Create: domain/model/PropertyDraftData.java

Minimum Day 10 fields should align with future Property creation contract, but not duplicate full Property Aggregate complexity.

Candidate:
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

## Task 10 — ListingSubmissionStatus

Create: domain/model/ListingSubmissionStatus.java

Values:
- CREATED
- SUBMITTED
- PROPERTY_CREATED
- REJECTED
- FAILED

## Task 11 — ListingSubmission Aggregate/Entity

Create: domain/model/ListingSubmission.java

Fields:
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

Day 10 only actively uses create/submit local state.

Tests:
- ListingSubmissionTest.java

Commit: feat(seller): add ListingSubmission state model

## Task 12 — Domain exceptions

Create only used exceptions:
- SellerNotFoundException
- SellerNotActiveException
- ListingSubmissionNotFoundException
- InvalidListingSubmissionStateException

Commit can be grouped with aggregates.

## Task 13 — Domain repository contracts

Create:
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

Important:
Do not expose Cassandra Page, Slice, PagingState or table classes in domain.

Commit: feat(seller): add domain repository contracts

## Task 14 — Application commands

Create:
- application/command/CreateSellerCommand.java
- application/command/CreateListingSubmissionCommand.java
- application/command/SubmitListingCommand.java

Do not create AcceptOffer/RejectOffer commands yet.

## Task 15 — Application queries

Create:
- application/query/GetSellerQuery.java
- application/query/ListSellerSubmissionsQuery.java

List query should include sellerId + YearMonth + bounded page size.

## Task 16 — Application results

Create:
- SellerResult.java
- ListingSubmissionResult.java
- ListingSubmissionPageResult.java or equivalent

Commit: feat(seller): add application commands queries and results

## Task 17 — Application services

Create:
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

## Task 18 — Cassandra physical model: seller_by_id

Create:
- infrastructure/cassandra/table/SellerByIdTable.java

Partition key:
- seller_id

Fields:
- seller_id
- user_id
- display_name
- status
- created_at
- updated_at

Primary query:
Get seller by seller_id.

Do not model relational joins.

## Task 19 — Cassandra physical model: listing submissions

Create:
- infrastructure/cassandra/table/ListingSubmissionBySellerMonthTable.java

Partition key:
- seller_id
- year_month

Clustering keys:
- created_at DESC
- submission_id

Fields include draft snapshot needed to reconstruct ListingSubmission.

Primary query:
List seller submissions for one seller and one month, newest first.

Commit: db(seller): add Cassandra query-first table models

## Task 20 — Cassandra Spring Data repositories

Create:
- infrastructure/cassandra/repository/SpringDataSellerByIdRepository.java
- infrastructure/cassandra/repository/SpringDataListingSubmissionRepository.java

Repository query must match partition key.

No ALLOW FILTERING.

Commit: feat(seller): add Cassandra repositories

## Task 21 — Cassandra mappers

Create:
- infrastructure/cassandra/mapper/SellerCassandraMapper.java
- infrastructure/cassandra/mapper/ListingSubmissionCassandraMapper.java

Mappings:
- domain -> table
- table -> domain

Explicit mapping preferred due denormalized physical model.

Commit: feat(seller): add Cassandra persistence mapping

## Task 22 — Cassandra adapters

Create:
- infrastructure/cassandra/adapter/CassandraSellerRepositoryAdapter.java
- infrastructure/cassandra/adapter/CassandraListingSubmissionRepositoryAdapter.java

Adapters implement domain repository contracts.

Do not leak Cassandra types.

Commit: feat(seller): add Cassandra repository adapters

## Task 23 — CQL schema

Create:
- SellerService/src/main/resources/cassandra/schema/V1__seller_tables.cql

Content:
- keyspace creation only if local bootstrap policy allows application-independent schema script
- seller_by_id
- listing_submissions_by_seller_and_month

Table definition must encode clustering order.

Do not create future tables yet:
- pending_offers_by_seller
- offers_by_seller_and_month
- seller_activity_by_seller_and_month

Commit: db(seller): add Cassandra query-first schema

## Task 24 — Schema bootstrap strategy

Decide one approach:
- explicit cqlsh bootstrap script
- test bootstrap via container init
- Spring Data schema action only for tests/local if justified

Production-like preference: explicit version-controlled schema, not uncontrolled auto-create.

Document exact command.

Commit: infra(seller): document Cassandra schema bootstrap

## Task 25 — REST requests

Create:
- presentation/rest/request/CreateSellerRequest.java
- presentation/rest/request/CreateListingSubmissionRequest.java
- presentation/rest/request/SubmitListingRequest.java only if submit needs body; otherwise action endpoint may have no body

Validation:
- userId required
- displayName required
- draft syntactic validation

Commit: feat(seller): add REST request contracts

## Task 26 — REST responses

Create:
- presentation/rest/response/SellerResponse.java
- presentation/rest/response/ListingSubmissionResponse.java
- presentation/rest/response/ListingSubmissionPageResponse.java

## Task 27 — REST mappers

Create:
- presentation/rest/mapper/SellerRestMapper.java
- presentation/rest/mapper/ListingSubmissionRestMapper.java

No business rule in mapper.

Commit: feat(seller): add REST response and mapping

## Task 28 — SellerController

Create: presentation/rest/SellerController.java

Endpoints:
- POST /sellers
- GET /sellers/{sellerId}

POST -> 201
GET -> 200 / 404

## Task 29 — ListingSubmissionController

Create: presentation/rest/ListingSubmissionController.java

Endpoints:
- POST /sellers/{sellerId}/listing-submissions
- POST /sellers/{sellerId}/listing-submissions/{submissionId}/submit
- GET /sellers/{sellerId}/listing-submissions?yearMonth=YYYY-MM&pageSize=N&pageState=...

Important:
POST create/submission on Day 10 means local SellerService state only.
It does NOT guarantee Property creation.

Commit: feat(seller): expose seller and listing submission APIs

## Task 30 — Error mapping

Stable mappings:
- SellerNotFoundException -> 404 SELLER_NOT_FOUND
- ListingSubmissionNotFoundException -> 404 LISTING_SUBMISSION_NOT_FOUND
- SellerNotActiveException -> 422 SELLER_NOT_ACTIVE
- InvalidListingSubmissionStateException -> 409 INVALID_LISTING_SUBMISSION_STATE
- duplicate user -> 409 if enforced

No raw Cassandra exception exposed.

Commit: feat(seller): map Seller domain failures to API errors

## Task 31 — Domain tests

Create:
- SellerTest.java
- ListingSubmissionTest.java
- PropertyDraftDataTest.java if meaningful

Cases:
- ACTIVE seller allowed
- SUSPENDED/INACTIVE rejected
- CREATED -> SUBMITTED
- invalid resubmit
- reject/fail state rules where implemented

Commit: test(seller): add domain and state-machine tests

## Task 32 — Application tests

Create:
- SellerApplicationServiceTest.java
- ListingSubmissionApplicationServiceTest.java

Use mocks/fakes for domain repository contracts.

Cases:
- create seller
- duplicate user
- get seller/not-found
- active create submission
- inactive seller rejected
- submit changes state only
- list passes exact seller/month partition criteria

Commit: test(seller): add application service tests

## Task 33 — Cassandra Testcontainers foundation

Create test support:
- SellerCassandraContainerTestBase.java or equivalent

Responsibilities:
- Cassandra container
- wait strategy
- keyspace/schema bootstrap
- dynamic properties

Remember Cassandra startup is slow; bounded wait strategy required.

Commit: test(seller): add Cassandra Testcontainers foundation

## Task 34 — Seller persistence integration tests

Create:
- CassandraSellerRepositoryAdapterIntegrationTest.java

Cases:
- save/load seller
- not found
- user uniqueness only if supported by explicit table/query design

Important:
Cassandra does not naturally enforce arbitrary uniqueness like RDBMS.
If userId uniqueness is required globally, it needs explicit table/LWT/ownership design.
Do not pretend existsByUserId alone gives race-safe uniqueness.

Any such rule must be explicitly designed or deferred.

Commit: test(seller): add seller Cassandra integration tests

## Task 35 — Listing submission integration tests

Create:
- CassandraListingSubmissionRepositoryAdapterIntegrationTest.java

Cases:
- save submission
- list by seller+month
- newest-first clustering
- paging within partition
- empty partition result

Commit: test(seller): add listing submission Cassandra tests

## Task 36 — Query design guard

Verify:
- no ALLOW FILTERING
- no cross-partition scan
- no relational join expectation
- no repository method requiring unsupported ad-hoc query

Document with test/code review.

## Task 37 — REST slice tests

Create:
- SellerControllerTest.java
- ListingSubmissionControllerTest.java

Cases:
- POST seller 201
- GET seller 200/404
- create listing submission
- inactive seller error
- submit action
- list month validation

Commit: test(seller): add REST adapter tests

## Task 38 — Onion Architecture tests

Create:
- architecture/SellerOnionArchitectureTest.java

Rules:
- domain depends on nothing outer
- application may depend on domain, not infrastructure/presentation
- infrastructure depends inward
- presentation depends on application
- domain must not depend on Cassandra/Spring/Kafka/RabbitMQ
- no cycles

Commit: test(seller): enforce Onion Architecture boundaries

## Task 39 — Runtime smoke

Start:
- Config Server
- Eureka
- Cassandra
- SellerService

Verify:
- service starts/registers
- seller create/get
- listing create
- local submit
- monthly list

No RabbitMQ publish expected.

## Task 40 — Documentation

Modify:
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

## Recommended Commit Sequence

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
22. test(seller): add Cassandra Testcontainers foundation
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
- Cassandra table models are separate from domain
- REST seller/listing endpoints work
- raw Cassandra exception does not leak
- Testcontainers verifies real Cassandra semantics
- Onion Architecture rules are automated
- no RabbitMQ reliable dispatch/Kafka/Saga implementation leaks into Day 10
- docs match actual implementation