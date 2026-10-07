# Day 65 — IaC drift, state ve lab yeniden oluşturma

**Durum: Planlandı.**

Provisioning/config/manifest katmanlarının recovery ve ownership ilişkisini kanıtlamak.

## Konular

- Drift detection; import/state; state recovery; idempotent rebuild; ownership matrix

## Kesin plan

Görevler, hedef dosyalar, commit sırası, hata senaryoları ve kapanış kriterleri için [Day 65 kesin planını](day-65-exact-file-plan.md) kullan.

Local lock/file state davranışı distributed team backend garantisi olarak sunulmaz; ücretli SaaS state gerekmez.

Ortak araç kararları: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md).
