# AgentService Design

## Purpose
Own agent, license and agency-facing business data.

## Architecture
Clean Architecture.

## Primary datastore
MySQL via Spring Data JPA + Hibernate.

## Target package structure
```text
com.aydindemir.agent
├── domain
│   ├── model
│   ├── repository
│   └── exception
├── application
│   ├── usecase
│   ├── command
│   ├── query
│   └── dto
├── infrastructure
│   ├── persistence
│   │   ├── entity
│   │   ├── repository
│   │   ├── mapper
│   │   └── adapter
│   └── configuration
└── presentation
    └── rest
```

## Planned domain model
- Agent
- AgentId
- AgentStatus
- LicenseNumber
- AgencyInfo

## Key rule
Domain code must not depend on Spring Data JPA. JPA entities/repositories belong in infrastructure.

## Day 7 dependency changes
Replace PostgreSQL runtime driver with MySQL driver for this module; keep Config, Eureka, Actuator, tracing, web and test dependencies.

## Required infrastructure
MySQL.

## Planned tests
Domain/use-case unit tests, JPA adapter integration tests, MySQL Testcontainers.
