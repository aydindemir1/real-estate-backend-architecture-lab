# PropertyService — Package / Class-Level Design

## Architecture
Vertical Slice Architecture

## Amaç
Kod organizasyonunu technical layer yerine business feature/use-case etrafında yapmak.

## Package yapısı

```text
com.aydindemir.property
├── PropertyServiceApplication
├── create
│   ├── CreatePropertyFromListingCommandHandler
│   ├── CreatePropertyFromListingCommand
│   ├── CreatePropertyResult
│   └── ListingCommandConsumer
├── getbyid
│   ├── GetPropertyQuery
│   ├── GetPropertyHandler
│   └── PropertyQueryController
├── update
│   ├── UpdatePropertyCommand
│   ├── UpdatePropertyHandler
│   └── UpdatePropertyController
├── publish
│   ├── PublishPropertyCommand
│   ├── PublishPropertyHandler
│   └── PublishPropertyController
├── changeprice
│   ├── ChangePropertyPriceCommand
│   ├── ChangePropertyPriceHandler
│   └── ChangePropertyPriceController
├── assignagent
│   ├── AssignAgentCommand
│   ├── AssignAgentHandler
│   └── AssignAgentController
├── withdraw
│   ├── WithdrawPropertyCommand
│   ├── WithdrawPropertyHandler
│   └── WithdrawPropertyController
├── hold
│   ├── HoldPropertyForOfferCommand
│   ├── HoldPropertyForOfferHandler
│   └── OfferRequestedEventConsumer
├── releasehold
│   ├── ReleasePropertyHoldCommand
│   ├── ReleasePropertyHoldHandler
│   └── SellerRejectedEventConsumer
├── reserve
│   ├── ReservePropertyCommand
│   ├── ReservePropertyHandler
│   └── SellerAcceptedEventConsumer
├── marksold
│   ├── MarkPropertySoldCommand
│   └── MarkPropertySoldHandler
└── shared
    ├── domain
    │   ├── Property
    │   ├── PropertyId
    │   ├── PropertyStatus
    │   ├── PropertyType
    │   ├── Money
    │   ├── Address
    │   ├── GeoLocation
    │   ├── Area
    │   └── exception
    ├── persistence
    │   ├── PropertyDocument
    │   ├── SpringDataPropertyRepository
    │   ├── PropertyRepository
    │   └── PropertyPersistenceMapper
    ├── messaging
    │   ├── PropertyEventPublisher
    │   ├── KafkaPropertyEventPublisher
    │   ├── ProcessedMessageRepository
    │   └── EventEnvelopeMapper
    ├── web
    │   ├── request
    │   ├── response
    │   └── PropertyRestMapper
    └── configuration
        ├── MongoConfiguration
        ├── KafkaConfiguration
        └── RabbitMqConfiguration
```

## Vertical Slice rule

Bir feature kendi command/query/handler/controller/consumer bileşenlerini birlikte taşır. `shared` yalnızca gerçekten cross-slice olan domain/infrastructure parçaları içerir.

## Önemli karar
`Property` Aggregate Root shared domain altında kalır; fakat use-case orchestration her slice içinde yer alır.
