# Day 9 — Exact File / Class / Commit Plan

## 0. Scope

Day 9 yalnızca BuyerService içindir.

Hedef:
- PostgreSQL/JPA -> Couchbase
- Hexagonal Architecture
- BuyerPreferences
- SavedSearch foundation
- inbound/outbound ports
- REST inbound adapter
- Couchbase outbound adapter
- unit/application/integration/architecture tests

Day 9 içinde Offer, Saga, Kafka, gRPC ve Redis idempotency yoktur.

## Task 1 — Existing BuyerService source audit

Verify:
- BuyerService/build.gradle
- BuyerService/src/main/resources/application.yml
- mevcut bootstrap class/package

Target base package: com.aydindemir.buyer

Mevcut package farklıysa controlled refactor yapılır.

Commit: refactor(buyer): align application base package

## Task 2 — Build dependencies

Modify: BuyerService/build.gradle

Remove:
- Spring Data JPA
- PostgreSQL driver

Keep:
- Eureka Client
- Config Client
- Actuator
- tracing baseline
- Web MVC if REST adapter remains
- OpenAPI if service-local docs retained

Add:
- Spring Data Couchbase
- Testcontainers JUnit Jupiter
- Couchbase integration test support as appropriate
- ArchUnit if not common

Commit: build(buyer): switch persistence dependencies to Couchbase

## Task 3 — Configuration

Modify: BuyerService/src/main/resources/application.yml

Keep bootstrap:
- application name
- Config Server import
- Config Server URL

External config non-secret:
- Couchbase connection string
- bucket name
- scope name
- collection name
- query timeout
- key/value timeout
- actuator baseline

Secrets:
- BUYER_COUCHBASE_USERNAME
- BUYER_COUCHBASE_PASSWORD

No literal secret in repository.

Commit: config(buyer): add Couchbase connection configuration

## Task 4 — Hexagonal package skeleton

Target tree:

BuyerService/src/main/java/com/aydindemir/buyer/
- BuyerServiceApplication.java
- domain/model/
- domain/exception/
- application/port/in/
- application/port/out/
- application/service/
- adapter/in/rest/request/
- adapter/in/rest/response/
- adapter/in/rest/mapper/
- adapter/out/persistence/couchbase/document/
- adapter/out/persistence/couchbase/repository/
- adapter/out/persistence/couchbase/mapper/
- adapter/out/persistence/couchbase/adapter/

No empty future gRPC/Kafka classes.

Commit: refactor(buyer): establish Hexagonal Architecture package boundaries

## Task 5 — Domain value objects

Create:
- domain/model/BuyerId.java
- domain/model/PriceRange.java
- domain/model/RoomRange.java
- domain/model/AreaRange.java
- domain/model/LocationPreference.java
- domain/model/NotificationSettings.java
- domain/model/SavedSearch.java

BuyerId: immutable UUID wrapper.

PriceRange:
- BigDecimal min
- BigDecimal max
- Currency currency
- min >= 0
- max >= min

RoomRange:
- min >= 0
- max >= min

AreaRange:
- min non-negative/positive according to business rule
- max >= min

LocationPreference initially city + optional district; no premature geo complexity.

SavedSearch must not contain Elasticsearch DSL.

Commit: feat(buyer): add BuyerPreferences value objects

## Task 6 — BuyerPreferences Aggregate

Create: domain/model/BuyerPreferences.java

Fields:
- BuyerId buyerId
- PriceRange priceRange
- preferredLocations
- propertyTypes
- RoomRange
- AreaRange
- preferredFeatures
- NotificationSettings
- savedSearches
- createdAt
- updatedAt

Behavior:
- create/update preferences
- addSavedSearch
- removeSavedSearch only if needed now

Invariants:
- nested values valid
- collections defensively copied
- no public setters
- embedded saved-search growth risk documented

Create exceptions only if used:
- BuyerPreferencesNotFoundException
- InvalidBuyerPreferencesException
- DuplicateSavedSearchException only if uniqueness is a real rule

Commit: feat(buyer): add BuyerPreferences aggregate

## Task 7 — Inbound ports

Create:
- application/port/in/UpdateBuyerPreferencesUseCase.java
- application/port/in/GetBuyerPreferencesUseCase.java
- application/port/in/AddSavedSearchUseCase.java

Also create immutable application inputs:
- UpdateBuyerPreferencesCommand
- AddSavedSearchCommand
- GetBuyerPreferencesQuery

Commit: feat(buyer): define inbound ports

## Task 8 — Outbound persistence ports

Create:
- application/port/out/SaveBuyerPreferencesPort.java
- application/port/out/LoadBuyerPreferencesPort.java

Methods:
- save(BuyerPreferences)
- Optional<BuyerPreferences> load(BuyerId)

