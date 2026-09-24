# Messaging Standard

Bu doküman RabbitMQ ve Kafka kullanımında ortak messaging prensiplerini ve teknolojiye özel best practice'leri tanımlar.

## 1. Messaging semantic önce gelir
- Önce bunun Command, Event, Work Queue, Event Stream, Projection update veya Saga interaction olup olmadığı belirlenir.
- Broker seçimi business semantic'ten sonra yapılır.

## 2. RabbitMQ ve Kafka rolleri
- RabbitMQ: Command, Work Queue, targeted processing, ACK/NACK, competing consumer.
- Kafka: Domain Event Streaming, replay, consumer group, ordered partition stream, CQRS Projection, Saga event flow.
- Aynı use-case iki broker ile sırf teknoloji göstermek için tekrar edilmez.

## 3. Command vs Event
- Command intent ifade eder: SubmitPropertyListingCommand.
- Event geçmişte olmuş business fact ifade eder: PropertyPublished.
- Event imperative isim taşımaz.

## 4. Message Envelope
Ortak metadata: messageId/eventId/commandId, messageType, occurredAt/createdAt, correlationId, causationId, schemaVersion, producer, payload.

## 5. Correlation ve Causation
- correlationId aynı business flow'u ilişkilendirir.
- causationId hangi message'ın yeni message'ı tetiklediğini gösterir.
- Örnek: PropertyHeld.causationId = OfferRequested.eventId.

## 6. Delivery semantics
- Duplicate delivery normal kabul edilir.
- Exactly-once business semantic varsayılmaz.
- Consumer idempotent tasarlanır.

## 7. Idempotent Consumer
- commandId/eventId üzerinden duplicate detection yapılır.
- Aynı message tekrar işlendiğinde duplicate business side-effect üretmemelidir.
- Mümkünse business state ile processed-message kaydı aynı local transaction boundary'de tutulur.

## 8. Inbox Pattern
Primary candidate'lar: PropertyService RabbitMQ consumer, SearchService Kafka consumers, Saga consumers.
Inbox alanları: messageId, messageType, processedAt, status.
Retention policy zorunludur.

## 9. Outbox Pattern
Primary candidate PropertyService'tir.
DB state change ile integration event publication arasındaki dual-write riskini azaltır.
Akış: local transaction içinde business state + outbox record; commit sonrası publisher outbox'tan broker'a yayın yapar ve kaydı published olarak işaretler.

## 10. Dual Write Anti-Pattern
DB save ve broker publish birbirinden bağımsız iki side-effect olarak bırakılmaz. Aradaki failure consistency problemi yaratır.

## 11. Retry
- Sadece transient failure için uygulanır.
- Retry öncesi operation idempotent mi, broker redelivery zaten var mı, duplicate side-effect riski var mı değerlendirilir.
- Exponential backoff + jitter tercih edilir.

## 12. Poison Message ve DLQ/DLT
- Validation/schema/impossible-state gibi retry ile düzelmeyecek message non-retryable kabul edilir.
- RabbitMQ: DLX/DLQ.
- Kafka: DLT.
- DLQ/DLT observable olmalı, replay/runbook ve root-cause süreci bulunmalıdır.

## 13. Ordering
- Kafka ordering partition içindedir.
- property.events key = propertyId.
- offer.events key = offerId.
- RabbitMQ'da concurrency/prefetch/requeue ordering'i etkileyebilir; strict ordering gerekiyorsa bilinçli trade-off gerekir.

## 14. Consumer Concurrency ve Backpressure
- Concurrency throughput artırır fakat ordering, DB contention ve downstream pressure yaratabilir.
- RabbitMQ prefetch bilinçli ayarlanır.
- Kafka consumer parallelism partition sayısı ile uyumlu olmalıdır.
- Overload halinde sonsuz retry yapılmaz.

## 15. ACK/NACK ve Offset
- RabbitMQ ACK business processing gerçekten başarılı olduktan sonra verilir.
- Retryable failure NACK/requeue veya retry flow'a gider.
- Non-retryable failure DLQ'ya gider.
- Kafka offset processing başarıyla tamamlanmadan commit edilmemelidir.

## 16. Consumer Group
- Aynı logical consumer capability aynı group içinde scale edilir.
- Farklı business consumer'lar farklı group kullanır.
- Örnek: search-projection-group, seller-offer-projection-group.

## 17. Partition ve Retention
- Partition sayısı throughput, consumer parallelism ve ordering ihtiyacına göre belirlenir.
- Retention replay ihtiyacı, storage ve compliance'a göre belirlenir.

## 18. Replay
- Kafka replay Search Projection rebuild için kullanılabilir.
- Alternatif olarak source-of-truth'tan reindex yapılabilir.
- Hangi stratejinin kullanılacağı use-case bazında belgelenir.

## 19. Schema Evolution
- Breaking değişiklikten kaçınılır.
- Yeni optional field eklemek tercih edilir.
- Field rename/remove aşamalı yapılır.
- schemaVersion taşınır.
- Gerekirse semantic olarak yeni event type oluşturulur.

