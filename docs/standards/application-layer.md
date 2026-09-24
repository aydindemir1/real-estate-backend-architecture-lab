# Application Layer Standard

Bu doküman projedeki application layer sorumluluklarını ve sınırlarını tanımlar.

Amaç:
- Controller ile domain/business logic'i ayırmak,
- use-case orchestration'ı doğru katmanda tutmak,
- transaction boundary'yi netleştirmek,
- external dependency kullanımını port/adapter veya infrastructure abstraction üzerinden yönetmek,
- side-effect'leri görünür ve test edilebilir hale getirmek.

# 1. Application Layer'ın rolü

Application Layer:
- use-case orchestration yapar,
- transaction boundary belirler,
- repository/port çağrılarını koordine eder,
- domain behavior'ı tetikler,
- external side-effect sırasını yönetir,
- authorization/ownership context'i kullanabilir,
- domain result'i presentation'a uygun result/DTO modeline dönüştürür.

Application Layer business invariant'ın ana sahibi değildir.

# 2. Controller sorumluluğu

Controller:
- HTTP request'i alır,
- syntactic validation yapar,
- authentication context'i çıkarır,
- Request DTO'yu Command/Query'ye map eder,
- use-case çağırır,
- sonucu Response DTO'ya map eder,
- HTTP status/header döndürür.

Controller'ın yapmayacağı:
- repository çağrısı
- transaction yönetimi
- domain state mutation
- Kafka/RabbitMQ publish
- gRPC orchestration
- retry
- business branching

# 3. Use Case

Use Case, application capability'nin açık contract'ıdır.

Örnek:
- CreateOfferUseCase
- PublishPropertyUseCase
- SubmitListingUseCase
- ChangeAgentAvailabilityUseCase

Clean/Hexagonal service'lerde interface olarak bulunabilir.

Vertical Slice'ta explicit interface zorunlu değildir; Handler use-case boundary'si olabilir.

# 4. Application Service

Application Service bir veya daha fazla closely-related use-case'i orkestre edebilir.

Örnek:
- OfferApplicationService
- SellerApplicationService

Ancak "God ApplicationService" oluşturulmaz.

Bir service çok büyürse capability bazında ayrılır.

# 5. Command

Command state change intent taşır.

Örnek:
- CreateOfferCommand
- PublishPropertyCommand
- ChangeAgentStatusCommand

Command:
- immutable olmalı,
- transport-specific annotation taşımamalı,
- domain entity olmamalı,
- request context'ten gereken identity/ownership bilgisini açıkça taşımalı.

# 6. Query

Query read intent taşır.

Örnek:
- GetPropertyQuery
- SearchPropertiesQuery
- ListBuyerOffersQuery

Query state değiştirmez.

# 7. Handler

Vertical Slice Architecture'ta Handler primary application orchestration unit'tir.

Örnek:
- PublishPropertyHandler
- HoldPropertyForOfferHandler

Handler:
1. gerekli data'yı yükler,
2. authorization/ownership check yapar,
3. domain behavior çağırır,
4. persistence yapar,
5. gerekli integration event/command üretimini koordine eder.

# 8. Transaction boundary

Transaction boundary application use-case/handler seviyesinde tanımlanır.

Controller veya repository transaction boundary sahibi değildir.

Örnek:
```text
PublishPropertyHandler
  -> load Property
  -> property.publish()
  -> save Property
  -> save OutboxEvent
```

Bu local transaction tek boundary içinde olabilir.

# 9. @Transactional kullanımı

`@Transactional`:
- application service/handler üzerinde tercih edilir,
- domain model'e konmaz,
- controller'a konmaz,
- read-only query için gerektiğinde `readOnly=true` kullanılabilir.

Distributed transaction için kullanılmaz.

# 10. Orchestration vs business logic

Application Layer:
```text
load -> authorize -> call domain -> save -> publish
```

Domain:
```text
business rule / invariant / state transition
```

Kötü:
```java
if (property.getStatus() == PUBLISHED) {
    property.setStatus(ON_HOLD);
}
```

Tercih:
```java
property.holdForOffer(command.offerId());
```

# 11. Ownership check

Ownership check application/security boundary'de yapılır.

Örnek:
- Seller sadece kendi Property'sini publish eder.
- Buyer yalnızca kendi Offer'ını cancel eder.

Domain entity user token bilmez.

# 12. Port kullanımı

Hexagonal/Clean service'lerde application layer low-level technology'yi direkt kullanmaz.

Kötü:
```java
KafkaTemplate
RedisTemplate
CouchbaseRepository
```

Tercih:
```text
PublishOfferEventPort
IdempotencyPort
SaveOfferPort
```

# 13. External service call

Application Layer external call'ı port/client abstraction üzerinden yapar.

