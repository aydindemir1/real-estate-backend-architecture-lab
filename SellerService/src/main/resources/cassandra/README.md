# SellerService Cassandra Schema Bootstrap

Day 10 için Cassandra şeması uygulama tarafından otomatik oluşturulmaz.

## Karar

- Şema version-controlled CQL ile yönetilir.
- Uygulamada `spring.cassandra.schema-action: none` kalır.
- Lokal bootstrap, Cassandra container içindeki `cqlsh` kullanılarak explicit çalıştırılır.
- Runtime başlamadan önce keyspace ve tablolar oluşturulmuş olmalıdır.
- Production-benzeri ortamlarda aynı CQL kontrollü deployment/migration adımıyla uygulanmalıdır.
- `ALLOW FILTERING`, otomatik schema create ve runtime DDL kullanılmaz.

Kaynak şema:

`SellerService/src/main/resources/cassandra/schema/V1__seller_tables.cql`

## Lokal bootstrap

```powershell
docker compose --profile seller up -d seller-cassandra
```

Container healthy olduktan sonra:

```powershell
Get-Content -Raw SellerService/src/main/resources/cassandra/schema/V1__seller_tables.cql |
  docker compose exec -T seller-cassandra cqlsh
```

Command Prompt / bash eşdeğeri:

```text
docker compose exec -T seller-cassandra cqlsh < SellerService/src/main/resources/cassandra/schema/V1__seller_tables.cql
```

## Doğrulama

```powershell
docker compose exec seller-cassandra cqlsh -e "DESCRIBE KEYSPACE seller_service;"
```

Beklenen tablolar:
- `seller_by_id`
- `listing_submissions_by_seller_and_month`

Beklenen primary key:

```text
PRIMARY KEY ((seller_id, year_month), created_at, submission_id)
CLUSTERING ORDER BY (created_at DESC, submission_id ASC)
```

## Test ortamı

Cassandra Testcontainers foundation aynı version-controlled CQL dosyasını bootstrap eder; production kodunda schema auto-create açılmaz.

## Day 10 runtime sonucu

Local Config Server `seller-service.yml` Cassandra ayarlarıyla çalıştırılmış ve SellerService gerçek Cassandra write/read akışıyla doğrulanmıştır.

Lokal Cassandra authentication kullanmadığı için boş username/password property'leri final config'te tutulmaz.