## 20. Serialization
- Başlangıçta JSON kabul edilir.
- Avro/Protobuf/Schema Registry yalnızca gerçek ihtiyaç varsa eklenir.
- Java native serialization kullanılmaz.

## 21. Message Size ve Sensitive Data
- Large payload/binary file broker message içine konmaz.
- Password, access token, secret, credential taşınmaz.
- Event yalnızca business fact ve consumer için gerekli context'i taşır.

## 22. Naming
- Kafka topic: property.events, offer.events.
- RabbitMQ exchange: real-estate.listing.commands.
- Queue: property.listing.submit.
- Event isimleri past tense, command isimleri imperative intent taşır.

## 23. Topic Granularity
- Her event type için ayrı topic açılmaz.
- Domain/use-case bazlı grouping tercih edilir.
- Retention/security/throughput ihtiyacı farklıysa ayrı topic değerlendirilebilir.

## 24. Consumer Boundary
Consumer yalnızca deserialize, basic envelope validation, deduplication, application handler çağrısı ve success/failure mapping yapar. Business logic consumer içine gömülmez.

## 25. Producer Boundary
Producer application/integration event'i transport representation'a map eder, metadata ekler ve publish eder. Domain model broker API'sini bilmez.

## 26. RabbitMQ reliability
- Critical command publish için Publisher Confirm değerlendirilebilir.
- Unroutable message için mandatory/alternate exchange seçenekleri değerlendirilebilir.
- ACK/NACK, Retry, TTL, DLX/DLQ ve Prefetch bilinçli konfigüre edilir.

## 27. Kafka reliability
- Auto-commit'e kör güvenilmez.
- Consumer group, partition key, offset ve retry/DLT policy açık olmalıdır.
- Kafka transaction özelliği business-level exactly-once varsayımı yaratmaz.

## 28. Saga Choreography
- BuyerService, PropertyService ve SellerService event ile reaksiyon verir.
- İlk aşamada central orchestrator yoktur.
- Compensation rollback değildir; business action'dır. Örnek: PropertyHoldReleased.
- Saga canonical state yalnızca Redis'te tutulmaz.

## 29. Projedeki RabbitMQ use-case
SellerService -> SubmitPropertyListingCommand -> RabbitMQ -> PropertyService.
Bu Command Messaging örneğidir.

## 30. Projedeki Kafka use-case'leri
- PropertyService -> property.events -> SearchService.
- BuyerService -> OfferRequested -> PropertyService -> PropertyHeld -> SellerService -> SellerAccepted/SellerRejected -> PropertyService.

## 31. Spring Cloud Stream ve Function
- Kafka integration'da binder abstraction ve functional model öğrenilir.
- Broker-specific davranış gerektiğinde tamamen gizlenmez.
- Function side-effect boundary'si açık tutulur.

## 32. Spring AMQP
RabbitMQ exchange/queue/binding, listener, retry/DLQ ve publisher confirm bilinçli konfigüre edilir.

## 33. Observability
Message context: messageId, correlationId, causationId, topic/queue, partition/offset gerektiğinde, consumer group, retry count.
Metrics: publish/consume rate, processing latency, failure rate, retry count, DLQ/DLT count, consumer lag, queue depth, redelivery.

## 34. Tracing ve Logging
- HTTP -> producer -> broker -> consumer -> downstream trace context propagate edilir.
- Full payload loglanmaz.
- Sensitive/large data maskelenir.

## 35. Testing
- Producer contract.
- Consumer contract.
- Duplicate delivery.
- Retryable failure.
- Non-retryable failure.
- DLQ/DLT.
- Ordering assumption.
- Schema compatibility.
- Kafka ve RabbitMQ için Testcontainers.

## 36. Failure Injection
İleri testlerde broker unavailable, DB unavailable, duplicate message, poison message ve delayed consumer senaryoları test edilir.

## 37. Messaging Security
Broker authentication, TLS gerektiğinde, topic/queue authorization, least privilege ve secret management uygulanır.

## 38. Anti-Pattern'ler
- Kafka'yı RPC gibi kullanmak.
- RabbitMQ ve Kafka'yı aynı use-case'te anlamsız duplicate etmek.
- Consumer içinde business logic.
- Retry forever.
- DLQ/DLT'yi göz ardı etmek.
- Entire entity'yi event payload'a koymak.
- Message içinde secret taşımak.
- Idempotency'siz consumer.
- Global ordering varsaymak.
- Auto-commit'e kör güvenmek.
- DB save + broker publish dual-write.
- Breaking schema change.
- Her event için ayrı topic.
- Full payload logging.

## 39. Messaging Review Checklist
- Message Command mı Event mi?
- Broker seçimi semantic'e uygun mu?
- Delivery semantic ne?
- Consumer idempotent mi?
- Duplicate delivery test edildi mi?
- Ordering requirement ve key doğru mu?
- Retryable/non-retryable ayrımı var mı?
- DLQ/DLT tasarlandı mı?
- Outbox/Inbox gerekiyor mu?
- Schema evolution planı var mı?
- Correlation/causation var mı?
- Sensitive data var mı?
- Message size makul mü?
- Consumer lag/queue depth observable mı?
- Replay strategy var mı?