# Yerel Veri Servisleri

Bu doküman local development sırasında kullanılan veri ve messaging container'larının kısa operasyonel referansıdır.

## Servis matrisi

| Profile | Docker servisi | Container | Image | Host port(lar) | Amaç |
|---|---|---|---|---|---|
| core | postgres | real-estate-auth-postgres | postgres:18.6 | 5433 | Auth canonical PostgreSQL |
| core | user-profile-postgres | real-estate-user-profile-postgres | postgres:18.6 | 5434 | UserProfile canonical PostgreSQL |
| core | rabbitmq | real-estate-rabbitmq | rabbitmq:4.3.6-management | 5672, 15672 | RabbitMQ baseline |
| agent | agent-mysql | real-estate-agent-mysql | mysql:8.4.11 | 3307 | Agent canonical target |
| buyer | buyer-couchbase | real-estate-buyer-couchbase | couchbase:community-8.0.2 | 8091, 11210 | Buyer canonical target |
| seller | seller-cassandra | real-estate-seller-cassandra | cassandra:5.0.9 | 9042 | Seller canonical datastore |
| seller | cassandra-admin | real-estate-cassandra-admin | local build | 8002 | Cassandra browser inspection UI |
| property | property-mongodb | real-estate-property-mongodb | mongo:8.0.30 | 27018 | Property canonical target |
| search | elasticsearch | real-estate-elasticsearch | docker.elastic.co/elasticsearch/elasticsearch:9.5.4 | 9200 | Search CQRS projection |
| redis | redis | real-estate-redis | redis:8.2.1 | 6379 | Ephemeral cache/idempotency/rate limiting |

## Kaynak stratejisi

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

Stopped container'lar CPU/RAM tüketmez; image ve volume'lar disk üzerinde kalır.

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

Runtime health:

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

## Buyer Couchbase bootstrap

Day 9 BuyerService canonical local Couchbase layout:

| Resource | Name |
|---|---|
| Bucket | `buyer` |
| Scope | `buyer_service` |
| Collection | `preferences` |
| Document key | `buyer-preferences::{buyerId}` |

Container ayağa kalktıktan sonra cluster ve logical data structure bootstrap işlemi version-controlled PowerShell script ile yapılır:

```powershell
docker compose --profile buyer up -d buyer-couchbase
./infra/couchbase/bootstrap-buyer.ps1
```

Script idempotent olacak şekilde tasarlanmıştır:

1. cluster daha önce initialize edilmemişse initialize eder,
2. `buyer` bucket yoksa oluşturur,
3. `buyer_service` scope yoksa oluşturur,
4. `preferences` collection yoksa oluşturur,
5. mevcut resource'ları yeniden oluşturmaya çalışmaz.

Local tek-node development için bucket replica sayısı `0` tutulur.

Day 9 persistence erişimi deterministic document key ile yapıldığı için secondary index oluşturulmaz. Yeni bir index ancak gerçek bir query pattern ortaya çıktığında eklenmelidir.

Admin credential değerleri script içine yazılmaz. Script çalışan container içindeki `COUCHBASE_ADMIN_USERNAME` ve `COUCHBASE_ADMIN_PASSWORD` environment değerlerini kullanır.


## Seller Cassandra bootstrap ve Cassandra Admin

Day 10 SellerService canonical local Cassandra modeli:

| Resource | Değer |
|---|---|
| Image | `cassandra:5.0.9` |
| Keyspace | `seller_service` |
| Seller table | `seller_by_id` |
| Listing table | `listing_submissions_by_seller_and_month` |
| CQL port | `9042` |
| Cassandra Admin | `http://localhost:8002` |

Seller profile:

```powershell
docker compose --profile seller up -d seller-cassandra cassandra-admin
```

Schema source of truth:

`SellerService/src/main/resources/cassandra/schema/V1__seller_tables.cql`

Lokal bootstrap:

```powershell
Get-Content -Raw SellerService/src/main/resources/cassandra/schema/V1__seller_tables.cql |
  docker compose exec -T seller-cassandra cqlsh
```

Doğrulama:

```powershell
docker compose exec seller-cassandra cqlsh -e "DESCRIBE KEYSPACE seller_service;"
```

Listing query modeli exact partition erişimine dayanır:

```text
partition key: (seller_id, year_month)
clustering:    created_at DESC, submission_id ASC
```

Day 10 flow'unda `ALLOW FILTERING` ve cross-partition scan kullanılmaz.

### Cassandra Admin notu

`cassandra-admin` lokal inspection kolaylığı için kullanılır; source of truth değildir.

Day 10 acceptance sırasında:
- keyspace ve tablolar görüntülendi,
- `seller_by_id` row'u görüntülendi,
- listing table görünümünde stale/empty rendering gözlendi.

Bu nedenle listing persistence doğrudan `cqlsh` partition query ile doğrulandı.
