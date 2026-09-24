# SearchService — Package / Class-Level Design

## Architecture
Vertical Slice Architecture + CQRS Query Side

## Amaç
Search capability'lerini feature bazında ayırmak ve Elasticsearch Projection modelini canonical Property modelinden bağımsız tutmak.

## Package yapısı

```text
com.aydindemir.search
├── SearchServiceApplication
├── searchproperties
│   ├── SearchPropertiesQuery
│   ├── SearchPropertiesHandler
│   ├── SearchPropertiesController
│   └── SearchPropertiesResponse
├── autocomplete
│   ├── AutocompleteQuery
│   ├── AutocompleteHandler
│   └── AutocompleteController
├── facetsearch
│   ├── FacetSearchQuery
│   ├── FacetSearchHandler
│   └── FacetSearchController
├── geosearch
│   ├── GeoSearchQuery
│   ├── GeoSearchHandler
│   └── GeoSearchController
├── indexing
│   ├── IndexPublishedPropertyHandler
│   ├── UpdatePropertyProjectionHandler
│   ├── UpdatePriceProjectionHandler
│   ├── UpdateStatusProjectionHandler
│   └── DeletePropertyProjectionHandler
├── eventconsumer
│   ├── PropertyPublishedEventConsumer
│   ├── PropertyUpdatedEventConsumer
│   ├── PropertyPriceChangedEventConsumer
│   ├── PropertyReservedEventConsumer
│   ├── PropertyWithdrawnEventConsumer
│   └── PropertySoldEventConsumer
├── reindex
│   ├── ReindexPropertyUseCase
│   ├── RebuildIndexUseCase
│   └── ReconcileIndexUseCase
└── shared
    ├── model
    │   └── PropertySearchDocument
    ├── elasticsearch
    │   ├── SpringDataPropertySearchRepository
    │   ├── PropertySearchQueryRepository
    │   ├── ElasticsearchPropertySearchAdapter
    │   └── PropertySearchMapper
    ├── messaging
    │   └── ProcessedEventRepository
    ├── graphql
    │   ├── PropertySearchGraphqlController
    │   └── GraphqlSearchMapper
    └── configuration
        ├── ElasticsearchConfiguration
        └── KafkaConfiguration
```

## CQRS rule

SearchService yalnızca Query Side'dır. Canonical write operation içermez. Projection event'lerle güncellenir.

## Önemli karar
`PropertySearchDocument` domain Aggregate değildir; Elasticsearch'e optimize edilmiş Read Model'dir.
