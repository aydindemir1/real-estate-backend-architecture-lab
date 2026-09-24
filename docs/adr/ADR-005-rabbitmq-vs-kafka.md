# ADR-005 — RabbitMQ and Kafka Responsibilities

**Status:** Accepted

## Decision
Keep RabbitMQ for command/task-style asynchronous messaging and use Kafka for durable domain-event streaming.

## Reference examples

RabbitMQ:
- AuthService → UserProfileService async profile creation

Kafka:
- PropertyPublished
- PropertyUpdated
- PropertyPriceChanged
- PropertyDeleted
- Saga/CQRS event flows

## Rationale
The project should teach semantic differences, not duplicate the same workflow with two brokers.

## Consequences
Two brokers increase local infrastructure complexity, but each has a distinct and documented purpose.
