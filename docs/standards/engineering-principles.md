# Engineering Principles Standard

Bu doküman Real Estate Backend Architecture Lab projesinde uygulanacak ortak mühendislik prensiplerini tanımlar.

Amaç yalnızca çalışan kod üretmek değil; okunabilir, test edilebilir, değiştirilebilir, güvenli ve architecture boundary'lerine sadık kod üretmektir.

## 1. Clean Code

### Naming

Class, method, variable ve package isimleri niyeti açıkça ifade etmelidir.

Kötü:
```java
process();
handle();
doStuff();
data;
obj;
```

Tercih:
```java
publishProperty();
holdPropertyForOffer();
changeAgentAvailability();
findPendingOffersBySeller();
```

Boolean isimleri soru gibi okunmalıdır:

```java
isPublished
isAgentActive
hasActiveOffer
canBeReserved
```

### Method boyutu

Method tek bir işi yapmalıdır.

Uzun orchestration akışları küçük private method'lara bölünebilir ancak sırf satır sayısını azaltmak için anlamsız parçalama yapılmaz.

### Guard Clause

Deep nesting yerine guard clause tercih edilir.

```java
if (!seller.isActive()) {
    throw new SellerNotActiveException(...);
}

if (!property.canBePublished()) {
    throw new InvalidPropertyStateException(...);
}
```

### Comments

Comment kötü naming veya kötü abstraction'ı telafi etmek için kullanılmaz.

Comment yalnızca:
- neden bu trade-off seçildi?
- neden sıra dışı bir workaround var?
- neden belirli bir external limitation mevcut?

gibi koddan doğrudan görülemeyen "why" bilgisini taşımalıdır.

## 2. OOP

### Encapsulation

Aggregate state doğrudan dışarıdan değiştirilemez.

Kötü:
```java
property.setStatus(PropertyStatus.PUBLISHED);
```

Tercih:
```java
property.publish();
```

### Behavior-rich model

Gerçek business invariant ve state transition varsa behavior Aggregate üzerinde bulunmalıdır.

Ancak yalnızca veri taşıyan DTO/Projection modellerine yapay domain behavior eklenmez.

### Composition over Inheritance

Business behavior paylaşımı için inheritance ilk tercih değildir.

Inheritance yalnızca açık "is-a" ilişkisi ve substitutability varsa kullanılır.

Çoğu durumda:
- Strategy
- composition
- delegation

tercih edilir.

### Primitive Obsession azaltma

Business anlamı güçlü primitive'ler Value Object ile temsil edilebilir:

- Money
- Email
- LicenseNumber
- PropertyId
- OfferId
- PriceRange
- GeoLocation

Ancak her String için Value Object oluşturulmaz.

## 3. SOLID

### SRP — Single Responsibility Principle

Bir class'ın değişmesi için tek ana nedeni olmalıdır.

Örnek:
- Controller HTTP sorumluluğu
- Handler/UseCase application orchestration
- Aggregate business invariant
- Repository persistence abstraction
- Mapper model dönüşümü

### OCP — Open/Closed Principle

Değişken business policy'lerde yeni davranış eklerken mevcut kodun gereksiz değiştirilmesini azalt.

Örnek candidate:
`PropertyPublicationPolicy`

Property type'a göre publication rule değişirse Strategy yaklaşımı değerlendirilebilir.

### LSP — Liskov Substitution Principle

Bir interface implementation'ı contract'ın semantic'ini bozmamalıdır.

Örneğin:
`AgentRepository` implementation'ları aynı expected behavior'ı sağlamalıdır.

### ISP — Interface Segregation Principle

Büyük "God Interface" oluşturulmaz.

Kötü:
```java
BuyerService {
  createPreferences();
  updatePreferences();
  createOffer();
  cancelOffer();
  requestViewing();
  publishEvent();
  ...
}
```

Hexagonal Architecture'da küçük port'lar tercih edilir.

### DIP — Dependency Inversion Principle

High-level business logic low-level technology detaylarına bağlı olmaz.

Örnek:
```text
BuyerApplicationService
       |
       v
PublishOfferEventPort
       ^
       |
KafkaOfferEventPublisher
```

## 4. KISS

Mümkün olan en basit doğru çözüm seçilir.

İlk use-case için gerekmiyorsa:
- generic framework
- reflection tabanlı abstraction
- custom mini framework
- aşırı inheritance
- gereksiz helper hierarchy

oluşturulmaz.

## 5. YAGNI

"İleride lazım olabilir" gerekçesiyle kullanılmayan abstraction veya feature eklenmez.

Örnek:
- RecommendationService ihtiyacı yoksa oluşturulmaz.
- AgencyService ihtiyacı yoksa AgencyInfo Value Object yeterlidir.
- GraphQL Mutation gerekmiyorsa eklenmez.

## 6. DRY

Business rule birden fazla yerde farklı biçimde kopyalanmaz.

Ancak her benzer görünen kod hemen ortak abstraction'a çekilmez.

Semantic duplication ile syntactic similarity ayrılır.

## 7. Tell, Don't Ask

Aggregate'ın state'i dışarı alınarak dışarıda karar verilmesi yerine Aggregate'a davranış söylenir.

Kötü:
```java
if (property.getStatus() == PUBLISHED) {
    property.setStatus(ON_HOLD);
}
```

Tercih:
```java
property.holdForOffer(offerId);
```

## 8. Law of Demeter

Deep object navigation azaltılır.

Kötü:
```java
order.getBuyer().getProfile().getAddress().getCity();
```

Bu kural mutlak değildir; gereksiz coupling'i azaltmak için kullanılır.

## 9. Immutability

Value Object'ler mümkün olduğunca immutable tasarlanır.

