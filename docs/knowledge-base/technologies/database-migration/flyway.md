# Flyway

**Kategori:** Technology  
**İlk eklendiği gün:** Day 8  
**Proje durumu:** Uygulandı  
**Kapsam:** Relational schema migration standardı; ilk application AgentService

## Nedir?

Flyway, relational database schema değişikliklerini versioned migration dosyalarıyla kontrollü biçimde uygulayan database migration aracıdır.

## Bu projede

AgentService ilk migration:

`db/migration/V1__create_agents_table.sql`

Naming convention:

```text
V<version>__<description>.sql
```

Flyway migration history'yi `flyway_schema_history` tablosunda izler.

## Startup flow

```text
Datasource
  -> Flyway scans migrations
  -> pending migrations run
  -> schema history updated
  -> Hibernate validates mapping/schema
```

Hibernate schema üretmez; `ddl-auto=validate` kullanılır.

## Kurallar

- Project-wide relational migration standardı Flyway'dir.
- AgentService içinde Liquibase ile karıştırılmaz.
- Uygulanmış migration değiştirilmek yerine yeni migration eklenir.

## Failure modes

- checksum mismatch
- failed migration
- incompatible schema change
- application/schema version mismatch
- privilege/configuration problemi

## İleri konular

- baseline
- repair
- repeatable migrations
- expand/contract
- zero-downtime schema evolution
- CI migration validation
