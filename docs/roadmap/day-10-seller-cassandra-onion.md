# Day 10 — SellerService: Cassandra + Onion Architecture

## Durum

**Completed / Verified**

Implementation, automated tests, CI, lokal runtime/Postman acceptance ve original PNG evidence repository sync tamamlanmıştır.

## Gerçekleşen kapsam

1. SellerService JPA/PostgreSQL persistence yaklaşımından Cassandra'ya geçirildi.
2. Onion Architecture boundaries kuruldu.
3. Seller Aggregate ve SellerStatus oluşturuldu.
4. ListingSubmission state model kuruldu.
5. PropertyDraftData domain modeli eklendi.
6. Domain repository abstractions tanımlandı.
7. Application commands, queries, results ve services oluşturuldu.
8. `seller_by_id` query table oluşturuldu.
9. `listing_submissions_by_seller_and_month` query-first table oluşturuldu.
10. Composite partition `(seller_id, year_month)` kullanıldı.
11. Clustering `created_at DESC, submission_id ASC` olarak kuruldu.
12. Spring Data Cassandra repository, mapper ve adapter katmanları implemente edildi.
13. Version-controlled CQL schema eklendi.
14. REST seller/listing API'leri implemente edildi.
15. Stable error envelope ve domain error mapping uygulandı.
16. Domain/application/REST/Cassandra integration/ArchUnit testleri yazıldı.
17. Testcontainers Cassandra foundation doğrulandı.
18. No-ALLOW-FILTERING query guard eklendi.
19. GitHub Actions CI başarılı oldu.
20. Config Server + Eureka + Cassandra + SellerService lokal runtime doğrulandı.
21. Cassandra Admin UI lokal geliştirme aracı olarak eklendi.
22. 6 success ve 10 error Postman acceptance senaryosu geçti.
23. Cassandra write/read persistence hem UI hem `cqlsh` ile doğrulandı.
24. Runtime'da bulunan REST parameter binding defect'i düzeltilip tekrar doğrulandı.

## Cassandra fiziksel model

### seller_by_id

Primary access:
- seller by `seller_id`

### listing_submissions_by_seller_and_month

Partition:
- `seller_id`
- `year_month`

Clustering:
- `created_at DESC`
- `submission_id ASC`

Ana query:
- one seller + one month
- bounded partition
- newest-first
- no `ALLOW FILTERING`

## REST acceptance

### Success

- POST `/sellers` → 201
- GET `/sellers/{sellerId}` → 200
- POST `/sellers/{sellerId}/listing-submissions` → 201
- GET monthly listing submissions → 200
- POST submit → 200
- GET after submit → 200 / SUBMITTED

### Error

Doğrulanan stable responses:
- 404 `SELLER_NOT_FOUND`
- 404 `LISTING_SUBMISSION_NOT_FOUND`
- 400 `VALIDATION_ERROR`
- 400 `BAD_REQUEST`
- 400 `MALFORMED_REQUEST_BODY`
- 409 `INVALID_LISTING_SUBMISSION_STATE`

## Runtime evidence

- `docs/evidence/day-10/README.md`
- `docs/collections/day-10/Day-10-SellerService.postman_collection.json`

Orijinal PNG evidence paketi hazırlanmıştır; binary repository sync lokal Git üzerinden yapılacaktır.

## Deferred

Day 10'a dahil edilmemiştir:
- RabbitMQ SubmitPropertyListingCommand
- publisher confirms
- reliable Cassandra → RabbitMQ dispatch
- Kafka
- Saga
- pending offer projections
- seller activity timeline
- offer history tables

## Completion gate

- [x] Cassandra persistence
- [x] framework-independent domain
- [x] Onion boundaries
- [x] Seller Aggregate
- [x] ListingSubmission state model
- [x] ACTIVE seller rule
- [x] query-first tables
- [x] bounded seller/month partition
- [x] newest-first clustering
- [x] no ALLOW FILTERING
- [x] domain/persistence model separation
- [x] REST API
- [x] stable error mapping
- [x] Testcontainers integration
- [x] ArchUnit boundaries
- [x] Config Server
- [x] Eureka
- [x] local Cassandra write/read
- [x] Postman success/error acceptance
- [x] original PNG evidence GitHub binary sync
- [x] canonical planning branch final sync


## Exact plan

- `docs/roadmap/day-10-exact-file-plan.md`
