# Day 43 — Agency tenant isolation ve Multi-Tenancy

Durum: **Planlandı**. Branch: `day/43-multi-tenancy`.

## Amaç ve önkoşullar

Day 14 identity/ownership; AgentService MySQL; Day 13 cache; Day 40–41 composition.

## Kapsam ve sahiplik sınırları

- Agency, AgentService bounded context’i içinde domain entity; ayrı AgencyService açılmaz.
- Agent başlangıçta tek Agency’ye bağlı; tenant identity + membership üzerinden resolve edilir.
- İlk strateji application authorization + tenant-scoped query ile row-level isolation.
- Schema-per-tenant/database-per-tenant yalnız ADR karşılaştırması; bütün servisler zorla tenant-aware yapılmaz.
- AgencyAdmin own-tenant; cross-tenant System/Admin erişimi explicit privilege gerektirir.

## Görevler

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

## Kapanış ölçütleri

- Tenant client tarafından keyfi seçilemiyor.
- Başka Agency verisi query/cache/BFF/GraphQL/job yoluyla sızmıyor.
- Privileged erişim açık; AgencyAdmin bütün tenant’lara erişemiyor.

Ayrıntılı dosya/adapter planı, bağımlılık sırası, commit’ler ve doğrulama: [Day 43 kesin planı](day-43-exact-file-plan.md). Kapanış kanıtı oluşmadan bu capability tamamlandı veya Verified olarak işaretlenmez.
