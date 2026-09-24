# Domain Modeling Standard

Bu doküman projede domain model tasarımı için ortak standardı tanımlar.

Amaç:
- business rule'ları doğru yerde tutmak,
- model ile persistence/integration detaylarını ayırmak,
- Aggregate boundary'lerini küçük ve tutarlı tutmak,
- gereksiz DDD karmaşıklığından kaçınmak,
- state transition ve invariant'ları test edilebilir hale getirmek.

# 1. Uygulama seviyesi

Bu proje tüm service'leri tam DDD implementation'ına zorlamaz.

DDD tactical pattern'leri yalnızca business complexity değer kattığı yerde kullanılır:
- Aggregate
- Entity
- Value Object
- Domain Service
- Domain Event
- Factory

AuthService ve UserProfileService N-Layer baseline olarak daha basit kalabilir.

# 2. Aggregate

Aggregate bir consistency boundary'dir.

Bir Aggregate:
- kendi invariant'larını korur,
- dışarıya Aggregate Root üzerinden erişilir,
- tek local transaction içinde tutarlı kalması gereken state'i kapsar.

Aggregate yalnızca "birkaç entity'yi aynı class altında toplamak" değildir.

# 3. Aggregate Root

Dış dünya Aggregate içindeki state'i doğrudan değiştirmez.

Kötü:

```java
property.setStatus(PropertyStatus.PUBLISHED);
```

Tercih:

```java
property.publish();
```

Aggregate Root business transition'ı doğrular.

# 4. Aggregate boundary seçimi

Boundary şu sorularla belirlenir:
- hangi invariant aynı transaction içinde korunmalı?
- hangi state birlikte değişmeli?
- hangi entity bağımsız lifecycle taşıyor?
- hangi veri başka Aggregate'tan yalnızca ID ile referanslanabilir?

Küçük Aggregate tercih edilir.

# 5. Cross-Aggregate reference

Farklı Aggregate'lar object graph ile birbirine gömülmez.

Tercih:

```text
Offer
- buyerId
- propertyId
```

Kaçınılacak:

```text
Offer
- Buyer buyer
- Property property
```

Microservice boundary varsa yalnızca external identity/reference tutulur.

# 6. Entity

Entity:
- stable identity taşır,
- state zaman içinde değişebilir,
- equality identity üzerinden düşünülür.

Örnek:
- Property
- Offer
- Agent
- Seller

Entity equality implementation'ı persistence proxy gibi framework detaylarından bağımsız düşünülmelidir.

# 7. Value Object

Value Object:
- identity taşımaz,
- value equality kullanır,
- mümkün olduğunca immutable'dır,
- kendi validation/invariant'ını korur.

Örnek:
- Money
- Email
- LicenseNumber
- Address
- GeoLocation
- PriceRange
- AreaRange

# 8. Value Object oluşturma

Invalid Value Object create edilmemelidir.

Örnek:

```java
Money.of(amount, currency)
```

create sırasında:
- amount null değil,
- amount >= 0 veya business rule'a göre > 0,
- currency valid

kontrol edilir.

# 9. Primitive Obsession

Business anlamı güçlü primitive'ler Value Object ile temsil edilir.

Ancak her String/UUID için wrapper oluşturmak zorunlu değildir.

Value Object şu durumlarda değerlidir:
- validation taşıyorsa
- behavior taşıyorsa
- yanlış primitive kullanımını engelliyorsa
- domain language'i güçlendiriyorsa

# 10. Invariant

Invariant, Aggregate her observable state'te doğru tutmak zorunda olduğu business rule'dur.

Property örnekleri:
- price > 0
- SOLD tekrar PUBLISHED olamaz
- aynı anda tek active offer/hold olabilir

Offer:
- amount > 0
- terminal state tekrar transition yapamaz

Agent:
- SUSPENDED Agent AVAILABLE olamaz

Invariant controller veya repository'de değil domain model'de korunur.

# 11. State Transition

State transition explicit behavior method ile yapılır.

