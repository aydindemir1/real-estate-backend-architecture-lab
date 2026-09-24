# Day 22 — Exact Test Hardening / Commit Plan

## 0. Scope

Day 22 yeni business feature günü değildir.

Hedef:
- test inventory
- unit/application/integration ayrımı
- Testcontainers standardization
- concurrency/idempotency/security coverage gaps
- deterministic time/random
- source set / tag / Gradle task structure
- flaky test removal
- CI-friendly test lifecycle

Day 22 içinde:
- Spring Cloud Contract yok
- failure-path contract hardening Day 23
- full E2E completion Day 26
- observability stack Day 24

## Task 1 — Test inventory

Create:
- docs/testing/test-inventory.md

For each module record:
- domain unit tests
- application tests
- controller/slice tests
- persistence integration tests
- messaging integration tests
- security tests
- architecture tests
- E2E tests

Mark gaps as:
- Critical
- High
- Medium

Commit: test: inventory current test suites and gaps

## Task 2 — Test taxonomy

Define project-wide categories:
- unit
- application
- slice
- integration
- architecture
- contract
- e2e

Each test should have one primary category.

Commit: docs(test): define test taxonomy

## Task 3 — Gradle test source-set strategy

Decide one consistent approach:

Option A:
- src/test for fast tests
- src/integrationTest for integration

Option B:
- JUnit tags in one source set

Preferred if repo scale justifies it:
- separate integrationTest source set
- architecture tests can remain test tag/task

Do not create excessive source sets per test type.

Commit: build(test): separate integration test lifecycle

## Task 4 — JUnit tags

Standard tags candidate:
- unit
- integration
- architecture
- security
- messaging
- slow

Tags should support CI stage selection.

Commit can group with Gradle lifecycle.

## Task 5 — Fast test task

Define:
- test -> unit/application/slice by default

Must not require Docker.

Goal:
fast local feedback.

## Task 6 — Integration test task

Define:
- integrationTest

Requires Docker/Testcontainers.

Includes:
- datastore
- broker
- security integration

## Task 7 — Architecture test task

Define:
- architectureTest or tagged execution

Includes ArchUnit.

Could be part of normal test if fast enough.

## Task 8 — Check lifecycle

Ensure:
- check depends on test
- check also runs architecture tests
- CI full stage runs integrationTest

Do not make every local compile trigger heavy containers.

Commit: build(test): wire test tasks into verification lifecycle

## Task 9 — Testcontainers version governance

Centralize version if not BOM-managed.

Review modules:
- MySQL
- Couchbase
- Cassandra
- MongoDB
- Elasticsearch
- Redis
- Kafka
- RabbitMQ
- Keycloak/Vault where practical

Commit: build(test): centralize Testcontainers dependency versions

## Task 10 — Reusable container policy

Rule:
- tests independent
- no hidden dependency on previous test data

Container reuse may be enabled locally only if it does not break isolation.

CI must run clean.

Do not rely on singleton global container unless lifecycle is explicit.

## Task 11 — Test data factory convention

Create per-module test factory/support classes.

Examples:
- AgentTestFactory
- BuyerPreferencesTestFactory
- SellerTestFactory
- PropertyTestFactory
- SearchDocumentFactory
- OfferTestFactory

Rule:
Factory provides valid defaults; individual tests override only relevant fields.

Commit: test: standardize test data factories

## Task 12 — Clock standardization

Replace direct Instant.now in domain tests with fixed Clock where business time matters.

Audit:
- Offer expiry
- Property publish
- ListingSubmission timestamps
- outbox/retry timestamps

Commit: test: standardize deterministic Clock usage

## Task 13 — Random/UUID determinism

Where IDs are generated internally and assertions depend on them:
- inject/test generator
- or assert behavior without exact UUID

Do not seed global random unless needed.

## Task 14 — AgentService gap closure

Ensure coverage:
- domain invariants
- duplicate license/user
- MySQL constraints
- optimistic locking if active
- REST validation
- Clean Architecture rules

Add only missing tests.

Commit: test(agent): close critical coverage gaps

## Task 15 — BuyerService gap closure

Ensure:
- Value Object ranges
- defensive copy
- Couchbase key determinism
- saved search round-trip
- ownership security
- Offer/idempotency tests if Day 19 complete
- Hexagonal rules

Commit: test(buyer): close critical coverage gaps

## Task 16 — SellerService gap closure

Ensure:
- Seller state
- ListingSubmission transitions
- Cassandra partition/query behavior
- reliable dispatch if Day 17 complete
- pending offer projection if Day 19 complete
- ownership security
- Onion rules

Commit: test(seller): close critical coverage gaps

