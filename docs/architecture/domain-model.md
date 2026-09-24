# Domain Model ve State Machine Tasarımı

## Aggregate sınırları

| Service | Aggregate / Model |
|---|---|
| AuthService | Account |
| UserProfileService | UserProfile |
| AgentService | Agent |
| BuyerService | BuyerPreferences, Offer |
| SellerService | Seller, ListingSubmission + Cassandra Projection'ları |
| PropertyService | Property |
| SearchService | PropertySearchDocument — Projection / Read Model, Aggregate değildir |

## Property Aggregate

### Temel field'lar
- propertyId
- sellerId
- agentId?
- title
- description
- propertyType
- address
- geoLocation
- price
- area
- roomCount
- features
- typeSpecificAttributes
- status
- activeOfferId?
- createdAt
- updatedAt
- publishedAt?
- reservedAt?
- soldAt?
- version

### Value Object'ler
- PropertyId
- SellerId
- AgentId
- Money
- Address
- GeoLocation
- Area

### PropertyStatus
- DRAFT
- PUBLISHED
- ON_HOLD
- RESERVED
- SOLD
- WITHDRAWN

### State Machine

```text
DRAFT --publish()--> PUBLISHED --hold()--> ON_HOLD --reserve()--> RESERVED --sell()--> SOLD
                         ^                   |
                         |----release()------|

DRAFT / PUBLISHED --withdraw()--> WITHDRAWN
```

### Behavior
- publish()
- updateDetails()
- changePrice()
- assignAgent()
- holdForOffer()
- releaseHold()
- reserve()
- withdraw()
- markSold()

## Offer Aggregate

### Field'lar
- offerId
- buyerId
- propertyId
- amount
- status
- idempotencyReference?
- createdAt
- updatedAt
- expiresAt?

### OfferStatus
- CREATED
- REQUESTED
- PROPERTY_HELD
- ACCEPTED
- REJECTED
- EXPIRED
- CANCELLED
- FAILED

### State Machine

```text
CREATED -> REQUESTED -> PROPERTY_HELD -> ACCEPTED
                    \-> REJECTED
                    \-> EXPIRED

CREATED / REQUESTED -> CANCELLED
```

### Behavior
- request()
- markPropertyHeld()
- accept()
- reject()
- expire()
- cancel()
- fail()

## BuyerPreferences Aggregate

State Machine kullanılmaz.

### Field'lar
- buyerId
- PriceRange
- preferredLocations
- propertyTypes
- RoomRange
- AreaRange
- preferredFeatures
- NotificationSettings
- SavedSearch listesi
- createdAt
- updatedAt

### Value Object'ler
- BuyerId
- PriceRange
- LocationPreference
- RoomRange
- AreaRange
- NotificationSettings
- SavedSearch

## Seller Aggregate

### Field'lar
- sellerId
- userId
- displayName
- status
- createdAt
- updatedAt

### SellerStatus
- ACTIVE
- SUSPENDED
- INACTIVE

## ListingSubmission

Property değildir; Seller'ın listing oluşturma talebidir.

### Field'lar
- submissionId
- sellerId
- PropertyDraftData
- status
- createdAt
- updatedAt

### ListingSubmissionStatus
- CREATED
- SUBMITTED
- PROPERTY_CREATED
- REJECTED
- FAILED

### State Machine

```text
CREATED -> SUBMITTED -> PROPERTY_CREATED
                   \-> REJECTED
                   \-> FAILED
```

`ACCEPTED` ara state'i kaldırılmıştır; PropertyService command'ı başarıyla işleyip Property oluşturduğunda `PropertyCreated` event'i SellerService projection'ını doğrudan `PROPERTY_CREATED` state'ine taşır.

## Agent Aggregate

### Field'lar
- agentId
- userId
- licenseNumber
- AgencyInfo
- AgentStatus
- AvailabilityStatus
- createdAt
- updatedAt
- version

### AgentStatus
- ACTIVE
- SUSPENDED
- INACTIVE

### AvailabilityStatus
- AVAILABLE
- BUSY
- OFFLINE

Agent lifecycle ile availability ayrı kavramlardır.

## Search Projection

`PropertySearchDocument` Aggregate değildir ve State Machine taşımaz. Property event'lerine tepki veren Projection Handler'lar tarafından güncellenir.
