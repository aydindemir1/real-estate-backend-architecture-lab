# RabbitMQ

**Category:** Technology  
**Introduced:** Day 6A  
**Project status:** Implemented / Integrated / Verified  
**Scope:** Message broker for command/work queue communication

## 1. Nedir?

RabbitMQ, producer ve consumer bileşenleri arasında message-based asynchronous communication sağlayan message broker'dır.

AMQP modelini temel alır ve özellikle queue-oriented messaging, work distribution ve routing senaryolarında kullanılır.

## 2. Hangi problemi çözer?

Producer'ın consumer'ı doğrudan ve senkron çağırması:
- temporal coupling,
- availability dependency,
- latency coupling,
- burst traffic problemi

oluşturabilir.

RabbitMQ araya broker koyarak producer ve consumer'ı zaman açısından ayrıştırır.

## 3. Temel mimari

```text
Producer
   |
   v
Exchange
   |
   v
Binding + Routing Key
   |
   v
Queue
   |
   v
Consumer
```

Producer doğrudan queue'ya değil, çoğunlukla exchange'e publish eder.

## 4. Exchange

Exchange mesajın hangi queue veya queue'lara yönleneceğini belirler.

Başlıca exchange türleri:

### Direct Exchange

Routing key exact match ile route eder.

### Topic Exchange

Pattern tabanlı routing sağlar.

### Fanout Exchange

Bağlı tüm queue'lara broadcast eder.

### Headers Exchange

Routing kararını message header'larına göre verir.

## 5. Queue

Queue mesajları consumer işleyene kadar buffer'lar.

Queue özellikleri:
- durable / non-durable
- exclusive
- auto-delete
- arguments

## 6. Binding

Exchange ile queue arasındaki routing ilişkisidir.

```text
Exchange
   |
   | binding
   v
Queue
```

## 7. Routing Key

Producer mesajı publish ederken routing metadata sağlar.

Direct/topic exchange bu key üzerinden route kararı verebilir.

## 8. Consumer

Consumer queue'dan message alır ve işler.

Bir queue üzerinde birden fazla consumer olabilir.

Bu durumda Competing Consumers modeliyle workload paylaşılabilir.

## 9. Acknowledgement

Consumer message'ı aldıktan sonra broker'a sonucu bildirir.

### ACK

Message başarıyla işlendi.

### NACK / Reject

Message işlenemedi.

Policy'ye göre:
- requeue,
- dead-letter,
- discard

davranışı uygulanabilir.

## 10. Delivery semantics

RabbitMQ'da practical delivery modeli çoğunlukla at-least-once semantics'e yakındır.

Bu nedenle duplicate delivery mümkündür.

Consumer idempotent tasarlanmalıdır.

## 11. Durability

Mesaj kaybını azaltmak için birlikte değerlendirilmesi gerekenler:
- durable exchange
- durable queue
- persistent message
- publisher confirms
- replicated/quorum queue strategy

Tek bir ayarın açık olması tam durability garantisi değildir.

## 12. Publisher Confirms

Broker'ın publish edilen message'ı kabul ettiğini producer'a bildirmesini sağlar.

Bu, reliable publication tasarımında önemli primitive'dir.

Ancak database commit + broker publish atomicity problemini tek başına çözmez.

Bu nedenle Outbox gibi pattern'ler gerekebilir.

## 13. Dead Letter Exchange

İşlenemeyen veya expire olan message'lar başka bir exchange'e yönlendirilebilir.

```text
Main Queue
   |
   | failure / reject / ttl
   v
Dead Letter Exchange
   |
   v
DLQ
```

## 14. Retry ile ilişkisi

Retry:
- consumer in-memory
- delayed queue
- TTL + dead lettering
- dedicated retry topology

gibi farklı şekillerde uygulanabilir.

Retry ile DLQ aynı şey değildir.

## 15. Prefetch

Consumer'ın aynı anda kaç unacked message alabileceğini sınırlar.

Prefetch tuning:
- throughput
- fairness
- memory
- slow consumer behavior

üzerinde etkilidir.

## 16. Connection ve Channel

RabbitMQ client:
- TCP Connection
- logical Channel

kavramlarını kullanır.

Genellikle her operation için yeni TCP connection açılmaz.

Channel daha lightweight logical communication unit'tir.

## 17. Bu projede nasıl kullanılıyor?

Day 6A'da async register flow için kullanılmıştır:

```text
AuthService
   |
   | publish
   v
RabbitMQ
   |
   v
UserProfileService
```

AuthService producer, UserProfileService consumer rolündedir.

Bu async flow mevcut OpenFeign synchronous flow'u kaldırmamış, alternatif capability olarak eklenmiştir.

## 18. Bu projedeki semantic rolü

Roadmap'te RabbitMQ şu amaçla konumlandırılmıştır:

> Targeted Command / Work Queue — "do this"

Örnek ileri kullanım:
- SubmitPropertyListingCommand

Kafka ise:
> Domain Event Streaming — "this happened"

için ayrılmıştır.

## 19. RabbitMQ ile Kafka farkı

RabbitMQ:
- queue/work delivery
- command-oriented routing
- flexible exchange routing
- acknowledgement-driven consumer processing

Kafka:
- append-only distributed log
- event streaming
- retention/replay
- partition-based ordered streams

Birbirinin tam alternatifi değildir.

## 20. Observability

İzlenmesi gerekenler:
- queue depth
- ready messages
- unacked messages
- publish rate
- consume rate
- redelivery
- connection/channel count
- consumer count
- DLQ growth

## 21. Production considerations

- quorum queues
- publisher confirms
- consumer acknowledgements
- idempotency
- prefetch
- retry/DLQ topology
- connection recovery
- TLS
- credentials
- vhost isolation
- monitoring
- disk/memory alarms
- message schema governance

## 22. Anti-pattern'ler

- queue'yu database gibi kullanmak
- unbounded retry
- duplicate delivery'yi yok saymak
- giant message payload
- one queue for unrelated workloads
- business event ile command semantics'ini karıştırmak
- ack'ı processing tamamlanmadan vermek

## 23. Alternatifleri

- Apache Kafka
- ActiveMQ Artemis
- NATS
- cloud queue services

## 24. İleri öğrenme konuları

- quorum queues
- streams
- publisher confirms
- alternate exchanges
- dead lettering
- consumer priorities
- federation
- shovel
- cluster internals
