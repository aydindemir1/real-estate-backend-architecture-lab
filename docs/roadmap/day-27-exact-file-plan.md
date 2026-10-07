# Day 27 — Kesin Contract Testing Planı

## Kapsam
- seçilmiş REST boundary'leri için Spring Cloud Contract
- provider verification
- generated stub'lar/consumer testleri
- Kafka event compatibility fixture'ları
- gRPC protobuf compatibility

## Task'ler
1. `docs/contracts/contract-test-inventory.md` oluştur.
2. Spring Cloud Contract için yalnızca anlamlı cross-service REST boundary'lerini seç.
3. Plugin/dependency'leri yalnızca seçilen provider'lara ekle.
4. Capability bazında organize edilmiş standard contract directory tanımla.
5. Seçilmiş Agent/Property/diğer gerçek service boundary'leri için provider contract'ları ekle.
6. Controller/application boundary'ye karşı provider verification testleri generate et.
7. Local stub artifact'ları generate et.
8. Uygun olduğu yerde gerçek REST consumer/OpenFeign client'ları stub'lara karşı doğrula.
9. REST backward compatibility'yi dokümante et: additive optional field kabul; rename/remove/type change breaking.
10. Kafka event schema ve version'larını inventory et.
11. Önceki JSON fixture'larını `src/test/resources/events/v1` altında sakla.
12. Mevcut kodun desteklenen önceki fixture'ları deserialize edebildiğini test et.
13. Unsupported schemaVersion'ın safe non-retryable path'e gittiğini test et.
14. Protobuf compatibility check ekle: field number'ları asla reuse etme; kaldırılan number'ları reserve et; additive field tercih et.
15. Request/response/status mapping için Buyer↔Agent gRPC contract testi ekle.
16. Stable critical API error contract'larını koru.
17. Contract-test Gradle/CI-ready task/report ekle.

## Commit sırası
1. `docs(contract): inventory critical service contracts`
2. `build(contract): add Spring Cloud Contract to selected providers`
3. `test(contract): add provider contracts and verification`
4. `build(contract): generate stub artifacts`
5. `test(contract): verify REST consumers against stubs`
6. `test(contract): add Kafka compatibility fixtures`
7. `test(contract): add protobuf compatibility checks`
8. `docs(contract): define backward-compatible contract evolution`

## Final gate
- contract'lar internal class'ları değil service boundary'lerini korur
- seçilmiş provider'lar doğrulanır
- ilgili yerlerde consumer stub'ları çalışır
- desteklenen önceki Kafka event fixture compatible
- protobuf evolution rule'ları enforce edilir
