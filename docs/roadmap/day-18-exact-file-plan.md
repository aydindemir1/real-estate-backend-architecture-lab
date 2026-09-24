# Day 18 — Exact File / Projection / Commit Plan

## 0. Scope

Day 18 yalnızca CQRS Search Projection içindir.

Hedef:
- PropertyService lifecycle event publication points
- SearchService Kafka consumer group
- idempotent projection handlers
- Elasticsearch projection updates
- projection freshness tracking
- replay compatibility
- end-to-end Property -> Kafka -> Search tests

Day 18 içinde:
- Offer Saga yok
- GraphQL expansion yok
- full reindex task yok
- reconciliation job yok
- advanced observability stack yok

## Task 1 — Finalize property event contracts

Review and freeze initial contracts used by Search:
- PropertyPublishedEvent
- PropertyUpdatedEvent
- PropertyPriceChangedEvent
- PropertyWithdrawnEvent
- PropertySoldEvent

Optional if Search needs them:
- PropertyCreatedEvent

Search should usually index only searchable/published state.

Commit: docs(cqrs): finalize Search-relevant Property events

## Task 2 — Event payload minimization

Each event carries only fields needed by consumers.

For Search projection, candidate payload:
- propertyId
- sellerId
- title
- description
- propertyType
- city
- district
- price
- currency
- roomCount
- features
- status
- publishedAt
- updatedAt

Do not serialize full Mongo document blindly.

Commit can group with event contract finalization.

## Task 3 — PropertyService publication points

Modify existing slices/use-cases.

Publish via Outbox-enabled port after local state change:
- publish -> PropertyPublishedEvent
- update details -> PropertyUpdatedEvent when slice exists
- change price -> PropertyPriceChangedEvent when slice exists
- withdraw -> PropertyWithdrawnEvent when slice exists
- sold -> PropertySoldEvent when slice exists

Day 18 should only wire events for behaviors currently implemented.

Do not invent endpoints just to emit every event.

Commit: feat(property): publish lifecycle events through outbox

## Task 4 — Search consumer binding

Add config:
- destination property.events
- group search-projection-group
- concurrency initially bounded

Create:
- eventconsumer/PropertyProjectionConsumer.java

Responsibilities:
- deserialize
- validate envelope
- deduplicate/inbox check
- route by eventType to projection handler

Do not put Elasticsearch update logic directly in Consumer lambda.

Commit: feat(search): add Property projection consumer

## Task 5 — Projection handler abstraction

Create package:
- projection/

Create handlers:
- PropertyPublishedProjectionHandler.java
- PropertyUpdatedProjectionHandler.java
- PropertyPriceChangedProjectionHandler.java
- PropertyWithdrawnProjectionHandler.java
- PropertySoldProjectionHandler.java

Only create handler for events actually active.

Commit: feat(search): add projection handlers

## Task 6 — Projection repository operations

Extend Search persistence abstraction with explicit operations:
- upsert(PropertySearchDocument)
- updatePrice(propertyId, ...)
- updateStatus(propertyId, ...)
- delete or deactivate(propertyId)

Prefer semantic methods over leaking Elasticsearch UpdateRequest.

Commit: feat(search): add projection repository operations

## Task 7 — Upsert strategy

PropertyPublished and full PropertyUpdated events can upsert full document.

PriceChanged can partial update if simpler/safer.

Withdrawn/Sold policy:
- either update status and exclude in query
- or delete document

Choose one and document.

Recommended:
keep document with status for traceability, query only searchable statuses.

Commit: feat(search): define projection state semantics

## Task 8 — Idempotent projection

Use Day 17 inbox/processed-message pattern.

Consumer key:
- consumerName = search-projection
- eventId

Flow:
1. check eventId
2. apply projection update
3. mark processed

Need local atomicity strategy appropriate to SearchService support store.

If inbox is not in Elasticsearch, chosen local store must be explicit.

Do not rely solely on Elasticsearch document version for message deduplication.

Commit: feat(search): make projection consumers idempotent

## Task 9 — Event ordering guard