Örnek:
```text
RequestViewingUseCase
 -> AgentAvailabilityPort
```

gRPC implementation adapter tarafındadır.

# 14. Side-effect ordering

Use-case içinde side-effect sırası bilinçli olmalıdır.

Örnek:
```text
validate
-> load aggregate
-> domain transition
-> persist
-> outbox/event record
-> commit
```

Commit öncesi external publish riskliyse Outbox tercih edilir.

# 15. Failure semantics

Application Layer failure'ları kategorize eder:
- domain failure
- dependency failure
- concurrency failure
- idempotency conflict
- authorization failure

Infrastructure exception doğrudan yukarı sızdırılmaz.

# 16. Retry

Application Service içine manuel retry loop yazılmaz.

Retry policy resilience/infrastructure katmanında uygulanır.

Retry öncesi operation'ın idempotent olup olmadığı değerlendirilir.

# 17. Idempotency

Critical write use-case'lerde idempotency application boundary'de kontrol edilir.

Örnek:
`CreateOfferUseCase`

Akış:
```text
check idempotency key
-> existing result varsa return
-> command execute
-> result store
```

# 18. Mapping

Boundary mapping:
- Request DTO -> Command
- Query -> Application Result
- Domain -> Result
- Result -> Response DTO

Application Layer REST-specific response code bilmez.

# 19. Application Result

Use-case sonucu domain entity expose etmek yerine stable result model olabilir.

Örnek:
`OfferResult`
`AgentResult`

Bu özellikle Clean/Hexagonal service'lerde faydalıdır.

# 20. Authentication context

Application Layer authentication principal'dan raw JWT parse etmez.

Presentation/Security adapter:
- userId
- roles/scopes
- subject

gibi normalized context sağlar.

# 21. Authorization context

Örnek abstraction:
`CurrentActor`

Alanlar:
- subjectId
- userId?
- roles
- scopes

Ancak shared framework üretmek için acele edilmez.

# 22. Query side optimization

Read use-case:
- domain Aggregate load etmek zorunda değildir,
- projection/read repository kullanabilir.

Özellikle SearchService tamamen read/query optimized çalışır.

# 23. CQRS ayrımı

Command/Query class kullanmak tek başına CQRS değildir.

Gerçek CQRS:
- write model ve read model ayrımı,
- farklı persistence model/flow

gerektirir.

# 24. Validation

Application Layer:
- cross-field business precondition
- ownership
- existence
- workflow precondition

kontrol edebilir.

Primitive format validation presentation boundary'de yapılır.

# 25. Concurrency

Application use-case concurrency riskini tanır.

Örnek:
`HoldPropertyForOfferHandler`

- version check
- Optimistic Locking
- conflict mapping

uygulanır.

# 26. Event handling use-case'leri

Kafka/RabbitMQ consumer doğrudan domain logic barındırmaz.

Consumer:
- message deserialize/validate
- deduplication
- application handler çağırma
- ack/nack kararını infrastructure policy ile yürütme

# 27. Scheduler / Task use-case

Spring Cloud Task veya scheduled job:
- application use-case çağırır,
- business rule task class'ın içine gömülmez.

# 28. Service bazında yaklaşım

## AuthService
N-Layer:
Controller -> Service -> Repository.

Service business/application orchestration'ın birleşik baseline örneğidir.

## UserProfileService
N-Layer baseline.

## AgentService
UseCase interface -> AgentApplicationService -> domain/repository abstraction.

## BuyerService
Inbound Port -> Application Service -> Outbound Port.

## SellerService
Application Service -> Domain -> Repository/Publisher port.

## PropertyService
Feature-specific Handler application boundary'dir.

## SearchService
Query Handler read-side application boundary'dir.

# 29. Anti-pattern'ler

Kaçınılacak:
- Fat Controller
- Controller -> Repository
- Controller -> KafkaTemplate
- Application Service içinde raw SQL
- Application Service içinde HTTP status code
- Domain entity'yi doğrudan response dönmek
- Generic BaseService
- her use-case için anlamsız interface
- transaction'ı birden fazla nested service'e dağıtmak
- side-effect sırasını belirsiz bırakmak
- external call'ı domain model içine koymak

# 30. Application Layer Review Checklist

- Use-case intent açık mı?
- Controller sadece boundary işi mi yapıyor?
- Transaction boundary doğru yerde mi?
- Domain behavior doğru modelde mi?
- Ownership check var mı?
- External technology port arkasında mı?
- Side-effect sırası güvenli mi?
- Retry/idempotency değerlendirildi mi?
- Concurrency riski var mı?
- Application Result domain entity'yi gereksiz expose ediyor mu?
- Handler/Application Service tek capability'e odaklı mı?
- Failure semantics açık mı?