```java
property.publish();
property.holdForOffer(offerId);
property.releaseHold(reason);
property.reserve(offerId);
```

Generic:
```java
changeStatus(status)
```

yalnızca gerçekten business semantic'i buysa kullanılır. Aksi halde transition intent kaybolur.

# 12. Invalid State Transition

Illegal transition:
- fail fast
- semantic domain exception

üretmelidir.

Örnek:
- InvalidPropertyStateException
- InvalidOfferStateException

# 13. Domain Service

Bir business rule:
- tek Aggregate'a doğal olarak ait değilse
- birden fazla domain object arasındaki calculation/policy ise

Domain Service düşünülebilir.

Örnek candidate:
`OfferEligibilityDomainService`

Ancak orchestration, repository call veya external service call yapan class Domain Service değildir; Application Service'tir.

# 14. Application Service ile Domain Service ayrımı

Domain Service:
- pure business rule
- infrastructure bilmez
- transaction management yapmaz

Application Service:
- use-case orchestration
- repository/port çağrısı
- transaction boundary
- external interaction koordinasyonu

# 15. Domain Event

Domain Event geçmişte olmuş business fact'tir.

Örnek:
- PropertyPublished
- PropertyPriceChanged
- OfferRequested

Domain Event:
```text
business fact
```

Kafka Event:
```text
integration representation / transport
```

Aynı class olmak zorunda değildir.

# 16. Domain Event oluşturma

Event Aggregate behavior sonucunda oluşabilir.

Örnek:

```java
property.publish();
```

sonucunda:

```text
PropertyPublishedDomainEvent
```

oluşabilir.

Integration layer bunu Kafka payload'a map eder.

# 17. Event payload

Domain Event'e:
- entire Aggregate snapshot
- persistence entity
- framework object

konmaz.

Event yalnızca business fact için gerekli data'yı taşır.

# 18. Factory

Complex valid Aggregate creation gerekiyorsa:
- static factory
- Factory

kullanılır.

Tercih sırası:
1. constructor/factory method
2. static factory
3. ayrı Factory class

Ayrı Factory yalnızca construction complexity gerçekse.

# 19. Constructor policy

Constructor invalid object oluşturamamalı.

Mümkünse:
- required field constructor/static factory
- optional field controlled method

kullanılır.

Public no-args constructor yalnızca framework zorunluluğu varsa persistence model'de bulunabilir.

# 20. Mutability

Value Object immutable.

Aggregate controlled mutable olabilir.

Mutable collection dışarı expose edilmez.

# 21. Time

Domain behavior time'a bağlıysa:
- time parameter verilebilir
- veya Clock abstraction application layer'dan sağlanabilir

Doğrudan her yerde `Instant.now()` çağrısı testability'yi azaltabilir.

# 22. Identity generation

ID:
- application layer
- factory
- persistence adapter

tarafından üretilebilir.

Seçim açık olmalıdır.

Domain'in infrastructure-specific ID generator'a bağımlı olması engellenir.

# 23. Persistence ignorance

Clean/Hexagonal/Onion service'lerde domain model:
- JPA annotation
- Spring Data annotation
- Elasticsearch annotation
- Cassandra table annotation

taşımaz.

Persistence model ayrı olur.

N-Layer baseline service'lerde bu kural daha gevşek olabilir.

# 24. Domain model != DTO

Request/Response DTO:
transport contract.

Domain model:
business behavior.

Birbirinin yerine kullanılmaz.

# 25. Domain model != Persistence model

Özellikle:
- AgentService
- SellerService
- BuyerService adapter'ları
- Search projection

için ayrı model kullanılabilir.

# 26. Search model

`PropertySearchDocument` Domain Aggregate değildir.

Read Model / Projection'dır.

Business invariant taşımaz.

# 27. Cassandra model

Cassandra table class'ları domain entity değildir.

Query model / persistence projection'dır.

`PendingOfferBySellerTable` gibi class'lar domain Aggregate sayılmaz.

# 28. Shared Kernel

