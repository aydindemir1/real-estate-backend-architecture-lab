# Day 9 — Exact File / Class / Commit Planı

## 0. Kapsam

Day 9 yalnızca BuyerService içindir.

Hedef:
- PostgreSQL/JPA -> Couchbase
- Hexagonal Architecture
- BuyerPreferences
- SavedSearch foundation
- inbound/outbound ports
- REST inbound adapter
- Couchbase outbound adapter
- unit/application/integration/architecture tests

Day 9 içinde Offer, Saga, Kafka, gRPC ve Redis idempotency yoktur.

## Task 1 — Mevcut BuyerService kaynak denetimi

Kontrol:
- `BuyerService/build.gradle`
- `BuyerService/src/main/resources/application.yml`
- mevcut bootstrap class/package

Target base package:
- `com.aydindemir.buyer`

## Task 2 — Build dependencies

`BuyerService/build.gradle`:

Kaldır:
- Spring Data JPA
- PostgreSQL driver

Koru:
- Eureka Client
- Config Client
- Actuator
- tracing baseline
- Spring Web MVC
- OpenAPI

Ekle:
- Spring Data Couchbase
- Testcontainers JUnit Jupiter
- Couchbase Testcontainers
- ArchUnit

## Task 3 — Configuration

`BuyerService/src/main/resources/application.yml`:
- application name
- root `.env` optional imports
- Config Server import
- Config Server URL

Config repository:
- `ConfigServerLocal/src/main/resources/config-repo/buyer-service.yml`

Kullanılan anahtarlar:
- `BUYER_COUCHBASE_CONNECTION_STRING`
- `BUYER_DB_USERNAME`
- `BUYER_DB_PASSWORD`
- `BUYER_COUCHBASE_KV_TIMEOUT`
- `BUYER_COUCHBASE_QUERY_TIMEOUT`
- `BUYER_COUCHBASE_BUCKET`
- `BUYER_COUCHBASE_SCOPE`
- `BUYER_COUCHBASE_COLLECTION`

Literal secret repository'ye yazılmaz.

## Task 4 — Hexagonal package skeleton

Target:

```text
com.aydindemir.buyer
├── domain/model
├── application/port/in
├── application/port/out
├── application/service
├── adapter/in/rest
└── adapter/out/persistence/couchbase
```

Day 9'da boş future gRPC/Kafka/Redis package/class oluşturulmaz.

## Task 5 — Domain Value Object'leri

- `BuyerId`
- `PriceRange`
- `RoomRange`
- `AreaRange`
- `LocationPreference`
- `NotificationSettings`
- `SavedSearch`

Kurallar:
- immutable model
- range invariant'ları
- negatif olmayan değerler
- max >= min
- gereksiz geo/search engine detail'i yok

## Task 6 — BuyerPreferences Aggregate

`BuyerPreferences` alanları:
- buyerId
- priceRange
- preferredLocations
- propertyTypes
- roomRange
- areaRange
- preferredFeatures
- notificationSettings
- savedSearches
- createdAt
- updatedAt

Davranış:
- preferences oluştur/güncelle
- saved search ekle

Kurallar:
- nested values geçerli
- collections defensively copied
- public setter yok

## Task 7 — Inbound ports

- `UpdateBuyerPreferencesUseCase`
- `GetBuyerPreferencesUseCase`
- `AddSavedSearchUseCase`

Application input:
- `UpdateBuyerPreferencesCommand`
- `GetBuyerPreferencesQuery`
- `AddSavedSearchCommand`

## Task 8 — Outbound persistence ports

- `SaveBuyerPreferencesPort`
- `LoadBuyerPreferencesPort`

Port contract'larında Couchbase/Spring Data type'ı bulunmaz.

## Task 9 — Application service

- `BuyerPreferencesResult`
- `BuyerPreferencesApplicationService`

Application service:
- inbound port'ları implement eder
- domain behavior'ı orchestrate eder
- persistence için outbound port kullanır
- Couchbase API'sini bilmez

## Task 10 — Couchbase document modeli

- `BuyerPreferencesDocument`
- nested document record'ları

Deterministic key:

```text
buyer-preferences::{buyerId}
```

Persistence document domain model değildir.

## Task 11 — Spring Data Couchbase repository

- `SpringDataBuyerPreferencesRepository`

Primary access:
- document ID

Direct key access yeterli olduğu için speculative secondary query/index eklenmez.

## Task 12 — Persistence mapper

- `BuyerPreferencesDocumentMapper`

Mapping:
- domain -> document
- document -> domain

## Task 13 — Persistence adapter

- `CouchbaseBuyerPreferencesAdapter`

Sorumluluk:
- deterministic key
- repository delegation
- mapping
- outbound port implementation

## Task 14 — Bucket / scope / collection bootstrap

Gerçekleşen isimler:
- bucket: `buyer`
- scope: `buyer_service`
- collection: `preferences`

Script:
- `infra/couchbase/bootstrap-buyer.ps1`

Storage backend:
- `couchstore`

Application role:
- `bucket_full_access[buyer]`

Secondary index:
- yok

## Task 15 — REST request contract'ları

