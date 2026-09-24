# Day 19 — Exact File / Saga / Commit Plan

## 0. Scope

Day 19 yalnızca Offer / Reservation Saga workflow içindir.

Hedef:
- BuyerService Offer Aggregate
- idempotent CreateOffer
- OfferRequested event
- Property hold
- Seller pending-offer projection
- Seller accept/reject
- Property reserve/release
- Buyer outcome updates
- Saga Choreography
- compensation
- concurrency correctness
- E2E tests

Day 19 içinde:
- centralized Saga orchestrator yok
- payment yok
- notification delivery yok
- advanced timeout scheduler yok
- full reconciliation job yok

## Task 1 — Finalize Offer state machine

Use existing model baseline:
- CREATED
- REQUESTED
- PROPERTY_HELD
- ACCEPTED
- REJECTED
- EXPIRED
- CANCELLED
- FAILED

Define valid transitions explicitly.

Commit: docs(saga): finalize Offer state machine

## Task 2 — Offer Aggregate

Create in BuyerService:
- domain/model/Offer.java
- OfferId.java

Fields:
- offerId
- buyerId
- propertyId
- Money amount
- OfferStatus status
- idempotencyReference
- createdAt
- updatedAt
- expiresAt optional

Behavior:
- create
- request
- markPropertyHeld
- accept
- reject
- expire
- cancel
- fail

Terminal states immutable.

Commit: feat(buyer): add Offer aggregate

## Task 3 — Offer repository ports

Create:
- SaveOfferPort.java
- LoadOfferPort.java
- ListBuyerOffersPort.java

BuyerService Couchbase adapter implements these.

Do not mix BuyerPreferences persistence methods into one generic repository.

Commit: feat(buyer): add Offer persistence ports

## Task 4 — Couchbase Offer document

Create:
- adapter/out/persistence/couchbase/document/OfferDocument.java
- repository
- mapper
- adapter

Key:
- offer::{offerId}

Candidate secondary indexes:
- buyerId + createdAt
- propertyId
- status

Only add indexes tied to actual query.

Commit: feat(buyer): add Couchbase Offer persistence

## Task 5 — Idempotency store for CreateOffer

Use Redis from Day 13.

Create:
- application/port/out/OfferIdempotencyPort.java
- adapter/out/redis/RedisOfferIdempotencyAdapter.java

Key:
- idempotency:offer:{key}

Stored value candidate:
- request hash
- offerId
- response/result snapshot
- createdAt

TTL explicit and documented.

Commit: feat(buyer): add Offer idempotency adapter

## Task 6 — CreateOffer use case

Create:
- application/port/in/CreateOfferUseCase.java
- CreateOfferCommand.java
- OfferResult.java
- OfferApplicationService.java

Flow:
1. read Idempotency-Key
2. hash canonical request
3. if key exists same hash -> return prior result
4. if key exists different hash -> conflict
5. create Offer
6. persist REQUESTED state
7. publish OfferRequested through reliable event path
8. persist idempotency result

Need careful local consistency between Offer save / outbox / idempotency record.

Document exact guarantee.

Commit: feat(buyer): add idempotent CreateOffer

## Task 7 — Offer REST endpoint

Endpoint:
- POST /buyers/{buyerId}/offers

Header:
- Idempotency-Key required

Request:
- propertyId
- amount
- currency

Response:
- offerId
- status

Errors:
- missing key -> 400
- same key different payload -> 409 IDEMPOTENCY_CONFLICT
- ownership -> 403

Commit: feat(buyer): expose CreateOffer API

## Task 8 — OfferRequested event

Ensure contract includes:
- offerId
- buyerId
- propertyId
- amount
- currency
- occurredAt
- correlationId
- schemaVersion

Kafka key = offerId or propertyId?

Decision:
Offer workflow topic key should preserve per-offer order, so offer.events key = offerId.

PropertyService consumer still needs property-level concurrency protection in Mongo.

Commit: feat(buyer): publish OfferRequested event

## Task 9 — Property hold command/event consumer

Create in PropertyService:
- hold/HandleOfferRequested.java
or equivalent slice/consumer adapter.

Flow:
1. consume OfferRequested
2. deduplicate eventId
3. load Property
4. require PUBLISHED
5. atomic transition to ON_HOLD
6. activeOfferId = offerId
7. save with optimistic concurrency
8. publish PropertyHeld or rejection outcome

