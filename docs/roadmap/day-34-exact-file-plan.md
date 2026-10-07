# Day 34 — Anti-Corruption Layer ve koşullu Strangler Fig: kesin uygulama ve commit planı

Durum: **Planlandı**. Hedef branch: `day/34-acl-strangler-fig`.

## Önkoşullar ve ilerleme

Day 33 doğrulanmış backend baseline; ExternalMLS sözleşmesi ve mevcut import yolu envanteri.

Yeni Day branch’i bir önceki kapanmış Day branch’inden türetilir. `main` ve kapanmış eski Day branch’leri bu plan güncellemesiyle değiştirilmez. `docs/backend-roadmap-design` canonical plan ve cumulative dokümantasyonu tutar; uygulama kodu ilgili Day branch’inde geliştirilir.

## Kapsam ve karar sınırları

- ListingSyncService yalnız ExternalMLS entegrasyonunu ve çevirisini sahiplenir; PropertyService canonical Property sahibi olarak kalır.
- ExternalMLS modeli, internal canonical command/model ve integration event sözlüğünden ayrılır.
- Strangler Fig yalnız gerçekten mevcut bir legacy import yolu varsa etkinleşir; yeni adapter tek başına Strangler Fig değildir.

## Dosya ve adapter planı

Aşağıdaki yollar uygulama başlangıcında mevcut package standardıyla kesinleştirilecek hedeflerdir. `...` placeholder’dır; literal package veya dosya adı değildir. Gerçek ihtiyaç yoksa boş sınıf/port oluşturulmaz.

- `ListingSyncService/src/main/java/.../application/`
- `ListingSyncService/src/main/java/.../infrastructure/externalmls/`
- `ListingSyncService/src/test/java/.../`
- `docs/architecture/external-mls-integration.md`
- `docs/adr/day-34-acl-strangler.md`

Service modüllerinde build/config/test ve gerektiği kadar ROADMAP/DESIGN/PACKAGE-DESIGN belgeleri güncellenir. Tam sürüm ve runtime seçimi uyumluluk doğrulamasından sonra yapılır; bu plan yeni bir ürünün gereksiz eklenmesini zorunlu tutmaz.

## Bağımlılık sırasına göre görevler

1. ExternalMLS örnek sözleşmesini, kimlik eşlemesini, veri kaynağını ve import iş kuralını belgeleyerek kapsamı kilitle.
2. ListingSyncService modülünü, build/config ve uygulama başlangıcını oluştur; domain/persistence seçimini gerçek ihtiyaçla gerekçelendir.
3. ExternalMlsClientPort, dış model DTO’ları ve HTTP adapter’ını oluştur; dış tipleri adapter sınırında tut.
4. CanonicalListingCommand ve çeviri bileşenini oluştur; ExternalMLS durum, para birimi ve zorunlu alanlarını internal modele dönüştür.
5. Kaynak kayıt kimliği + sürüm ile tekrar gönderim ve eski sürüm politikasını belirle; mevcut reliable outbound/inbox kurallarını kullan.
6. PropertyService’e erişimi mevcut command/application sınırı üzerinden yap; servisler arası veritabanı erişimi ekleme.
7. Timeout, authentication, geçersiz veri ve dış sözleşme sürüm uyuşmazlığını kararlı hatalara çevir.
8. Legacy yol gerçekten varsa routing/cutover ve geri dönüş planı oluştur; yoksa Strangler Fig durumunu koşullu/uygulanmadı olarak kaydet.
9. Çeviri birim testleri, dış contract adapter testleri, duplicate/stale kayıt ve dış sistem kesinti testlerini ekle.
10. ArchUnit ile ExternalMLS DTO’larının Property domain’e sızmadığını doğrula; ADR ve Knowledge Base etkisini güncelle.

## Önerilen commit sırası

1. `docs(acl): ExternalMLS sözleşmesini ve sahiplik sınırını tanımla`
2. `build(listing-sync): entegrasyon modülünü hazırla`
3. `feat(acl): dış sistem portunu ve canonical çeviriyi ekle`
4. `feat(listing-sync): güvenilir import akışını bağla`
5. `test(acl): çeviri sürüm tekrar ve kesinti senaryolarını doğrula`
6. `feat(strangler): mevcut legacy yol varsa kademeli geçiş ekle`
7. `docs(acl): tasarım kararlarını ve gerçek capability durumunu kaydet`

Sıra bu milestone’ın implementation commit’leri içindir. Her commit tek tutarlı davranış veya karar içerir; küçük komşu görevler inceleme açıklığı korunuyorsa birleştirilebilir. Koşullu özellik uygulanmazsa ilgili feat/test commit’i atlanır ve karar docs commit’inde gerekçelendirilir.

## Doğrulama ve kabul ölçütleri

- Dış sözlük domain’e sızmıyor; Property sahipliği korunuyor.
- Import tekrarları güvenli; dış sistem hataları sınırlandırılmış.
- Strangler Fig iddiası gerçek legacy yol ve geçiş kanıtına dayanıyor.

- Önce ilgili GitHub CI/build/test kontrolleri başarılı olur; ardından local runtime ve protokole uygun API/E2E senaryoları doğrulanır.
- Görevlerde belirtilen happy path yanında failure, concurrency/idempotency ve authorization sınırları gerçek adapter testleriyle doğrulanır.
- Test ve runtime evidence ilgili Day’e bağlanır; atlanan kontrol veya test açıkça belirtilir.
- ADR ve service belgeleri actual implementation ile hizalanır; Knowledge Base impact review yapılır ve canonical branch’e dokümantasyon senkronize edilir.
- `Infrastructure Ready`, `Implemented`, `Integrated`, `Verified` ve `Design Only` ayrı tutulur. Kanıt yoksa Verified yazılmaz.

## Kapsam dışı ve kapanış

Day 34 kapsamını aşan yeni teknolojiler/iş akışları sırf çeşitlilik için eklenmez. Docker/Kubernetes/CI/CD genişletmesi bu backend plan güncellemesinin parçası değildir. [Gün özeti](day-34-acl-strangler-fig.md) ve [ana roadmap](../../ROADMAP.md) aynı kararları taşır. Day bir milestone’dır; tek takvim gününde bitme zorunluluğu yoktur.
