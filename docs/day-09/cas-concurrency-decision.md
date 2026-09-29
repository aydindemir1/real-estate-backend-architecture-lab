# Day 9 — Couchbase CAS / Optimistic Concurrency Decision

## Status

Deferred intentionally.

## Context

Spring Data Couchbase supports optimistic locking through Couchbase CAS by placing `@Version` on a persistence document field. A document loaded through Spring Data receives the current CAS value; a stale save fails with `OptimisticLockingFailureException`.

Day 9's Hexagonal persistence contract is currently:

```text
LoadBuyerPreferencesPort.load(BuyerId) -> Optional<BuyerPreferences>
SaveBuyerPreferencesPort.save(BuyerPreferences) -> BuyerPreferences
```

The persistence document is intentionally separated from the domain aggregate.

## Why CAS is not enabled in Day 9

Adding `@Version` only to `BuyerPreferencesDocument` would not provide correct optimistic concurrency with the current port design.

The CAS value would be populated on the Couchbase document during load, but `BuyerPreferencesDocumentMapper.toDomain(...)` intentionally returns only the domain aggregate. The CAS value would therefore be lost before the application modifies and saves the aggregate.

Recreating a new document with version `0` or an unset version on every save would not preserve the original CAS token and would create misleading concurrency semantics.

Day 9 does not introduce a hidden persistence-specific token into the domain or silently change the established port contracts.

## Correct future options

A later concurrency-focused milestone may choose one of these explicit designs:

1. Add a persistence-agnostic aggregate version/concurrency token to `BuyerPreferences` and carry it through the ports.
2. Change the persistence port contract to return/save a wrapper that carries both the aggregate and concurrency metadata.
3. Introduce a dedicated update port whose adapter owns the load-modify-save CAS cycle.

Any selected approach must include an integration test proving that two independently loaded stale representations cannot overwrite each other.

## Day 9 decision

No `@Version` field is added in Day 9.

No fake optimistic-locking test is added.

The existing deterministic-key Couchbase persistence remains the Day 9 baseline.

This is an explicit deferred item, not an unimplemented claim.
