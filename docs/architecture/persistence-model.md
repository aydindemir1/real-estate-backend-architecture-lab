# Physical Persistence Model

## AuthService — PostgreSQL

`accounts`
- id PK
- email UNIQUE
- username UNIQUE
- password_hash
- status
- created_at
- updated_at
- version

## UserProfileService — PostgreSQL

`user_profiles`
- id PK
- auth_account_id UNIQUE
- display_name
- first_name
- last_name
- phone_number
- avatar_url
- created_at
- updated_at

## AgentService — MySQL

`agents`
- id PK
- user_id UNIQUE
- license_number UNIQUE
- agency_name
- agency_reg_number
- office_phone
- status
- availability_status
- created_at
- updated_at
- version

Index candidate'ları:
- status
- availability_status
- agency_name

Clean Architecture gereği domain model JPA annotation taşımaz. JPA Entity infrastructure altında ayrıdır.

## BuyerService — Couchbase

### BuyerPreferences document key
`buyer-preferences::{buyerId}`

Document:
- type
- buyerId
- budget
- preferredLocations
- propertyTypes
- roomRange
- areaRange
- preferredFeatures
- notificationSettings
- savedSearches
- createdAt
- updatedAt

### Offer document key
`offer::{offerId}`

Document:
- type
- offerId
- buyerId
- propertyId
- amount
- currency
- status
- createdAt
- updatedAt
- expiresAt

Secondary index candidate'ları:
- buyerId + createdAt
- propertyId
- status

## SellerService — Cassandra

Cassandra query-first tasarlanır.

### seller_by_id
Partition Key: `seller_id`

### listing_submissions_by_seller_and_month
Partition Key: `(seller_id, year_month)`
Clustering: `created_at DESC, submission_id`

### pending_offers_by_seller
Partition Key: `seller_id`
Clustering: `created_at DESC, offer_id`

### offers_by_seller_and_month
Partition Key: `(seller_id, year_month)`
Clustering: `created_at DESC, offer_id`

### seller_activity_by_seller_and_month
Partition Key: `(seller_id, year_month)`
Clustering: `occurred_at DESC, activity_id`

JPA-style relation kullanılmaz. Denormalization bilinçli olabilir.

## PropertyService — MongoDB

Collection: `properties`

Document:
- _id
- sellerId
- agentId
- title
- description
- propertyType
- address
- geoLocation
- price
- area
- roomCount
- features
- attributes
- status
- activeOfferId
- createdAt
- updatedAt
- publishedAt
- reservedAt
- soldAt
- version

Index candidate'ları:
- sellerId + createdAt
- status
- agentId
- gerektiğinde geoLocation 2dsphere

Optimistic Concurrency için `@Version` değerlendirilecektir.

## SearchService — Elasticsearch

`PropertySearchDocument` alanları:
- propertyId
- title
- description
- propertyType
- country
- city
- district
- neighborhood
- location
- price
- currency
- grossArea
- netArea
- roomCount
- features
- sellerId
- agentId
- status
- publishedAt
- updatedAt

Mapping prensipleri:
- title/description -> text
- exact filter alanları -> keyword
- location -> geo_point
- price/area/roomCount -> numeric
- publishedAt/updatedAt -> date

Local lab başlangıcı için 1 primary shard / 0 replica değerlendirilebilir.

## Redis

Canonical business data tutulmaz.

Örnek key'ler:
- `idempotency:offer:{idempotencyKey}`
- `rate-limit:{subject}:{route}:{window}`
- `cache:property-summary:{propertyId}`
- gerekirse `saga:offer:{offerId}`

TTL değerleri implementation gününde use-case'e göre kesinleştirilecektir.
