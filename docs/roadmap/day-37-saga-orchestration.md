# Day 37 — Property Purchase Completion için Saga Orchestration

Durum: **Planlandı**. Branch: `day/37-saga-orchestration`.

## Amaç ve önkoşullar

Day 22 choreography; Day 35–36 Offer write-side; accepted Offer iş gereksinimi.

## Kapsam ve sahiplik sınırları

- Day 22 Offer/Reservation choreography korunur; bu yeni satın alma tamamlama akışıdır.
- Akış: accepted Offer → Contract → Escrow → Title Transfer → Property SOLD → Agent Commission.
- PurchaseProcessService yalnız saga/process state; ContractService sözleşme; EscrowService escrow/payment; TitleTransferService yasal devir yaşam döngüsünü sahiplenir.
- PropertyService Property lifecycle, AgentService commission entitlement/record sahibi olarak kalır.
- Compensatable/retryable/irreversible adımlar ayrılır; devir sonrası commission hatası global rollback gerektirmeyebilir.

## Görevler

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

## Kapanış ölçütleri

- Orchestrator domain sahipliğini devralmıyor.
- Duplicate/result replay adımı iki kez uygulamıyor.
- Devir sonrası başarısızlık sahte global rollback ile gizlenmiyor; reconciliation doğrulanmış.

Ayrıntılı dosya/adapter planı, bağımlılık sırası, commit’ler ve doğrulama: [Day 37 kesin planı](day-37-exact-file-plan.md). Kapanış kanıtı oluşmadan bu capability tamamlandı veya Verified olarak işaretlenmez.
