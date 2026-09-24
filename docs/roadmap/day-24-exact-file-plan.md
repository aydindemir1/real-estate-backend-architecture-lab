# Day 24 — Exact Observability / Metrics / Dashboard / Commit Plan

## 0. Scope

Day 24 yalnızca observability hardening içindir.

Hedef:
- OpenTelemetry
- OTLP export
- Prometheus
- Grafana
- Loki
- Tempo
- correlationId / traceId
- HTTP/gRPC/Kafka/RabbitMQ/datastore metrics
- business metrics
- projection freshness
- dashboards
- sample actionable alerts
- observability integration tests

Day 24 içinde:
- production paging/on-call platform yok
- full SLO program yok
- commercial APM yok
- distributed profiling yok

## Task 1 — Observability architecture document

Create:
- docs/architecture/observability-architecture.md

Signal model:
- Logs
- Metrics
- Traces

Golden signals:
- latency
- traffic
- errors
- saturation

Commit: docs(observability): define observability architecture

## Task 2 — Existing baseline audit

Review current:
- Micrometer
- Brave/Zipkin
- Actuator
- logging pattern

Document migration/compatibility path.

Goal:
OpenTelemetry becomes target instrumentation/export model.

Commit: docs(observability): record current tracing baseline

## Task 3 — OpenTelemetry dependency strategy

Prefer Spring Boot/Micrometer-compatible OpenTelemetry integration.

Add only what is necessary:
- Micrometer Tracing bridge to OTel
- OTLP exporter

Avoid duplicate instrumentation stacks.

Commit: build(observability): add OpenTelemetry tracing dependencies

## Task 4 — Service resource attributes

Configure per service:
- service.name
- service.version
- deployment.environment

Optional:
- service.namespace = real-estate

Do not use high-cardinality resource attributes.

Commit: config(observability): standardize service resource attributes

## Task 5 — OTLP endpoint config

External config:
- OTEL_EXPORTER_OTLP_ENDPOINT
- sampling probability if applicable

Do not hard-code localhost in production-like config.

Commit: config(observability): add OTLP exporter settings

## Task 6 — Tempo infrastructure

Modify docker-compose.yml.

Add:
- tempo

Create config:
- infra/observability/tempo/tempo.yaml

Local single-binary mode acceptable.

Explicit image version.

Commit: infra(observability): add Tempo tracing backend

## Task 7 — Prometheus infrastructure

Add:
- prometheus

Create:
- infra/observability/prometheus/prometheus.yml

Scrape targets:
- Gateway
- Auth/UserProfile
- Agent
- Buyer
- Seller
- Property
- Search

Use actuator/prometheus.

Commit: infra(observability): add Prometheus metrics backend

## Task 8 — Loki infrastructure

Add:
- loki

Create:
- infra/observability/loki/loki-config.yaml

Need log shipping strategy.

## Task 9 — Log shipper decision

Choose one:
- Grafana Alloy
- Promtail if still appropriate for chosen stack/version

Prefer current supported option at implementation time.

Do not assume deprecated tooling without version check.

Create:
- infra/observability/alloy/config.alloy or equivalent

Commit: infra(observability): add Loki log pipeline

## Task 10 — Grafana infrastructure

Add:
- grafana

Create provisioning:
- infra/observability/grafana/provisioning/datasources/
- infra/observability/grafana/provisioning/dashboards/

Datasources:
- Prometheus
- Tempo
- Loki

Commit: infra(observability): add Grafana with provisioned datasources

## Task 11 — Trace-log correlation

Logging pattern/json fields should include:
- traceId
- spanId
- correlationId
- service

Do not manually generate traceId.

Commit: feat(observability): standardize trace and log correlation

## Task 12 — CorrelationId policy

Gateway:
- accept valid incoming X-Correlation-Id or generate one

Propagate through:
- HTTP/Feign
- gRPC metadata
- Kafka event envelope
- RabbitMQ message headers

Do not replace distributed trace with correlation ID; both serve different needs.

