# UserProfileService Design

## Purpose
Own user profile data.

## Architecture
Existing N-Layer architecture.

## Datastore
PostgreSQL via Spring Data JPA + Hibernate.

## Existing integrations
- REST
- RabbitMQ async consumer from AuthService
- Config
- Eureka
- tracing

## Constraint
UserProfileService keeps PostgreSQL and its existing working baseline. It must not be migrated merely to make every service use a different database.