Commit: feat(property): hold Property for Offer

## Task 10 — Property hold race policy

Two offers may arrive concurrently for same Property.

Correctness requirement:
- at most one successful hold

Mechanism:
- Mongo optimistic locking / atomic conditional update

Second contender:
- receives business rejection outcome, not infrastructure retry forever

Commit can group with hold behavior.

## Task 11 — Hold rejection event

Need explicit outcome if Property cannot be held.

Choose one event:
- PropertyHoldRejected

Payload:
- offerId
- propertyId
- reasonCode

Do not misuse generic OfferRejected because seller has not rejected it.

Update event catalog accordingly.

Commit: feat(property): publish hold outcome events

## Task 12 — PropertyHeld event

Payload:
- offerId
- propertyId
- sellerId
- buyerId optional if needed
- heldAt

BuyerService updates Offer -> PROPERTY_HELD.

SellerService creates pending-offer projection.

Commit: feat(property): publish PropertyHeld event

## Task 13 — Buyer PropertyHeld consumer

Create:
- adapter/in/messaging/PropertyHeldConsumer.java

Application handler:
- MarkOfferPropertyHeldUseCase

Flow:
- deduplicate
- load Offer
- transition REQUESTED -> PROPERTY_HELD
- save

Commit: feat(buyer): react to PropertyHeld

## Task 14 — Buyer hold rejection consumer

Create handler for PropertyHoldRejected.

Outcome:
- Offer -> REJECTED or FAILED?

Recommended:
- REJECTED if business property unavailable
- FAILED if infrastructure/internal failure

Use reasonCode.

Commit: feat(buyer): react to Property hold rejection

## Task 15 — Seller pending-offer projection model

Create Cassandra table:
- pending_offers_by_seller

Partition key:
- seller_id

Clustering:
- created_at DESC
- offer_id

Fields:
- offer_id
- property_id
- buyer_id
- amount
- currency
- status
- created_at

Commit: db(seller): add pending-offer projection table

## Task 16 — Seller PropertyHeld consumer

Create:
- infrastructure/messaging/kafka/PropertyHeldEventConsumer.java

Flow:
- deduplicate
- write pending-offer projection

Do not create Seller Aggregate mutation just to store read projection.

Commit: feat(seller): project held Offers for Seller

## Task 17 — List pending offers use case

Create:
- ListPendingOffersQuery
- application service/handler
- GET /sellers/{sellerId}/offers/pending

Reads Cassandra projection.

Commit: feat(seller): expose pending Offers

## Task 18 — AcceptOffer use case

Create:
- AcceptOfferCommand
- AcceptOfferUseCase

Flow:
1. ownership/auth
2. load pending projection
3. validate pending state
4. publish SellerAccepted event
5. update/remove projection as designed

Need idempotency for repeat accept.

Commit: feat(seller): add AcceptOffer decision

## Task 19 — RejectOffer use case

Same shape:
- RejectOfferCommand
- RejectOfferUseCase

Publishes SellerRejected.

Commit: feat(seller): add RejectOffer decision

## Task 20 — Seller decision REST endpoints

Endpoints:
- POST /sellers/{sellerId}/offers/{offerId}/accept
- POST /sellers/{sellerId}/offers/{offerId}/reject

Return:
- 202 Accepted or 200?

Since downstream reservation is asynchronous, recommended 202 Accepted.

Commit: feat(seller): expose Offer decision API

## Task 21 — SellerAccepted event

Payload:
- offerId
- propertyId
- sellerId
- acceptedAt

Kafka key = offerId.

Commit can group with accept use-case.

## Task 22 — SellerRejected event

Payload:
- offerId
- propertyId
- sellerId
- rejectedAt
- reasonCode optional

Commit can group with reject use-case.

## Task 23 — Property SellerAccepted consumer

Create:
- reserve/HandleSellerAccepted.java

Flow:
1. deduplicate
2. load Property
3. require ON_HOLD
4. activeOfferId must match
5. reserve()
6. save
7. publish PropertyReserved

Commit: feat(property): reserve Property after Seller acceptance

## Task 24 — Property SellerRejected consumer

Create:
- releasehold/HandleSellerRejected.java

Flow:
1. deduplicate
2. load Property
3. if ON_HOLD and matching offer -> release to PUBLISHED
4. clear activeOfferId
5. save
6. publish PropertyHoldReleased

