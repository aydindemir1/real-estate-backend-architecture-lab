# Day 16 — Kafka + Spring Cloud Stream + Spring Cloud Function

## Goal
Domain Event Streaming foundation'ını Kafka ile kurmak.

## Tasks
1. Kafka local infra ekle.
2. Stream/Function dependencies ekle.
3. property.events / offer.events topic strategy config ekle.
4. common event envelope model oluştur.
5. Property event publisher abstraction/adaptor oluştur.
6. Offer event publisher abstraction/adaptor oluştur.
7. functional producer bindings ekle.
8. minimal consumer örneği oluştur.
9. propertyId / offerId partition key uygula.
10. consumer groups tanımla.
11. correlation/causation propagation ekle.
12. duplicate-safe consumer foundation ekle.
13. Kafka Testcontainers tests yaz.
14. partition/order/consumer group tests yaz.
15. docs güncelle.

## Suggested commits
1. infra(kafka): add local Kafka
2. build(kafka): add Stream and Function dependencies
3. feat(messaging): add event envelope contracts
4. feat(property): add property event publisher
5. feat(buyer): add offer event publisher
6. feat(messaging): add functional consumer foundation
7. test(kafka): add partition and consumer integration tests
8. docs(kafka): document event-streaming topology

## Done
Kafka event-streaming foundation çalışır; RabbitMQ command role korunur.

## Exact messaging/file plan

Implementation source of truth: `docs/roadmap/day-16-exact-file-plan.md`
