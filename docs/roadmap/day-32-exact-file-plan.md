# Day 32 — Exact Architecture Fitness + Documentation Audit Plan

## Scope
- ArchUnit consolidation
- dependency/module drift
- build/config/contract/persistence drift
- ADR reconciliation
- API/gRPC/GraphQL/messaging/security/observability docs audit
- static/dependency/secret/privacy review
- Eureka vs Consul vs ZooKeeper comparison

## Tasks
1. Create `docs/architecture/architecture-fitness-inventory.md`.
2. Consolidate but do not flatten service-specific Clean/Hexagonal/Onion/Vertical Slice/CQRS rules.
3. Run package/module cycle audit; remove prohibited cross-service source dependencies.
4. Audit root/common helpers for accidental shared domain model and global dependency bloat.
5. Remove unused module-specific dependencies.
6. Reconcile service names, ports, Config keys, Vault paths, topic/queue names and resilience property names.
7. Reconcile REST docs vs implementation.
8. Reconcile proto vs gRPC adapters.
9. Reconcile GraphQL schema vs resolver/security.
10. Reconcile Kafka/RabbitMQ contract catalog vs actual messages.
11. Reconcile persistence docs vs migrations/tables/documents/indexes.
12. Audit all ADR statuses and add only real missing decisions.
13. Audit runbook coverage.
14. Audit authorization matrix/identity/Vault docs.
15. Audit observability dashboards/metrics/alerts docs.
16. Run available compiler/static analysis; fix high-value issues only.
17. Review known vulnerable direct dependencies; avoid risky major upgrades on audit day.
18. Scan repository for real passwords, tokens, client secrets, JWT keys and broker credentials.
19. Review logs/privacy for Authorization, secret or excessive personal payload.
20. Add current Eureka vs Consul vs ZooKeeper comparison and decision context without introducing new runtime dependency.
21. Verify final architecture checklist:
   - database per service
   - no cross-service DB access
   - Redis non-canonical
   - Search derived
   - Property owns reservation concurrency
   - broker/framework APIs outside domain
22. Verify code-quality checklist: no God Service, generic base abuse, DTO=Entity, empty catch, magic secret, business logic in controllers/listeners/mappers.

## Commit sequence
1. `docs(architecture): inventory architecture fitness rules`
2. `test(architecture): consolidate architecture suites`
3. `refactor(architecture): remove dependency cycles if needed`
4. `build: remove unused module dependencies`
5. `config: reconcile distributed configuration names`
6. `docs(contract): reconcile implemented contracts`
7. `docs(data): reconcile persistence models`
8. `docs(adr): reconcile architecture decisions`
9. `docs(security): reconcile security documentation`
10. `docs(observability): reconcile telemetry documentation`
11. `refactor: resolve high-value static findings`
12. `docs(architecture): add Eureka Consul ZooKeeper comparison`

## Final gate
- architecture tests green
- no prohibited module/package cycles
- docs match implementation
- ADR/runbook inventory current
- dependency/secret/privacy audits pass or known risks documented
- no new feature scope introduced
