# Day 23 — Exact Contract / Failure-Path / Commit Plan

## 0. Scope

Day 23 yalnızca contract verification ve failure-path hardening içindir.

Hedef:
- Spring Cloud Contract
- critical REST provider contracts
- generated stubs
- consumer verification
- Kafka event compatibility checks
- gRPC protobuf compatibility checks
- dependency outage tests
- poison message tests
- DLT/DLQ replay-safety
- invalid config startup tests

Day 23 içinde:
- final E2E completion yok
- observability stack yok
- chaos platform yok
- load/performance campaign yok

## Task 1 — Contract inventory

Create:
- docs/contracts/contract-test-inventory.md

List critical cross-service contracts:
- Auth/UserProfile if still active
- Buyer -> Agent gRPC
- Seller -> Property RabbitMQ command
- Property -> Search Kafka events
- Gateway -> downstream REST shape only where consumer/provider risk exists

Prioritize contracts that cross service boundaries.

Commit: docs(contract): inventory critical service contracts

## Task 2 — Spring Cloud Contract scope

Use Spring Cloud Contract primarily for REST provider/consumer verification.

Do not force it onto Kafka/gRPC if native schema/proto checks are clearer.

Document contract technology ownership.

Commit: docs(contract): define contract verification strategy

## Task 3 — Build setup

Add Spring Cloud Contract plugin/dependencies to selected provider modules only.

Potential providers:
- AgentService
- SellerService
- PropertyService
- SearchService only if another service consumes REST directly

Do not add plugin globally to every module.

Commit: build(contract): add Spring Cloud Contract to selected providers

## Task 4 — Contract directory convention

Use standard provider contract location.

Example:
- src/contractTest/resources/contracts/
or plugin-standard default path.

Organize by capability, not endpoint dump.

Example:
- agent/availability/
- property/read/

Commit can group with build setup.

## Task 5 — Agent REST contract

If Agent REST endpoint is consumed by external/internal client and worth protecting, define contract:
- GET /agents/{id}
- PATCH availability only if consumer dependency exists

Validate:
- request
- status
- response shape
- stable error

Commit: test(contract): add Agent provider contracts

## Task 6 — Buyer REST contract

Add only if there is a real cross-service consumer.

Do not contract-test every public endpoint just because tool exists.

## Task 7 — Seller REST contract

Potential critical contract:
- seller pending offers / decision endpoints if another component integrates directly

Skip if only external client/UI consumes it.

## Task 8 — Property REST contract

Potential critical contract:
- GET Property
- publish response if externally consumed

Provider contract protects stable response/error shape.

Commit: test(contract): add Property provider contracts

## Task 9 — Provider verification tests

For each provider contract:
- generated test executes against controller/application boundary
- deterministic fixture/stub use
- no live remote service dependency

Commit: test(contract): verify provider implementations

## Task 10 — Stub publishing/build artifact

Generate stubs as build artifact.

Do not publish to external repository yet unless Nexus phase requires it later.

Local Gradle dependency/testing use is enough.

Commit: build(contract): generate provider stub artifacts

## Task 11 — Consumer stub tests

Where a service uses REST client against provider:
- use generated stub
- verify client deserialization/error handling

Example candidate:
- existing OpenFeign integration if still present

Commit: test(contract): verify REST consumers against generated stubs

## Task 12 — Contract backward compatibility rule

Breaking examples:
- remove field
- rename field
- change type
- change status/error code unexpectedly

Non-breaking examples:
- additive optional field
- new endpoint

Document provider evolution rules.

Commit: docs(contract): define backward-compatible REST evolution

## Task 13 — Kafka event compatibility inventory

List schemas:
- PropertyPublished
- PropertyUpdated
- PropertyPriceChanged
- PropertyWithdrawn
- PropertySold
- OfferRequested
- PropertyHeld
- SellerAccepted
- SellerRejected
- PropertyReserved
- PropertyHoldReleased

Each has schemaVersion.

Commit: docs(contract): inventory event schemas

## Task 14 — Event serialization compatibility tests

Create tests that deserialize current + previous known payload fixtures.

