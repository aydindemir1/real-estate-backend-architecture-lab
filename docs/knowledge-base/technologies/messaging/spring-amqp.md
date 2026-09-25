# Spring AMQP

**Category:** Technology  
**Introduced:** Day 6A  
**Project status:** Implemented / Integrated / Verified  
**Scope:** Spring abstraction and integration layer for AMQP messaging

## 1. Nedir?

Spring AMQP, AMQP tabanlı messaging sistemleriyle Spring uygulamalarının entegrasyonunu kolaylaştıran Spring projesidir.

RabbitMQ ile kullanım için:
- template abstraction,
- listener container,
- message conversion,
- topology declaration,
- error handling

gibi capability'ler sağlar.

## 2. RabbitMQ ile farkı

RabbitMQ:
> Message broker.

Spring AMQP:
> Java/Spring application'ın RabbitMQ gibi AMQP broker'larla çalışmasını kolaylaştıran framework/integration layer.

Aynı şey değildir.

## 3. Ana bileşenler

### RabbitTemplate

Message publish etmek için high-level abstraction.

### @RabbitListener

Consumer method tanımlamak için declarative listener annotation.

### MessageConverter

Java object ile message payload arasında serialization/deserialization yapar.

### Queue / Exchange / Binding Beans

Broker topology'sini Spring configuration üzerinden declare etmeyi sağlar.

### Listener Container

Consumer lifecycle, connection/channel, acknowledgement ve concurrency yönetimini sağlar.

## 4. Producer flow

```text
Application Service
      |
      v
RabbitTemplate
      |
      v
MessageConverter
      |
      v
RabbitMQ Client
      |
      v
Broker
```

## 5. Consumer flow

```text
RabbitMQ
   |
   v
Listener Container
   |
   v
MessageConverter
   |
   v
@RabbitListener
   |
   v
Application Logic
```

## 6. Message conversion

Java object doğrudan wire üzerinde taşınmaz.

Örneğin JSON converter kullanıldığında:

```text
Java Object
   |
   v
JSON
   |
   v
RabbitMQ Message
```

Consumer tarafında tersine çevrilir.

## 7. Topology declaration

Spring bean'leriyle:
- Queue
- Exchange
- Binding

declare edilebilir.

Application startup sırasında broker topology oluşturulabilir veya doğrulanabilir.

## 8. Acknowledgement

Spring AMQP farklı acknowledgement modellerini destekler.

Policy seçimi:
- processing guarantee
- duplicate delivery
- failure handling

üzerinde doğrudan etkilidir.

## 9. Error handling

Listener failure durumlarında:
- retry
- reject
- requeue
- recoverer
- DLQ

gibi davranışlar configure edilebilir.

Default behavior'a körü körüne güvenilmemelidir.

## 10. Concurrency

Listener container birden fazla consumer thread/channel ile çalışabilir.

Concurrency artırmak throughput'u artırabilir ancak:
- ordering
- downstream capacity
- database concurrency
- memory

dikkate alınmalıdır.

## 11. Bu projede nasıl kullanılıyor?

Day 6A async register flow'unda:

### AuthService
- producer
- RabbitTemplate

### UserProfileService
- consumer
- @RabbitListener

kullanılmıştır.

JSON message conversion ile service'ler arasında message aktarılmıştır.

## 12. Tracing ile entegrasyon

Day 6A'da producer ve consumer span'lerinin aynı distributed trace içinde görünmesi doğrulanmıştır.

Spring/Micrometer instrumentation message boundary boyunca trace context propagation'a yardımcı olur.

## 13. Spring AMQP ile Spring Cloud Stream farkı

Spring AMQP:
> Broker-specific messaging abstraction/integration.

Spring Cloud Stream:
> Binder abstraction üzerinden event-driven application geliştirme modeli.

Bu projede RabbitMQ direct command/work queue kullanımında Spring AMQP bilinçli olarak korunur.

Spring Cloud Stream ileri Kafka/event-streaming milestone'ında ayrıca öğrenilecektir.

## 14. Avantajları

- Spring idiomatic API
- RabbitTemplate abstraction
- declarative listeners
- message conversion
- container lifecycle
- topology declaration
- retry/error handling integration

## 15. Trade-off'ları

- framework defaults broker semantics'i gizleyebilir
- acknowledgement behavior yanlış anlaşılabilir
- excessive annotation magic debugging'i zorlaştırabilir
- topology code ve ops ownership karıştırılabilir

## 16. Production considerations

- listener concurrency
- prefetch
- ack mode
- retry/DLQ
- connection recovery
- publisher confirms
- message converter security
- schema/versioning
- tracing context
- idempotent consumer

## 17. Anti-pattern'ler

- @RabbitListener içine çok fazla business orchestration koymak
- message DTO ile domain entity'yi aynı object yapmak
- retry policy'yi default bırakıp semantics'i bilmemek
- serialization formatını versionlamamak
- duplicate delivery'yi göz ardı etmek

## 18. İleri öğrenme konuları

- RabbitListenerContainerFactory
- manual acknowledgement
- publisher returns/confirms
- retry interceptors
- dead-letter recoverer
- batch listeners
- observation/tracing