Tercih:
- final field
- record
- defensive copy
- unmodifiable collection

Aggregate tamamen immutable olmak zorunda değildir; controlled mutation behavior method'ları üzerinden yapılır.

## 10. Null handling

Domain code'da null semantic'i açık olmalıdır.

Tercihler:
- Optional yalnızca uygun return type'larda
- Null yerine Empty Collection
- Value Object validation
- constructor/factory precondition

`Optional` entity field olarak kullanılmaz.

## 11. Exception kullanımı

Exception normal control flow için kullanılmaz.

Domain/Application exception isimleri gerçek problem semantiğini ifade eder.

Örnek:
- InvalidPropertyStateException
- SellerNotActiveException
- OfferNotFoundException
- IdempotencyConflictException

Infrastructure exception doğrudan API'ye sızdırılmaz.

## 12. Dependency Direction

Her service kendi Architecture rule'una uyar.

### Clean Architecture
domain <- application <- infrastructure/presentation

### Hexagonal Architecture
domain/application core <- ports <- adapters

### Onion Architecture
domain merkezde, dış halkalar içeri bağımlı

### Vertical Slice Architecture
feature'lar birbirinin internal implementation'ına bağımlı olmamalı

## 13. Framework bağımlılığı

Framework convenience, domain model'i bozacak şekilde kullanılmaz.

Örnek:
AgentService domain model'i JPA annotation taşımaz.

Ancak N-Layer baseline service'lerde JPA entity'nin domain entity ile aynı olması eğitim karşılaştırması gereği kabul edilebilir.

## 14. Mapping

Mapping yalnızca model boundary varsa kullanılır.

Örnek:
- REST DTO <-> Application command
- Domain <-> Persistence entity
- Domain Event <-> Kafka payload
- Domain <-> Elasticsearch projection

Gereksiz mapper chain oluşturulmaz.

## 15. Transaction boundary

Transaction controller'da yönetilmez.

Application use-case seviyesinde transaction boundary tanımlanır.

Distributed transaction kullanılmaz; service boundary dışında Saga/Eventual Consistency kullanılır.

## 16. Side effect yönetimi

Bir use-case'in side effect'leri görünür olmalıdır:
- DB write
- event publish
- external call
- cache write

Business logic ile infrastructure side effect iç içe geçirilmez.

## 17. Configuration

Magic value kod içinde tutulmaz.

Environment/service configuration:
- timeout
- retry count
- topic/queue
- cache TTL
- rate limit
- external endpoint

configuration üzerinden yönetilir.

## 18. Logging

Business logic log string'lerine bağımlı olmaz.

Sensitive data loglanmaz:
- password
- token
- secret
- full credential

Structured logging tercih edilir.

## 19. Method parameter

Çok fazla primitive parameter yerine anlamlı Command/Value Object kullanılabilir.

Kötü:
```java
createOffer(UUID buyerId, UUID propertyId, BigDecimal amount, String currency, ...)
```

Tercih:
```java
createOffer(CreateOfferCommand command)
```

## 20. Collection kullanımı

Domain collection'lar mümkün olduğunca dışarı mutable olarak expose edilmez.

Getter:
```java
return List.copyOf(features);
```

gibi defensive yaklaşım kullanabilir.

## 21. Time

Doğrudan `Instant.now()` kullanımını domain'in her yerine yaymak yerine test edilebilirlik gereken yerde `Clock` injection değerlendirilebilir.

Özellikle:
- expiry
- timeout
- offer expiration
- activity time

testlerinde faydalıdır.

## 22. UUID / ID üretimi

ID generation strategy application/domain tasarımına göre açık olmalıdır.

Gerekirse:
`IdGenerator` abstraction kullanılabilir.

Sırf pattern göstermek için abstraction oluşturulmaz.

## 23. Senior-level code review soruları

Her pull request için:

- Bu class'ın tek sorumluluğu var mı?
- Naming niyeti açık mı?
- Business invariant doğru katmanda mı?
- Domain model dış teknolojiye gereksiz bağlı mı?
- Controller business logic içeriyor mu?
- Repository business orchestration yapıyor mu?
- Transaction boundary doğru yerde mi?
- Concurrency/idempotency riski var mı?
- Retry gerçekten güvenli mi?
- Null/Optional semantic'i açık mı?
- Mapping boundary gerekli mi?
- Test edilmesi kolay mı?
- Failure path test edilmiş mi?
- Sensitive data loglanıyor mu?
- Bu abstraction gerçekten gerekli mi?
- Aynı davranış başka yerde duplicate mı?
- Architecture dependency rule bozulmuş mu?

## 24. Anti-pattern listesi

Projede kaçınılacak:
- God Class
- God Service
- Fat Controller
- Anemic Domain Model gereken yerde
- Generic BaseService her service'e zorla
- Generic Repository üzerine gereksiz abstraction
- Utility Class çöplüğü
- DTO = Entity
- Entity'nin doğrudan API response olması
- Shared database
- Cross-service direct DB access
- Distributed Monolith
- Chatty synchronous communication
- Retry storm
- Catch Exception ve ignore
- Empty catch
- boolean flag ile karmaşık behavior
- magic string / magic number
- hard-coded secret
- premature optimization
- premature abstraction

## 25. Definition of Code Quality

Bir feature tamamlanmış sayılmadan önce:

- naming okunabilir
- architecture boundary korunmuş
- business rule doğru yerde
- happy/failure path test edilmiş
- error contract uyumlu
- security ownership kontrol edilmiş
- observability düşünülmüş
- concurrency/idempotency değerlendirilmiş
- gereksiz duplication/abstraction yok
- static analysis sonucu kabul edilebilir
- documentation gerekiyorsa güncellenmiş

olmalıdır.
