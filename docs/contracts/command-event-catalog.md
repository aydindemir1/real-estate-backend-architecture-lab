# Command ve Event Catalog

## RabbitMQ Command Catalog

### CreateUserProfileCommand
Mevcut AuthService -> UserProfileService async flow.

Semantic:
"Bu user için profile oluştur."

### SubmitPropertyListingCommand
Producer: SellerService  
Consumer: PropertyService  
Broker: RabbitMQ

Alanlar:
- commandId
- submissionId
- sellerId
- propertyDraft
- createdAt
- correlationId

Idempotency key:
- commandId

Başarılı işleme sonrası PropertyService canonical Property oluşturur.

## Kafka Event Catalog

### PropertyCreated
Producer: PropertyService  
Topic: `property.events`  
Key: propertyId

Payload:
- propertyId
- sellerId
- status
- createdAt

Consumer candidate:
- SellerService

Amaç:
ListingSubmission projection'ını `PROPERTY_CREATED` durumuna taşımak.

### PropertyPublished
Producer: PropertyService  
Topic: `property.events`  
Key: propertyId

Payload:
- propertyId
- sellerId
- searchable property snapshot
- publishedAt

Consumer:
- SearchService

### PropertyUpdated
Producer: PropertyService  
Topic: `property.events`

Consumer:
- SearchService

### PropertyPriceChanged
Producer: PropertyService  
Topic: `property.events`

Payload:
- propertyId
- oldPrice
- newPrice
- currency
- changedAt

Consumer:
- SearchService

### PropertyHeld
Producer: PropertyService  
Topic: `property.events`

Payload:
- propertyId
- offerId
- sellerId
- heldAt

Consumers:
- SellerService
- BuyerService

### PropertyHoldReleased
Producer: PropertyService  
Topic: `property.events`

Payload:
- propertyId
- offerId
- reason
- releasedAt

Consumers:
- BuyerService
- SearchService gerektiğinde

### PropertyReserved
Producer: PropertyService  
Topic: `property.events`

Payload:
- propertyId
- offerId
- reservedAt

Consumers:
- BuyerService
- SearchService

### PropertyWithdrawn
Producer: PropertyService  
Topic: `property.events`

Consumer:
- SearchService

### PropertySold
Producer: PropertyService  
Topic: `property.events`

Consumer:
- SearchService

## Offer / Saga Event'leri

### OfferRequested
Producer: BuyerService  
Topic: `offer.events`  
Key: offerId

Payload:
- offerId
- buyerId
- propertyId
- amount
- currency
- requestedAt

Consumer:
- PropertyService

### SellerAccepted
Producer: SellerService  
Topic: `offer.events`  
Key: offerId

Payload:
- offerId
- sellerId
- propertyId
- acceptedAt

Consumer:
- PropertyService

### SellerRejected
Producer: SellerService  
Topic: `offer.events`

Consumer:
- PropertyService

### OfferAccepted
Producer: BuyerService veya application reaction sonrası derived domain event; implementation öncesi gerekliliği yeniden değerlendirilecek.

### OfferRejected
Benzer şekilde derived event olarak yalnızca gerçek consumer ihtiyacı varsa publish edilecek.

### OfferExpired
Producer: BuyerService / maintenance process

Consumer:
- PropertyService, eğer aktif hold release edilmesi gerekiyorsa.

## Event envelope standardı

Her Kafka event:
- eventId
- eventType
- aggregateId
- aggregateType
- occurredAt
- correlationId
- causationId
- schemaVersion
- payload

## Event tasarım prensipleri

- Event geçmişte olmuş bir gerçeği ifade eder.
- Command imperative intent ifade eder.
- Event payload consumer'ın başka service datastore'una erişmesini gerektirmeyecek kadar anlamlı olmalıdır.
- Event'e gereksiz bütün aggregate snapshot'ı konulmaz.
- Schema evolution için `schemaVersion` taşınır.
- Duplicate delivery normal kabul edilir; consumer Idempotent olmalıdır.
