# Day 11 — Exact File / Class / Commit Plan

## 0. Scope

Day 11 yalnızca PropertyService içindir.

Hedef:
- PostgreSQL/JPA -> MongoDB
- canonical Property Aggregate
- Vertical Slice Architecture
- GetProperty
- PublishProperty
- optimistic concurrency
- unit/integration/architecture tests

Day 11 içinde:
- Seller -> Property RabbitMQ command yok
- Kafka event publication yok
- Saga hold/reserve yok
- Search projection yok
- fake public POST /properties yok

## Task 1 — Existing PropertyService source audit

Verify:
- PropertyService/build.gradle
- PropertyService/src/main/resources/application.yml
- mevcut bootstrap class/package

Target base package: com.aydindemir.property

Mevcut package farklıysa controlled refactor yapılır.

Commit: refactor(property): align application base package

## Task 2 — Build dependencies

Modify: PropertyService/build.gradle

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
- Spring Data MongoDB
- Testcontainers JUnit Jupiter
- MongoDB Testcontainers support/module as appropriate
- ArchUnit if not common

Commit: build(property): switch persistence dependencies to MongoDB

## Task 3 — Configuration

Modify: PropertyService/src/main/resources/application.yml

Keep bootstrap:
- application name
- Config Server import
- Config Server URL

External non-secret config:
- Mongo connection URI/host template
- database name
- timeout options if explicitly configured
- actuator baseline

Secrets:
- PROPERTY_MONGO_USERNAME
- PROPERTY_MONGO_PASSWORD

No literal secret in repo.

Commit: config(property): add MongoDB connection configuration

## Task 4 — Vertical Slice package skeleton

Target tree:

PropertyService/src/main/java/com/aydindemir/property/
- PropertyServiceApplication.java
- shared/domain/model/
- shared/domain/exception/
- shared/persistence/document/
- shared/persistence/repository/
- shared/persistence/mapper/
- shared/persistence/adapter/
- shared/web/error/
- getbyid/
- publish/

Do not create future slices such as hold/reserve/withdraw unless implemented now.

Commit: refactor(property): establish Vertical Slice foundation

## Task 5 — PropertyId / SellerId / AgentId

Create:
- shared/domain/model/PropertyId.java
- shared/domain/model/SellerId.java
- shared/domain/model/AgentId.java

Immutable UUID wrappers.

## Task 6 — Money

Create: shared/domain/model/Money.java

Fields:
- BigDecimal amount
- Currency currency

Rules:
- amount > 0 for property price
- currency non-null
- BigDecimal semantics per Java standard

Tests: MoneyTest.java

## Task 7 — Address

Create: shared/domain/model/Address.java

Minimum fields:
- city
- district
- line1 or description

Do not over-model postal/geocoding data yet.

## Task 8 — GeoLocation

Create: shared/domain/model/GeoLocation.java

Fields:
- latitude
- longitude

Rules:
- latitude range
- longitude range

Optional in Day 11 if property can exist without geocoded location.

## Task 9 — Area

Create: shared/domain/model/Area.java

Use BigDecimal or appropriate numeric type.

Rule: positive.

## Task 10 — PropertyStatus

Create: shared/domain/model/PropertyStatus.java

Values:
- DRAFT
- PUBLISHED
- ON_HOLD
- RESERVED
- SOLD
- WITHDRAWN

Day 11 actively exercises DRAFT and PUBLISHED only.

## Task 11 — Property Aggregate

Create: shared/domain/model/Property.java

Fields:
- PropertyId propertyId
- SellerId sellerId
- AgentId agentId optional
- title
- description
- propertyType
- Address address
- GeoLocation geoLocation optional
- Money price
- Area area
- roomCount
- features
- typeSpecificAttributes only if current model truly needs it
- PropertyStatus status
- activeOfferId optional but unused Day 11
- createdAt
- updatedAt
- publishedAt optional

