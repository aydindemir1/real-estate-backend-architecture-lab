# Clean Architecture

**Category:** Architecture  
**İlk eklendiği gün:** Day 8  
**Proje durumu:** Implemented  
**Kapsam:** AgentService

## Nedir?

Clean Architecture, business/domain logic'i framework, database ve delivery mechanism gibi dış detaylardan ayırmayı hedefleyen dependency-oriented architecture yaklaşımıdır.

```text
Presentation ---> Application ---> Domain
Infrastructure ------------------> Domain/Application
```

Domain outer layer'ları bilmez.

## Temel katmanlar

### Domain
Aggregate, Value Object, business behavior, invariant ve repository abstraction'larını taşır. Framework bağımlılığı olmamalıdır.

### Application
Use-case orchestration, command/query/result contract'ları ve transaction boundary'lerini taşır.

### Infrastructure
Database, Spring Data, JPA ve framework adapter'larını taşır.

### Presentation
HTTP request/response ve controller gibi delivery concern'lerini taşır.

## Dependency Rule

AgentService için:
- domain -> application/infrastructure/presentation yok
- application -> infrastructure/presentation yok
- presentation -> persistence infrastructure yok
- infrastructure -> domain/application olabilir

Bu kurallar ArchUnit ile executable rule haline getirilmiştir.

## Domain / persistence ayrımı

`Agent` JPA entity değildir.  
`AgentJpaEntity` persistence modelidir.

`AgentRepository` domain port, `AgentRepositoryAdapter` infrastructure adapter rolündedir.

## Avantajlar

- framework-independent domain
- business rules için güçlü testability
- boundary'lerin görünür olması
- technology details'in localize edilmesi

## Trade-off'lar

- daha fazla class/mapping
- küçük CRUD servislerde ceremony riski
- abstraction'ların gerçek boundary ihtiyacına dayanması gerekir

## Bu projede

Day 8 AgentService:
- framework-free Agent Aggregate
- domain repository abstraction
- application use-case interfaces
- JPA/Spring Data yalnız infrastructure
- REST DTO/controller yalnız presentation
- ArchUnit dependency tests
