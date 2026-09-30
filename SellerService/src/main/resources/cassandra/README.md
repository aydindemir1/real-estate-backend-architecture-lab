# SellerService Cassandra Schema Bootstrap

Day 10 için Cassandra şeması uygulama tarafından otomatik oluşturulmaz.

## Karar

- Şema version-controlled CQL ile yönetilir.
- Uygulamada `spring.cassandra.schema-action: none` kalır.
- Lokal bootstrap, Cassandra container içindeki `cqlsh` kullanılarak explicit çalıştırılır.
- Runtime başlamadan önce keyspace ve tablolar oluşturulmuş olmalıdır.
- Production-benzeri ortamlarda da aynı CQL kontrollü deployment/migration adımıyla uygulanmalıdır.
- `ALLOW FILTERING`, otomatik schema create ve runtime DDL kullanılmaz.

Kaynak şema:

`SellerService/src/main/resources/cassandra/schema/V1__seller_tables.cql`

## Lokal bootstrap

Önce yalnızca Seller Cassandra servisini başlat:

```powershell
docker compose --profile seller up -d seller-cassandra
```

Container healthy olduktan sonra şemayı uygula:

```powershell
Get-Content -Raw SellerService/src/main/resources/cassandra/schema/V1__seller_tables.cql |
  docker compose exec -T seller-cassandra cqlsh
```

Windows Command Prompt veya bash kullananlar için eşdeğer komut:

```text
docker compose exec -T seller-cassandra cqlsh < SellerService/src/main/resources/cassandra/schema/V1__seller_tables.cql
```

## Doğrulama

Keyspace ve tabloları doğrula:

```powershell
docker compose exec seller-cassandra cqlsh -e "DESCRIBE KEYSPACE seller_service;"
```

Beklenen tablolar:

- `seller_by_id`
- `listing_submissions_by_seller_and_month`

Listing tablosunun beklenen primary key tasarımı:

```text
PRIMARY KEY ((seller_id, year_month), created_at, submission_id)
CLUSTERING ORDER BY (created_at DESC, submission_id ASC)
```

## Test ortamı

Integration testlerde Cassandra Testcontainers kullanılacaktır. Test bootstrap kodu aynı version-controlled CQL dosyasını container ayağa kalktıktan sonra uygular; production kodunda schema auto-create açılmaz.

## Not

Local Config Server içindeki `seller-service.yml` Day 10 runtime aşamasından önce PostgreSQL/JPA ayarlarından Cassandra ayarlarına geçirilecektir. Bu dosya Task 24 kapsamındaki schema bootstrap kararından ayrı tutulmuştur.
