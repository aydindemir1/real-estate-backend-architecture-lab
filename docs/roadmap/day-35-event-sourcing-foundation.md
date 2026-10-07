# Day 35 — Offer write-side için Event Sourcing temeli

Durum: **Planlandı**. Branch: `day/35-event-sourcing-foundation`.

## Amaç ve önkoşullar

Day 22 Offer state machine/idempotency ve Day 18–20 reliable outbound kuralları.

## Kapsam ve sahiplik sınırları

- Event Sourcing yalnız BuyerService içindeki Offer Aggregate write-side için uygulanır.
- Couchbase append-only event stream authoritative geçmişi tutar; current state event’lerden yeniden oluşturulur.
- Day 22 state machine transition guard olarak korunur; internal domain event ile Kafka integration event ayrı sözleşmelerdir.
- Yeni EventStoreDB eklenmez; OfferCountered yalnız gerçek negotiation gereksinimi varsa eklenir.

## Görevler

1. Day 22 Offer akışını, invariant’ları ve mevcut kayıtları incele; geçiş ve rollback sınırını ADR ile belirle.
2. Offer event sözlüğünü ve deterministik apply/evolve kurallarını tanımla; sürüm, timestamp ve actor metadata sınırlarını belirle.
3. OfferEventStorePort için loadStream/append(expectedVersion) sözleşmesini oluştur; Couchbase adapter’ını yaz.
4. Stream identity, sequence/version ve eşzamanlı append doğruluğunu Couchbase’in seçilen atomicity mekanizmasıyla gerekçelendir.
5. Offer rebuild bileşenini oluştur; replay sırasında dış yan etki veya tekrar broker publish çalıştırma.
6. Create/accept/reject gibi mevcut command handler’larını rebuilt state üzerindeki transition guard’a bağla.
7. Durable Idempotency-Key → request hash → Offer/result ilişkisini koru; Redis’i correctness kaynağı yapma.
8. Event append ile kritik outbound kaydın atomik/durable ilişkisini netleştir; append ardından korumasız save→send kullanma.
9. Eski state kayıtları varsa migration ve başarısızlıkta geri dönüş planı oluştur; veri yoksa bu durumu açıkça kaydet.
10. Append concurrency, duplicate command, geçersiz transition, crash-window ve yeniden başlatma entegrasyon testlerini yaz.

## Kapanış ölçütleri

- Aynı stream’e eşzamanlı append kayıp update üretmiyor.
- Rebuilt state aynı invariant ve transition kurallarına uyuyor.
- Kritik integration event kaybolmuyor; replay yan etki üretmiyor.

Ayrıntılı dosya/adapter planı, bağımlılık sırası, commit’ler ve doğrulama: [Day 35 kesin planı](day-35-exact-file-plan.md). Kapanış kanıtı oluşmadan bu capability tamamlandı veya Verified olarak işaretlenmez.
