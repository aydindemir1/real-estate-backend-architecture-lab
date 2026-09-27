# AgentService — Package / Class-Level Design

## Architecture

Clean Architecture

## Actual Day 8 package structure

```text
com.aydindemir.agent
├── AgentServiceApplication
├── domain
│   ├── model
│   │   ├── Agent
│   │   ├── AgentId
│   │   ├── UserId
│   │   ├── LicenseNumber
│   │   ├── AgencyInfo
│   │   ├── AgentStatus
│   │   └── AvailabilityStatus
│   ├── repository
│   │   └── AgentRepository
│   └── exception
├── application
│   ├── usecase
│   │   ├── CreateAgentUseCase
│   │   ├── GetAgentUseCase
│   │   └── ChangeAvailabilityUseCase
│   ├── command
│   ├── query
│   ├── result
│   ├── exception
│   └── service
│       └── AgentApplicationService
├── infrastructure
│   ├── persistence
│   │   ├── entity/AgentJpaEntity
│   │   ├── repository/SpringDataAgentRepository
│   │   ├── mapper/AgentPersistenceMapper
│   │   └── adapter/AgentRepositoryAdapter
│   └── configuration/ApplicationClockConfiguration
└── presentation
    ├── error
    │   ├── AgentErrorResponse
    │   └── AgentExceptionHandler
    └── rest
        ├── AgentController
        ├── request
        ├── response
        └── mapper/AgentRestMapper
```

## Dependency rule

```text
presentation ------> application
infrastructure ---> application/domain
application -------> domain
domain ------------> hiçbir outer layer'a bağımlı değil
```

ArchUnit bu yönü enforce eder.

## Day 8 scope

Yalnız:
- CreateAgent
- GetAgent
- ChangeAvailability

Bu Day'de yok:
- profile update
- lifecycle status REST endpoint'leri
- list/filter/pagination
- get-by-userId
- gRPC
- Keycloak/RBAC
- Idempotency-Key

## Persistence ayrımı

`domain.model.Agent` JPA annotation taşımaz.

`AgentJpaEntity` infrastructure persistence modelidir.

Persistence hydration sırasında `Agent.create(...)` değil, persisted identity/state/timestamp/version değerlerini koruyan `Agent.reconstitute(...)` kullanılır.
