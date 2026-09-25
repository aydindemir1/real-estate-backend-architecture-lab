# Service Autonomy

**Category:** Principle  
**Introduced:** Day 3  
**Project status:** Implemented as target principle / Incrementally strengthened  
**Scope:** Independent service ownership and lifecycle

## 1. Nedir?

Service Autonomy, bir microservice'in kendi responsibility alanında mümkün olduğunca bağımsız karar verebilmesi, çalışabilmesi, deploy edilebilmesi ve evrilebilmesi prensibidir.

## 2. Autonomy ne değildir?

Hiç dependency olmaması veya hiçbir shared infrastructure kullanılmaması anlamına gelmez. Dependency'lerin explicit, kontrollü ve contract tabanlı olması gerekir.

## 3. Autonomy boyutları

- Code autonomy: service kendi implementation'ına sahiptir.
- Data autonomy: kendi canonical verisinin sahibidir.
- Deployment autonomy: bağımsız deploy edilebilir.
- Runtime autonomy: başka service failure'larından mümkün olduğunca izole edilir.
- Team autonomy: ownership nettir.

## 4. Hangi problemi çözer?

- deployment coupling
- shared database dependency
- ownership ambiguity
- cascading changes
- global release coordination

## 5. Gereksinimler

- clear boundary
- own datastore
- explicit contract
- failure handling
- configuration isolation
- independent build/module
- observability
- versioning discipline

## 6. Avantajları ve trade-off'ları

Bağımsız delivery, fault isolation ve scaling sağlar. Daha fazla autonomy ise duplicate data, eventual consistency, messaging, reconciliation ve operational overhead getirebilir.

## 7. Bu projede nasıl uygulanıyor?

Auth, UserProfile, Agent, Buyer, Seller, Property ve Search ayrı service sınırlarıdır. Day 7 target datastore ownership Agent->MySQL, Buyer->Couchbase, Seller->Cassandra, Property->MongoDB ve Search->Elasticsearch olacak şekilde hazırlanmıştır.

## 8. Shared infrastructure ile ilişkisi

Eureka, Config Server veya RabbitMQ gibi platform capability'lerinin ortak olması business service autonomy'yi otomatik olarak bozmaz. Kritik nokta business state, contract ve internal implementation ownership'ıdır.

## 9. Anti-pattern'ler

- shared database schema
- cross-service table joins
- direct repository sharing
- one release train for every service
- shared internal domain object
- synchronous dependency chain explosion

## 10. İleri öğrenme konuları

- bounded contexts
- team topologies
- decentralized governance
- platform engineering
- service ownership model