# Datastores

Relational, document, wide-column, search ve in-memory data teknolojileri.

## Mevcut durum

- [PostgreSQL](postgresql.md) — Uygulandı / Doğrulandı
- [MySQL](mysql.md) — Uygulandı
- [MongoDB](mongodb.md) — Infrastructure hazır
- [Couchbase](couchbase.md) — Uygulandı / Doğrulandı
- [Apache Cassandra](cassandra.md) — Uygulandı / Doğrulandı
- [Elasticsearch](elasticsearch.md) — Infrastructure hazır
- [Redis](redis.md) — Infrastructure hazır

## Ownership

- Auth -> PostgreSQL
- UserProfile -> PostgreSQL
- Agent -> MySQL
- Buyer -> Couchbase
- Seller -> Cassandra
- Property -> MongoDB
- Search -> Elasticsearch projection
- Redis -> cache/idempotency/rate limiting/ephemeral state

Elasticsearch ve Redis canonical business source of truth değildir.
