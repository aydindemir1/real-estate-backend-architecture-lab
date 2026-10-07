# Day 55 — Probes, rollout ve graceful shutdown

**Durum: Planlandı.**

Yeni sürüme geçerken trafik ve mesaj correctness korumak.

## Konular

- Startup/readiness/liveness; terminationGracePeriod; draining; consumer shutdown

## Kesin plan

Görevler, hedef dosyalar, commit sırası, hata senaryoları ve kapanış kriterleri için [Day 55 kesin planını](day-55-exact-file-plan.md) kullan.

Readiness başarı yanıtı business correctness veya bütün dependency’lerin sağlığı anlamına gelmez.

Ortak araç kararları: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md).
