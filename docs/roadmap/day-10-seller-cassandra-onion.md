# Day 10 — SellerService: Cassandra + Onion Architecture

## Goal
Seller ve ListingSubmission capability'sini Cassandra query-first model ile kurmak.

## Tasks
1. JPA/PostgreSQL dependency'lerini kaldır.
2. Cassandra dependency/config ekle.
3. Onion package boundaries oluştur.
4. Seller domain model oluştur.
5. ListingSubmission domain model/state machine oluştur.
6. PropertyDraftData value object oluştur.
7. domain repository abstractions oluştur.
8. application commands/queries/services oluştur.
9. seller_by_id table model oluştur.
10. listing_submissions_by_seller_and_month table model oluştur.
11. partition/clustering key mapping yap.
12. Cassandra repository adapter'ları oluştur.
13. CQL schema script oluştur.
14. local submit flow'u external RabbitMQ publish olmadan uygula.
15. REST endpoints ekle.
16. pagination/time-bucket query semantics belirle.
17. domain tests yaz.
18. Cassandra integration tests yaz.
19. newest-first ordering test et.
20. no ALLOW FILTERING rule doğrula.
21. Onion ArchUnit rules yaz.
22. docs güncelle.

## Suggested commits
1. build(seller): switch persistence to Cassandra
2. refactor(seller): establish Onion Architecture packages
3. feat(seller): add Seller domain model
4. feat(seller): add ListingSubmission state model
5. feat(seller): add application services
6. db(seller): add Cassandra query-first schema
7. feat(seller): add Cassandra adapters
8. feat(seller): expose seller and listing APIs
9. test(seller): add domain tests
10. test(seller): add Cassandra integration tests
11. test(seller): enforce Onion Architecture rules
12. docs(seller): finalize Day 10 design

## Verification
- seller create/get
- ACTIVE seller submission
- inactive seller rejected
- monthly partition query
- newest-first clustering
- no Cassandra dependency in domain

## Done
SellerService query-first Cassandra model ile çalışır; RabbitMQ reliable dispatch bilinçli olarak sonraya bırakılır.
