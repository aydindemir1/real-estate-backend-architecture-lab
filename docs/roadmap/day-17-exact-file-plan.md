# Day 17 — Exact File / Reliability / Commit Plan

## 0. Scope

Day 17 yalnızca reliable messaging hardening içindir.

Hedef:
- Outbox
- Inbox / processed-message tracking
- Idempotent Consumer
- retryable vs non-retryable classification
- Kafka retry/DLT
- RabbitMQ DLX/DLQ
- poison message handling
- SellerService Cassandra -> RabbitMQ reliable dispatch decision + implementation
- replay safety
- failure-path tests

Day 17 içinde:
- Saga business orchestration yok
- Search projection business logic yok
- advanced observability stack yok
- full operational automation yok

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

## Task 13 — Kafka retry classification

Create:
- RetryableMessagingException.java
- NonRetryableMessagingException.java

Classification examples:

Retryable:
- temporary network failure
- temporary downstream unavailable
- timeout

Non-retryable:
- malformed payload
- unsupported schema version
- permanent validation error

Business conflict:
- explicit use-case handling, not blind retry

Commit: feat(messaging): classify retryable messaging failures

## Task 14 — Kafka retry topic / DLT policy

Configure with Spring Kafka/Stream-supported pattern.

Need explicit:
- retry attempts
- backoff
- DLT destination naming
- original topic metadata preservation

Candidate:
- property.events.retry
- property.events.dlt
- offer.events.retry
- offer.events.dlt

Do not create infinite retry.

Commit: feat(kafka): add bounded retry and DLT policy

## Task 15 — Kafka poison message handling

Malformed/non-deserializable message must not block partition forever.

Use error handling/deserialization strategy that routes after bounded failure.

Preserve:
- original payload where safe
- headers
- exception classification

Commit can group with DLT policy.

## Task 16 — RabbitMQ DLX/DLQ policy

Existing target topology:
- exchange real-estate.listing.commands
- queue property.listing.submit
- DLX real-estate.listing.dlx
- DLQ property.listing.submit.dlq
- dead routing property.listing.submit.dead

Implement/configure only when Seller -> Property command flow activates.

Add:
- dead-letter exchange binding
- bounded retry/redelivery strategy
- no hot requeue loop

Commit: feat(rabbitmq): add DLQ topology and failure policy

## Task 17 — RabbitMQ publisher confirms

Enable publisher confirms/returns if chosen reliability strategy uses direct publish acknowledgment.

Handle:
- ack
- nack
- unroutable returned message

Do not equate broker ack with consumer processing success.

Commit: feat(rabbitmq): add publisher confirm handling

## Task 18 — Seller Cassandra -> RabbitMQ reliable dispatch ADR

Create ADR:
- docs/adr/ADR-007-cassandra-rabbitmq-reliable-dispatch.md

Evaluate alternatives:
1. naive save + publish
2. Cassandra pending command table + dispatcher
3. CDC-based approach
4. external transaction coordinator — reject unless justified

Recommended for lab:
Cassandra pending-command/outbox-like table + idempotent dispatcher.

Important:
Call it Cassandra-friendly pending dispatch pattern, not fake relational transactional outbox.

Commit: docs(adr): decide Cassandra RabbitMQ reliable dispatch

## Task 19 — Cassandra pending listing command table

If ADR chooses pending dispatch, create:
- infrastructure/cassandra/table/PendingListingCommandTable.java
- repository
- mapper
- adapter

Query-first table candidate:
pending_listing_commands_by_bucket

Partition strategy must avoid unbounded single partition.

Candidate partition key:
- dispatch_bucket (e.g. yyyyMMddHH or shard bucket)

Clustering:
- created_at
- command_id

Fields:
- command_id
- submission_id
- seller_id
- payload
- status
- retry_count
- next_attempt_at
- created_at

Commit: db(seller): add pending listing command dispatch table

## Task 20 — Seller local write + pending command

Goal:
When ListingSubmission becomes SUBMITTED, persist submission state and pending command with best achievable local consistency.

Cassandra caveat:
Cross-partition atomicity is not guaranteed.

Design choices:
- use same partition/batch only if data modeling supports it
- otherwise reconciliation must detect gaps

Do not use LOGGED BATCH across unrelated partitions just to simulate relational transaction.

Document exact consistency guarantee.

