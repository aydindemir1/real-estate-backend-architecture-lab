# Hibernate

**Category:** Technology  
**Introduced:** Day 1  
**Project status:** Implemented / Verified  
**Scope:** JPA ORM provider

## 1. Nedir?

Hibernate, Java object modeli ile relational database modeli arasında mapping sağlayan ORM framework'üdür.

Bu projede JPA provider olarak Spring Data JPA altında kullanılır.

## 2. ORM nedir?

Object-Relational Mapping:

```text
Java Object Model
      |
      v
ORM Mapping
      |
      v
Relational Tables
```

## 3. Temel kavramlar

- Entity
- Persistence Context
- Session / EntityManager
- Dirty Checking
- Lazy Loading
- Eager Loading
- First-Level Cache
- Flush
- Transaction
- Mapping

## 4. Persistence Context

Managed entity'lerin lifecycle'ını takip eder.

Entity state'leri:
- transient
- managed
- detached
- removed

## 5. Dirty Checking

Managed entity değiştiğinde Hibernate transaction flush sırasında değişikliği algılayıp SQL update üretebilir.

## 6. First-Level Cache

Persistence context scope içinde aynı entity tekrar yüklendiğinde database roundtrip azaltılabilir.

## 7. Lazy Loading

Relation ihtiyaç olduğunda yüklenir.

Avantaj:
- gereksiz data load azaltabilir

Risk:
- N+1
- LazyInitializationException
- beklenmedik query

## 8. Eager Loading

Relation hemen yüklenir.

Aşırı kullanım:
- büyük join
- fazla data
- performance problemi

oluşturabilir.

## 9. SQL üretimi

Hibernate object operation'larını SQL'e dönüştürür.

Ancak developer:
- generated SQL
- query plan
- index
- transaction

konularını anlamaya devam etmelidir.

## 10. Avantajları

- ORM productivity
- JPA standardı
- dirty checking
- relationship mapping
- caching
- mature ecosystem

## 11. Dezavantajları

- abstraction leak
- N+1
- hidden SQL
- mapping complexity
- performance surprises
- object/relational impedance mismatch

## 12. Bu projede nasıl kullanılıyor?

Auth ve UserProfile PostgreSQL persistence katmanında Spring Data JPA üzerinden Hibernate kullanılmaktadır.

## 13. Production considerations

- show_sql production'da kullanılmamalı
- SQL logging kontrollü olmalı
- ddl-auto update uzun vadeli schema management değildir
- migration tool kullanılmalı
- fetch plan bilinçli tasarlanmalı
- batching ve indexing ölçülmelidir

## 14. Alternatifleri

- JDBC
- jOOQ
- MyBatis
- Spring Data JDBC
- native SQL

## 15. İleri öğrenme konuları

- Hibernate internals
- flush modes
- second-level cache
- batch fetching
- bytecode enhancement
- locking
- optimistic concurrency
