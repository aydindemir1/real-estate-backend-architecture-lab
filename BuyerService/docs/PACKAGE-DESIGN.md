# BuyerService — Package / Class-Level Design

## Architecture

**Hexagonal Architecture**

Day 9 implementation'ı yalnız BuyerPreferences / SavedSearch capability'sini kapsar.

## Gerçekleşen package yapısı

```text
com.aydindemir.buyer
├── BuyerServiceApplication
├── domain
│   └── model
│       ├── BuyerId
│       ├── BuyerPreferences
│       ├── PriceRange
│       ├── RoomRange
│       ├── AreaRange
│       ├── LocationPreference
│       ├── NotificationSettings
│       └── SavedSearch
├── application
│   ├── exception
│   │   └── BuyerPreferencesNotFoundException
│   ├── port
│   │   ├── in
│   │   │   ├── UpdateBuyerPreferencesUseCase
│   │   │   ├── GetBuyerPreferencesUseCase
│   │   │   ├── AddSavedSearchUseCase
│   │   │   ├── UpdateBuyerPreferencesCommand
│   │   │   ├── GetBuyerPreferencesQuery
│   │   │   └── AddSavedSearchCommand
│   │   └── out
│   │       ├── SaveBuyerPreferencesPort
│   │       └── LoadBuyerPreferencesPort
│   └── service
│       ├── BuyerPreferencesApplicationService
│       └── BuyerPreferencesResult
├── adapter
│   ├── in
│   │   └── rest
│   │       ├── BuyerPreferencesController
│   │       ├── error
│   │       │   ├── ApiErrorResponse
│   │       │   └── BuyerApiExceptionHandler
│   │       ├── mapper
│   │       │   └── BuyerPreferencesRestMapper
│   │       ├── request
│   │       │   ├── UpdateBuyerPreferencesRequest
│   │       │   └── AddSavedSearchRequest
│   │       └── response
│   │           └── BuyerPreferencesResponse
│   └── out
│       └── persistence
│           └── couchbase
│               ├── adapter
│               │   └── CouchbaseBuyerPreferencesAdapter
│               ├── document
│               │   ├── BuyerPreferencesDocument
│               │   ├── MoneyRangeDocument
│               │   ├── LocationPreferenceDocument
│               │   ├── NotificationSettingsDocument
│               │   └── SavedSearchDocument
│               ├── mapper
│               │   └── BuyerPreferencesDocumentMapper
│               └── repository
│                   └── SpringDataBuyerPreferencesRepository
└── config
    └── BuyerApplicationConfiguration
```

## Dependency kuralları

### Domain
Domain:
- Spring bilmez
- Couchbase bilmez
- application bilmez
- adapter bilmez

### Application
Application:
- domain'i bilir
- inbound/outbound port contract'larını taşır
- adapter implementation'larını bilmez
- Couchbase API'si bilmez

### Inbound adapter
REST adapter:
- inbound port'ları kullanır
- outbound persistence adapter'a doğrudan gitmez

### Outbound adapter
Couchbase adapter:
- outbound port'ları implement eder
- Spring Data repository ve persistence document modelini içeride tutar

## Domain / persistence model ayrımı

`BuyerPreferences`:
- business model / Aggregate

`BuyerPreferencesDocument`:
- Couchbase persistence modeli

İki model `BuyerPreferencesDocumentMapper` ile çevrilir.

Bu ayrım Couchbase annotation ve persistence detail'lerinin domain'e sızmasını engeller.

## Deterministic key

```java
public static final String KEY_PREFIX = "buyer-preferences::";
```

Key:

```text
buyer-preferences::{buyerId}
```

## Test package'ları

```text
src/test/java/com/aydindemir/buyer
├── domain/model
├── application/service
├── application/support
├── adapter/in/rest
├── adapter/out/persistence/couchbase
├── architecture
└── smoke
```

Önemli test sınıfları:
- `BuyerPreferencesTest`
- `PriceRangeTest`
- `RoomRangeTest`
- `AreaRangeTest`
- `BuyerPreferencesApplicationServiceTest`
- `InMemoryBuyerPreferencesStore`
- `BuyerPreferencesControllerTest`
- `BuyerCouchbaseContainerTestBase`
- `CouchbaseBuyerPreferencesAdapterIntegrationTest`
- `BuyerHexagonalArchitectureTest`
- `BuyerServiceSmokeTest`

## Day 9'da bulunmayan package/class'lar

Aşağıdaki gelecekteki tasarımlar Day 9 implementation'ına dahil değildir:

- Offer domain/application/persistence
- gRPC adapter
- Kafka adapter
- Redis adapter
- Saga classes

Dokümantasyon bunları gerçekleşmiş package yapısı olarak göstermemelidir.