Commit: feat(property): release Property hold after rejection

## Task 25 — PropertyReserved event

Payload:
- offerId
- propertyId
- reservedAt

BuyerService consumes -> Offer ACCEPTED.

Seller projection can move to history.

Commit: feat(property): publish PropertyReserved event

## Task 26 — PropertyHoldReleased event

Payload:
- offerId
- propertyId
- releasedAt
- reasonCode

BuyerService consumes -> Offer REJECTED.

Commit: feat(property): publish PropertyHoldReleased event

## Task 27 — Buyer outcome consumers

Create:
- PropertyReservedConsumer
- PropertyHoldReleasedConsumer

Handlers:
- MarkOfferAcceptedUseCase
- MarkOfferRejectedUseCase

Commit: feat(buyer): apply Saga outcomes to Offer

## Task 28 — Seller pending projection cleanup/history

On reserved/released:
- remove pending projection
- write history projection only if Day 19 really needs it

If history table is not required by current API, just remove/update pending and defer full history.

Commit: feat(seller): update pending Offer projection after outcome

## Task 29 — Compensation semantics

Document Saga compensation:
- seller rejected -> release Property
- property reservation failed after seller accepted -> emit reservation failure outcome and release if safe
- buyer update failure -> retry via messaging; Property remains authoritative

Do not implement distributed rollback.

Commit: docs(saga): define compensation semantics

## Task 30 — Reservation failure event

If reserve fails for business reason after SellerAccepted, explicit event required.

Candidate:
- PropertyReservationFailed

Buyer -> FAILED or REJECTED depending cause.

Seller pending state cleaned/reconciled.

Only add if actual failure path exists in implementation.

## Task 31 — Offer expiry policy

Day 19 may define expiresAt but automated expiry scheduler can be deferred.

Document:
- Offer can expire
- actual timed expiration worker later via Task/reconciliation if needed

Do not add ad-hoc scheduler unless scope requires.

## Task 32 — Concurrent Offer race test

Create E2E/integration:
- two OfferRequested for same PUBLISHED Property
- both race
- exactly one PropertyHeld
- one hold rejection
- Property activeOfferId matches winner

Commit: test(saga): verify concurrent Offer hold invariant

## Task 33 — Duplicate event tests

Repeat:
- OfferRequested
- PropertyHeld
- SellerAccepted
- SellerRejected
- PropertyReserved

Verify idempotent transitions.

Commit: test(saga): verify duplicate event idempotency

## Task 34 — Out-of-order event tests

Examples:
- SellerAccepted before PropertyHeld projection reaches Buyer
- duplicate old hold release after reservation

Handlers should reject/no-op impossible stale state safely.

Commit: test(saga): verify stale event handling

## Task 35 — Saga happy-path E2E

Flow:
1. Buyer POST Offer
2. OfferRequested
3. Property ON_HOLD
4. Seller sees pending offer
5. Seller accepts
6. Property RESERVED
7. Buyer Offer ACCEPTED
8. Seller pending removed

Use bounded polling.

Commit: test(saga): add acceptance end-to-end flow

## Task 36 — Saga rejection E2E

Flow:
1. Buyer Offer
2. Property ON_HOLD
3. Seller rejects
4. Property PUBLISHED
5. Buyer Offer REJECTED

Commit: test(saga): add rejection end-to-end flow

## Task 37 — Property unavailable E2E

Flow:
- Offer to RESERVED/SOLD/ON_HOLD property
- hold rejected
- Offer rejected/fails with correct semantic

Commit: test(saga): add unavailable Property flow

## Task 38 — Idempotency API tests

Cases:
- same Idempotency-Key + same payload -> same offerId/result
- same key + different payload -> 409
- retry after network/client timeout does not duplicate Offer

Commit: test(buyer): verify CreateOffer idempotency

## Task 39 — Stuck Saga state model

Define detectable intermediate states:
- Offer REQUESTED too long
- Offer PROPERTY_HELD too long
- Property ON_HOLD too long

Day 19 only detection fields/status timestamps.

Recovery automation deferred to Day 25.

Commit: feat(saga): expose Saga state timestamps for recovery

## Task 40 — Saga correlation

Ensure every event in one workflow shares correlationId.

causationId chains:
- OfferRequested -> PropertyHeld -> SellerAccepted -> PropertyReserved

