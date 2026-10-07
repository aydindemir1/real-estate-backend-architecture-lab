# Day 31 — Kesin Spring Cloud Task + Reindex / Reconciliation Planı

## Kapsam
- Spring Cloud Task metadata
- single/full reindex
- versioned physical index + stable alias
- bounded batch/checkpoint
- reconciliation report/repair
- rollback/runbook'lar

## Task'ler
1. Finite maintenance task'lerini inventory et.
2. Spring Cloud Task'i Search-owned maintenance capability'ye ekle; generic TaskService microservice oluşturma.
3. Task metadata repository configure et; Elasticsearch kullanma.
4. Standard isimler: search-single-property-reindex, search-full-reindex, search-reconciliation.
5. Direct cross-service Mongo access olmadan canonical reindex source'u finalize et.
6. Gerekirse persistence document değil search source DTO döndüren secured PropertyService internal/export read contract expose et.
7. Single-property reindex upsert implemente et.
8. Full rebuild'i yeni physical index'e yap; aktif index'i önce silme.
9. `properties-v{sequence}` gibi physical name ve stable alias `properties-read` kullan.
10. `SearchIndexManager` ekle: create, inspect alias, atomic switch, controlled rollback.
11. Source'tan stable bounded cursor/keyset pagination kullan.
12. Bounded Elasticsearch bulk request kullan ve partial failure'ları tespit et.
13. Rerun'ları idempotent yap.
14. Resume için checkpoint/last successful cursor sakla.
15. Source/Elastic/mapping/bulk failure durumunda task'i failed işaretle ve alias switch yapma.
16. Cutover öncesi candidate index'i doğrula: counts + zero fatal bulk failures + sample validation + mapping/health.
17. Rollback için previous index'i geçici olarak tut.
18. Reconciliation implemente et: missing, stale version, wrong searchable state, opsiyonel orphan.
19. Default reconciliation mode report-only; repair explicit flag gerektirir.
20. Missing/stale kayıtları repair et; orphan yalnızca explicit repair policy ile delete edilir.
21. Task parameter'larını validate et ve source access'i service identity ile secure et.
22. Structured task log ve low-cardinality task metric'leri ekle.
23. Single reindex, full rebuild, failure-before-switch, checkpoint resume, report, repair, rerun idempotency ve Task metadata testleri ekle.
24. Concurrent full rebuild'lerin alias switch için race etmesini engelle.
25. Reindex ve reconciliation runbook'ları ekle.
26. Destructive local index-loss recovery exercise yap.

## Final gate
- direct Property Mongo access yok
- valid replacement oluşmadan active search silinmiyor
- alias switch atomic
- failed rebuild eski alias'ı koruyor
- checkpoint/resume çalışıyor
- report/repair explicit
- rerun idempotent
- rollback/runbook'lar test edilmiş