Behavior:
- createDraft(...)
- publish(Clock)
- updateDetails(...) only if needed by current test fixture/use-case
- changePrice(...) candidate but not required for public slice Day 11

Invariants:
- price positive
- required identity/title/type/address
- only DRAFT can publish initially
- SOLD/WITHDRAWN cannot publish
- no public state setters

Tests:
- PropertyTest.java

Commit: feat(property): add Property aggregate and value objects

## Task 12 — Domain exceptions

Create only needed:
- PropertyNotFoundException.java
- InvalidPropertyStateException.java
- InvalidPropertyPriceException.java if Money error is not enough
- PropertyConcurrentModificationException.java

Commit can be grouped with Aggregate.

## Task 13 — Shared repository abstraction

Create: shared/domain/model or shared/domain/repository/PropertyRepository.java

Preferred path:
- shared/domain/repository/PropertyRepository.java

Methods:
- save(Property)
- Optional<Property> findById(PropertyId)

Do not add speculative seller/status query methods until actual slice needs them.

Commit: feat(property): add Property repository contract

## Task 14 — Mongo document

Create: shared/persistence/document/PropertyDocument.java

Collection: properties

Fields:
- id
- sellerId
- agentId
- title
- description
- propertyType
- address nested document/value
- geoLocation nested
- priceAmount
- currency
- area
- roomCount
- features
- typeSpecificAttributes if kept
- status
- activeOfferId optional
- createdAt
- updatedAt
- publishedAt
- version

Annotations:
- @Document
- @Id
- @Version

Do not annotate domain Aggregate.

Commit: feat(property): add Mongo property document

## Task 15 — Mongo repository

Create: shared/persistence/repository/SpringDataPropertyRepository.java

Extends appropriate Mongo repository.

Day 11 minimum direct lookup by id.

Future query methods not added speculatively.

Commit: feat(property): add Mongo repository

## Task 16 — Persistence mapper

Create: shared/persistence/mapper/PropertyDocumentMapper.java

Mappings:
- domain -> document
- document -> domain

Reconstruction must preserve timestamps/status and not trigger create-new semantics.

Explicit mapper preferred if Aggregate reconstruction is non-trivial.

Commit: feat(property): add Mongo persistence mapping

## Task 17 — Repository adapter

Create: shared/persistence/adapter/MongoPropertyRepositoryAdapter.java

Implements PropertyRepository.

Responsibilities:
- repository delegation
- mapping
- translate optimistic locking exception to PropertyConcurrentModificationException

Commit: feat(property): add Mongo repository adapter

## Task 18 — Index configuration

Create or configure only current justified indexes.

Candidate:
- sellerId + createdAt
- status

Since Day 11 public use-case is id lookup + publish, status index may be deferred if not queried yet.

Rule:
index only if tied to an actual/near-term query.

Optional files:
- shared/persistence/configuration/MongoIndexConfiguration.java
or repository-level index annotations if consistent with project standard.

Do not add 2dsphere yet unless geo search is implemented.

Commit if applicable: db(property): add Mongo indexes for current queries

## Task 19 — GetProperty query

Create in getbyid slice:
- getbyid/GetPropertyQuery.java
- getbyid/GetPropertyResult.java
- getbyid/GetPropertyHandler.java

Flow:
1. repository findById
2. not found -> PropertyNotFoundException
3. map to result

Handler may be Spring bean; shared domain remains Spring-free.

Commit: feat(property): add get-property slice

## Task 20 — GetProperty controller

Create:
- getbyid/GetPropertyController.java
- getbyid/GetPropertyResponse.java
- optional mapper if not inline-simple

Endpoint:
- GET /properties/{propertyId}

Return:
- 200
- 404

Commit can be grouped with slice if coherent.

## Task 21 — PublishProperty command

Create in publish slice:
- publish/PublishPropertyCommand.java
- publish/PublishPropertyResult.java
- publish/PublishPropertyHandler.java

Flow:
1. load Property
2. call property.publish(clock)
3. save
4. return result

