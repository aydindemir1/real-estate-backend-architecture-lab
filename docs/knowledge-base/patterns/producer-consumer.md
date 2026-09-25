# Producer / Consumer Pattern

**Category:** Pattern  
**Introduced:** Day 6A  
**Project status:** Implemented / Verified  
**Scope:** Decoupled message production and processing

## 1. Nedir?

Producer / Consumer Pattern, bir bileşenin work/message üretip başka bir bileşenin bunu tüketmesini sağlar.

## 2. Temel yapı

```text
Producer
  |
  v
Queue / Broker
  |
  v
Consumer
```

## 3. Problem

Producer'ın işi kendisinin tamamlaması:
- response süresini uzatabilir
- coupling yaratabilir
- workload burst'lerini yönetmeyi zorlaştırabilir

## 4. Queue ne sağlar?

- buffering
- decoupling
- deferred processing
- consumer scaling

## 5. Competing Consumers

Bir queue'yu birden fazla consumer okuyabilir.

```text
Queue
  +--> Consumer 1
  +--> Consumer 2
  +--> Consumer 3
```

Bu throughput artırabilir.

## 6. Bu projede nasıl kullanılıyor?

Day 6A:
- AuthService producer
- RabbitMQ broker/queue
- UserProfileService consumer

şeklinde async register flow oluşturulmuştur.

## 7. Avantajları

- temporal decoupling
- load leveling
- independent scaling
- retry capability

## 8. Riskler

- duplicate delivery
- message ordering
- poison message
- consumer lag
- idempotency requirement

## 9. Production considerations

- ack/nack
- prefetch
- concurrency
- DLQ
- retry
- idempotency
- message schema versioning

## 10. İlgili kavramlar

- Asynchronous Messaging
- Work Queue
- Competing Consumers
- Idempotent Consumer
- DLQ

## 11. İleri öğrenme konuları

- backpressure
- consumer groups
- quorum queues
- message replay