Commit: feat(observability): propagate correlation identifiers

## Task 13 — Structured logging

Prefer structured JSON in container environment if current logging stack supports cleanly.

Fields:
- timestamp
- level
- service
- traceId
- spanId
- correlationId
- message
- exception type

No sensitive payload/token/password.

Commit: feat(logging): add structured service logs

## Task 14 — Logging levels

Policy:
- INFO business/operational milestones sparingly
- WARN recoverable abnormal state
- ERROR failed operation
- DEBUG developer detail

Do not log each successful DB call/event at INFO.

## Task 15 — Prometheus actuator config

Enable:
- micrometer-registry-prometheus
- actuator prometheus endpoint

Secure endpoint according to Day 14 policy.

Commit: feat(metrics): expose Prometheus metrics

## Task 16 — HTTP server metrics

Use framework-provided metrics:
- request count
- latency histogram/timers
- status
- route/template

Ensure URI label uses route template, not raw ID path.

## Task 17 — HTTP client metrics

Feign/client metrics:
- latency
- success/failure
- dependency name

Low-cardinality labels only.

## Task 18 — gRPC metrics

Instrument:
- client calls
- server calls
- duration
- status code

Tag by service/method, not agentId/userId.

Commit: feat(metrics): add gRPC metrics

## Task 19 — Kafka metrics

Expose/collect:
- producer send errors
- consumer records
- consumer lag via broker/exporter or app metrics
- DLT count

Do not invent app metric where broker/client metric already exists.

Commit: feat(metrics): add Kafka messaging metrics

## Task 20 — RabbitMQ metrics

Expose/collect:
- publish failures
- consumer failures
- queue depth via broker exporter/management metrics
- DLQ count

Commit: feat(metrics): add RabbitMQ messaging metrics

## Task 21 — Database metrics

Relational:
- Hikari active/idle/pending

Other datastore:
- client pool/latency metrics where library exposes them

Do not create per-query high-cardinality metric.

Commit: feat(metrics): expose datastore saturation metrics

## Task 22 — Resilience metrics

Integrate Day 20 metrics:
- circuit state
- retry count
- bulkhead rejected
- rate-limit rejected

Ensure names/tags consistent.

## Task 23 — Business metric: Offer flow

Candidate metrics:
- offers_created_total
- offers_accepted_total
- offers_rejected_total
- offer_hold_conflicts_total

Labels:
- outcome/reason bounded enums

Never label by buyerId/propertyId/offerId.

Commit: feat(metrics): add Offer business metrics

## Task 24 — Business metric: Property

Candidate:
- properties_published_total
- property_state_transitions_total with bounded from/to labels

Keep cardinality bounded.

Commit: feat(metrics): add Property lifecycle metrics

## Task 25 — Projection freshness metric

Metric:
- search_projection_freshness_seconds

Measure event occurredAt -> projection completion.

Also:
- search_projection_failures_total

Do not label propertyId.

Commit: feat(metrics): add Search projection freshness metrics

## Task 26 — Outbox/Inbox reliability metrics

Candidate:
- outbox_pending_count gauge
- outbox_publish_failures_total
- duplicate_messages_total
- dlt_messages_total
- dlq_messages_total

Commit: feat(metrics): add messaging reliability metrics

## Task 27 — Trace propagation HTTP

Verify Gateway -> downstream -> Feign spans are linked.

No separate root trace per hop.

## Task 28 — Trace propagation gRPC

Verify Buyer -> Agent same trace.

## Task 29 — Trace propagation Kafka

Producer span/context injected into message.

Consumer creates linked/continued context according to instrumentation model.

## Task 30 — Trace propagation RabbitMQ

Same for command workflow.

Commit: test(observability): verify cross-protocol trace propagation

## Task 31 — Trace sampling policy

Local:
- 100% acceptable for learning

Production-like:
- configurable sampling

Do not hard-code always-on as universal recommendation.

