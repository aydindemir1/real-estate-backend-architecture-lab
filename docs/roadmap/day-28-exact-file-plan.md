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
1. docs(chaos): hipotez steady-state ve abort ölçütlerini tanımla
2. `docs(test): define failure-path matrix`
3. `test(failure): add datastore outage scenarios`
4. `test(failure): verify Redis Kafka and RabbitMQ outage behavior`
5. `test(failure): verify Config Vault HTTP and gRPC failures`
6. `test(failure): verify Elasticsearch outage`
7. `test(failure): add poison-message scenarios`
8. `test(failure): verify DLT and DLQ replay safety`
9. `test(config): verify fail-fast configuration and migration failures`
10. `build(test): add failure test task and report`
11. test(chaos): izole kontrollü hata enjeksiyonu ekle
12. test(chaos): recovery backlog ve duplicate güvenliğini doğrula
13. docs(chaos): deney sonucunu ve temizlik kanıtını kaydet

Küçük ve aynı sorumluluğa ait komşu commit’ler birleştirilebilir; karar, uygulama ve doğrulama ayrı incelenebilir kalır.

## Final gate
- bütün temel failure domain'leri için explicit expected behavior var
- outage testleri bounded
- poison message'lar süresiz bloklayamaz
- replay güvenli
- failed scenario'lar hidden harmful side effect üretmiyor

## Onaylanan system design ek kapsamı — Sınırlı chaos engineering deneyi

Durum: **Planlandı**. Bu bölüm günün mevcut temel görevlerine eklenir; tamamlanmış implementation iddiası değildir. Ek görevler foundation kurulduktan sonra ve günün dokümantasyon/kapanış adımından önce uygulanır. Yukarıdaki commit sırası bu kapsamı içerir.

### Ek görevler ve çıktı belgeleri

1. Day 28 failure matrix’inden bir gerçek akışı seç: örneğin Kafka bağlantısı kesilince commit edilmiş Property ve pending Outbox kaybolmaz; bağlantı düzeldikten sonra Search catch-up yapar.
2. Hipotez, steady-state ölçütü, süre/büyüklük sınırı, durdurma koşulu ve temizlik prosedürünü deney başlamadan yaz.
3. İzole local/Testcontainers ortamında kontrollü hata enjeksiyonu uygula; container stop veya scoped network fault seçimini açıkla. Yeni chaos platformu kurma.
4. Day 29–30 telemetry henüz hazır olmadığı için mevcut test assertion’ları/structured logs/pending count/final state ile ölç; Day 30’da dashboard kanıtı eklenebileceğini belirt.
5. Recovery sonrasında duplicate side-effect olmadığını, doğru backlog drain ve eventual projection convergence davranışını doğrula; deterministik CI failure testinden ayrı opt-in experiment tutulabilir.
6. docs/testing/chaos-experiment.md içine önce/sonra evidence, sonuç, beklenmeyen davranış ve takip görevini kaydet; chaos engineering kapsamının bu sınırlı deney olduğunu belirt.

### Ek kabul ölçütleri

- Hipotez/steady-state/abort/cleanup tanımlı.
- Deney izole ve süre sınırlı; durable state ve recovery testli.
- Sınırlı deney production chaos programı olarak sunulmuyor.

Kapanışta ilgili service ROADMAP/DESIGN belgeleri ve Knowledge Base gerçek implementation/kanıtlarla güncellenir. Önce ilgili GitHub CI başarılı olur; ardından local runtime/API doğrulaması yapılır. Bir Day bir milestone’dır; kapsam gerektiğinde birden fazla takvim gününde tamamlanabilir.
