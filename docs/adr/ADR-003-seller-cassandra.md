# ADR-003 — Cassandra for SellerService

**Status:** Accepted

## Context
SellerService is the chosen service for learning wide-column, distributed and query-driven modeling.

## Decision
Use Spring Data Cassandra. Model tables from access patterns such as seller lookup, listing submission history and time-ordered seller activity.

## Rationale
Cassandra teaches partition keys, clustering keys, denormalization and distributed data design that would not be learned by adding another relational database.

## Alternatives considered
MySQL, PostgreSQL, MongoDB.

## Consequences
No joins or JPA-style aggregate navigation. Duplicate/denormalized data may be intentional. Classic relational transactional-outbox assumptions must not be falsely applied.
