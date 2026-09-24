# Data Architecture

## Database-per-service

The project uses database-per-service boundaries for educational clarity and independent ownership. AuthService and UserProfileService keep the existing PostgreSQL baseline.

## Datastore assignments

- PostgreSQL — AuthService, UserProfileService
- MySQL — AgentService
- Couchbase — BuyerService
- Cassandra — SellerService
- MongoDB — PropertyService
- Elasticsearch — SearchService projection
- Redis — shared infrastructure capability, not a canonical business datastore

## Why each datastore exists

### MySQL
Relational modeling, JPA/Hibernate portability, constraints, transactions and indexes.

### Couchbase
Document-oriented buyer preferences and ports/adapters persistence isolation.

### Cassandra
Query-first modeling, partition/clustering keys, denormalization, high-write distributed data patterns.

### MongoDB
Flexible property aggregates with heterogeneous property attributes.

### Elasticsearch
Full-text search, filters, aggregations, facets, autocomplete, fuzzy and geo queries.

### Redis
Caching, idempotency keys, rate limiting and short-lived state.

## Important constraints

- No Oracle or SQL Server in the current roadmap.
- Elasticsearch is not source of truth.
- Redis is not a canonical aggregate store.
- Cassandra tables are designed from access patterns, not JPA-style entity relations.
