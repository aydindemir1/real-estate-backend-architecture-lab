# Polyglot Persistence

**Category:** Approach  
**Introduced:** Day 7  
**Project status:** Infrastructure Ready / Partially Implemented  
**Scope:** Choosing datastore technology by service workload and data model

## 1. Nedir?

Polyglot Persistence, sistemdeki tüm data problemlerini tek database teknolojisine zorlamak yerine farklı service veya workload'lar için farklı persistence teknolojileri seçme yaklaşımıdır.

## 2. Hangi problemi çözer?

Her database aynı güçlü yönlere sahip değildir.

Örnek:
- relational transaction
- document flexibility
- wide-column write scalability
- key-value latency
- full-text search

farklı veri ihtiyaçlarıdır.

Tek database'i her problem için kullanmak gereksiz trade-off yaratabilir.

## 3. Temel fikir

```text
Service / Workload
      |
      v
Best-fit persistence model
      |
      v
Suitable datastore
```

## 4. Bu projede target ownership

```text
Auth        -> PostgreSQL
UserProfile -> PostgreSQL
Agent       -> MySQL
Buyer       -> Couchbase
Seller      -> Cassandra
Property    -> MongoDB
Search      -> Elasticsearch
Redis       -> cache/idempotency/rate limiting only
```

## 5. Neden yapılır?

- relational integrity ihtiyacı
- flexible document model
- horizontal write scaling
- full-text search
- low-latency ephemeral access
- workload-specific optimization

## 6. Database per Service ile ilişkisi

Polyglot Persistence çoğu zaman Database per Service pattern'i ile birlikte kullanılır.

Ancak aynı şey değildir.

Database per Service:
> Data ownership boundary.

Polyglot Persistence:
> Technology selection diversity.

Bir sistem database-per-service kullanıp her service için PostgreSQL de kullanabilir.

## 7. Avantajları

- workload'a uygun datastore
- independent scaling
- data model flexibility
- specialized query capability
- teknoloji öğrenme ve karşılaştırma

## 8. Dezavantajları

- operational complexity
- farklı backup/recovery modeli
- farklı consistency semantics
- farklı driver/library
- farklı migration strategy
- farklı monitoring
- farklı failure modes
- ekip öğrenme maliyeti

## 9. En büyük risk: technology zoo

Polyglot Persistence "her service farklı database kullansın" demek değildir.

Her teknoloji seçiminin:
- gerçek ihtiyacı,
- ownership modeli,
- operational karşılığı,
- trade-off'u

olmalıdır.

## 10. Consistency etkisi

Farklı datastore kullanan servisler arasında ACID transaction doğal olarak yoktur.

Bu nedenle:
- eventual consistency
- Saga
- Outbox
- idempotency
- reconciliation

gibi distributed data patterns gerekebilir.

## 11. Day 7 durumu

Day 7 sonunda:
- PostgreSQL baseline zaten çalışıyordu
- MySQL
- MongoDB
- Couchbase
- Cassandra
- Elasticsearch
- Redis

local container altyapıları hazırlandı ve healthcheck ile doğrulandı.

Ancak application integration ayrı Day'lerde yapılacaktır.

## 12. Infrastructure Ready ile Implemented farkı

Day 7 sonunda:

- MySQL: infrastructure ready
- Couchbase: infrastructure ready
- Cassandra: infrastructure ready
- MongoDB: infrastructure ready
- Elasticsearch: infrastructure ready
- Redis: infrastructure ready

Service-level persistence implementation:
- Day 8–13

arasında yapılacaktır.

## 13. Production considerations

- backup/restore
- schema migration
- consistency model
- replication
- durability
- monitoring
- capacity planning
- driver compatibility
- disaster recovery
- operational expertise

her datastore için ayrı değerlendirilmelidir.

## 14. İlgili pattern ve architecture'lar

- Database per Service
- Microservices Architecture
- CQRS
- Event-Driven Architecture
- Eventual Consistency
- Saga
- Outbox

## 15. İleri öğrenme konuları

- CAP theorem
- consistency models
- replication strategies
- partitioning
- indexing
- database internals
- data locality
- multi-model databases
