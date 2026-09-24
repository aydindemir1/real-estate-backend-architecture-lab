# ADR-004 — Elasticsearch as Search Projection

**Status:** Accepted

## Context
Real-estate search requires text search, structured filtering, facets, ranges, autocomplete and geo capabilities.

## Decision
PropertyService/MongoDB remains the source of truth. SearchService consumes property events and builds an Elasticsearch projection.

## Rationale
Elasticsearch is optimized for search, not canonical transactional ownership.

## Consequences
Search is eventually consistent. The index must be rebuildable from canonical property data or event history. Reindex/reconciliation becomes an explicit operational capability.
