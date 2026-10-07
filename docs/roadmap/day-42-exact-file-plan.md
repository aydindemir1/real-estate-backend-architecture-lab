# Day 42 — Spring Batch ile raporlama ve stale listing tespiti: kesin uygulama ve commit planı

Durum: **Planlandı**. Hedef branch: `day/42-spring-batch`.

## Önkoşullar ve ilerleme

Day 33 backend; Day 31 Cloud Task sorumluluk sınırı.

Yeni Day branch’i bir önceki kapanmış Day branch’inden türetilir. `main` ve kapanmış eski Day branch’leri bu plan güncellemesiyle değiştirilmez. `docs/backend-roadmap-design` canonical plan ve cumulative dokümantasyonu tutar; uygulama kodu ilgili Day branch’inde geliştirilir.

## Kapsam ve karar sınırları

- Ana use-case market statistics reporting; gerçek Job/Step/ItemReader/ItemProcessor/ItemWriter/chunk semantics uygulanır.
- JobRepository, restartability/checkpoint, bounded retry/skip, job parameters ve idempotent rerun kapsamdadır.
- Stale listing önce tespit/candidate processing; otomatik withdraw yalnız açık iş politikası varsa.
- Spring Batch processing ile Spring Cloud Task kısa ömürlü execution/orchestration ayrı sorumluluklardır.

## Dosya ve adapter planı

Aşağıdaki yollar uygulama başlangıcında mevcut package standardıyla kesinleştirilecek hedeflerdir. `...` placeholder’dır; literal package veya dosya adı değildir. Gerçek ihtiyaç yoksa boş sınıf/port oluşturulmaz.

- `İhtiyaçla seçilen reporting modülü/src/main/java/.../batch/`
- `İhtiyaçla seçilen reporting modülü/src/test/java/.../`
- `docs/adr/day-42-batch-task-boundary.md`
- `docs/runbooks/market-report-job.md`

Service modüllerinde build/config/test ve gerektiği kadar ROADMAP/DESIGN/PACKAGE-DESIGN belgeleri güncellenir. Tam sürüm ve runtime seçimi uyumluluk doğrulamasından sonra yapılır; bu plan yeni bir ürünün gereksiz eklenmesini zorunlu tutmaz.

## Bağımlılık sırasına göre görevler

1. Rapor tüketicisi, tarih aralığı, kaynak verinin consistency/freshness sınırı ve çıktı sahipliğini belirle.
2. Job’un hangi mevcut modülde veya gerçekten gerekli reporting modülünde yaşadığını gerekçelendir; yeni scheduler/datastore zorunlu tutma.
3. Spring Batch bağımlılık/config ve JobRepository execution metadata persistence kararını oluştur.
4. Reader/Processor/Writer sınırlarını, chunk büyüklüğünü ve pagination yaklaşımını seç.
5. Job parameters için benzersizlik/aynı işin tekrar çalışması ve rapor idempotency politikasını uygula.
6. Transient error retry ve bozuk item skip sınırlarını uygula; yanlış veriyi sessizce yutma.
7. Checkpoint/restart sonrası tamamlanmış chunk’ların etkilerini iki kez üretmeyen recovery akışı kur.
8. Stale listing candidate üret; domain-state mutation yapma, otomatik withdraw için ayrı açık business policy şartını koru.
9. Job success/failure/restart, aynı parameter rerun, skip limit ve candidate-only behavior testlerini ekle.
10. Cloud Task/Batch sorumluluk ADR’sini ve rapor freshness/runbook/Knowledge Base belgelerini güncelle.

## Önerilen commit sırası

1. `docs(batch): rapor kaynağını ve Job sahipliğini tanımla`
2. `build(batch): Batch ve execution metadata temelini ekle`
3. `feat(batch): reader processor writer ve chunk akışını ekle`
4. `feat(batch): idempotent rerun restart ve bounded retry ekle`
5. `feat(batch): stale listing candidate raporunu ekle`
6. `test(batch): restart skip rerun ve domain sınırını doğrula`
7. `docs(batch): Cloud Task ayrımını ve runbooku kaydet`

Sıra bu milestone’ın implementation commit’leri içindir. Her commit tek tutarlı davranış veya karar içerir; küçük komşu görevler inceleme açıklığı korunuyorsa birleştirilebilir. Koşullu özellik uygulanmazsa ilgili feat/test commit’i atlanır ve karar docs commit’inde gerekçelendirilir.

## Doğrulama ve kabul ölçütleri

- Gerçek Batch Job/Step/chunk execution doğrulanmış.
- Restart/rerun duplicate output üretmiyor.
- Stale listing tespiti açık politika olmadan domain state değiştirmiyor.

- Önce ilgili GitHub CI/build/test kontrolleri başarılı olur; ardından local runtime ve protokole uygun API/E2E senaryoları doğrulanır.
- Görevlerde belirtilen happy path yanında failure, concurrency/idempotency ve authorization sınırları gerçek adapter testleriyle doğrulanır.
- Test ve runtime evidence ilgili Day’e bağlanır; atlanan kontrol veya test açıkça belirtilir.
- ADR ve service belgeleri actual implementation ile hizalanır; Knowledge Base impact review yapılır ve canonical branch’e dokümantasyon senkronize edilir.
- `Infrastructure Ready`, `Implemented`, `Integrated`, `Verified` ve `Design Only` ayrı tutulur. Kanıt yoksa Verified yazılmaz.

## Kapsam dışı ve kapanış

Day 42 kapsamını aşan yeni teknolojiler/iş akışları sırf çeşitlilik için eklenmez. Docker/Kubernetes/CI/CD genişletmesi bu backend plan güncellemesinin parçası değildir. [Gün özeti](day-42-spring-batch.md) ve [ana roadmap](../../ROADMAP.md) aynı kararları taşır. Day bir milestone’dır; tek takvim gününde bitme zorunluluğu yoktur.
