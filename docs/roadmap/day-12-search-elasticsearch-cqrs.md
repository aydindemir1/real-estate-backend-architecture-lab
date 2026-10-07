# Day 12 — SearchService: Elasticsearch + CQRS Query Side Foundation

## Amaç
SearchService'i bağımsız query-side module olarak ayağa kaldırmak.

## Görevler
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

## Önerilen commit'ler
1. build(search): add SearchService module runtime foundation
2. feat(search): add Elasticsearch document and mapping
3. feat(search): add Elasticsearch persistence adapter
4. feat(search): add SearchProperties query slice
5. feat(search): expose basic search API
6. test(search): add Elasticsearch integration tests
7. test(search): enforce CQRS query-side boundaries
8. docs(search): finalize Day 12 design

## Doğrulama
- module starts/registers
- document indexed
- text query works
- exact filters work
- range query works
- pagination bounded
- no canonical Property write model

## Tamamlanma durumu
SearchService standalone Elasticsearch query-side foundation olarak çalışır.

## Kesin file/class planı

Implementation için source of truth: `docs/roadmap/day-12-exact-file-plan.md`

## Onaylanan ek öğrenme ve uygulama kapsamı

- SQL/NoSQL seçim matrisini hazırla: transaction/invariant, sorgu biçimi, indeks, consistency, veri sahipliği ve operasyonel maliyet. MySQL/PostgreSQL, MongoDB/Couchbase/Cassandra ve Elasticsearch rollerini karşılaştır; Elasticsearch’ü canonical store olarak seçme.
- Latency ile throughput farkını aynı SearchProperties use-case üzerinden açıkla. Test verisi, warm-up, concurrency, süre, hata oranı ve p50/p95/p99 ölçüm koşullarını belirle; yapay scale gereksinimi üretme.
- Mevcut API Design standardını arama sözleşmesine uygula: resource/HTTP semantics, bounded pagination, filtre/sort whitelist, validation, kararlı error code, versioning kararı ve OpenAPI örnekleri.
- Mevcut handler/controller testlerine max size, negatif page, invalid range, izin verilmeyen sort ve dependency unavailable senaryolarını ekle; zaten bulunan testi tekrar yazma.
- Yerel sınırlı ölçümde latency/throughput ve kaynak kullanımını raporla; sonuçları production kapasite garantisi olarak sunma.
- docs/architecture/datastore-selection.md ve docs/testing/search-performance-baseline.md belgelerine seçim gerekçesini/ölçüm koşullarını kaydet; SearchService DESIGN/ROADMAP ve Knowledge Base etkisini güncelle.

Güncel görevler, commit sırası ve ek kabul ölçütleri [kesin planda](day-12-exact-file-plan.md) yer alır. Bu ek kapsam **planlıdır**; doğrulama kanıtı oluşmadan tamamlandı olarak işaretlenmez.
