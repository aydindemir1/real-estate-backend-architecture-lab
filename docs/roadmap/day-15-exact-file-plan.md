# Day 15 — Exact File / Class / Contract / Commit Plan

## 0. Scope

Day 15 protocol specialization günüdür.

Hedef:
- REST primary public API olarak korunur
- BuyerService -> AgentService internal gRPC availability
- SearchService GraphQL read/query
- protocol-specific timeout/auth/error semantics
- contract/integration tests

Day 15 içinde:
- Kafka yok
- Saga yok
- GraphQL mutation yok
- full service-mesh/mTLS yok
- REST replacement yok

## Task 1 — Protocol boundary audit

Confirm:
- REST public/business API remains baseline
- gRPC only for internal Agent availability use-case
- GraphQL only for flexible read/search use-case

Document rationale in communication architecture if needed.

Commit: docs(protocol): confirm REST gRPC GraphQL boundaries

## Task 2 — gRPC build support

Modify root/dependency governance only if needed.

Add to relevant modules:
- protobuf plugin
- protobuf-java
- grpc-stub
- grpc-protobuf
- grpc-spring integration library only if chosen and compatible

Modules:
- AgentService
- BuyerService

Do not add gRPC dependency globally to every service.

Commit: build(grpc): add protobuf and gRPC support

## Task 3 — Proto source layout

Create shared contract location candidate:
- contracts/grpc/agent-availability.proto

or module-owned contract if repository convention prefers provider ownership.

Recommended package:
- realestate.agent.v1

Java package option:
- com.aydindemir.contract.agent.v1

## Task 4 — AgentAvailabilityService proto

Define:
- service AgentAvailabilityService
- rpc CheckAvailability(CheckAvailabilityRequest) returns (CheckAvailabilityResponse)

Request fields:
- string agent_id
- optional requested_time only if current business use-case truly needs it

Response fields:
- bool available
- string status
- optional reason_code

Compatibility rules:
- field numbers never reused
- additive changes preferred
- no domain entity dump

Commit: feat(contract): add AgentAvailability gRPC contract

## Task 5 — Proto generation configuration

Configure generated source directories.

Ensure generated code is build output, not manually edited.

Verify:
- AgentService compile
- BuyerService compile

Commit: build(grpc): configure protobuf code generation

## Task 6 — AgentService application query/use-case

Create if not already represented:
- application/query/CheckAgentAvailabilityQuery.java
- application/result/AgentAvailabilityResult.java
- application/usecase/CheckAgentAvailabilityUseCase.java

Implementation may reuse AgentApplicationService or a focused query service.

Rule:
gRPC adapter must not query persistence directly.

Commit: feat(agent): add availability query use case

## Task 7 — AgentService gRPC server adapter

Create package:
- infrastructure/grpc/ or presentation/grpc/ depending existing Clean Architecture convention

Preferred:
- presentation/grpc/AgentAvailabilityGrpcService.java

Responsibilities:
- map protobuf request -> application query
- call use case
- map result -> protobuf response
- map application exceptions -> gRPC status

No business rules in adapter.

Commit: feat(agent): expose availability gRPC service

## Task 8 — gRPC status mapping

Map:
- invalid id/input -> INVALID_ARGUMENT
- agent not found -> NOT_FOUND
- auth missing -> UNAUTHENTICATED
- forbidden -> PERMISSION_DENIED
- invalid/precondition state -> FAILED_PRECONDITION
- timeout -> DEADLINE_EXCEEDED where applicable
- temporary dependency failure -> UNAVAILABLE

Create:
- AgentGrpcExceptionMapper.java if useful

Commit: feat(agent): standardize gRPC error mapping

## Task 9 — gRPC server configuration

Configure:
- port separate from HTTP if library requires
- max message size bounded
- reflection only local/dev if used
- interceptors for auth/tracing

No unlimited defaults without review.

Commit: config(agent): configure gRPC server

## Task 10 — BuyerService outbound port

Create:
- application/port/out/AgentAvailabilityPort.java

Method:
- AgentAvailabilityResult checkAvailability(AgentId or external id abstraction)

Do not expose protobuf types.

Commit: feat(buyer): add AgentAvailability outbound port

## Task 11 — BuyerService gRPC client adapter

Create:
- adapter/out/grpc/GrpcAgentAvailabilityAdapter.java

Responsibilities:
- build protobuf request
- apply deadline
- call stub
- map response
- translate gRPC status to application semantic

Commit: feat(buyer): add AgentAvailability gRPC adapter

## Task 12 — gRPC client configuration

