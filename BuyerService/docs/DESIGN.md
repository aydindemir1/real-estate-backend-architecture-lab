# BuyerService Design

## Amaç
Buyer preferences, saved criteria ve buyer-side behavior verilerinin ownership'ini taşımak.

## Architecture
Hexagonal Architecture.

## Primary datastore
Couchbase + Spring Data Couchbase.

## Hedef package structure
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

## Planlanan document model
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

## Temel port'lar
- SaveBuyerPreferencesPort
- FindBuyerPreferencesPort
- AgentAvailabilityPort
- PublishOfferRequestedPort

## Day 7 dependency değişiklikleri
Spring Data Couchbase kullanılacak; bu module için gereksiz JPA/PostgreSQL dependency'leri kaldırılacak.

## Gerekli infrastructure
Couchbase.

## Planlanan testler
Fake outbound port'larla application service testleri, Couchbase adapter Integration Testing ve Testcontainers.
