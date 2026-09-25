# Day 7 — Build & Local Data Infrastructure Foundation

Day 7'nin amacı business implementation yapmak değil; sonraki service günleri için build ownership, polyglot persistence altyapısı ve kontrollü local development ortamını hazırlamaktır.

## Tamamlanan çalışmalar

### Build ve dependency ownership

- Polyglot datastore dependency alias'ları merkezi dependency catalog'a eklendi.
- Root `build.gradle` içindeki her module'e zorla uygulanan Web, OpenAPI, MapStruct, JWT ve OpenFeign dependency'leri ilgili service module'lerine taşındı.
- `SearchService` minimum Spring Boot module foundation olarak eklendi.
- Root Gradle project adı `real-estate-backend-architecture-lab` olarak normalize edildi.

### Secret ve configuration hygiene

- Local credential ve secret değerleri repository dışındaki `.env` dosyasına taşındı.
- `.env.example` version control altında örnek contract olarak tutulur.
- Config Server local repository içindeki password/JWT secret literal fallback'leri kaldırıldı.
- Gerçek `.env` dosyası Git tarafından ignore edilir.

### Polyglot local data infrastructure

| Owning service | Technology | Docker service | Host port | Role |
|---|---|---|---:|---|
| AuthService | PostgreSQL 18.6 | `postgres` | 5433 | Canonical |
| UserProfileService | PostgreSQL 18.6 | `user-profile-postgres` | 5434 | Canonical |
| AgentService | MySQL 8.4.11 | `agent-mysql` | 3307 | Canonical target |
| BuyerService | Couchbase Community 8.0.2 | `buyer-couchbase` | 8091 / 11210 | Canonical target |
| SellerService | Cassandra 5.0.9 | `seller-cassandra` | 9042 | Canonical target |
| PropertyService | MongoDB 8.0.30 | `property-mongodb` | 27018 | Canonical target |
| SearchService | Elasticsearch 9.5.4 | `elasticsearch` | 9200 | Derived CQRS query projection |
| Shared infrastructure | Redis 8.2.1 | `redis` | 6379 | Cache / idempotency / rate limiting |
| Messaging baseline | RabbitMQ 4.3.6 Management | `rabbitmq` | 5672 / 15672 | Command/work queue |

Agent, Buyer, Seller ve Property için Day 1–6'dan kalan geçici PostgreSQL Compose servisleri kaldırıldı. Application-level persistence migration'ları Day 8–11 arasında service service yapılacaktır.

## Docker Compose profiles

Local makinede Cassandra, Couchbase ve Elasticsearch gibi ağır servislerin gereksiz yere aynı anda çalışmasını önlemek için profile tabanlı çalışma modeli kullanılır.

| Profile | Services |
|---|---|
| `core` | Auth PostgreSQL, UserProfile PostgreSQL, RabbitMQ |
| `agent` | MySQL |
| `buyer` | Couchbase |
| `seller` | Cassandra |
| `property` | MongoDB |
| `search` | Elasticsearch |
| `redis` | Redis |

Örnek:

```powershell
docker compose --profile core up -d
docker compose --profile agent up -d
docker compose --profile property --profile search up -d
```

Profile verilmeden `docker compose config` çalıştırıldığında `services: {}` görülmesi beklenen davranıştır.

Tüm profile configuration'larını doğrulamak için:

```powershell
docker compose --profile core --profile agent --profile buyer --profile seller --profile property --profile search --profile redis config
```

## Day 7 validation

Aşağıdaki datastore container'ları ayrı ayrı başarıyla başlatılıp healthcheck ile doğrulandı:

- MySQL
- MongoDB
- Couchbase
- Cassandra
- Elasticsearch
- Redis

Core profile altında Auth PostgreSQL, UserProfile PostgreSQL ve RabbitMQ birlikte `healthy` olarak doğrulandı.

Gradle regression:

```powershell
.\gradlew.bat check
.\gradlew.bat compileJava
```

Her iki build doğrulaması da başarılıdır.

## Bilinçli olarak Day 7 dışında bırakılanlar

Day 7'de business/domain implementation yapılmadı.

- AgentService -> MySQL persistence migration: Day 8
- BuyerService -> Couchbase persistence migration: Day 9
- SellerService -> Cassandra persistence migration: Day 10
- PropertyService -> MongoDB persistence migration: Day 11
- SearchService -> Elasticsearch query model/index/mapping: Day 12
- Redis application integration: Day 13

Bu ayrım sayesinde Day 7 yalnızca build ve local infrastructure foundation olarak kalır.
