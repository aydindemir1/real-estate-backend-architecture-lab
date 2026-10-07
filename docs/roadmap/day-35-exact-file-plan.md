# Day 35 — Offer write-side için Event Sourcing temeli: kesin uygulama ve commit planı

Durum: **Planlandı**. Hedef branch: `day/35-event-sourcing-foundation`.

## Önkoşullar ve ilerleme

Day 22 Offer state machine/idempotency ve Day 18–20 reliable outbound kuralları.

Yeni Day branch’i bir önceki kapanmış Day branch’inden türetilir. `main` ve kapanmış eski Day branch’leri bu plan güncellemesiyle değiştirilmez. `docs/backend-roadmap-design` canonical plan ve cumulative dokümantasyonu tutar; uygulama kodu ilgili Day branch’inde geliştirilir.

## Kapsam ve karar sınırları

- Event Sourcing yalnız BuyerService içindeki Offer Aggregate write-side için uygulanır.
- Couchbase append-only event stream authoritative geçmişi tutar; current state event’lerden yeniden oluşturulur.
- Day 22 state machine transition guard olarak korunur; internal domain event ile Kafka integration event ayrı sözleşmelerdir.
- Yeni EventStoreDB eklenmez; OfferCountered yalnız gerçek negotiation gereksinimi varsa eklenir.

## Dosya ve adapter planı

Aşağıdaki yollar uygulama başlangıcında mevcut package standardıyla kesinleştirilecek hedeflerdir. `...` placeholder’dır; literal package veya dosya adı değildir. Gerçek ihtiyaç yoksa boş sınıf/port oluşturulmaz.

- `BuyerService/src/main/java/.../offer/`
- `BuyerService/src/main/java/.../application/port/OfferEventStorePort.java`
- `BuyerService/src/main/java/.../infrastructure/couchbase/`
- `BuyerService/src/test/java/.../offer/`
- `docs/adr/day-35-offer-event-sourcing.md`

Service modüllerinde build/config/test ve gerektiği kadar ROADMAP/DESIGN/PACKAGE-DESIGN belgeleri güncellenir. Tam sürüm ve runtime seçimi uyumluluk doğrulamasından sonra yapılır; bu plan yeni bir ürünün gereksiz eklenmesini zorunlu tutmaz.

## Bağımlılık sırasına göre görevler

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

## Önerilen commit sırası

1. `docs(event-sourcing): Offer event ve geçiş sözleşmesini tanımla`
2. `feat(offer): event uygulama ve rebuild modelini ekle`
3. `feat(buyer): Couchbase event store adapterını ekle`
4. `refactor(offer): command akışını event stream üzerine bağla`
5. `feat(offer): durable idempotency ve outbound ilişkisini koru`
6. `test(event-sourcing): concurrency ve crash-window davranışını doğrula`
7. `docs(event-sourcing): migration ve rollback kararını kaydet`

Sıra bu milestone’ın implementation commit’leri içindir. Her commit tek tutarlı davranış veya karar içerir; küçük komşu görevler inceleme açıklığı korunuyorsa birleştirilebilir. Koşullu özellik uygulanmazsa ilgili feat/test commit’i atlanır ve karar docs commit’inde gerekçelendirilir.

## Doğrulama ve kabul ölçütleri

- Aynı stream’e eşzamanlı append kayıp update üretmiyor.
- Rebuilt state aynı invariant ve transition kurallarına uyuyor.
- Kritik integration event kaybolmuyor; replay yan etki üretmiyor.

- Önce ilgili GitHub CI/build/test kontrolleri başarılı olur; ardından local runtime ve protokole uygun API/E2E senaryoları doğrulanır.
- Görevlerde belirtilen happy path yanında failure, concurrency/idempotency ve authorization sınırları gerçek adapter testleriyle doğrulanır.
- Test ve runtime evidence ilgili Day’e bağlanır; atlanan kontrol veya test açıkça belirtilir.
- ADR ve service belgeleri actual implementation ile hizalanır; Knowledge Base impact review yapılır ve canonical branch’e dokümantasyon senkronize edilir.
- `Infrastructure Ready`, `Implemented`, `Integrated`, `Verified` ve `Design Only` ayrı tutulur. Kanıt yoksa Verified yazılmaz.

## Kapsam dışı ve kapanış

Day 35 kapsamını aşan yeni teknolojiler/iş akışları sırf çeşitlilik için eklenmez. Docker/Kubernetes/CI/CD genişletmesi bu backend plan güncellemesinin parçası değildir. [Gün özeti](day-35-event-sourcing-foundation.md) ve [ana roadmap](../../ROADMAP.md) aynı kararları taşır. Day bir milestone’dır; tek takvim gününde bitme zorunluluğu yoktur.
