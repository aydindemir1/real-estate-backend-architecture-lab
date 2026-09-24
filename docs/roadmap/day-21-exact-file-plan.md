# Day 21 — Exact CQRS + Elasticsearch Event Projection Plan

## Scope
- Property lifecycle events through reliable publication
- Search Kafka consumer group
- Elasticsearch projection handlers
- idempotency + stale-event protection
- projection freshness
- eventual consistency
- E2E publish-to-search

## Tasks

1. Finalize Search-relevant events: PropertyPublished, PropertyUpdated, PropertyPriceChanged, PropertyWithdrawn, PropertySold as actually implemented.
2. Keep event payload minimal and consumer-oriented; do not serialize Mongo documents blindly.
3. Wire Property state changes to the Day 18 Outbox reliable publication path.
4. Configure Search binding: `property.events`, group `search-projection-group`.
5. Create `PropertyProjectionConsumer` that delegates to handlers.
6. Create active handlers only:
   - PropertyPublishedProjectionHandler
   - PropertyUpdatedProjectionHandler
   - PropertyPriceChangedProjectionHandler
   - PropertyWithdrawnProjectionHandler
   - PropertySoldProjectionHandler
7. Add semantic projection repository operations: upsert, updatePrice, updateStatus/deactivate.
8. Keep terminal documents with status; public search initially returns only PUBLISHED.
9. Deduplicate by eventId using inbox/processed-message semantics.
10. Add `sourceVersion`, `sourceUpdatedAt`, `projectionUpdatedAt`, `lastEventOccurredAt`.
11. Reject/no-op stale lower-version/older events.
12. Ensure replay determinism.
13. Add Kafka+Elasticsearch integration test.
14. Add Property publish → Outbox → Kafka → Elasticsearch → Search E2E.
15. Add duplicate event test.
16. Add stale/out-of-order event test.
17. Add projection failure→DLT test.
18. Expose low-cardinality freshness/failure metric hooks for Day 29.
19. Document eventual consistency semantics.
20. Add ArchUnit rule: Search projection does not synchronously call PropertyService to build projection.

## Commit sequence
1. `docs(cqrs): finalize Search-relevant Property events`
2. `feat(property): publish lifecycle events through outbox`
3. `feat(search): add Property projection consumer`
4. `feat(search): add projection handlers`
5. `feat(search): make projection consumers idempotent`
6. `feat(search): guard projection against stale events`
7. `feat(search): track projection freshness metadata`
8. `feat(search): enforce searchable property statuses`
9. `test(cqrs): add projection integration tests`
10. `test(cqrs): add Property-to-Search E2E flow`
11. `test(cqrs): verify replay and stale-event safety`
12. `docs(cqrs): document eventual consistency`

## Final gate
- MongoDB remains source of truth
- Elasticsearch remains derived read model
- duplicate/stale events are safe
- PUBLISHED projection searchable
- terminal states excluded from normal search
- E2E event projection works
- failure routes to DLT after bounded retry