## Task 17 — PropertyService gap closure

Ensure:
- aggregate transitions
- Mongo @Version
- listing command idempotency
- outbox
- hold/reserve/release
- concurrent Offer invariant
- Vertical Slice rules

Commit: test(property): close critical coverage gaps

## Task 18 — SearchService gap closure

Ensure:
- mapping
- text/filter/range
- projection idempotency
- stale-event guard
- eventual consistency E2E
- GraphQL authorization
- CQRS rules

Commit: test(search): close critical coverage gaps

## Task 19 — Security matrix test coverage

Create matrix-based test inventory from authorization matrix.

For each protected capability test:
- no token
- wrong role
- wrong scope if relevant
- wrong owner
- admin override if allowed

Do not duplicate identical tests unnecessarily; parameterized tests may help.

Commit: test(security): complete authorization matrix coverage

## Task 20 — Messaging duplicate tests

Audit all active consumers.

Each side-effecting consumer must have:
- first delivery success
- exact duplicate no-op
- duplicate after restart/repository reload

Commit: test(messaging): complete duplicate-delivery coverage

## Task 21 — Messaging retry tests

Ensure:
- retryable -> bounded retry
- non-retryable -> DLT/DLQ
- no infinite requeue

Commit: test(messaging): complete retry-path coverage

## Task 22 — Concurrency test matrix

Create:
- docs/testing/concurrency-test-matrix.md

Scenarios:
- Property publish stale version
- concurrent Offer hold
- duplicate Idempotency-Key
- same key different payload
- Couchbase CAS if enabled
- Cassandra dispatcher duplicate execution

Commit: docs(test): define concurrency test matrix

## Task 23 — Concurrency test implementation

Use barriers/latches/futures only where needed.

Tests must not rely on arbitrary sleep.

Commit: test(concurrency): harden race-condition coverage

## Task 24 — Awaitility policy

Use Awaitility/bounded polling for eventual async conditions.

Define defaults:
- max timeout
- poll interval

Do not use Thread.sleep for async correctness.

Commit: test: standardize async assertions

## Task 25 — Flaky test audit

Identify tests that pass only on rerun.

Typical causes:
- timing
- shared state
- test order
- port collision
- container startup
- clock/random

Fix root cause.

Commit: test: eliminate identified flaky tests

## Task 26 — Parallel test safety

Review if tests can run in parallel.

Disable parallelism selectively for shared external resource tests if needed.

Do not globally disable unless necessary.

## Task 27 — Database cleanup strategy

Per datastore define test isolation:
- relational rollback/truncate
- Mongo collection cleanup
- Couchbase document cleanup
- Cassandra partition/keyspace cleanup
- Elasticsearch index recreate/cleanup
- Redis flush scoped DB/container

Do not let test order matter.

Commit: test(integration): standardize datastore cleanup

## Task 28 — Messaging cleanup strategy

Ensure test topics/queues/groups do not leak state.

Strategies:
- unique test topic suffix
- clean container per suite
- reset offsets carefully

Commit: test(messaging): isolate broker integration tests

## Task 29 — Controller slice test conventions

Rules:
- no real DB
- mock inbound use-case
- test validation/status/error mapping

Do not turn controller tests into full integration tests.

## Task 30 — Application service test conventions

Use fake/mock ports.

No Spring context unless proxy behavior is subject of test.

## Task 31 — Domain test conventions

No Spring.

No datastore.

No network.

Focus:
- invariants
- transitions
- value objects

## Task 32 — Integration test conventions

Use real infrastructure through Testcontainers.

Do not mock database/broker behavior in integration category.

## Task 33 — Architecture tests

Consolidate module ArchUnit rules.

Ensure no duplicates/conflicting rules.

Categories:
- Clean
- Hexagonal
- Onion
- Vertical Slice
- CQRS
- messaging adapters
- security framework leakage

Commit: test(architecture): consolidate architecture fitness tests

## Task 34 — Mutation testing decision

Evaluate PIT or similar for selected domain modules.

Day 22 default:
document as optional future hardening; do not add if build cost disproportionate.

## Task 35 — Coverage metric policy

Do not chase arbitrary line coverage percentage.

Use coverage report only as gap signal.

Critical business rules require explicit scenario coverage.

## Task 36 — JaCoCo baseline

If not present, add JaCoCo report.

Do not set meaningless 90% global gate.

Potential targeted gate later per critical package.

Commit if added: build(test): add JaCoCo reporting

## Task 37 — Test naming convention

Prefer descriptive behavior names.

Examples:
- suspendedAgentCannotBecomeAvailable
- duplicateOfferRequestReturnsSameOffer

Do not use generic test1/test2.

