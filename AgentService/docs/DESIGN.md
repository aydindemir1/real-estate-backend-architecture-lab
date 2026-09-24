# AgentService Design

## Amaç
Agent, license ve agency-facing business data'nın ownership'ini taşımak.

## Architecture
Clean Architecture.

## Primary datastore
MySQL + Spring Data JPA + Hibernate.

## Hedef package structure
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

## Planlanan domain model
- Agent
- AgentId
- AgentStatus
- LicenseNumber
- AgencyInfo

## Temel kural
Domain katmanı Spring Data JPA'yı bilmemelidir. JPA entity ve repository implementation'ları infrastructure altında bulunmalıdır.

## Day 7 dependency değişiklikleri
Bu module'de PostgreSQL runtime driver kaldırılıp MySQL driver kullanılacak; Config, Eureka, Actuator, tracing, web ve test dependency'leri korunacak.

## Gerekli infrastructure
MySQL.

## Planlanan testler
Domain/use-case Unit Testing, JPA adapter Integration Testing ve MySQL Testcontainers.
