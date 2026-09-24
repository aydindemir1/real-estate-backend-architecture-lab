# Day 25 — Exact Spring Cloud Task / Reindex / Reconciliation / Commit Plan

## 0. Scope

Day 25 finite maintenance/recovery jobs içindir.

Hedef:
- Spring Cloud Task
- single-property reindex
- full Elasticsearch rebuild
- Mongo -> Elasticsearch reconciliation
- idempotent rerun
- bounded batch/pagination
- failure/resume semantics
- safe alias switch
- task metrics/logging
- integration tests
- runbook

Day 25 içinde:
- long-running streaming job yok
- Spring Batch zorunlu değil
- Kubernetes CronJob yok
- production scheduler/orchestrator yok

## Task 1 — Task use-case inventory

Create/update:
- docs/operations/maintenance-task-inventory.md

Tasks:
- single-property reindex
- full search index rebuild
- projection reconciliation
- optional cache warm only if real need

Each task must be finite and rerunnable.

Commit: docs(task): inventory finite maintenance tasks

## Task 2 — Spring Cloud Task dependency

Add to SearchService or dedicated maintenance module.

Decision:
Prefer SearchService-owned maintenance task capability unless a dedicated task module clearly improves isolation.

Do not create generic TaskService microservice.

Commit: build(task): add Spring Cloud Task

## Task 3 — Task metadata repository

Configure Spring Cloud Task metadata store.

Preferred:
- relational metadata store if current stack supports cleanly

If no suitable shared relational DB is desirable, evaluate lightweight dedicated local PostgreSQL schema.

Do not store Task metadata in Elasticsearch.

Commit: config(task): configure task metadata repository

## Task 4 — Task naming convention

Names:
- search-single-property-reindex
- search-full-reindex
- search-reconciliation

Task execution params recorded.

## Task 5 — SinglePropertyReindex command

Create:
- task/reindex/SinglePropertyReindexTask.java

Input:
- propertyId

Flow:
1. load canonical Property from source API/approved read path
2. map to Search projection
3. upsert Elasticsearch document
4. record success

Important:
SearchService must not read Property MongoDB directly across service boundary if service autonomy policy forbids it.

Preferred recovery source:
- PropertyService internal API or replayable event/source export designed explicitly.

Commit: feat(search): add single-property reindex task

## Task 6 — Reindex source strategy

Finalize one explicit source:
- PropertyService paged internal read API
or
- dedicated export endpoint
or
- event replay if complete historical event log is sufficient

Do not silently bypass service ownership with direct cross-service Mongo access.

Commit: docs(task): finalize reindex source strategy

## Task 7 — Property export/read contract

If PropertyService API is chosen, create internal read contract:
- GET /internal/properties/{id}/search-document-source

or a paged export resource.

Secure with service identity.

Do not expose persistence document.

Commit: feat(property): expose internal search reindex source

## Task 8 — Full reindex architecture

Create:
- task/reindex/FullSearchReindexTask.java

Do not delete active index first.

Flow:
1. create new physical index
2. stream/paginate source data
3. bulk index bounded batches
4. verify
5. alias switch
6. retain old index temporarily

Commit: feat(search): add full reindex task

## Task 9 — Versioned physical index naming

Pattern:
- properties-v{timestamp-or-sequence}

Stable alias:
- properties-read

Search queries target alias, not physical index.

Commit: feat(search): add index alias strategy

## Task 10 — Index creation helper

Create:
- task/reindex/SearchIndexManager.java

Responsibilities:
- create physical index with current mapping/settings
- inspect alias
- switch alias atomically
- delete old index only through explicit cleanup

Commit: feat(search): add safe index management

## Task 11 — Batch/pagination strategy

Property source pagination must be bounded.

Use:
- stable cursor/keyset pagination preferred for large datasets

Do not use unbounded list load.

Config:
- batchSize
- pageSize

Commit: feat(task): add bounded reindex pagination

## Task 12 — Bulk indexing

Use Elasticsearch bulk API with bounded batch.

Handle partial bulk failures explicitly.

Do not mark whole task success if some documents failed silently.

Commit: feat(search): add bounded bulk indexing

## Task 13 — Idempotent rerun

Full reindex rerun creates a new candidate index or resumes safely according to execution model.

Single-property reindex is naturally upsert/idempotent.

Reconciliation fixes can be rerun.

Commit: feat(task): make maintenance tasks idempotent

## Task 14 — Resume/checkpoint strategy

Spring Cloud Task records execution, but data progress may need checkpoint.

Options:
- task execution context/custom checkpoint store
- cursor parameter persisted per execution

Day 25 minimum:
record last successful cursor/batch for full reindex.

Commit: feat(task): add reindex checkpoint support

## Task 15 — Failure semantics

Failure classes:
- source unavailable
- Elasticsearch unavailable
- mapping failure
- bulk partial failure
- auth failure

