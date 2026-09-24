# Day 12 — SearchService: Elasticsearch + CQRS Query Side Foundation

## Goal
SearchService'i bağımsız query-side module olarak ayağa kaldırmak.

## Tasks
1. SearchService build/application bootstrap oluştur.
2. Elasticsearch dependency/config ekle.
3. service name/config/eureka/actuator baseline ekle.
4. PropertySearchDocument oluştur.
5. explicit index mapping tanımla.
6. 1 shard / 0 replica local setting ekle.
7. Elasticsearch repository/adapter oluştur.
8. SearchPropertiesQuery oluştur.
9. SearchPropertiesHandler oluştur.
10. search REST controller oluştur.
11. q/status/type/price-range basic filters ekle.
12. bounded pagination ekle.
13. no-result/error semantics tanımla.
14. test data indexing fixture oluştur.
15. Elasticsearch Testcontainers tests yaz.
16. full-text/filter/range tests yaz.
17. CQRS query-side architecture rule yaz.
18. Mongo/write-side dependency olmadığını doğrula.
19. docs güncelle.

## Suggested commits
1. build(search): add SearchService module runtime foundation
2. feat(search): add Elasticsearch document and mapping
3. feat(search): add Elasticsearch persistence adapter
4. feat(search): add SearchProperties query slice
5. feat(search): expose basic search API
6. test(search): add Elasticsearch integration tests
7. test(search): enforce CQRS query-side boundaries
8. docs(search): finalize Day 12 design

## Verification
- module starts/registers
- document indexed
- text query works
- exact filters work
- range query works
- pagination bounded
- no canonical Property write model

## Done
SearchService standalone Elasticsearch query-side foundation olarak çalışır.