Commit: feat(seller): persist pending listing command on submit

## Task 21 — Seller dispatch worker

Create:
- application/service/ListingCommandDispatchService.java
- infrastructure/messaging/rabbitmq/RabbitMqListingCommandPublisher.java

Flow:
1. load due pending commands
2. publish with commandId/correlationId
3. wait for publisher confirm if enabled
4. mark dispatched
5. retry transient failures

Bound batch size.

Commit: feat(seller): add reliable listing command dispatcher

## Task 22 — PropertyService command consumer

Create:
- presentation/messaging/rabbitmq/SubmitPropertyListingCommandConsumer.java

Flow:
1. validate envelope
2. deduplicate commandId
3. call CreatePropertyFromListingCommand use-case
4. ack only after durable local success

Do not put business creation logic in listener.

Commit: feat(property): consume SubmitPropertyListingCommand

## Task 23 — Property command inbox

Create Mongo processed-command persistence:
- ProcessedListingCommandDocument.java
- repository/adapter

Unique key:
- commandId

Ensure duplicate command does not create duplicate Property.

Commit: feat(property): add listing command inbox deduplication

## Task 24 — CreatePropertyFromListingCommand use-case

Create in PropertyService:
- command/CreatePropertyFromListingCommand.java
- handler/use-case

Flow:
- validate seller/listing payload
- create DRAFT Property
- save Property
- persist processed command atomically where possible
- create PropertyCreated outbox event if required by current topology

This is the first real production-like Property creation path.

Commit: feat(property): create Property from listing command

## Task 25 — Idempotency conflict semantics

Same messageId + same semantic payload -> no-op/same outcome.

Same messageId + different payload hash -> quarantine/non-retryable conflict.

Add payloadHash only if practical and deterministic.

## Task 26 — Retry backoff configuration

External config:
- max attempts
- initial delay
- multiplier
- max delay

Use Duration.

Add jitter if supported.

Commit: config(messaging): add bounded retry backoff

## Task 27 — DLQ/DLT metadata

Preserve:
- original destination
- original message id
- correlationId
- event/command type
- failure class/code
- attempt count
- failedAt

Do not log secrets/full sensitive payload unnecessarily.

## Task 28 — Replay command/tooling

Day 17 should define manual controlled replay procedure, not necessarily a production UI.

Create docs/runbook:
- docs/runbooks/messaging-replay.md

Procedure:
1. inspect sample
2. classify root cause
3. fix
4. verify idempotency
5. replay limited batch
6. monitor

Commit: docs(messaging): add DLQ DLT replay runbook

## Task 29 — Outbox integration tests

Create:
- PropertyOutboxIntegrationTest.java

Cases:
- property change writes outbox record
- publisher success marks published
- transient failure keeps pending/retry metadata
- crash-window duplicate publish tolerated by consumer

Commit: test(messaging): add outbox integration tests

## Task 30 — Inbox/idempotency tests

Create:
- IdempotentConsumerIntegrationTest.java

Cases:
- first message processes
- duplicate message no-op
- duplicate after restart still no-op
- same id different payload conflict if hash policy enabled

Commit: test(messaging): add inbox idempotency tests

## Task 31 — Kafka retry/DLT tests

Create:
- KafkaRetryDltIntegrationTest.java

Cases:
- retryable failure retries bounded times
- non-retryable routes directly/quickly to DLT per chosen policy
- poison message does not block partition indefinitely
- DLT retains metadata

Commit: test(kafka): add retry and DLT tests

## Task 32 — RabbitMQ DLQ tests

Create:
- RabbitMqDlqIntegrationTest.java

Cases:
- transient failure redelivery bounded
- non-retryable dead-letters
- DLQ routing correct
- no infinite requeue loop

Commit: test(rabbitmq): add DLQ integration tests

## Task 33 — Cassandra dispatch reliability tests

Create:
- ListingCommandDispatchIntegrationTest.java

Cases:
- submitted listing produces pending command
- successful publish marks dispatched
- broker down keeps pending
- retry later succeeds
- duplicate dispatcher execution does not create duplicate Property

Commit: test(seller): add reliable dispatch integration tests

## Task 34 — Consumer crash-window test

Simulate:
- business side effect succeeds
- ack/processed marker update boundary fails

Verify re-delivery does not duplicate business effect.

