# BuyerService Roadmap

## Amaç

BuyerService, buyer preferences ve saved search capability'sinin ownership'ini taşır.

Day 9 itibarıyla servis **Couchbase + Hexagonal Architecture** ile implemente edilmiş ve lokal runtime'da doğrulanmıştır.

## Day 9 — Tamamlandı / Doğrulandı

### Architecture
- Hexagonal Architecture
- framework-independent domain
- inbound port / outbound port ayrımı
- REST inbound adapter
- Couchbase outbound persistence adapter
- ArchUnit architecture fitness kuralları

### Domain
- `BuyerId`
- `PriceRange`
- `RoomRange`
- `AreaRange`
- `LocationPreference`
- `NotificationSettings`
- `SavedSearch`
- `BuyerPreferences` Aggregate

### Application
- `UpdateBuyerPreferencesUseCase`
- `GetBuyerPreferencesUseCase`
- `AddSavedSearchUseCase`
- `SaveBuyerPreferencesPort`
- `LoadBuyerPreferencesPort`
- `BuyerPreferencesApplicationService`

### Persistence
- Couchbase Community 8.0.2
- bucket: `buyer`
- scope: `buyer_service`
- collection: `preferences`
- deterministic document key: `buyer-preferences::{buyerId}`
- direct key lookup
- Day 9'da secondary index yok
- application credential contract: `BUYER_DB_USERNAME` / `BUYER_DB_PASSWORD`

### REST API
- `PUT /buyers/{buyerId}/preferences`
- `GET /buyers/{buyerId}/preferences`
- `POST /buyers/{buyerId}/saved-searches`

### Test ve doğrulama
- domain unit testleri
- application testleri + in-memory fake outbound port
- Couchbase Testcontainers integration testleri
- REST controller testleri
- ArchUnit Hexagonal Architecture testleri
- GitHub Actions CI
- STS üzerinden lokal runtime
- Config Server
- Eureka registration
- gerçek Couchbase write/read
- Postman success/error acceptance senaryoları

Kanıtlar:
- `docs/evidence/day-09/`
- `docs/collections/day-09/Day-09-BuyerService.postman_collection.json`

## Bilinçli olarak ertelenen

### CAS / optimistic concurrency
Day 9'da fake veya eksik concurrency koruması eklenmedi.

Karar:
- `docs/day-09/cas-concurrency-decision.md`

Mevcut Hexagonal port contract CAS token'ını domain/application boyunca taşımadığı için `@Version` eklemek tek başına doğru optimistic concurrency sağlamayacaktır.

## Day 9 kapsamı dışında kalanlar

Aşağıdakiler Day 9 implementation'ı değildir:

- Offer Aggregate / Offer persistence
- Saga
- Kafka
- gRPC
- Redis idempotency
- Keycloak authorization
- ileri distributed workflow'lar

Bu konular ilgili sonraki Day'lerde ayrıca ele alınacaktır.

## Detaylı dokümanlar

- `docs/DESIGN.md`
- `docs/PACKAGE-DESIGN.md`
- `../docs/roadmap/day-09-buyer-couchbase-hexagonal.md`
