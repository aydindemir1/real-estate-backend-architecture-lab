# Day 16 — Kesin GraphQL Read API Planı

## Kapsam

- SearchService GraphQL schema
- SearchPropertiesHandler üzerinde resolver
- authorization
- bounded query cost
- GraphQL testleri ve REST parity

## Task 1 — GraphQL dependency kurulumu

SearchService/build.gradle dosyasını değiştir.

Ekle:
- spring-boot-starter-graphql

GraphQL'i bütün service'lere ekleme.

Commit: build(search): add GraphQL support

## Task 2 — GraphQL schema

Oluştur:
- SearchService/src/main/resources/graphql/search.graphqls

İlk schema:
- Query.searchProperties
- yalnızca read use-case mevcutsa opsiyonel Query.property

Type'lar:
- PropertySearchResult
- PropertySearchItem
- SearchPageInfo
- SearchFilterInput

Elasticsearch DSL'i expose etme.

Commit: feat(search): add GraphQL search schema

## Task 3 — GraphQL input design

SearchFilterInput candidate alanları:
- text
- status
- propertyType
- city
- district
- minPrice
- maxPrice
- page
- size

Mevcut SearchPropertiesQuery application modelini mapper üzerinden yeniden kullan.

## Task 4 — GraphQL resolver/controller

Oluştur:
- graphql/SearchQueryController.java

@QueryMapping veya desteklenen annotation'ı kullan.

Sorumluluklar:
- GraphQL input -> SearchPropertiesQuery mapping
- mevcut SearchPropertiesHandler'ı çağırma
- result mapping

Duplicate Elasticsearch query logic oluşturma.

Commit: feat(search): add GraphQL search resolver

## Task 5 — GraphQL response model'leri

Yalnızca gerekliyse oluştur:
- graphql/PropertySearchGraphQlResponse.java
- graphql/SearchPageInfo.java

Transport coupling yalnızca adapter mapping boundary'de kabul edilebilir kalıyorsa application result yeniden kullanılabilir; açıklık için transport model tercih edilir.

Commit resolver ile birleştirilebilir.

## Task 6 — GraphQL authorization

Day 14'teki Search read role/scope policy'sini uygula.

Resolver'ın yalnızca Gateway arkasında gizli olmasına güvenme.

Commit: feat(search): secure GraphQL search queries

## Task 7 — GraphQL query complexity/depth

Bounded policy belirle.

İlk schema shallow olduğu için config'i basit tut.

Gerekmedikçe third-party complexity framework ekleme.

En az:
- bounded page size
- recursive schema yok
- expensive unbounded query shape'lerini disable/limit et

Commit: config(search): bound GraphQL query cost

## Task 8 — N+1 review

İlk Search GraphQL, batch result döndüren tek bir search handler çağırmalıdır; bu nedenle N+1 oluşmamalıdır.

Nested resolver pattern gerçekten N+1 üretmiyorsa DataLoader ekleme.

Kararı dokümante et.

## Task 9 — GraphQL error mapping

Map et:
- validation -> stable extension code içeren GraphQL error
- forbidden -> security error
- downstream unavailable -> stable error extension

Stack trace/Elasticsearch exception expose etme.

Gerekirse oluştur:
- graphql/GraphQlExceptionResolver.java

Commit: feat(search): standardize GraphQL error mapping

## Task 10 — GraphQL testleri

Oluştur:
- graphql/SearchGraphQlTest.java

Senaryolar:
- search query success
- filters
- empty result
- invalid page/price range
- unauthorized/forbidden

GraphQlTester kullan.

Commit: test(graphql): add SearchService GraphQL tests

## Task 11 — REST parity testi

REST ve GraphQL'in aynı application handler'ı çağırdığını ve semantic olarak tutarlı sonuçlar ürettiğini doğrula.

Duplicate business query logic oluşturma.

Opsiyonel olarak oluştur:
- SearchProtocolParityTest.java

Yalnızca faydalıysa commit:
test(search): verify REST and GraphQL query parity

