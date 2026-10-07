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
1. docs(consistency): CAP ve projection consistency sözleşmesini tanımla
2. `docs(cqrs): finalize Search-relevant Property events`
3. `feat(property): publish lifecycle events through outbox`
4. `feat(search): add Property projection consumer`
5. `feat(search): add projection handlers`
6. `feat(search): make projection consumers idempotent`
7. `feat(search): guard projection against stale events`
8. `feat(search): track projection freshness metadata`
9. `feat(search): enforce searchable property statuses`
10. `test(cqrs): add projection integration tests`
11. `test(cqrs): add Property-to-Search E2E flow`
12. `test(cqrs): verify replay and stale-event safety`
13. `docs(cqrs): document eventual consistency`
14. test(cqrs): stale read ve read-your-writes beklentisini doğrula
15. test(cqrs): consumer kesintisi sonrası catch-up davranışını doğrula
16. docs(consistency): ölçülen freshness ve garanti sınırlarını kaydet

Küçük ve aynı sorumluluğa ait komşu commit’ler birleştirilebilir; karar, uygulama ve doğrulama ayrı incelenebilir kalır.

## Final gate
- MongoDB source of truth olarak kalır
- Elasticsearch derived read model olarak kalır
- duplicate/stale event'ler güvenlidir
- PUBLISHED projection aranabilir
- terminal state'ler normal search'ten çıkarılır
- E2E event projection çalışır
- failure bounded retry sonrasında DLT'ye yönlenir

## Onaylanan system design ek kapsamı — CAP ve consistency modellerinin proje üzerinden öğrenilmesi

Durum: **Planlandı**. Bu bölüm günün mevcut temel görevlerine eklenir; tamamlanmış implementation iddiası değildir. Ek görevler foundation kurulduktan sonra ve günün dokümantasyon/kapanış adımından önce uygulanır. Yukarıdaki commit sırası bu kapsamı içerir.

### Ek görevler ve çıktı belgeleri

1. CAP’i network partition koşulunda consistency/availability tercihi olarak açıkla; her ortamda rastgele iki özellik seçimi gibi anlatma. Consistency garantisini operation bazında canonical write ve derived Search read için ayır.
2. Strong/eventual consistency ile read-your-writes beklentisini karşılaştır; Mongo commit, reliable event delivery ve Elasticsearch refresh/projection gecikmesinin farklı aşamalar olduğunu belgeleyerek kullanıcı sözleşmesini tanımla.
3. Property update sonrası geçici stale Search sonucunu kontrollü entegrasyon testiyle göster; süre/sürüm sınırlarını ölç ve Search sourceVersion guard ile hizala.
4. Consumer durması/yeniden başlaması ve bağlantı kesintisi altında canonical state korunumu, projection catch-up ve duplicate/stale event güvenliğini test et. Test yalnız latency üretiyorsa gerçek network partition doğrulandı iddiasında bulunma.
5. docs/architecture/consistency-models.md ve ilgili ADR’de görünürlük/freshness sınırını kaydet; Knowledge Base kavramlarını güncelle.

### Ek kabul ölçütleri

- CAP açıklaması partition bağlamında doğru.
- Canonical write ile Search visibility ayrılmış.
- Stale read/catch-up davranışı ve ölçüm koşulları kanıtlı.

Kapanışta ilgili service ROADMAP/DESIGN belgeleri ve Knowledge Base gerçek implementation/kanıtlarla güncellenir. Önce ilgili GitHub CI başarılı olur; ardından local runtime/API doğrulaması yapılır. Bir Day bir milestone’dır; kapsam gerektiğinde birden fazla takvim gününde tamamlanabilir.