Commit: config(observability): add configurable trace sampling

## Task 32 — Exemplars / metric-trace linkage

If current Prometheus/Grafana stack supports exemplars cleanly, enable.

Otherwise defer.

Do not add complexity merely to check feature box.

## Task 33 — Grafana service overview dashboard

Create JSON/provisioned dashboard:
- infra/observability/grafana/dashboards/service-overview.json

Panels:
- request rate
- error rate
- p95/p99 latency
- JVM memory/GC
- CPU
- active threads/connections

Commit: docs(grafana): add service overview dashboard

## Task 34 — Messaging dashboard

Create:
- messaging-overview.json

Panels:
- Kafka consumer lag
- producer errors
- RabbitMQ queue depth
- DLQ/DLT
- outbox pending

Commit: docs(grafana): add messaging dashboard

## Task 35 — Saga dashboard

Panels:
- offers by outcome
- hold conflicts
- stuck/intermediate states candidate
- saga duration candidate

Use bounded dimensions.

Commit: docs(grafana): add Offer Saga dashboard

## Task 36 — Search dashboard

Panels:
- search request rate
- search latency
- Elasticsearch errors
- projection freshness
- projection failures

Commit: docs(grafana): add Search dashboard

## Task 37 — Sample alert rules

Create:
- infra/observability/prometheus/alerts.yml

Candidate alerts:
- high 5xx rate
- p99 latency sustained
- Kafka lag high
- DLQ/DLT non-zero sustained
- outbox backlog growing
- projection freshness high
- DB pool saturation

Rules must be actionable.

Thresholds local/demo, not production SLO claims.

Commit: feat(observability): add sample actionable alerts

## Task 38 — Alert annotation/runbook link

Each alert includes:
- summary
- service
- symptom
- runbook reference

Commit: docs(observability): link alerts to runbooks

## Task 39 — Trace scenario: Offer Saga

Verify one Offer flow trace/correlation across:
- Buyer HTTP
- Kafka OfferRequested
- Property consumer
- Kafka PropertyHeld
- Seller consumer/decision
- Property reserve
- Buyer outcome

Even if asynchronous segments create linked spans rather than one strict parent tree, correlation must be navigable.

## Task 40 — Trace scenario: Listing command

Verify:
- Seller HTTP
- RabbitMQ command
- Property consumer
- PropertyCreated/outbox/Kafka if active

## Task 41 — Sensitive logging tests

Create tests/static scan for:
- Authorization header
- passwords
- client secrets
- Vault token

Ensure logs do not contain these.

Commit: test(logging): prevent sensitive telemetry leakage

## Task 42 — Cardinality tests/review

Review custom metrics labels.

Forbidden labels:
- userId
- buyerId
- sellerId
- propertyId
- offerId
- traceId
- raw URL

Commit: test(metrics): enforce low-cardinality custom metrics

## Task 43 — OTLP outage test

Stop Tempo/collector/export endpoint.

Verify:
- business requests continue
- exporter failure does not exhaust resources

Commit: test(observability): verify telemetry backend isolation

## Task 44 — Loki outage test

Log shipper/Loki unavailable.

Verify application does not fail.

Container stdout remains primary fallback.

## Task 45 — Prometheus outage test

Prometheus not scraping should have zero application behavior impact.

No special app code required.

## Task 46 — Integration smoke

Start observability profile.

Verify:
- Prometheus targets UP
- Grafana datasources healthy
- Tempo receives traces
- Loki receives logs
- traceId visible in log search

## Task 47 — Zipkin comparison/migration note

Document:
- previous Zipkin/Brave baseline
- OpenTelemetry/Tempo target
- whether Zipkin is removed or kept as optional learning comparison

Prefer one active primary tracing pipeline.

Commit: docs(observability): document Zipkin to OpenTelemetry transition

## Task 48 — Docker Compose profile

Create/adjust profile:
- observability

Includes:
- Prometheus
- Grafana
- Loki
- Tempo
- log shipper

