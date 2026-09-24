# Day 22 — Exact Saga + Offer / Reservation Workflow Plan

## Scope
- Offer Aggregate/state machine
- durable HTTP idempotency
- OfferRequested
- Property hold/reserve/release
- Seller pending-offer projection
- Saga Choreography + compensation
- concurrency and E2E tests

## Correctness rule — durable idempotency
Redis is not the source of truth. BuyerService/Couchbase durably stores:
- Idempotency-Key
- canonical request hash
- OfferId
- result/status

Same key + same payload returns the prior result. Same key + different payload returns 409. Redis may accelerate lookup only.

## Tasks

1. Implement Offer states CREATED, REQUESTED, PROPERTY_HELD, ACCEPTED, REJECTED, EXPIRED, CANCELLED, FAILED.
2. Create Offer Aggregate and invariants; terminal states immutable.
3. Add Couchbase Offer persistence with key `offer::{offerId}`.
4. Add durable Couchbase idempotency document/record with unique deterministic key.
5. Optionally add Redis accelerator adapter; correctness must survive Redis loss.
6. Implement `CreateOfferUseCase` with Idempotency-Key.
7. Ensure Offer save + required outbound event use the Day 18 durable Buyer/Couchbase publication strategy.
8. Expose `POST /buyers/{buyerId}/offers`.
9. Publish `OfferRequested` using reliable outbound mechanism.
10. Property consumes OfferRequested and atomically/optimistically transitions PUBLISHED→ON_HOLD with activeOfferId.
11. Publish `PropertyHeld` or explicit `PropertyHoldRejected`.
12. Buyer consumes hold outcomes and transitions Offer.
13. Add Cassandra `pending_offers_by_seller` projection.
14. Seller consumes PropertyHeld and writes pending offer.
15. Expose pending-offer query.
16. Implement Seller accept/reject use cases.
17. Persist SellerAccepted/SellerRejected through Day 20 durable Cassandra outbound messaging before publishing to Kafka.
18. Expose async 202 accept/reject endpoints.
19. Property consumes SellerAccepted -> RESERVED -> PropertyReserved.
20. Property consumes SellerRejected -> release to PUBLISHED -> PropertyHoldReleased.
21. Buyer consumes final outcome -> ACCEPTED/REJECTED.
22. Clean/update Seller pending projection.
23. Define compensation rules; no distributed rollback.
24. Preserve one correlationId through workflow and causation chain per event.
25. Add stuck-state timestamps for later recovery.
26. Add duplicate-event tests.
27. Add out-of-order/stale-event tests.
28. Add two-offer race test: exactly one hold.
29. Add happy-path acceptance E2E.
30. Add rejection E2E.
31. Add unavailable Property E2E.
32. Add Idempotency-Key retry tests, including Redis loss/restart scenario.
33. Add choreography ArchUnit rules.

## Commit sequence
1. `feat(buyer): add Offer aggregate and persistence`
2. `feat(buyer): add durable Offer idempotency`
3. `feat(buyer): add idempotent CreateOffer`
4. `feat(buyer): publish OfferRequested reliably`
5. `feat(property): hold Property for Offer`
6. `feat(buyer): react to Property hold outcomes`
7. `db(seller): add pending-offer projection`
8. `feat(seller): project held Offers`
9. `feat(seller): add reliable accept and reject decisions`
10. `feat(property): reserve or release Property`
11. `feat(buyer): apply final Saga outcomes`
12. `test(buyer): verify durable CreateOffer idempotency`
13. `test(saga): verify concurrent Offer invariant`
14. `test(saga): add acceptance and rejection E2E`
15. `docs(saga): finalize choreography and compensation`

## Final gate
- Redis loss cannot create duplicate Offer
- OfferRequested cannot be silently lost after durable Offer commit
- Seller decision event cannot be silently lost after durable decision
- exactly one active Property hold
- accept reserves, reject releases
- duplicates/stale events safe
- no cross-service DB access
- choreography, not central orchestrator
