# Day 17 — Kesin Kafka + Stream + Function Foundation Planı

## Kapsam

- Kafka local infrastructure
- Spring Cloud Stream
- Spring Cloud Function
- event envelope
- property.events foundation
- partition/group/order testleri

## Reliability koruması
Kritik business publication, best-effort save-then-send şeklinde bağlanmaz. Day 17 transport/contracts temelini kurar; Day 18–20 reliability katmanını oluşturur. Offer publication, gerçek Offer workflow ile Day 22'de aktive edilir.

## Task 1 — Kafka local infrastructure

`docker-compose.yml` dosyasını değiştir.

Local learning setup ile compatible, explicit image/version kullanan Kafka ekle.

Basit bir local topology seç:
- mevcut image/tooling temiz şekilde destekliyorsa single broker KRaft tercih edilir

Zorunlu:
- explicit version
- host + compose network için doğru advertised listeners
- yararlıysa named volume
- healthcheck
- bounded memory

Seçilen Kafka image gerçekten gerektirmiyorsa ZooKeeper ekleme.

Commit: infra(kafka): add local Kafka broker

## Task 2 — Dependency kurulumu

Yalnızca şu anda publish/consume yapan module'lere ekle.

Muhtemel module'ler:
- PropertyService
- BuyerService
- yalnızca minimal consumer demo burada yer alacaksa SearchService

Dependency'ler:
- spring-cloud-stream
- Kafka binder
- transitively yeterli değilse Spring Cloud Function core
- yalnızca faydalıysa test binder
- Testcontainers Kafka

Kafka client'ı global olarak bütün module'lere ekleme.

Commit: build(kafka): add Stream and Function dependencies

## Task 3 — Topic isimleri ve ownership

Canonical topic'ler:
- property.events
- offer.events

Naming domain/capability odaklıdır.

Her event type için ayrı topic oluşturma.

## Task 4 — Event envelope modeli

Service domain model'lerini birbirine bağlamayan contract package/location oluştur.

Aday:
- contracts/events/CommonEventEnvelope.java

Alanlar:
- eventId
- eventType
- aggregateId
- aggregateType
- occurredAt
- correlationId
- causationId
- schemaVersion
- payload

Implementation seçimi:
- pratik olduğu yerde typed payload ile generic envelope
- veya aynı metadata yapısını taşıyan event-specific record

Type safety kayboluyorsa raw Map payload kullanma.

Commit: feat(messaging): add event envelope contracts

## Task 5 — Property event contract'ları

İlk event payload type'larını oluştur:
- PropertyCreatedEvent
- PropertyPublishedEvent
- PropertyUpdatedEvent
- PropertyPriceChangedEvent
- PropertyHeldEvent
- PropertyHoldReleasedEvent
- PropertyReservedEvent
- PropertyWithdrawnEvent
- PropertySoldEvent

Day 16'da her event'in aktif olarak emit edilmesi gerekmez.

En azından Stream wiring'i kanıtlamak için yeterli bir veya iki gerçek producer örneği implemente et; mevcut Property use-case'leri destekliyorsa PropertyPublished ve PropertyPriceChanged tercih edilebilir.

Sırf coverage için kullanılmayan fake event emission üretme.

Commit: feat(property): add property event contracts

## Task 6 — Offer event contract'ları

İlk payload type'larını oluştur:
- OfferRequestedEvent
- SellerAcceptedEvent
- SellerRejectedEvent
- OfferExpiredEvent

Offer Aggregate henüz aktif değilse Day 16 producer foundation-only olabilir.

Var olmayan workflow'dan fake offer event emit etme.

Commit: feat(buyer): add offer event contracts

## Task 7 — Spring Cloud Stream binding naming

Functional binding isimleri kullan.

Aday function'lar:
- propertyEventsSupplier veya explicit StreamBridge tabanlı publisher
- yalnızca mevcut flow destekliyorsa offerEventsSupplier
- propertyEventConsumer demo

Config içinde açıkça dokümante edilmiş binding isimleri tercih et.

## Task 8 — Producer abstraction — PropertyService

Application/integration boundary oluştur:
- PublishPropertyEventPort.java

Infrastructure adapter oluştur:
- KafkaPropertyEventPublisher.java

Sorumluluklar:
- application/integration event mapping
- envelope metadata ekleme
- message key = propertyId ayarlama
- property.events'e publish etme

Domain Aggregate Kafka'yı bilmez.

Commit: feat(property): add property event publisher

## Task 9 — Producer abstraction — BuyerService

Oluştur:
- PublishOfferEventPort.java
- KafkaOfferEventPublisher.java

Day 19 Offer workflow henüz yoksa adapter application emission olmadan wired ve contract-tested olabilir.

Fake business trigger oluşturma.

Commit: feat(buyer): add offer event publisher

## Task 10 — StreamBridge vs Supplier kararı

Karar kuralı:
- application action ile tetiklenen event publication -> StreamBridge genellikle daha açıktır
- continuous/generated source -> Supplier

