# SearchService Design

## Purpose
Provide query-optimized property search.

## Architecture
Vertical Slice + CQRS Query Side.

## Primary datastore
Elasticsearch via Spring Data Elasticsearch.

## Ownership
SearchService owns only the search projection. It does not own canonical property data.

## Target package structure
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

## Planned PropertySearchDocument
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

## Planned capabilities
Full-text search, filtering, ranges, sorting, pagination, aggregations/facets, autocomplete, fuzzy matching and geo-distance search.

## Data flow
PropertyService/MongoDB → Kafka property events → SearchService → Elasticsearch.

## Rebuild rule
The Elasticsearch index must be rebuildable from canonical property data/event history.

## Required infrastructure
Elasticsearch; Kafka from Day 10.

## Planned tests
Index mapping/query integration tests, event projection tests, reindex/reconciliation tests.
