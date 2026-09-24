# Design Pattern Policy

Bu doküman projede kullanılacak Design Pattern'lerin seçim politikasını tanımlar.

Amaç pattern sayısını artırmak değil, gerçek variation point, dependency boundary, lifecycle veya integration problemlerini doğru pattern ile çözmektir.

## 1. Genel kural

Her pattern için şu sorular cevaplanmalıdır:

1. Hangi problem var?
2. Bu problem pattern olmadan neden zorlaşıyor?
3. Pattern hangi coupling'i azaltıyor veya hangi variation'ı yönetiyor?
4. Alternatif daha basit çözüm var mı?
5. Pattern test edilebilirliği artırıyor mu?
6. Pattern kodu gerçekten daha anlaşılır yapıyor mu?

Bu sorulara güçlü cevap yoksa pattern kullanılmaz.

---

# 2. Repository Pattern

## Kullanım

### AgentService
`AgentRepository`

### SellerService
`SellerRepository`, `ListingSubmissionRepository`

### PropertyService
`PropertyRepository`

### BuyerService
Hexagonal Architecture tarafında:
`SaveOfferPort`, `LoadOfferPort` gibi port'lar Repository Pattern semantiğini taşır.

## Amaç
Domain/application katmanını persistence technology'den ayırmak.

## Kaçınılacak
Spring Data repository'nin üstüne yalnızca isim değiştiren gereksiz generic repository wrapper oluşturmak.

---

# 3. Adapter Pattern

## Kullanım

Hexagonal Architecture'ın temel pattern'lerinden biri.

### BuyerService
- Couchbase persistence adapter
- gRPC Agent availability adapter
- Kafka publisher adapter
- Redis Idempotency adapter

### AgentService
- JPA repository adapter

### SellerService
- Cassandra adapter
- RabbitMQ publisher adapter
- Kafka publisher/consumer adapter

## Amaç
External technology contract'ını application port'a çevirmek.

---

# 4. Strategy Pattern

## Primary candidate
Property publication / validation policy.

Örnek variation point:

```text
APARTMENT
HOUSE
LAND
COMMERCIAL
```

Her PropertyType farklı publication rule taşıyorsa:

```text
PropertyPublicationPolicy
├── ApartmentPublicationPolicy
├── HousePublicationPolicy
├── LandPublicationPolicy
└── CommercialPublicationPolicy
```

`PropertyPublicationPolicyResolver` uygun Strategy'yi seçebilir.

## İkinci candidate
Search sorting strategy, yalnızca query complexity bunu gerçekten gerektirirse.

## Kural
Tek bir if/else için Strategy hierarchy oluşturulmaz. Variation büyüdüğünde uygulanır.

---

# 5. Factory / Factory Method

## Kullanım

Complex Aggregate construction gerektiğinde.

### Property
`PropertyFactory.createFromListingSubmission(...)`

### Offer
`Offer.create(...)`

### Agent
Gerekirse `Agent.create(...)`

## Amaç
Valid Aggregate creation rule'larını tek noktada tutmak.

## Tercih
Basitse static factory method yeterlidir.

Örnek:

```java
Offer.create(...)
```

Ayrı Factory class yalnızca construction logic gerçekten karmaşıksa kullanılır.

---

# 6. Builder Pattern

## Kullanım

Production domain object'lerinde default tercih değildir.

Gerçekten çok sayıda optional field bulunan complex immutable Value Object veya test fixture construction'da değerlendirilebilir.

Özellikle test tarafında:

```text
PropertyTestBuilder
OfferTestBuilder
```

kullanımı mantıklı olabilir.

## Kaçınılacak
Lombok `@Builder` ile Aggregate invariant'larını bypass etmek.

---

# 7. State Pattern / State Behavior

Property ve Offer gerçek State Machine taşır.

Ancak ilk tercih ayrı State class hierarchy değildir.

Tercih:

```java
property.publish();
property.holdForOffer(...);
property.reserve(...);
```

ve Aggregate içinde controlled transition.

## Ne zaman GoF State Pattern?
State-specific behavior çok büyür ve Aggregate devasa switch/if bloklarına dönüşürse.

Yani:

```text
State Machine var
!=
zorunlu olarak GoF State Pattern kullan
```

---

# 8. Specification Pattern

## Candidate
Search/filter tarafında business-oriented query criteria.

### SearchService
`PropertySearchCriteria`

Ancak Elasticsearch query DSL zaten güçlü olduğu için klasik JPA Specification Pattern birebir kopyalanmayacaktır.

## Kullanım
Composable business filter ihtiyacı gerçekten oluşursa.

Örnek:

```text
PriceRangeCriteria
LocationCriteria
PropertyTypeCriteria
FeatureCriteria
```

bunlar tek search criteria modelinde birleşebilir.

## Kural
Technology query DSL'i gereksiz abstraction arkasına tamamen gizlenmez.

---

# 9. Observer / Domain Event Pattern

## Kullanım
Domain Event üretimi.

### Property
- PropertyPublished
- PropertyPriceChanged
- PropertyReserved

### Offer
- OfferRequested

Ancak in-process Observer ile Kafka aynı şey değildir.

Domain Event:
business fact.

Kafka:
event transport/integration mechanism.

Bu separation korunmalıdır.

---

# 10. Outbox Pattern

## Kullanım
Database state change ile integration event publication arasında consistency gerektiğinde.

Primary candidate:
PropertyService.

Örnek:

```text
Property status update
+
OutboxEvent insert
```

aynı local transaction scope'ta.

MongoDB transaction/outbox collection yaklaşımı değerlendirilecektir.

## Kural
SellerService Cassandra'ya classic relational Transactional Outbox zorla uygulanmaz.

