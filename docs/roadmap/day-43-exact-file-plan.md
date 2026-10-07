# Day 43 — Agency tenant isolation ve Multi-Tenancy: kesin uygulama ve commit planı

Durum: **Planlandı**. Hedef branch: `day/43-multi-tenancy`.

## Önkoşullar ve ilerleme

Day 14 identity/ownership; AgentService MySQL; Day 13 cache; Day 40–41 composition.

Yeni Day branch’i bir önceki kapanmış Day branch’inden türetilir. `main` ve kapanmış eski Day branch’leri bu plan güncellemesiyle değiştirilmez. `docs/backend-roadmap-design` canonical plan ve cumulative dokümantasyonu tutar; uygulama kodu ilgili Day branch’inde geliştirilir.

## Kapsam ve karar sınırları

- Agency, AgentService bounded context’i içinde domain entity; ayrı AgencyService açılmaz.
- Agent başlangıçta tek Agency’ye bağlı; tenant identity + membership üzerinden resolve edilir.
- İlk strateji application authorization + tenant-scoped query ile row-level isolation.
- Schema-per-tenant/database-per-tenant yalnız ADR karşılaştırması; bütün servisler zorla tenant-aware yapılmaz.
- AgencyAdmin own-tenant; cross-tenant System/Admin erişimi explicit privilege gerektirir.

## Dosya ve adapter planı

Aşağıdaki yollar uygulama başlangıcında mevcut package standardıyla kesinleştirilecek hedeflerdir. `...` placeholder’dır; literal package veya dosya adı değildir. Gerçek ihtiyaç yoksa boş sınıf/port oluşturulmaz.

- `AgentService/src/main/java/.../agency/`
- `AgentService/src/main/resources/db/migration/`
- `AgentService/src/test/java/.../`
- `docs/adr/day-43-tenant-isolation.md`
- `docs/security/tenant-authorization.md`

Service modüllerinde build/config/test ve gerektiği kadar ROADMAP/DESIGN/PACKAGE-DESIGN belgeleri güncellenir. Tam sürüm ve runtime seçimi uyumluluk doğrulamasından sonra yapılır; bu plan yeni bir ürünün gereksiz eklenmesini zorunlu tutmaz.

## Bağımlılık sırasına göre görevler

1. Agency-owned/scoped kaynakları, membership yaşam döngüsünü ve mevcut kayıtların migration politikasını belirle.
2. AgentService Agency entity/value types ve MySQL migration/indekslerini oluştur.
3. Authenticated identity üzerinden tenant context resolve et; serbest client header’ını authority olarak kabul etme.
4. Agency membership/AgencyAdmin authorization’ı application boundary’de uygula; explicit privileged cross-tenant erişimi ayır.
5. Persistence query ve identifier lookup’larını tenant-scoped yap; unscoped fallback bırakma.
6. Yalnız tenant-scoped use-case’lerde cache key namespace’e tenant ekle; eski cache key geçiş/temizliğini belirle.
7. BFF/GraphQL/internal call propagation’da doğrulanmış context taşı; owning service tekrar authorization yapar.
8. Background job/reconciliation için explicit tenant scope veya privileged system execution sınırı tanımla.
9. Cross-tenant IDOR, filtre bypass, cache leakage, yanlış tenant header, admin privilege ve composition/job boundary testlerini yaz.
10. Row-level/schema/database-per-tenant ADR’sini ve migration/rollback/Knowledge Base belgelerini güncelle.

## Önerilen commit sırası

1. `docs(tenant): Agency sahipliğini ve migration politikasını tanımla`
2. `feat(agent): Agency modeli ve migration ekle`
3. `feat(tenant): identity tabanlı context ve membership kontrolü ekle`
4. `feat(agent): tenant-scoped persistence sorgularını ekle`
5. `feat(tenant): cache composition ve job sınırlarını bağla`
6. `test(tenant): IDOR cache ve cross-tenant sızıntıyı doğrula`
7. `docs(tenant): isolation stratejisini ve ayrıcalık sınırlarını kaydet`

Sıra bu milestone’ın implementation commit’leri içindir. Her commit tek tutarlı davranış veya karar içerir; küçük komşu görevler inceleme açıklığı korunuyorsa birleştirilebilir. Koşullu özellik uygulanmazsa ilgili feat/test commit’i atlanır ve karar docs commit’inde gerekçelendirilir.

## Doğrulama ve kabul ölçütleri

- Tenant client tarafından keyfi seçilemiyor.
- Başka Agency verisi query/cache/BFF/GraphQL/job yoluyla sızmıyor.
- Privileged erişim açık; AgencyAdmin bütün tenant’lara erişemiyor.

- Önce ilgili GitHub CI/build/test kontrolleri başarılı olur; ardından local runtime ve protokole uygun API/E2E senaryoları doğrulanır.
- Görevlerde belirtilen happy path yanında failure, concurrency/idempotency ve authorization sınırları gerçek adapter testleriyle doğrulanır.
- Test ve runtime evidence ilgili Day’e bağlanır; atlanan kontrol veya test açıkça belirtilir.
- ADR ve service belgeleri actual implementation ile hizalanır; Knowledge Base impact review yapılır ve canonical branch’e dokümantasyon senkronize edilir.
- `Infrastructure Ready`, `Implemented`, `Integrated`, `Verified` ve `Design Only` ayrı tutulur. Kanıt yoksa Verified yazılmaz.

## Kapsam dışı ve kapanış

Day 43 kapsamını aşan yeni teknolojiler/iş akışları sırf çeşitlilik için eklenmez. Docker/Kubernetes/CI/CD genişletmesi bu backend plan güncellemesinin parçası değildir. [Gün özeti](day-43-multi-tenancy.md) ve [ana roadmap](../../ROADMAP.md) aynı kararları taşır. Day bir milestone’dır; tek takvim gününde bitme zorunluluğu yoktur.
