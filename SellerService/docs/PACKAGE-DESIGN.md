# SellerService — Package / Class-Level Design

## Architecture
Onion Architecture

## Amaç
Domain model'i merkeze koymak; application ve infrastructure katmanlarını dış halkalar olarak konumlandırmak.

## Package yapısı

```text
com.aydindemir.seller
├── SellerServiceApplication
├── domain
│   ├── model
│   │   ├── Seller
│   │   ├── ListingSubmission
│   │   ├── SellerId
│   │   ├── UserId
│   │   ├── ListingSubmissionId
│   │   ├── PropertyDraftData
│   │   ├── SellerStatus
│   │   └── ListingSubmissionStatus
│   ├── event
│   │   ├── ListingSubmittedDomainEvent
│   │   ├── OfferAcceptedDomainEvent
│   │   └── OfferRejectedDomainEvent
│   ├── repository
│   │   ├── SellerRepository
│   │   ├── ListingSubmissionRepository
│   │   ├── PendingOfferProjectionRepository
│   │   └── SellerActivityRepository
│   ├── service
│   │   └── SellerDomainService
│   └── exception
│       ├── SellerNotFoundException
│       ├── SellerNotActiveException
│       └── ListingSubmissionNotFoundException
├── application
│   ├── command
│   │   ├── CreateSellerCommand
│   │   ├── SubmitListingCommand
│   │   ├── AcceptOfferCommand
│   │   └── RejectOfferCommand
│   ├── query
│   │   ├── GetSellerQuery
│   │   ├── ListSellerSubmissionsQuery
│   │   ├── ListPendingOffersQuery
│   │   └── GetSellerActivityQuery
│   ├── service
│   │   ├── SellerApplicationService
│   │   ├── ListingSubmissionApplicationService
│   │   └── SellerOfferApplicationService
│   └── port
│       ├── ListingCommandPublisher
│       └── SellerEventPublisher
├── infrastructure
│   ├── cassandra
│   │   ├── table
│   │   │   ├── SellerByIdTable
│   │   │   ├── ListingSubmissionBySellerMonthTable
│   │   │   ├── PendingOfferBySellerTable
│   │   │   ├── OfferBySellerMonthTable
│   │   │   └── SellerActivityBySellerMonthTable
│   │   ├── repository
│   │   │   ├── SpringDataSellerByIdRepository
│   │   │   ├── SpringDataListingSubmissionRepository
│   │   │   ├── SpringDataPendingOfferRepository
│   │   │   └── SpringDataSellerActivityRepository
│   │   ├── mapper
│   │   │   └── SellerCassandraMapper
│   │   └── adapter
│   │       ├── CassandraSellerRepositoryAdapter
│   │       ├── CassandraListingSubmissionAdapter
│   │       └── CassandraOfferProjectionAdapter
│   ├── messaging
│   │   ├── rabbitmq
│   │   │   └── RabbitMqListingCommandPublisher
│   │   └── kafka
│   │       ├── KafkaSellerEventPublisher
│   │       ├── PropertyCreatedEventConsumer
│   │       └── PropertyHeldEventConsumer
│   └── configuration
│       ├── CassandraConfiguration
│       ├── RabbitMqConfiguration
│       └── KafkaConfiguration
└── presentation
    └── rest
        ├── SellerController
        ├── ListingSubmissionController
        ├── SellerOfferController
        ├── request
        ├── response
        └── mapper
```

## Onion rule

```text
domain <- application <- infrastructure/presentation
```

Outer layer'lar inner layer'ı bilir; domain hiçbir infrastructure technology'sine bağımlı değildir.
