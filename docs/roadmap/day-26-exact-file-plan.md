# Day 26 — Exact Testing Hardening Plan

## Scope
- test inventory/taxonomy
- fast vs integration lifecycle
- Testcontainers governance
- coverage-gap closure
- security/messaging/concurrency hardening
- deterministic/flaky-test policy
- CI-ready Gradle tasks

## Tasks
1. Create `docs/testing/test-inventory.md` by module and test type.
2. Define categories: unit, application, slice, integration, architecture, contract, e2e.
3. Choose separate `integrationTest` source set/task or equivalent JUnit-tag lifecycle.
4. Keep `test` Docker-free and fast.
5. Wire architecture tests into verification if fast.
6. Centralize Testcontainers version governance.
7. Standardize per-module valid-default test factories.
8. Replace time-sensitive `Instant.now()` behavior with injected/fixed `Clock`.
9. Close critical Agent/Buyer/Seller/Property/Search coverage gaps.
10. Complete authorization matrix test coverage.
11. Ensure every side-effecting consumer has duplicate-delivery tests.
12. Ensure retryable/non-retryable messaging paths are tested.
13. Create `docs/testing/concurrency-test-matrix.md`.
14. Automate Property optimistic locking, concurrent Offer hold, Idempotency-Key, dispatcher duplicate scenarios.
15. Standardize Awaitility/bounded polling; remove arbitrary `Thread.sleep`.
16. Audit/fix flaky tests: timing, shared state, order, port collision, container startup, clock/random.
17. Standardize datastore cleanup per technology.
18. Isolate Kafka/RabbitMQ integration test state.
19. Consolidate ArchUnit tests without hiding service-specific rules.
20. Add JaCoCo as gap signal if useful; no arbitrary global coverage vanity target.
21. Document CI stages and targeted Gradle commands.
22. Keep real Vault secrets out of ordinary tests.

## Commit sequence
1. `test: inventory current suites and gaps`
2. `build(test): separate integration test lifecycle`
3. `build(test): centralize Testcontainers versions`
4. `test: standardize factories clock and async assertions`
5. `test(<service>): close critical coverage gaps`
6. `test(security): complete authorization matrix coverage`
7. `test(messaging): complete duplicate and retry coverage`
8. `test(concurrency): harden race-condition coverage`
9. `test: eliminate flaky tests`
10. `test(integration): standardize infrastructure cleanup`
11. `test(architecture): consolidate fitness suites`
12. `docs(test): define CI test stages and commands`

## Final gate
- fast tests need no Docker
- integration tests use real infrastructure
- concurrency/idempotency/security critical paths automated
- async tests deterministic
- known flaky tests fixed or explicitly visible
- CI-ready test tasks documented