No Couchbase or Spring Data types in ports.

Commit: feat(buyer): define outbound persistence ports

## Task 9 — Application result and service

Create:
- application/service/BuyerPreferencesResult.java
- application/service/BuyerPreferencesApplicationService.java

Service implements all three inbound ports.

Update flow:
1. load existing if needed
2. create/update domain
3. save through outbound port
4. return result

Get flow:
1. load
2. not-found semantic
3. return result

AddSavedSearch flow:
1. load
2. domain behavior
3. save
4. return result

No Couchbase API dependency.

Commit: feat(buyer): implement BuyerPreferences application service

## Task 10 — Couchbase document model

Create: adapter/out/persistence/couchbase/document/BuyerPreferencesDocument.java

Document fields:
- id
- optional type discriminator
- buyerId
- price range
- preferred locations
- property types
- room range
- area range
- features
- notification settings
- saved searches
- createdAt
- updatedAt

Deterministic key: buyer-preferences::{buyerId}

Document model is not the domain model.

Commit: feat(buyer): add Couchbase document model

## Task 11 — Couchbase repository

Create: adapter/out/persistence/couchbase/repository/SpringDataBuyerPreferencesRepository.java

Primary lookup is document ID.

Do not add secondary queries/indexes if direct key access is enough.

Commit: feat(buyer): add Couchbase repository

## Task 12 — Persistence mapper

Create: adapter/out/persistence/couchbase/mapper/BuyerPreferencesDocumentMapper.java

Mappings:
- domain -> document
- document -> domain

Explicit mapper preferred if nested reconstruction is non-trivial.

Commit: feat(buyer): add Couchbase persistence mapping

## Task 13 — Persistence adapter

Create: adapter/out/persistence/couchbase/adapter/CouchbaseBuyerPreferencesAdapter.java

Implements SaveBuyerPreferencesPort and LoadBuyerPreferencesPort.

Responsibilities:
- deterministic key
- repository delegation
- mapping
- persistence exception translation when needed

Commit: feat(buyer): add Couchbase persistence adapter

## Task 14 — Bucket / scope / collection strategy

Suggested logical names:
- bucket: buyer
- scope: buyer_service
- collection: preferences

Exact names may be adjusted to existing conventions.

Day 9 bootstrap setup should be version-controlled/documented.

Direct key access means no speculative secondary index is required.

Commit: infra(buyer): document Couchbase bucket and collection setup

## Task 15 — REST request contracts

Create:
- adapter/in/rest/request/UpdateBuyerPreferencesRequest.java
- adapter/in/rest/request/AddSavedSearchRequest.java

Boundary validation includes required/non-negative/basic size constraints.

Cross-field range rules remain protected by domain value objects.

Commit: feat(buyer): add REST request contracts

## Task 16 — REST response and mapper

Create:
- adapter/in/rest/response/BuyerPreferencesResponse.java
- adapter/in/rest/mapper/BuyerPreferencesRestMapper.java

Mapper:
- request + buyerId -> command
- application result -> response

No business logic.

Commit: feat(buyer): add REST response and mapping

## Task 17 — REST controller

Create: adapter/in/rest/BuyerPreferencesController.java

Endpoints:
- PUT /buyers/{buyerId}/preferences
- GET /buyers/{buyerId}/preferences
- POST /buyers/{buyerId}/saved-searches

PUT can return 200 with current representation for simple idempotent semantics.

GET absent -> 404.

SavedSearch POST -> 201 candidate.

Commit: feat(buyer): expose preferences and saved-search API

## Task 18 — Error mapping

Stable error mappings:
- BuyerPreferencesNotFoundException -> 404 BUYER_PREFERENCES_NOT_FOUND
- invalid price/room/area semantic -> 422
- malformed validation -> 400

Raw Couchbase exception must not leak.

Commit: feat(buyer): map Buyer domain failures to API errors

## Task 19 — Domain tests

Create:
- PriceRangeTest.java
- RoomRangeTest.java
- AreaRangeTest.java
- BuyerPreferencesTest.java

Cases:
- valid ranges
- invalid ranges
- defensive copies
- add saved search

Commit: test(buyer): add domain invariant tests

## Task 20 — Test fake

Create test-only:
- application/support/InMemoryBuyerPreferencesStore.java

Implements outbound ports for readable application tests.

## Task 21 — Application tests

Create: BuyerPreferencesApplicationServiceTest.java

Cases:
- create/update success
- get success
- get not-found
- add saved search
- invalid domain input
- expected saved aggregate state

Commit: test(buyer): add application port tests

## Task 22 — Couchbase Testcontainers foundation

Create test support:
- BuyerCouchbaseContainerTestBase.java or equivalent

