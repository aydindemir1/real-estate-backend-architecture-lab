# Day 20 — Exact File / Resilience / Commit Plan

## 0. Scope

Day 20 yalnızca advanced resilience hardening içindir.

Hedef:
- timeout budget
- retry ownership
- Circuit Breaker
- TimeLimiter
- Bulkhead
- Redis-backed Rate Limiter
- fallback semantics
- retry storm prevention
- saturation tests
- resilience metrics baseline

Day 20 içinde:
- service mesh yok
- chaos platform yok
- global SLO dashboard yok
- Vault yok
- Kubernetes policy yok

## Task 1 — Dependency inventory

Create:
- docs/architecture/resilience-matrix.md

List synchronous dependencies:
- Gateway -> services
- BuyerService -> AgentService gRPC
- existing OpenFeign calls
- Config Server startup dependency
- Eureka discovery
- datastore clients where app-level timeout applies

Async dependencies:
- Kafka
- RabbitMQ

Classify each:
- critical
- degradable
- optional

Commit: docs(resilience): inventory dependency failure boundaries

## Task 2 — Timeout budget matrix

For each sync dependency define:
- connect timeout
- read/call timeout
- total request budget
- caller timeout must be less than upstream/client budget

Do not invent arbitrary numbers without local baseline.

Use conservative initial values as configuration and mark benchmark-tunable.

Example relationship:
client budget > Gateway budget > service downstream budget

Commit: docs(resilience): define timeout budget ownership

## Task 3 — Retry ownership matrix

One logical retry owner per call path.

Review:
- Gateway retry
- Feign retry
- gRPC retry
- Resilience4j retry

Rule:
Do not stack retries at every layer.

Write explicit owner for each dependency.

Commit: docs(resilience): define retry ownership

## Task 4 — Resilience4j config foundation

Add/verify dependencies:
- spring-cloud-starter-circuitbreaker-resilience4j

Create typed config only if useful.

External config groups:
- circuitbreaker
- retry
- timelimiter
- bulkhead
- ratelimiter

Commit: build(resilience): add required Resilience4j support

## Task 5 — Agent gRPC Circuit Breaker

BuyerService -> AgentService availability call is a strong candidate.

Create decorator/application adapter around AgentAvailabilityPort.

Preferred structure:
- ResilientAgentAvailabilityAdapter
or
- resilience decorator around outbound port

Do not embed annotations everywhere.

Commit: feat(buyer): add Circuit Breaker to Agent availability dependency

## Task 6 — Agent gRPC TimeLimiter

Ensure explicit gRPC deadline already exists from Day 15.

TimeLimiter should not conflict with gRPC deadline.

Decide one effective timeout owner:
- gRPC deadline primary
- TimeLimiter only if wrapping async Future-based adapter adds value

If redundant, document and do not add unnecessary TimeLimiter.

Important:
Use each resilience primitive only where semantically useful.

## Task 7 — OpenFeign Circuit Breaker review

For each existing Feign client:
- identify dependency
- add Circuit Breaker only if partial failure risk is meaningful
- configure timeout at HTTP client level

Do not create global one-size-fits-all circuit.

Commit per service if applicable:
feat(<service>): add dependency-specific circuit breaker

## Task 8 — Retry policy

Retry only transient, idempotent-safe operations.

Safe candidates:
- GET/read calls
- selected gRPC read

Unsafe by default:
- POST create
- state-changing write without idempotency

Configure:
- max attempts
- backoff
- jitter if supported

Commit: feat(resilience): add bounded retry policies

## Task 9 — Retry storm prevention

Rules:
- no nested retries
- bounded attempts
- backoff
- circuit opens before saturation cascades

Test combined behavior.

## Task 10 — Bulkhead strategy

Identify risky dependencies:
- slow gRPC
- external HTTP
- expensive search candidate

Choose:
- semaphore bulkhead for simple sync isolation
- thread-pool bulkhead only if justified

Do not create executor explosion.

Commit: feat(resilience): add dependency bulkheads

## Task 11 — Connection pool saturation review

Resilience is not only Resilience4j.

Review:
- HTTP connection pools
- DB pools
- gRPC channels

Ensure bulkhead limits align with downstream pool capacity.

Document if connection pool is actual bottleneck.

## Task 12 — Gateway resilience review

Gateway responsibilities:
- coarse edge timeout
- optional coarse Circuit Breaker
- rate limit

Gateway must not duplicate service-specific retry policy.

Review existing Circuit Breaker/fallback from Day 5.

Commit: refactor(gateway): align edge resilience ownership

## Task 13 — Fallback semantics

Fallback allowed only if correctness preserved.

