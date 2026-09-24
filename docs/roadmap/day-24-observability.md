# Day 24 — OpenTelemetry + Prometheus + Grafana + Loki + Tempo

## Goal
Logs, metrics ve traces'i tek operational model altında birleştirmek.

## Tasks
1. telemetry architecture/config finalize et.
2. OpenTelemetry integration ekle.
3. OTLP export ekle.
4. Prometheus metrics expose et.
5. Grafana provisioning ekle.
6. Loki centralized logs ekle.
7. Tempo tracing backend ekle.
8. correlationId/traceId propagation doğrula.
9. HTTP/gRPC/Kafka/RabbitMQ metrics ekle.
10. datastore metrics ekle.
11. business metrics ekle.
12. projection freshness metric ekle.
13. dashboards oluştur.
14. sample alerts/runbook links ekle.
15. Zipkin comparison/migration note yaz.
16. observability integration tests yaz.

## Suggested commits
1. infra(observability): add Prometheus Grafana Loki Tempo
2. feat(observability): add OpenTelemetry export
3. feat(observability): standardize log correlation
4. feat(metrics): add service and messaging metrics
5. feat(metrics): add business and freshness metrics
6. docs(grafana): add operational dashboards
7. test(observability): verify trace and metric propagation
8. docs(observability): document Zipkin comparison and runbooks

## Done
Metric -> trace -> log correlation yapılabilir; critical flow'lar operational olarak görünürdür.

## Exact observability/file plan

Implementation source of truth: `docs/roadmap/day-24-exact-file-plan.md`
