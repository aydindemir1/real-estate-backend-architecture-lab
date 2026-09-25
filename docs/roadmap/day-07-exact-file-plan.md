# Day 7 — Exact File / Task / Commit Plan

## 0. Scope

Day 7 yalnızca build ve local data infrastructure foundation'dır.

Business service implementation yapılmayacaktır.

Bu dosya Day 7 implementation sırasında primary source of truth'tur.

---

# Task 1 — Datastore dependency catalog

## Files

### Modify
`/dependencies.gradle`

## Exact changes

Yeni alias'lar:

- `springBootDataCouchbase`
- `springBootDataCassandra`
- `springBootDataMongoDb`
- `springBootDataElasticsearch`
- `springBootDataRedis`
- `mySql`
- `testcontainersJunitJupiter`

Gerekirse daha sonra service Day'lerinde datastore-specific test dependency eklenir.

## Rules

- Spring-managed starter version'larını mümkün olduğunca Boot dependency management'a bırak.
- MySQL JDBC version'ını mümkünse Boot BOM yönetsin.
- Testcontainers version governance ayrı central source üzerinden yapılmalı; Day 7 implementation sırasında mevcut Spring Boot compatibility doğrulanmalı.
- Dynamic version yok.

## Verification

```bash
./gradlew dependencies
```

veya daha dar:
```bash
./gradlew :AgentService:dependencies
```

## Commit

`build: add polyglot datastore dependency catalog`

---

# Task 2 — Root common dependency cleanup

## Files

### Modify
`/build.gradle`

## Current issue

Şu dependency'ler bütün subproject'lere zorunlu ekleniyor:

- Spring Web
- Swagger
- MapStruct
- Auth0 JWT
- OpenFeign

Bunların tamamı her module için gerçekten common değildir.

## Day 7 target

Root'ta gerçekten common olanlar tutulur.

Strong candidates to remain common:
- test starter
- JUnit launcher
- Lombok annotation processing
- MapStruct processor yalnız gerçekten genel strategy buysa yeniden değerlendirilir

Strong candidates to move to service modules:
- Auth0 JWT
- OpenFeign
- Swagger
- Web starter gerektiğinde

## Important constraint

Bu cleanup tek adımda bütün module'leri kırmamalıdır.

Önce dependency ownership matrix çıkarılır:

| Dependency | Modules that really need it |
|---|---|
| Web MVC | REST-facing application modules |
| OpenAPI | REST-facing modules |
| OpenFeign | only Feign clients |
| java-jwt | legacy Auth/security-related modules |
| MapStruct | modules with generated mapping |

Sonra root'tan yalnız güvenli olanlar çıkarılır ve ilgili service `build.gradle` dosyalarına taşınır.

## Files potentially modified

- `/build.gradle`
- `/AuthService/build.gradle`
- `/UserProfileService/build.gradle`
- `/AgentService/build.gradle`
- `/BuyerService/build.gradle`
- `/SellerService/build.gradle`
- `/PropertyService/build.gradle`
- `/ApiGatewayService/build.gradle`
- gerekirse diğer application module build files

## Verification

```bash
./gradlew check
```

## Commit

`build: clarify common and service-specific dependencies`

## Rule

Eğer cleanup scope beklenenden büyürse Day 7 içinde tek ayrı commit olarak tutulur; datastore container değişiklikleriyle karıştırılmaz.

---

# Task 3 — SearchService module foundation

## Files

### Modify
`/settings.gradle`

Add:

```gradle
include 'SearchService'
```

### Create
`/SearchService/build.gradle`

Day 7 minimum:

- Spring Boot application plugin root'tan gelir
- Config Client
- Eureka Client
- Actuator
- tracing baseline
- Elasticsearch dependency henüz alias hazırlandıktan sonra eklenebilir
- business search code yok

### Create
`/SearchService/src/main/java/com/aydindemir/search/SearchServiceApplication.java`

Minimum Spring Boot bootstrap class.

### Create
`/SearchService/src/main/resources/application.yml`

