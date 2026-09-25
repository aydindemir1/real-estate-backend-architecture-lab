# Messaging

Message broker, messaging framework ve asynchronous communication teknolojileri.

## Day 1–7

- [RabbitMQ](rabbitmq.md)
- [Spring AMQP](spring-amqp.md)

## Kavramsal ayrım

- RabbitMQ -> Message broker
- Spring AMQP -> Spring/RabbitMQ integration framework
- Producer / Consumer -> Pattern
- Asynchronous Messaging -> Approach
- AMQP -> Protocol/model family

Bu kavramlar aynı şey değildir ve canonical dokümanları farklı kategorilerde tutulur.

## Bu projedeki semantic role

Day 6 itibarıyla RabbitMQ:
- targeted command
- work queue
- asynchronous processing

için kullanılmaktadır.

Roadmap'in ilerleyen aşamalarında Kafka:
- domain event streaming
- CQRS projection
- saga event flow

için ayrı teknoloji olarak eklenecektir.

## Sonraki roadmap kapsamı

- Retry topology
- DLQ
- idempotent consumer
- reliable publication
- Spring Cloud Stream
- Apache Kafka
