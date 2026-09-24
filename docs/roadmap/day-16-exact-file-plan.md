# Day 16 — Exact GraphQL Read API Plan

## Scope

- SearchService GraphQL schema
- resolver over SearchPropertiesHandler
- authorization
- bounded query cost
- GraphQL tests and REST parity

## Task 1 — GraphQL dependency setup

Modify SearchService/build.gradle

Add:
- spring-boot-starter-graphql

Do not add GraphQL to all services.

Commit: build(search): add GraphQL support

## Task 2 — GraphQL schema

Create:
- SearchService/src/main/resources/graphql/search.graphqls

Initial schema:
- Query.searchProperties
- Query.property optional only if read use-case exists

Types:
- PropertySearchResult
- PropertySearchItem
- SearchPageInfo
- SearchFilterInput

Do not expose Elasticsearch DSL.

Commit: feat(search): add GraphQL search schema

## Task 3 — GraphQL input design

SearchFilterInput candidate fields:
- text
- status
- propertyType
- city
- district
- minPrice
- maxPrice
- page
- size

Reuse existing SearchPropertiesQuery application model via mapper.

## Task 4 — GraphQL resolver/controller

Create:
- graphql/SearchQueryController.java

Use @QueryMapping or supported annotation.

Responsibilities:
- map GraphQL input -> SearchPropertiesQuery
- call existing SearchPropertiesHandler
- map result

No duplicate Elasticsearch query logic.

Commit: feat(search): add GraphQL search resolver

## Task 5 — GraphQL response models

Create only if needed:
- graphql/PropertySearchGraphQlResponse.java
- graphql/SearchPageInfo.java

Can reuse application result if transport coupling remains acceptable only at adapter mapping boundary; prefer transport model for clarity.

Commit can group with resolver.

## Task 6 — GraphQL authorization

Apply Search read role/scope policy from Day 14.

Do not rely on resolver being hidden behind Gateway only.

Commit: feat(search): secure GraphQL search queries

## Task 7 — GraphQL query complexity/depth

Set bounded policy.

Since initial schema is shallow, keep config simple.

Do not add third-party complexity framework unless needed.

At minimum:
- bounded page size
- no recursive schema
- disable/limit expensive unbounded query shapes

Commit: config(search): bound GraphQL query cost

## Task 8 — N+1 review

Initial Search GraphQL should call one search handler returning a batch result, so N+1 should not arise.

Do not add DataLoader unless nested resolver pattern actually creates N+1.

Document decision.

## Task 9 — GraphQL error mapping

Map:
- validation -> GraphQL error with stable extension code
- forbidden -> security error
- downstream unavailable -> stable error extension

Do not expose stack trace/Elasticsearch exception.

Create:
- graphql/GraphQlExceptionResolver.java if needed

Commit: feat(search): standardize GraphQL error mapping

## Task 10 — GraphQL tests

Create:
- graphql/SearchGraphQlTest.java

Cases:
- search query success
- filters
- empty result
- invalid page/price range
- unauthorized/forbidden

Use GraphQlTester.

Commit: test(graphql): add SearchService GraphQL tests

## Task 11 — REST parity test

Verify REST and GraphQL call the same application handler and produce semantically consistent results.

Do not duplicate business query logic.

Create optional:
- SearchProtocolParityTest.java

Commit only if useful:
test(search): verify REST and GraphQL query parity

## Task 12 — Contract documentation

Update:
- docs/contracts/grpc-contract.md
- docs/contracts/graphql-schema.md
- docs/architecture/communication-architecture.md
- docs/roadmap/day-15-grpc-graphql.md

Record actual:
- proto package/version
- gRPC deadline
- auth mode
- status mapping
- GraphQL schema
- scope
- complexity/page limits

Commit: docs(protocol): finalize gRPC and GraphQL contracts

## Recommended Commit Sequence

1. docs(protocol): confirm REST gRPC GraphQL boundaries
2. build(grpc): add protobuf and gRPC support
3. feat(contract): add AgentAvailability gRPC contract
4. build(grpc): configure protobuf code generation
5. feat(agent): add availability query use case
6. feat(agent): expose availability gRPC service
7. feat(agent): standardize gRPC error mapping
8. config(agent): configure gRPC server
9. feat(buyer): add AgentAvailability outbound port
10. feat(buyer): add AgentAvailability gRPC adapter
11. config(buyer): configure Agent gRPC client
12. feat(grpc): secure Agent availability calls
13. feat(buyer): add agent availability application flow
14. test(grpc): add adapter unit tests
15. test(grpc): add availability integration test
16. build(search): add GraphQL support
17. feat(search): add GraphQL search schema
18. feat(search): add GraphQL search resolver
19. feat(search): secure GraphQL search queries
20. config(search): bound GraphQL query cost
21. feat(search): standardize GraphQL error mapping
22. test(graphql): add SearchService GraphQL tests
23. docs(protocol): finalize gRPC and GraphQL contracts

Adjacent technical commits may be merged if cohesive; gRPC and GraphQL should remain reviewable as separate protocol capabilities.

## Explicitly Deferred from Day 15

Do not implement:
- GraphQL mutation
- GraphQL subscriptions
- full viewing scheduler
- streaming gRPC
- bidirectional streaming
- mTLS
- service mesh
- Kafka
- Saga
- custom GraphQL federation

## Critical Design Note — REST remains primary

gRPC and GraphQL are specialized additions, not replacements for all REST endpoints.

## Critical Design Note — No duplicate business logic

REST and GraphQL must call the same application query/use-case layer.

gRPC adapter must call Agent application use-case rather than persistence directly.

## Critical Design Note — Deadlines

Every gRPC client call has an explicit deadline.

Timeout ownership is service-client level; later Day 20 resilience policies build on this baseline.

## Day 15 Final Gate

Day 15 closes only if:
- proto contract is versioned and generated reproducibly
- AgentService exposes gRPC availability through application use case
- BuyerService calls Agent via outbound port + gRPC adapter
- explicit deadline exists
- auth/service identity is applied
- gRPC errors map to semantic statuses
- integration test proves client/server compatibility
- SearchService GraphQL schema is explicit
- GraphQL resolver reuses existing search handler
- GraphQL read authorization works
- query/page cost is bounded
- GraphQL errors do not leak internals
- REST still works
- no business logic duplicated between REST/GraphQL/gRPC adapters
- no Kafka/Saga/mTLS/streaming scope leaks into Day 15
- docs match actual implementation

## Source-of-truth note

This file follows the final Day 15–33 roadmap. Earlier combined Day numbering is superseded by `docs/roadmap/LEGACY-DAY-MAPPING.md`.
