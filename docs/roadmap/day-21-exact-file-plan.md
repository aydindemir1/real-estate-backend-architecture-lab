# Day 21 — Kesin CQRS + Elasticsearch Event Projection Planı

## Kapsam
- Reliable publication üzerinden Property lifecycle event'leri
- Search Kafka consumer group
- Elasticsearch projection handler'ları
- idempotency + stale-event protection
- projection freshness
- eventual consistency
- E2E publish-to-search

## Task'ler

1. Gerçekte implemente edildiği şekliyle Search ile ilgili event'leri finalize et: PropertyPublished, PropertyUpdated, PropertyPriceChanged, PropertyWithdrawn, PropertySold.
2. Event payload'ını minimal ve consumer-oriented tut; Mongo document'larını körlemesine serialize etme.
3. Property state değişikliklerini Day 18 Outbox reliable publication path'e bağla.
4. Search binding'i configure et: `property.events`, group `search-projection-group`.
5. Handler'lara delegate eden `PropertyProjectionConsumer` oluştur.
6. Yalnızca aktif handler'ları oluştur:
   - PropertyPublishedProjectionHandler
   - PropertyUpdatedProjectionHandler
   - PropertyPriceChangedProjectionHandler
   - PropertyWithdrawnProjectionHandler
   - PropertySoldProjectionHandler
7. Semantic projection repository operation'ları ekle: upsert, updatePrice, updateStatus/deactivate.
8. Terminal document'ları status ile tut; public search başlangıçta yalnızca PUBLISHED döndürür.
9. Inbox/processed-message semantics kullanarak eventId ile deduplication yap.
10. `sourceVersion`, `sourceUpdatedAt`, `projectionUpdatedAt`, `lastEventOccurredAt` ekle.
11. Daha düşük version/eski event'leri reject/no-op yap.
12. Replay determinism sağla.
13. Kafka+Elasticsearch integration test ekle.
14. Property publish → Outbox → Kafka → Elasticsearch → Search E2E ekle.
15. Duplicate event testi ekle.
16. Stale/out-of-order event testi ekle.
17. Projection failure→DLT testi ekle.
18. Day 29 için low-cardinality freshness/failure metric hook'ları expose et.
19. Eventual consistency semantics'i dokümante et.
20. ArchUnit rule ekle: Search projection, projection oluşturmak için PropertyService'i synchronous olarak çağırmaz.

## Commit sırası
1. `docs(cqrs): finalize Search-relevant Property events`
2. `feat(property): publish lifecycle events through outbox`
3. `feat(search): add Property projection consumer`
4. `feat(search): add projection handlers`
5. `feat(search): make projection consumers idempotent`
6. `feat(search): guard projection against stale events`
7. `feat(search): track projection freshness metadata`
8. `feat(search): enforce searchable property statuses`
9. `test(cqrs): add projection integration tests`
10. `test(cqrs): add Property-to-Search E2E flow`
11. `test(cqrs): verify replay and stale-event safety`
12. `docs(cqrs): document eventual consistency`

## Final gate
- MongoDB source of truth olarak kalır
- Elasticsearch derived read model olarak kalır
- duplicate/stale event'ler güvenlidir
- PUBLISHED projection aranabilir
- terminal state'ler normal search'ten çıkarılır
- E2E event projection çalışır
- failure bounded retry sonrasında DLT'ye yönlenir
