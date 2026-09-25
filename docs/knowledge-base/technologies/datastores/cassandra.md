# Apache Cassandra

**Category:** Technology  
**Introduced:** Day 7  
**Project status:** Infrastructure Ready  
**Scope:** Target wide-column datastore for SellerService

## 1. Nedir?

Apache Cassandra, distributed, partitioned, wide-column NoSQL database'dir.

Yüksek write throughput, horizontal scalability ve availability odaklı workload'lar için tasarlanmıştır.

## 2. Data model

Cassandra'da data model query-first tasarlanır.

Temel yapılar:
- keyspace
- table
- partition key
- clustering columns
- rows

## 3. Partition key

Partition key verinin cluster'da hangi node/partition'a gideceğini belirler.

Yanlış partition key:
- hotspot
- unbounded partition
- poor distribution

oluşturabilir.

## 4. Clustering columns

Aynı partition içindeki row ordering ve range query modelini belirler.

## 5. Query-first modeling

Relational normalizasyon yerine:
> Hangi query'yi çalıştıracağım?

sorusu üzerinden table tasarlanır.

Bu nedenle aynı business data birden fazla query-specific table'da denormalize edilebilir.

## 6. Write path

Basitleştirilmiş:

```text
Client Write
   |
   v
Commit Log
   |
   v
Memtable
   |
   v
SSTable
```

## 7. Read path

Read sırasında:
- memtable
- SSTable
- bloom filter
- partition index

mekanizmaları devreye girebilir.

## 8. Compaction

Immutable SSTable'lar zaman içinde compaction ile birleştirilir.

Compaction strategy workload'a göre önemlidir.

## 9. Distributed architecture

Cassandra masterless/peer-to-peer tasarıma sahiptir.

Consistent hashing/token ranges ile data dağıtılır.

Replication factor ile kopyalar tutulur.

## 10. Tunable consistency

Read/write consistency level seçilebilir.

Örnek:
- ONE
- QUORUM
- ALL
- LOCAL_QUORUM

Consistency/availability trade-off workload'a göre ayarlanır.

## 11. Bu projede nasıl kullanılıyor?

Day 7'de SellerService için:
- Cassandra 5.x container
- persistent volume
- datacenter config
- cqlsh healthcheck

hazırlanmıştır.

Application-level schema/model Day 10'a aittir.

## 12. Target Seller query model

Planlanan query-oriented tables:
- seller_by_id
- listing_submissions_by_seller_and_month
- later pending offer projections

gibi yapılardır.

## 13. Avantajları

- horizontal scalability
- high write throughput
- no single master
- tunable consistency
- fault tolerance

## 14. Trade-off'ları

- relational join yok
- ad-hoc query sınırlı
- query-first schema gerekir
- partition design kritik
- tombstone/compaction yönetimi gerekir

## 15. Production considerations

- replication factor
- consistency level
- partition size
- tombstones
- compaction
- repair
- backup
- disk sizing
- JVM tuning
- monitoring

## 16. Reliability ile ilişkisi

SellerService'in critical outbound message'ları için relational transactional outbox semantics doğrudan kopyalanmayacaktır.

Cassandra-friendly durable pending outbound strategy roadmap'te ayrıca ele alınacaktır.

## 17. İleri öğrenme konuları

- gossip
- hinted handoff
- repair
- anti-entropy
- SSTables
- compaction
- tombstones
- consistency levels
- token ring
