# Day 8 — AgentService / MySQL / Clean Architecture

## Status

- Implementation: Implemented
- Verification: Pending full build/test execution
- Branch: `day/08-agent-mysql-clean`

## Scope

Day 8 yalnız AgentService içindir.

Uygulananlar:
- PostgreSQL -> MySQL
- Flyway
- Clean Architecture
- Agent Aggregate / Value Objects
- CreateAgent / GetAgent / ChangeAvailability
- REST DTO + stable error semantics
- optimistic concurrency
- Testcontainers / controller slice / ArchUnit / smoke tests

## Persistence schema

```text
agents
id                           CHAR(36) PK
user_id                      CHAR(36) UNIQUE NOT NULL
license_number               VARCHAR(128) UNIQUE NOT NULL
agency_name                  VARCHAR(255) NOT NULL
agency_registration_number   VARCHAR(128) NULL
office_phone                 VARCHAR(64) NULL
status                       VARCHAR(32) NOT NULL
availability_status          VARCHAR(32) NOT NULL
created_at                   TIMESTAMP(6) NOT NULL
updated_at                   TIMESTAMP(6) NOT NULL
version                      BIGINT NOT NULL
```

Migration:
`AgentService/src/main/resources/db/migration/V1__create_agents_table.sql`

## API

- `POST /agents`
- `GET /agents/{agentId}`
- `PATCH /agents/{agentId}/availability`

## Stable errors

- AGENT_NOT_FOUND -> 404
- AGENT_ALREADY_EXISTS_FOR_USER -> 409
- DUPLICATE_LICENSE_NUMBER -> 409
- INVALID_AGENT_STATE -> 409
- AGENT_CONCURRENT_UPDATE -> 409

## Implementation notes

- Persistence mapper explicit Java mapper'dır.
- Empty/ceremonial PersistenceConfiguration oluşturulmadı.
- `ApplicationClockConfiguration` ile UTC Clock bean sağlandı.
- Config Client default URI baseline ile uyumlu olarak `http://localhost:8889` kaldı.
- Keycloak/RBAC Day 14'e bırakıldı.
- Synchronous UserProfile existence check eklenmedi.

## Test coverage added

- Domain Unit Tests
- Application Unit Tests
- MySQL Testcontainers
- Flyway/schema integration
- repository round-trip
- unique constraints
- optimistic locking
- Controller slice tests
- ArchUnit
- smoke tests

## Completion state

Implementation tamamlandı. Full build/test execution sonucu green olmadan Day 8 `Verified` sayılmaz.

Knowledge Base impact reviewed and updated.
