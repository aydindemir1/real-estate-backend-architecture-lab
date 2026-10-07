# Day 39 — SSE ile gerçek zamanlı Property bildirimleri: kesin uygulama ve commit planı

Durum: **Planlandı**. Hedef branch: `day/39-realtime-notification`.

## Önkoşullar ve ilerleme

Day 38 WatchSubscription/reactive matching ve ownership doğrulanmış olmalı.

Yeni Day branch’i bir önceki kapanmış Day branch’inden türetilir. `main` ve kapanmış eski Day branch’leri bu plan güncellemesiyle değiştirilmez. `docs/backend-roadmap-design` canonical plan ve cumulative dokümantasyonu tutar; uygulama kodu ilgili Day branch’inde geliştirilir.

## Kapsam ve karar sınırları

- Primary client delivery Server-Sent Events; WebSocket yalnız gerçek bidirectional requirement varsa değerlendirilir.
- Kafka event’leri client’a doğrudan açılmaz; client-facing notification sözleşmesine dönüştürülür.
- Ayrı notification-history datastore yalnız gerçek ihtiyaçla seçilir; gösterim için eklenmez.

## Dosya ve adapter planı

Aşağıdaki yollar uygulama başlangıcında mevcut package standardıyla kesinleştirilecek hedeflerdir. `...` placeholder’dır; literal package veya dosya adı değildir. Gerçek ihtiyaç yoksa boş sınıf/port oluşturulmaz.

- `PropertyWatchService/src/main/java/.../notification/`
- `PropertyWatchService/src/main/java/.../sse/`
- `PropertyWatchService/src/test/java/.../`
- `docs/architecture/property-notifications.md`

Service modüllerinde build/config/test ve gerektiği kadar ROADMAP/DESIGN/PACKAGE-DESIGN belgeleri güncellenir. Tam sürüm ve runtime seçimi uyumluluk doğrulamasından sonra yapılır; bu plan yeni bir ürünün gereksiz eklenmesini zorunlu tutmaz.

## Bağımlılık sırasına göre görevler

1. SSE endpoint, authentication yöntemi ve client-facing notification sözleşmesini tanımla; native EventSource header sınırlarını hesaba kat, token’ı URL/loglara sızdırma.
2. Day 38 matching çıktısını safe notification DTO’ya dönüştür; internal payload/kişisel veri sızıntısını engelle.
3. Authenticated Buyer için yalnız kendi WatchSubscription stream’ini aç; başka kullanıcı erişimini reddet.
4. Connect/disconnect/reconnect ve reactive resource cleanup davranışını uygula.
5. Slow-consumer için bounded buffer/backpressure, overflow/disconnect ve reconnect davranışını belirle.
6. Event identity ve Last-Event-ID semantics’i değerlendirilip garanti sınırını yaz; geçmiş tutulmuyorsa kayıpsız replay iddiasında bulunma.
7. Duplicate upstream event ve concurrent bağlantıların delivery davranışını tanımla.
8. Ownership, invalid token, stream cancellation ve subscription removal testlerini ekle.
9. Reconnect, duplicate, slow consumer ve bağlantı sonrası kaynak sızıntısı entegrasyon testlerini yaz.
10. REST/gRPC/Kafka ile SSE sorumluluk farkını ve delivery/runbook sınırlarını Knowledge Base’e yansıt.

## Önerilen commit sırası

1. `docs(sse): bildirim sözleşmesini ve reconnect garantisini tanımla`
2. `feat(watch): client bildirim dönüşümünü ekle`
3. `feat(sse): yetkili Buyer stream endpointini ekle`
4. `feat(sse): cleanup ve bounded slow-consumer politikasını ekle`
5. `test(sse): auth reconnect duplicate ve kaynak temizliğini doğrula`
6. `docs(sse): delivery sınırlarını ve runbooku kaydet`

Sıra bu milestone’ın implementation commit’leri içindir. Her commit tek tutarlı davranış veya karar içerir; küçük komşu görevler inceleme açıklığı korunuyorsa birleştirilebilir. Koşullu özellik uygulanmazsa ilgili feat/test commit’i atlanır ve karar docs commit’inde gerekçelendirilir.

## Doğrulama ve kabul ölçütleri

- Başka Buyer’ın bildirimi okunamıyor.
- Slow consumer sınırsız memory büyümesi yaratmıyor.
- Reconnect garantisi testlerle aynı; cancellation kaynakları temizliyor.

- Önce ilgili GitHub CI/build/test kontrolleri başarılı olur; ardından local runtime ve protokole uygun API/E2E senaryoları doğrulanır.
- Görevlerde belirtilen happy path yanında failure, concurrency/idempotency ve authorization sınırları gerçek adapter testleriyle doğrulanır.
- Test ve runtime evidence ilgili Day’e bağlanır; atlanan kontrol veya test açıkça belirtilir.
- ADR ve service belgeleri actual implementation ile hizalanır; Knowledge Base impact review yapılır ve canonical branch’e dokümantasyon senkronize edilir.
- `Infrastructure Ready`, `Implemented`, `Integrated`, `Verified` ve `Design Only` ayrı tutulur. Kanıt yoksa Verified yazılmaz.

## Kapsam dışı ve kapanış

Day 39 kapsamını aşan yeni teknolojiler/iş akışları sırf çeşitlilik için eklenmez. Docker/Kubernetes/CI/CD genişletmesi bu backend plan güncellemesinin parçası değildir. [Gün özeti](day-39-realtime-notification.md) ve [ana roadmap](../../ROADMAP.md) aynı kararları taşır. Day bir milestone’dır; tek takvim gününde bitme zorunluluğu yoktur.
