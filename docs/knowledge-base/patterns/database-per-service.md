# Database per Service Pattern

**Category:** Pattern  
**Introduced:** Day 3 / Strengthened Day 7  
**Project status:** Architecture rule / Incrementally implemented  
**Scope:** Independent datastore ownership per service

## 1. Nedir?

Database per Service, her microservice'in kendi data store boundary'sine sahip olması pattern'idir.

Başka service'ler bu database'e doğrudan erişmez.

## 2. Problem

Shared database:
- schema coupling
- deployment coupling
- ownership ambiguity
- cross-service transaction temptation

oluşturabilir.

## 3. Temel kural

```text
Service A -> Database A
Service B -> Database B
Service C -> Database C
```

Service B, Database A'ya doğrudan bağlanmaz.

## 4. Data Ownership ile ilişkisi

Data Ownership prensiptir.

Database per Service bu prensibin teknik realization pattern'idir.

## 5. Bu projede target yapı

- Auth -> PostgreSQL
- UserProfile -> PostgreSQL
- Agent -> MySQL
- Buyer -> Couchbase
- Seller -> Cassandra
- Property -> MongoDB
- Search -> Elasticsearch derived projection

## 6. Search istisnası

SearchService datastore'u canonical business database değildir.

Elasticsearch, Property source of truth'tan beslenen derived query model'dir.

## 7. Avantajları

- schema autonomy
- service autonomy
- independent scaling
- polyglot persistence
- clear ownership

## 8. Dezavantajları

- cross-service join yok
- distributed transaction yok
- eventual consistency
- data duplication
- integration complexity

## 9. Data paylaşımı nasıl yapılır?

- REST
- events
- messaging
- projections
- API composition

## 10. Anti-pattern'ler

- shared schema
- direct cross-service SQL
- foreign key across service DB
- canonical state duplication

## 11. Production considerations

- backup ownership
- migration ownership
- disaster recovery
- access isolation
- secret separation
- monitoring

## 12. İleri öğrenme konuları

- saga
- CQRS
- outbox
- CDC
- reconciliation
