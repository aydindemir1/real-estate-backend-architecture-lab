# Day 12 — Exact File / Class / Commit Plan

## 0. Scope

Day 12 yalnızca SearchService içindir.

Hedef:
- standalone SearchService runtime module
- Elasticsearch
- Vertical Slice + CQRS Query Side
- PropertySearchDocument
- explicit mapping
- SearchProperties query
- REST search endpoint
- Testcontainers
- architecture fitness

Day 12 içinde:
- Kafka projection consumer yok
- Property lifecycle event processing yok
- GraphQL yok
- autocomplete/facets/geo yok
- reindex/reconciliation yok

## Task 1 — SearchService module audit

Verify:
- settings.gradle içinde SearchService include
- SearchService/build.gradle
- SearchService bootstrap class
- SearchService application.yml

Day 7 foundation eksikse önce sadece eksik foundation tamamlanır.

Target base package: com.aydindemir.search

Commit only if needed:
refactor(search): align application base package

## Task 2 — Build dependencies

Modify: SearchService/build.gradle

Required:
- Spring Boot Web MVC
- Spring Data Elasticsearch
- Config Client
- Eureka Client
- Actuator
- tracing baseline
- OpenAPI if service docs retained
- Testcontainers JUnit Jupiter
- Elasticsearch Testcontainers support/module as appropriate
- ArchUnit if not common

Do not add:
- MongoDB
- Kafka
- GraphQL
- Redis

Commit: build(search): add Elasticsearch query-side dependencies

## Task 3 — Configuration

Modify: SearchService/src/main/resources/application.yml

Keep bootstrap:
- spring.application.name = search-service
- Config Server import
- Config Server URL

External config non-secret:
- Elasticsearch host/URI
- index name
- connection timeout
- socket/request timeout
- actuator baseline

Secrets only if local security enabled:
- SEARCH_ELASTIC_USERNAME
- SEARCH_ELASTIC_PASSWORD

Commit: config(search): add Elasticsearch connection configuration

## Task 4 — Package skeleton

Target tree:

SearchService/src/main/java/com/aydindemir/search/
- SearchServiceApplication.java
- shared/model/
- shared/elasticsearch/document/
- shared/elasticsearch/repository/
- shared/elasticsearch/adapter/
- shared/elasticsearch/configuration/
- shared/web/error/
- searchproperties/

Do not create autocomplete/facet/geosearch/reindex/eventconsumer slices yet.

Commit: refactor(search): establish CQRS query-side package boundaries

## Task 5 — PropertySearchDocument

Create: shared/elasticsearch/document/PropertySearchDocument.java

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

Optional only if immediately useful:
- geoLocation

Rule:
This is a read projection document, not canonical Property Aggregate.

Commit: feat(search): add PropertySearchDocument

## Task 6 — Explicit Elasticsearch mapping

Create one explicit mapping artifact.

Preferred candidate:
- SearchService/src/main/resources/elasticsearch/property-search-index.json

Mapping:
- propertyId -> keyword
- title -> text
- description -> text
- propertyType -> keyword
- city -> keyword
- district -> keyword
- price -> scaled_float/double based on chosen numeric strategy
- currency -> keyword
- roomCount -> integer
- features -> keyword
- status -> keyword
- publishedAt -> date
- updatedAt -> date

Local settings:
- number_of_shards = 1
- number_of_replicas = 0

Dynamic mapping should not be blindly relied upon.

Commit: db(search): add explicit Elasticsearch index mapping

## Task 7 — Index bootstrap

Create candidate:
- shared/elasticsearch/configuration/ElasticsearchIndexInitializer.java

Responsibilities:
- check index existence
- create index with mapping/settings if absent

Alternative:
explicit external bootstrap script if project prefers infra-owned creation.

Day 12 should choose one clear ownership model.

Rule:
Do not delete/recreate index on every startup.

Commit: feat(search): add controlled index bootstrap

## Task 8 — Repository abstraction

Create:
- shared/elasticsearch/repository/PropertySearchRepository.java

This may be application-facing abstraction if Spring Data repository is kept infrastructure-specific.

Methods minimum:
- save/index document for test/bootstrap support
- find/search via adapter abstraction

Prefer search-specific query object rather than leaking Pageable/NativeQuery upward.

Commit: feat(search): add search repository contract

## Task 9 — Spring Data Elasticsearch repository

