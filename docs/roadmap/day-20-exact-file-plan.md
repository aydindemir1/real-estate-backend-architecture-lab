# Day 20 — Kesin Cassandra Reliable Outbound Messaging Planı

## Kapsam
- Cassandra-friendly durable pending outbound messages
- Seller listing command'ın RabbitMQ'ya reliable dispatch edilmesi
- sonraki Seller Kafka kararları için reusable outbound foundation
- publisher confirms
- Property command inbox/deduplication
- CreatePropertyFromListingCommand
- reliability testleri

## Task'ler

1. Naive save+send, pending-message table/dispatcher, CDC ve distributed transaction seçeneklerini karşılaştıran `ADR-007-cassandra-reliable-outbound-messaging.md` yaz.
2. Broker-neutral durable pending outbound message pattern seç.
3. `pending_outbound_messages_by_bucket` gibi Cassandra table modeli oluştur.
4. Bounded dispatch bucket/shard ile partition yap; `created_at`, `message_id` ile cluster et.
5. Şu alanları persist et: messageId, aggregateId, messageType, destinationKind, payload, status, retryCount, nextAttemptAt, createdAt, correlationId.
6. Unbounded partition veya `ALLOW FILTERING` kullanma.
7. ListingSubmission SUBMITTED durumuna geçtiğinde durable pending `SubmitPropertyListingCommand` persist et.
8. Exact Cassandra consistency guarantee'yi açıkça belirt; relational atomic outbox varmış gibi davranma.
9. `ListingCommandDispatchService` implemente et.
10. Confirms/returns kullanan RabbitMQ publisher adapter implemente et.
11. Pending message'ı yalnızca broker confirmation sonrasında dispatched olarak işaretle.
12. Transient failure'ları retry için pending bırak.
13. Durable pending modeli broker-neutral tut; böylece Day 22'de SellerAccepted/SellerRejected Kafka message'ları aynı temeli kullanabilir.
14. PropertyService içinde `SubmitPropertyListingCommandConsumer` oluştur.
15. commandId ile key'lenen Mongo processed-command/inbox storage ekle.
16. `CreatePropertyFromListingCommand` application use-case implemente et.
17. RabbitMQ ACK yalnızca durable Property creation + inbox başarıyla tamamlandıktan sonra verilsin.
18. Duplicate command duplicate Property oluşturmamalı.
19. Broker-down/recovery testleri ekle.
20. Duplicate-dispatch testleri ekle.
21. Cassandra partition/query testleri ekle.
22. Seller/Property design docs ve messaging topology'yi güncelle.

## Commit sırası
1. `docs(adr): decide Cassandra reliable outbound messaging`
2. `db(seller): add pending outbound message table`
3. `feat(seller): persist pending listing command on submit`
4. `feat(seller): add reliable outbound dispatcher`
5. `feat(rabbitmq): integrate publisher confirms with Seller dispatcher`
6. `feat(property): add listing command inbox deduplication`
7. `feat(property): consume SubmitPropertyListingCommand`
8. `feat(property): create Property from listing command`
9. `test(seller): verify broker outage recovery`
10. `test(property): verify duplicate listing command safety`
11. `docs(messaging): document Cassandra outbound guarantees`

## Final gate
- best-effort Cassandra save→broker send yok
- pending message partition bounded
- dispatcher retryable ve idempotent
- broker outage listing command kaybına yol açmıyor
- Property command consumer commandId ile deduplication yapıyor
- duplicate dispatch yalnızca bir Property oluşturuyor
- durable model sonraki Kafka Seller decision event'lerini destekleyebiliyor
