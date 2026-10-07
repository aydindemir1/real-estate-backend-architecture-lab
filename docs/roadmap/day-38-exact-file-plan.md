# Day 38 — PropertyWatchService için Reactive Architecture: kesin uygulama ve commit planı

Durum: **Planlandı**. Hedef branch: `day/38-reactive-webflux`.

## Önkoşullar ve ilerleme

Day 21 güvenilir Property projection/event akışı ve Day 14 identity modeli.

Yeni Day branch’i bir önceki kapanmış Day branch’inden türetilir. `main` ve kapanmış eski Day branch’leri bu plan güncellemesiyle değiştirilmez. `docs/backend-roadmap-design` canonical plan ve cumulative dokümantasyonu tutar; uygulama kodu ilgili Day branch’inde geliştirilir.

## Kapsam ve karar sınırları

- PropertyWatchService yalnız WatchSubscription capability sahibi; Property sahipliği PropertyService’te kalır.
- Spring WebFlux + Project Reactor; persistence ihtiyacına göre gerçekten reactive datastore adapter seçilir; R2DBC zorunlu değil.
- Reactive path içinde blocking JDBC/OpenFeign, block(), blockFirst(), Thread.sleep() yok.
- SearchService imperative baseline korunur; virtual threads yalnız karşılaştırma konusu olabilir.

## Dosya ve adapter planı

Aşağıdaki yollar uygulama başlangıcında mevcut package standardıyla kesinleştirilecek hedeflerdir. `...` placeholder’dır; literal package veya dosya adı değildir. Gerçek ihtiyaç yoksa boş sınıf/port oluşturulmaz.

- `PropertyWatchService/src/main/java/.../`
- `PropertyWatchService/src/test/java/.../`
- `docs/adr/day-38-reactive-watch.md`
- `docs/testing/reactive-load-comparison.md`

Service modüllerinde build/config/test ve gerektiği kadar ROADMAP/DESIGN/PACKAGE-DESIGN belgeleri güncellenir. Tam sürüm ve runtime seçimi uyumluluk doğrulamasından sonra yapılır; bu plan yeni bir ürünün gereksiz eklenmesini zorunlu tutmaz.

## Bağımlılık sırasına göre görevler

1. WatchSubscription create/remove/read gereksinimini ve Buyer ownership sınırını tanımla.
2. PropertyWatchService build/runtime, WebFlux ve Reactor bağımlılıklarını hazırlayıp MVC/blocking bağımlılık sızıntısını kontrol et.
3. WatchSubscription modelini ve reactive application port’larını oluştur; persistence seçiminde mevcut altyapıyı önce değerlendir.
4. Reactive datastore adapter’ını ve config’i oluştur; seçilen driver’ın gerçekten non-blocking olduğunu doğrula.
5. Property price/status integration event consumer’ını oluştur; broker teslim modelini Reactor pipeline ve acknowledge/retry davranışıyla hizala.
6. Filtering/matching pipeline’ını oluştur; duplicate event safety, ordering ve concurrency sınırlarını belirle.
7. Authentication ve subscription ownership kontrollerini reactive request context üzerinden uygula.
8. Blocking çağrı denetimi, bounded concurrency/backpressure ve error propagation testlerini ekle.
9. Aynı iş yükü/veri/ortam altında imperative vs reactive karşılaştırmalı load test yap; latency percentile, throughput ve kaynak kullanımını ölç.
10. Reactive seçim ADR’sini, ölçüm koşullarını ve SSE’nin Day 39’a ait olduğunu belgeleyerek Knowledge Base’i güncelle.

## Önerilen commit sırası

1. `docs(reactive): watch use-case ve persistence kararını tanımla`
2. `build(watch): WebFlux ve Reactor modülünü hazırla`
3. `feat(watch): reactive subscription port ve adapterlarını ekle`
4. `feat(watch): Property event matching pipelineını ekle`
5. `feat(watch): reactive ownership kontrolünü ekle`
6. `test(reactive): blocking backpressure ve event tekrarını doğrula`
7. `test(performance): aynı koşullarda reactive karşılaştırması yap`
8. `docs(reactive): ölçüm sonuçlarını ve sınırları kaydet`

Sıra bu milestone’ın implementation commit’leri içindir. Her commit tek tutarlı davranış veya karar içerir; küçük komşu görevler inceleme açıklığı korunuyorsa birleştirilebilir. Koşullu özellik uygulanmazsa ilgili feat/test commit’i atlanır ve karar docs commit’inde gerekçelendirilir.

## Doğrulama ve kabul ölçütleri

- WatchSubscription dışında canonical sahiplik yok.
- Reactive path blocking çağrı içermiyor.
- Load test koşulları ve sonuçları kayıtlı; ölçülmemiş scale iddiası yok.

- Önce ilgili GitHub CI/build/test kontrolleri başarılı olur; ardından local runtime ve protokole uygun API/E2E senaryoları doğrulanır.
- Görevlerde belirtilen happy path yanında failure, concurrency/idempotency ve authorization sınırları gerçek adapter testleriyle doğrulanır.
- Test ve runtime evidence ilgili Day’e bağlanır; atlanan kontrol veya test açıkça belirtilir.
- ADR ve service belgeleri actual implementation ile hizalanır; Knowledge Base impact review yapılır ve canonical branch’e dokümantasyon senkronize edilir.
- `Infrastructure Ready`, `Implemented`, `Integrated`, `Verified` ve `Design Only` ayrı tutulur. Kanıt yoksa Verified yazılmaz.

## Kapsam dışı ve kapanış

Day 38 kapsamını aşan yeni teknolojiler/iş akışları sırf çeşitlilik için eklenmez. Docker/Kubernetes/CI/CD genişletmesi bu backend plan güncellemesinin parçası değildir. [Gün özeti](day-38-reactive-webflux.md) ve [ana roadmap](../../ROADMAP.md) aynı kararları taşır. Day bir milestone’dır; tek takvim gününde bitme zorunluluğu yoktur.
