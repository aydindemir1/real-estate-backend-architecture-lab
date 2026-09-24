# Day 17 — Exact Kafka + Stream + Function Foundation Plan

## Scope

- Kafka local infrastructure
- Spring Cloud Stream
- Spring Cloud Function
- event envelope
- property.events foundation
- partition/group/order tests

## Reliability guard
Critical business publication is not wired as best-effort save-then-send. Day 17 establishes transport/contracts; Day 18–20 establish reliability. Offer publication is activated with the real Offer workflow in Day 22.

## Task 1 — Kafka local infrastructure

Modify docker-compose.yml.

Add Kafka using an explicit image/version compatible with local learning setup.

Choose one simple local topology:
- single broker KRaft preferred if current image/tooling supports cleanly

Required:
- explicit version
- advertised listeners correct for host + compose network
- named volume if useful
- healthcheck
- bounded memory

Do not add ZooKeeper unless chosen Kafka image genuinely requires it.

Commit: infra(kafka): add local Kafka broker

## Task 2 — Dependency setup

Add only to modules that publish/consume now.

Likely modules:
- PropertyService
- BuyerService
- SearchService only if minimal consumer demo is placed there

Dependencies:
- spring-cloud-stream
- Kafka binder
- Spring Cloud Function core if not transitively sufficient
- test binder only if useful
- Testcontainers Kafka

Do not add Kafka client globally to every module.

Commit: build(kafka): add Stream and Function dependencies

## Task 3 — Topic names and ownership

Canonical topics:
- property.events
- offer.events

Naming is domain/capability-oriented.

Do not create one topic per event type.

## Task 4 — Event envelope model

Create contract package/location that does not couple service domain models.

Candidate:
- contracts/events/CommonEventEnvelope.java

Fields:
- eventId
- eventType
- aggregateId
- aggregateType
- occurredAt
- correlationId
- causationId
- schemaVersion
- payload

Implementation choice:
- generic envelope with typed payload where practical
- or event-specific record carrying same metadata shape

Avoid raw Map payload if type safety is lost.

Commit: feat(messaging): add event envelope contracts

## Task 5 — Property event contracts

Create initial event payload types:
- PropertyCreatedEvent
- PropertyPublishedEvent
- PropertyUpdatedEvent
- PropertyPriceChangedEvent
- PropertyHeldEvent
- PropertyHoldReleasedEvent
- PropertyReservedEvent
- PropertyWithdrawnEvent
- PropertySoldEvent

Day 16 does not need every event actively emitted.

At minimum, implement one or two producer examples sufficient to prove Stream wiring, preferably PropertyPublished and PropertyPriceChanged candidate if current Property use-cases support them.

Do not fabricate unused event emission just for coverage.

Commit: feat(property): add property event contracts

## Task 6 — Offer event contracts

Create initial payload types:
- OfferRequestedEvent
- SellerAcceptedEvent
- SellerRejectedEvent
- OfferExpiredEvent

Day 16 producer can be foundation-only if Offer Aggregate is not yet active.

Do not emit fake offer events from nonexistent workflow.

Commit: feat(buyer): add offer event contracts

## Task 7 — Spring Cloud Stream binding naming

Use functional binding names.

Candidate functions:
- propertyEventsSupplier or explicit StreamBridge-based publisher
- offerEventsSupplier only if current flow supports it
- propertyEventConsumer demo

Prefer clear binding names documented in config.

## Task 8 — Producer abstraction — PropertyService

Create application/integration boundary:
- PublishPropertyEventPort.java

Create infrastructure adapter:
- KafkaPropertyEventPublisher.java

Responsibilities:
- map application/integration event
- add envelope metadata
- set message key = propertyId
- publish to property.events

Domain Aggregate does not know Kafka.

Commit: feat(property): add property event publisher

## Task 9 — Producer abstraction — BuyerService

Create:
- PublishOfferEventPort.java
- KafkaOfferEventPublisher.java

If Day 19 Offer workflow not present yet, adapter may be wired and contract-tested without application emission.

Do not create fake business trigger.

