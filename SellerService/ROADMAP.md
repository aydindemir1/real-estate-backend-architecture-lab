# SellerService Roadmap

## Amaç

SellerService; seller state'i ile PropertyService'e gönderilmeden önceki listing submission lifecycle'ının ownership'ini taşır.

Day 10 itibarıyla servis **Apache Cassandra + Onion Architecture** ile implemente edilmiş ve automated tests, CI ve lokal runtime acceptance ile doğrulanmıştır.

## Day 10 — Tamamlandı / Doğrulandı

### Architecture
- Onion Architecture
- framework-independent domain
- inward dependency direction
- domain repository abstractions
- Cassandra infrastructure adapters
- REST presentation boundary
- ArchUnit architecture fitness rules

### Domain
- `SellerId`
- `UserId`
- `SellerStatus`
- `Seller` Aggregate
- `ListingSubmissionId`
- `ListingSubmissionStatus`
- `PropertyDraftData`
- `ListingSubmission`

Seller status:
- ACTIVE
- SUSPENDED
- INACTIVE

Yalnız ACTIVE seller listing create/submit akışına devam edebilir.

Listing state model:
- CREATED
- SUBMITTED
- PROPERTY_CREATED
- REJECTED
- FAILED

Day 10 runtime flow:
- `CREATED -> SUBMITTED`

### Application
- `CreateSellerCommand`
- `CreateListingSubmissionCommand`
- `SubmitListingCommand`
- `GetSellerQuery`
- `ListSellerSubmissionsQuery`
- `SellerApplicationService`
- `ListingSubmissionApplicationService`

### Persistence
- Apache Cassandra 5.0.9
- keyspace: `seller_service`
- `seller_by_id`
- `listing_submissions_by_seller_and_month`
- partition: `(seller_id, year_month)`
- clustering: `created_at DESC, submission_id ASC`
- explicit version-controlled CQL schema
- `spring.cassandra.schema-action: none`
- no `ALLOW FILTERING`
- no cross-partition scan

Global `userId` uniqueness Day 10'da Cassandra-safe LWT/ownership table ile enforce edilmemektedir; repository contract yalnız seller-id lookup ve save taşır.

### REST API
- `POST /sellers`
- `GET /sellers/{sellerId}`
- `POST /sellers/{sellerId}/listing-submissions`
- `GET /sellers/{sellerId}/listing-submissions?yearMonth=YYYY-MM&pageSize=N&pageState=...`
- `POST /sellers/{sellerId}/listing-submissions/{submissionId}/submit`

### Stable error semantics
- `404 SELLER_NOT_FOUND`
- `404 LISTING_SUBMISSION_NOT_FOUND`
- `422 SELLER_NOT_ACTIVE`
- `409 INVALID_LISTING_SUBMISSION_STATE`
- `400 VALIDATION_ERROR`
- `400 BAD_REQUEST`
- `400 MALFORMED_REQUEST_BODY`
- `500 DATABASE_ERROR`
- `500 INTERNAL_SERVER_ERROR`

### Test ve doğrulama
- domain unit tests
- application tests
- Cassandra Testcontainers integration tests
- exact seller/month partition tests
- newest-first clustering tests
- paging tests
- query-design guard
- REST controller tests
- ArchUnit Onion Architecture tests
- GitHub Actions CI
- Config Server
- Eureka registration
- gerçek Cassandra write/read
- 6 success Postman scenario
- 10 error Postman scenario
- runtime defect -> fix -> retest evidence

Kanıtlar:
- `docs/evidence/day-10/`
- `docs/collections/day-10/Day-10-SellerService.postman_collection.json`

## Bilinçli olarak ertelenen

Day 10 kapsamında yoktur:
- RabbitMQ SubmitPropertyListingCommand publish
- publisher confirms
- reliable Cassandra-to-RabbitMQ dispatch
- Kafka
- Saga
- pending offer projections
- seller activity timeline
- offer history tables

## Detaylı dokümanlar

- `docs/DESIGN.md`
- `docs/PACKAGE-DESIGN.md`
- `../docs/roadmap/day-10-seller-cassandra-onion.md`
- `../docs/roadmap/day-10-exact-file-plan.md`