Do not require observability stack for every unit test.

Commit: infra(observability): add optional observability profile

## Task 49 — Resource limits

Local stack is heavy.

Set reasonable memory constraints/tuning where supported:
- Elasticsearch
- Grafana
- Tempo/Loki

Document developer resource expectation.

## Task 50 — Documentation reconciliation

Modify/create:
- docs/architecture/observability-architecture.md
- docs/roadmap/day-24-observability.md
- docs/runbooks/observability-backend-outage.md
- docs/infrastructure/local-observability.md

Record:
- ports
- image versions
- scrape paths
- OTLP endpoint
- log pipeline
- dashboards
- alert rules
- sampling
- cardinality rules

Commit: docs(observability): finalize observability implementation guide

## Recommended Commit Sequence

1. docs(observability): define observability architecture
2. docs(observability): record current tracing baseline
3. build(observability): add OpenTelemetry tracing dependencies
4. config(observability): standardize service resource attributes
5. config(observability): add OTLP exporter settings
6. infra(observability): add Tempo tracing backend
7. infra(observability): add Prometheus metrics backend
8. infra(observability): add Loki log pipeline
9. infra(observability): add Grafana with provisioned datasources
10. feat(observability): standardize trace and log correlation
11. feat(observability): propagate correlation identifiers
12. feat(logging): add structured service logs
13. feat(metrics): expose Prometheus metrics
14. feat(metrics): add gRPC metrics
15. feat(metrics): add Kafka messaging metrics
16. feat(metrics): add RabbitMQ messaging metrics
17. feat(metrics): expose datastore saturation metrics
18. feat(metrics): add Offer business metrics
19. feat(metrics): add Property lifecycle metrics
20. feat(metrics): add Search projection freshness metrics
21. feat(metrics): add messaging reliability metrics
22. config(observability): add configurable trace sampling
23. test(observability): verify cross-protocol trace propagation
24. docs(grafana): add service overview dashboard
25. docs(grafana): add messaging dashboard
26. docs(grafana): add Offer Saga dashboard
27. docs(grafana): add Search dashboard
28. feat(observability): add sample actionable alerts
29. docs(observability): link alerts to runbooks
30. test(logging): prevent sensitive telemetry leakage
31. test(metrics): enforce low-cardinality custom metrics
32. test(observability): verify telemetry backend isolation
33. docs(observability): document Zipkin to OpenTelemetry transition
34. infra(observability): add optional observability profile
35. docs(observability): finalize observability implementation guide

Adjacent metrics commits can be grouped by capability, but infra, instrumentation, dashboards, tests and docs should remain independently reviewable.

## Explicitly Deferred from Day 24

Do not implement:
- commercial APM
- eBPF profiling
- production pager/on-call integration
- full SLO/error-budget governance
- long-term telemetry retention policy
- distributed continuous profiling

## Critical Design Note — Three Signals

Logs, metrics and traces complement each other.

No single signal is sufficient for incident diagnosis.

## Critical Design Note — Cardinality

Custom metrics must use bounded labels.

Business IDs belong in logs/traces, not metric labels.

## Critical Design Note — Telemetry Failure

Prometheus/Loki/Tempo outage must not fail business requests.

## Day 24 Final Gate

Day 24 closes only if:
- OpenTelemetry is the primary tracing model
- OTLP export works
- Prometheus scrapes services
- Grafana datasources are provisioned
- Loki receives application logs
- Tempo receives traces
- traceId/spanId/correlationId are visible in logs
- HTTP/gRPC/Kafka/RabbitMQ traces propagate
- core infrastructure metrics are available
- Offer/Property/Search business metrics exist
- projection freshness metric exists
- custom metric labels are low-cardinality
- dashboards are provisioned
- sample alerts are actionable and link to runbooks
- sensitive data is absent from telemetry
- telemetry backend outage does not break business flow
- optional observability compose profile exists
- no commercial APM/SLO/pager scope leaks into Day 24
- docs match actual implementation