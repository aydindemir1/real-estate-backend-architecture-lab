# Day 18 — Kesin Outbox + Inbox + Idempotent Consumer Planı

## Kapsam

- Property Mongo Outbox
- Inbox/Processed Message
- duplicate-safe consumer'lar
- atomic local side effect'ler
- crash-window testleri
- Couchbase ve Cassandra için reliable-publication design

## Zorunlu correctness eki
- Property/MongoDB Outbox kullanır.
- Seller/Cassandra durable pending outbound messages + dispatcher/reconciliation kullanır.
- Buyer/Couchbase, Day 22'den önce durable outbound-publication strategy'ye sahip olmalıdır.
- Kritik broker publication için best-effort yaklaşımı yasaktır.

## Task 1 — Reliability failure matrix

Doküman oluştur/güncelle:
- docs/architecture/messaging-reliability.md

Failure mode'ları listele:
- DB commit başarılı, publish başarısız
- publish başarılı, DB commit başarısız
- duplicate delivery
- consumer side effect sonrası ack öncesi crash
- malformed payload
- transient broker outage
- downstream timeout
- poison message
- replay duplicate

Her birini strategy ile eşleştir.

Commit: docs(messaging): define reliability failure matrix

## Task 2 — Delivery semantics bildirimi

Project baseline olarak şunları ilan et:
- at-least-once delivery
- duplicate delivery beklenen durumdur
- exactly-once business effect, idempotency + local transaction semantics ile sağlanır

Kafka için end-to-end business semantics seviyesinde exactly-once iddiasında bulunma.

## Task 3 — PropertyService Outbox modeli

PropertyService MongoDB kullandığı ve lifecycle event publish ettiği için primary candidate'dır.

Oluştur:
- shared/outbox/OutboxMessage.java
- shared/outbox/OutboxStatus.java
- shared/outbox/OutboxRepository.java

Alanlar:
- outboxId
- aggregateId
- aggregateType
- eventType
- payload
- occurredAt
- correlationId
- causationId
- schemaVersion
- status
- retryCount
- nextAttemptAt
- publishedAt

## Task 4 — Mongo Outbox document

Oluştur:
- shared/outbox/mongo/OutboxDocument.java
- shared/outbox/mongo/SpringDataOutboxRepository.java
- shared/outbox/mongo/MongoOutboxRepositoryAdapter.java

Index'ler:
- status + nextAttemptAt
- operational query gerekiyorsa aggregateId adayı

Commit: feat(property): add Mongo outbox persistence

## Task 5 — Atomic Property + Outbox write

MongoDB multi-document transaction'ı yalnızca replica set topology ile desteklediği için dikkatli seçim yap.

Tercih edilen learning seçenekleri:
- replica-set Testcontainers/local topology ile aynı Mongo transaction
veya
- model maintainable kalıyorsa single-document embedded outbox

İki ayrı Mongo save işlemini atomic gibi göstermeye çalışma.

Multi-document transaction kullanılıyorsa Property state + Outbox record etrafında transaction boundary oluştur.

Commit: feat(property): persist domain state and outbox atomically

## Task 6 — Outbox event mapper

Oluştur:
- shared/outbox/OutboxEventMapper.java

Sorumluluk:
- integration event -> persisted payload/envelope

Kafka client kullanmaz.

Commit: feat(property): add outbox event mapping

## Task 7 — Outbox publisher

Oluştur:
- shared/outbox/OutboxPublisher.java
- shared/outbox/OutboxPublishingJob.java veya application service

Flow:
1. pending due record'ları çek
2. PublishPropertyEventPort üzerinden publish et
3. success durumunda published olarak işaretle
4. transient failure durumunda retry metadata güncelle

Batch size bounded olmalıdır.

Unbounded polling kullanma.

Commit: feat(messaging): add outbox publisher

## Task 8 — Outbox scheduling trigger

Day 17 için lightweight trigger seç:
- @Scheduled polling

Spring Cloud Task Day 25'te kalır.

Config:
- interval Duration
- batch size

Scaled environment'da coordination strategy olmadan birden fazla uncontrolled scheduler instance oluşturma.

Local lab single instance olabilir; distributed scaling caveat'ını dokümante et.

Commit: config(messaging): add bounded outbox polling

## Task 9 — Outbox publish idempotency

Broker ack sonrası mark-published öncesi crash olursa Outbox record birden fazla kez publish edilebilir.

Bu nedenle consumer'lar eventId ile deduplication yapmalıdır.

Producer-side exactly-once illüzyonuna güvenme.

## Task 10 — Inbox modeli

Generic consumer-side kavram oluştur:
- ProcessedMessage.java
- ProcessedMessageRepository.java

Alanlar:
- messageId/eventId
- consumerName
- processedAt
- opsiyonel payloadHash

Unique logical key:
- consumerName + messageId

## Task 11 — Datastore bazında Inbox persistence

Her service'e tek DB teknolojisi zorlamaya çalışma.

Örnekler:
- SearchService Elasticsearch, inbox source için ideal değildir; uygunsa canonical/local supporting store kullan veya açıkça gerekçelendirilmiş dedicated lightweight persistence seç
- SellerService Cassandra, processed_message_by_consumer table kullanabilir
- BuyerService Couchbase idempotency document saklayabilir

Day 17, yalnızca şu anda gerçek side-effecting consumer bulunan yerde inbox implemente etmelidir.

Her service'e kullanılmayan inbox table ekleme.

## Task 12 — Idempotent consumer wrapper

Application/infrastructure helper pattern oluştur:
- IdempotentMessageHandler.java

Flow:
1. messageId kontrol et
2. zaten processed ise -> no-op/ack
3. handler çalıştır
4. mümkünse processed marker'ı aynı local transaction içinde persist et

Kural:
processed marker + side effect, local datastore capability'leri içinde atomic olmalıdır.

Commit: feat(messaging): add idempotent consumer foundation

## Task 13 — Outbox integration testleri

Oluştur:
- PropertyOutboxIntegrationTest.java

Senaryolar:
- property change outbox record yazar
- publisher success published olarak işaretler
- transient failure pending/retry metadata bırakır
- crash-window duplicate publish consumer tarafından tolere edilir

Commit: test(messaging): add outbox integration tests

## Task 14 — Inbox/idempotency testleri

Oluştur:
- IdempotentConsumerIntegrationTest.java

Senaryolar:
- ilk message işlenir
- duplicate message no-op
- restart sonrası duplicate hâlâ no-op
- hash policy aktifse aynı id farklı payload conflict

Commit: test(messaging): add inbox idempotency tests

## Task 15 — Consumer crash-window testi

Simüle et:
- business side effect başarılı
- ack/processed marker update boundary başarısız

Re-delivery'nin duplicate business effect üretmediğini doğrula.

Bu, at-least-once correctness için temel bir testtir.

Commit: test(messaging): verify consumer crash-window idempotency

## Task 16 — Architecture testleri

Kurallar:
- domain broker API'lerine bağımlı değil
- application port'ları publish capability tanımlar
- listener'lar yalnızca application handler çağırır
- outbox persistence Kafka class'larını dışarı sızdırmaz
- RabbitMQ publisher infrastructure içinde kalır

Commit: test(messaging): enforce reliability adapter boundaries

## Source-of-truth notu

Bu dosya final Day 15–33 roadmap'i izler. Önceki birleşik Day numaralandırması `docs/roadmap/LEGACY-DAY-MAPPING.md` ile superseded edilmiştir.