Minimum:

- `spring.application.name: search-service`
- Config Server import
- Config Server URL environment fallback

## No business code

Day 7'de oluşturulmayacak:
- SearchPropertiesHandler
- Elasticsearch repository
- controller
- document model

Bunlar Day 12.

## Verification

```bash
./gradlew projects
./gradlew :SearchService:compileJava
```

## Commit

`build(search): register SearchService module foundation`

---

# Task 4 — Secret/config hygiene

## Files

### Already correct, verify only
`/.gitignore`

Mevcut policy zaten şunları içeriyor:

```text
.env
.env.*
!.env.example
```

Bu nedenle gereksiz change yapılmaz.

### Create
`/.env.example`

Example-only values:

- POSTGRES_AUTH_DB
- POSTGRES_AUTH_USER
- POSTGRES_AUTH_PASSWORD
- POSTGRES_USER_PROFILE_DB
- POSTGRES_USER_PROFILE_USER
- POSTGRES_USER_PROFILE_PASSWORD
- RABBITMQ_USER
- RABBITMQ_PASSWORD
- MYSQL_AGENT_DATABASE
- MYSQL_AGENT_USER
- MYSQL_AGENT_PASSWORD
- COUCHBASE_ADMIN_USER
- COUCHBASE_ADMIN_PASSWORD
- CASSANDRA_USER if authentication enabled
- CASSANDRA_PASSWORD if authentication enabled
- MONGO_INITDB_ROOT_USERNAME if auth enabled
- MONGO_INITDB_ROOT_PASSWORD if auth enabled
- ELASTIC_PASSWORD only if security enabled in local profile

Exact variable set implementation sırasında selected local auth modes'a göre finalize edilir.

### Modify
`/docker-compose.yml`

### Also modify local Config Server files
- `/ConfigServerLocal/src/main/resources/config-repo/auth-service.yml`
- `/ConfigServerLocal/src/main/resources/config-repo/user-profile-service.yml`
- `/ConfigServerLocal/src/main/resources/config-repo/agent-service.yml`
- `/ConfigServerLocal/src/main/resources/config-repo/buyer-service.yml`
- `/ConfigServerLocal/src/main/resources/config-repo/seller-service.yml`
- `/ConfigServerLocal/src/main/resources/config-repo/property-service.yml`

Current `main` contains literal local fallback credentials in these config files, including PostgreSQL passwords, RabbitMQ credentials and an Auth JWT secret fallback. Day 7 secret hygiene therefore covers both Compose and Config Server local config.

Replace literal credentials with:

`${VARIABLE_NAME:-safe_local_example_or_required_behavior}`

Security-sensitive password için repository literal default bırakmamak tercih edilir.

## Important rule

Vault Day 21'e kadar local secret injection environment/.env üzerinden yapılabilir.

## Verification

```bash
docker compose config
```

Repository search:
- known literal password yok
- RabbitMQ literal credential yok
- committed JWT secret fallback yok
- Config Server local config contains no real/secret-like credential defaults

Non-secret local defaults such as host, port, database name and username may remain only when explicitly classified as non-secret.

## Commit

`infra: externalize local infrastructure credentials`

Then:

`chore: add environment example file`

Bunlar iki ayrı küçük commit olabilir.

---

# Task 5 — Add Agent MySQL container

## File

### Modify
`/docker-compose.yml`

## Remove later

Current:
`agent-postgres`

Bu task'ta önce MySQL eklenir; PostgreSQL removal ayrı cleanup commit'te yapılır.

## Add service

Logical name:
`agent-mysql`

Suggested container:
`java44-agent-mysql`

Configuration:
- explicit image version
- database from env
- user/password from env
- named volume
- unique host port
- healthcheck

## Volume

`agent_mysql_data`

## Verification

```bash
docker compose up -d agent-mysql
docker compose ps
```

Health check green.

## Commit

`infra: add AgentService MySQL container`

---

# Task 6 — Add Property MongoDB container

## File

`/docker-compose.yml`