This is a key at-least-once correctness test.

Commit: test(messaging): verify consumer crash-window idempotency

## Task 35 — Metrics/logging baseline

Day 24 owns full observability.

Day 17 minimum counters/logs:
- outbox pending count log/metric candidate
- retry exhausted count
- DLT/DLQ count
- duplicate detected count

If custom metrics added, keep low cardinality.

Commit if implemented: feat(messaging): expose reliability metrics

## Task 36 — Architecture tests

Rules:
- domain does not depend on broker APIs
- application ports define publish capability
- listeners only call application handlers
- outbox persistence does not leak Kafka classes
- RabbitMQ publisher stays infrastructure

Commit: test(messaging): enforce reliability adapter boundaries

## Task 37 — Documentation reconciliation

Modify:
- docs/architecture/messaging-topology.md
- docs/architecture/messaging-reliability.md
- docs/contracts/command-event-catalog.md
- docs/roadmap/day-17-reliable-messaging.md
- SellerService DESIGN/PACKAGE-DESIGN
- PropertyService DESIGN/PACKAGE-DESIGN

Add actual:
- retry counts/backoff
- DLT/DLQ names
- outbox storage
- inbox storage
- Cassandra dispatch guarantee
- replay procedure

Commit: docs(messaging): finalize reliable messaging architecture

## Recommended Commit Sequence

1. docs(messaging): define reliability failure matrix
2. feat(property): add Mongo outbox persistence
3. feat(property): persist domain state and outbox atomically
4. feat(messaging): add outbox publisher
5. config(messaging): add bounded outbox polling
6. feat(messaging): add idempotent consumer foundation
7. feat(messaging): classify retryable messaging failures
8. feat(kafka): add bounded retry and DLT policy
9. feat(rabbitmq): add DLQ topology and failure policy
10. feat(rabbitmq): add publisher confirm handling
11. docs(adr): decide Cassandra RabbitMQ reliable dispatch
12. db(seller): add pending listing command dispatch table
13. feat(seller): persist pending listing command on submit
14. feat(seller): add reliable listing command dispatcher
15. feat(property): add listing command inbox deduplication
16. feat(property): consume SubmitPropertyListingCommand
17. feat(property): create Property from listing command
18. config(messaging): add bounded retry backoff
19. test(messaging): add outbox integration tests
20. test(messaging): add inbox idempotency tests
21. test(kafka): add retry and DLT tests
22. test(rabbitmq): add DLQ integration tests
23. test(seller): add reliable dispatch integration tests
24. test(messaging): verify consumer crash-window idempotency
25. test(messaging): enforce reliability adapter boundaries
26. docs(messaging): add DLQ DLT replay runbook
27. docs(messaging): finalize reliable messaging architecture

Large reliability changes should remain split by concern. Do not merge Outbox, Cassandra dispatch, Kafka DLT and tests into one huge commit.

## Explicitly Deferred from Day 17

Do not implement:
- Offer Saga choreography
- Search projection business handling
- Cloud Task replay jobs
- full Grafana dashboards
- advanced broker security/TLS
- global schema registry

## Critical Design Note — Exactly Once

Project does not claim end-to-end exactly-once delivery.

Target is:
- at-least-once transport
- idempotent consumer
- atomic local side effects where possible
- duplicate-safe business outcome

## Critical Design Note — Cassandra

Do not force relational Transactional Outbox semantics onto Cassandra.

The chosen pending-dispatch strategy must state its exact guarantees and reconciliation needs.

## Critical Design Note — Ack Timing

Consumer acknowledgement occurs only after durable local success.

## Day 17 Final Gate

Day 17 closes only if:
- Property Outbox persists reliably
- Outbox publisher is bounded and retry-aware
- duplicate event delivery is safe
- inbox/processed-message semantics work for active consumers
- Kafka retry/DLT is bounded
- poison messages cannot block forever
- RabbitMQ DLQ topology works
- publisher confirms/unroutable handling works if selected
- Seller Cassandra -> RabbitMQ reliability ADR is accepted
- pending listing command dispatch works under broker outage
- Property command consumer deduplicates commandId
- duplicate listing command does not create duplicate Property
- replay runbook exists
- crash-window idempotency is tested
- domain/application layers do not depend directly on broker APIs
- no Saga/Search projection scope leaks into Day 17
- docs match actual implementation