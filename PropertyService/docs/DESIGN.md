# PropertyService Design

## Amaç
Canonical property lifecycle ve write model ownership'ini taşımak.

## Architecture
Vertical Slice Architecture.

## Primary datastore
MongoDB + Spring Data MongoDB.

## Hedef package structure
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

## Planlanan canonical document
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

## Neden MongoDB?
Farklı property type'ları heterogeneous attribute'lara sahip olabilir. MongoDB canonical source olarak kalır; Elasticsearch yalnızca projection olacaktır.

## Planlanan event'ler
- PropertyCreated
- PropertyPublished
- PropertyUpdated
- PropertyPriceChanged
- PropertyDeleted

## Day 7 dependency değişiklikleri
Spring Data MongoDB kullanılacak; bu module için gereksiz JPA/PostgreSQL dependency'leri kaldırılacak.

## Gerekli infrastructure
MongoDB. Kafka Day 10'da eklenecek.

## Planlanan testler
Slice/use-case testleri, MongoDB Integration Testing ve Kafka eklendiğinde event publication testleri.