Task exits failed; no alias switch.

Commit: feat(task): define task failure semantics

## Task 16 — Retry policy

Only transient source/Elasticsearch failures.

Bounded retry per batch.

No infinite loop.

Reuse resilience conventions.

Commit: feat(task): add bounded task retries

## Task 17 — Verification before alias switch

Checks candidate:
- indexed count vs exported count
- zero fatal bulk failures
- sample document validation
- mapping exists
- health acceptable

Do not rely on count alone.

Commit: feat(search): verify candidate index before cutover

## Task 18 — Atomic alias switch

Use Elasticsearch alias update API in one atomic operation.

Move:
- properties-read from old -> new

Search downtime target:
- zero/minimal local cutover.

Commit: feat(search): add atomic index alias switch

## Task 19 — Old index retention policy

Keep previous index temporarily for rollback.

Do not auto-delete immediately.

Cleanup can be manual or explicit follow-up command.

Commit: docs(task): define old-index retention policy

## Task 20 — Rollback alias operation

Create operator function/command:
- rollback alias to previous index

Only if mapping/query compatibility remains safe.

Commit: feat(search): add controlled alias rollback

## Task 21 — Reconciliation model

Create:
- task/reconcile/SearchProjectionReconciliationTask.java

Goal:
compare canonical Property source vs Elasticsearch projection.

Check:
- missing projection
- stale version
- wrong searchable status
- optional orphan document

Commit: feat(search): add projection reconciliation task

## Task 22 — Reconciliation comparison key

Use:
- propertyId
- sourceVersion
- sourceUpdatedAt
- projection version/freshness metadata

Avoid full document comparison if unnecessary.

## Task 23 — Reconciliation modes

Modes:
- report-only
- repair

Default safer mode:
- report-only

Repair must be explicit flag.

Commit: feat(task): add report and repair reconciliation modes

## Task 24 — Missing projection repair

Repair mode:
- fetch canonical projection source
- upsert

Idempotent.

## Task 25 — Stale projection repair

If sourceVersion newer:
- overwrite projection with canonical current state.

## Task 26 — Orphan projection policy

If Elasticsearch doc has no canonical Property:
- report
- delete only in explicit repair mode

Do not auto-delete by default.

## Task 27 — Searchable status reconciliation

Verify only allowed statuses visible under alias/search query.

## Task 28 — Task parameters

Standardize CLI/application args:
- --task.name
- --propertyId
- --batchSize
- --mode=report|repair
- --fromCursor

Validate fail-fast.

Commit: feat(task): validate maintenance task parameters

## Task 29 — Task security

Internal source API uses service-to-service credentials.

Task invocation itself is operational, not public HTTP endpoint by default.

Do not expose reindex endpoint publicly.

Commit: feat(task): secure reindex source access

## Task 30 — Logging

Log:
- taskExecutionId
- task name
- batch number
- processed count
- failed count
- cursor
- candidate index

Do not log full property payload.

Commit: feat(task): add structured maintenance task logs

## Task 31 — Task metrics

Metrics candidate:
- task_runs_total
- task_failures_total
- task_duration_seconds
- reindex_documents_processed_total
- reconciliation_mismatches_total

Bounded labels:
- task name
- outcome

Commit: feat(metrics): add maintenance task metrics

## Task 32 — Single-property reindex integration test

Flow:
1. canonical source exists
2. projection missing/stale
3. run task
4. projection corrected

Commit: test(task): verify single-property reindex

## Task 33 — Full rebuild integration test

Flow:
1. active alias old index
2. seed canonical properties
3. run full task
4. new physical index populated
5. validation passes
6. alias points new index
7. old retained

Commit: test(task): verify full search index rebuild

## Task 34 — Failure-before-switch test

Inject bulk/source failure.

Verify:
- task fails
- alias remains old index
- old search still works

Commit: test(task): protect alias switch on failure

## Task 35 — Resume test

Fail after several batches.

Resume using checkpoint.

Verify no duplication/corruption and eventual completion.

Commit: test(task): verify reindex checkpoint resume

## Task 36 — Reconciliation report test

Seed:
- missing
- stale
- orphan

Run report mode.

Verify counts/details.

Commit: test(task): verify reconciliation reporting

## Task 37 — Reconciliation repair test

Run repair mode.

Verify missing/stale corrected and explicit orphan policy.

Commit: test(task): verify reconciliation repair

## Task 38 — Rerun idempotency test

Run same task twice.

Final projection identical.

No duplicate business side effect.

Commit: test(task): verify maintenance task idempotency

## Task 39 — Task metadata test

Verify Spring Cloud Task records:
- execution id
- start/end
- status
- exit code/message

Commit: test(task): verify task execution metadata

## Task 40 — Concurrent task guard

