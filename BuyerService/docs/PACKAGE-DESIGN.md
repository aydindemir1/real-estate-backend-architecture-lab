# BuyerService — Package / Class-Level Design

## Architecture
Hexagonal Architecture

## Amaç
Application core'u inbound ve outbound adapter'lardan ayırmak.

## Package yapısı

```text
com.aydindemir.buyer
├── BuyerServiceApplication
├── domain
│   ├── model
│   │   ├── BuyerPreferences
│   │   ├── Offer
│   │   ├── BuyerId
│   │   ├── OfferId
│   │   ├── PropertyId
│   │   ├── PriceRange
│   │   ├── AreaRange
│   │   ├── RoomRange
│   │   ├── LocationPreference
│   │   ├── NotificationSettings
│   │   ├── SavedSearch
│   │   └── OfferStatus
│   ├── service
│   │   └── OfferDomainService
│   └── exception
│       ├── OfferNotFoundException
│       ├── InvalidOfferStateException
│       └── BuyerPreferencesNotFoundException
├── application
│   ├── port
│   │   ├── in
│   │   │   ├── CreateBuyerPreferencesUseCase
│   │   │   ├── UpdateBuyerPreferencesUseCase
│   │   │   ├── GetBuyerPreferencesUseCase
│   │   │   ├── AddSavedSearchUseCase
│   │   │   ├── CreateOfferUseCase
│   │   │   ├── CancelOfferUseCase
│   │   │   ├── ListBuyerOffersUseCase
│   │   │   └── RequestViewingUseCase
│   │   └── out
│   │       ├── SaveBuyerPreferencesPort
│   │       ├── LoadBuyerPreferencesPort
│   │       ├── SaveOfferPort
│   │       ├── LoadOfferPort
│   │       ├── ListBuyerOffersPort
│   │       ├── AgentAvailabilityPort
│   │       ├── PublishOfferEventPort
│   │       └── IdempotencyPort
│   └── service
│       ├── BuyerPreferencesApplicationService
│       ├── OfferApplicationService
│       └── ViewingApplicationService
├── adapter
│   ├── in
│   │   ├── rest
│   │   │   ├── BuyerPreferencesController
│   │   │   ├── OfferController
│   │   │   ├── ViewingController
│   │   │   ├── request
│   │   │   ├── response
│   │   │   └── mapper
│   │   └── messaging
│   │       ├── PropertyHeldEventConsumer
│   │       ├── PropertyReservedEventConsumer
│   │       └── PropertyHoldReleasedEventConsumer
│   └── out
│       ├── persistence
│       │   ├── couchbase
│       │   │   ├── BuyerPreferencesDocument
│       │   │   ├── OfferDocument
│       │   │   ├── SpringDataBuyerPreferencesRepository
│       │   │   └── SpringDataOfferRepository
│       │   ├── mapper
│       │   │   └── BuyerPersistenceMapper
│       │   └── BuyerPersistenceAdapter
│       ├── grpc
│       │   └── AgentAvailabilityGrpcAdapter
│       ├── messaging
│       │   └── KafkaOfferEventPublisher
│       └── redis
│           └── RedisIdempotencyAdapter
└── configuration
    ├── CouchbaseConfiguration
    ├── KafkaConfiguration
    ├── RedisConfiguration
    └── GrpcClientConfiguration
```

## Hexagonal rule

Application yalnızca `port.in` ve `port.out` contract'larını bilir. Couchbase, Kafka, Redis ve gRPC adapter detayları core'a sızmaz.
