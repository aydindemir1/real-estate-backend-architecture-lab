# UserProfileService Design

## Amaç
User profile data ownership'ini taşımak.

## Architecture
Mevcut N-Layer Architecture.

## Datastore
PostgreSQL + Spring Data JPA + Hibernate.

## Mevcut integration'lar
- REST
- AuthService'ten RabbitMQ async consumer
- Spring Cloud Config
- Eureka
- tracing

## Kısıt
UserProfileService PostgreSQL ve mevcut çalışan baseline yapısını koruyacaktır. Yalnızca her service farklı database kullansın amacıyla migration yapılmayacaktır.
