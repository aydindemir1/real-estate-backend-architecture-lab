# Day 25 — Spring Cloud Task + Reindex / Reconciliation

## Goal
Finite maintenance/recovery job'larını kontrollü ve rerunnable hale getirmek.

## Tasks
1. Task dependency/setup ekle.
2. task execution metadata modelini ekle.
3. single-property reindex use-case oluştur.
4. full index rebuild task oluştur.
5. Mongo -> Elasticsearch reconciliation task oluştur.
6. idempotent rerun semantics ekle.
7. batch/pagination strategy ekle.
8. failure/resume policy ekle.
9. task metrics/logging ekle.
10. reindex alias-switch strategy uygula.
11. integration tests yaz.
12. runbook oluştur.

## Suggested commits
1. build(task): add Spring Cloud Task
2. feat(search): add single-property reindex task
3. feat(search): add full index rebuild
4. feat(search): add reconciliation task
5. feat(search): add safe alias-switch workflow
6. test(task): add rerun and failure tests
7. docs(task): add reindex and reconciliation runbook

## Done
Search recovery task'ları idempotent, observable ve controlled şekilde çalışır.
