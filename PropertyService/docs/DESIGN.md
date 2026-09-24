# PropertyService Design

## Purpose
Own the canonical property lifecycle and write model.

## Architecture
Vertical Slice Architecture.

## Primary datastore
MongoDB via Spring Data MongoDB.

## Target package structure
```text
com.aydindemir.property
├── create
├── publish
├── update
├── updateprice
├── getbyid
└── shared
    ├── domain
    ├── persistence
    ├── messaging
    └── configuration
```

## Planned canonical document
Property:
- id
- sellerId
- title
- description
- propertyType
- address/location
- price/currency
- grossArea/netArea
- roomCount
- features
- status
- createdAt
- updatedAt
- type-specific attributes

## Why MongoDB
Property types can have heterogeneous attributes. MongoDB remains canonical; Elasticsearch is only a projection.

## Planned events
- PropertyCreated
- PropertyPublished
- PropertyUpdated
- PropertyPriceChanged
- PropertyDeleted

## Day 7 dependency changes
Use Spring Data MongoDB and remove unnecessary JPA/PostgreSQL dependencies from this module.

## Required infrastructure
MongoDB; Kafka is added in Day 10.

## Planned tests
Slice/use-case tests, Mongo integration tests and event-publication tests when Kafka is introduced.
