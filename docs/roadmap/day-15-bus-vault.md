# Day 15 — Spring Cloud Bus + Vault

**Status:** Planned  
**Implementation branch:** `day/15-bus-vault`

## Goal

Deliver this milestone without reimplementing Day 1-6 baseline capabilities.

## Planned scope

- Spring Cloud Bus
- Spring Cloud Vault
- Config vs secret separation
- Central config change propagation
- Credential rotation/failure tests

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
