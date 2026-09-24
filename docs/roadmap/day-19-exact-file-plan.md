# Day 19 — Exact Retry + DLT/DLQ + Replay Safety Plan

## Scope
- retryable/non-retryable classification
- Kafka bounded retry + DLT
- RabbitMQ DLX/DLQ
- poison-message handling
- replay metadata/runbook
- replay-safety tests

## Tasks

1. Define `RetryableMessagingException` and `NonRetryableMessagingException`.
2. Classify transient network/timeout/unavailable as retryable; malformed payload, unsupported schema and permanent validation as non-retryable.
3. Configure Kafka retry policy with bounded attempts/backoff.
4. Add `property.events.retry`, `property.events.dlt`, `offer.events.retry`, `offer.events.dlt` only where active.
5. Preserve original topic, message/event id, correlationId, type, attempt count and failure metadata.
6. Configure deserialization/poison-message handling so a bad record cannot block a partition indefinitely.
7. Configure RabbitMQ DLX/DLQ for listing commands:
   - `real-estate.listing.dlx`
   - `property.listing.submit.dlq`
   - routing `property.listing.submit.dead`
8. Prevent hot requeue/infinite redelivery.
9. Enable publisher confirms/returns where direct RabbitMQ publisher reliability uses them.
10. Add external retry config using `Duration`: max attempts, initial delay, multiplier, max delay and jitter if supported.
11. Add Kafka retry/DLT integration tests.
12. Add RabbitMQ DLQ integration tests.
13. Add poison-message tests for Kafka and RabbitMQ.
14. Add replay-safety test: fail → DLT/DLQ → fix → controlled replay → exactly one business effect.
15. Add `docs/runbooks/messaging-replay.md`.
16. Update messaging topology and command/event catalog.

## Commit sequence
1. `feat(messaging): classify retryable messaging failures`
2. `feat(kafka): add bounded retry and DLT policy`
3. `feat(rabbitmq): add DLQ topology and failure policy`
4. `feat(rabbitmq): add publisher confirm handling`
5. `config(messaging): add bounded retry backoff`
6. `test(kafka): add retry and DLT tests`
7. `test(rabbitmq): add DLQ integration tests`
8. `test(failure): add poison-message scenarios`
9. `test(failure): verify controlled replay safety`
10. `docs(messaging): add replay runbook`

## Final gate
- no infinite retry/requeue
- retry classification explicit
- DLT/DLQ metadata preserved
- poison messages do not block processing
- replay is controlled and idempotent
- raw broker errors do not leak into business/API layers
