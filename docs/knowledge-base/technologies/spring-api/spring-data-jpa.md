# Spring Data JPA

**Category:** Technology  
**Introduced:** Day 1  
**Project status:** Implemented / Verified  
**Scope:** Repository abstraction over JPA

## 1. Nedir?

Spring Data JPA, JPA tabanlı persistence erişimini repository abstraction ve query derivation gibi özelliklerle kolaylaştıran Spring Data modülüdür.

Kendisi ORM değildir; JPA abstraction'ını developer-friendly repository modeliyle kullanmayı sağlar.

## 2. JPA ile ilişkisi

JPA bir specification'dır.

Spring Data JPA:
- repository abstraction
- query generation
- pagination
- specification integration

sağlar.

Hibernate ise JPA provider olarak çalışabilir.

## 3. Temel yapı

```text
Application Service
      |
      v
Spring Data Repository
      |
      v
JPA EntityManager
      |
      v
Hibernate
      |
      v
Database
```

## 4. Repository

Yaygın interface'ler:
- Repository
- CrudRepository
- PagingAndSortingRepository
- JpaRepository

## 5. Query derivation

Method isminden query türetilebilir.

Örnek:
```java
findByEmail(String email)
```

## 6. Custom queries

- @Query
- JPQL
- native SQL
- Specification
- custom repository implementation

kullanılabilir.

## 7. EntityManager

JPA persistence context'i yönetir.

Spring Data repository'leri altında EntityManager kullanılır.

## 8. Transaction ile ilişkisi

Persistence operation'ları uygun transaction boundary içinde çalışmalıdır.

@Transactionall application/service boundary'de bilinçli kullanılmalıdır.

## 9. Avantajları

- repository boilerplate azaltır
- hızlı CRUD
- pagination/sorting
- JPA ecosystem integration
- test kolaylığı

## 10. Trade-off'ları

- query generation yanlış kullanılırsa complexity gizlenebilir
- N+1 problemi
- lazy loading
- persistence model/domain model karışması
- repository abstraction'ının business semantics'i gizlemesi

## 11. Anti-pattern'ler

- her şeyi generic JpaRepository ile çözmek
- controller'dan repository çağırmak
- entity'yi API response olarak döndürmek
- transaction boundary'yi belirsiz bırakmak

## 12. Bu projede nasıl kullanılıyor?

AuthService ve UserProfileService PostgreSQL persistence için Spring Data JPA kullanmaktadır.

Agent/Buyer/Seller/Property baseline'ında da Day 7'ye kadar JPA/PostgreSQL dependency'leri korunmuştur; ilgili datastore migration'ları sonraki Day'lerde yapılacaktır.

## 13. Production considerations

- N+1
- fetch strategy
- pagination
- transaction isolation
- batching
- connection pool
- query plan
- index strategy
- OSIV

## 14. İleri öğrenme konuları

- EntityManager lifecycle
- persistence context
- specifications
- projections
- entity graphs
- locking