Commit: feat(buyer): add offer event publisher

## Task 10 — StreamBridge vs Supplier decision

Decision rule:
- event publication triggered by application action -> StreamBridge is usually clearer
- continuous/generated source -> Supplier

Document choice.

Do not force Supplier for imperative business event publication.

## Task 11 — Kafka message key

property.events key = propertyId
offer.events key = offerId

Key must be explicitly set in producer headers/config.

Purpose:
- partition affinity
- per-aggregate ordering

## Task 12 — Consumer group names

Initial groups:
- search-projection-group
- seller-offer-projection-group candidate

Day 16 minimal consumer should use a real logical group name.

Do not use random generated group IDs in production-like config.

## Task 13 — Minimal consumer foundation

Preferred location:
- SearchService minimal property event consumer if it can remain non-business/no projection yet

Create:
- eventconsumer/PropertyEventConsumer.java

Function role Day 16:
- deserialize
- validate envelope basics
- log metadata
- call no-op/test handler or minimal technical handler

Do not update Elasticsearch projection yet.

Alternative if no-op consumer feels artificial:
use a dedicated test consumer only and defer Search consumer wiring to Day 18.

Choose whichever avoids fake production code.

Commit if production consumer added:
feat(search): add property event consumer foundation

## Task 14 — Spring Cloud Function model

Consumer bean should be explicit and small.

Example semantic:
- Consumer<PropertyPublishedEventEnvelope>

Do not bury business logic inside lambda.

## Task 15 — Serialization

Initial format:
- JSON

Rules:
- explicit content type
- no Java serialization
- event schema version field
- backward-compatible additive changes preferred

Avro/Schema Registry deferred.

## Task 16 — Correlation and causation

Producer obtains current correlationId from request/context when available.

causationId:
- current triggering message/event id if message-driven
- null/absent for user-originated root event

Consumer restores/propagates correlation context where practical.

Commit: feat(messaging): propagate event correlation metadata

## Task 17 — Basic duplicate-safe consumer foundation

Day 17 owns full Inbox/Idempotent Consumer.

Day 16 only enforce design hooks:
- every event has eventId
- consumer handler API receives eventId
- side-effecting consumer must be written assuming duplicate delivery

Do not implement full processed-message store yet.

## Task 18 — Stream configuration

Add external config:
- brokers
- destinations
- consumer groups
- content type
- partition key expression/strategy
- concurrency default 1 initially

Do not add aggressive retry config yet.

Commit: config(kafka): add Stream bindings and topic configuration

## Task 19 — Topic provisioning strategy

Choose:
- binder auto-provision for local learning
or
- explicit topic creation in local infra

Production-like preference is explicit topic properties where important.

If auto-provision used, still document expected partitions.

Initial partitions:
- small fixed count, e.g. 3, if useful to demonstrate partitioning

Do not over-provision.

## Task 20 — Partition behavior test

Create:
- KafkaPartitioningIntegrationTest.java

Scenario:
- publish multiple events same propertyId -> same partition
- different propertyIds may distribute

Do not assert exact partition number unless hash algorithm/config is intentionally fixed.

Commit: test(kafka): verify aggregate partitioning

## Task 21 — Producer integration test

Create:
- PropertyEventPublisherIntegrationTest.java
- OfferEventPublisherIntegrationTest.java only if meaningful

Verify:
- correct topic
- correct key
- event envelope fields
- JSON deserialization

Commit: test(kafka): add producer integration tests

## Task 22 — Consumer integration test

Create:
- PropertyEventConsumerIntegrationTest.java if consumer foundation exists

Verify:
- consumer group receives
- payload deserializes
- correlation metadata available

Commit: test(kafka): add consumer integration test

## Task 23 — Ordering test

Publish ordered events for same aggregate key.

Verify observed order within one partition.

Document:
ordering is per partition, not global.

Commit: test(kafka): verify per-aggregate ordering

## Task 24 — Consumer group behavior test

Create test proving two instances in same group divide partitions/messages rather than both processing every event.

If test infrastructure complexity is too high for Day 16, document and defer to hardening Day 22.

