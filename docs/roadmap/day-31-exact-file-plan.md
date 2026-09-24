# Day 31 — Exact Spring Cloud Task + Reindex / Reconciliation Plan

## Scope
- Spring Cloud Task metadata
- single/full reindex
- versioned physical index + stable alias
- bounded batch/checkpoint
- reconciliation report/repair
- rollback/runbooks

## Tasks
1. Inventory finite maintenance tasks.
2. Add Spring Cloud Task to Search-owned maintenance capability; no generic TaskService microservice.
3. Configure Task metadata repository; never Elasticsearch.
4. Standard names: search-single-property-reindex, search-full-reindex, search-reconciliation.
5. Finalize canonical reindex source without direct cross-service Mongo access.
6. If needed, expose secured PropertyService internal/export read contract returning search source DTO, not persistence document.
7. Implement single-property reindex upsert.
8. Implement full rebuild into new physical index; never delete active index first.
9. Use physical name like `properties-v{sequence}` and stable alias `properties-read`.
10. Add `SearchIndexManager`: create, inspect alias, atomic switch, controlled rollback.
11. Use stable bounded cursor/keyset pagination from source.
12. Use bounded Elasticsearch bulk requests and detect partial failures.
13. Make reruns idempotent.
14. Store checkpoint/last successful cursor for resume.
15. On source/Elastic/mapping/bulk failure, mark task failed and do not switch alias.
16. Verify candidate index before cutover: counts + zero fatal bulk failures + sample validation + mapping/health.
17. Retain previous index temporarily for rollback.
18. Implement reconciliation: missing, stale version, wrong searchable state, optional orphan.
19. Default reconciliation mode report-only; repair requires explicit flag.
20. Repair missing/stale; delete orphan only with explicit repair policy.
21. Validate task parameters and secure source access with service identity.
22. Add structured task logs and low-cardinality task metrics.
23. Add tests for single reindex, full rebuild, failure-before-switch, checkpoint resume, report, repair, rerun idempotency and Task metadata.
24. Prevent concurrent full rebuilds from racing alias switch.
25. Add reindex and reconciliation runbooks.
26. Perform destructive local index-loss recovery exercise.

## Final gate
- no direct Property Mongo access
- active search never deleted before valid replacement
- alias switch atomic
- failed rebuild leaves old alias intact
- checkpoint/resume works
- report/repair explicit
- rerun idempotent
- rollback/runbooks tested
