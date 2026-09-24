# API Catalog

Bu doküman public/internal HTTP API boundary'lerini tanımlar.

## Versioning boundary

Client-facing public API, Gateway üzerinden `/api/v1` prefix'i ile expose edilecektir.

Bu catalog içindeki service-local path'ler okunabilirliği korumak için prefix'siz gösterilebilir. Örneğin:

`GET /properties/{propertyId}`

public edge'de:

`GET /api/v1/properties/{propertyId}`

olarak route edilebilir.

## AuthService
### POST /auth/register
### POST /auth/login

Day 8 sonrasında primary authentication Keycloak'a taşınacağı için mevcut login/register flow eğitim baseline olarak yeniden değerlendirilecektir.

## UserProfileService
### GET /users/{userId}
### PATCH /users/{userId}

## AgentService
### POST /agents
### GET /agents/{agentId}
### PATCH /agents/{agentId}
### PATCH /agents/{agentId}/status
### PATCH /agents/{agentId}/availability

Public REST dışında Agent availability için gRPC contract ayrıca tanımlanır.

## BuyerService
### PUT /buyers/{buyerId}/preferences
### GET /buyers/{buyerId}/preferences
### POST /buyers/{buyerId}/saved-searches
### GET /buyers/{buyerId}/offers
### POST /buyers/{buyerId}/offers

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
### POST /buyers/{buyerId}/viewing-requests

## SellerService
### POST /sellers
### GET /sellers/{sellerId}
### POST /sellers/{sellerId}/listing-submissions

ListingSubmission oluşturur. Target architecture'da asynchronous Property creation RabbitMQ command flow ile devam eder; reliable publication strategy Day 11 öncesi finalize edilir.

### GET /sellers/{sellerId}/listing-submissions
### GET /sellers/{sellerId}/offers/pending
### POST /sellers/{sellerId}/offers/{offerId}/accept
### POST /sellers/{sellerId}/offers/{offerId}/reject
### GET /sellers/{sellerId}/activity

## PropertyService
### GET /properties/{propertyId}
### PATCH /properties/{propertyId}
### POST /properties/{propertyId}/publish
### PATCH /properties/{propertyId}/price
### POST /properties/{propertyId}/withdraw
### POST /properties/{propertyId}/assign-agent

Hold/reserve public API değildir; Saga event handler'ları internal use-case olarak çalışır.

## SearchService
### GET /search/properties
### GET /search/autocomplete
### GET /search/facets

GraphQL read contract ayrıca tanımlanır.

## ApiGatewayService

Client-facing route boundary'dir. Business-specific endpoint üretmez.

## Genel API kuralları

- Resource identity URL path üzerinden açık olmalı.
- Command-like state transition'larda explicit action endpoint kabul edilir.
- Validation, authorization ve business conflict ayrıştırılır.
- Idempotency gereken write endpoint'lerinde `Idempotency-Key` kullanılır.
- Correlation ID propagate edilir.
