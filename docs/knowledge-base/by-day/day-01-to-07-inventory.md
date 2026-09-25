# Day 1–7 Initial Knowledge Inventory

Bu dosya, ilk yedi Day sonunda Knowledge Base'e alınacak kavramların başlangıç envanteridir. Canonical konu dosyaları ayrı ayrı oluşturulacaktır.

## Architectures

- Microservices Architecture
- Layered / N-Layer Architecture

## Approaches

- Synchronous Communication
- Asynchronous Messaging
- Centralized Configuration
- Externalized Configuration
- Distributed Tracing
- Polyglot Persistence

## Principles

- Separation of Concerns
- Loose Coupling
- Service Autonomy
- Data Ownership
- Configuration Externalization

## Patterns

- API Gateway Pattern
- Circuit Breaker Pattern
- Fallback Pattern
- Service Registry Pattern
- Service Discovery Pattern
- Client-Side Load Balancing
- Database per Service
- Producer / Consumer
- Health Check

## Technologies

### Core Java / Build
- Java 21
- Gradle
- Lombok
- MapStruct

### Spring / API
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate
- SpringDoc OpenAPI
- Auth0 java-jwt

### Spring Cloud
- Spring Cloud OpenFeign
- Spring Cloud Config
- Spring Cloud Gateway
- Spring Cloud Circuit Breaker
- Spring Cloud Netflix Eureka
- Spring Cloud LoadBalancer

### Reliability / Observability
- Resilience4j
- Spring Boot Actuator
- Micrometer Tracing
- Brave
- Zipkin

### Messaging
- RabbitMQ
- Spring AMQP

### Containerization
- Docker
- Docker Compose
- Docker Compose Profiles

### Datastores
- PostgreSQL — Implemented / Verified
- MySQL — Infrastructure Ready
- MongoDB — Infrastructure Ready
- Couchbase — Infrastructure Ready
- Cassandra — Infrastructure Ready
- Elasticsearch — Infrastructure Ready for Search projection
- Redis — Infrastructure Ready for ephemeral use cases

## Protocols & Formats

- HTTP
- REST
- JSON
- AMQP
- JWT

## Day 7 state distinction

Day 7'de MySQL, MongoDB, Couchbase, Cassandra, Elasticsearch ve Redis container altyapıları hazırlanıp healthcheck ile doğrulanmıştır. Bunların application-level kullanımının tamamlandığı anlamına gelmez.

Service integration roadmap:
- Day 8 — Agent + MySQL
- Day 9 — Buyer + Couchbase
- Day 10 — Seller + Cassandra
- Day 11 — Property + MongoDB
- Day 12 — Search + Elasticsearch
- Day 13 — Redis application integration
