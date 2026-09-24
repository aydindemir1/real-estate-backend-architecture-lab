# ADR-004 — Elasticsearch as Search Projection

**Status:** Accepted

## Context
Real-estate search; full-text search, structured filtering, facet, range, autocomplete ve geo query gerektirir.

## Decision
PropertyService/MongoDB source of truth olarak kalacaktır. SearchService property event'lerini tüketip Elasticsearch projection oluşturacaktır.

## Rationale
Elasticsearch transactional canonical ownership için değil search workload için optimize edilmiştir.

## Consequences
Search data Eventual Consistency ile güncellenir. Index, canonical property data veya event history üzerinden yeniden üretilebilir olmalıdır. Reindex ve reconciliation operational capability olarak ele alınacaktır.
