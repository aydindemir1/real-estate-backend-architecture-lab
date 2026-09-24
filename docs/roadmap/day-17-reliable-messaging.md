# Day 17 — Outbox + Inbox + Idempotency + Retry + DLQ/DLT

## Goal
At-least-once messaging altında correctness ve recoverability sağlamak.

## Tasks
1. reliable messaging failure modes'larını kesinleştir.
2. PropertyService Outbox model/storage ekle.
3. outbox publisher ekle.
4. Inbox/processed-message model ekle.
5. idempotent consumer logic ekle.
6. retryable/non-retryable classification ekle.
7. Kafka retry/DLT policy ekle.
8. RabbitMQ DLX/DLQ policy ekle.
9. poison message handling ekle.
10. SellerService Cassandra -> RabbitMQ reliable dispatch stratejisini ADR ile finalize et.
11. seçilen Cassandra-friendly dispatch mechanism'i implement et.
12. publisher confirms gerekiyorsa ekle.
13. duplicate/retry/DLQ tests yaz.
14. outbox backlog observability foundation ekle.
15. docs/runbook güncelle.

## Suggested commits
1. docs(messaging): finalize reliable-delivery decisions
2. feat(property): add outbox persistence
3. feat(messaging): add outbox publisher
4. feat(messaging): add inbox and idempotent consumer
5. feat(kafka): add retry and DLT policies
6. feat(rabbitmq): add DLQ and publisher reliability
7. feat(seller): implement reliable listing command dispatch
8. test(messaging): add duplicate and failure-path tests
9. docs(messaging): add replay and DLQ runbooks

## Done
Duplicate delivery business side-effect üretmez; failure/retry/DLQ recovery paths test edilmiştir.

## Exact reliability/file plan

Implementation source of truth: `docs/roadmap/day-17-exact-file-plan.md`
