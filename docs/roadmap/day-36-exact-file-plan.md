# Day 36 — Event Sourcing replay, audit ve koşullu snapshot: kesin uygulama ve commit planı

Durum: **Planlandı**. Hedef branch: `day/36-event-sourcing-replay`.

## Önkoşullar ve ilerleme

Day 35 event store ve deterministik rebuild doğrulanmış olmalı.

Yeni Day branch’i bir önceki kapanmış Day branch’inden türetilir. `main` ve kapanmış eski Day branch’leri bu plan güncellemesiyle değiştirilmez. `docs/backend-roadmap-design` canonical plan ve cumulative dokümantasyonu tutar; uygulama kodu ilgili Day branch’inde geliştirilir.

## Kapsam ve karar sınırları

- Audit/history read API ham event store’u public API olarak açmaz.
- Snapshot bir optimizasyondur; stream uzunluğu/performance ihtiyacı olmadan production default değildir.
- Mevcut reliable outbound yolu korunur; replay integration event’lerini yeniden yayınlamaz.

## Dosya ve adapter planı

Aşağıdaki yollar uygulama başlangıcında mevcut package standardıyla kesinleştirilecek hedeflerdir. `...` placeholder’dır; literal package veya dosya adı değildir. Gerçek ihtiyaç yoksa boş sınıf/port oluşturulmaz.

- `BuyerService/src/main/java/.../offer/replay/`
- `BuyerService/src/main/java/.../offer/history/`
- `BuyerService/src/test/java/.../offer/`
- `docs/runbooks/offer-replay.md`
- `docs/adr/day-36-offer-snapshot.md`

Service modüllerinde build/config/test ve gerektiği kadar ROADMAP/DESIGN/PACKAGE-DESIGN belgeleri güncellenir. Tam sürüm ve runtime seçimi uyumluluk doğrulamasından sonra yapılır; bu plan yeni bir ürünün gereksiz eklenmesini zorunlu tutmaz.

## Bağımlılık sırasına göre görevler

1. Full replay ile current state karşılaştırmasının veri setini ve doğrulama ölçütlerini tanımla.
2. Aggregate rebuild doğrulama aracını mevcut uygulama sınırında oluştur; bozuk stream’i sessizce düzeltme.
3. Audit/history response modelini oluştur; pagination, field redaction ve ownership authorization uygula.
4. Eksik/tekrarlı sıra, tanınmayan event sürümü ve corruption için açık hata/runbook politikası belirle.
5. Event schema değişimi varsa eski stream’in okunma uyumluluğunu test et; migration/upcasting kararını ihtiyaçla ver.
6. Snapshot ihtiyacını mevcut stream uzunluğu ve rebuild süresiyle değerlendir; gerekmiyorsa uygulanmadığını belgeleyerek bırak.
7. Snapshot seçilirse snapshot version + kalan event’lerden rebuild yap; eski/bozuk snapshot için full replay fallback tanımla.
8. Snapshot sonucu ile full replay sonucunun eşitliğini, yanlış aggregate/version ve concurrency sınırlarını test et.
9. Audit API için başka kullanıcı, yetkisiz rol, eksik aggregate ve pagination testlerini ekle.
10. Replay prosedürünü, veri kaybı sınırlarını ve Knowledge Base etkisini belgeleyerek actual status kaydet.

## Önerilen commit sırası

1. `docs(replay): full replay ve audit doğrulama planını tanımla`
2. `feat(offer): rebuild doğrulama akışını ekle`
3. `feat(audit): yetkili Offer history API ekle`
4. `test(replay): corruption ve eski event uyumluluğunu doğrula`
5. `feat(snapshot): ölçüm gerekçesi varsa snapshot optimizasyonu ekle`
6. `test(snapshot): uygulanmışsa full replay eşitliğini doğrula`
7. `docs(replay): recovery prosedürünü ve snapshot kararını kaydet`

Sıra bu milestone’ın implementation commit’leri içindir. Her commit tek tutarlı davranış veya karar içerir; küçük komşu görevler inceleme açıklığı korunuyorsa birleştirilebilir. Koşullu özellik uygulanmazsa ilgili feat/test commit’i atlanır ve karar docs commit’inde gerekçelendirilir.

## Doğrulama ve kabul ölçütleri

- Full replay deterministik ve yan etkisiz.
- Audit API ownership ve hassas veri sınırlarını koruyor.
- Snapshot uygulanmışsa full replay ile eşit; uygulanmamışsa durum açık.

- Önce ilgili GitHub CI/build/test kontrolleri başarılı olur; ardından local runtime ve protokole uygun API/E2E senaryoları doğrulanır.
- Görevlerde belirtilen happy path yanında failure, concurrency/idempotency ve authorization sınırları gerçek adapter testleriyle doğrulanır.
- Test ve runtime evidence ilgili Day’e bağlanır; atlanan kontrol veya test açıkça belirtilir.
- ADR ve service belgeleri actual implementation ile hizalanır; Knowledge Base impact review yapılır ve canonical branch’e dokümantasyon senkronize edilir.
- `Infrastructure Ready`, `Implemented`, `Integrated`, `Verified` ve `Design Only` ayrı tutulur. Kanıt yoksa Verified yazılmaz.

## Kapsam dışı ve kapanış

Day 36 kapsamını aşan yeni teknolojiler/iş akışları sırf çeşitlilik için eklenmez. Docker/Kubernetes/CI/CD genişletmesi bu backend plan güncellemesinin parçası değildir. [Gün özeti](day-36-event-sourcing-replay.md) ve [ana roadmap](../../ROADMAP.md) aynı kararları taşır. Day bir milestone’dır; tek takvim gününde bitme zorunluluğu yoktur.
