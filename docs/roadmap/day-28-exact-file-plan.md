# Day 28 — Exact Failure-Path Testing Plan

## Scope
- dependency outage matrix
- datastore/broker/config/Vault failures
- HTTP/gRPC failures
- poison messages
- DLT/DLQ replay safety
- invalid config/migration failure
- stable negative API behavior

## Tasks
1. Create `docs/testing/failure-path-matrix.md`.
2. Add representative MySQL/Mongo/Cassandra/Couchbase/Elasticsearch outage tests.
3. Verify raw driver errors never leak.
4. Test Redis outage for Offer-idempotency accelerator and Gateway rate-limit policies.
5. Test Kafka outage: local transaction/outbox remains durable and pending.
6. Test RabbitMQ outage: Seller pending outbound message remains durable and later succeeds.
7. Test Config Server startup/runtime semantics.
8. Test Vault critical-secret startup fail-fast.
9. Test downstream HTTP 503 through timeout/circuit/retry mapping.
10. Test slow Agent gRPC deadline and absence of nested retries.
11. Test Elasticsearch outage: Search returns explicit unavailable semantics, never fake empty success.
12. Test Kafka malformed/unsupported event → DLT and partition continues.
13. Test RabbitMQ malformed command → DLQ and no hot loop.
14. Test Kafka DLT replay after root-cause fix.
15. Test RabbitMQ DLQ replay and duplicate-safe command consumer.
16. Test same message id + different payload conflict policy where enabled.
17. Test invalid critical config fails early.
18. Test relational migration/checksum failure blocks startup.
19. Assert negative scenarios also have no harmful side effects.
20. Create separate `failureTest` execution/report and link runbooks.

## Commit sequence
1. `docs(test): define failure-path matrix`
2. `test(failure): add datastore outage scenarios`
3. `test(failure): verify Redis Kafka and RabbitMQ outage behavior`
4. `test(failure): verify Config Vault HTTP and gRPC failures`
5. `test(failure): verify Elasticsearch outage`
6. `test(failure): add poison-message scenarios`
7. `test(failure): verify DLT and DLQ replay safety`
8. `test(config): verify fail-fast configuration and migration failures`
9. `build(test): add failure test task and report`

## Final gate
- all key failure domains have an explicit expected behavior
- outage tests are bounded
- poison messages cannot block indefinitely
- replay is safe
- failed scenarios do not create hidden harmful side effects
