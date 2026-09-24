# ADR-002 — Polyglot Persistence

**Status:** Accepted

## Decision
Use distinct datastore models where each teaches a different backend problem:

- MySQL for AgentService
- Couchbase for BuyerService
- Cassandra for SellerService
- MongoDB for PropertyService
- Elasticsearch for SearchService
- Redis for cross-cutting infrastructure

## Rationale
The project is an education/portfolio laboratory, but technologies must map to realistic responsibilities instead of being added as checkboxes.

## Consequences
Testing and local infrastructure become more complex. Testcontainers and clear service boundaries are therefore mandatory later in the roadmap.
