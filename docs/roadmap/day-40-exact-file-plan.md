# Day 40 — Web ve Mobile için Backend-for-Frontend: kesin uygulama ve commit planı

Durum: **Planlandı**. Hedef branch: `day/40-bff`.

## Önkoşullar ve ilerleme

Day 33 backend contracts ve Day 14 identity/service credentials baseline.

Yeni Day branch’i bir önceki kapanmış Day branch’inden türetilir. `main` ve kapanmış eski Day branch’leri bu plan güncellemesiyle değiştirilmez. `docs/backend-roadmap-design` canonical plan ve cumulative dokümantasyonu tutar; uygulama kodu ilgili Day branch’inde geliştirilir.

## Kapsam ve karar sınırları

- Ayrı deployable WebBffService ve MobileBffService oluşturulur; frontend yazılmaz.
- Web: Agent/Seller dashboard composition; Mobile: Buyer odaklı hafif composition.
- Gateway edge/platform, BFF client-specific composition sorumluluğunu taşır.
- BFF canonical state/invariant sahibi değildir; mümkün olduğunca stateless; yeni canonical datastore yok.
- GraphQL Federation entegrasyonu Day 41’e bırakılır.

## Dosya ve adapter planı

Aşağıdaki yollar uygulama başlangıcında mevcut package standardıyla kesinleştirilecek hedeflerdir. `...` placeholder’dır; literal package veya dosya adı değildir. Gerçek ihtiyaç yoksa boş sınıf/port oluşturulmaz.

- `WebBffService/`
- `MobileBffService/`
- `docs/architecture/bff-responsibilities.md`
- `docs/testing/bff-composition.md`

Service modüllerinde build/config/test ve gerektiği kadar ROADMAP/DESIGN/PACKAGE-DESIGN belgeleri güncellenir. Tam sürüm ve runtime seçimi uyumluluk doğrulamasından sonra yapılır; bu plan yeni bir ürünün gereksiz eklenmesini zorunlu tutmaz.

## Bağımlılık sırasına göre görevler

1. Her client için gerekli read use-case ve response alanlarını tanımla; required/optional dependency matrisini çıkar.
2. WebBffService ve MobileBffService için minimal build/config/runtime ve adapter sınırlarını oluştur.
3. Mevcut REST/gRPC sözleşmelerinden client-specific response composition uygula.
4. Bağımsız downstream çağrıları bounded parallel yürüt; uygun yürütme modelini ve thread/pool sınırlarını belirle.
5. Token/service identity propagation ve response alanı authorization sınırını uygula; BFF’i authorization bypass yapma.
6. Timeout budget, fan-out sınırı ve optional dependency için partial degradation response sözleşmesini tanımla.
7. Required dependency failure için kararlı hata döndür; fake success veya eksik alanı normal veri gibi sunma.
8. Response mapping, parallel-call, downstream timeout ve partial failure contract/integration testlerini ekle.
9. ArchUnit ile BFF’in domain aggregate/repository sahibi olmadığını ve domain iş kuralı taşımadığını doğrula.
10. Gateway/BFF/Federated Router sorumluluk matrisini ve Knowledge Base belgelerini güncelle.

## Önerilen commit sırası

1. `docs(bff): client use-case ve dependency matrisini tanımla`
2. `build(bff): Web ve Mobile modüllerini hazırla`
3. `feat(web-bff): dashboard composition akışını ekle`
4. `feat(mobile-bff): Buyer read composition akışını ekle`
5. `feat(bff): identity timeout ve partial degradation kurallarını ekle`
6. `test(bff): fan-out failure ve sahiplik sınırlarını doğrula`
7. `docs(bff): API sözleşmelerini ve sorumlulukları kaydet`

Sıra bu milestone’ın implementation commit’leri içindir. Her commit tek tutarlı davranış veya karar içerir; küçük komşu görevler inceleme açıklığı korunuyorsa birleştirilebilir. Koşullu özellik uygulanmazsa ilgili feat/test commit’i atlanır ve karar docs commit’inde gerekçelendirilir.

## Doğrulama ve kabul ölçütleri

- Web/Mobile response sözleşmeleri ayrılmış; frontend eklenmemiş.
- Canonical veri veya invariant sahipliği BFF’e taşınmamış.
- Parallel çağrılar bounded; partial failure açık ve doğrulanmış.

- Önce ilgili GitHub CI/build/test kontrolleri başarılı olur; ardından local runtime ve protokole uygun API/E2E senaryoları doğrulanır.
- Görevlerde belirtilen happy path yanında failure, concurrency/idempotency ve authorization sınırları gerçek adapter testleriyle doğrulanır.
- Test ve runtime evidence ilgili Day’e bağlanır; atlanan kontrol veya test açıkça belirtilir.
- ADR ve service belgeleri actual implementation ile hizalanır; Knowledge Base impact review yapılır ve canonical branch’e dokümantasyon senkronize edilir.
- `Infrastructure Ready`, `Implemented`, `Integrated`, `Verified` ve `Design Only` ayrı tutulur. Kanıt yoksa Verified yazılmaz.

## Kapsam dışı ve kapanış

Day 40 kapsamını aşan yeni teknolojiler/iş akışları sırf çeşitlilik için eklenmez. Docker/Kubernetes/CI/CD genişletmesi bu backend plan güncellemesinin parçası değildir. [Gün özeti](day-40-bff.md) ve [ana roadmap](../../ROADMAP.md) aynı kararları taşır. Day bir milestone’dır; tek takvim gününde bitme zorunluluğu yoktur.
