# SellerService — Package Design

Base package:

`com.aydindemir.seller`

## Layers

### domain

- `domain.model`
- `domain.event`
- `domain.repository`
- `domain.service`
- `domain.exception`

Outer-framework dependency yoktur.

### application

- `application.command`
- `application.query`
- `application.service`
- `application.port`

Domain use-case orchestration burada yapılır.

### infrastructure

- `infrastructure.cassandra.table`
- `infrastructure.cassandra.repository`
- `infrastructure.cassandra.mapper`
- `infrastructure.cassandra.adapter`
- `infrastructure.configuration`

Cassandra-specific implementation burada kalır.

### presentation

- `presentation.rest.request`
- `presentation.rest.response`
- `presentation.rest.mapper`
- REST controllers / advice

## Enforced rules

ArchUnit:
- domain outer layer'a bağımlı olamaz
- application infrastructure/presentation'a bağımlı olamaz
- domain Spring/Cassandra/Kafka/RabbitMQ bağımlılığı taşıyamaz
- package cycles yasaktır

Runtime parameter binding explicit annotation names ile yapılır.