Examples:
- Search unavailable -> 503, not fake empty list if that hides outage
- Agent availability unavailable -> explicit unavailable/degraded result only if business semantics allow

Never return fake success.

Commit: feat(resilience): standardize fallback semantics

## Task 14 — Redis-backed Rate Limiter design

Use Redis from Day 13.

Scope:
- Gateway edge rate limiting

Define key:
- authenticated subject + route/capability + time window

Anonymous routes if any use IP carefully only where appropriate.

Do not use raw token as key.

Commit: docs(rate-limit): define distributed rate limit policy

## Task 15 — Gateway Redis RateLimiter

Use Spring Cloud Gateway Redis RateLimiter if it fits architecture.

Configure per route:
- replenish rate
- burst capacity
- requested tokens

Start with conservative local values and mark benchmark-tunable.

Commit: feat(gateway): add Redis-backed rate limiting

## Task 16 — Rate limit key resolver

Create:
- SecuritySubjectKeyResolver.java

Use authenticated subject where available.

Fallback for unauthenticated endpoint only if such endpoint exists.

Do not key by role only.

Commit: feat(gateway): add authenticated rate-limit key resolver

## Task 17 — 429 error contract

Standardize:
- HTTP 429
- stable code RATE_LIMIT_EXCEEDED
- Retry-After header if practical

Commit: feat(gateway): standardize rate-limit responses

## Task 18 — Rate limiter failure policy

Redis unavailable decision:
- critical write endpoint -> fail-closed candidate
- general read/search -> fail-open candidate only if abuse risk acceptable

Do not use one global answer.

Document route sensitivity.

Commit: docs(rate-limit): define Redis outage behavior

## Task 19 — Circuit Breaker configuration per dependency

Config fields:
- failure rate threshold
- slow call threshold
- sliding window
- minimum calls
- open-state duration
- half-open permitted calls

Do not copy same numbers blindly to every dependency.

## Task 20 — Retry configuration per dependency

Config:
- max attempts
- wait duration
- exceptions allowed
- exceptions ignored

Business validation/conflict must not retry.

## Task 21 — Bulkhead configuration

Config:
- max concurrent calls
- max wait duration

Align with expected downstream capacity.

## Task 22 — TimeLimiter configuration

Only for dependencies where call abstraction benefits from it.

Do not layer TimeLimiter on top of an already stricter explicit deadline without reason.

## Task 23 — Resilience decorators vs annotations

Preferred:
- explicit decorator/adaptor for outbound ports

Use annotation style only when it keeps ownership clear.

Avoid business services covered in multiple invisible annotations.

## Task 24 — Failure classification

Create stable technical exception mapping:
- DownstreamTimeoutException
- DownstreamUnavailableException
- CircuitOpenException or semantic equivalent

Do not leak CallNotPermittedException/TimeoutException directly to API.

Commit: feat(resilience): translate resilience failures

## Task 25 — API error mapping

Map:
- timeout -> 504 when acting as gateway/proxy semantics
- dependency unavailable/circuit open -> 503
- rate limit -> 429

Internal service may map timeout to 503/504 based on boundary semantics.

Keep project error model consistent.

Commit: feat(resilience): map resilience failures to API errors

## Task 26 — Circuit Breaker unit tests

Create tests around decorated adapter.

Cases:
- failures open circuit
- open circuit rejects quickly
- half-open recovery

Use deterministic config in tests.

Commit: test(resilience): verify Circuit Breaker behavior

## Task 27 — Retry tests

Cases:
- transient failure then success
- permanent failure not retried
- bounded attempt count
- write call without idempotency not retried

Commit: test(resilience): verify retry ownership and bounds

## Task 28 — Timeout tests

Cases:
- slow dependency exceeds budget
- request fails within bounded time
- worker/thread pool does not remain blocked indefinitely

Commit: test(resilience): verify timeout budgets

## Task 29 — Bulkhead saturation tests

Scenario:
- many concurrent slow calls
- bulkhead caps active calls
- excess calls fail fast/bounded

Verify unrelated endpoint remains responsive where practical.

Commit: test(resilience): verify bulkhead isolation

## Task 30 — Rate limit integration tests

Use Redis Testcontainers.

Cases:
- under limit allowed
- burst handled
- over limit -> 429
- different subjects isolated
- Redis outage follows configured fail-open/fail-closed route policy

Commit: test(rate-limit): verify distributed rate limiting

## Task 31 — Retry storm integration test

Construct failure chain where downstream is unavailable.

Verify total attempts remain within expected bound across Gateway + service.

This test protects against accidental nested retries.

Commit: test(resilience): prevent nested retry storms