## Task 25 — Failure handling baseline

Day 16 only:
- consumer exception visible
- no silent swallow
- no infinite retry

Full retry topic/DLT policy Day 17.

## Task 26 — Observability baseline

Log only metadata:
- eventId
- eventType
- aggregateId
- correlationId

Do not log full payload by default.

Full Kafka metrics Day 24.

## Task 27 — Security

If Kafka local auth is disabled for simplicity, document as local-only.

Production-like authentication/TLS can be later infrastructure hardening.

Do not confuse application OAuth token with broker authentication.

## Task 28 — RabbitMQ coexistence verification

Confirm existing RabbitMQ command/work-queue flows still work.

Architecture rule:
- RabbitMQ command semantic remains
- Kafka event semantic added

No migration of existing Auth -> UserProfile RabbitMQ flow.

## Task 29 — Architecture tests

Add rules where useful:
- domain packages do not depend on Kafka/Stream
- event publisher adapters implement ports
- Search/Buyer/Property application layers do not depend on binder classes directly

Create/update:
- messaging architecture tests per service

Commit: test(messaging): enforce broker adapter boundaries

## Task 30 — Documentation

Modify:
- docs/architecture/messaging-topology.md
- docs/contracts/command-event-catalog.md
- docs/roadmap/day-16-kafka-stream-function.md
- affected service DESIGN/PACKAGE-DESIGN docs

Record actual:
- Kafka image/version
- topic names
- partition count
- key strategy
- binding names
- consumer groups
- serialization format
- correlation metadata
- RabbitMQ/Kafka semantic split

Commit: docs(kafka): finalize event-streaming topology

## Recommended Commit Sequence

1. infra(kafka): add local Kafka broker
2. build(kafka): add Stream and Function dependencies
3. feat(messaging): add event envelope contracts
4. feat(property): add property event contracts
5. feat(buyer): add offer event contracts
6. feat(property): add property event publisher
7. feat(buyer): add offer event publisher
8. config(kafka): add Stream bindings and topic configuration
9. feat(messaging): propagate event correlation metadata
10. feat(search): add property event consumer foundation — only if non-artificial
11. test(kafka): add producer integration tests
12. test(kafka): verify aggregate partitioning
13. test(kafka): verify per-aggregate ordering
14. test(kafka): add consumer integration test — if consumer exists
15. test(messaging): enforce broker adapter boundaries
16. docs(kafka): finalize event-streaming topology

Adjacent event-contract commits can be combined if small. Infrastructure, contracts, producer adapters, config, tests and docs should remain separately reviewable.

## Explicitly Deferred from Day 16

Do not implement:
- Inbox
- Outbox
- processed-message table
- retry topics
- DLT
- poison message policy
- full Search projection
- Saga
- seller offer projection
- Avro
- Schema Registry
- Kafka transactions

## Critical Design Note — Stream is not Kafka ignorance

Spring Cloud Stream abstracts integration plumbing but does not remove the need to understand:
- partitions
- keys
- consumer groups
- offsets
- ordering
- replay

## Critical Design Note — Function model

Use Spring Cloud Function to structure consumer logic, not to hide business rules in lambdas.

## Critical Design Note — Command vs Event

Kafka carries facts that happened.
RabbitMQ continues to carry targeted commands/work where designed.

## Day 16 Final Gate

Day 16 closes only if:
- Kafka starts locally
- property.events exists/works
- offer.events contract exists
- event envelope metadata is standardized
- propertyId/offerId key strategy is explicit
- at least one real producer flow is verified
- consumer group behavior/config is explicit
- JSON serialization is explicit
- correlation/causation fields are populated where possible
- same aggregate key preserves partition ordering
- domain/application layers do not depend directly on Kafka binder APIs
- RabbitMQ baseline still works
- no Outbox/Inbox/DLT/Saga/Search projection scope leaks into Day 16
- docs match actual implementation

## Source-of-truth note

This file follows the final Day 15–33 roadmap. Earlier combined Day numbering is superseded by `docs/roadmap/LEGACY-DAY-MAPPING.md`.
