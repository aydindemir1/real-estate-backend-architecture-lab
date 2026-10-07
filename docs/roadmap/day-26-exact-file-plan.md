# Day 26 — Kesin Testing Hardening Planı

## Kapsam
- test inventory/taxonomy
- fast vs integration lifecycle
- Testcontainers governance
- coverage-gap closure
- security/messaging/concurrency hardening
- deterministic/flaky-test policy
- CI-ready Gradle task'leri

## Task'ler
1. Module ve test type bazında `docs/testing/test-inventory.md` oluştur.
2. Kategorileri tanımla: unit, application, slice, integration, architecture, contract, e2e.
3. Ayrı `integrationTest` source set/task veya eşdeğer JUnit-tag lifecycle seç.
4. `test` task'ini Docker-free ve hızlı tut.
5. Hızlıysa architecture testlerini verification lifecycle'a bağla.
6. Testcontainers version governance'ı centralize et.
7. Per-module valid-default test factory'lerini standardize et.
8. Time-sensitive `Instant.now()` davranışını injected/fixed `Clock` ile değiştir.
9. Kritik Agent/Buyer/Seller/Property/Search coverage gap'lerini kapat.
10. Authorization matrix test coverage'ını tamamla.
11. Her side-effecting consumer'ın duplicate-delivery testine sahip olduğundan emin ol.
12. Retryable/non-retryable messaging path'lerinin test edildiğinden emin ol.
13. `docs/testing/concurrency-test-matrix.md` oluştur.
14. Property optimistic locking, concurrent Offer hold, Idempotency-Key ve dispatcher duplicate senaryolarını automate et.
15. Awaitility/bounded polling'i standardize et; arbitrary `Thread.sleep` kullanımını kaldır.
16. Flaky testleri audit et/düzelt: timing, shared state, order, port collision, container startup, clock/random.
17. Her technology için datastore cleanup yaklaşımını standardize et.
18. Kafka/RabbitMQ integration test state'ini isolate et.
19. Service-specific rule'ları gizlemeden ArchUnit testlerini consolidate et.
20. Yararlıysa coverage-gap signal olarak JaCoCo ekle; arbitrary global coverage vanity target koyma.
21. CI stage'lerini ve targeted Gradle command'larını dokümante et.
22. Real Vault secret'larını ordinary test'lerden uzak tut.

## Commit sırası
1. `test: inventory current suites and gaps`
2. `build(test): separate integration test lifecycle`
3. `build(test): centralize Testcontainers versions`
4. `test: standardize factories clock and async assertions`
5. `test(<service>): close critical coverage gaps`
6. `test(security): complete authorization matrix coverage`
7. `test(messaging): complete duplicate and retry coverage`
8. `test(concurrency): harden race-condition coverage`
9. `test: eliminate flaky tests`
10. `test(integration): standardize infrastructure cleanup`
11. `test(architecture): consolidate fitness suites`
12. `docs(test): define CI test stages and commands`

## Final gate
- fast testler Docker gerektirmez
- integration testleri gerçek infrastructure kullanır
- concurrency/idempotency/security kritik path'leri otomatik test edilir
- async testler deterministic
- bilinen flaky testler düzeltilmiş veya açıkça görünür
- CI-ready test task'leri dokümante edilmiş