## Add service

`property-mongodb`

Container:
`java44-property-mongodb`

Required:
- explicit version
- named volume
- unique host port
- healthcheck
- auth strategy explicit

## Volume

`property_mongodb_data`

## Verification

Container starts and ping/health works.

## Commit

`infra: add PropertyService MongoDB container`

---

# Task 7 — Add Buyer Couchbase container

## File

`/docker-compose.yml`

## Add service

`buyer-couchbase`

Container:
`java44-buyer-couchbase`

Need to define:
- explicit Couchbase version
- admin credentials via env
- ports only actually needed locally
- named volume
- healthcheck/readiness strategy

## Important note

Couchbase often needs cluster/bucket initialization after container startup.

Day 7 scope:
- container
- documented bootstrap command/script candidate

Day 9:
- actual bucket/scope/collection/index setup tied to BuyerService.

## Optional new file

If initialization is repeatable and simple:

`/infra/couchbase/init.sh`

Only create if needed; no premature script.

## Commit

`infra: add BuyerService Couchbase container`

---

# Task 8 — Add Seller Cassandra container

## File

`/docker-compose.yml`

## Add service

`seller-cassandra`

Container:
`java44-seller-cassandra`

Required:
- explicit version
- named volume
- unique local port
- healthcheck
- controlled startup period because Cassandra startup is slower

## Volume

`seller_cassandra_data`

## Day 7 scope

No CQL business tables yet.

Day 10 owns:
- keyspace
- seller_by_id
- listing_submissions_by_seller_and_month

## Commit

`infra: add SellerService Cassandra container`

---

# Task 9 — Add Elasticsearch container

## File

`/docker-compose.yml`

## Add service

`elasticsearch`

Container:
`java44-elasticsearch`

Local learning configuration:
- single-node
- explicit image version
- bounded JVM memory
- named volume
- healthcheck
- security mode explicit

## Volume

`elasticsearch_data`

## Day 7 scope

No index/mapping yet.

Day 12 owns:
- index
- mapping
- SearchService queries

## Commit

`infra: add Elasticsearch container`

---

# Task 10 — Add Redis container

## File

`/docker-compose.yml`

## Add service

`redis`

Container:
`java44-redis`

Required:
- explicit version
- named volume only if desired for local persistence semantics
- healthcheck using redis-cli ping
- unique local port

## Day 7 scope

No cache/idempotency/rate-limit code.

Day 13 owns Redis application foundation.

## Commit

`infra: add Redis container`

---

# Task 11 — Remove temporary service PostgreSQL containers

## File

`/docker-compose.yml`

## Remove services

- `agent-postgres`
- `buyer-postgres`
- `seller-postgres`
- `property-postgres`

## Remove volumes

- `agent_postgres_data`
- `buyer_postgres_data`
- `seller_postgres_data`
- `property_postgres_data`

## Preserve

- Auth PostgreSQL
- UserProfile PostgreSQL
- RabbitMQ

## Important

This removes local infrastructure definition, not service code dependency yet.

Service code datastore switch occurs Day 8–11 one service at a time.

## Verification

```bash
docker compose config
docker compose ps
```

## Commit

`infra: remove temporary service PostgreSQL containers`

---

# Task 12 — Compose grouping/resource strategy

## File

Primary:
`/docker-compose.yml`

Optional only if needed:
- compose profiles inside same file

## Decision

Because Couchbase + Cassandra + Elasticsearch together are resource-heavy, do not require every developer to run every Day 7 datastore simultaneously.

Preferred initial approach:

Use Docker Compose profiles:

- `core`
  - Auth PostgreSQL
  - UserProfile PostgreSQL
  - RabbitMQ

- `agent`
  - MySQL

- `buyer`
  - Couchbase

- `seller`
  - Cassandra

- `property`
  - MongoDB

- `search`
  - Elasticsearch

- `redis`
  - Redis

Alternative:
document selective `docker compose up service-name`.

Implementation should choose whichever remains simplest.

## Rule

