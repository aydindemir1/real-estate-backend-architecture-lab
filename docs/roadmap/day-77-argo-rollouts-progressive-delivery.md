# Day 77 — Canary, blue-green ve Argo Rollouts

**Durum: Planlandı.**

Yeni sürüm etkisini trafik ve metrics ile sınırlayarak doğrulamak.

## Konular

- Rollout CRD; blue-green/canary; AnalysisTemplate; traffic weights; abort

## Kesin plan

Görevler, hedef dosyalar, commit sırası, hata senaryoları ve kapanış kriterleri için [Day 77 kesin planını](day-77-exact-file-plan.md) kullan.

Istio zorunlu traffic router olarak erkenden kurulmaz; seçilmiş Gateway API/provider desteği milestone başlangıcında test edilir.

Ortak araç kararları: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md).