Do not generate a new correlationId at each hop.

Commit: feat(saga): preserve correlation and causation chain

## Task 41 — Saga architecture tests

Rules:
- no central orchestrator class
- services consume events through adapters/handlers
- no cross-service DB access
- Buyer cannot mutate Property DB
- Seller cannot mutate Property DB
- Property remains hold/reservation authority

Commit: test(saga): enforce choreography boundaries

## Task 42 — Documentation

Modify:
- docs/business/system-workflows.md
- docs/architecture/domain-model.md
- docs/architecture/messaging-topology.md
- docs/contracts/command-event-catalog.md
- docs/roadmap/day-19-saga-offer-reservation.md
- Buyer/Seller/Property DESIGN docs

Create/update sequence diagram candidate:
- docs/architecture/offer-reservation-saga.md

Include:
- happy path
- rejection
- property unavailable
- compensation
- correlation IDs

Commit: docs(saga): finalize Offer reservation choreography

## Recommended Commit Sequence

1. docs(saga): finalize Offer state machine
2. feat(buyer): add Offer aggregate
3. feat(buyer): add Offer persistence ports
4. feat(buyer): add Couchbase Offer persistence
5. feat(buyer): add Offer idempotency adapter
6. feat(buyer): add idempotent CreateOffer
7. feat(buyer): expose CreateOffer API
8. feat(buyer): publish OfferRequested event
9. feat(property): hold Property for Offer
10. feat(property): publish hold outcome events
11. feat(buyer): react to PropertyHeld
12. feat(buyer): react to Property hold rejection
13. db(seller): add pending-offer projection table
14. feat(seller): project held Offers for Seller
15. feat(seller): expose pending Offers
16. feat(seller): add AcceptOffer decision
17. feat(seller): add RejectOffer decision
18. feat(seller): expose Offer decision API
19. feat(property): reserve Property after Seller acceptance
20. feat(property): release Property hold after rejection
21. feat(property): publish PropertyReserved event
22. feat(property): publish PropertyHoldReleased event
23. feat(buyer): apply Saga outcomes to Offer
24. feat(seller): update pending Offer projection after outcome
25. docs(saga): define compensation semantics
26. feat(saga): preserve correlation and causation chain
27. feat(saga): expose Saga state timestamps for recovery
28. test(buyer): verify CreateOffer idempotency
29. test(saga): verify concurrent Offer hold invariant
30. test(saga): verify duplicate event idempotency
31. test(saga): verify stale event handling
32. test(saga): add acceptance end-to-end flow
33. test(saga): add rejection end-to-end flow
34. test(saga): add unavailable Property flow
35. test(saga): enforce choreography boundaries
36. docs(saga): finalize Offer reservation choreography

Adjacent event-handler commits may be grouped if cohesive. Buyer, Property, Seller and test changes should remain independently reviewable.

## Explicitly Deferred from Day 19

Do not implement:
- payment processing
- notification delivery
- centralized Saga orchestrator
- full timed expiration scheduler
- automated stuck-saga repair
- Cloud Task reconciliation
- distributed transaction coordinator

## Critical Design Note — Property owns concurrency

PropertyService is authoritative for whether a Property can be held/reserved.

Seller decision does not bypass Property invariant.

## Critical Design Note — Saga Choreography

No central orchestrator coordinates the workflow.

Each service reacts to domain/integration events and updates only its own local state.

## Critical Design Note — Idempotency

HTTP Idempotency-Key protects CreateOffer retries.

Message eventId/inbox protects asynchronous duplicate delivery.

These are different concerns and both are required.

## Day 19 Final Gate

Day 19 closes only if:
- Offer Aggregate/state machine works
- CreateOffer is idempotent
- same key same payload returns same result
- same key different payload returns conflict
- OfferRequested is published reliably
- Property allows at most one active hold
- concurrent Offers produce one winner
- Seller sees pending held Offer
- Seller accept/reject works asynchronously
- acceptance reserves Property
- rejection releases Property
- Buyer Offer reaches ACCEPTED/REJECTED correctly
- duplicate events are safe
- stale/out-of-order events do not corrupt state
- correlation/causation chain is preserved
- happy-path E2E works
- rejection E2E works
- unavailable Property path works
- no cross-service DB access exists
- choreography boundaries are automated/tested
- no payment/notification/Cloud Task scope leaks into Day 19
- docs match actual implementation