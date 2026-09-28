# Day 1–7 Initial Knowledge Inventory

Bu dosya ilk yedi Day sonunda Knowledge Base'e alınan canonical kavramların envanteridir.

## Architectures

- [Microservices Architecture](../architectures/microservices-architecture.md)
- [Layered / N-Layer Architecture](../architectures/layered-n-layer-architecture.md)

## Approaches

- [Synchronous Communication](../approaches/synchronous-communication.md)
- [Asynchronous Messaging](../approaches/asynchronous-messaging.md)
- [Centralized Configuration](../approaches/centralized-configuration.md)
- [Externalized Configuration](../approaches/externalized-configuration.md)
- [Distributed Tracing](../approaches/distributed-tracing.md)
- [Polyglot Persistence](../approaches/polyglot-persistence.md)

## Principles

- [Separation of Concerns](../principles/separation-of-concerns.md)
- [Loose Coupling](../principles/loose-coupling.md)
- [Service Autonomy](../principles/service-autonomy.md)
- [Data Ownership](../principles/data-ownership.md)
- [Configuration Externalization](../principles/configuration-externalization.md)

## Patterns

- [API Gateway](../patterns/api-gateway.md)
- [Circuit Breaker](../patterns/circuit-breaker.md)
- [Fallback](../patterns/fallback.md)
- [Service Registry](../patterns/service-registry.md)
- [Service Discovery](../patterns/service-discovery.md)
- [Client-Side Load Balancing](../patterns/client-side-load-balancing.md)
- [Database per Service](../patterns/database-per-service.md)
- [Producer / Consumer](../patterns/producer-consumer.md)
- [Health Check](../patterns/health-check.md)

## Technologies

### Core Java & Build
- [Java 21](../technologies/core-java-build/java-21.md)
- [Gradle](../technologies/core-java-build/gradle.md)
- [Lombok](../technologies/core-java-build/lombok.md)
- [MapStruct](../technologies/core-java-build/mapstruct.md)

### Spring & API
- [Spring Boot](../technologies/spring-api/spring-boot.md)
- [Spring MVC](../technologies/spring-api/spring-mvc.md)
- [Spring Data JPA](../technologies/spring-api/spring-data-jpa.md)
- [Hibernate](../technologies/spring-api/hibernate.md)
- [SpringDoc OpenAPI](../technologies/spring-api/springdoc-openapi.md)

### Spring Cloud
- [Spring Cloud OpenFeign](../technologies/spring-cloud/spring-cloud-openfeign.md)
- [Spring Cloud Config](../technologies/spring-cloud/spring-cloud-config.md)
- [Spring Cloud Gateway](../technologies/spring-cloud/spring-cloud-gateway.md)
- [Spring Cloud Circuit Breaker](../technologies/spring-cloud/spring-cloud-circuit-breaker.md)
- [Spring Cloud Netflix Eureka](../technologies/spring-cloud/spring-cloud-netflix-eureka.md)
- [Spring Cloud LoadBalancer](../technologies/spring-cloud/spring-cloud-loadbalancer.md)

### Observability
- [Spring Boot Actuator](../technologies/observability/spring-boot-actuator.md)
- [Micrometer Tracing](../technologies/observability/micrometer-tracing.md)
- [Brave](../technologies/observability/brave.md)
- [Zipkin](../technologies/observability/zipkin.md)

### Resilience
- [Resilience4j](../technologies/resilience/resilience4j.md)

### Messaging
- [RabbitMQ](../technologies/messaging/rabbitmq.md)
- [Spring AMQP](../technologies/messaging/spring-amqp.md)

### Containerization
- [Docker](../technologies/containerization/docker.md)
- [Docker Compose](../technologies/containerization/docker-compose.md)
- [Docker Compose Profiles](../technologies/containerization/docker-compose-profiles.md)

### Datastores
- [PostgreSQL](../technologies/datastores/postgresql.md) — Implemented / Verified
- [MySQL](../technologies/datastores/mysql.md) — Infrastructure Ready
- [MongoDB](../technologies/datastores/mongodb.md) — Infrastructure Ready
- [Couchbase](../technologies/datastores/couchbase.md) — Infrastructure Ready
- [Apache Cassandra](../technologies/datastores/cassandra.md) — Infrastructure Ready
- [Elasticsearch](../technologies/datastores/elasticsearch.md) — Infrastructure Ready
- [Redis](../technologies/datastores/redis.md) — Infrastructure Ready

### Security & Authentication
- [Auth0 java-jwt](../technologies/security-auth/auth0-java-jwt.md)

## Protocols & Formats

- [HTTP](../protocols/http.md)
- [REST](../protocols/rest.md)
- [JSON](../protocols/json.md)
- [AMQP](../protocols/amqp.md)
- [JWT](../protocols/jwt.md)

## Day 7 state distinction

Day 7'de MySQL, MongoDB, Couchbase, Cassandra, Elasticsearch ve Redis local container infrastructure olarak hazırlanıp healthcheck ile doğrulanmıştır. Bu, application-level integration'ın tamamlandığı anlamına gelmez.

Service integration roadmap:
- Day 8 — Agent + MySQL
- Day 9 — Buyer + Couchbase
- Day 10 — Seller + Cassandra
- Day 11 — Property + MongoDB
- Day 12 — Search + Elasticsearch
- Day 13 — Redis application integration