Kafka key = propertyId gives per-property partition ordering.

Still add stale-event protection candidate using event occurredAt/version.

PropertySearchDocument fields:
- sourceVersion candidate
- lastEventOccurredAt

If sourceVersion is available from Property Aggregate version, prefer it.

Rule:
older event must not overwrite newer projection state.

Commit: feat(search): guard projection against stale events

## Task 10 — Projection freshness fields

Add to document:
- sourceUpdatedAt
- projectionUpdatedAt
- lastEventOccurredAt

Freshness calculation:
projectionUpdatedAt - lastEventOccurredAt

Do not expose internal timestamps unless useful to API.

Commit: feat(search): track projection freshness metadata

## Task 11 — Search query filter update

Ensure public search only returns searchable statuses.

Candidate:
- PUBLISHED
- maybe ON_HOLD depending business visibility

Decide now:
Recommended initial searchable status = PUBLISHED only.

Reserved/Sold/Withdrawn excluded.

Commit: feat(search): enforce searchable property statuses

## Task 12 — PropertyPublished handler

Create full projection from event.

Fields mapped to PropertySearchDocument.

Test mapping independently.

Commit: feat(search): project PropertyPublished events

## Task 13 — PropertyPriceChanged handler

Update price/currency/updatedAt/version.

Reject stale version/event.

Commit: feat(search): project PropertyPriceChanged events

## Task 14 — PropertyUpdated handler

Update searchable fields.

Do not update fields not owned by event.

Commit: feat(search): project PropertyUpdated events

## Task 15 — Withdraw/Sold handlers

Policy selected in Task 7.

Recommended:
- update status
- keep doc
- search query excludes non-searchable status

Commit: feat(search): project terminal property statuses

## Task 16 — Replay compatibility

Projection handlers must be deterministic and rerunnable.

Given same ordered event sequence, final projection should be same.

No external side effects beyond projection/inbox.

Commit: test(cqrs): verify projection replay determinism

## Task 17 — Kafka consumer integration test

Create:
- PropertyProjectionConsumerIntegrationTest.java

Scenario:
- publish event to property.events
- Search consumer processes
- Elasticsearch doc appears/updates

Use Kafka + Elasticsearch Testcontainers.

Commit: test(cqrs): add projection consumer integration test

## Task 18 — Publish-to-search E2E test

Create:
- PropertySearchProjectionE2ETest.java

Flow:
1. create DRAFT fixture
2. publish Property via application use-case
3. outbox publishes Kafka event
4. Search consumer processes
5. search query returns Property

Use Awaitility/bounded polling, not Thread.sleep.

Commit: test(cqrs): add Property to Search end-to-end flow

## Task 19 — Price update E2E test

If ChangePrice slice exists by Day 18:
1. publish property
2. change price
3. event
4. search reflects new price

If slice not implemented, do not invent just for test.

## Task 20 — Withdraw/Sold E2E test

If behaviors exist:
- terminal status causes search exclusion

Otherwise defer.

## Task 21 — Duplicate event test

Publish same eventId twice.

Verify:
- projection not duplicated/corrupted
- processed marker prevents duplicate side-effect

Commit: test(cqrs): verify duplicate projection idempotency

## Task 22 — Out-of-order stale event test

Scenario:
- newer event applied
- older event delivered afterward

Verify older version/timestamp does not overwrite.

Commit: test(cqrs): reject stale projection events

## Task 23 — DLT projection test

Simulate projection failure.

Verify:
- bounded retry
- DLT after exhaustion
- good messages continue

Commit: test(cqrs): verify projection failure routing

## Task 24 — Projection freshness metric/log

Day 24 owns full metrics stack.

Day 18 minimum:
- calculate freshness
- expose low-cardinality metric candidate if Micrometer already available

Metric name candidate:
- search_projection_freshness_seconds

Labels:
- eventType maybe

Never label by propertyId.

Commit if implemented: feat(search): expose projection freshness metric

## Task 25 — Consumer lag awareness

Document that Kafka lag + projection freshness are complementary.

Do not equate zero lag with fresh projection if handler is slow/failing.

