# Day 9 — BuyerService: Couchbase + Hexagonal Architecture

## Durum

**Completed / Verified**

BuyerPreferences ve SavedSearch capability'si Couchbase üzerinde Hexagonal Architecture ile implemente edilmiş, automated tests/CI ve lokal runtime kabul testleriyle doğrulanmıştır.

## Gerçekleşen kapsam

1. BuyerService JPA/PostgreSQL persistence bağımlılıkları kaldırıldı.
2. Spring Data Couchbase eklendi.
3. Hexagonal package boundaries kuruldu.
4. Domain Value Object'leri oluşturuldu.
5. `BuyerPreferences` Aggregate oluşturuldu.
6. Invariant ve defensive copy kuralları uygulandı.
7. Inbound use-case port'ları oluşturuldu.
8. Outbound persistence port'ları oluşturuldu.
9. Application service implemente edildi.
10. Domain modelden ayrı Couchbase document modeli oluşturuldu.
11. Deterministic key stratejisi uygulandı.
12. Spring Data Couchbase repository oluşturuldu.
13. Persistence mapper ve adapter oluşturuldu.
14. Bucket/scope/collection bootstrap script'i version control altında tutuldu.
15. REST request/response contract'ları oluşturuldu.
16. REST controller ve mapper oluşturuldu.
17. Stable error mapping uygulandı.
18. Domain ve application testleri yazıldı.
19. Couchbase Testcontainers integration test altyapısı ve persistence testleri yazıldı.
20. REST controller testleri yazıldı.
21. ArchUnit ile Hexagonal Architecture sınırları executable hale getirildi.
22. CAS/optimistic concurrency bilinçli olarak ertelendi ve karar dokümante edildi.
23. GitHub Actions CI doğrulandı.
24. STS + Config Server + Eureka + Couchbase ile lokal runtime doğrulandı.
25. Postman success/error acceptance senaryoları geçti.
26. Runtime evidence PNG, startup log ve Postman collection olarak arşivlendi.

## Couchbase gerçek konfigürasyonu

- bucket: `buyer`
- scope: `buyer_service`
- collection: `preferences`
- document key: `buyer-preferences::{buyerId}`
- storage backend: `couchstore`
- secondary index: yok
- application role: `bucket_full_access[buyer]`

Credentials:
- `BUYER_DB_USERNAME`
- `BUYER_DB_PASSWORD`

Secret değerler repository'de tutulmaz.

## REST acceptance

| Endpoint | Sonuç |
|---|---:|
| PUT `/buyers/{buyerId}/preferences` | 200 |
| GET `/buyers/{buyerId}/preferences` | 200 |
| POST `/buyers/{buyerId}/saved-searches` | 201 |

Hata kabul senaryoları:
- 404 missing preferences
- 400 Bean Validation
- 422 domain semantic range
- 400 invalid UUID
- 400 malformed JSON

## Evidence

- `docs/evidence/day-09/README.md`
- `docs/evidence/day-09/png/`
- `docs/evidence/day-09/buyer-service-startup-success.log`
- `docs/collections/day-09/Day-09-BuyerService.postman_collection.json`

## CAS kararı

Day 9'da optimistic concurrency fake biçimde eklenmemiştir.

Detay:
- `docs/day-09/cas-concurrency-decision.md`

## Scope dışı

Day 9'a dahil değildir:
- Offer
- Saga
- Kafka
- gRPC
- Redis idempotency
- Keycloak authorization

## Completion gate

- [x] Couchbase persistence
- [x] framework-independent domain
- [x] inbound/outbound ports
- [x] deterministic key
- [x] Testcontainers persistence integration
- [x] REST API
- [x] stable error envelope
- [x] ArchUnit boundaries
- [x] Config Server
- [x] Eureka
- [x] lokal Couchbase write/read
- [x] Postman success/error acceptance
- [x] evidence arşivi
- [x] documentation update
