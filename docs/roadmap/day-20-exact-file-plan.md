# Day 20 — Exact Cassandra Reliable Outbound Messaging Plan

## Scope
- Cassandra-friendly durable pending outbound messages
- Seller listing command reliable dispatch to RabbitMQ
- reusable outbound foundation for later Seller Kafka decisions
- publisher confirms
- Property command inbox/deduplication
- CreatePropertyFromListingCommand
- reliability tests

## Tasks

1. Write ADR `ADR-007-cassandra-reliable-outbound-messaging.md` comparing naive save+send, pending-message table/dispatcher, CDC and distributed transaction.
2. Select broker-neutral durable pending outbound message pattern.
3. Create Cassandra table model such as `pending_outbound_messages_by_bucket`.
4. Partition by bounded dispatch bucket/shard; cluster by `created_at`, `message_id`.
5. Persist fields: messageId, aggregateId, messageType, destinationKind, payload, status, retryCount, nextAttemptAt, createdAt, correlationId.
6. Do not use unbounded partition or `ALLOW FILTERING`.
7. When ListingSubmission moves to SUBMITTED, persist durable pending `SubmitPropertyListingCommand`.
8. State exact Cassandra consistency guarantee; do not pretend relational atomic outbox exists.
9. Implement `ListingCommandDispatchService`.
10. Implement RabbitMQ publisher adapter with confirms/returns.
11. Mark pending message dispatched only after broker confirmation.
12. Keep transient failures pending for retry.
13. Make durable pending model broker-neutral so Day 22 SellerAccepted/SellerRejected Kafka messages can reuse it.
14. In PropertyService create `SubmitPropertyListingCommandConsumer`.
15. Add Mongo processed-command/inbox storage keyed by commandId.
16. Implement `CreatePropertyFromListingCommand` application use case.
17. ACK RabbitMQ only after durable Property creation + inbox success.
18. Duplicate command must not create a duplicate Property.
19. Add broker-down/recovery tests.
20. Add duplicate-dispatch tests.
21. Add Cassandra partition/query tests.
22. Update Seller/Property design docs and messaging topology.

## Commit sequence
1. `docs(adr): decide Cassandra reliable outbound messaging`
2. `db(seller): add pending outbound message table`
3. `feat(seller): persist pending listing command on submit`
4. `feat(seller): add reliable outbound dispatcher`
5. `feat(rabbitmq): integrate publisher confirms with Seller dispatcher`
6. `feat(property): add listing command inbox deduplication`
7. `feat(property): consume SubmitPropertyListingCommand`
8. `feat(property): create Property from listing command`
9. `test(seller): verify broker outage recovery`
10. `test(property): verify duplicate listing command safety`
11. `docs(messaging): document Cassandra outbound guarantees`

## Final gate
- no best-effort Cassandra save→broker send
- pending message partition bounded
- dispatcher retryable and idempotent
- broker outage does not lose listing command
- Property command consumer deduplicates commandId
- duplicate dispatch creates one Property
- durable model can support later Kafka Seller decision events