Create:
- shared/elasticsearch/repository/SpringDataPropertySearchRepository.java

Primary simple operations:
- save
- findById

Complex search can use ElasticsearchOperations in adapter.

Do not force all search logic into derived repository methods.

Commit: feat(search): add Spring Data Elasticsearch repository

## Task 10 — Search criteria model

Create:
- searchproperties/SearchPropertiesQuery.java

Fields:
- text query optional
- status optional
- propertyType optional
- minPrice optional
- maxPrice optional
- page size
- cursor/searchAfter candidate optional later

Day 12 pagination:
bounded page/size acceptable for small local data.

Deep pagination strategy search_after documented for later.

Commit: feat(search): add search query model

## Task 11 — Search result model

Create:
- searchproperties/SearchPropertyItem.java
- searchproperties/SearchPropertiesResult.java

Result contains:
- items
- page metadata
- total if query strategy supports safely

Do not expose Elasticsearch hit internals.

Commit can group with query handler.

## Task 12 — Elasticsearch search adapter

Create:
- shared/elasticsearch/adapter/ElasticsearchPropertySearchAdapter.java

Uses:
- ElasticsearchOperations or supported Spring Data abstraction

Responsibilities:
- build query from SearchPropertiesQuery
- text query title/description
- exact filters status/type/city/district
- price range
- bounded pagination
- map hits to result

No business write ownership.

Commit: feat(search): add Elasticsearch search adapter

## Task 13 — SearchPropertiesHandler

Create:
- searchproperties/SearchPropertiesHandler.java

Flow:
1. validate/normalize query
2. enforce bounded page size
3. call search adapter
4. return SearchPropertiesResult

No canonical Property mutation.

Commit: feat(search): add SearchProperties handler

## Task 14 — REST request/query parameter contract

Create optional mapper model:
- searchproperties/SearchPropertiesRequest.java

Or bind query params directly if small and readable.

Candidate query params:
- q
- status
- type
- city
- district
- minPrice
- maxPrice
- page
- size

Sorting:
do not expose arbitrary field names.

If sort is included, whitelist supported values.

Commit: feat(search): add search REST query contract

## Task 15 — REST response contract

Create:
- searchproperties/SearchPropertiesResponse.java
- searchproperties/SearchPropertyResponse.java

Expose only stable search result data.

Commit can group with controller.

## Task 16 — SearchPropertiesController

Create:
- searchproperties/SearchPropertiesController.java

Endpoint:
- GET /search/properties

Semantics:
- empty results -> 200 with empty list
- invalid range/page -> 400
- no 404 for empty search

Commit: feat(search): expose property search API

## Task 17 — Error/validation handling

Stable errors:
- invalid page size -> 400 VALIDATION_ERROR
- minPrice > maxPrice -> 422 INVALID_PRICE_RANGE or 400 if treated as request validation; choose one consistent with global error policy
- Elasticsearch unavailable -> 503 via translated infrastructure error

No raw Elasticsearch exception leaks.

Commit: feat(search): map search failures to API errors

## Task 18 — Test indexing fixture

Create test-only:
- src/test/java/com/aydindemir/search/support/PropertySearchDocumentFactory.java

Creates deterministic documents.

Do not add production write endpoint.

## Task 19 — Query handler unit tests

Create:
- searchproperties/SearchPropertiesHandlerTest.java

Cases:
- normalize empty q
- max page size enforcement
- invalid range
- adapter receives expected criteria

Commit: test(search): add query handler tests

## Task 20 — Elasticsearch Testcontainers foundation

Create:
- support/SearchElasticsearchContainerTestBase.java

Responsibilities:
- start Elasticsearch container
- configure single-node test mode
- dynamic properties
- wait strategy
- index bootstrap

Pin container version compatible with client/server expectations.

Commit: test(search): add Elasticsearch Testcontainers foundation

## Task 21 — Mapping/index integration test

Create:
- shared/elasticsearch/ElasticsearchIndexMappingIntegrationTest.java

Verify:
- index created
- 1 shard
- 0 replica local/test
- field mappings expected

Commit: test(search): verify index mapping

## Task 22 — Search integration test

Create:
- shared/elasticsearch/ElasticsearchPropertySearchAdapterIntegrationTest.java

