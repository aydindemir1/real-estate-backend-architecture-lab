# Day 7 — Polyglot Persistence & Architecture Foundations

**Status:** Ready for implementation  
**Implementation branch:** `day/07-polyglot-persistence`

## Amaç

Day 1–6 baseline capability'lerini yeniden uygulamadan Polyglot Persistence ve service-specific Architecture foundation'larını kurmak.

## Scope

- AgentService -> MySQL + Clean Architecture
- BuyerService -> Couchbase + Hexagonal Architecture
- SellerService -> Cassandra + Onion Architecture
- PropertyService -> MongoDB + Vertical Slice Architecture
- SearchService -> Elasticsearch + Vertical Slice / CQRS Query Side foundation
- Redis -> infrastructure foundation

## Explicitly deferred

- Keycloak
- gRPC / GraphQL
- Kafka / Stream / Function
- Outbox / Inbox
- Saga
- advanced resilience
- Vault / Bus
- full observability stack
- reindex/reconciliation job

SellerService -> PropertyService RabbitMQ publication Day 7'de aktive edilmez; reliable publication strategy Day 11 öncesi finalize edilir.

## Exact implementation source of truth

Detaylı class/package, dependency/config, datastore, test ve Definition of Done planı:

`docs/roadmap/day-07-exact-implementation-plan.md`

## Day 7 principle

Foundation milestone olduğu için target end-state'in bütün class/use-case'leri bir anda implemente edilmez. Her service için Architecture + datastore kombinasyonunu kanıtlayan minimum gerçek vertical path uygulanır.

## Definition of Done

Exact plan içindeki Definition of Done maddelerinin tamamı karşılanmalıdır.
