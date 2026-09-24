# Day 15 — Exact gRPC Internal Communication Plan

## Scope

- AgentAvailability protobuf contract
- AgentService gRPC server adapter
- BuyerService outbound port and client adapter
- deadline/auth/status mapping
- unit and integration tests

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

## Source-of-truth note

This file follows the final Day 15–33 roadmap. Earlier combined Day numbering is superseded by `docs/roadmap/LEGACY-DAY-MAPPING.md`.
