# MongoDB

**Category:** Technology  
**Introduced:** Day 7  
**Project status:** Infrastructure Ready  
**Scope:** Target canonical document datastore for PropertyService

## 1. Nedir?

MongoDB, JSON-benzeri BSON document'leri collection'lar içinde saklayan document-oriented NoSQL database'dir.

## 2. Veri modeli

```text
Database
  |
  v
Collection
  |
  v
Document (BSON)
```

Document nested object ve array içerebilir.

## 3. Schema flexibility

MongoDB fixed relational table schema zorunluluğunu azaltır.

Ancak "schemaless" ifadesi "schema yok" anlamına gelmez.

Application ve validation layer yine veri shape'ini yönetmelidir.

## 4. BSON

MongoDB internal wire/storage modelinde BSON kullanır.

JSON'a benzer ama ek type desteği sunar.

## 5. Atomicity

Single-document write operation'ları atomic'tir.

Bu nedenle aggregate boundary'yi tek document içinde modellemek güçlü bir pattern olabilir.

Multi-document transaction da mümkündür ancak document model avantajını doğru kullanmak gerekir.

## 6. Indexing

Yaygın:
- single field
- compound
- multikey
- text
- geospatial
- TTL

index'ler desteklenir.

## 7. Replica Set

High availability için replica set kullanılır.

```text
Primary
  |
  +--> Secondary
  +--> Secondary
```

Election ile yeni primary seçilebilir.

## 8. Sharding

Large-scale horizontal distribution için shard architecture kullanılabilir.

## 9. Bu projede nasıl kullanılıyor?

Day 7'de PropertyService için:
- MongoDB 8.x container
- persistent volume
- authenticated healthcheck
- environment-driven admin credentials

hazırlanmıştır.

Application integration Day 11'e aittir.

## 10. Target Property model

Property aggregate:
- listing attributes
- lifecycle state
- embedded value structures

gibi document-oriented model için uygundur.

## 11. Avantajları

- flexible document model
- nested aggregate representation
- rich indexing
- horizontal scaling support
- developer-friendly JSON/BSON model

## 12. Trade-off'ları

- relational join semantics sınırlıdır
- document growth dikkat ister
- duplicated data olabilir
- schema discipline application tarafında gerekir
- transaction ihtiyacı artıyorsa model sorgulanmalıdır

## 13. Production considerations

- replica set
- write concern
- read concern
- indexes
- document size
- backup
- shard key
- connection pool
- schema validation

## 14. Bu projedeki architecture rolü

PropertyService canonical source of truth olacaktır.

Search için Elasticsearch derived projection olacak; MongoDB search index'in alternatifi değildir.

## 15. İleri öğrenme konuları

- WiredTiger
- oplog
- replica sets
- sharding
- write concern
- read concern
- aggregation pipeline
