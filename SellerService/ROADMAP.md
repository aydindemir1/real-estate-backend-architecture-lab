# SellerService Roadmap

## Mevcut durum

Day 10 kapsamında SellerService için **Apache Cassandra + Onion Architecture** implementation tamamlandı ve automated tests, GitHub CI, lokal runtime, Eureka registration, gerçek Cassandra write/read ve Postman acceptance ile doğrulandı.

### Day 10 — Completed / Verified

Uygulanan kapsam:
- Seller Aggregate
- ListingSubmission state model
- PropertyDraftData
- Onion Architecture boundaries
- framework-independent domain
- Cassandra query-first persistence
- `seller_by_id`
- `listing_submissions_by_seller_and_month`
- partition `(seller_id, year_month)`
- clustering `created_at DESC, submission_id ASC`
- explicit version-controlled CQL schema
- no `ALLOW FILTERING`
- REST seller/listing API
- stable error semantics
- Cassandra Testcontainers
- ArchUnit architecture fitness rules
- Config Client
- Eureka Client
- runtime/Postman evidence

Detaylı actual design:
- `docs/DESIGN.md`
- `docs/PACKAGE-DESIGN.md`
- `../docs/roadmap/day-10-seller-cassandra-onion.md`
- `../docs/roadmap/day-10-exact-file-plan.md`

## Sonraki SellerService milestone'ları

Canonical schedule için repository root `ROADMAP.md` source of truth'tur.

SellerService'i doğrudan etkileyen planlı alanlar arasında:
- Day 14 security / Keycloak authorization,
- Day 17 Kafka + Spring Cloud Stream/Function foundation,
- Day 18 reliable publication design,
- Day 19 retry / DLT-DLQ / replay safety,
- Day 20 Cassandra-friendly reliable outbound messaging + RabbitMQ dispatch

bulunur.

Day 10'da RabbitMQ reliable dispatch, Kafka business publication, Saga ve offer projections bilinçli olarak implemente edilmemiştir.
