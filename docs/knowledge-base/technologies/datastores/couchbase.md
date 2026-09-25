# Couchbase

**Category:** Technology  
**Introduced:** Day 7  
**Project status:** Infrastructure Ready  
**Scope:** Target document/key-value datastore for BuyerService

## 1. Nedir?

Couchbase, distributed document/key-value database platformudur.

JSON document storage ile key-value access ve SQL++ query capability'lerini birlikte sunar.

## 2. Veri modeli

Temel logical hierarchy:

```text
Cluster
  |
  v
Bucket
  |
  v
Scope
  |
  v
Collection
  |
  v
Document
```

## 3. Key-value access

Document key biliniyorsa direct KV lookup çok düşük latency ile yapılabilir.

Örnek key:
```text
buyer-preferences::{buyerId}
offer::{offerId}
```

## 4. SQL++

Couchbase JSON documents üzerinde SQL-benzeri query language sağlar.

KV access ile ad-hoc query ihtiyacı farklı access path'lerdir.

## 5. Distributed architecture

Data cluster node'ları arasında partition/shard edilir.

Couchbase terminology'sinde vBucket mekanizması distribution için temel role sahiptir.

## 6. Replication

Data replica'ları farklı node'larda tutulabilir.

Node failure durumunda failover mekanizmaları devreye girebilir.

## 7. Indexing

SQL++ query için secondary indexes gerekir.

Primary index development kolaylığı sağlayabilir ancak production query design için explicit index daha uygundur.

## 8. Bu projede nasıl kullanılıyor?

Day 7'de BuyerService için:
- Couchbase Community 8.x container
- admin credentials
- persistent volume
- HTTP healthcheck

hazırlanmıştır.

Bucket/scope/collection/index initialization Day 9'a bırakılmıştır.

## 9. Day 7 statüsü

`Infrastructure Ready`.

Buyer persistence implementation henüz yapılmamıştır.

## 10. Target Buyer data

Buyer tarafında:
- preferences
- offer state
- durable idempotency record

gibi document/KV oriented access modelleri planlanmaktadır.

## 11. Avantajları

- key-value performance
- JSON document model
- SQL++ query
- distributed architecture
- flexible schema

## 12. Trade-off'ları

- cluster operations relational DB'den farklıdır
- index consistency seçenekleri anlaşılmalıdır
- durability level doğru seçilmelidir
- KV ve query access modelleri ayrı tasarlanmalıdır

## 13. Production considerations

- bucket sizing
- memory quotas
- durability level
- replicas
- indexes
- failover
- backup
- rebalance
- query consistency

## 14. Reliability ile ilişkisi

BuyerService ileride OfferRequested gibi critical event publish edecektir.

Committed state ile outbound event kaybını önleyecek durable publication strategy ayrıca finalize edilmelidir.

## 15. İleri öğrenme konuları

- vBuckets
- DCP
- durability
- rebalance
- SQL++ optimizer
- scopes/collections
- XDCR
