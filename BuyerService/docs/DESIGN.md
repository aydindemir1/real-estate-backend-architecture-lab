# BuyerService Design

## Purpose
Own buyer preferences, saved criteria and buyer-side behavior.

## Architecture
Hexagonal Architecture.

## Primary datastore
Couchbase via Spring Data Couchbase.

## Target package structure
```text
com.aydindemir.buyer
├── domain
│   ├── model
│   └── service
├── application
│   ├── port
│   │   ├── in
│   │   └── out
│   └── service
└── adapter
    ├── in
    │   ├── rest
    │   ├── grpc
    │   └── messaging
    └── out
        ├── persistence
        ├── messaging
        └── client
```

## Planned document model
BuyerPreferences:
- buyerId
- budgetMin / budgetMax
- preferredCities
- preferredDistricts
- preferredPropertyTypes
- roomCount preferences
- feature preferences
- notification preferences
- saved criteria

## Key ports
- SaveBuyerPreferencesPort
- FindBuyerPreferencesPort
- AgentAvailabilityPort
- PublishOfferRequestedPort

## Day 7 dependency changes
Use Spring Data Couchbase; remove unnecessary JPA/PostgreSQL dependencies from this module.

## Required infrastructure
Couchbase.

## Planned tests
Application services with fake outbound ports, Couchbase adapter integration tests, Testcontainers.
