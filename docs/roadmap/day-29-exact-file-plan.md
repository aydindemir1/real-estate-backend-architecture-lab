# Day 29 — Exact OpenTelemetry Instrumentation Plan

## Scope
- OpenTelemetry/Micrometer tracing
- structured logging
- traceId/spanId/correlationId
- HTTP/gRPC/Kafka/RabbitMQ propagation
- core infrastructure/business metrics
- low-cardinality guard
- telemetry failure isolation

## Tasks
1. Create/update observability architecture document with logs/metrics/traces and golden signals.
2. Audit existing Micrometer/Brave/Zipkin baseline and define OTel migration.
3. Add Micrometer Tracing bridge/OTLP exporter without duplicate instrumentation stacks.
4. Standardize resource attributes: service.name, service.version, deployment.environment, optional namespace.
5. Externalize OTLP endpoint and trace sampling configuration.
6. Standardize log fields: timestamp, level, service, traceId, spanId, correlationId, message, exception type.
7. Gateway accepts/generates X-Correlation-Id and propagates it.
8. Propagate trace/correlation over HTTP/Feign, gRPC metadata, Kafka headers/envelope, RabbitMQ headers.
9. Expose Prometheus-format application metrics endpoint through Micrometer/Actuator for Day 30 scraping.
10. Verify low-cardinality HTTP server/client metrics.
11. Add gRPC client/server metrics.
12. Add Kafka producer/consumer and RabbitMQ application metrics where broker/client metrics are insufficient.
13. Add datastore pool/saturation metrics where supported.
14. Integrate Day 23 resilience metrics.
15. Add bounded business metrics: offers_created/accepted/rejected, hold conflicts, properties published/state transitions.
16. Add projection freshness/failure metrics.
17. Add outbox pending/publish-failure, duplicate, DLT/DLQ metric hooks.
18. Verify one trace context traverses HTTP→gRPC and async broker boundaries appropriately.
19. Add configurable sampling; 100% local is acceptable but not hard-coded universal policy.
20. Add sensitive-telemetry tests: no Authorization/password/client secret/Vault token.
21. Review custom metric labels; forbid userId/buyerId/sellerId/propertyId/offerId/traceId/raw URL.
22. Test OTLP backend unavailable does not fail business requests or exhaust resources.
23. Document Zipkin comparison and choose one primary tracing pipeline.

## Commit sequence
1. `docs(observability): define OTel instrumentation architecture`
2. `build(observability): add OpenTelemetry tracing dependencies`
3. `config(observability): standardize resource attributes and OTLP settings`
4. `feat(logging): add structured correlated logs`
5. `feat(observability): propagate trace and correlation context`
6. `feat(metrics): add infrastructure and business metrics`
7. `test(observability): verify cross-protocol trace propagation`
8. `test(logging): prevent sensitive telemetry leakage`
9. `test(metrics): enforce low-cardinality metrics`
10. `test(observability): verify telemetry backend isolation`

## Final gate
- OTel is primary tracing model
- trace/log correlation visible
- cross-protocol propagation works
- metrics bounded in cardinality
- secrets absent from telemetry
- exporter outage does not affect business correctness
