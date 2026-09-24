# GraphQL Schema Tasarımı

GraphQL yalnızca flexible read/query use-case'leri için kullanılacaktır. REST'in tamamını replace etmeyecektir.

## Primary use

Property/Search read use-case'leri.

## Query taslağı

```graphql
type Query {
  property(id: ID!): Property
  searchProperties(filter: PropertySearchFilter!, page: PageInput): PropertySearchPage!
  autocomplete(query: String!): [AutocompleteSuggestion!]!
}
```

## Property

```graphql
type Property {
  id: ID!
  title: String!
  description: String
  propertyType: PropertyType!
  city: String!
  district: String!
  price: Money!
  grossArea: Float
  netArea: Float
  roomCount: Int
  features: [String!]!
  status: PropertyStatus!
}
```

## Search filter

```graphql
input PropertySearchFilter {
  query: String
  city: String
  district: String
  propertyType: PropertyType
  minPrice: Float
  maxPrice: Float
  minArea: Float
  maxArea: Float
  roomCount: Int
  features: [String!]
  latitude: Float
  longitude: Float
  distanceKm: Float
}
```

## Pagination

```graphql
input PageInput {
  page: Int = 0
  size: Int = 20
}

type PropertySearchPage {
  items: [PropertySearchResult!]!
  totalElements: Int!
  page: Int!
  size: Int!
}
```

## Tasarım kuralları

- GraphQL write/mutation ilk aşamada kullanılmayacak.
- N+1 problemi gözlemlenecek; gerekirse DataLoader gibi çözüm değerlendirilecek.
- Authorization query resolver seviyesinde uygulanabilir.
- Elasticsearch query capability GraphQL schema'ya birebir taşınmayacak; business-oriented field/filter sunulacak.
