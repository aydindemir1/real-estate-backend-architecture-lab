# Day 13 — Redis Infrastructure Foundation

## Goal
Redis'i canonical datastore olmadan ortak ephemeral infrastructure capability olarak hazırlamak.

## Tasks
1. Redis dependency alias/config ekle.
2. Redis container health doğrula.
3. typed Redis config oluştur.
4. serializer seçimini netleştir.
5. key namespace standardını kod/dokümana yansıt.
6. TTL convention oluştur.
7. minimal Redis adapter/helper sadece connectivity/ephemeral foundation için oluştur.
8. connectivity integration test yaz.
9. TTL expiry test yaz.
10. serialization round-trip test yaz.
11. Redis'in canonical source olmadığını architecture test/docs ile doğrula.
12. Day 14+ kullanım noktalarını yalnız interface/roadmap seviyesinde belirt.
13. docs güncelle.

## Suggested commits
1. build(redis): add Redis dependency
2. feat(redis): add typed Redis configuration
3. feat(redis): add key and serialization foundation
4. test(redis): add connectivity and TTL integration tests
5. docs(redis): document Redis role and conventions

## Verification
- connect/read/write
- TTL expires
- serializer deterministic
- no business aggregate owned by Redis

## Done
Redis foundation hazır; idempotency/rate-limit/cache henüz gereksiz yere implemente edilmemiş.
