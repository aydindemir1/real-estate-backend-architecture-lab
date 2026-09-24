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
BuyerService → AgentService gibi düşük latency ve strongly-defined contract gerektiren internal service-to-service use-case'ler.

### GraphQL
Client'ın field selection avantajından yararlandığı flexible read/query use-case'leri. Tüm REST endpoint'lerin replacement'ı değildir.

### RabbitMQ
Command/task-oriented asynchronous flow'lar. Mevcut AuthService → UserProfileService async akışı referans use-case olarak korunur.

### Kafka
Durable domain-event streaming ve multi-consumer Event-Driven Architecture akışları.

Beklenen property event'leri:
- PropertyListingSubmitted
- PropertyCreated
- PropertyPublished
- PropertyUpdated
- PropertyPriceChanged
- PropertyDeleted

## Reliability pattern'leri

Kafka/RabbitMQ messaging ilerleyen günlerde şu pattern'lerle genişletilecek:
- Idempotency
- Retry
- DLQ/DLT
- Outbox / Inbox
- Eventual Consistency
- Saga Choreography

## Kural

Aynı flow'u yalnızca teknoloji göstermek amacıyla iki farklı protocol ile uygulamayacağız. Her protocol'ün açık bir architecture veya learning gerekçesi olacak.