Seçimi dokümante et.

Imperative business event publication için Supplier'ı zorlamaya çalışma.

## Task 11 — Kafka message key

property.events key = propertyId  
offer.events key = offerId

Key producer headers/config içinde explicit olarak set edilmelidir.

Amaç:
- partition affinity
- per-aggregate ordering

## Task 12 — Consumer group isimleri

İlk group'lar:
- search-projection-group
- seller-offer-projection-group adayı

Day 16 minimal consumer gerçek bir logical group name kullanmalıdır.

Production-like config içinde random generated group ID kullanma.

## Task 13 — Minimal consumer foundation

Tercih edilen location:
- business/projection yapmadan kalabiliyorsa SearchService minimal property event consumer

Oluştur:
- eventconsumer/PropertyEventConsumer.java

Day 16 function rolü:
- deserialize
- envelope temel alanlarını validate et
- metadata logla
- no-op/test handler veya minimal technical handler çağır

Henüz Elasticsearch projection update etme.

No-op consumer yapay görünüyorsa alternatif:
yalnızca dedicated test consumer kullan ve Search consumer wiring'i Day 18'e ertele.

Fake production code oluşturmayan seçeneği tercih et.

Production consumer eklenirse commit:
feat(search): add property event consumer foundation

## Task 14 — Spring Cloud Function modeli

Consumer bean explicit ve küçük olmalıdır.

Örnek semantic:
- Consumer<PropertyPublishedEventEnvelope>

Business logic'i lambda içine gömme.

## Task 15 — Serialization

İlk format:
- JSON

Kurallar:
- explicit content type
- Java serialization yok
- event schema version field
- backward-compatible additive change tercih edilir

Avro/Schema Registry ertelenir.

## Task 16 — Correlation ve causation

Producer, varsa mevcut request/context üzerinden correlationId alır.

causationId:
- message-driven ise tetikleyen mevcut message/event id
- user-originated root event için null/absent

Consumer, pratik olduğu yerde correlation context'i restore/propagate eder.

Commit: feat(messaging): propagate event correlation metadata

## Task 17 — Basic duplicate-safe consumer foundation

Day 17 full Inbox/Idempotent Consumer kapsamına sahiptir.

Day 16 yalnızca design hook'larını zorunlu kılar:
- her event eventId taşır
- consumer handler API eventId alır
- side-effect yapan consumer duplicate delivery beklenerek yazılır

Henüz full processed-message store implemente etme.

## Task 18 — Stream configuration

External config ekle:
- brokers
- destinations
- consumer groups
- content type
- partition key expression/strategy
- başlangıçta concurrency default 1

Henüz aggressive retry config ekleme.

Commit: config(kafka): add Stream bindings and topic configuration

## Task 19 — Topic provisioning strategy

Seç:
- local learning için binder auto-provision
veya
- local infra içinde explicit topic creation

Önemli yerlerde production-like tercih explicit topic property'leridir.

Auto-provision kullanılsa bile beklenen partition sayısını dokümante et.

İlk partition sayısı:
- partitioning göstermek için yararlıysa küçük sabit sayı, örneğin 3

Gereksiz yüksek partition sayısı verme.

## Task 20 — Partition behavior testi

Oluştur:
- KafkaPartitioningIntegrationTest.java

Senaryo:
- aynı propertyId ile birden fazla event publish et -> aynı partition
- farklı propertyId'ler farklı partition'lara dağılabilir

Hash algorithm/config özellikle sabitlenmiyorsa exact partition number assert etme.

Commit: test(kafka): verify aggregate partitioning

## Task 21 — Producer integration testi

Oluştur:
- PropertyEventPublisherIntegrationTest.java
- anlamlıysa OfferEventPublisherIntegrationTest.java

Doğrula:
- doğru topic
- doğru key
- event envelope alanları
- JSON deserialization

Commit: test(kafka): add producer integration tests

## Task 22 — Consumer integration testi

Consumer foundation mevcutsa oluştur:
- PropertyEventConsumerIntegrationTest.java

Doğrula:
- consumer group receive ediyor
- payload deserialize oluyor
- correlation metadata erişilebilir

Commit: test(kafka): add consumer integration test

## Task 23 — Ordering testi

Aynı aggregate key için ordered event'ler publish et.

Tek partition içinde gözlenen sırayı doğrula.

Dokümante et:
ordering global değil, per partition'dır.

Commit: test(kafka): verify per-aggregate ordering

## Task 24 — Consumer group behavior testi

Aynı group içindeki iki instance'ın her event'i birlikte işlemek yerine partition/message'ları bölüştüğünü kanıtlayan test oluştur.

Test infrastructure complexity Day 16 için fazla yüksekse dokümante et ve hardening Day 22'ye ertele.

## Task 25 — Failure handling baseline

Day 16'da yalnızca:
- consumer exception görünür
- silent swallow yok
- infinite retry yok

