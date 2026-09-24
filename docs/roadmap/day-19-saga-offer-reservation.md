# Day 19 — Saga + Offer / Reservation Workflow

## Goal
Offer/Reservation workflow'unu Saga Choreography ile kurmak.

## Tasks
1. Offer Aggregate'i finalize et.
2. CreateOffer + Idempotency-Key uygula.
3. OfferRequested publish et.
4. PropertyService hold consumer ekle.
5. atomic PUBLISHED -> ON_HOLD transition yap.
6. PropertyHeld publish et.
7. Seller pending-offer projection oluştur.
8. Accept/Reject use-case ekle.
9. SellerAccepted/SellerRejected publish et.
10. Property reserve/release handlers ekle.
11. Buyer offer state consumers ekle.
12. compensation semantics ekle.
13. concurrent offer race test et.
14. duplicate event tests yaz.
15. stuck saga/reconciliation note ekle.
16. E2E saga test yaz.
17. docs/state diagrams güncelle.

## Suggested commits
1. feat(buyer): add Offer aggregate
2. feat(buyer): add idempotent create-offer
3. feat(property): add property hold flow
4. feat(seller): add pending-offer projection
5. feat(seller): add accept-reject decisions
6. feat(property): add reserve and release compensation
7. feat(buyer): react to saga outcomes
8. test(saga): add concurrency and duplicate tests
9. test(saga): add end-to-end saga test
10. docs(saga): finalize workflow and recovery notes

## Done
At most one active Property hold invariant korunur; accept/reject compensation flow'u uçtan uca çalışır.
