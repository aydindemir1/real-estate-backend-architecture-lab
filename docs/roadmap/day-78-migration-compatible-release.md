# Day 78 — Migration uyumluluğu ve rollback/forward-fix

**Durum: Planlandı.**

Uygulama release’i ile veri değişikliğinin recovery sınırını ayırmak.

## Konular

- Expand/contract; old/new overlap; migration Jobs; reliable event schema; forward-fix

## Kesin plan

Görevler, hedef dosyalar, commit sırası, hata senaryoları ve kapanış kriterleri için [Day 78 kesin planını](day-78-exact-file-plan.md) kullan.

Canonical veri geri dönüşü image rollback ile eş tutulmaz; her datastore için aynı migration mekaniği varsayılmaz.

Ortak araç kararları: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md).