Full retry topic/DLT policy Day 17'de.

## Task 26 — Observability baseline

Yalnızca metadata logla:
- eventId
- eventType
- aggregateId
- correlationId

Default olarak full payload loglama.

Full Kafka metrics Day 24'te.

## Task 27 — Security

Local Kafka auth basitlik için disabled ise bunun local-only olduğunu dokümante et.

Production-like authentication/TLS sonraki infrastructure hardening aşamasına bırakılabilir.

Application OAuth token ile broker authentication'ı birbirine karıştırma.

## Task 28 — RabbitMQ coexistence doğrulaması

Mevcut RabbitMQ command/work-queue flow'larının hâlâ çalıştığını doğrula.

Architecture kuralı:
- RabbitMQ command semantic korunur
- Kafka event semantic eklenir

Mevcut Auth -> UserProfile RabbitMQ flow'u migrate edilmez.

## Task 29 — Architecture testleri

Yararlı olduğu yerde şu rule'ları ekle:
- domain package'ları Kafka/Stream'e bağımlı değildir
- event publisher adapter'ları port'ları implement eder
- Search/Buyer/Property application layer'ları binder class'larına doğrudan bağımlı değildir

Service bazında messaging architecture testlerini oluştur/güncelle.

Commit: test(messaging): enforce broker adapter boundaries

## Task 30 — Dokümantasyon

Değiştir:
- docs/architecture/messaging-topology.md
- docs/contracts/command-event-catalog.md
- docs/roadmap/day-16-kafka-stream-function.md
- etkilenen service DESIGN/PACKAGE-DESIGN dokümanları

Gerçekte kullanılanları kaydet:
- Kafka image/version
- topic isimleri
- partition sayısı
- key strategy
- binding isimleri
- consumer groups
- serialization format
- correlation metadata
- RabbitMQ/Kafka semantic ayrımı

Commit: docs(kafka): finalize event-streaming topology

## Önerilen Commit Sırası

1. infra(kafka): add local Kafka broker
2. build(kafka): add Stream and Function dependencies
3. feat(messaging): add event envelope contracts
4. feat(property): add property event contracts
5. feat(buyer): add offer event contracts
6. feat(property): add property event publisher
7. feat(buyer): add offer event publisher
8. config(kafka): add Stream bindings and topic configuration
9. feat(messaging): propagate event correlation metadata
10. feat(search): add property event consumer foundation — yalnızca yapay değilse
11. test(kafka): add producer integration tests
12. test(kafka): verify aggregate partitioning
13. test(kafka): verify per-aggregate ordering
14. test(kafka): add consumer integration test — consumer varsa
15. test(messaging): enforce broker adapter boundaries
16. docs(kafka): finalize event-streaming topology

Küçükse bitişik event-contract commit'leri birleştirilebilir. Infrastructure, contracts, producer adapters, config, tests ve docs ayrı ayrı review edilebilir kalmalıdır.

## Day 16'dan Açıkça Ertelenenler

Implement etme:
- Inbox
- Outbox
- processed-message table
- retry topics
- DLT
- poison message policy
- full Search projection
- Saga
- seller offer projection
- Avro
- Schema Registry
- Kafka transactions

## Kritik Tasarım Notu — Stream, Kafka bilgisini gereksiz kılmaz

Spring Cloud Stream integration plumbing'i abstract eder; ancak aşağıdakileri anlamaya ihtiyaç devam eder:
- partitions
- keys
- consumer groups
- offsets
- ordering
- replay

## Kritik Tasarım Notu — Function modeli

Spring Cloud Function'ı consumer logic'i yapılandırmak için kullan; business rule'ları lambda içine gizlemek için değil.

## Kritik Tasarım Notu — Command vs Event

Kafka gerçekleşmiş fact'leri taşır.
RabbitMQ tasarlandığı şekilde targeted command/work taşımaya devam eder.

## Day 16 Final Gate

Day 16 yalnızca aşağıdakiler sağlanırsa kapanır:
- Kafka local olarak başlıyor
- property.events mevcut/çalışıyor
- offer.events contract mevcut
- event envelope metadata standardize edilmiş
- propertyId/offerId key strategy explicit
- en az bir gerçek producer flow doğrulanmış
- consumer group behavior/config explicit
- JSON serialization explicit
- mümkün olduğu yerde correlation/causation alanları dolduruluyor
- aynı aggregate key partition ordering'i koruyor
- domain/application layer'ları Kafka binder API'lerine doğrudan bağımlı değil
- RabbitMQ baseline hâlâ çalışıyor
- Outbox/Inbox/DLT/Saga/Search projection scope Day 16'ya sızmıyor
- docs gerçek implementation ile eşleşiyor

## Source-of-truth notu

Bu dosya final Day 15–33 roadmap'i izler. Önceki birleşik Day numaralandırması `docs/roadmap/LEGACY-DAY-MAPPING.md` ile superseded edilmiştir.
