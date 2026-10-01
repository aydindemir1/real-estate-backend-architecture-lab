# SellerService — Package / Class-Level Design

## Architecture

**Onion Architecture**

Day 10 implementation'ı Seller ve ListingSubmission capability'sini kapsar.

## Gerçekleşen package yapısı

```text
com.aydindemir.seller
├── SellerServiceApplication
├── domain
│   ├── model
│   │   ├── Seller
│   │   ├── SellerId
│   │   ├── UserId
│   │   ├── SellerStatus
│   │   ├── ListingSubmission
│   │   ├── ListingSubmissionId
│   │   ├── ListingSubmissionStatus
│   │   └── PropertyDraftData
│   ├── repository
│   │   ├── SellerRepository
│   │   ├── ListingSubmissionRepository
│   │   ├── ListingSubmissionPage
│   │   └── ListingSubmissionPageRequest
│   └── exception
│       ├── SellerNotFoundException
│       ├── SellerNotActiveException
│       ├── ListingSubmissionNotFoundException
│       └── InvalidListingSubmissionStateException
├── application
│   ├── command
│   │   ├── CreateSellerCommand
│   │   ├── CreateListingSubmissionCommand
│   │   └── SubmitListingCommand
│   ├── query
│   │   ├── GetSellerQuery
│   │   └── ListSellerSubmissionsQuery
│   └── service
│       ├── SellerApplicationService
│       ├── ListingSubmissionApplicationService
│       ├── SellerResult
│       ├── ListingSubmissionResult
│       └── ListingSubmissionPageResult
├── infrastructure
│   ├── cassandra
│   │   ├── table
│   │   │   ├── SellerByIdTable
│   │   │   └── ListingSubmissionBySellerMonthTable
│   │   ├── repository
│   │   │   ├── SpringDataSellerByIdRepository
│   │   │   └── SpringDataListingSubmissionRepository
│   │   ├── mapper
│   │   │   ├── SellerCassandraMapper
│   │   │   └── ListingSubmissionCassandraMapper
│   │   └── adapter
│   │       ├── CassandraSellerRepositoryAdapter
│   │       └── CassandraListingSubmissionRepositoryAdapter
│   └── configuration
│       └── SellerApplicationConfiguration
└── presentation
    └── rest
        ├── SellerController
        ├── ListingSubmissionController
        ├── SellerApiExceptionHandler
        ├── mapper
        │   ├── SellerRestMapper
        │   └── ListingSubmissionRestMapper
        ├── request
        │   ├── CreateSellerRequest
        │   ├── CreateListingSubmissionRequest
        │   └── SubmitListingRequest
        └── response
            ├── ApiErrorResponse
            ├── SellerResponse
            ├── ListingSubmissionResponse
            └── ListingSubmissionPageResponse
```

Legacy baseline `controller/HelloController` ayrıca bulunur; Day 10 Onion business flow'unun parçası değildir.

## Dependency kuralları

### Domain
- application bilmez
- infrastructure bilmez
- presentation bilmez
- Spring/Jakarta/Cassandra/Kafka/RabbitMQ bilmez

### Application
- domain'i bilir
- infrastructure/presentation bilmez
- Spring/Cassandra/messaging framework'lerine bağımlı değildir

### Infrastructure
- domain repository contract'larını implement eder
- presentation'a bağımlı değildir
- Cassandra-specific detail'leri kendi sınırında tutar

### Presentation
- application/domain contract'larını kullanır
- Cassandra infrastructure'a doğrudan bağımlı değildir

Bu sınırlar `SellerOnionArchitectureTest` ile enforce edilir.

## Domain / persistence ayrımı

Domain:
- `Seller`
- `ListingSubmission`

Persistence:
- `SellerByIdTable`
- `ListingSubmissionBySellerMonthTable`

Mapping:
- `SellerCassandraMapper`
- `ListingSubmissionCassandraMapper`

## Cassandra query shape

Listing list query:
```text
seller_id + year_month
```

Exact submission lookup:
```text
seller_id + year_month + created_at + submission_id
```

## Test package'ları

```text
src/test/java/com/aydindemir/seller
├── domain/model
├── application/service
├── infrastructure/cassandra
├── presentation/rest
└── architecture
```

Önemli testler:
- `SellerTest`
- `ListingSubmissionTest`
- `PropertyDraftDataTest`
- `SellerApplicationServiceTest`
- `ListingSubmissionApplicationServiceTest`
- `CassandraSellerRepositoryAdapterIntegrationTest`
- `CassandraListingSubmissionRepositoryAdapterIntegrationTest`
- `CassandraQueryDesignGuardTest`
- `SellerControllerTest`
- `ListingSubmissionControllerTest`
- `SellerOnionArchitectureTest`

## Day 10'da bulunmayan package/class'lar

Aşağıdakiler actual implementation değildir:
- `domain.event`
- `domain.service`
- `application.port`
- RabbitMQ/Kafka adapter'ları
- Saga classes
- offer projection package'ları

Plan dokümanındaki boş/aday package hedefleri actual package yapısı olarak gösterilmez.
