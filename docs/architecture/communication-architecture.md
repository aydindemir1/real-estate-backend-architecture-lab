# Communication Architecture

## Mevcut baseline

- REST / Spring MVC
- Spring Cloud OpenFeign
- RabbitMQ / Spring AMQP
- Eureka + Spring Cloud LoadBalancer
- API Gateway

## Planlanan iletişim rolleri

### REST
Primary public HTTP API ve synchronous integration baseline.

### OpenFeign
REST'in uygun olduğu mevcut internal HTTP client modeli.

### gRPC
BuyerService → AgentService gibi low-latency ve strongly-defined contract gerektiren internal service-to-service use-case'ler.

### GraphQL
Flexible read/query use-case'leri. Tüm REST endpoint'lerin replacement'ı değildir.

### RabbitMQ
Command / Work Queue ağırlıklı asynchronous flow.

Mevcut:
- AuthService → UserProfileService async profile creation

Yeni target flow:
- SellerService → `SubmitPropertyListingCommand` → PropertyService

### Kafka
Durable Domain Event Streaming, CQRS Projection ve Saga event akışları.

Initial property event set:
- PropertyCreated
- PropertyPublished
- PropertyUpdated
- PropertyPriceChanged
- PropertyHeld
- PropertyHoldReleased
- PropertyReserved
- PropertyWithdrawn
- PropertySold

Initial offer event set:
- OfferRequested
- SellerAccepted
- SellerRejected
- OfferExpired

`OfferAccepted` / `OfferRejected` yalnızca gerçek downstream consumer ihtiyacı doğarsa eklenir.

## Reliability pattern'leri

- Idempotent Consumer
- Retry
- DLQ / DLT
- Outbox / Inbox
- Eventual Consistency
- Saga Choreography

## Önemli not

SellerService Cassandra state write ile RabbitMQ publish arasındaki reliable publication stratejisi henüz final değildir. Bu cross-service command flow Day 7 foundation kapsamında uygulanmayacak; Day 11 öncesi Cassandra'ya uygun reliability strategy netleştirilecektir.

## Kural

Aynı flow yalnızca teknoloji göstermek amacıyla iki farklı protocol ile uygulanmaz. Her protocol'ün açık architecture veya learning gerekçesi vardır.
