# Apache Cassandra

**Category:** Technology  
**Introduced:** Day 7 infrastructure  
**Application implementation:** Day 10  
**Project status:** Implemented / Verified  
**Scope:** SellerService

## 1. Nedir?

Apache Cassandra distributed wide-column NoSQL database'tir. Horizontal scale, yüksek write throughput ve partition-key tabanlı öngörülebilir erişim için tasarlanır. Relational join modelini taklit etmek yerine query-first ve denormalized data model kullanır.

## 2. Hangi problemi çözer?

Özellikle:
- çok büyük veri hacmi,
- yüksek write rate,
- partition-key ile öngörülebilir erişim,
- horizontal scale,
- node failure toleransı

gerektiren sistemlerde güçlüdür.

Arbitrary ad-hoc query, relational join ve global uniqueness Cassandra'nın doğal güçlü tarafları değildir.

## 3. Temel kavramlar

- cluster
- datacenter
- node
- keyspace
- table
- partition key
- clustering column
- replication factor
- consistency level
- denormalization
- tombstone
- compaction
- repair

## 4. Query-first data modeling

Cassandra'da tablo entity ilişkilerine göre değil, cevaplanacak query'ye göre tasarlanır.

Day 10 örneği:

`listing_submissions_by_seller_and_month`

Query:
- belirli seller
- belirli ay
- newest-first
- bounded page

Bu nedenle:

```text
PRIMARY KEY ((seller_id, year_month), created_at, submission_id)
```

## 5. Bu projedeki fiziksel model

Keyspace:
- `seller_service`

Tables:
- `seller_by_id`
- `listing_submissions_by_seller_and_month`

Listing partition:

```text
(seller_id, year_month)
```

Clustering:

```text
created_at DESC
submission_id ASC
```

`year_month` bir time-bucket'tır; partition growth'ünü tüm seller geçmişi boyunca sınırsız bırakmamak için kullanılır.

## 6. Uygulanan query kuralları

Day 10:
- no `ALLOW FILTERING`
- no cross-partition scan
- no relational join expectation
- exact partition-key query
- bounded `pageSize`
- paging state domain'e Cassandra `Page`/`Slice` tipi sızdırmadan taşınır
- exact submission lookup full primary-key identity ile yapılır

## 7. Domain / persistence ayrımı

Domain:
- `Seller`
- `ListingSubmission`

Cassandra physical models:
- `SellerByIdTable`
- `ListingSubmissionBySellerMonthTable`

Explicit mapper:
- `SellerCassandraMapper`
- `ListingSubmissionCassandraMapper`

## 8. Spring Data Cassandra

Infrastructure:
- `SpringDataSellerByIdRepository`
- `SpringDataListingSubmissionRepository`
- `CassandraSellerRepositoryAdapter`
- `CassandraListingSubmissionRepositoryAdapter`

Domain repository interface'leri Spring Data tiplerini bilmez.

## 9. Schema yönetimi

Source of truth:

`SellerService/src/main/resources/cassandra/schema/V1__seller_tables.cql`

Runtime:
- `spring.cassandra.schema-action: none`

Schema application startup'a bırakılmaz; local explicit `cqlsh` bootstrap ile uygulanır.

## 10. Consistency semantics

Day 10 tek-node local Cassandra kullanır:
- `NetworkTopologyStrategy`
- `datacenter1: 1`

Bu local eğitim topolojisi production replication/consistency tasarımını temsil etmez.

Production'da ayrıca replication factor, read/write consistency ve multi-DC topology bilinçli seçilmelidir.

## 11. Uniqueness sınırı

Cassandra arbitrary global uniqueness'i RDBMS unique constraint gibi otomatik sağlamaz.

Day 10'da global `user_id` uniqueness için ayrı ownership table veya LWT tasarlanmamıştır. Bu nedenle dokümantasyon böyle bir garanti iddia etmez.

## 12. Testcontainers

Integration test kapsamı:
- seller save/load
- listing save/load
- seller + month partition
- month isolation
- newest-first clustering
- paging
- empty partition
- exact row lookup

Aynı version-controlled CQL test container'a bootstrap edilir.

## 13. Runtime doğrulaması

Lokal:
- Cassandra 5.0.9
- port 9042
- datacenter `datacenter1`
- explicit schema bootstrap

Postman create/list/submit akışı gerçek Cassandra persistence üzerinde doğrulandı.

Listing row ayrıca doğrudan `cqlsh SELECT` ile doğrulandı.

## 14. Cassandra Admin

IBM Cassandra Admin tabanlı lokal browser UI Day 10'da seller profile'a eklendi.

- keyspace/table discovery çalıştı
- `seller_by_id` row görüntülendi
- listing table UI'da stale/empty rendering gözlendi

UI source of truth kabul edilmedi; listing persistence `cqlsh` ile doğrulandı.

## 15. Avantajlar

- horizontal scalability
- yüksek write throughput potansiyeli
- partition-key erişiminde öngörülebilirlik
- query-specific denormalized model
- multi-node availability tasarımına uygunluk

## 16. Trade-off'lar

- query-before-table tasarımı gerekir
- joins yoktur
- ad-hoc query esnekliği sınırlıdır
- denormalization write amplification yaratabilir
- partition sizing dikkat ister
- tombstone/compaction/repair operasyonel bilgi gerektirir
- LWT performans maliyeti taşır

## 17. Ne zaman tercih edilmemeli?

Aşağıdaki ihtiyaçlarda relational database daha doğal olabilir:
- yoğun relational join
- güçlü cross-row ACID transaction
- çok çeşitli ad-hoc query
- küçük/orta ölçekli basit CRUD
- global uniqueness'in doğal constraint olarak beklendiği modeller

## 18. Alternatifler

Probleme göre:
- PostgreSQL/MySQL
- MongoDB
- Couchbase
- ScyllaDB
- diğer managed key-value/wide-column datastore'lar

değerlendirilebilir.

## 19. Production considerations

- partition cardinality/size
- replication factor
- consistency levels
- compaction strategy
- tombstones
- repair
- hinted handoff
- backup/restore
- authentication/TLS
- multi-node/multi-DC topology
- observability
- capacity planning

## 20. İleri öğrenme konuları

- gossip
- consistent hashing / token ring
- vnode
- SSTable
- memtable / commit log
- compaction strategies
- read/write path internals
- LWT / Paxos
- repair / anti-entropy
- multi-DC consistency
