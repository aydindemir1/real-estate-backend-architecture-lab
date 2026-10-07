# Day 40 — Web ve Mobile için Backend-for-Frontend

Durum: **Planlandı**. Branch: `day/40-bff`.

## Amaç ve önkoşullar

Day 33 backend contracts ve Day 14 identity/service credentials baseline.

## Kapsam ve sahiplik sınırları

- Ayrı deployable WebBffService ve MobileBffService oluşturulur; frontend yazılmaz.
- Web: Agent/Seller dashboard composition; Mobile: Buyer odaklı hafif composition.
- Gateway edge/platform, BFF client-specific composition sorumluluğunu taşır.
- BFF canonical state/invariant sahibi değildir; mümkün olduğunca stateless; yeni canonical datastore yok.
- GraphQL Federation entegrasyonu Day 41’e bırakılır.

## Görevler

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

## Kapanış ölçütleri

- Web/Mobile response sözleşmeleri ayrılmış; frontend eklenmemiş.
- Canonical veri veya invariant sahipliği BFF’e taşınmamış.
- Parallel çağrılar bounded; partial failure açık ve doğrulanmış.

Ayrıntılı dosya/adapter planı, bağımlılık sırası, commit’ler ve doğrulama: [Day 40 kesin planı](day-40-exact-file-plan.md). Kapanış kanıtı oluşmadan bu capability tamamlandı veya Verified olarak işaretlenmez.