## Task 26 — API eventual consistency note

Document Search API semantics:
- write success does not guarantee immediate search visibility
- projection is eventually consistent

Optional response metadata not needed.

Commit: docs(cqrs): document eventual consistency semantics

## Task 27 — Architecture tests

Rules:
- Search projection handlers do not call PropertyService directly
- no OpenFeign read-after-write for projection
- Search still owns only Elasticsearch projection
- Property domain does not depend on SearchService
- consumer depends on application/projection handler, not controller

Commit: test(cqrs): enforce projection architecture boundaries

## Task 28 — Recovery hook design

Day 25 owns reindex/reconciliation.

Day 18 only ensure handlers can be invoked from replay/reindex pathways later.

Do not add Cloud Task now.

## Task 29 — Documentation

Modify:
- docs/architecture/messaging-topology.md
- docs/architecture/domain-model.md if projection fields need update
- docs/contracts/command-event-catalog.md
- SearchService/docs/DESIGN.md
- PropertyService/docs/DESIGN.md
- docs/roadmap/day-18-cqrs-search-projection.md

Record actual:
- events consumed
- consumer group
- searchable statuses
- stale-event policy
- idempotency strategy
- freshness metadata
- eventual consistency

Commit: docs(cqrs): finalize Search projection design

## Recommended Commit Sequence

1. docs(cqrs): finalize Search-relevant Property events
2. feat(property): publish lifecycle events through outbox
3. feat(search): add Property projection consumer
4. feat(search): add projection handlers
5. feat(search): add projection repository operations
6. feat(search): define projection state semantics
7. feat(search): make projection consumers idempotent
8. feat(search): guard projection against stale events
9. feat(search): track projection freshness metadata
10. feat(search): enforce searchable property statuses
11. feat(search): project PropertyPublished events
12. feat(search): project PropertyPriceChanged events — if active
13. feat(search): project PropertyUpdated events — if active
14. feat(search): project terminal property statuses — if active
15. test(cqrs): add projection consumer integration test
16. test(cqrs): add Property to Search end-to-end flow
17. test(cqrs): verify duplicate projection idempotency
18. test(cqrs): reject stale projection events
19. test(cqrs): verify projection failure routing
20. test(cqrs): verify projection replay determinism
21. test(cqrs): enforce projection architecture boundaries
22. docs(cqrs): document eventual consistency semantics
23. docs(cqrs): finalize Search projection design

Adjacent event-handler commits may be grouped where they are one cohesive projection capability. Do not combine Property event publication, Search projection logic and all tests into one giant commit.

## Explicitly Deferred from Day 18

Do not implement:
- Offer Saga
- Seller offer projection
- Spring Cloud Task
- full reindex UI/job
- Mongo-vs-Elasticsearch reconciliation job
- Grafana dashboards
- autocomplete/facets/geo expansion

## Critical Design Note — CQRS

CQRS here is system-level:
- PropertyService/MongoDB = write model/source of truth
- SearchService/Elasticsearch = read model/projection

Command/query class naming alone is not the CQRS implementation.

## Critical Design Note — Eventual Consistency

Property write can succeed before Search projection updates.

This is expected behavior, not an error.

## Critical Design Note — Ordering

Kafka key=propertyId gives per-property partition ordering.

Projection still protects against stale/replayed older events when version/timestamp is available.

## Day 18 Final Gate

Day 18 closes only if:
- Property lifecycle event publication is wired through reliable outbox path
- Search consumer group processes property.events
- PropertyPublished creates/upserts projection
- active update events modify projection correctly
- non-searchable terminal statuses are excluded from search
- duplicate event is safe
- stale event cannot overwrite newer projection
- projection freshness metadata exists
- E2E Property publish -> Kafka -> Elasticsearch -> Search works
- projection failure reaches DLT after bounded retry
- replay is deterministic
- Search does not synchronously call PropertyService to build projection
- CQRS architecture rules are automated
- eventual consistency is documented
- no Saga/reindex-task/full observability scope leaks into Day 18
- docs match actual implementation