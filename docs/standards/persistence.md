# Persistence Standard

Bu doküman projedeki persistence, datastore ownership, transaction, indexing, query design, migration ve consistency kurallarını tanımlar.

Amaç:
- doğru datastore'u doğru workload için kullanmak,
- data ownership'i service boundary içinde tutmak,
- query-first ve access-pattern-aware tasarım yapmak,
- persistence teknolojisinin domain model'i bozmasını engellemek,
- transaction ve consistency beklentilerini açık hale getirmek.

# 1. Data Ownership

Her service kendi data store'unun owner'ıdır.

Yasak:
- başka service'in database'ine doğrudan erişmek,
- cross-service SQL join,
- shared schema/table ownership,
- bir service'in başka service'in persistence model'ini import etmesi.

Cross-service data ihtiyacı:
- API
- event
- projection

ile çözülür.

# 2. Database per Service

Bu proje Database per Service yaklaşımını kullanır.

Datastore seçimi workload ve öğrenme hedefiyle bilinçli yapılmıştır:

- AuthService -> PostgreSQL
- UserProfileService -> PostgreSQL
- AgentService -> MySQL
- BuyerService -> Couchbase
- SellerService -> Cassandra
- PropertyService -> MongoDB
- SearchService -> Elasticsearch
- Redis -> ephemeral/cache/idempotency/rate limit

# 3. Source of Truth

Her business concept için canonical owner açık olmalıdır.

Örnek:
- Property canonical source -> PropertyService / MongoDB
- Search projection -> Elasticsearch, source of truth değildir
- Seller pending offer projection -> Cassandra, canonical Offer değildir

# 4. Persistence Model != Domain Model

Clean/Hexagonal/Onion service'lerde persistence model domain model'den ayrılır.

Örnek:
- Agent -> AgentJpaEntity
- Seller -> Cassandra table models
- Offer -> OfferDocument

N-Layer baseline service'lerde entity/domain ayrımı daha gevşek olabilir.

# 5. Repository Responsibility

Repository:
- load/save/query persistence işi yapar,
- business orchestration yapmaz,
- external service çağırmaz,
- event publish etmez.

# 6. Transaction Boundary

Local transaction application use-case seviyesinde tanımlanır.

Distributed transaction yoktur.

Service boundary dışında:
- Saga
- Outbox
- Eventual Consistency

kullanılır.

# 7. Transaction Scope

Transaction mümkün olduğunca kısa tutulur.

Transaction içinde:
- remote HTTP/gRPC call
- blocking external broker wait
- uzun CPU işi

yapılmaz.

# 8. Isolation ve Concurrency

Isolation level default olarak datastore standardına bırakılır.

Daha güçlü isolation yalnızca gerçek invariant gerektiriyorsa seçilir.

Concurrency için tercih:
- Optimistic Locking
- version field
- compare-and-set semantic

Pessimistic locking yalnızca gerekçeli use-case'te.

# 9. Index Tasarımı

Index query pattern'den türetilir.

Kural:
```text
query -> filter/sort pattern -> index
```

"Belki lazım olur" diye index eklenmez.

Her index:
- write cost
- storage cost
- maintenance cost

taşır.

# 10. Query Design

Repository method isimleri query intent'i açık göstermelidir.

Kötü:
`findData()`

Tercih:
`findPendingOffersBySellerId(...)`

Complex query'ler ölçülmeden optimize edilmez.

# 11. Pagination

Unbounded list query yasaktır.

Relational DB:
offset pagination küçük dataset'te kabul edilebilir.

Deep pagination için keyset/cursor değerlendirilebilir.

Elasticsearch:
`search_after` ileri aşamada tercih edilir.

Cassandra:
paging state.

# 12. N+1

JPA tarafında N+1 gözlemlenmeli ve test/profiling ile doğrulanmalıdır.

Çözümler:
- fetch join
- entity graph
- projection
- batch fetch

Her relation EAGER yapılmaz.

# 13. Lazy Loading

Lazy proxy presentation layer'a sızdırılmaz.

Transaction dışı serialization'a güvenilmez.

# 14. Migration

Relational DB schema migration version-controlled olmalıdır.

Tercih:
- Flyway veya Liquibase

Auth/UserProfile mevcut yapı korunur.
AgentService MySQL için migration tool kullanılacaktır.

Production-benzeri service'lerde `ddl-auto=update` kullanılmaz.

# 15. Schema Evolution

Breaking schema değişiklikleri aşamalı yapılır.