---

# 11. Inbox Pattern

## Kullanım
Duplicate message/event processing'i kalıcı olarak engellemek gerektiğinde.

Candidate:
- PropertyService RabbitMQ command consumer
- SearchService Kafka projection consumer
- Saga consumers

`ProcessedMessage` / `ProcessedEvent` persistence ile uygulanabilir.

---

# 12. Idempotent Consumer Pattern

RabbitMQ ve Kafka consumer'larında zorunlu consideration.

Örnek:
`SubmitPropertyListingCommand` iki kez gelirse iki Property oluşturulmamalı.

Key:
- commandId
- eventId

---

# 13. Saga Pattern

## Kullanım
Offer & Reservation workflow.

Style:
Saga Choreography.

İlk aşamada central Orchestrator yok.

## Katılımcılar
- BuyerService
- PropertyService
- SellerService

## Compensation
`PropertyHoldReleased`

---

# 14. CQRS Pattern

## Sistem seviyesi kullanım

Write Side:
`PropertyService / MongoDB`

Query Side:
`SearchService / Elasticsearch`

Bu projede CQRS her service içinde command/query class ayırmak anlamına gelmez.

Command/Query class naming tek başına CQRS sayılmaz.

---

# 15. Materialized View / Projection Pattern

## Kullanım

### SearchService
Elasticsearch PropertySearchDocument.

### SellerService
Cassandra seller-facing offer/history projection'ları.

Projection canonical ownership değildir.

---

# 16. Facade Pattern

## Candidate
Complex external integration facade gerektiğinde.

Örnek:
BuyerService'te Agent availability + additional viewing rules zamanla büyürse `ViewingFacade` düşünülebilir.

## Şimdilik
Zorunlu değil.

---

# 17. Decorator Pattern

## Candidate
Cross-cutting port behavior:

- metrics
- tracing
- caching

Ancak Spring AOP/Observation infrastructure zaten uygun çözüm sunuyorsa elle Decorator hierarchy oluşturulmaz.

---

# 18. Chain of Responsibility

## Kullanım candidate
Property publication validation pipeline.

Örnek:
- RequiredFieldsValidator
- PriceValidator
- LocationValidator
- TypeSpecificValidator

Fakat küçük rule set'te basit domain validation daha iyidir.

Chain ancak validation step sayısı ve extensibility ihtiyacı arttığında uygulanır.

---

# 19. Template Method

## Policy
Default olarak kullanılmayacak.

Inheritance tabanlı workflow sharing yerine composition tercih edilir.

Yalnızca çok güçlü invariant workflow ve küçük extension point'ler varsa değerlendirilebilir.

---

# 20. Proxy Pattern

Çoğunlukla framework tarafından sağlanır:
- Spring AOP
- transaction proxy
- security proxy
- Feign client proxy

Elle implementation gereksizdir.

---

# 21. Singleton Pattern

Application-level manuel Singleton yazılmayacaktır.

Spring Bean lifecycle zaten singleton scope sağlayabilir.

Global mutable singleton state yasaktır.

---

# 22. Command Pattern

Application command object'leri:

- CreateOfferCommand
- PublishPropertyCommand
- SubmitListingCommand

semantic olarak Command Pattern'e yakındır.

Ancak generic command bus framework Day 7-20 scope'unda zorunlu değildir.

---

# 23. Query Object Pattern

SearchService için çok doğal.

Örnek:
`SearchPropertiesQuery`

Query parameter'larını tek object içinde taşır.

Ayrıca application layer method signature'larını sadeleştirir.

---

# 24. Mapper Pattern

Boundary olduğunda kullanılır:
- REST DTO -> Command
- Domain -> Persistence
- Domain Event -> Kafka Event
- Domain -> Elasticsearch Projection

MapStruct yalnızca değer kattığı yerde kullanılır.

Simple 2-field conversion için aşırı mapper class üretilmez.

---

# 25. Anti-pattern olarak kaçınılacak pattern kullanımları

- Her class için interface
- Her entity için Factory
- Her DTO için Builder
- Her if için Strategy
- Her flow için Chain of Responsibility
- Generic AbstractBaseService
- Generic BaseRepository
- Generic Mapper framework katmanı
- Pattern isimleriyle aşırı package fragmentation
- sırf CV için GoF pattern sayısı artırma

---

# 26. Service bazında pattern matrisi

| Service | Primary pattern'ler |
|---|---|
| AuthService | Repository, Mapper, existing Layered patterns |
| UserProfileService | Repository, Mapper |
| AgentService | Repository, Adapter, Factory/Static Factory, Strategy candidate |
| BuyerService | Ports & Adapters, Repository semantic ports, Adapter, Command, Idempotency |
| SellerService | Repository, Adapter, Projection, Command Publisher, Saga participant |
| PropertyService | Vertical Slice, Aggregate behavior, Factory, Strategy candidate, Outbox, Inbox, Saga participant |
| SearchService | CQRS Query Side, Projection, Query Object, Specification-like criteria, Inbox |
| ApiGatewayService | Filter Chain, Proxy/framework patterns, Rate Limiter |

---

# 27. Pattern review checklist

Yeni pattern eklenmeden önce:

- Problem gerçek mi?
- Simpler alternative var mı?
- Variation point bugün var mı?
- Coupling azalıyor mu?
- Testability artıyor mu?
- Kod okunabilirliği iyileşiyor mu?
- Pattern infrastructure ile domain'i karıştırıyor mu?
- Pattern service'in Architecture stiline uyuyor mu?
- Pattern yalnızca teknoloji göstermek için mi ekleniyor?

Son sorunun cevabı "evet" ise pattern kullanılmaz.
