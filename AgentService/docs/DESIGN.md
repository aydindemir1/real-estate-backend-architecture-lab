# AgentService Design

## Amaç

AgentService; agent professional identity, license bilgisi, agency-facing profile bilgisi, lifecycle status ve availability state'in ownership'ini taşır.

## Architecture

Clean Architecture.

```text
presentation ------> application
infrastructure ---> application/domain
application -------> domain
domain ------------> outer layer'ları bilmez
```

Domain katmanı Spring, JPA ve Hibernate bağımlılığı taşımaz.

## Primary datastore

MySQL + Spring Data JPA + Hibernate.

Schema ownership Flyway'dedir:

`src/main/resources/db/migration/V1__create_agents_table.sql`

Runtime persistence kuralları:
- `spring.jpa.hibernate.ddl-auto=validate`
- `spring.jpa.open-in-view=false`
- MySQL native ENUM yok
- cross-service database FK yok
- `user_id` ve `license_number` unique constraint ile korunur

## Domain model

Aggregate:
- `Agent`

Value Objects / identity types:
- `AgentId`
- `UserId`
- `LicenseNumber`
- `AgencyInfo`

State:
- `AgentStatus`: ACTIVE, SUSPENDED, INACTIVE
- `AvailabilityStatus`: AVAILABLE, BUSY, OFFLINE

Yeni Agent `ACTIVE + OFFLINE` başlar.

SUSPENDED veya INACTIVE Agent AVAILABLE/BUSY olamaz. Suspend/deactivate availability'yi OFFLINE yapar. INACTIVE terminal state'tir.

## Identity ownership

`Agent.userId`, UserProfile identity'yi temsil eden external reference'tır.

Day 8'de:
- synchronous UserProfile existence check yok
- cross-service DB FK yok
- Keycloak subject doğrudan Agent ID değildir

Keycloak subject -> UserProfile -> Agent mapping'i Day 14 Security milestone'ına aittir.

## Application layer

Use-case interface'leri:
- `CreateAgentUseCase`
- `GetAgentUseCase`
- `ChangeAvailabilityUseCase`

`AgentApplicationService` orchestration ve transaction boundary'lerini taşır.

## Persistence mapping

Domain `Agent` ile `AgentJpaEntity` ayrıdır.

- New aggregate: `Agent.create(...)`
- Persistence hydration: `Agent.reconstitute(...)`
- Mapping: explicit `AgentPersistenceMapper`
- `@Version`: Hibernate optimistic locking

## REST API

- `POST /agents` -> 201 Created + Location + AgentResponse
- `GET /agents/{agentId}` -> 200 / 404
- `PATCH /agents/{agentId}/availability` -> 200 AgentResponse

Public response `version` expose etmez.

## Stable errors

- `AGENT_NOT_FOUND` -> 404
- `AGENT_ALREADY_EXISTS_FOR_USER` -> 409
- `DUPLICATE_LICENSE_NUMBER` -> 409
- `INVALID_AGENT_STATE` -> 409
- `AGENT_CONCURRENT_UPDATE` -> 409
- validation/malformed body -> 400

## Test coverage

- Domain Unit Tests
- Application Unit Tests
- MySQL Testcontainers integration tests
- Flyway schema checks
- unique constraint tests
- optimistic locking integration test
- Controller slice tests
- ArchUnit rules
- service smoke tests

Test sınıflarının varlığı implementation durumunu gösterir. Full suite green sonucu ayrıca çalıştırılarak doğrulanmadan `Verified` kabul edilmez.
