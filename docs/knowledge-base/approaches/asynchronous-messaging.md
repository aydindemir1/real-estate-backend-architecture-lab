# Asynchronous Messaging

**Category:** Approach  
**Introduced:** Day 6A  
**Project status:** Implemented / Integrated / Verified  
**Scope:** Message-based decoupled communication

## 1. Nedir?

Asynchronous Messaging, producer'ın bir mesajı broker veya queue'ya gönderip consumer'ın aynı anda hazır olmasını beklemeden işlemi ilerletebildiği communication yaklaşımıdır.

```text
Producer
   |
   v
Broker / Queue
   |
   v
Consumer
```

## 2. Hangi problemi çözer?

Synchronous request/response modelindeki temporal coupling'i azaltır.

Producer ve consumer'ın aynı anda ayakta olması zorunluluğunu azaltarak:
- decoupling
- buffering
- deferred processing
- workload smoothing

sağlar.

## 3. Ne işe yarar?

- producer ve consumer lifecycle'larını ayrıştırır
- burst traffic'i buffer'lar
- slow consumer'ların sistemi doğrudan bloklamasını engeller
- background processing sağlar
- command/work queue senaryolarını destekler
- event-driven flow'lara temel olabilir

## 4. Hangi senaryolarda kullanılır?

- immediate response gerekli değilse
- iş arka planda yapılabiliyorsa
- workload queue ile dengelenmek isteniyorsa
- producer'ın consumer availability'sine bağımlılığı azaltılmak isteniyorsa
- retry/DLQ gibi broker tabanlı reliability isteniyorsa

## 5. Command ile Event farkı

Asynchronous olmak command ve event'in aynı şey olduğu anlamına gelmez.

Command:
> "Bu işi yap."

Event:
> "Bu olay gerçekleşti."

Bu projede:
- RabbitMQ -> Command / Work Queue
- Kafka -> Domain Event Streaming

olarak ayrıştırılmıştır.

## 6. Temel kavramlar

- Producer
- Broker
- Exchange / Topic
- Queue
- Routing
- Consumer
- Acknowledgement
- Redelivery
- Persistence
- Retry
- DLQ / DLT
- Idempotency
- Ordering

## 7. İç işleyiş

```text
Producer
  |
  | publish
  v
Broker
  |
  | routing
  v
Queue
  |
  | delivery
  v
Consumer
  |
  | ack / nack
  v
Broker
```

Consumer başarısız olursa broker politikasına göre message:
- yeniden teslim edilebilir,
- retry queue'ya gidebilir,
- dead-letter queue'ya taşınabilir.

## 8. Avantajları

- loose temporal coupling
- buffering
- resilience
- throughput smoothing
- independent consumer scaling
- background processing
- retry/DLQ desteği

## 9. Dezavantajları

- eventual consistency
- duplicate delivery ihtimali
- ordering problemleri
- debugging zorluğu
- distributed tracing ihtiyacı
- idempotent consumer gereksinimi
- operational complexity

## 10. Delivery semantics

Pratikte yaygın semantik:
- at-most-once
- at-least-once
- effectively-once business processing

"Exactly once" ifadesi dikkatli kullanılmalıdır; broker, datastore ve side effect boundary birlikte değerlendirilmelidir.

## 11. Bu projede nasıl kullanılıyor?

Day 6A'da AuthService -> UserProfileService için alternatif async flow eklenmiştir:

```text
POST /auth/register-async
        |
        v
AuthService
        |
        v
RabbitMQ
        |
        v
UserProfileService
```

Spring AMQP:
- RabbitTemplate
- @RabbitListener
- JSON message conversion

ile kullanılmıştır.

## 12. Observability

Async sistemlerde trace context message header'ları üzerinden taşınmalıdır.

Projede RabbitMQ producer ve consumer span'leri Zipkin üzerinde aynı distributed trace içinde doğrulanmıştır.

## 13. Production considerations

- message schema versioning
- retry ownership
- poison message handling
- DLQ
- idempotency
- duplicate delivery
- ordering requirement
- consumer concurrency
- prefetch
- backpressure
- broker durability
- replay strategy

## 14. Alternatifleri

- synchronous REST
- Kafka event streaming
- gRPC
- scheduled polling

## 15. İleri öğrenme konuları

- transactional outbox
- inbox
- idempotent consumer
- dead-letter topology
- competing consumers
- message ordering
- RabbitMQ quorum queues
- Kafka consumer groups
