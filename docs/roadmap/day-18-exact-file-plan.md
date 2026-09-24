# Day 18 — Exact Outbox + Inbox + Idempotent Consumer Plan

## Scope

- Property Mongo Outbox
- Inbox/Processed Message
- duplicate-safe consumers
- atomic local side effects
- crash-window tests
- reliable-publication design for Couchbase and Cassandra

## Mandatory correctness addendum
- Property/MongoDB uses Outbox.
- Seller/Cassandra uses durable pending outbound messages + dispatcher/reconciliation.
- Buyer/Couchbase must have a durable outbound-publication strategy before Day 22.
- Best-effort critical broker publication is prohibited.

## Task 1 — Reliability failure matrix

Create/update docs:
- docs/architecture/messaging-reliability.md

List failure modes:
- DB commit succeeds, publish fails
- publish succeeds, DB commit fails
- duplicate delivery
- consumer crashes after side effect before ack
- malformed payload
- transient broker outage
- downstream timeout
- poison message
- replay duplicate

Map each to strategy.

Commit: docs(messaging): define reliability failure matrix

## Task 2 — Delivery semantics declaration

Declare project baseline:
- at-least-once delivery
- duplicate delivery is expected
- exactly-once business effect achieved through idempotency + local transaction semantics

Do not claim Kafka exactly-once end-to-end business semantics.

## Task 3 — PropertyService Outbox model

Primary candidate because PropertyService uses MongoDB and publishes lifecycle events.

Create:
- shared/outbox/OutboxMessage.java
- shared/outbox/OutboxStatus.java
- shared/outbox/OutboxRepository.java

Fields:
- outboxId
- aggregateId
- aggregateType
- eventType
- payload
- occurredAt
- correlationId
- causationId
- schemaVersion
- status
- retryCount
- nextAttemptAt
- publishedAt

## Task 4 — Mongo Outbox document

Create:
- shared/outbox/mongo/OutboxDocument.java
- shared/outbox/mongo/SpringDataOutboxRepository.java
- shared/outbox/mongo/MongoOutboxRepositoryAdapter.java

Indexes:
- status + nextAttemptAt
- aggregateId candidate if operational query needs it

Commit: feat(property): add Mongo outbox persistence

## Task 5 — Atomic Property + Outbox write

Because MongoDB supports multi-document transactions only in replica set topology, choose carefully.

Preferred learning options:
- same Mongo transaction with replica-set Testcontainers/local topology
or
- single-document embedded outbox only if model remains maintainable

Do not pretend two separate Mongo saves are atomic.

Create transaction boundary around Property state + Outbox record if using multi-document transaction.

Commit: feat(property): persist domain state and outbox atomically

## Task 6 — Outbox event mapper

Create:
- shared/outbox/OutboxEventMapper.java

Responsibilities:
- integration event -> persisted payload/envelope

No Kafka client usage.

Commit: feat(property): add outbox event mapping

## Task 7 — Outbox publisher

Create:
- shared/outbox/OutboxPublisher.java
- shared/outbox/OutboxPublishingJob.java or application service

Flow:
1. fetch pending due records
2. publish through PublishPropertyEventPort
3. mark published on success
4. update retry metadata on transient failure

Batch size bounded.

Do not use unbounded polling.

Commit: feat(messaging): add outbox publisher

## Task 8 — Outbox scheduling trigger

Choose one lightweight trigger for Day 17:
- @Scheduled polling

Spring Cloud Task remains Day 25.

Config:
- interval Duration
- batch size

Do not create multiple uncontrolled scheduler instances in scaled environment without coordination strategy.

Since local lab may be single instance, document distributed scaling caveat.

Commit: config(messaging): add bounded outbox polling

## Task 9 — Outbox publish idempotency

Outbox record may be published more than once if crash occurs after broker ack but before mark-published.

Therefore consumers must deduplicate by eventId.

Do not rely on producer-side exactly-once illusion.

## Task 10 — Inbox model

Create generic consumer-side concept:
- ProcessedMessage.java
- ProcessedMessageRepository.java

Fields:
- messageId/eventId
- consumerName
- processedAt
- payloadHash optional

Unique logical key:
- consumerName + messageId

## Task 11 — Inbox persistence per datastore

Do not force one DB technology across services.

Examples:
- SearchService Elasticsearch is not ideal as inbox source; use its canonical/local supporting store only if suitable or a dedicated lightweight persistence choice explicitly justified
- SellerService Cassandra can use processed_message_by_consumer table
- BuyerService Couchbase can store idempotency documents

Day 17 should implement inbox where an actual side-effecting consumer exists now.

Do not create unused inbox tables in every service.

## Task 12 — Idempotent consumer wrapper

Create application/infrastructure helper pattern:
- IdempotentMessageHandler.java

Flow:
1. check messageId
2. if already processed -> no-op/ack
3. execute handler
4. persist processed marker in same local transaction where possible

Rule:
processed marker + side effect should be atomic within local datastore capabilities.

Commit: feat(messaging): add idempotent consumer foundation

## Task 13 — Outbox integration tests

Create:
- PropertyOutboxIntegrationTest.java

Cases:
- property change writes outbox record
- publisher success marks published
- transient failure keeps pending/retry metadata
- crash-window duplicate publish tolerated by consumer

Commit: test(messaging): add outbox integration tests

## Task 14 — Inbox/idempotency tests

Create:
- IdempotentConsumerIntegrationTest.java

Cases:
- first message processes
- duplicate message no-op
- duplicate after restart still no-op
- same id different payload conflict if hash policy enabled

Commit: test(messaging): add inbox idempotency tests

## Task 15 — Consumer crash-window test

Simulate:
- business side effect succeeds
- ack/processed marker update boundary fails

Verify re-delivery does not duplicate business effect.

This is a key at-least-once correctness test.

Commit: test(messaging): verify consumer crash-window idempotency

## Task 16 — Architecture tests

Rules:
- domain does not depend on broker APIs
- application ports define publish capability
- listeners only call application handlers
- outbox persistence does not leak Kafka classes
- RabbitMQ publisher stays infrastructure

Commit: test(messaging): enforce reliability adapter boundaries

## Source-of-truth note

This file follows the final Day 15–33 roadmap. Earlier combined Day numbering is superseded by `docs/roadmap/LEGACY-DAY-MAPPING.md`.