Cases:
- index documents
- text query title
- text query description
- exact status filter
- exact propertyType filter
- city/district filter
- price range
- combined filters
- empty results
- bounded pagination

Commit: test(search): add Elasticsearch query integration tests

## Task 23 — REST slice tests

Create:
- searchproperties/SearchPropertiesControllerTest.java

Cases:
- basic query 200
- filters 200
- empty list 200
- invalid page/size 400
- invalid price range
- translated dependency failure

Commit: test(search): add search REST adapter tests

## Task 24 — CQRS query-side architecture tests

Create:
- architecture/SearchQuerySideArchitectureTest.java

Rules:
- SearchService must not depend on PropertyService source packages
- no MongoDB dependency
- no canonical Property Aggregate
- no business write repository
- searchproperties slice may depend on shared search infrastructure
- shared must not depend on feature slice
- no cycles

Optional rule:
classes in search service should not use mutation-oriented names like saveCanonicalProperty.

Commit: test(search): enforce CQRS query-side boundaries

## Task 25 — Runtime smoke

Start:
- Config Server
- Eureka
- Elasticsearch
- SearchService

Index deterministic test/local documents through test/admin bootstrap only.

Verify:
- service starts/registers
- GET /search/properties?q=...
- exact filter
- price range

No Kafka expected.

## Task 26 — Documentation

Modify:
- SearchService/docs/DESIGN.md
- SearchService/docs/PACKAGE-DESIGN.md
- SearchService/ROADMAP.md
- docs/roadmap/day-12-search-elasticsearch-cqrs.md

Record actual:
- index name
- mapping file
- bootstrap ownership
- supported query params
- pagination limit
- current analyzers
- Kafka projection deferred to Day 18
- GraphQL deferred to Day 15
- reindex deferred to Day 25

Commit: docs(search): finalize Elasticsearch CQRS query-side foundation

## Recommended Commit Sequence

1. build(search): add Elasticsearch query-side dependencies
2. config(search): add Elasticsearch connection configuration
3. refactor(search): establish CQRS query-side package boundaries
4. feat(search): add PropertySearchDocument
5. db(search): add explicit Elasticsearch index mapping
6. feat(search): add controlled index bootstrap
7. feat(search): add search repository contract
8. feat(search): add Spring Data Elasticsearch repository
9. feat(search): add search query model
10. feat(search): add Elasticsearch search adapter
11. feat(search): add SearchProperties handler
12. feat(search): add search REST query contract
13. feat(search): expose property search API
14. feat(search): map search failures to API errors
15. test(search): add query handler tests
16. test(search): add Elasticsearch Testcontainers foundation
17. test(search): verify index mapping
18. test(search): add Elasticsearch query integration tests
19. test(search): add search REST adapter tests
20. test(search): enforce CQRS query-side boundaries
21. docs(search): finalize Elasticsearch CQRS query-side foundation

Adjacent tiny commits can be combined if they remain cohesive. Index/mapping, query implementation and tests should stay independently reviewable.

## Explicitly Deferred from Day 12

Do not implement:
- Kafka consumer
- PropertyPublished projection handler
- PropertyUpdated projection handler
- PropertyPriceChanged projection handler
- PropertyWithdrawn projection handler
- GraphQL
- autocomplete
- facets
- fuzzy search
- geo search
- Turkish analyzer
- edge n-gram
- reindex
- reconciliation
- search_after deep pagination

## Analyzer decision

Day 12 starts with standard analyzer.

Turkish analyzer/asciifolding/edge n-gram are added only when a real search requirement justifies them.

## Source-of-truth rule

Elasticsearch remains derived/query-side storage.

Day 12 test documents are fixture data only.

Production-like canonical Property writes stay in PropertyService/MongoDB.

## Day 12 Final Gate

Day 12 closes only if:
- SearchService starts as independent module
- Elasticsearch connection/config works
- explicit mapping exists
- local index uses 1 shard / 0 replica
- PropertySearchDocument is clearly a projection/read model
- text query works
- exact status/type filters work
- price range works
- pagination is bounded
- empty search returns 200 empty list
- raw Elasticsearch exception does not leak
- Testcontainers verifies real Elasticsearch behavior
- SearchService has no MongoDB/canonical write dependency
- CQRS query-side rules are automated
- Config/Eureka/Actuator baseline works
- no Kafka/GraphQL/reindex implementation leaks into Day 12
- docs match actual implementation