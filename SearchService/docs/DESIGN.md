# SearchService Design

## Amaç
Query-optimized property search sağlamak.

## Architecture
Vertical Slice + CQRS Query Side.

## Primary datastore
Elasticsearch + Spring Data Elasticsearch.

## Ownership
SearchService yalnızca search projection'ın sahibidir. Canonical property data'nın sahibi değildir.

## Hedef package structure
```text
com.aydindemir.search
├── searchproperties
├── autocomplete
├── facetsearch
├── geosearch
├── indexing
├── eventconsumer
└── shared
    ├── elasticsearch
    ├── configuration
    └── observability
```

## Planlanan PropertySearchDocument
- propertyId
- title
- description
- propertyType
- city
- district
- latitude
- longitude
- price
- currency
- grossArea
- netArea
- roomCount
- features
- sellerId
- status
- createdAt
- updatedAt

## Planlanan capabilities
Full-text search, filtering, ranges, sorting, pagination, aggregations/facets, autocomplete, fuzzy matching ve geo-distance search.

## Data flow
PropertyService/MongoDB → Kafka property event'leri → SearchService → Elasticsearch.

## Rebuild kuralı
Elasticsearch index'i canonical property data veya event history üzerinden yeniden üretilebilir olmalıdır.

## Gerekli infrastructure
Elasticsearch. Kafka Day 10'da eklenecek.

## Planlanan testler
Index mapping/query Integration Testing, event projection testleri ve Reindex/Reconciliation testleri.