Files candidate:
- src/test/resources/events/v1/*.json

Verify additive optional field compatibility.

Commit: test(contract): add Kafka event compatibility fixtures

## Task 15 — Event required-field policy

Do not make previously optional field required without version bump/migration.

Test unsupported schemaVersion -> non-retryable/DLT path.

Commit: test(contract): verify event schema-version handling

## Task 16 — Protobuf compatibility checks

Use proto source history/current descriptor comparison if tooling available.

Rules:
- field numbers never reused
- removed field numbers reserved
- additive fields preferred
- service/method breaking change explicit

Commit: test(contract): add gRPC protobuf compatibility checks

## Task 17 — gRPC consumer contract test

BuyerService client against AgentService stub/in-process server.

Verify:
- request field mapping
- response mapping
- NOT_FOUND
- DEADLINE_EXCEEDED
- PERMISSION_DENIED

Commit: test(contract): verify Agent availability gRPC contract

## Task 18 — Failure-path matrix

Create:
- docs/testing/failure-path-matrix.md

Scenarios:
- DB unavailable
- Redis unavailable
- Kafka unavailable
- RabbitMQ unavailable
- Config Server unavailable
- Vault unavailable
- downstream HTTP 503
- gRPC timeout
- Elasticsearch unavailable
- poison message
- malformed event
- invalid startup config

Map expected behavior.

Commit: docs(test): define failure-path matrix

## Task 19 — Database unavailable tests

Representative per persistence style:
- relational/MySQL
- MongoDB
- Cassandra
- Couchbase
- Elasticsearch

Do not need every endpoint for every DB.

Verify:
- bounded failure
- stable application error
- no raw driver exception leak

Commit: test(failure): add datastore outage scenarios

## Task 20 — Redis unavailable tests

Cover active Redis usages:
- Offer idempotency
- Gateway rate limit

Verify configured fail-open/fail-closed semantics.

Commit: test(failure): verify Redis outage policies

## Task 21 — Kafka unavailable test

Producer path:
- outbox remains pending
- business local commit semantics remain correct

Consumer path:
- no message loss claim

Commit: test(failure): verify Kafka outage behavior

## Task 22 — RabbitMQ unavailable test

Seller reliable dispatch:
- pending command remains
- dispatcher retries later

Business submission local state not silently lost.

Commit: test(failure): verify RabbitMQ outage behavior

## Task 23 — Config Server unavailable test

Startup behavior:
- critical config unavailable -> expected fail-fast/bounded behavior

Runtime:
- already running service retains current config.

Commit: test(failure): verify Config Server outage semantics

## Task 24 — Vault unavailable test

Reuse Day 21 semantics.

Verify startup fail-fast for critical secret.

Commit: test(failure): verify Vault startup failure behavior

## Task 25 — Downstream HTTP 503 test

Feign client path:
- bounded timeout
- circuit/retry policy
- semantic 503/translated error

Commit: test(failure): verify HTTP dependency failure behavior

## Task 26 — gRPC timeout test

Simulate slow AgentService.

Verify:
- deadline exceeded within budget
- no nested retry storm
- translated application error

Commit: test(failure): verify gRPC timeout behavior

## Task 27 — Elasticsearch unavailable test

Search endpoint:
- explicit 503/degraded semantic
- no fake empty success

Projection consumer:
- retry/DLT according to policy

Commit: test(failure): verify Elasticsearch outage behavior

## Task 28 — Poison message test

Kafka malformed/unsupported event:
- bounded handling
- DLT
- partition continues

RabbitMQ malformed command:
- DLQ
- no infinite requeue

Commit: test(failure): add poison-message scenarios

## Task 29 — DLT replay-safety test

Procedure under test:
1. fail message
2. lands in DLT
3. fix dependency/data condition
4. replay
5. idempotent handler processes once

Commit: test(failure): verify Kafka DLT replay safety

## Task 30 — DLQ replay-safety test

RabbitMQ equivalent.

Verify duplicate-safe command consumer.

Commit: test(failure): verify RabbitMQ DLQ replay safety

## Task 31 — Same message ID different payload test

Where payload-hash conflict policy exists:
- same id + different payload -> quarantine/non-retryable

Verify does not mutate business state.

Commit: test(failure): verify message identity conflict handling

## Task 32 — Invalid config startup tests

Representative:
- missing DB URL
- missing secret
- invalid timeout Duration
- unsupported enum/property

Expected:
- startup fails clearly and early

Commit: test(config): verify fail-fast configuration validation

## Task 33 — Migration failure test

For relational migration:
- invalid migration/checksum mismatch candidate

Verify startup blocks with explicit migration error.

Do not auto-repair silently.

Commit: test(failure): verify schema migration failure behavior

## Task 34 — Resilience regression tests

Ensure failure tests also assert:
- no retry storm
- no unbounded wait
- circuit opens where designed

Reuse Day 20 test utilities.

## Task 35 — Error contract stability tests

For critical failures verify stable error codes/status:
- 401
- 403
- 409
- 429
- 503
- 504

Commit: test(contract): verify stable API error contracts

## Task 36 — Failure test tagging

Tag:
- failure
- contract

Keep heavy scenarios separate from fast unit tests.

Update Gradle tasks if needed.

Commit: build(test): add contract and failure test tasks

## Task 37 — Test report separation

Generate separate reports for:
- contractTest
- failureTest

or tagged equivalent.

## Task 38 — Runbook linkage

Each failure-path category should link to corresponding runbook.

Examples:
- Kafka outage -> messaging runbook
- Vault -> vault-outage
- dependency timeout -> dependency-outage

Commit: docs(runbook): link failure tests to operational procedures

## Task 39 — Contract CI stage design

Document CI stage order:
1. unit/application
2. architecture
3. integration
4. contract
5. failure-path
6. later E2E

No Jenkins implementation yet.

Commit: docs(test): define contract and failure CI stages

## Task 40 — Documentation reconciliation

Modify:
- docs/contracts/README.md or create if missing
- docs/contracts/contract-test-inventory.md
- docs/testing/failure-path-matrix.md
- docs/roadmap/day-23-contract-failure-testing.md
- relevant service DESIGN docs if contract ownership changes

Commit: docs(contract): finalize contract and failure-path verification

## Recommended Commit Sequence

1. docs(contract): inventory critical service contracts
2. docs(contract): define contract verification strategy
3. build(contract): add Spring Cloud Contract to selected providers
4. test(contract): add Agent provider contracts — if selected
5. test(contract): add Property provider contracts — if selected
6. test(contract): verify provider implementations
7. build(contract): generate provider stub artifacts
8. test(contract): verify REST consumers against generated stubs
9. docs(contract): define backward-compatible REST evolution
10. docs(contract): inventory event schemas
11. test(contract): add Kafka event compatibility fixtures
12. test(contract): verify event schema-version handling
13. test(contract): add gRPC protobuf compatibility checks
14. test(contract): verify Agent availability gRPC contract
15. docs(test): define failure-path matrix
16. test(failure): add datastore outage scenarios
17. test(failure): verify Redis outage policies
18. test(failure): verify Kafka outage behavior
19. test(failure): verify RabbitMQ outage behavior
20. test(failure): verify Config Server outage semantics
21. test(failure): verify Vault startup failure behavior
22. test(failure): verify HTTP dependency failure behavior
23. test(failure): verify gRPC timeout behavior
24. test(failure): verify Elasticsearch outage behavior
25. test(failure): add poison-message scenarios
26. test(failure): verify Kafka DLT replay safety
27. test(failure): verify RabbitMQ DLQ replay safety
28. test(failure): verify message identity conflict handling
29. test(config): verify fail-fast configuration validation
30. test(failure): verify schema migration failure behavior
31. test(contract): verify stable API error contracts
32. build(test): add contract and failure test tasks
33. docs(runbook): link failure tests to operational procedures
34. docs(test): define contract and failure CI stages
35. docs(contract): finalize contract and failure-path verification

Small related contract commits can be grouped by provider. Failure tests should remain grouped by failure domain rather than one enormous test commit.

## Explicitly Deferred from Day 23

Do not implement:
- final end-to-end product suite
- load/performance test campaign
- chaos engineering platform
- Grafana alerting
- Jenkins pipeline
- consumer-driven contracts for every internal implementation detail

## Critical Design Note — Contract Testing

Contract testing protects service boundaries, not internal class structures.

Do not contract-test every DTO or endpoint indiscriminately.

## Critical Design Note — Failure Tests

Failure-path tests must assert both the error and the absence of harmful side effects.

## Critical Design Note — Replay

Replay is safe only after root cause is fixed and idempotency is verified.

## Day 23 Final Gate

Day 23 closes only if:
- critical REST provider contracts are selected and verified
- generated stubs validate real consumers where applicable
- REST backward compatibility rules are documented
- Kafka event fixtures verify schema compatibility
- unsupported event schema versions follow safe failure path
- protobuf compatibility rules are automated/documented
- DB/Redis/Kafka/RabbitMQ/Config/Vault/downstream outage behaviors are tested
- poison messages reach DLT/DLQ without blocking indefinitely
- replay-safety tests pass
- invalid critical config fails fast
- stable API error codes/status are protected
- contract/failure tests have separate CI-ready execution path
- runbooks are linked to failure scenarios
- no final E2E/load/chaos/observability scope leaks into Day 23
- docs match actual verification strategy