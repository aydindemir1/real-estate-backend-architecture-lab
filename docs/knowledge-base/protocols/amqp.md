# AMQP

**Category:** Messaging Protocol / Model  
**Introduced:** Day 6A  
**Project status:** Implemented through RabbitMQ / Spring AMQP  
**Scope:** Broker-oriented messaging semantics

## 1. Nedir?

AMQP, Advanced Message Queuing Protocol adını taşıyan messaging protocol/model ailesidir.

Message-oriented middleware sistemlerinde:
- producer
- broker
- routing
- queue
- consumer

iletişimini standardize etmeyi amaçlar.

## 2. RabbitMQ ile ilişkisi

RabbitMQ AMQP ekosistemiyle güçlü ilişkiye sahip message broker'dır.

Spring AMQP ise Java/Spring integration framework'üdür.

Ayrım:
- AMQP -> protocol/model
- RabbitMQ -> broker
- Spring AMQP -> application integration library

## 3. Messaging modeli

RabbitMQ bağlamında yaygın akış:

```text
Publisher
  |
  v
Exchange
  |
  v
Binding
  |
  v
Queue
  |
  v
Consumer
```

## 4. Message kavramı

Message:
- payload/body
- headers/properties

taşıyabilir.

## 5. Routing

Routing:
- exchange type
- routing key
- binding

üzerinden yapılır.

## 6. Delivery acknowledgement

Consumer processing sonucu:
- ack
- nack
- reject

semantics'i ile broker'a bildirilebilir.

## 7. Reliability primitives

AMQP/RabbitMQ tarafında:
- durable queue
- persistent message
- publisher confirm
- acknowledgement
- dead lettering

gibi reliability primitives bulunur.

Bunlar application-level exactly-once business processing garantisi değildir.

## 8. Bu projede nasıl kullanılıyor?

Day 6A async registration flow:
- AuthService producer
- RabbitMQ broker
- UserProfileService consumer

şeklindedir.

Spring AMQP integration katmanı olarak kullanılır.

## 9. Command/work queue rolü

Bu projede RabbitMQ/AMQP yaklaşımı targeted command/work queue için konumlandırılmıştır.

Kafka event streaming ayrı semantics taşır.

## 10. Avantajları

- broker-mediated decoupling
- routing flexibility
- acknowledgement
- queue semantics
- workload buffering

## 11. Trade-off'ları

- duplicate delivery
- ordering
- retry topology
- broker operations
- message schema evolution

## 12. AMQP 0-9-1 ve AMQP 1.0

AMQP ailesinde farklı protocol sürümleri/model yaklaşımları vardır.

RabbitMQ'nun yaygın core messaging modeli AMQP 0-9-1 semantics'iyle ilişkilidir.

AMQP 1.0 farklı wire protocol/model yaklaşımına sahiptir.

## 13. Production considerations

- TLS
- auth
- vhost
- ack mode
- publisher confirms
- DLQ
- retry
- idempotency
- monitoring

## 14. İleri öğrenme konuları

- AMQP frame model
- channel multiplexing
- publisher confirms
- transactions
- AMQP 1.0 comparison
