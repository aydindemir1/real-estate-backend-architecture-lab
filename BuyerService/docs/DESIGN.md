# BuyerService Tasarımı

## 1. Amaç

BuyerService, buyer tarafındaki preference ve saved search verilerinin sahibi olan servistir.

Day 9 kapsamında hedef capability:

- BuyerPreferences oluşturma/güncelleme
- BuyerPreferences okuma
- SavedSearch ekleme
- Couchbase üzerinde kalıcı saklama

Offer, Saga, Kafka, gRPC ve Redis bu Day'in implementation scope'unda değildir.

## 2. Architecture

BuyerService Day 9 itibarıyla **Hexagonal Architecture** kullanır.

Temel bağımlılık yönü:

```text
REST Adapter
    |
    v
Inbound Ports
    |
    v
Application Service
    |
    v
Domain
    |
    v
Outbound Ports
    ^
    |
Couchbase Persistence Adapter
```

Domain katmanı Spring, Couchbase veya adapter package'larını bilmez.

Application katmanı adapter implementation'larını bilmez.

## 3. Domain modeli

### Aggregate
- `BuyerPreferences`

### Value Object / Domain Model
- `BuyerId`
- `PriceRange`
- `RoomRange`
- `AreaRange`
- `LocationPreference`
- `NotificationSettings`
- `SavedSearch`

### Temel invariant'lar
- fiyat değerleri negatif olamaz
- max price, min price'dan küçük olamaz
- room range geçerli olmalıdır
- area range geçerli olmalıdır
- collection alanları defensively copied edilir
- domain model public setter yaklaşımı kullanmaz

## 4. Application katmanı

### Inbound ports
- `UpdateBuyerPreferencesUseCase`
- `GetBuyerPreferencesUseCase`
- `AddSavedSearchUseCase`

### Application input'ları
- `UpdateBuyerPreferencesCommand`
- `GetBuyerPreferencesQuery`
- `AddSavedSearchCommand`

### Outbound ports
- `SaveBuyerPreferencesPort`
- `LoadBuyerPreferencesPort`

### Application service
- `BuyerPreferencesApplicationService`

Application service orchestration yapar; Couchbase API'si bilmez.

## 5. REST inbound adapter

Controller:
- `BuyerPreferencesController`

Endpoint'ler:

| Method | Path | Başarılı response |
|---|---|---:|
| PUT | `/buyers/{buyerId}/preferences` | 200 |
| GET | `/buyers/{buyerId}/preferences` | 200 |
| POST | `/buyers/{buyerId}/saved-searches` | 201 |

Request DTO'ları:
- `UpdateBuyerPreferencesRequest`
- `AddSavedSearchRequest`

Response:
- `BuyerPreferencesResponse`

REST ile application contract arasında:
- `BuyerPreferencesRestMapper`

Path variable adı runtime reflection'a bırakılmamış, açıkça `@PathVariable("buyerId")` olarak belirtilmiştir.

## 6. Error semantics

Day 9'da kullanılan temel mapping:

- missing preferences -> `404 BUYER_PREFERENCES_NOT_FOUND`
- Bean Validation -> `400 VALIDATION_ERROR`
- malformed JSON / invalid UUID -> `400 INVALID_REQUEST`
- domain semantic violation -> `422 INVALID_BUYER_PREFERENCES`
- persistence unavailable -> `503 BUYER_PERSISTENCE_UNAVAILABLE`
- unexpected -> `500 INTERNAL_ERROR`

Raw Couchbase exception API'ye sızdırılmaz.

## 7. Couchbase persistence

### Physical hierarchy

```text
Cluster
└── bucket: buyer
    └── scope: buyer_service
        └── collection: preferences
```

### Document key

```text
buyer-preferences::{buyerId}
```

Key deterministiktir ve primary access path direct document-key lookup'tır.

### Persistence model

Domain Aggregate doğrudan Couchbase document değildir.

Ayrı persistence model:
- `BuyerPreferencesDocument`
- `MoneyRangeDocument`
- `LocationPreferenceDocument`
- `NotificationSettingsDocument`
- `SavedSearchDocument`

Mapping:
- `BuyerPreferencesDocumentMapper`

Adapter:
- `CouchbaseBuyerPreferencesAdapter`

Repository:
- `SpringDataBuyerPreferencesRepository`

Day 9'da secondary index eklenmemiştir; mevcut use-case'ler direct key access ile karşılanmaktadır.

## 8. Configuration

BuyerService bootstrap config:
- `BuyerService/src/main/resources/application.yml`

Root `.env` STS direct run için aşağıdaki optional import'larla okunur:
- `optional:file:.env[.properties]`
- `optional:file:../.env[.properties]`

Config Server:
- default URL: `http://localhost:8888`

Remote/local config repository:
- `ConfigServerLocal/src/main/resources/config-repo/buyer-service.yml`

### Config anahtarları

- `BUYER_COUCHBASE_CONNECTION_STRING`
- `BUYER_DB_USERNAME`
- `BUYER_DB_PASSWORD`
- `BUYER_COUCHBASE_KV_TIMEOUT`
- `BUYER_COUCHBASE_QUERY_TIMEOUT`
- `BUYER_COUCHBASE_BUCKET`
- `BUYER_COUCHBASE_SCOPE`
- `BUYER_COUCHBASE_COLLECTION`

Secret değerler repository'ye yazılmaz.

## 9. Couchbase bootstrap

Script:
- `infra/couchbase/bootstrap-buyer.ps1`

Bootstrap:
- cluster initialization
- `buyer` bucket
- `buyer_service` scope
- `preferences` collection
- BuyerService application user
- Community-compatible `bucket_full_access[buyer]` role

Storage backend:
- `couchstore`

Secondary index:
- yok

## 10. Test stratejisi

### Domain tests
- range invariant'ları
- defensive collection behavior
- saved search behavior

### Application tests
- in-memory fake persistence port
- update/get/add saved search flow'ları

### Persistence integration
- Couchbase Testcontainers
- save/load
- deterministic key
- update same document
- nested round-trip
- saved search round-trip
- missing document

### REST tests
- success
- not-found
- validation
- malformed/invalid request

### Architecture tests
ArchUnit ile:
- domain framework independent
- domain application/adapter bağımsız
- application adapter bağımsız
- inbound adapter outbound adapter'a bağımlı değil
- REST controller inbound port kullanıyor
- Couchbase adapter outbound port'ları implement ediyor
- top-level package cycle yok

## 11. CAS / optimistic concurrency

Day 9'da bilinçli olarak ertelenmiştir.

Neden:
- persistence CAS token'ı mevcut domain/application port contract'ında taşınmıyor
- yalnız `BuyerPreferencesDocument` üzerine `@Version` koymak gerçek stale-write koruması sağlamaz

Detay:
- `docs/day-09/cas-concurrency-decision.md`

## 12. Lokal runtime doğrulaması

Doğrulananlar:
- Config Server -> PASS
- Couchbase authentication -> PASS
- bucket open -> PASS
- Eureka registration -> PASS
- PUT/GET preferences -> PASS
- POST saved search -> PASS
- persistence write/read -> PASS
- validation/error senaryoları -> PASS

Kanıt:
- `docs/evidence/day-09/`
