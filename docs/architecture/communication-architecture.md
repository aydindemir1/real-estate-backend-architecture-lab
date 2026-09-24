# Communication Architecture

## Existing baseline

- REST / Spring MVC
- Spring Cloud OpenFeign
- RabbitMQ / Spring AMQP
- Eureka + Spring Cloud LoadBalancer
- API Gateway

## Planned communication responsibilities

### REST
Primary public HTTP API and baseline synchronous integration style.

### OpenFeign
Existing internal HTTP client style where REST remains appropriate.

### gRPC
Internal low-latency, strongly defined service-to-service contracts such as BuyerService → AgentService checks.

### GraphQL
Flexible read/query use cases where clients benefit from field selection. It is not intended to replace all REST endpoints.

### RabbitMQ
Command/task-oriented asynchronous flows. Existing AuthService → UserProfileService async flow remains as the reference use case.

### Kafka
Durable domain-event streaming and multi-consumer event-driven flows.

Expected property events include:
- PropertyListingSubmitted
- PropertyCreated
- PropertyPublished
- PropertyUpdated
- PropertyPriceChanged
- PropertyDeleted

## Reliability patterns

Kafka/RabbitMQ messaging will be extended with:
- idempotency
- retry
- DLQ/DLT
- Outbox/Inbox where datastore guarantees make sense
- eventual consistency
- Saga choreography

## Rule

Do not use two protocols for the same flow merely to demonstrate technology. Each protocol must have a distinct learning or architectural reason.
