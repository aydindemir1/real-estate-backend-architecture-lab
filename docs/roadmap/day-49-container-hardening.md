# Day 49 — Container güvenliği ve kaynak sınırları

**Durum: Planlandı.**

Non-root ve sınırlı runtime ile JVM/servis davranışını doğrulamak.

## Konular

- UID/GID; read-only filesystem; capabilities; signals; JVM/container memory

## Kesin plan

Görevler, hedef dosyalar, commit sırası, hata senaryoları ve kapanış kriterleri için [Day 49 kesin planını](day-49-exact-file-plan.md) kullan.

Container hardening service authorization yerine geçmez; distroless/Alpine/JRE varyantlarını paralel ürün olarak işletme.

Ortak araç kararları: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md).
