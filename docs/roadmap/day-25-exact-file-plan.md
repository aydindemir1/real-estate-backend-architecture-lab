# Day 25 — Exact Spring Cloud Bus Plan

## Scope
- Spring Cloud Bus over RabbitMQ
- refresh-safe property inventory
- targeted RefreshScope
- protected distributed refresh
- Bus outage tests
- control-plane separation

## Tasks
1. Add Spring Cloud Bus AMQP dependency where needed.
2. Use RabbitMQ as Bus transport; keep Bus destination separate from business exchanges.
3. Load Bus broker credential from Vault.
4. Create `docs/config/refreshable-properties.md`.
5. Classify runtime-refresh-safe vs restart-required properties.
6. Add `@RefreshScope` only to beans that genuinely support safe refresh.
7. Implement controlled Config Server change → busrefresh → service refresh flow.
8. Protect refresh/bus actuator endpoints with admin/ops authorization.
9. Add deterministic Bus refresh integration test.
10. Add test proving non-refresh-safe critical infrastructure config does not silently mutate.
11. Test RabbitMQ/Bus outage: current service behavior continues with existing config.
12. Enforce control-plane separation: Bus events are not domain/business events.
13. Add `docs/runbooks/config-refresh.md`.
14. Document Config Server outage vs Bus outage differences.

## Commit sequence
1. `build(bus): add Spring Cloud Bus over RabbitMQ`
2. `config(bus): add distributed config refresh channel`
3. `docs(config): classify refresh-safe properties`
4. `feat(config): add targeted refresh scope`
5. `feat(bus): add controlled distributed refresh`
6. `feat(security): protect Bus refresh endpoints`
7. `test(bus): verify distributed refresh and broker outage`
8. `test(bus): enforce control-plane separation`
9. `docs(config): add config refresh runbook`

## Final gate
- Bus is separate control plane
- approved properties refresh live
- unsafe infrastructure settings remain restart-bound
- busrefresh endpoint protected
- broker outage does not break steady-state requests
