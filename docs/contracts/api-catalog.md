# API Catalog

Bu doküman public/internal HTTP API boundary'lerini tanımlar. Path ve payload isimleri implementation öncesi contract baseline olarak kullanılır.

## AuthService

### POST /auth/register
Amaç: account registration.

Request:
- email
- username
- password

Response:
- accountId
- email
- username
- status

### POST /auth/login
Day 8 sonrasında primary authentication Keycloak'a taşınacağı için mevcut endpoint eğitim baseline olarak korunabilir.

## UserProfileService

### GET /users/{userId}
User profile getirir.

### PATCH /users/{userId}
Genel profile alanlarını günceller.

## AgentService

### POST /agents
Agent oluşturur.

### GET /agents/{agentId}
Agent getirir.

### PATCH /agents/{agentId}
Agent profile bilgilerini günceller.

### PATCH /agents/{agentId}/status
AgentStatus değiştirir.

### PATCH /agents/{agentId}/availability
AvailabilityStatus değiştirir.

Public REST dışında Agent availability için gRPC contract ayrıca tanımlanır.

## BuyerService

### PUT /buyers/{buyerId}/preferences
BuyerPreferences oluşturur/günceller.

### GET /buyers/{buyerId}/preferences
BuyerPreferences getirir.

### POST /buyers/{buyerId}/saved-searches
SavedSearch ekler.

### GET /buyers/{buyerId}/offers
Buyer'ın Offer kayıtlarını listeler.

### POST /buyers/{buyerId}/offers
Offer oluşturur.

Header:
- Idempotency-Key

Request:
- propertyId
- amount
- currency

Response:
- offerId
- status

### POST /buyers/{buyerId}/offers/{offerId}/cancel
Uygun state'teki Offer'ı cancel eder.

### POST /buyers/{buyerId}/viewing-requests
Agent availability kontrolünü tetikleyen viewing request başlatır.

## SellerService

### POST /sellers
Seller oluşturur.

### GET /sellers/{sellerId}
Seller getirir.

### POST /sellers/{sellerId}/listing-submissions
ListingSubmission oluşturur ve RabbitMQ command akışını başlatır.

Request:
- title
- description
- propertyType
- requestedPrice
- currency
- address
- grossArea
- netArea
- roomCount
- features

Response:
- submissionId
- status

### GET /sellers/{sellerId}/listing-submissions
Seller listing submission geçmişini getirir.

### GET /sellers/{sellerId}/offers/pending
Seller'ın pending offer projection'larını getirir.

### POST /sellers/{sellerId}/offers/{offerId}/accept
SellerAccepted event akışını başlatır.

### POST /sellers/{sellerId}/offers/{offerId}/reject
SellerRejected event akışını başlatır.

### GET /sellers/{sellerId}/activity
Seller activity timeline döndürür.

## PropertyService

### GET /properties/{propertyId}
Canonical Property getirir.

### PATCH /properties/{propertyId}
İzin verilen property detail alanlarını günceller.

### POST /properties/{propertyId}/publish
DRAFT -> PUBLISHED transition uygular.

### PATCH /properties/{propertyId}/price
Price değiştirir.

### POST /properties/{propertyId}/withdraw
DRAFT/PUBLISHED -> WITHDRAWN transition uygular.

### POST /properties/{propertyId}/assign-agent
Agent assignment uygular.

Hold/reserve endpoint'leri public API olmak zorunda değildir; Saga event handler'ları internal use-case olarak çalışır.

## SearchService

### GET /search/properties
Query parameter örnekleri:
- q
- city
- district
- propertyType
- minPrice
- maxPrice
- minArea
- maxArea
- roomCount
- features
- lat
- lon
- distance
- sort
- page
- size

### GET /search/autocomplete
Query:
- q

### GET /search/facets
Aggregation/facet data döndürür.

GraphQL read contract ayrıca tanımlanır.

## ApiGatewayService

Client-facing route boundary'dir. Business-specific endpoint üretmez; ilgili service endpoint'lerine route eder.

## Genel API kuralları

- Resource identity URL path üzerinden açık olmalı.
- Command-like state transition'larda explicit action endpoint kabul edilir.
- Validation error, authorization error ve business rule violation ayrıştırılmalıdır.
- Idempotency gereken write endpoint'lerinde `Idempotency-Key` kullanılır.
- Correlation ID propagate edilir.
