# AuthService Design

## Amaç
Authentication/account baseline ownership'ini taşımak.

## Architecture
Mevcut N-Layer Architecture bilinçli olarak korunacaktır.

## Datastore
PostgreSQL + Spring Data JPA + Hibernate.

## Mevcut integration'lar
- REST
- UserProfileService'e OpenFeign
- Async profile creation için RabbitMQ
- Spring Cloud Config
- Eureka / LoadBalancer
- tracing

## Planlanan gelişim
Day 8 kapsamında Spring Security, Keycloak, OAuth2/OIDC, RBAC ve service-to-service authentication eklenecek. Mevcut custom JWT yalnızca eğitimsel karşılaştırma için gerektiği ölçüde korunabilir.

## Kısıt
Bu service yalnızca tutarlılık sağlamak amacıyla Clean Architecture, Hexagonal Architecture veya Onion Architecture'a dönüştürülmeyecektir; N-Layer karşılaştırma baseline'ı olarak kalacaktır.
