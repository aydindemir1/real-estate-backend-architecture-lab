# Apache Cassandra

**Category:** Technology  
**Introduced:** Day 7 infrastructure  
**Application implementation:** Day 10  
**Project status:** Implemented / Verified  
**Scope:** SellerService

## 1. Nedir?

Apache Cassandra distributed wide-column NoSQL database'tir.

Bu projede relational model kopyalanmamış, query-first physical model kullanılmıştır.

## 2. Day 10 modeli

Keyspace:

`seller_service`

Tables:
- `seller_by_id`
- `listing_submissions_by_seller_and_month`

## 3. Query-first design

Seller lookup:

`seller_by_id`

Listing history:

`listing_submissions_by_seller_and_month`

Partition:

```text
(seller_id, year_month)
```

Clustering:

```text
created_at DESC
submission_id ASC
```

Bu model one-seller/one-month bounded query için tasarlanmıştır.

## 4. Uygulanan kurallar

- no `ALLOW FILTERING`
- no cross-partition scan
- no relational join assumption
- domain Aggregate ile Cassandra table class ayrıdır
- explicit mapper/adapter kullanılır
- schema version-controlled CQL ile tutulur

## 5. Spring Data Cassandra

SellerService Cassandra infrastructure:
- table models
- Spring Data repositories
- explicit mappers
- domain repository adapters

Cassandra types domain layer'a sızdırılmaz.

## 6. Testcontainers

Integration test kapsamı:
- seller save/load
- listing save/load
- seller+month partition
- month isolation
- newest-first clustering
- paging
- empty partition

## 7. Runtime verification

Lokal:
- Cassandra 5.0.9
- port 9042
- datacenter `datacenter1`
- explicit CQL bootstrap

Postman create/list/submit akışı gerçek Cassandra persistence üzerinde doğrulandı.

Ayrıca listing row doğrudan `cqlsh SELECT` ile doğrulandı.

## 8. Cassandra Admin

IBM Cassandra Admin lokal browser UI olarak Day 10'a eklendi.

- keyspace/table discovery çalıştı
- `seller_by_id` row görüntülendi
- listing table UI'da stale/empty rendering gözlendi

Listing persistence bu UI'ya güvenilmeden doğrudan `cqlsh` ile doğrulandı.

## 9. Production considerations

İleri konular:
- replication factor
- consistency levels
- partition sizing
- compaction strategy
- tombstones
- repair
- backup/restore
- multi-node topology
- authentication/TLS
- observability