## Task 12 — Contract documentation

Güncelle:
- docs/contracts/grpc-contract.md
- docs/contracts/graphql-schema.md
- docs/architecture/communication-architecture.md
- docs/roadmap/day-15-grpc-graphql.md

Gerçekte kullanılanları kaydet:
- proto package/version
- gRPC deadline
- auth mode
- status mapping
- GraphQL schema
- scope
- complexity/page limits

Commit: docs(protocol): finalize gRPC and GraphQL contracts

## Önerilen Commit Sırası

1. docs(protocol): confirm REST gRPC GraphQL boundaries
2. build(grpc): add protobuf and gRPC support
3. feat(contract): add AgentAvailability gRPC contract
4. build(grpc): configure protobuf code generation
5. feat(agent): add availability query use case
6. feat(agent): expose availability gRPC service
7. feat(agent): standardize gRPC error mapping
8. config(agent): configure gRPC server
9. feat(buyer): add AgentAvailability outbound port
10. feat(buyer): add AgentAvailability gRPC adapter
11. config(buyer): configure Agent gRPC client
12. feat(grpc): secure Agent availability calls
13. feat(buyer): add agent availability application flow
14. test(grpc): add adapter unit tests
15. test(grpc): add availability integration test
16. build(search): add GraphQL support
17. feat(search): add GraphQL search schema
18. feat(search): add GraphQL search resolver
19. feat(search): secure GraphQL search queries
20. config(search): bound GraphQL query cost
21. feat(search): standardize GraphQL error mapping
22. test(graphql): add SearchService GraphQL tests
23. docs(protocol): finalize gRPC and GraphQL contracts

Bitişik teknik commit'ler cohesive ise birleştirilebilir; gRPC ve GraphQL ayrı protocol capability'ler olarak review edilebilir kalmalıdır.

## Day 15'ten Açıkça Ertelenenler

Implement etme:
- GraphQL mutation
- GraphQL subscriptions
- full viewing scheduler
- streaming gRPC
- bidirectional streaming
- mTLS
- service mesh
- Kafka
- Saga
- custom GraphQL federation

## Kritik Tasarım Notu — REST primary olarak kalır

gRPC ve GraphQL özel amaçlı eklemelerdir; tüm REST endpoint'lerinin replacement'ı değildir.

## Kritik Tasarım Notu — Duplicate business logic yok

REST ve GraphQL aynı application query/use-case layer'ını çağırmalıdır.

gRPC adapter persistence'a doğrudan erişmek yerine Agent application use-case'i çağırmalıdır.

## Kritik Tasarım Notu — Deadlines

Her gRPC client call explicit deadline'a sahiptir.

Timeout ownership service-client seviyesindedir; sonraki Day 20 resilience policy'leri bu baseline üzerine kurulur.

## Day 15 Final Gate

Day 15 yalnızca aşağıdakiler sağlanırsa kapanır:
- proto contract version-controlled ve reproducible şekilde generate ediliyor
- AgentService application use-case üzerinden gRPC availability expose ediyor
- BuyerService outbound port + gRPC adapter üzerinden Agent'i çağırıyor
- explicit deadline mevcut
- auth/service identity uygulanmış
- gRPC error'ları semantic status'lara map ediliyor
- integration test client/server compatibility'yi kanıtlıyor
- SearchService GraphQL schema explicit
- GraphQL resolver mevcut search handler'ı yeniden kullanıyor
- GraphQL read authorization çalışıyor
- query/page cost bounded
- GraphQL error'ları internal detayları sızdırmıyor
- REST hâlâ çalışıyor
- REST/GraphQL/gRPC adapter'ları arasında business logic duplicate edilmemiş
- Kafka/Saga/mTLS/streaming scope Day 15'e sızmıyor
- docs gerçek implementation ile eşleşiyor

## Source-of-truth notu

Bu dosya final Day 15–33 roadmap'i izler. Önceki birleşik Day numaralandırması `docs/roadmap/LEGACY-DAY-MAPPING.md` ile superseded edilmiştir.