## Task 38 — Given/When/Then style

Use logically in test structure/comments only where helpful.

No mandatory comment noise.

## Task 39 — Assertion library consistency

Use AssertJ as primary where already standard.

Do not mix styles unnecessarily.

## Task 40 — Mocking policy

Mockito only at boundaries.

Do not mock Value Objects/Aggregates.

Avoid verifying every internal method call.

## Task 41 — Testcontainers startup diagnostics

On failure, container logs should be accessible.

Add meaningful wait strategies.

Do not use overly long blind timeouts.

## Task 42 — CI stage design

Document stages:
1. compile/static
2. unit/application/slice
3. architecture
4. integration/Testcontainers
5. later contract/failure
6. later E2E

Day 22 only prepares Gradle tasks; Jenkins itself comes later.

Commit: docs(test): define CI test stages

## Task 43 — Test report artifacts

Ensure Gradle emits JUnit XML/HTML reports.

Integration task separate reports.

## Task 44 — Failure reproducibility

Document command to run one failing test/class.

Examples:
- ./gradlew :PropertyService:test --tests ...
- ./gradlew :PropertyService:integrationTest --tests ...

Commit: docs(test): document targeted test execution

## Task 45 — Local developer test profile

Use test profile only for test-specific config.

No shared persistent local DB dependency.

## Task 46 — Secret handling in tests

Use fake local credentials/Testcontainers defaults.

Do not require real Vault secrets for ordinary unit/integration tests unless testing Vault itself.

## Task 47 — Performance of test suite

Measure approximate stage duration.

If integration tests too slow:
- group by module
- reuse container within class/suite carefully
- avoid restarting infra per method

Correctness before speed.

## Task 48 — Test quarantine policy

No permanent @Disabled for flaky test without issue/roadmap note.

Quarantine temporary and visible.

## Task 49 — Documentation reconciliation

Create/update:
- docs/testing/README.md
- docs/testing/test-inventory.md
- docs/testing/concurrency-test-matrix.md
- docs/roadmap/day-22-testing.md

Record:
- categories
- Gradle tasks
- container policy
- deterministic time policy
- async assertion policy
- CI stages

Commit: docs(test): finalize testing hardening standard

## Recommended Commit Sequence

1. test: inventory current test suites and gaps
2. docs(test): define test taxonomy
3. build(test): separate integration test lifecycle
4. build(test): wire test tasks into verification lifecycle
5. build(test): centralize Testcontainers dependency versions
6. test: standardize test data factories
7. test: standardize deterministic Clock usage
8. test(agent): close critical coverage gaps
9. test(buyer): close critical coverage gaps
10. test(seller): close critical coverage gaps
11. test(property): close critical coverage gaps
12. test(search): close critical coverage gaps
13. test(security): complete authorization matrix coverage
14. test(messaging): complete duplicate-delivery coverage
15. test(messaging): complete retry-path coverage
16. docs(test): define concurrency test matrix
17. test(concurrency): harden race-condition coverage
18. test: standardize async assertions
19. test: eliminate identified flaky tests
20. test(integration): standardize datastore cleanup
21. test(messaging): isolate broker integration tests
22. test(architecture): consolidate architecture fitness tests
23. build(test): add JaCoCo reporting — if selected
24. docs(test): define CI test stages
25. docs(test): document targeted test execution
26. docs(test): finalize testing hardening standard

Adjacent coverage-gap commits can be grouped per service. Build/test-infrastructure changes should remain separate from business test additions.

## Explicitly Deferred from Day 22

Do not implement:
- Spring Cloud Contract
- full failure-path matrix from Day 23
- final E2E suite from Day 26
- Jenkins pipeline
- mutation testing unless explicitly selected
- performance/load test campaign

## Critical Design Note — Test Pyramid

Most business rules should be verified without Spring or Docker.

Integration tests exist for real infrastructure semantics, not as a replacement for unit tests.

## Critical Design Note — Determinism

Tests should not depend on clock time, random order, previous test state or arbitrary sleep.

## Day 22 Final Gate

Day 22 closes only if:
- test inventory exists
- unit/integration/architecture categories are explicit
- Gradle has clear fast vs integration lifecycle
- Testcontainers versions are governed
- critical domain/application gaps are closed
- datastore integrations use real containers
- messaging duplicate/retry tests exist
- security matrix coverage is substantially complete
- concurrency race scenarios are automated
- async tests use bounded polling, not arbitrary sleeps
- known flaky tests are fixed or visibly quarantined
- datastore/broker test isolation is deterministic
- architecture tests are consolidated
- CI-ready test stages are documented
- no Contract/E2E/Jenkins scope leaks into Day 22
- docs match actual test structure