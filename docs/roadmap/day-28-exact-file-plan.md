# Day 28 — Kesin Failure-Path Testing Planı

## Kapsam
- dependency outage matrix
- datastore/broker/config/Vault failure'ları
- HTTP/gRPC failure'ları
- poison message'lar
- DLT/DLQ replay safety
- invalid config/migration failure
- stable negative API behavior

## Task'ler
1. `docs/testing/failure-path-matrix.md` oluştur.
2. Representative MySQL/Mongo/Cassandra/Couchbase/Elasticsearch outage testleri ekle.
3. Raw driver error'larının hiçbir zaman dışarı sızmadığını doğrula.
4. Offer-idempotency accelerator ve Gateway rate-limit policy'leri için Redis outage test et.
5. Kafka outage test et: local transaction/outbox durable ve pending kalmalı.
6. RabbitMQ outage test et: Seller pending outbound message durable kalmalı ve daha sonra başarıyla gönderilmeli.
7. Config Server startup/runtime semantics'i test et.
8. Vault critical-secret startup fail-fast davranışını test et.
9. Downstream HTTP 503'ü timeout/circuit/retry mapping üzerinden test et.
10. Slow Agent gRPC deadline ve nested retry olmadığını test et.
11. Elasticsearch outage test et: Search explicit unavailable semantics döndürmeli, fake empty success dönmemeli.
12. Kafka malformed/unsupported event → DLT ve partition processing devam etsin.
13. RabbitMQ malformed command → DLQ ve hot loop olmasın.
14. Root-cause düzeltildikten sonra Kafka DLT replay test et.
15. RabbitMQ DLQ replay ve duplicate-safe command consumer test et.
16. Aktifse aynı message id + farklı payload conflict policy'sini test et.
17. Invalid critical config'in erken fail ettiğini test et.
18. Relational migration/checksum failure'ın startup'ı engellediğini test et.
19. Negative scenario'ların harmful side effect üretmediğini de assert et.
20. Ayrı `failureTest` execution/report oluştur ve runbook'lara link ver.

## Commit sırası
1. `docs(test): define failure-path matrix`
2. `test(failure): add datastore outage scenarios`
3. `test(failure): verify Redis Kafka and RabbitMQ outage behavior`
4. `test(failure): verify Config Vault HTTP and gRPC failures`
5. `test(failure): verify Elasticsearch outage`
6. `test(failure): add poison-message scenarios`
7. `test(failure): verify DLT and DLQ replay safety`
8. `test(config): verify fail-fast configuration and migration failures`
9. `build(test): add failure test task and report`

## Final gate
- bütün temel failure domain'leri için explicit expected behavior var
- outage testleri bounded
- poison message'lar süresiz bloklayamaz
- replay güvenli
- failed scenario'lar hidden harmful side effect üretmiyor
