# DTO ve Message Contract Tasarımı

## Genel prensipler

- Domain model doğrudan dış contract olarak expose edilmez.
- REST Request/Response DTO, gRPC message, RabbitMQ Command ve Kafka Event payload birbirinden ayrıdır.
- Service'ler shared domain model kullanmaz.
- Contract field'ları consumer ihtiyacına göre tasarlanır.
- Internal persistence model dış contract değildir.

# REST DTO'ları

## AgentService

### CreateAgentRequest
- userId
- licenseNumber
- agencyName
- agencyRegistrationNumber?
- officePhone?

### AgentResponse
- agentId
- userId
- licenseNumber
- agencyName
- status
- availabilityStatus
- createdAt
- updatedAt

### ChangeAgentStatusRequest
- status

### ChangeAvailabilityRequest
- availabilityStatus

## BuyerService

### Day 9 — Implemented / Verified

#### UpdateBuyerPreferencesRequest
- minPrice
- maxPrice
- currency
- preferredLocations[]
  - city
  - district?
- propertyTypes[]
- minRooms?
- maxRooms?
- minArea?
- maxArea?
- preferredFeatures[]
- notificationSettings
  - emailEnabled
  - pushEnabled
  - smsEnabled

#### AddSavedSearchRequest
- name
- minPrice?
- maxPrice?
- currency?
- locations[]
  - city
  - district?
- propertyTypes[]
- minRooms?
- maxRooms?
- minArea?
- maxArea?
- preferredFeatures[]

#### BuyerPreferencesResponse
- buyerId
- priceRange
  - min
  - max
  - currency
- preferredLocations[]
- propertyTypes[]
- roomRange
  - min
  - max
- areaRange
  - min
  - max
- preferredFeatures[]
- notificationSettings
- savedSearches[]
  - id
  - name
  - priceRange
  - locations[]
  - propertyTypes[]
  - roomRange
  - areaRange
  - preferredFeatures[]
  - createdAt
- createdAt
- updatedAt

### Sonraki Day'ler için planlanan DTO'lar

Offer ve Viewing DTO'ları Day 9 implementation'ı değildir. İlgili capability implemente edildiği Day'de contract'ları kesinleştirilecektir.

## SellerService

### CreateSellerRequest
- userId
- displayName

### SubmitListingRequest
- title
- description
- propertyType
- requestedPrice
- currency
- address
- latitude?
- longitude?
- grossArea
- netArea?
- roomCount?
- features[]
- typeSpecificAttributes

### ListingSubmissionResponse
- submissionId
- sellerId
- status
- createdAt
- updatedAt

### PendingOfferResponse
- offerId
- propertyId
- buyerId
- amount
- currency
- createdAt

## PropertyService

### PropertyResponse
- propertyId
- sellerId
- agentId?
- title
- description
- propertyType
- address
- geoLocation?
- price
- area
- roomCount?
- features[]
- status
- createdAt
- updatedAt
- publishedAt?
- reservedAt?
- soldAt?

### UpdatePropertyRequest
- title?
- description?
- address?
- geoLocation?
- area?
- roomCount?
- features?
- typeSpecificAttributes?

### ChangePriceRequest
- amount
- currency

### AssignAgentRequest
- agentId

## SearchService

### SearchPropertiesResponse
- items[]
- page
- size
- totalElements
- facets?

### PropertySearchItem
- propertyId
- title
- propertyType
- city
- district
- price
- currency
- grossArea
- roomCount
- features
- status
- publishedAt

# RabbitMQ Command

## SubmitPropertyListingCommand
- commandId
- submissionId
- sellerId
- propertyDraft
- createdAt
- correlationId

### PropertyDraft
- title
- description
- propertyType
- requestedPrice
- currency
- address
- geoLocation?
- grossArea
- netArea?
- roomCount?
- features
- typeSpecificAttributes

# Kafka Event Payload prensibi

Event envelope ayrı tutulur. Payload yalnızca event'e özgü alanları taşır.

Örnek:

### PropertyPublishedPayload
- propertyId
- sellerId
- title
- description
- propertyType
- city
- district
- geoLocation?
- price
- area
- roomCount?
- features
- status
- publishedAt

### OfferRequestedPayload
- offerId
- buyerId
- propertyId
- amount
- currency
- requestedAt

### SellerAcceptedPayload
- offerId
- sellerId
- propertyId
- acceptedAt

## Kural

Bir DTO'ya gelecekte lazım olabilir diye gereksiz field eklenmez. Contract evolution kontrollü yapılır.