Responsibilities:
- start Couchbase container
- bootstrap bucket/scope/collection
- dynamic Spring properties

No hard-coded shared local Couchbase for tests.

Commit: test(buyer): add Couchbase Testcontainers foundation

## Task 23 — Persistence integration tests

Create: CouchbaseBuyerPreferencesAdapterIntegrationTest.java

Cases:
- save/load
- deterministic key
- update same document
- nested value round-trip
- saved search round-trip
- missing document -> Optional.empty

Commit: test(buyer): add Couchbase persistence integration tests

## Task 24 — CAS / concurrency decision

If Spring Data Couchbase CAS/version support fits cleanly, add version and concurrency test.

If not, explicitly defer rather than fake concurrency protection.

Optional class: BuyerPreferencesConcurrencyIntegrationTest.java

Optional commit: test(buyer): verify Couchbase CAS concurrency

## Task 25 — REST slice tests

Create: BuyerPreferencesControllerTest.java

Cases:
- PUT 200
- GET 200
- GET 404
- invalid request
- POST saved search

Use inbound port mocks, not Couchbase.

Commit: test(buyer): add REST adapter tests

## Task 26 — Hexagonal architecture tests

Create: architecture/BuyerHexagonalArchitectureTest.java

Rules:
- domain does not depend on application/adapter/Spring/Couchbase
- application does not depend on adapter
- adapter.in depends on application.port.in
- adapter.out depends on/implements application.port.out
- adapter.in must not directly depend on adapter.out
- no cycles

Commit: test(buyer): enforce Hexagonal Architecture boundaries

## Task 27 — Runtime smoke

Start Config Server, Eureka, Couchbase, BuyerService.

Verify:
- service registration
- PUT preferences
- GET preferences
- POST saved search

Automated tests remain acceptance baseline.

## Task 28 — Documentation

Modify:
- BuyerService/docs/DESIGN.md
- BuyerService/docs/PACKAGE-DESIGN.md
- BuyerService/ROADMAP.md
- docs/roadmap/day-09-buyer-couchbase-hexagonal.md

Record actual:
- bucket/scope/collection
- document key
- exact config keys
- test coverage
- deferred Offer/gRPC/Kafka/Redis work

Commit: docs(buyer): finalize Couchbase Hexagonal implementation

## Recommended Commit Sequence

1. refactor(buyer): align application base package
2. build(buyer): switch persistence dependencies to Couchbase
3. config(buyer): add Couchbase connection configuration
4. refactor(buyer): establish Hexagonal Architecture package boundaries
5. feat(buyer): add BuyerPreferences value objects
6. feat(buyer): add BuyerPreferences aggregate
7. feat(buyer): define inbound ports
8. feat(buyer): define outbound persistence ports
9. feat(buyer): implement BuyerPreferences application service
10. feat(buyer): add Couchbase document model
11. feat(buyer): add Couchbase repository
12. feat(buyer): add Couchbase persistence mapping
13. feat(buyer): add Couchbase persistence adapter
14. infra(buyer): document Couchbase bucket and collection setup
15. feat(buyer): add REST request contracts
16. feat(buyer): add REST response and mapping
17. feat(buyer): expose preferences and saved-search API
18. feat(buyer): map Buyer domain failures to API errors
19. test(buyer): add domain invariant tests
20. test(buyer): add application port tests
21. test(buyer): add Couchbase Testcontainers foundation
22. test(buyer): add Couchbase persistence integration tests
23. test(buyer): add REST adapter tests
24. test(buyer): enforce Hexagonal Architecture boundaries
25. docs(buyer): finalize Couchbase Hexagonal implementation

Adjacent tiny commits can be merged when they represent one coherent change, but unrelated architectural layers should not be collapsed into one giant commit.

## Explicitly Deferred from Day 9

Do not implement:
- Offer Aggregate
- CreateOffer
- Offer idempotency
- Redis business adapter
- AgentAvailability gRPC implementation
- Kafka publisher
- Saga
- notification delivery
- Elasticsearch query logic

Future interfaces should not be added unless a current Day 9 class genuinely needs them.

## Day 9 Final Gate

Day 9 closes only if:
- BuyerService no longer uses JPA/PostgreSQL
- Couchbase connection/config works
- BuyerPreferences domain is framework-free
- range value objects enforce invariants
- collections are defensively copied
- inbound ports exist
- outbound persistence ports exist
- application service depends only on ports/domain
- Couchbase document is separate from domain
- deterministic document key works
- save/load/update integration test passes
- REST PUT/GET/saved-search endpoints work
- raw Couchbase exception does not leak
- Hexagonal dependency rules are automated
- Config/Eureka/Actuator baseline works
- no Offer/Kafka/gRPC/Redis business implementation leaks into Day 9
- docs match actual implementation