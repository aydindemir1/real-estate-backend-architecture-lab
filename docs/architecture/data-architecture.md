# Data Architecture

## Database per Service

Proje, service ownership sınırlarını netleştirmek ve independent persistence kararlarını öğretmek için Database per Service yaklaşımını kullanır. AuthService ve UserProfileService mevcut PostgreSQL baseline'ını korur.

## Datastore dağılımı

- PostgreSQL — AuthService, UserProfileService
- MySQL — AgentService
- Couchbase — BuyerService
- Cassandra — SellerService
- MongoDB — PropertyService
- Elasticsearch — SearchService projection
- Redis — shared infrastructure capability; canonical business datastore değildir

## Datastore'ların projedeki öğretim amacı

### MySQL
Relational modeling, JPA/Hibernate portability, constraints, transactions ve indexes.

### Couchbase
Document-oriented buyer preferences ve Hexagonal Architecture içinde persistence adapter izolasyonu.

### Cassandra
Query-first modeling, partition key, clustering key, denormalization ve distributed data design.

### MongoDB
Heterogeneous property attribute'larını taşıyan flexible property aggregate modeli.

### Elasticsearch
Full-text search, filtering, aggregations, facets, autocomplete, fuzzy search ve geo query.

### Redis
Caching, idempotency key, rate limiting ve short-lived state.

## Kısıtlar

- Oracle ve SQL Server mevcut roadmap kapsamında kullanılmayacak.
- Elasticsearch source of truth değildir.
- Redis canonical aggregate store değildir.
- Cassandra tabloları JPA tarzı entity relation yaklaşımıyla değil access pattern'lere göre tasarlanacaktır.
