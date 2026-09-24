# ADR-005 — RabbitMQ ve Kafka Sorumlulukları

**Status:** Accepted

## Decision
RabbitMQ command/task-style asynchronous messaging için korunacak; Kafka durable domain-event streaming için kullanılacaktır.

## Referans örnekler

RabbitMQ:
- AuthService → UserProfileService async profile creation

Kafka:
- PropertyPublished
- PropertyUpdated
- PropertyPriceChanged
- PropertyDeleted
- Saga / CQRS event flow'ları

## Rationale
Amaç aynı workflow'u iki broker ile tekrar etmek değil, farklı messaging semantic'lerini öğrenmektir.

## Consequences
İki broker local infrastructure complexity'yi artırır; ancak her biri farklı ve dokümante edilmiş bir role sahip olacaktır.