Örnek:
1. yeni nullable column
2. app yeni field'i kullanır
3. backfill
4. eski field deprecate
5. sonra remove

# 16. Data Validation

DB constraint ve domain invariant birbirini tamamlar.

Örnek:
- UNIQUE email
- UNIQUE license_number
- NOT NULL

Critical invariant yalnızca application check'e bırakılmaz.

# 17. Connection Pool

Relational DB connection pool bilinçli konfigüre edilir.

İzlenecek:
- max pool size
- connection timeout
- idle timeout
- leak detection gerektiğinde

Pool boyutu "yüksek olsun" diye artırılmaz.

# 18. Timeout

Datastore client timeout'ları explicit olmalıdır.

Default infinite/çok uzun timeout kabul edilmez.

# 19. Retry

Database retry yalnızca transient failure için.

Retryable değil:
- constraint violation
- invalid query
- business conflict

# 20. Sensitive Data

Password/token/secret plaintext persist edilmez.

Credential encryption/hashing security standardında detaylandırılır.

# PostgreSQL / MySQL Standardı

# 21. Relational Modeling

Normalized model default başlangıçtır.

Denormalization yalnızca read performance için gerekçeyle yapılır.

# 22. Primary Key

Opaque ID tercih edilir.

UUID kullanılabilir.

DB-specific UUID storage optimization implementation aşamasında değerlendirilir.

# 23. Unique Constraint

Uniqueness yalnızca service check ile korunmaz.

Örnek:
- email
- username
- license_number

DB-level UNIQUE constraint olmalıdır.

# 24. Foreign Key

Aynı service schema'sındaki gerçek relational integrity için kullanılabilir.

Cross-service ID'lerde FK kullanılmaz.

# 25. JPA Entity Design

Kaçınılacak:
- devasa bidirectional graph
- default EAGER collection
- entity'yi API response olarak expose etmek
- equals/hashCode'da mutable field kullanmak

# 26. Batch Write

Gerçek bulk workload varsa batch insert/update değerlendirilir.

Normal CRUD için premature batching yapılmaz.

# Couchbase Standardı

# 27. Document Boundary

Document Aggregate/read-write boundary'ye yakın tasarlanır.

BuyerPreferences embedded nested document için uygundur.

Offer ayrı document'tır.

# 28. Document Key

Predictable namespace:

`buyer-preferences::{buyerId}`
`offer::{offerId}`

Key business query'ye değer katıyorsa deterministic olabilir.

# 29. Secondary Index

N1QL query pattern varsa index eklenir.

Candidate:
- buyerId + createdAt
- propertyId
- status

# 30. Document Size

Unbounded nested collection oluşturulmaz.

Örneğin saved searches büyürse ayrı document'e ayrılabilir.

# 31. CAS / Optimistic Concurrency

Concurrent document update için Couchbase CAS mekanizması değerlendirilebilir.

# Cassandra Standardı

# 32. Query-First Design

Cassandra'da table önce entity'den değil query/access pattern'den tasarlanır.

```text
query -> partition key -> clustering key -> table
```

# 33. Join Yok

Join, foreign key, ad-hoc relational query beklenmez.

Denormalization normaldir.

# 34. Partition Key

Partition:
- dengeli dağılmalı,
- bounded büyümeli,
- hotspot oluşturmamalı.

Time bucket gerektiğinde kullanılır:
`year_month`

# 35. Clustering Key

Sort order ve range query ihtiyacına göre tasarlanır.

# 36. ALLOW FILTERING

Production-like query tasarımında kaçınılır.

Query'ye uygun table tasarlanır.

# 37. Large Partition

Unbounded seller activity gibi timeline'larda month bucket kullanılır.

# 38. Tombstone

Aşırı delete/TTL kullanımı tombstone yaratabilir.

TTL bilinçli kullanılır.

# 39. Consistency Level

Default consistency kör değiştirilmez.

Business requirement gerekirse read/write consistency trade-off'u belgelenir.

# MongoDB Standardı

# 40. Aggregate Document

Property Aggregate document olarak persist edilir.

Embedded Value Object'ler doğal şekilde document içinde tutulabilir.

# 41. Document Growth

Unbounded arrays önlenir.

# 42. Index

Candidate:
- sellerId + createdAt
- status
- agentId
- gerekiyorsa 2dsphere

# 43. Optimistic Locking

Property concurrency için `@Version` kullanılacaktır.

# 44. Mongo Transaction