Transaction boundary:
Mongo single-document update/save.

Day 11 no Kafka event publish.

Commit: feat(property): add publish-property slice

## Task 22 — Publish controller

Create:
- publish/PublishPropertyController.java
- publish/PublishPropertyResponse.java

Endpoint:
- POST /properties/{propertyId}/publish

Return:
- 200 with updated representation or 204

Recommended 200 if status/timestamp returned.

Errors:
- 404
- 409 invalid state
- 409 optimistic conflict

Commit: feat(property): expose publish-property API

## Task 23 — Error mapping

Create/modify shared web error handler:
- shared/web/error/PropertyExceptionHandler.java or service-consistent advice

Mappings:
- PropertyNotFoundException -> 404 PROPERTY_NOT_FOUND
- InvalidPropertyStateException -> 409 INVALID_PROPERTY_STATE
- PropertyConcurrentModificationException -> 409 PROPERTY_CONCURRENT_MODIFICATION or stable chosen code
- malformed request -> 400

No raw Mongo/Spring Data exception leaks.

Commit: feat(property): map Property domain failures to API errors

## Task 24 — Test fixture strategy

Important:
Target business flow creates Property from Seller listing command, but that is deferred.

Therefore Day 11 must not add public POST /properties just for testing.

Create test-only fixture/helper:
- src/test/java/com/aydindemir/property/support/PropertyTestFactory.java
and/or
- repository-level integration test setup that inserts DRAFT directly through adapter

No production fake creation endpoint.

Commit can be part of test commit.

## Task 25 — Domain tests

Create:
- PropertyTest.java
- MoneyTest.java
- GeoLocationTest.java if implemented
- AreaTest.java

Cases:
- valid draft
- positive price
- invalid price
- DRAFT -> PUBLISHED
- republish rejected
- withdrawn/sold publish rejected if those transitions are constructed in test
- timestamps update via fixed Clock

Commit: test(property): add aggregate invariant tests

## Task 26 — Handler tests

Create:
- getbyid/GetPropertyHandlerTest.java
- publish/PublishPropertyHandlerTest.java

Use fake/mock PropertyRepository.

Cases:
- get success
- get not found
- publish success
- invalid state
- save invoked with published aggregate

Commit: test(property): add vertical slice handler tests

## Task 27 — Mongo Testcontainers foundation

Create test support:
- src/test/java/com/aydindemir/property/support/PropertyMongoContainerTestBase.java

Responsibilities:
- MongoDB container
- dynamic properties
- clean test collection strategy

No shared local Mongo dependency for automated tests.

Commit: test(property): add MongoDB Testcontainers foundation

## Task 28 — Repository integration test

Create:
- shared/persistence/adapter/MongoPropertyRepositoryAdapterIntegrationTest.java

Cases:
- save/load round-trip
- nested value objects round-trip
- enum/status round-trip
- timestamps
- version initialized/updated
- missing id -> Optional.empty

Commit: test(property): add Mongo persistence integration tests

## Task 29 — Optimistic locking test

Create:
- shared/persistence/adapter/PropertyOptimisticLockingIntegrationTest.java

Scenario:
1. persist DRAFT
2. load two copies
3. save/publish first
4. attempt stale save second
5. expect translated conflict

This is important because later Offer/Saga concurrency depends on Property being the canonical concurrency owner.

Commit: test(property): verify optimistic locking behavior

## Task 30 — Get controller slice test

Create:
- getbyid/GetPropertyControllerTest.java

Cases:
- 200
- 404 mapping
- response contract

## Task 31 — Publish controller slice test

Create:
- publish/PublishPropertyControllerTest.java

Cases:
- 200 publish
- 404
- 409 invalid state
- malformed id

Commit: test(property): add REST slice tests

## Task 32 — Vertical Slice architecture tests

Create:
- architecture/PropertyVerticalSliceArchitectureTest.java