Create/configure:
- AgentGrpcClientProperties.java if custom typed config needed
- GrpcClientConfiguration.java only if library auto-config insufficient

External config:
- logical target/service name
- port
- deadline Duration

Prefer service discovery integration if supported cleanly; otherwise explicit local target for learning environment.

Commit: config(buyer): configure Agent gRPC client

## Task 13 — gRPC deadline

Every call uses explicit deadline.

Do not rely on infinite default.

Example target is configured, not hard-coded in adapter.

Commit can group with client adapter/config.

## Task 14 — gRPC authentication

Day 14 Keycloak foundation reused.

Decide internal call mode:
- propagated user token if user context is needed
- service Client Credentials if machine identity is appropriate

For availability lookup, service identity + required scope is preferred unless business authorization requires end-user context.

Create client interceptor/token supplier only if framework support does not already cover it.

Commit: feat(grpc): secure Agent availability calls

## Task 15 — gRPC tracing/context propagation

Ensure:
- trace context propagated
- correlationId propagated if supported via metadata

Do not build custom tracing stack; use existing Micrometer/OTel-compatible hooks.

Commit if code needed: feat(grpc): propagate tracing context

## Task 16 — Buyer application integration

Add minimal use-case that exercises port only if a current Buyer flow needs it.

Candidate:
- CheckAssignedAgentAvailability

Do not invent full viewing scheduler.

Create:
- application/port/in/CheckAssignedAgentAvailabilityUseCase.java
- corresponding command/query/result only if not already present

Commit: feat(buyer): add agent availability application flow

## Task 17 — gRPC unit tests

Agent side:
- AgentAvailabilityGrpcServiceTest.java

Cases:
- available
- unavailable
- not found mapping
- invalid request mapping

Buyer side:
- GrpcAgentAvailabilityAdapterTest.java

Cases:
- success mapping
- DEADLINE_EXCEEDED translation
- NOT_FOUND translation
- UNAVAILABLE translation

Commit: test(grpc): add adapter unit tests

## Task 18 — gRPC integration test

Create:
- AgentAvailabilityGrpcIntegrationTest.java

Run real in-process or containerized/local server depending test framework.

Verify:
- protobuf serialization
- server/client compatibility
- deadline
- auth metadata if practical

Commit: test(grpc): add availability integration test

## Task 19 — GraphQL dependency setup

Modify SearchService/build.gradle

Add:
- spring-boot-starter-graphql

Do not add GraphQL to all services.

Commit: build(search): add GraphQL support

## Task 20 — GraphQL schema

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

## Task 21 — GraphQL input design

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

## Task 22 — GraphQL resolver/controller

Create:
- graphql/SearchQueryController.java

Use @QueryMapping or supported annotation.

Responsibilities:
- map GraphQL input -> SearchPropertiesQuery
- call existing SearchPropertiesHandler
- map result

No duplicate Elasticsearch query logic.

Commit: feat(search): add GraphQL search resolver

## Task 23 — GraphQL response models

Create only if needed:
- graphql/PropertySearchGraphQlResponse.java
- graphql/SearchPageInfo.java

Can reuse application result if transport coupling remains acceptable only at adapter mapping boundary; prefer transport model for clarity.

Commit can group with resolver.

## Task 24 — GraphQL authorization

Apply Search read role/scope policy from Day 14.

Do not rely on resolver being hidden behind Gateway only.

Commit: feat(search): secure GraphQL search queries

## Task 25 — GraphQL query complexity/depth

Set bounded policy.

Since initial schema is shallow, keep config simple.

Do not add third-party complexity framework unless needed.

At minimum:
- bounded page size
- no recursive schema
- disable/limit expensive unbounded query shapes

Commit: config(search): bound GraphQL query cost

## Task 26 — N+1 review

Initial Search GraphQL should call one search handler returning a batch result, so N+1 should not arise.

Do not add DataLoader unless nested resolver pattern actually creates N+1.

Document decision.

## Task 27 — GraphQL error mapping

Map:
- validation -> GraphQL error with stable extension code
- forbidden -> security error
- downstream unavailable -> stable error extension

Do not expose stack trace/Elasticsearch exception.

Create:
- graphql/GraphQlExceptionResolver.java if needed

Commit: feat(search): standardize GraphQL error mapping

## Task 28 — GraphQL tests

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

## Task 29 — REST parity test

Verify REST and GraphQL call the same application handler and produce semantically consistent results.

Do not duplicate business query logic.

Create optional:
- SearchProtocolParityTest.java

Commit only if useful:
test(search): verify REST and GraphQL query parity

## Task 30 — Contract documentation

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