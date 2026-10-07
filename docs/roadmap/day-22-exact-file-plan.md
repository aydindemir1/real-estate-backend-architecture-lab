# Day 22 — Kesin Saga + Offer / Reservation Workflow Planı

## Kapsam
- Offer Aggregate/state machine
- durable HTTP idempotency
- OfferRequested
- Property hold/reserve/release
- Seller pending-offer projection
- Saga Choreography + compensation
- concurrency ve E2E testleri

## Correctness kuralı — durable idempotency
Redis source of truth değildir. BuyerService/Couchbase aşağıdakileri durable olarak saklar:
- Idempotency-Key
- canonical request hash
- OfferId
- result/status

Aynı key + aynı payload önceki sonucu döndürür. Aynı key + farklı payload 409 döndürür. Redis yalnızca lookup'ı hızlandırabilir.

## Task'ler

1. Offer state'lerini implemente et: CREATED, REQUESTED, PROPERTY_HELD, ACCEPTED, REJECTED, EXPIRED, CANCELLED, FAILED.
2. Offer Aggregate ve invariant'ları oluştur; terminal state'ler immutable olsun.
3. `offer::{offerId}` key'i ile Couchbase Offer persistence ekle.
4. Unique deterministic key ile durable Couchbase idempotency document/record ekle.
5. Opsiyonel Redis accelerator adapter ekle; correctness Redis kaybından etkilenmemelidir.
6. Idempotency-Key ile `CreateOfferUseCase` implemente et.
7. Offer save + required outbound event'in Day 18 durable Buyer/Couchbase publication strategy kullandığından emin ol.
8. `POST /buyers/{buyerId}/offers` expose et.
9. Reliable outbound mechanism ile `OfferRequested` publish et.
10. Property, OfferRequested tüketip PUBLISHED→ON_HOLD geçişini activeOfferId ile atomic/optimistic olarak uygular.
11. `PropertyHeld` veya explicit `PropertyHoldRejected` publish et.
12. Buyer hold outcome'larını tüketip Offer state'ini geçirir.
13. Cassandra `pending_offers_by_seller` projection ekle.
14. Seller PropertyHeld tüketip pending offer yazar.
15. Pending-offer query expose et.
16. Seller accept/reject use-case'lerini implemente et.
17. SellerAccepted/SellerRejected'i Kafka'ya publish etmeden önce Day 20 durable Cassandra outbound messaging üzerinden persist et.
18. Async 202 accept/reject endpoint'lerini expose et.
19. Property SellerAccepted tüketir -> RESERVED -> PropertyReserved.
20. Property SellerRejected tüketir -> PUBLISHED'a release -> PropertyHoldReleased.
21. Buyer final outcome tüketir -> ACCEPTED/REJECTED.
22. Seller pending projection'ı temizle/güncelle.
23. Compensation rule'larını tanımla; distributed rollback yok.
24. Workflow boyunca tek correlationId ve event başına causation chain koru.
25. Sonraki recovery için stuck-state timestamp'leri ekle.
26. Duplicate-event testleri ekle.
27. Out-of-order/stale-event testleri ekle.
28. İki-offer race testi ekle: tam olarak bir hold.
29. Happy-path acceptance E2E ekle.
30. Rejection E2E ekle.
31. Unavailable Property E2E ekle.
32. Redis loss/restart senaryosu dahil Idempotency-Key retry testleri ekle.
33. Choreography ArchUnit rule'ları ekle.

## Commit sırası
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
- Redis kaybı duplicate Offer oluşturamaz
- durable Offer commit sonrasında OfferRequested sessizce kaybolamaz
- durable decision sonrasında Seller decision event sessizce kaybolamaz
- tam olarak bir active Property hold vardır
- accept reserve eder, reject release eder
- duplicate/stale event'ler güvenlidir
- cross-service DB access yok
- central orchestrator değil, choreography kullanılır