- `UpdateBuyerPreferencesRequest`
- `AddSavedSearchRequest`

Boundary validation:
- required
- non-negative
- temel format/constraint

Cross-field semantic rule'lar domain tarafından korunur.

## Task 16 — REST response ve mapper

- `BuyerPreferencesResponse`
- `BuyerPreferencesRestMapper`

Mapper business logic içermez.

## Task 17 — REST controller

- `PUT /buyers/{buyerId}/preferences`
- `GET /buyers/{buyerId}/preferences`
- `POST /buyers/{buyerId}/saved-searches`

Expected:
- PUT -> 200
- GET -> 200
- POST -> 201
- absent GET -> 404

## Task 18 — Error mapping

Stable mapping:
- `BUYER_PREFERENCES_NOT_FOUND` -> 404
- `VALIDATION_ERROR` -> 400
- `INVALID_REQUEST` -> 400
- `INVALID_BUYER_PREFERENCES` -> 422
- `BUYER_PERSISTENCE_UNAVAILABLE` -> 503
- `INTERNAL_ERROR` -> 500

Raw Couchbase exception dış API'ye sızmaz.

## Task 19 — Domain tests

- `PriceRangeTest`
- `RoomRangeTest`
- `AreaRangeTest`
- `BuyerPreferencesTest`

Kapsam:
- valid/invalid ranges
- defensive copies
- saved search behavior

## Task 20 — Test fake

- `InMemoryBuyerPreferencesStore`

Outbound persistence port'larını test amacıyla implement eder.

## Task 21 — Application tests

- `BuyerPreferencesApplicationServiceTest`

Kapsam:
- create/update
- get
- not-found
- add saved search
- invalid domain input

## Task 22 — Couchbase Testcontainers foundation

- `BuyerCouchbaseContainerTestBase`

Sorumluluk:
- Couchbase container
- bucket/scope/collection bootstrap
- dynamic Spring properties

Shared lokal Couchbase integration test baseline değildir.

## Task 23 — Persistence integration tests

- `CouchbaseBuyerPreferencesAdapterIntegrationTest`

Kapsam:
- save/load
- deterministic key
- same-document update
- nested round-trip
- saved-search round-trip
- missing document

## Task 24 — CAS / concurrency kararı

Day 9'da CAS **bilinçli olarak ertelenmiştir**.

Detay:
- `docs/day-09/cas-concurrency-decision.md`

Fake optimistic locking eklenmez.

## Task 25 — REST controller tests

- `BuyerPreferencesControllerTest`

Kapsam:
- PUT 200
- GET 200
- GET 404
- invalid request
- POST saved search

Couchbase yerine inbound port mocks kullanılır.

## Task 26 — Hexagonal Architecture tests

- `BuyerHexagonalArchitectureTest`

ArchUnit kuralları:
- domain -> application/adapter yok
- domain -> Spring/Couchbase/Jakarta yok
- application -> adapter yok
- adapter.in -> adapter.out yok
- REST controller inbound ports kullanır
- Couchbase adapter outbound ports implement eder
- top-level package cycle yok

## Task 27 — Runtime smoke / acceptance

Çalıştır:
- Config Server
- Eureka
- Couchbase
- BuyerService

Doğrula:
- Config Server config load
- Eureka registration
- PUT preferences
- GET preferences
- POST saved search
- Couchbase write/read
- error scenarios

Evidence:
- `docs/evidence/day-09/`
- `docs/collections/day-09/Day-09-BuyerService.postman_collection.json`

## Task 28 — Documentation

Güncelle:
- `BuyerService/docs/DESIGN.md`
- `BuyerService/docs/PACKAGE-DESIGN.md`
- `BuyerService/ROADMAP.md`
- `docs/roadmap/day-09-buyer-couchbase-hexagonal.md`
- Knowledge Base impact

Kaydet:
- actual bucket/scope/collection
- document key
- config keys
- test coverage
- CAS defer kararı
- scope dışındaki Offer/gRPC/Kafka/Redis işleri

## Day 9 Final Gate

- [x] BuyerService JPA/PostgreSQL persistence kullanmıyor
- [x] Couchbase connection/config çalışıyor
- [x] BuyerPreferences domain framework-free
- [x] range invariant'ları uygulanıyor
- [x] collections defensively copied
- [x] inbound ports mevcut
- [x] outbound persistence ports mevcut
- [x] application service yalnız domain/ports'a bağlı
- [x] Couchbase document domain'den ayrı
- [x] deterministic document key çalışıyor
- [x] save/load/update integration test kapsamı mevcut
- [x] REST endpoint'leri çalışıyor
- [x] raw Couchbase exception API'ye sızmıyor
- [x] Hexagonal dependency rules otomatik
- [x] Config/Eureka baseline çalışıyor
- [x] Offer/Kafka/gRPC/Redis business implementation Day 9'a sızmıyor
- [x] docs actual implementation ile hizalı

## Completion

Day 9 **Completed / Verified**.

Kanıt:
- `docs/evidence/day-09/README.md`
- `docs/day-09/cas-concurrency-decision.md`
- `docs/collections/day-09/Day-09-BuyerService.postman_collection.json`
