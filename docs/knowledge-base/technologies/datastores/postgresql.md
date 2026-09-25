# PostgreSQL

**Category:** Technology  
**Introduced:** Day 1  
**Project status:** Implemented / Integrated / Verified  
**Scope:** Relational source-of-truth datastore for AuthService and UserProfileService

## 1. Nedir?

PostgreSQL, ACID transaction, relational modeling, SQL, indexing ve güçlü consistency özellikleri sunan open-source relational database management system'idir.

## 2. Veri modeli

Temel yapı:
- database
- schema
- table
- row
- column
- primary key
- foreign key
- constraint
- index

Relational model explicit schema ve constraint tabanlıdır.

## 3. ACID

PostgreSQL transaction'ları:
- Atomicity
- Consistency
- Isolation
- Durability

özelliklerini destekler.

## 4. MVCC

PostgreSQL concurrency için Multi-Version Concurrency Control kullanır.

Amaç:
- reader/writer blocking'i azaltmak
- transaction isolation sağlamak
- snapshot semantics sunmak

Eski row version'ları VACUUM mekanizmasıyla temizlenir.

## 5. WAL

Write-Ahead Logging, değişikliklerin data page'lerinden önce log'a yazılmasını sağlar.

WAL:
- crash recovery
- replication
- durability

için kritik mekanizmadır.

## 6. Process architecture

PostgreSQL klasik olarak process-oriented architecture kullanır.

Basitleştirilmiş:

```text
Client
  |
  v
Postmaster / Server
  |
  +--> Backend Process
  +--> WAL Writer
  +--> Checkpointer
  +--> Background Writer
  +--> Autovacuum
```

## 7. Buffer cache

Database page'leri shared buffers ve işletim sistemi page cache üzerinden yönetilir.

## 8. Indexing

Yaygın index türleri:
- B-tree
- Hash
- GIN
- GiST
- BRIN

Default ve en yaygın general-purpose index B-tree'dir.

## 9. Query planner

PostgreSQL query planner:
- table statistics
- available index'ler
- estimated row counts
- join strategies

üzerinden execution plan üretir.

## 10. Bu projede nasıl kullanılıyor?

AuthService ve UserProfileService canonical relational datastore olarak PostgreSQL kullanır.

Day 7 sonunda bu iki PostgreSQL container korunmuş, Agent/Buyer/Seller/Property için geçici PostgreSQL container'ları kaldırılmıştır.

## 11. Neden uygun?

Auth/UserProfile alanlarında:
- structured schema
- uniqueness
- relational constraints
- transactional integrity

önemlidir.

## 12. Avantajları

- güçlü SQL
- ACID
- mature optimizer
- indexing seçenekleri
- JSON desteği
- extensibility
- production maturity

## 13. Trade-off'ları

- horizontal write scaling doğal default değildir
- schema değişiklikleri migration gerektirir
- kötü query/index tasarımı ciddi performans problemi oluşturabilir
- long-running transaction MVCC cleanup'ı etkileyebilir

## 14. Production considerations

- connection pooling
- VACUUM/autovacuum
- index maintenance
- query plans
- WAL growth
- backups
- replication
- transaction isolation
- lock monitoring
- storage capacity

## 15. İleri öğrenme konuları

- MVCC internals
- WAL
- planner/optimizer
- HOT updates
- vacuum
- replication
- partitioning
- locking
