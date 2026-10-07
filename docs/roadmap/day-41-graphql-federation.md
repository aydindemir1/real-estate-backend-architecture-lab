# Day 41 — GraphQL Federation ve read composition

Durum: **Planlandı**. Branch: `day/41-graphql-federation`.

## Amaç ve önkoşullar

Day 16 Search GraphQL API; Day 40 BFF ve mevcut Property/Agent read contracts.

## Kapsam ve sahiplik sınırları

- Search GraphQL API korunarak Search subgraph’a evrilir; Property ve Agent read subgraph adapter’ları eklenir.
- Router yalnız schema composition/entity resolution/query planning yapar; business/canonical state sahibi değildir.
- Search yalnız Elasticsearch projection sahibi; Property canonical sahibi PropertyService’tir.
- BFF korunur; uygun composition-heavy read use-case’lerde graph kullanabilir; REST/gRPC kaldırılmaz.

## Görevler

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

## Kapanış ölçütleri

- Search API ve REST/gRPC baseline korunmuş.
- N+1, depth/complexity, auth ve fan-out sınırları testli.
- Router/BFF canonical state veya domain kuralı sahibi değil.

Ayrıntılı dosya/adapter planı, bağımlılık sırası, commit’ler ve doğrulama: [Day 41 kesin planı](day-41-exact-file-plan.md). Kapanış kanıtı oluşmadan bu capability tamamlandı veya Verified olarak işaretlenmez.
