# Datastores

Relational, document, wide-column, search ve in-memory data teknolojileri.

## Current state

- [PostgreSQL](postgresql.md) — Implemented / Verified
- [MySQL](mysql.md) — Implemented
- [MongoDB](mongodb.md) — Infrastructure Ready
- [Couchbase](couchbase.md) — Implemented / Verified
- [Apache Cassandra](cassandra.md) — Implemented / Verified
- [Elasticsearch](elasticsearch.md) — Infrastructure Ready
- [Redis](redis.md) — Infrastructure Ready

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