Multi-document transaction yalnızca gerçekten gerekliyse.

Single-document atomicity tercih edilir.

Outbox aynı transaction gerektiriyorsa Mongo transaction değerlendirilebilir.

# 45. Partial Update

Atomic operator kullanımında domain invariant bypass edilmemelidir.

Rich Aggregate update'lerinde load -> behavior -> save tercih edilir.

# Elasticsearch Standardı

# 46. Elasticsearch Role

Elasticsearch canonical database değildir.

Search/read projection'dır.

# 47. Mapping

Dynamic mapping'e production benzeri kullanımda kör güvenilmez.

Explicit mapping:
- text
- keyword
- numeric
- date
- geo_point

# 48. text vs keyword

Full-text:
`text`

filter/sort/aggregation:
`keyword`

# 49. Analyzer

Başlangıç:
standard analyzer.

İleri:
- Turkish analyzer
- asciifolding
- edge n-gram

yalnızca use-case gerektirirse.

# 50. Shard

Local lab:
1 primary shard, 0 replica kabul edilebilir.

Production sizing data volume/query load'a göre yapılır.

# 51. Refresh

Her write sonrası manual refresh yapılmaz.

Near-real-time semantic kabul edilir.

# 52. Deep Pagination

Büyük result set için `search_after` tercih edilir.

# 53. Reindex

Schema/mapping değişiminde:
- new index
- reindex
- alias switch

yaklaşımı öğrenilecektir.

# Redis Standardı

# 54. Redis Role

Redis source of truth değildir.

Kullanım:
- cache
- idempotency
- rate limit
- short-lived state

# 55. Key Naming

Namespace zorunlu.

Örnek:
- `cache:property-summary:{propertyId}`
- `idempotency:offer:{key}`
- `rate-limit:{subject}:{route}:{window}`

# 56. TTL

Ephemeral key'lerde TTL explicit olmalıdır.

TTL business semantics'e göre seçilir.

# 57. Cache Invalidation

Cache kullanmadan önce invalidation stratejisi belirlenir.

"Cache ekleyelim sonra bakarız" yapılmaz.

# 58. Cache Stampede

High-traffic expensive query varsa:
- jitter
- lock/single-flight
- stale-while-revalidate

gibi pattern'ler değerlendirilebilir.

# 59. Serialization

Redis payload küçük ve versionable olmalıdır.

Java native serialization kullanılmaz.

# 60. Data Retention

Her datastore için retention ihtiyacı açık olmalıdır.

Özellikle:
- event dedup records
- idempotency records
- activity history
- search index

# 61. Backup / Recovery Awareness

Lab production backup sistemi kurmayabilir fakat design dokümanında:
- source of truth
- reconstructable projection
- disposable cache

ayrımı açık olmalıdır.

# 62. Observability

Persistence metric'leri:
- query latency
- connection pool utilization
- timeout
- error rate
- slow query
- Cassandra latency
- Elasticsearch query latency
- Redis hit/miss

izlenebilir olmalıdır.

# 63. Performance Validation

Optimization öncesi:
- query plan
- explain
- metrics
- benchmark

kullanılır.

Relational DB'de EXPLAIN / EXPLAIN ANALYZE öğrenilir.

# 64. Test Strategy

Persistence adapter test'leri gerçek datastore behavior'ını doğrulamalıdır.

Tercih:
Testcontainers.

Mock repository ile database semantics test edildiği varsayılmaz.

# 65. Persistence Anti-Pattern'leri

Kaçınılacak:
- shared database
- cross-service join
- ddl-auto=update production-benzeri ortam
- index everywhere
- EAGER everything
- ALLOW FILTERING ile Cassandra model kurtarmaya çalışma
- Elasticsearch'i source of truth yapmak
- Redis'i canonical state yapmak
- unbounded collection/document/partition
- transaction içinde remote call
- repository içinde business logic
- persistence exception'ını doğrudan API'ye sızdırmak

# 66. Persistence Review Checklist

- Data owner hangi service?
- Source of truth neresi?
- Query pattern belli mi?
- Index query'den türedi mi?
- Transaction boundary kısa mı?
- Concurrency nasıl çözülüyor?
- Migration strategy var mı?
- Pagination bounded mı?
- Retry güvenli mi?
- DB constraint gerekli mi?
- Projection ile canonical model karışmış mı?
- Datastore'un doğal modeline uygun tasarlandı mı?
- Test gerçek datastore semantics'ini doğruluyor mu?