## Task 32 — Circuit + retry ordering test

Verify selected operator order.

Document whether retry occurs inside or outside circuit measurement.

Choose consciously.

Commit can group with resilience integration tests.

## Task 33 — Resilience metrics

Micrometer metrics candidate:
- circuit state
- call success/failure
- retry count
- bulkhead rejected
- rate-limit rejected

Use low-cardinality labels:
- service/dependency name

Never label by user/property/request ID.

Commit: feat(observability): expose resilience metrics

## Task 34 — Logging

Log state transitions at appropriate levels:
- circuit open/half-open
- retry exhausted
- bulkhead reject

Do not log every successful call.

## Task 35 — Architecture tests

Rules:
- resilience code wraps outbound adapters/ports
- domain layer has no Resilience4j dependency
- controllers do not configure retry logic
- Gateway rate limiting remains edge concern

Commit: test(resilience): enforce resilience boundaries

## Task 36 — Operational runbook updates

Modify:
- docs/standards/operational-readiness-runbook.md only if implementation-specific clarification is needed

Create/update:
- docs/runbooks/dependency-outage.md

Include:
- circuit open
- timeout spike
- bulkhead saturation
- Redis rate-limit outage
- what to inspect

Commit: docs(resilience): add dependency outage runbook

## Task 37 — Documentation

Modify:
- docs/architecture/resilience-matrix.md
- docs/roadmap/day-20-resilience.md
- Gateway DESIGN
- BuyerService DESIGN for gRPC resilience
- affected service DESIGN docs

Record actual:
- timeout values
- retry owners
- circuit policies
- bulkhead limits
- rate-limit policies
- fail-open/fail-closed decisions

Commit: docs(resilience): finalize advanced resilience policies

## Recommended Commit Sequence

1. docs(resilience): inventory dependency failure boundaries
2. docs(resilience): define timeout budget ownership
3. docs(resilience): define retry ownership
4. build(resilience): add required Resilience4j support
5. feat(buyer): add Circuit Breaker to Agent availability dependency
6. feat(resilience): add bounded retry policies
7. feat(resilience): add dependency bulkheads
8. refactor(gateway): align edge resilience ownership
9. feat(resilience): standardize fallback semantics
10. docs(rate-limit): define distributed rate limit policy
11. feat(gateway): add Redis-backed rate limiting
12. feat(gateway): add authenticated rate-limit key resolver
13. feat(gateway): standardize rate-limit responses
14. docs(rate-limit): define Redis outage behavior
15. feat(resilience): translate resilience failures
16. feat(resilience): map resilience failures to API errors
17. test(resilience): verify Circuit Breaker behavior
18. test(resilience): verify retry ownership and bounds
19. test(resilience): verify timeout budgets
20. test(resilience): verify bulkhead isolation
21. test(rate-limit): verify distributed rate limiting
22. test(resilience): prevent nested retry storms
23. feat(observability): expose resilience metrics
24. test(resilience): enforce resilience boundaries
25. docs(resilience): add dependency outage runbook
26. docs(resilience): finalize advanced resilience policies

Small adjacent config/test commits can be merged if cohesive. Do not combine all resilience primitives into one huge commit.

## Explicitly Deferred from Day 20

Do not implement:
- service mesh resilience
- Kubernetes HPA
- chaos engineering platform
- adaptive concurrency
- production SLO alerting
- Vault
- mTLS

## Critical Design Note — Retry Ownership

One logical retry owner per dependency path.

Gateway + Feign + gRPC + Resilience4j must not all retry the same failed request.

## Critical Design Note — Rate Limiting

Rate limiting is distributed edge policy, backed by Redis.

Redis outage behavior is route-sensitive, not globally fail-open/fail-closed.

## Critical Design Note — Fallback

Fallback must preserve truthfulness.

Fake success is worse than explicit degradation.

## Day 20 Final Gate

Day 20 closes only if:
- dependency inventory exists
- timeout budgets are explicit
- retry owner is explicit per path
- no nested retry storm exists
- Circuit Breaker works on selected dependencies
- TimeLimiter is used only where non-redundant
- Bulkhead isolates slow dependencies
- Gateway coarse resilience is aligned with service-level policy
- Redis-backed RateLimiter works
- 429 contract works
- Redis rate-limit outage behavior is documented/tested
- fallbacks do not fake success
- resilience failures map to stable API semantics
- circuit/retry/timeout/bulkhead/rate-limit tests pass
- resilience metrics are low-cardinality
- domain layer has no Resilience4j dependency
- no service-mesh/Kubernetes/Vault scope leaks into Day 20
- docs match actual implementation