Service'ler arasında shared domain model library oluşturulmayacaktır.

Örneğin `Money` farklı service'lerde benzer olsa bile:
- service autonomy
- independent evolution

için local representation tercih edilir.

Gerçek shared contract gerekiyorsa schema/contract paylaşılır, domain implementation değil.

# 29. Bounded responsibility

Her service kendi business language ve ownership sınırına sahiptir.

Örnek:
- PropertyService canonical Property
- SearchService PropertySearchDocument
- SellerService seller-facing offer projection

aynı concept'in farklı bounded representation'ları olabilir.

# 30. Domain naming

Class/method isimleri ubiquitous language'e yakın olmalıdır.

Tercih:
- publishProperty
- submitListing
- reserveProperty
- acceptOffer

Kaçınılacak:
- processData
- executeAction
- updateThing

# 31. Business rule location

Rule şu sırayla değerlendirilir:
1. Value Object
2. Aggregate
3. Domain Service
4. Application Service
5. Infrastructure

Business rule infrastructure'a itilmez.

# 32. Query logic

Complex read query logic domain Aggregate içine konmaz.

Search/read optimization:
- Query Handler
- Read Model
- Repository query

tarafında olabilir.

# 33. Aggregate loading

Bir use-case için gereken Aggregate yüklenir.

Tüm object graph eager yüklenmez.

Cross-service data join yapılmaz.

# 34. Transaction scope

Bir local transaction ideal olarak tek Aggregate değişimini kapsar.

Birden çok service/Aggregate consistency gerekiyorsa:
- Saga
- Eventual Consistency

kullanılır.

# 35. Domain exception

Domain exception:
- business semantic taşır
- HTTP status bilmez

Örnek:
`InvalidPropertyStateException`

HTTP 409 mapping'i presentation/error handler katmanında yapılır.

# 36. Validation boundary

Syntactic validation DTO/boundary'de.

Business invariant domain'de.

Duplicate validation mümkün olduğunca önlenir; güvenlik amacıyla kritik invariant domain'de tekrar korunabilir.

# 37. Domain model test standardı

Domain test:
- Spring context açmamalı
- database kullanmamalı
- network kullanmamalı

Test:
- invariant
- valid transition
- invalid transition
- Value Object equality
- edge cases

üzerine odaklanır.

# 38. Model anti-pattern'leri

Kaçınılacak:
- getter/setter dolu Anemic Domain Model, behavior gerekmesine rağmen
- devasa Aggregate
- cross-service object graph
- Entity = API DTO
- Entity = Kafka payload
- persistence annotation'ı domain'e sızdırma
- generic BaseEntity üzerinden gereksiz inheritance
- her primitive için wrapper
- her operation için Domain Service
- Domain Event adı altında technical event üretme

# 39. Service bazında Domain Modeling yaklaşımı

## AuthService
N-Layer baseline; Account modeli daha basit olabilir.

## UserProfileService
N-Layer baseline; davranış sınırlı.

## AgentService
Rich Agent Aggregate + Value Object + persistence ignorance.

## BuyerService
BuyerPreferences ve Offer ayrı Aggregate.

## SellerService
Seller/ListingSubmission domain model; Cassandra table'ları ayrı persistence projection.

## PropertyService
En zengin Aggregate; state transition ve invariant'lar burada merkezde.

## SearchService
Read Model/Projection; rich domain model beklenmez.

# 40. Domain Review Checklist

- Aggregate boundary neden burada?
- Invariant nerede korunuyor?
- Cross-Aggregate reference ID ile mi?
- Entity ve Value Object ayrımı doğru mu?
- Value Object immutable mı?
- Invalid object oluşturulabiliyor mu?
- State transition explicit mi?
- Domain infrastructure biliyor mu?
- Domain Event business fact mi?
- DTO/persistence model domain'e karışmış mı?
- Aggregate gereğinden büyük mü?
- Bu rule gerçekten Domain Service mi?
- Test framework'süz yazılabiliyor mu?
