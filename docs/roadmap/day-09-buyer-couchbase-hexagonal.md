# Day 9 — BuyerService: Couchbase + Hexagonal Architecture

## Goal
BuyerPreferences/SavedSearch capability'sini Couchbase ve Hexagonal Architecture ile kurmak.

## Tasks
1. JPA/PostgreSQL dependency'lerini kaldır.
2. Couchbase dependency/config ekle.
3. domain package oluştur.
4. BuyerId, PriceRange, RoomRange, AreaRange, LocationPreference, NotificationSettings, SavedSearch oluştur.
5. BuyerPreferences Aggregate oluştur.
6. invariant ve defensive copy kuralları ekle.
7. inbound ports oluştur.
8. outbound persistence ports oluştur.
9. application services oluştur.
10. Couchbase document model oluştur.
11. deterministic document key strategy oluştur.
12. Spring Data Couchbase repository oluştur.
13. persistence adapter oluştur.
14. bucket/scope/collection/index bootstrap stratejisini yaz.
15. request/response DTO'ları oluştur.
16. REST adapter oluştur.
17. not-found/error mapping ekle.
18. fake port ile application tests yaz.
19. Couchbase Testcontainers integration tests yaz.
20. Hexagonal ArchUnit kuralları yaz.
21. docs güncelle.

## Suggested commits
1. build(buyer): switch persistence to Couchbase
2. refactor(buyer): establish Hexagonal Architecture packages
3. feat(buyer): add BuyerPreferences domain model
4. feat(buyer): add inbound and outbound ports
5. feat(buyer): add application services
6. feat(buyer): add Couchbase persistence adapter
7. feat(buyer): expose preferences and saved-search API
8. test(buyer): add domain and application tests
9. test(buyer): add Couchbase integration tests
10. test(buyer): enforce Hexagonal Architecture rules
11. docs(buyer): finalize Day 9 design

## Verification
- upsert/get preferences
- add saved search
- invalid ranges rejected
- document key deterministic
- application/domain does not depend on adapters

## Done
BuyerService preferences capability Couchbase üzerinde ve Hexagonal boundaries korunarak çalışır.

## Exact file/class plan

Implementation source of truth: `docs/roadmap/day-09-exact-file-plan.md`
