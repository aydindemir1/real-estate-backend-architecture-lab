# Day 22 — Unit + Integration + Testcontainers Hardening

## Goal
Önceki Day'lerde yazılan testleri sistematik hale getirip coverage boşluklarını kapatmak.

## Tasks
1. test inventory çıkar.
2. domain critical invariant gaps bul.
3. application orchestration gaps kapat.
4. datastore Testcontainers coverage tamamla.
5. messaging integration tests tamamla.
6. security tests tamamla.
7. idempotency/concurrency tests tamamla.
8. test tagging/source-set ayır.
9. flaky/timing testleri düzelt.
10. Clock/Awaitility kullanımını standardize et.
11. architecture tests'i build lifecycle'a bağla.
12. CI-ready test task'ları oluştur.
13. docs güncelle.

## Suggested commits
1. test: audit and classify test suites
2. test(domain): close invariant coverage gaps
3. test(integration): harden datastore coverage
4. test(messaging): harden broker scenarios
5. test(security): complete authorization coverage
6. test(concurrency): add idempotency and race tests
7. build(test): separate test tasks and tags
8. docs(test): finalize testing matrix

## Done
Critical behaviors happy/failure/concurrency/security paths ile güvence altındadır.

## Exact testing/file plan

Implementation source of truth: `docs/roadmap/day-22-exact-file-plan.md`
