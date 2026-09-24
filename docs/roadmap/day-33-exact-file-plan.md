# Day 33 — Exact E2E + Recovery + Backend Completion Plan

## Scope
- critical business E2E
- security/service identity E2E
- outage/recovery exercises
- controlled replay
- final Gradle/Compose verification
- backend completion report
- README/ROADMAP closeout

## Tasks
1. Add reproducible backend E2E Compose profile with only required infrastructure.
2. Registration/Profile E2E: identity/account → UserProfile → role-specific profile mapping.
3. Seller Listing E2E: listing submission → durable pending command → RabbitMQ → Property DRAFT; duplicate safe.
4. Property→Search E2E: publish → Outbox → Kafka → Elasticsearch → REST search; optional GraphQL parity.
5. Offer Acceptance E2E end-to-end.
6. Offer Rejection E2E end-to-end.
7. Concurrent Offer E2E: exactly one hold, no double reservation.
8. Security ownership E2E for Buyer/Seller/Agent plus explicit Admin override where allowed.
9. Service Client Credentials E2E with wrong scope → 403.
10. Representative dependency-failure E2E: Agent unavailable, bounded timeout/circuit and truthful error.
11. Kafka outage recovery: Outbox pending → broker restored → Search catches up.
12. RabbitMQ outage recovery: pending Seller outbound survives → broker restored → one Property.
13. Elasticsearch loss recovery using Day 31 full rebuild/alias switch.
14. Vault outage runbook/tabletop check against real behavior.
15. One Kafka DLT and one RabbitMQ DLQ controlled replay exercise.
16. Create `docs/runbooks/README.md` completeness index if not already done.
17. Create `docs/testing/final-verification.md` with exact Gradle commands for test, architecture, integration, contract, failure and E2E.
18. Run final `clean test`, architecture, integration, contract, failure, E2E and `check`; no hidden skipped failures.
19. Run `docker compose config` and required profile health validation.
20. Create `docs/BACKEND-COMPLETION-REPORT.md`.
21. Update root README to only completed capabilities; explicitly separate Kubernetes/CI/CD future phase.
22. Mark backend roadmap phase complete.
23. Optional learning outcomes document.
24. Optional milestone tag only after main merge + green verification.

## Completion report sections
- service/architecture matrix
- persistence technologies
- protocols
- messaging/reliability
- security
- resilience
- testing
- observability
- recovery/operations
- intentionally deferred work
- known limitations

## Final gate
- all critical E2Es green
- recovery exercises green
- replay verified
- final Gradle verification green
- Compose valid/healthy
- completion report exists
- README contains no unimplemented claims
- next Docker/Kubernetes/Spring Cloud Kubernetes/Jenkins/SonarQube/Nexus/Harbor/Argo CD phase clearly separated
