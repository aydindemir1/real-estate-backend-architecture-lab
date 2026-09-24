# ADR-001 — Database per Service

**Status:** Accepted

## Context
The laboratory needs clear service ownership and independent persistence decisions while preserving the stable Day 1-6 baseline.

## Decision
Each new business service owns its primary datastore. AuthService and UserProfileService remain on PostgreSQL because they are existing stable baseline services.

No service writes directly to another service's database.

## Rationale
- teaches ownership boundaries
- makes polyglot persistence explicit
- supports independent evolution
- creates realistic eventual-consistency problems to solve later

## Alternatives considered
- one shared relational database
- schema-per-service inside one database

## Consequences
Cross-service joins disappear. Integration occurs through APIs/events. Distributed consistency must be handled explicitly.
