# Day 9 Knowledge Index

Day 9, BuyerService'i Couchbase persistence ve Hexagonal Architecture ile application-level olarak implemente eder.

## Architecture

- [Hexagonal Architecture](../architectures/hexagonal-architecture.md) — Implemented / Verified

## Technology

- [Couchbase](../technologies/datastores/couchbase.md) — Implemented / Verified

## Scope expansion

Day 9 ayrıca şu konuları derinleştirir:
- Spring Data Couchbase
- deterministic document-key access
- domain / persistence model ayrımı
- inbound/outbound ports
- adapter mapping
- Testcontainers
- ArchUnit architecture fitness rules
- Config Client
- Eureka Client
- REST validation/error semantics

## Project-specific links

- `BuyerService/ROADMAP.md`
- `BuyerService/docs/DESIGN.md`
- `BuyerService/docs/PACKAGE-DESIGN.md`
- `docs/roadmap/day-09-buyer-couchbase-hexagonal.md`
- `docs/evidence/day-09/README.md`

## State note

Implementation, automated tests, CI ve lokal runtime doğrulamaları tamamlanmıştır.

CAS / optimistic concurrency bilinçli olarak ertelenmiştir ve ayrı decision document ile kayıt altındadır.

Day 9 **Completed / Verified** durumundadır.
