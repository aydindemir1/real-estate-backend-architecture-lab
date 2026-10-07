# Onion Architecture

**Kategori:** Architecture  
**İlk eklendiği gün:** Day 10  
**Proje durumu:** Uygulandı / Doğrulandı  
**Kapsam:** SellerService

## Nedir?

Onion Architecture, business/domain modelini sistemin merkezine yerleştirip framework, database, HTTP ve diğer dış detayların merkeze doğru bağımlı olmasını hedefleyen dependency-oriented architecture yaklaşımıdır.

```text
Presentation ------> Application ------> Domain
Infrastructure -----------------------> Domain/Application
```

Bağımlılık yönü dış katmanlardan iç katmanlara doğrudur.

## Temel katmanlar

### Domain
Aggregate, Value Object, domain behavior, invariant, exception ve repository abstraction'larını taşır.

Domain:
- Spring bilmez
- Cassandra bilmez
- HTTP bilmez
- messaging framework'lerini bilmez

### Application
Use-case orchestration, command/query ve result contract'larını taşır. Domain'e bağımlı olabilir; infrastructure veya presentation implementation'larına bağımlı olmaz.

### Infrastructure
Database ve framework integration detail'lerini taşır:
- Cassandra table modelleri
- Spring Data Cassandra repository'leri
- persistence mapper'ları
- domain repository adapter'ları
- application configuration

### Presentation
HTTP delivery concern'lerini taşır:
- REST controllers
- request/response DTO'ları
- REST mapper'ları
- exception handler

## Dependency Rule

Day 10 SellerService için:
- domain -> application/infrastructure/presentation yok
- application -> infrastructure/presentation yok
- infrastructure -> presentation yok
- presentation -> infrastructure yok
- domain/application -> Spring/Cassandra/Kafka/RabbitMQ yok
- top-level package cycle yok

Bu kurallar `SellerOnionArchitectureTest` ile ArchUnit üzerinden executable hale getirilmiştir.

## Repository abstraction

Domain:
- `SellerRepository`
- `ListingSubmissionRepository`

Infrastructure:
- `CassandraSellerRepositoryAdapter`
- `CassandraListingSubmissionRepositoryAdapter`

Spring Data repository tipleri domain'e sızmaz.

## Domain / persistence ayrımı

Domain:
- `Seller`
- `ListingSubmission`

Persistence:
- `SellerByIdTable`
- `ListingSubmissionBySellerMonthTable`

Explicit mapper'lar iki modeli çevirir.

## Clean / Hexagonal ile ilişkisi

Ortak hedef:
- business core'u dış detaylardan ayırmak
- dependency direction'ı merkeze doğru kurmak
- framework/database bağımlılıklarını sınırda tutmak

Bu projede karşılaştırmalı öğrenme için:
- Day 8 AgentService -> Clean Architecture
- Day 9 BuyerService -> Hexagonal Architecture
- Day 10 SellerService -> Onion Architecture

## Avantajlar

- framework-independent core
- güçlü testability
- persistence technology'nin localize edilmesi
- domain/persistence model ayrımı
- architecture boundary'lerinin görünür olması

## Trade-off'lar

- ek abstraction ve mapping maliyeti
- küçük CRUD servislerde ceremony riski
- dependency rule executable testlerle korunmazsa architecture erosion olabilir

## Bu projede

Day 10 SellerService:
- framework-independent domain
- application orchestration
- Cassandra infrastructure adapters
- REST presentation layer
- explicit mapper boundaries
- ArchUnit dependency rules

ile Onion Architecture'ı gerçek implementation olarak kullanır.

## İlgili dokümanlar

- `SellerService/docs/DESIGN.md`
- `SellerService/docs/PACKAGE-DESIGN.md`
- `docs/roadmap/day-10-seller-cassandra-onion.md`
