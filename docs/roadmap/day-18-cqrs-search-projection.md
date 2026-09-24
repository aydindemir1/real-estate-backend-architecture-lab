# Day 18 — CQRS + Elasticsearch Event Projection

## Goal
Property write model ile Search query modelini event-driven CQRS ile bağlamak.

## Tasks
1. PropertyPublished/Updated/PriceChanged/Withdrawn/Sold contracts finalize et.
2. PropertyService event publication noktalarını ekle.
3. SearchService consumer group ekle.
4. projection handlers oluştur.
5. idempotent projection update ekle.
6. delete/deactivate semantics ekle.
7. projection freshness timestamp ekle.
8. eventual consistency API/docs davranışını belirt.
9. event replay compatibility test et.
10. end-to-end Property -> Kafka -> Search test yaz.
11. failure/DLT projection test yaz.
12. docs güncelle.

## Suggested commits
1. docs(cqrs): finalize property event contracts
2. feat(property): publish property lifecycle events
3. feat(search): add projection consumers
4. feat(search): add idempotent projection handlers
5. feat(search): track projection freshness
6. test(cqrs): add end-to-end projection tests
7. docs(cqrs): document eventual consistency

## Done
Mongo canonical source olarak kalır; Elasticsearch projection Kafka event'leriyle güncellenir.
