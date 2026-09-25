# Datastores

Relational, document, wide-column, search ve in-memory data teknolojileri.

## Day 1–7

- [PostgreSQL](postgresql.md) — Implemented / Verified
- [MySQL](mysql.md) — Infrastructure Ready
- [MongoDB](mongodb.md) — Infrastructure Ready
- [Couchbase](couchbase.md) — Infrastructure Ready
- [Apache Cassandra](cassandra.md) — Infrastructure Ready
- [Elasticsearch](elasticsearch.md) — Infrastructure Ready
- [Redis](redis.md) — Infrastructure Ready

## Project ownership

- Auth -> PostgreSQL
- UserProfile -> PostgreSQL
- Agent -> MySQL
- Buyer -> Couchbase
- Seller -> Cassandra
- Property -> MongoDB
- Search -> Elasticsearch derived projection
- Redis -> cache/idempotency/rate limiting/ephemeral state

## Canonical data rule

Elasticsearch ve Redis canonical business source of truth değildir.

Search index yeniden üretilebilir projection'dır. Redis ise acceleration/ephemeral infrastructure rolündedir.

## Day 7 scope

Day 7'de MySQL, MongoDB, Couchbase, Cassandra, Elasticsearch ve Redis local infrastructure olarak hazırlanmış ve healthcheck ile doğrulanmıştır.

Service-level persistence integration:
- Day 8 -> MySQL
- Day 9 -> Couchbase
- Day 10 -> Cassandra
- Day 11 -> MongoDB
- Day 12 -> Elasticsearch
- Day 13 -> Redis