Rules:
- getbyid must not depend on publish internals
- publish must not depend on getbyid internals
- shared may not depend on feature slices
- domain model must not depend on Mongo/Spring/web
- controllers may call their own handler only
- no cycles

Commit: test(property): enforce Vertical Slice boundaries

## Task 33 — Runtime smoke

Start:
- Config Server
- Eureka
- MongoDB
- PropertyService

Prepare DRAFT through test fixture/manual repository bootstrap only for local smoke.

Verify:
- GET property
- POST publish
- GET shows PUBLISHED

No fake production creation endpoint.

## Task 34 — Documentation

Modify:
- PropertyService/docs/DESIGN.md
- PropertyService/docs/PACKAGE-DESIGN.md
- PropertyService/ROADMAP.md
- docs/roadmap/day-11-property-mongodb-vertical-slice.md

Record actual:
- collection name
- document fields
- version strategy
- current indexes
- Get/Publish slices
- Seller command creation deferred
- Kafka event publication deferred
- Saga transitions deferred

Commit: docs(property): finalize MongoDB Vertical Slice implementation

## Recommended Commit Sequence

1. refactor(property): align application base package
2. build(property): switch persistence dependencies to MongoDB
3. config(property): add MongoDB connection configuration
4. refactor(property): establish Vertical Slice foundation
5. feat(property): add Property aggregate and value objects
6. feat(property): add Property repository contract
7. feat(property): add Mongo property document
8. feat(property): add Mongo repository
9. feat(property): add Mongo persistence mapping
10. feat(property): add Mongo repository adapter
11. db(property): add Mongo indexes for current queries — only if justified
12. feat(property): add get-property slice
13. feat(property): add publish-property slice
14. feat(property): map Property domain failures to API errors
15. test(property): add aggregate invariant tests
16. test(property): add vertical slice handler tests
17. test(property): add MongoDB Testcontainers foundation
18. test(property): add Mongo persistence integration tests
19. test(property): verify optimistic locking behavior
20. test(property): add REST slice tests
21. test(property): enforce Vertical Slice boundaries
22. docs(property): finalize MongoDB Vertical Slice implementation

Adjacent tiny commits may be combined if cohesive. Domain, persistence, slice, test and docs concerns should remain independently reviewable.

## Explicitly Deferred from Day 11

Do not implement:
- Seller listing command consumer
- PropertyCreated event
- Kafka producer
- Search projection
- HoldPropertyForOffer
- ReleasePropertyHold
- ReserveProperty
- MarkPropertySold
- WithdrawProperty endpoint
- ChangePrice endpoint
- AssignAgent endpoint
- geo search

Domain methods for future states should only exist if the Aggregate design genuinely benefits and tests cover them; otherwise implement when the corresponding Day arrives.

## Critical Design Note — Creation path

Production target says canonical Property is created from SubmitPropertyListingCommand.

Since reliable Seller -> Property dispatch is deferred, Day 11 does not expose a public create endpoint that would contradict the target workflow.

Persistence creation is verified through test fixture/integration setup.

## Critical Design Note — Concurrency owner

PropertyService is canonical owner of property availability/reservation state.

Mongo @Version/optimistic concurrency established in Day 11 becomes the baseline for later Saga race handling.

## Day 11 Final Gate

Day 11 closes only if:
- PropertyService no longer uses JPA/PostgreSQL
- MongoDB connection/config works
- Property Aggregate is framework-free
- Money/Area/Geo value rules are tested
- DRAFT -> PUBLISHED transition works
- invalid republish is rejected
- Mongo document is separate from domain
- @Version is configured
- stale concurrent update is detected
- GET slice works
- Publish slice works
- no fake public POST /properties exists
- raw Mongo exception does not leak
- Testcontainers verifies real Mongo semantics
- Vertical Slice dependency rules are automated
- Config/Eureka/Actuator baseline works
- no RabbitMQ/Kafka/Saga/Search implementation leaks into Day 11
- docs match actual implementation