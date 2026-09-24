# Day 23 — Exact Advanced Resilience Plan

## Scope
- dependency inventory + timeout budget
- retry ownership
- Circuit Breaker
- TimeLimiter only where non-redundant
- Bulkhead
- Redis-backed Gateway Rate Limiting
- truthful fallback/error semantics
- resilience tests/metrics

## Tasks

1. Create `docs/architecture/resilience-matrix.md`.
2. Define connect/read/total budgets for each synchronous dependency.
3. Define exactly one logical retry owner per call path.
4. Verify Resilience4j dependencies only in modules that need them.
5. Decorate Buyer→Agent gRPC outbound port with Circuit Breaker where useful.
6. Keep gRPC deadline primary; add TimeLimiter only if it adds non-duplicative value.
7. Review existing Feign clients for timeout/Circuit Breaker needs.
8. Retry only transient, idempotent-safe calls; no nested retries.
9. Add bounded backoff and jitter where supported.
10. Add semaphore/thread-pool Bulkhead only where capacity isolation is justified.
11. Align bulkhead limits with HTTP/DB/gRPC pool capacity.
12. Reconcile Gateway edge Circuit Breaker/fallback with service-specific ownership.
13. Never return fake-success fallback.
14. Define Redis-backed Gateway rate-limit policy.
15. Add authenticated subject + route/capability key resolver; never raw JWT.
16. Add 429 `RATE_LIMIT_EXCEEDED`; Retry-After if practical.
17. Define route-specific Redis outage fail-open/fail-closed behavior.
18. Translate Resilience4j exceptions to stable technical/application errors.
19. Add Circuit Breaker open/half-open recovery tests.
20. Add retry-bound tests.
21. Add timeout-budget tests.
22. Add Bulkhead saturation/isolation tests.
23. Add Redis rate-limit Testcontainers tests.
24. Add nested retry-storm prevention test.
25. Add low-cardinality metric hooks.
26. Add dependency-outage runbook.
27. Add ArchUnit: domain/controllers do not own Resilience4j policy.

## Commit sequence
1. `docs(resilience): define dependency timeout and retry ownership`
2. `feat(buyer): add Circuit Breaker to Agent availability dependency`
3. `feat(resilience): add bounded retry and bulkhead policies`
4. `refactor(gateway): align edge resilience ownership`
5. `feat(gateway): add Redis-backed rate limiting`
6. `feat(resilience): translate resilience failures`
7. `test(resilience): verify circuit retry timeout and bulkhead behavior`
8. `test(rate-limit): verify distributed rate limiting`
9. `test(resilience): prevent nested retry storms`
10. `docs(resilience): finalize outage runbook and policies`

## Final gate
- retry ownership explicit
- no nested retry storm
- timeout bounded
- Circuit Breaker/Bulkhead work where selected
- TimeLimiter not duplicated unnecessarily
- rate limiting distributed and route-sensitive
- fallback never hides outage as success
- domain layer has no Resilience4j dependency
