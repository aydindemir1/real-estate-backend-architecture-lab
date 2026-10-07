# Local Data Services — Yerel Veri Servisleri

Bu doküman local development sırasında kullanılan veri ve messaging container'larının kısa operasyonel referansıdır.

## Service matrisi

| Profile | Docker service | Container | Image | Host port(lar) | Amaç |
|---|---|---|---|---|---|
| core | postgres | real-estate-auth-postgres | postgres:18.6 | 5433 | Auth canonical PostgreSQL |
| core | user-profile-postgres | real-estate-user-profile-postgres | postgres:18.6 | 5434 | UserProfile canonical PostgreSQL |
| core | rabbitmq | real-estate-rabbitmq | rabbitmq:4.3.6-management | 5672, 15672 | RabbitMQ baseline |
| agent | agent-mysql | real-estate-agent-mysql | mysql:8.4.11 | 3307 | Agent canonical target |
| buyer | buyer-couchbase | real-estate-buyer-couchbase | couchbase:community-8.0.2 | 8091, 11210 | Buyer canonical target |
| seller | seller-cassandra | real-estate-seller-cassandra | cassandra:5.0.9 | 9042 | Seller canonical target |
| property | property-mongodb | real-estate-property-mongodb | mongo:8.0.30 | 27018 | Property canonical target |
| search | elasticsearch | real-estate-elasticsearch | docker.elastic.co/elasticsearch/elasticsearch:9.5.4 | 9200 | Search CQRS projection |
| redis | redis | real-estate-redis | redis:8.2.1 | 6379 | Ephemeral cache/idempotency/rate limiting |

## Resource stratejisi

Bütün datastore'ları aynı anda çalıştırmak local development standardı değildir. İlgili use case veya integration test için gereken minimum profile set'i açılır.

Örnekler:

```powershell
docker compose --profile core up -d
docker compose --profile agent up -d
docker compose --profile property --profile search up -d
docker compose --profile buyer --profile seller --profile property up -d
```

İş bittiğinde CPU/RAM'i serbest bırakmak için ilgili profile servisleri durdurulabilir:

```powershell
docker compose --profile seller stop
```

Durdurulmuş container'lar CPU/RAM tüketmez; image ve volume'lar disk üzerinde kalır.

## Konfigürasyon

Credential ve local secret değerleri repository'ye commit edilmez. `.env.example` template olarak kullanılır:

```powershell
Copy-Item .env.example .env
```

Ardından local `.env` değerleri doldurulur.

## Doğrulama

Bir profile'ın service resolution'ını kontrol etmek için:

```powershell
docker compose --profile seller config --services
```

Tüm Compose modelini parse etmek için:

```powershell
docker compose --profile core --profile agent --profile buyer --profile seller --profile property --profile search --profile redis config
```

Runtime health kontrolü:

```powershell
docker compose --profile core ps
docker compose ps agent-mysql
docker compose ps property-mongodb
```

## Data Ownership kuralı

Redis canonical datastore değildir. Elasticsearch de Property aggregate'ın source of truth'u değildir; SearchService için derived query projection'dır.

Canonical target ownership dağılımı:

- Auth -> PostgreSQL
- UserProfile -> PostgreSQL
- Agent -> MySQL
- Buyer -> Couchbase
- Seller -> Cassandra
- Property -> MongoDB
- Search -> Elasticsearch derived projection
