# AgentService — Package / Class-Level Design

## Architecture
Clean Architecture

## Amaç
Domain ve application katmanlarını Spring/JPA gibi framework detaylarından bağımsız tutmak.

## Package yapısı

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
│   ├── service
│   │   └── AgentDomainService
│   └── exception
│       ├── AgentNotFoundException
│       ├── InvalidAgentStateException
│       └── DuplicateLicenseNumberException
├── application
│   ├── usecase
│   │   ├── CreateAgentUseCase
│   │   ├── GetAgentUseCase
│   │   ├── UpdateAgentProfileUseCase
│   │   ├── ChangeAgentStatusUseCase
│   │   ├── ChangeAvailabilityUseCase
│   │   └── CheckAvailabilityUseCase
│   ├── command
│   │   ├── CreateAgentCommand
│   │   ├── UpdateAgentProfileCommand
│   │   ├── ChangeAgentStatusCommand
│   │   └── ChangeAvailabilityCommand
│   ├── query
│   │   ├── GetAgentQuery
│   │   └── CheckAvailabilityQuery
│   ├── result
│   │   ├── AgentResult
│   │   └── AvailabilityResult
│   └── service
│       └── AgentApplicationService
├── infrastructure
│   ├── persistence
│   │   ├── entity
│   │   │   └── AgentJpaEntity
│   │   ├── repository
│   │   │   └── SpringDataAgentRepository
│   │   ├── mapper
│   │   │   └── AgentPersistenceMapper
│   │   └── adapter
│   │       └── AgentRepositoryAdapter
│   ├── grpc
│   │   ├── AgentAvailabilityGrpcService
│   │   └── GrpcAgentMapper
│   └── configuration
│       ├── PersistenceConfiguration
│       └── GrpcConfiguration
└── presentation
    └── rest
        ├── AgentController
        ├── request
        │   ├── CreateAgentRequest
        │   ├── UpdateAgentProfileRequest
        │   ├── ChangeAgentStatusRequest
        │   └── ChangeAvailabilityRequest
        ├── response
        │   └── AgentResponse
        └── mapper
            └── AgentRestMapper
```

## Dependency rule

```text
presentation ------> application
infrastructure ---> application/domain
application -------> domain
domain ------------> hiçbir outer layer'a bağımlı değil
```

## Kritik kural
`domain.model.Agent` JPA annotation taşımaz. `AgentJpaEntity` ayrı persistence model'dir.
