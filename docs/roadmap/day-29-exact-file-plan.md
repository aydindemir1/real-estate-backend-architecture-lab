# Day 29 — Kesin OpenTelemetry Instrumentation Planı

## Kapsam
- OpenTelemetry/Micrometer tracing
- structured logging
- traceId/spanId/correlationId
- HTTP/gRPC/Kafka/RabbitMQ propagation
- core infrastructure/business metrics
- low-cardinality guard
- telemetry failure isolation

## Task'ler
1. Logs/metrics/traces ve golden signals içeren observability architecture dokümanını oluştur/güncelle.
2. Mevcut Micrometer/Brave/Zipkin baseline'ını audit et ve OTel migration'ını tanımla.
3. Duplicate instrumentation stack oluşturmadan Micrometer Tracing bridge/OTLP exporter ekle.
4. Resource attribute'larını standardize et: service.name, service.version, deployment.environment, opsiyonel namespace.
5. OTLP endpoint ve trace sampling configuration'ı externalize et.
6. Log field'larını standardize et: timestamp, level, service, traceId, spanId, correlationId, message, exception type.
7. Gateway X-Correlation-Id kabul eder/üretir ve propagate eder.
8. Trace/correlation context'i HTTP/Feign, gRPC metadata, Kafka headers/envelope, RabbitMQ headers üzerinden propagate et.
9. Day 30 scraping için Micrometer/Actuator üzerinden Prometheus-format application metrics endpoint expose et.
10. Low-cardinality HTTP server/client metric'lerini doğrula.
11. gRPC client/server metrics ekle.
12. Broker/client metric'leri yetersizse Kafka producer/consumer ve RabbitMQ application metrics ekle.
13. Destekleniyorsa datastore pool/saturation metric'leri ekle.
14. Day 23 resilience metric'lerini integrate et.
15. Bounded business metric'ler ekle: offers_created/accepted/rejected, hold conflicts, properties published/state transitions.
16. Projection freshness/failure metric'leri ekle.
17. Outbox pending/publish-failure, duplicate, DLT/DLQ metric hook'ları ekle.
18. Tek trace context'in HTTP→gRPC ve async broker boundary'lerini uygun şekilde geçtiğini doğrula.
19. Configurable sampling ekle; local için %100 kabul edilebilir ancak universal hard-coded policy olmasın.
20. Sensitive-telemetry testleri ekle: Authorization/password/client secret/Vault token yok.
21. Custom metric label'larını review et; userId/buyerId/sellerId/propertyId/offerId/traceId/raw URL yasak.
22. OTLP backend unavailable durumunun business request'leri fail etmediğini veya resource exhaustion yaratmadığını test et.
23. Zipkin karşılaştırmasını dokümante et ve tek primary tracing pipeline seç.

## Commit sırası
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
- OTel primary tracing model
- trace/log correlation görünür
- cross-protocol propagation çalışıyor
- metrics cardinality bounded
- telemetry içinde secret yok
- exporter outage business correctness'i etkilemiyor
