# MySQL

**Category:** Technology  
**Introduced:** Day 7  
**Project status:** Infrastructure Ready  
**Scope:** Target relational datastore for AgentService

## 1. Nedir?

MySQL, relational data model, SQL ve transactional storage engine desteği sunan yaygın RDBMS'tir.

Bu projede AgentService için target relational datastore olarak seçilmiştir.

## 2. Storage engine architecture

MySQL architecture'da SQL layer ile storage engine ayrımı vardır.

```text
Client
  |
  v
SQL Layer
  |
  v
Optimizer / Executor
  |
  v
Storage Engine
  |
  v
InnoDB
```

Modern transactional workload'larda InnoDB ana storage engine'dir.

## 3. InnoDB

InnoDB:
- ACID transaction
- row-level locking
- MVCC
- clustered primary index
- foreign key
- redo/undo logging

sağlar.

## 4. Clustered index

InnoDB'da table data primary key B+Tree yaprağında tutulur.

Secondary index'ler primary key değerini referanslar.

Bu nedenle primary key tasarımı storage/layout üzerinde önemlidir.

## 5. MVCC

Consistent reads ve transaction isolation için row versioning kullanır.

Undo log eski version'ların oluşturulmasında rol oynar.

## 6. Redo log

Committed değişikliklerin crash recovery için dayanıklılığını destekler.

## 7. Query optimizer

Execution plan:
- statistics
- index selectivity
- join order
- access method

üzerinden seçilir.

## 8. Bu projede nasıl kullanılıyor?

Day 7'de:
- MySQL 8.4.x container
- Agent-specific volume
- environment-driven credentials
- healthcheck

hazırlanmıştır.

Application-level migration Day 8'e aittir.

## 9. Day 7 statüsü

`Infrastructure Ready`.

AgentService code/config hâlâ Day 8 migration'ı beklemektedir.

## 10. Neden AgentService?

Agent domain'i structured relational state, unique constraints ve optimistic concurrency gibi ihtiyaçlara uygundur.

## 11. Avantajları

- mature RDBMS
- ACID
- widespread operational knowledge
- strong ecosystem
- good OLTP performance

## 12. Trade-off'ları

- schema migration gerekir
- horizontal scaling ayrı architecture konusu
- index/PK tasarımı InnoDB'da kritik
- implicit behavior/version differences dikkat ister

## 13. Production considerations

- InnoDB buffer pool
- redo logs
- connection pool
- slow query log
- index design
- replication
- backup/restore
- charset/collation
- transaction isolation

## 14. PostgreSQL ile fark

İkisi de güçlü relational database'dir.

Farklar:
- storage architecture
- optimizer behavior
- SQL feature set
- extension model
- replication/operational tooling

use-case bazında değerlendirilmelidir.

## 15. İleri öğrenme konuları

- InnoDB internals
- clustered indexes
- undo/redo
- isolation
- replication
- EXPLAIN
- buffer pool
