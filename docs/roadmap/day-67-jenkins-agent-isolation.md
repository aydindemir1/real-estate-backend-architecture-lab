# Day 67 — Jenkins agent ve credential isolation

**Durum: Planlandı.**

Build yetkilerini ve container erişimini kontrollü tutmak.

## Konular

- Ephemeral agents; credentials binding; least privilege; untrusted PR; Docker socket

## Kesin plan

Görevler, hedef dosyalar, commit sırası, hata senaryoları ve kapanış kriterleri için [Day 67 kesin planını](day-67-exact-file-plan.md) kullan.

Agent isolation host access riskini otomatik kaldırmaz; privileged build yalnız explicit trusted lab scope’ta.

Ortak araç kararları: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md).
