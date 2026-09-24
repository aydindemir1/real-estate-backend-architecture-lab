# Day 14 — Advanced Resilience

**Status:** Planned  
**Implementation branch:** `day/14-resilience`

## Goal

Deliver this milestone without reimplementing Day 1-6 baseline capabilities.

## Planned scope

- Extend existing Circuit Breaker
- Retry
- TimeLimiter
- Bulkhead
- Redis-backed RateLimiter
- Different policies for HTTP/gRPC/messaging

## Documentation rule

Detailed service-specific package structures, data models and dependency changes live in each service's `docs/DESIGN.md`. This file records only milestone scope and cross-service work.

## Required before implementation

- Review affected service DESIGN documents.
- Finalize API/event contracts needed by this milestone.
- Finalize infrastructure/configuration changes.
- Define positive and failure-path tests.
- Confirm Definition of Done.

## Definition of Done

- Implementation works on the Day branch.
- Automated tests for the milestone pass.
- Existing baseline regression tests remain valid.
- Relevant DESIGN/ROADMAP/ADR files are updated in the same branch.
- Only then is the Day branch eligible to merge into `main`.
