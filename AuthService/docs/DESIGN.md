# AuthService Design

## Purpose
Own authentication/account baseline.

## Architecture
Existing N-Layer architecture is preserved intentionally.

## Datastore
PostgreSQL via Spring Data JPA + Hibernate.

## Existing integrations
- REST
- OpenFeign to UserProfileService
- RabbitMQ async profile-creation command
- Config
- Eureka/LoadBalancer
- tracing

## Planned evolution
Day 8 introduces Spring Security, Keycloak, OAuth2/OIDC, RBAC and service-to-service authentication. Existing custom JWT may remain only for educational comparison.

## Constraint
Do not refactor this service into Clean/Hexagonal/Onion merely for consistency; it is the N-Layer comparison baseline.
