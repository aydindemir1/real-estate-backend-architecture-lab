# Day 7 — Build & Local Data Infrastructure Foundation

**Status:** Planned  
**Implementation branch:** `day/07-build-data-infra`

## Amaç

Day 8–13 service/datastore milestone'ları için ortak build ve local infrastructure temelini hazırlamak.

Bu Day içinde business service implementation yapılmaz.

## Scope

- Gradle datastore dependency alias'ları
- SearchService module registration foundation
- Docker Compose credential hygiene
- `.env` / `.env.example`
- MySQL container
- Couchbase container
- Cassandra container
- MongoDB container
- Elasticsearch container
- Redis container
- eski Agent/Buyer/Seller/Property temporary PostgreSQL container'larının kaldırılması
- Auth/UserProfile PostgreSQL ve RabbitMQ baseline'ının korunması

## Explicitly out of scope

- Agent domain/use-case implementation -> Day 8
- Buyer implementation -> Day 9
- Seller implementation -> Day 10
- Property implementation -> Day 11
- Search query implementation -> Day 12
- Redis business capability -> Day 13+

## Small commit policy

Day 7 de tek devasa commit olmayacaktır.

Candidate commits:

1. `build: add polyglot datastore dependency aliases`
2. `build: register SearchService module foundation`
3. `infra: externalize local compose credentials`
4. `infra: add MySQL and MongoDB containers`
5. `infra: add Couchbase and Cassandra containers`
6. `infra: add Elasticsearch and Redis containers`
7. `infra: remove temporary service PostgreSQL containers`
8. `docs: update Day 7 infrastructure documentation`

## Definition of Done

- Gradle Wrapper build halen çalışıyor
- Java 21 toolchain korunuyor
- dependency definitions compile edilebilir
- local credential literal repository'den kaldırılmış
- `.env` gitignored
- `.env.example` mevcut
- datastore image version'ları explicit
- container health/startup documented
- Auth/UserProfile/RabbitMQ baseline bozulmamış
- hiçbir Day 8–13 business service capability yanlışlıkla Day 7'de implement edilmemiş
