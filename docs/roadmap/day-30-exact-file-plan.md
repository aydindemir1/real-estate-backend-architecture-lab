# Day 30 — Exact Prometheus + Grafana + Loki + Tempo Stack Plan

## Scope
- Prometheus
- Grafana
- Loki
- Tempo
- supported log shipper
- provisioned datasources/dashboards
- sample actionable alerts
- optional observability Compose profile

## Tasks
1. Add Tempo with explicit image version and local config.
2. Add Prometheus with explicit scrape config for Gateway and all services.
3. Add Loki with explicit local config.
4. Choose a currently supported log shipper at implementation time; prefer Grafana Alloy if appropriate.
5. Add Grafana with provisioned Prometheus, Tempo and Loki datasources.
6. Add Compose `observability` profile so unit/integration tests do not require stack.
7. Add service overview dashboard: traffic, errors, p95/p99, JVM, CPU, threads/connections.
8. Add messaging dashboard: Kafka lag/errors, RabbitMQ queue depth, DLT/DLQ, outbox backlog.
9. Add Offer Saga dashboard: outcomes, hold conflicts, intermediate/stuck states/duration candidates.
10. Add Search dashboard: query latency/errors, Elasticsearch failures, projection freshness.
11. Add sample Prometheus alert rules for sustained 5xx, p99, lag, DLT/DLQ, outbox backlog, freshness and pool saturation.
12. Every alert contains actionable summary and runbook link.
13. Verify Grafana datasources healthy, Prometheus targets UP, Tempo receives traces, Loki receives logs.
14. Verify traceId can be used to navigate from logs to traces where supported.
15. Test Loki/Tempo/Prometheus unavailability does not affect application correctness.
16. Add reasonable local resource/memory guidance.

## Commit sequence
1. `infra(observability): add Tempo`
2. `infra(observability): add Prometheus`
3. `infra(observability): add Loki and supported log pipeline`
4. `infra(observability): add Grafana provisioning`
5. `docs(grafana): add service and messaging dashboards`
6. `docs(grafana): add Saga and Search dashboards`
7. `feat(observability): add sample actionable alerts`
8. `infra(observability): add optional observability profile`
9. `test(observability): verify local stack integration`
10. `docs(observability): finalize local observability guide`

## Final gate
- three datasources provisioned
- services scraped
- logs/traces visible
- dashboards load
- alerts are actionable, not noise-only
- observability backend outage never breaks business flow
