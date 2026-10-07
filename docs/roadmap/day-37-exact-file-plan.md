# Day 37 — Property Purchase Completion için Saga Orchestration: kesin uygulama ve commit planı

Durum: **Planlandı**. Hedef branch: `day/37-saga-orchestration`.

## Önkoşullar ve ilerleme

Day 22 choreography; Day 35–36 Offer write-side; accepted Offer iş gereksinimi.

Yeni Day branch’i bir önceki kapanmış Day branch’inden türetilir. `main` ve kapanmış eski Day branch’leri bu plan güncellemesiyle değiştirilmez. `docs/backend-roadmap-design` canonical plan ve cumulative dokümantasyonu tutar; uygulama kodu ilgili Day branch’inde geliştirilir.

## Kapsam ve karar sınırları

- Day 22 Offer/Reservation choreography korunur; bu yeni satın alma tamamlama akışıdır.
- Akış: accepted Offer → Contract → Escrow → Title Transfer → Property SOLD → Agent Commission.
- PurchaseProcessService yalnız saga/process state; ContractService sözleşme; EscrowService escrow/payment; TitleTransferService yasal devir yaşam döngüsünü sahiplenir.
- PropertyService Property lifecycle, AgentService commission entitlement/record sahibi olarak kalır.
- Compensatable/retryable/irreversible adımlar ayrılır; devir sonrası commission hatası global rollback gerektirmeyebilir.

## Dosya ve adapter planı

Aşağıdaki yollar uygulama başlangıcında mevcut package standardıyla kesinleştirilecek hedeflerdir. `...` placeholder’dır; literal package veya dosya adı değildir. Gerçek ihtiyaç yoksa boş sınıf/port oluşturulmaz.

- `PurchaseProcessService/`
- `ContractService/`
- `EscrowService/`
- `TitleTransferService/`
- `PropertyService/src/main/java/.../`
- `AgentService/src/main/java/.../`
- `docs/architecture/purchase-process.md`
- `docs/runbooks/purchase-reconciliation.md`

Service modüllerinde build/config/test ve gerektiği kadar ROADMAP/DESIGN/PACKAGE-DESIGN belgeleri güncellenir. Tam sürüm ve runtime seçimi uyumluluk doğrulamasından sonra yapılır; bu plan yeni bir ürünün gereksiz eklenmesini zorunlu tutmaz.

## Bağımlılık sırasına göre görevler

1. İş akışını, sahiplik matrisini, dış sistem simülasyon sınırını ve point-of-no-return adımını kilitle; gerçek ödeme/tapu entegrasyonu iddiasında bulunma.
2. PurchaseProcessService, ContractService, EscrowService ve TitleTransferService için minimal build/runtime ve servis sözleşmelerini oluştur.
3. Process state machine ve durable saga state persistence kararını gerçek sorgu/atomicity ihtiyacına göre ver; sırf çeşitlilik için datastore ekleme.
4. Her adımın command/result sözleşmesini ve processId/correlation/causation/idempotency kurallarını tanımla.
5. Orchestrator dispatch ve sonuç tüketimini mevcut reliable outbound/inbox altyapısına bağla.
6. Contract ve Escrow adımlarında success, rejection, timeout ve compensation davranışını uygula.
7. Title Transfer için irreversible tamamlanma sınırını belirle; Property SOLD ve commission kaydını doğru owner üzerinden bağla.
8. Crash/restart sonrası process devamı, bounded retry ve uzayan pending durum için reconciliation/operatör prosedürü oluştur.
9. Happy path, her adımın failure/compensation, duplicate/out-of-order result ve post-transfer commission failure E2E testlerini yaz.
10. Choreography vs Orchestration ADR’sini ve servislerin ROADMAP/DESIGN/Knowledge Base belgelerini güncelle.

## Önerilen commit sırası

1. `docs(saga): satın alma akışını ve compensation sınırlarını tanımla`
2. `build(purchase): süreç ve katılımcı servislerin temelini ekle`
3. `feat(saga): durable process state ve sözleşmeleri ekle`
4. `feat(purchase): Contract ve Escrow adımlarını bağla`
5. `feat(purchase): devir SOLD ve commission adımlarını bağla`
6. `feat(saga): restart retry ve reconciliation davranışını ekle`
7. `test(saga): compensation ve irreversible failure akışlarını doğrula`
8. `docs(saga): choreography karşılaştırmasını ve runbookları tamamla`

Sıra bu milestone’ın implementation commit’leri içindir. Her commit tek tutarlı davranış veya karar içerir; küçük komşu görevler inceleme açıklığı korunuyorsa birleştirilebilir. Koşullu özellik uygulanmazsa ilgili feat/test commit’i atlanır ve karar docs commit’inde gerekçelendirilir.

## Doğrulama ve kabul ölçütleri

- Orchestrator domain sahipliğini devralmıyor.
- Duplicate/result replay adımı iki kez uygulamıyor.
- Devir sonrası başarısızlık sahte global rollback ile gizlenmiyor; reconciliation doğrulanmış.

- Önce ilgili GitHub CI/build/test kontrolleri başarılı olur; ardından local runtime ve protokole uygun API/E2E senaryoları doğrulanır.
- Görevlerde belirtilen happy path yanında failure, concurrency/idempotency ve authorization sınırları gerçek adapter testleriyle doğrulanır.
- Test ve runtime evidence ilgili Day’e bağlanır; atlanan kontrol veya test açıkça belirtilir.
- ADR ve service belgeleri actual implementation ile hizalanır; Knowledge Base impact review yapılır ve canonical branch’e dokümantasyon senkronize edilir.
- `Infrastructure Ready`, `Implemented`, `Integrated`, `Verified` ve `Design Only` ayrı tutulur. Kanıt yoksa Verified yazılmaz.

## Kapsam dışı ve kapanış

Day 37 kapsamını aşan yeni teknolojiler/iş akışları sırf çeşitlilik için eklenmez. Docker/Kubernetes/CI/CD genişletmesi bu backend plan güncellemesinin parçası değildir. [Gün özeti](day-37-saga-orchestration.md) ve [ana roadmap](../../ROADMAP.md) aynı kararları taşır. Day bir milestone’dır; tek takvim gününde bitme zorunluluğu yoktur.
