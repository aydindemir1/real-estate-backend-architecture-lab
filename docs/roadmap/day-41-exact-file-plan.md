# Day 41 — GraphQL Federation ve read composition: kesin uygulama ve commit planı

Durum: **Planlandı**. Hedef branch: `day/41-graphql-federation`.

## Önkoşullar ve ilerleme

Day 16 Search GraphQL API; Day 40 BFF ve mevcut Property/Agent read contracts.

Yeni Day branch’i bir önceki kapanmış Day branch’inden türetilir. `main` ve kapanmış eski Day branch’leri bu plan güncellemesiyle değiştirilmez. `docs/backend-roadmap-design` canonical plan ve cumulative dokümantasyonu tutar; uygulama kodu ilgili Day branch’inde geliştirilir.

## Kapsam ve karar sınırları

- Search GraphQL API korunarak Search subgraph’a evrilir; Property ve Agent read subgraph adapter’ları eklenir.
- Router yalnız schema composition/entity resolution/query planning yapar; business/canonical state sahibi değildir.
- Search yalnız Elasticsearch projection sahibi; Property canonical sahibi PropertyService’tir.
- BFF korunur; uygun composition-heavy read use-case’lerde graph kullanabilir; REST/gRPC kaldırılmaz.

## Dosya ve adapter planı

Aşağıdaki yollar uygulama başlangıcında mevcut package standardıyla kesinleştirilecek hedeflerdir. `...` placeholder’dır; literal package veya dosya adı değildir. Gerçek ihtiyaç yoksa boş sınıf/port oluşturulmaz.

- `SearchService/src/main/resources/graphql/`
- `PropertyService/src/main/resources/graphql/`
- `AgentService/src/main/resources/graphql/`
- `infra/graphql-federation/`
- `docs/architecture/graphql-federation.md`

Service modüllerinde build/config/test ve gerektiği kadar ROADMAP/DESIGN/PACKAGE-DESIGN belgeleri güncellenir. Tam sürüm ve runtime seçimi uyumluluk doğrulamasından sonra yapılır; bu plan yeni bir ürünün gereksiz eklenmesini zorunlu tutmaz.

## Bağımlılık sırasına göre görevler

1. Federated read use-case, entity key ve field ownership matrisini kilitle; iki servis aynı canonical field’ı sahiplenmesin.
2. Federation runtime ve Spring GraphQL entegrasyonunun uyumluluğunu milestone başlangıcında doğrula; sürümü gerekçeli seç.
3. Search schema/resolver’ı mevcut handler üzerinde subgraph’a evrilt.
4. PropertyService ve AgentService’e kendi read use-case’lerine delegasyon yapan subgraph adapter’ları ekle.
5. Router schema composition/config oluştur; composition failure’ı başlangıçta görünür kıl.
6. Entity resolution için batching/DataLoader uygula; N+1’i call-count testiyle doğrula.
7. Query depth/complexity ve fan-out/timeout budget sınırlarını belirle ve uygula.
8. Auth propagation ve field/resource authorization’ı owning service’te koru; başka kullanıcı erişimini test et.
9. Day 40 BFF’de yalnız seçilen uygun read akışını federated graph’a bağla; required/optional failure semantics’i koru.
10. Schema composition, entity resolution, N+1, limit aşımı, partial failure ve canonical ownership architecture testlerini yaz; ADR/Knowledge Base’i güncelle.

## Önerilen commit sırası

1. `docs(federation): entity ve field sahipliğini tanımla`
2. `build(federation): uyumlu runtime ve router temelini ekle`
3. `feat(search): mevcut GraphQL APIyi subgrapha evrilt`
4. `feat(federation): Property ve Agent read subgraphlarını ekle`
5. `feat(federation): batching auth ve query sınırlarını ekle`
6. `feat(bff): seçilmiş read use-case için federated graphı bağla`
7. `test(federation): N+1 partial failure ve ownership doğrula`
8. `docs(federation): schema ve işletim kararlarını kaydet`

Sıra bu milestone’ın implementation commit’leri içindir. Her commit tek tutarlı davranış veya karar içerir; küçük komşu görevler inceleme açıklığı korunuyorsa birleştirilebilir. Koşullu özellik uygulanmazsa ilgili feat/test commit’i atlanır ve karar docs commit’inde gerekçelendirilir.

## Doğrulama ve kabul ölçütleri

- Search API ve REST/gRPC baseline korunmuş.
- N+1, depth/complexity, auth ve fan-out sınırları testli.
- Router/BFF canonical state veya domain kuralı sahibi değil.

- Önce ilgili GitHub CI/build/test kontrolleri başarılı olur; ardından local runtime ve protokole uygun API/E2E senaryoları doğrulanır.
- Görevlerde belirtilen happy path yanında failure, concurrency/idempotency ve authorization sınırları gerçek adapter testleriyle doğrulanır.
- Test ve runtime evidence ilgili Day’e bağlanır; atlanan kontrol veya test açıkça belirtilir.
- ADR ve service belgeleri actual implementation ile hizalanır; Knowledge Base impact review yapılır ve canonical branch’e dokümantasyon senkronize edilir.
- `Infrastructure Ready`, `Implemented`, `Integrated`, `Verified` ve `Design Only` ayrı tutulur. Kanıt yoksa Verified yazılmaz.

## Kapsam dışı ve kapanış

Day 41 kapsamını aşan yeni teknolojiler/iş akışları sırf çeşitlilik için eklenmez. Docker/Kubernetes/CI/CD genişletmesi bu backend plan güncellemesinin parçası değildir. [Gün özeti](day-41-graphql-federation.md) ve [ana roadmap](../../ROADMAP.md) aynı kararları taşır. Day bir milestone’dır; tek takvim gününde bitme zorunluluğu yoktur.
