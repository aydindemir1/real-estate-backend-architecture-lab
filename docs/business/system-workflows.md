# Sistem İş Analizi ve Business Workflow'lar

Bu doküman sistemin business scope, actor, bounded responsibility ve temel workflow kararlarını kalıcılaştırır.

## Actor'lar

- Guest
- Buyer
- Seller
- Agent
- Admin
- System / Service Account

## Temel Business Workflow'lar

1. Account Registration & Authentication
2. Role/Profile Onboarding
3. Property Listing Submission
4. Property Publication
5. Property Search
6. Viewing / Agent Interaction
7. Offer & Reservation Saga
8. Search Projection / Reindex

## Service responsibility özeti

### AuthService
Account registration, authentication baseline ve credential lifecycle.

### UserProfileService
Genel user profile data.

### AgentService
Agent professional profile, license, status ve availability.

### BuyerService
BuyerPreferences, SavedSearch, Viewing Request ve Offer lifecycle'ın buyer-side ownership'i.

### SellerService
Seller profile, listing submission history, seller activity ve seller-facing offer projection/decision.

### PropertyService
Canonical Property lifecycle, publication, hold, reservation ve sold/withdrawn state.

### SearchService
Elasticsearch tabanlı search projection ve query model. Canonical Property owner değildir.

### ApiGatewayService
Routing, authentication boundary, Rate Limiting, correlation ve resilience. Business rule içermez.

## Property Listing Submission

SellerService listing submission oluşturur ve RabbitMQ üzerinden `SubmitPropertyListingCommand` gönderir.

```text
Seller
  |
SellerService
  |
  | SubmitPropertyListingCommand
  v
RabbitMQ
  |
PropertyService
  |
MongoDB
```

PropertyService command'ı işler, canonical Property oluşturur ve sonrasında Kafka üzerinden `PropertyCreated` gibi domain event yayınlayabilir.

RabbitMQ burada Command Messaging için, Kafka ise Domain Event Streaming için kullanılır.

## Property Publication

Property oluşturulması ile publication ayrıdır.

```text
DRAFT -> PUBLISHED
```

Publication için en az:
- valid sellerId
- title
- description
- propertyType
- location
- positive price
- gerekli property type alanları

bulunmalıdır.

Publish sonrasında `PropertyPublished` Kafka event'i üretilir.

## Search Projection

```text
PropertyService / MongoDB
        |
        | PropertyPublished / Updated / PriceChanged / Withdrawn / Sold
        v
       Kafka
        |
        v
SearchService / Elasticsearch
```

MongoDB source of truth, Elasticsearch derived Projection'dır. Search Eventual Consistency ile güncellenir.

## Viewing / Agent Interaction

BuyerService, AgentService ile gRPC üzerinden synchronous availability kontrolü yapar.

```text
BuyerService
   |
  gRPC
   |
AgentService
```

İlk scope'ta full calendar engine yerine `AVAILABLE / UNAVAILABLE` seviyesinde başlanır.

## Offer & Reservation Saga

Offer lifecycle'ın buyer-facing ownership'i BuyerService'tedir.

```text
BuyerService
  |
  | OfferRequested
  v
Kafka
  |
PropertyService
  |
  | PropertyHeld
  v
Kafka
  |
SellerService
  |
  | SellerAccepted / SellerRejected
  v
Kafka
  |
PropertyService
  |
  | PropertyReserved / PropertyHoldReleased
  v
Kafka
  |
BuyerService
```

Saga Choreography kullanılır. Merkezi Saga Orchestrator ilk aşamada kullanılmaz.

## Temel Business Invariant'lar

### Property
- Seller kendisine ait olmayan Property'yi değiştiremez.
- SOLD tekrar PUBLISHED olamaz.
- WITHDRAWN aktif search'te görünmez.
- Aynı Property aynı anda iki active reservation taşıyamaz.
- Price > 0 olmalıdır.

### Offer
- Amount > 0 olmalıdır.
- Buyer kendi Property'sine Offer veremez.
- SOLD Property'ye Offer verilemez.
- Terminal Offer tekrar accept/reject edilemez.
- Aynı Idempotency Key iki Offer oluşturmamalıdır.

### Agent
- SUSPENDED / INACTIVE Agent availability için kullanılamaz.
- LicenseNumber unique olmalıdır.

### Seller
- Yalnızca ACTIVE Seller listing submit edebilir.
- Seller yalnızca kendi pending Offer'ına karar verebilir.

## Consistency modeli

Strong consistency service'in kendi datastore boundary'si içindeki kritik state transition'larda hedeflenir.

Eventual Consistency:
- Property -> Search Projection
- Saga boyunca Buyer / Seller / Property local state'leri
- Seller-facing offer projection