Do not add extra compose files unless the single-file profile approach becomes unreadable.

## Commit

If profiles are used:

`infra: add selective local datastore profiles`

---

# Task 13 — Compose/config validation

## No production code

Run:

```bash
docker compose config
```

Start one datastore at a time.

Validate:
- health
- port
- volume
- credentials from env
- restart behavior

## Suggested validation sequence

1. core
2. agent-mysql
3. property-mongodb
4. redis
5. elasticsearch
6. buyer-couchbase
7. seller-cassandra

Resource-heavy services last.

## Commit

No commit if no code change.

If healthcheck fixes are required:

`fix(infra): correct datastore health checks`

---

# Task 14 — Baseline regression verification

## Commands

```bash
./gradlew check
```

And service-specific baseline startup/tests as currently available.

Must preserve:
- AuthService
- UserProfileService
- Config Servers
- Eureka
- ApiGateway
- RabbitMQ baseline

## Important

Day 7 should not alter business behavior.

## Commit

Only if regression fix needed:

`fix: preserve Day 1-6 baseline after infrastructure refactor`

---

# Task 15 — Documentation

## Modify

- `/docs/roadmap/day-07-build-data-infra.md`
- `/ROADMAP.md` only if implementation materially differs
- `/README.md` only if local startup instructions need update

## Create candidate

`/docs/infrastructure/local-data-services.md`

Recommended content:
- service
- Docker image/version
- port
- volume
- profile
- healthcheck
- owning application service
- whether canonical/projection/cache

## Commit

`docs: document local polyglot data infrastructure`

---

# Final Day 7 Commit Sequence

Recommended final sequence:

1. `build: add polyglot datastore dependency catalog`
2. `build: clarify common and service-specific dependencies`
3. `build(search): register SearchService module foundation`
4. `infra: externalize local infrastructure credentials`
5. `chore: add environment example file`
6. `infra: add AgentService MySQL container`
7. `infra: add PropertyService MongoDB container`
8. `infra: add BuyerService Couchbase container`
9. `infra: add SellerService Cassandra container`
10. `infra: add Elasticsearch container`
11. `infra: add Redis container`
12. `infra: remove temporary service PostgreSQL containers`
13. `infra: add selective local datastore profiles` — only if selected
14. `fix(infra): correct datastore health checks` — only if necessary
15. `docs: document local polyglot data infrastructure`

No artificial commit is created if a task causes no file change.

---

# Exact Day 7 Files

## Definitely modify

- `settings.gradle`
- `build.gradle` — after dependency ownership audit
- `dependencies.gradle`
- `docker-compose.yml`
- `ConfigServerLocal/src/main/resources/config-repo/*.yml` secret-bearing files

## Definitely create

- `.env.example`
- `SearchService/build.gradle`
- `SearchService/src/main/java/com/aydindemir/search/SearchServiceApplication.java`
- `SearchService/src/main/resources/application.yml`
- `docs/infrastructure/local-data-services.md`

## Verify but likely do not modify

- `.gitignore`

## Potentially modify depending dependency ownership audit

- service `build.gradle` files that need Web/OpenAPI/OpenFeign/JWT moved from root

## Explicitly do not create on Day 7

- Agent domain classes
- Buyer domain classes
- Seller domain classes
- Property domain classes
- Search document/repository/controller
- Redis application adapter
- Kafka code
- Keycloak code
- gRPC code
- GraphQL code

---

# Day 7 Final Gate

Day 7 closes only when:

- root Gradle configuration resolves
- SearchService is recognized as a module
- no real/local credential literal remains committed in compose or Config Server local config
- `.env.example` exists
- MySQL starts
- MongoDB starts
- Couchbase starts
- Cassandra starts
- Elasticsearch starts
- Redis starts
- temporary Agent/Buyer/Seller/Property PostgreSQL compose services are gone
- Auth/UserProfile PostgreSQL still works
- RabbitMQ still works
- Day 1–6 baseline build/tests remain green
- no Day 8+ business implementation leaked into Day 7