Prevent two full reindex tasks from switching aliases unpredictably.

Options:
- task lock
- execution guard
- operational rule + datastore lock

Implement simple explicit guard.

Commit: feat(task): prevent concurrent full reindex runs

## Task 41 — Reindex runbook

Create:
- docs/runbooks/search-reindex.md

Include:
- when to run
- prechecks
- command
- monitoring
- cutover verification
- rollback
- cleanup

Commit: docs(task): add search reindex runbook

## Task 42 — Reconciliation runbook

Create:
- docs/runbooks/search-reconciliation.md

Include report vs repair.

Commit: docs(task): add search reconciliation runbook

## Task 43 — Operational dry-run

Execute local scenario:
- delete Search index
- run full rebuild
- verify search restored

This satisfies recovery exercise from NFR/runbook standards.

## Task 44 — Architecture tests

Rules:
- task code may read via approved service contract, not direct Property Mongo access
- Search task owns Search projection only
- no public controller dependency
- no domain model shared across services

Commit: test(task): enforce maintenance architecture boundaries

## Task 45 — Documentation reconciliation

Modify:
- docs/roadmap/day-25-cloud-task.md
- SearchService/docs/DESIGN.md
- SearchService/docs/PACKAGE-DESIGN.md
- docs/architecture/persistence-model.md if alias/index model needs update
- docs/operations/maintenance-task-inventory.md

Record actual:
- Task metadata DB
- task names
- source strategy
- index alias/name
- batch size
- checkpoint strategy
- reconciliation policy

Commit: docs(task): finalize maintenance and recovery architecture

## Recommended Commit Sequence

1. docs(task): inventory finite maintenance tasks
2. build(task): add Spring Cloud Task
3. config(task): configure task metadata repository
4. docs(task): finalize reindex source strategy
5. feat(property): expose internal search reindex source — if selected
6. feat(search): add single-property reindex task
7. feat(search): add index alias strategy
8. feat(search): add safe index management
9. feat(search): add full reindex task
10. feat(task): add bounded reindex pagination
11. feat(search): add bounded bulk indexing
12. feat(task): make maintenance tasks idempotent
13. feat(task): add reindex checkpoint support
14. feat(task): define task failure semantics
15. feat(task): add bounded task retries
16. feat(search): verify candidate index before cutover
17. feat(search): add atomic index alias switch
18. docs(task): define old-index retention policy
19. feat(search): add controlled alias rollback
20. feat(search): add projection reconciliation task
21. feat(task): add report and repair reconciliation modes
22. feat(task): validate maintenance task parameters
23. feat(task): secure reindex source access
24. feat(task): add structured maintenance task logs
25. feat(metrics): add maintenance task metrics
26. test(task): verify single-property reindex
27. test(task): verify full search index rebuild
28. test(task): protect alias switch on failure
29. test(task): verify reindex checkpoint resume
30. test(task): verify reconciliation reporting
31. test(task): verify reconciliation repair
32. test(task): verify maintenance task idempotency
33. test(task): verify task execution metadata
34. feat(task): prevent concurrent full reindex runs
35. test(task): enforce maintenance architecture boundaries
36. docs(task): add search reindex runbook
37. docs(task): add search reconciliation runbook
38. docs(task): finalize maintenance and recovery architecture

Adjacent task-helper commits may be merged if cohesive. Reindex source, index lifecycle, reconciliation and tests should remain separately reviewable.

## Explicitly Deferred from Day 25

Do not implement:
- Kubernetes CronJob
- enterprise scheduler
- Spring Batch unless workload complexity later requires it
- full data warehouse ETL
- cache warm unless proven useful
- automatic production repair without operator control

## Critical Design Note — Service Ownership

Search reindex must not violate PropertyService data ownership by directly reading its MongoDB.

Use an explicit contract or event/replay mechanism.

## Critical Design Note — Alias Switch

Never delete the active index before a successful rebuild.

Build new, verify, atomically switch alias, retain old temporarily.

## Critical Design Note — Reconciliation

Report-only is safer default.

Repair requires explicit operator intent.

## Day 25 Final Gate

Day 25 closes only if:
- Spring Cloud Task records executions
- single-property reindex works
- full index rebuild uses a new physical index
- Search queries use stable alias
- source data is paged/batched
- bulk failures are detected
- alias does not switch on failed rebuild
- alias switch is atomic
- previous index is retained temporarily
- rollback path exists
- task rerun is idempotent
- checkpoint/resume works for full reindex
- reconciliation finds missing/stale/orphan projections
- repair mode is explicit
- concurrent full rebuilds are guarded
- task metrics/logs exist
- reindex/reconciliation runbooks exist
- local destructive index recovery exercise succeeds
- no direct cross-service MongoDB access exists
- no Kubernetes scheduler/Spring Batch scope leaks into Day 25
- docs match actual implementation