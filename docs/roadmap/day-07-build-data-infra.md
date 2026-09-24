# Day 7 — Build & Local Data Infrastructure Foundation

## Goal
Day 8–13 service/datastore implementation'ları için build ve local infrastructure temelini hazırlamak.

## Tasks
1. Root Gradle/dependency yapısını audit et.
2. MySQL, Couchbase, Cassandra, MongoDB, Elasticsearch, Redis dependency alias'larını ekle.
3. SearchService module registration foundation ekle.
4. Root common dependency'leri yeniden değerlendir; gereksiz global JWT/OpenFeign gibi bağımlılıkları tespit et.
5. docker-compose credential literal'larını kaldır.
6. .env kullanımını ekle ve .gitignore güncelle.
7. .env.example ekle.
8. MySQL container ekle.
9. MongoDB container ekle.
10. Couchbase container ekle.
11. Cassandra container ekle.
12. Elasticsearch container ekle.
13. Redis container ekle.
14. Eski Agent/Buyer/Seller/Property PostgreSQL container'larını kaldır.
15. Auth/UserProfile PostgreSQL ve RabbitMQ baseline'ını smoke test et.
16. Container port/version/health bilgilerini dokümante et.

## Suggested commits
1. build: add polyglot datastore dependency aliases
2. build: register SearchService module foundation
3. build: reduce unnecessary global dependencies
4. infra: externalize local compose credentials
5. chore: add env example and gitignore rules
6. infra: add MySQL and MongoDB services
7. infra: add Couchbase and Cassandra services
8. infra: add Elasticsearch and Redis services
9. infra: remove temporary service PostgreSQL containers
10. test: verify baseline infrastructure startup
11. docs: document Day 7 local infrastructure

## Verification
- ./gradlew projects
- ./gradlew check
- docker compose config
- selected containers healthy
- Auth/UserProfile/RabbitMQ baseline still works

## Done
Build green, secret hygiene corrected, all Day 8–13 datastore foundations available, no business service implemented yet.

## Exact file-level plan

Implementation source of truth: `docs/roadmap/day-07-exact-file-plan.md`
