# Day 11 — PropertyService: MongoDB + Vertical Slice Architecture

## Goal
Canonical Property Aggregate'i MongoDB ve Vertical Slice Architecture ile kurmak.

## Tasks
1. JPA/PostgreSQL dependency'lerini kaldır.
2. MongoDB dependency/config ekle.
3. shared/domain model oluştur.
4. PropertyId, SellerId, AgentId, Money, Address, GeoLocation, Area oluştur.
5. PropertyStatus ve Property Aggregate oluştur.
6. publish/update/price invariants ekle.
7. Mongo PropertyDocument oluştur.
8. @Version optimistic concurrency ekle.
9. repository abstraction/adapter oluştur.
10. sellerId+createdAt/status index'lerini tanımla.
11. getbyid slice oluştur.
12. publish slice oluştur.
13. minimal web DTO/mapper oluştur.
14. test fixture ile DRAFT property persistence oluştur.
15. domain tests yaz.
16. Mongo Testcontainers tests yaz.
17. optimistic locking conflict test et.
18. slice-to-slice dependency ArchUnit rule yaz.
19. docs güncelle.

## Suggested commits
1. build(property): switch persistence to MongoDB
2. refactor(property): establish Vertical Slice foundation
3. feat(property): add Property aggregate and value objects
4. feat(property): add Mongo persistence foundation
5. feat(property): add get-by-id slice
6. feat(property): add publish slice
7. test(property): add aggregate tests
8. test(property): add Mongo integration and concurrency tests
9. test(property): enforce vertical slice boundaries
10. docs(property): finalize Day 11 design

## Verification
- DRAFT persisted
- GET works
- DRAFT -> PUBLISHED works
- invalid re-publish rejected
- version conflict detected
- no public fake POST /properties introduced

## Done
Property canonical model MongoDB üzerinde çalışır ve Vertical Slice boundary korunur.

## Exact file/class plan

Implementation source of truth: `docs/roadmap/day-11-exact-file-plan.md`
