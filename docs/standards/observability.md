# Observability Standard

Bu doküman projede uygulanacak logging, metrics, tracing, correlation, business observability ve alerting standartlarını tanımlar.

## 1. Observability pillars
- Logs
- Metrics
- Traces

Bu üçü birbirini tamamlar; tek başına log yeterli kabul edilmez.

## 2. Correlation
Her request/message flow mümkün olduğunca şu context'i taşır:
- traceId
- spanId
- correlationId
- causationId messaging için
- requestId gerektiğinde

## 3. Trace propagation
Context şu zincirde propagate edilir:
HTTP -> Gateway -> REST/gRPC -> producer -> broker -> consumer -> datastore/downstream.

## 4. Structured Logging
Plain string log yerine structured fields tercih edilir.

Örnek alanlar:
- service
- operation
- aggregateId
- userId gerektiğinde
- correlationId
- traceId
- eventId/commandId
- outcome
- durationMs

## 5. Log level
- ERROR: action required / failed operation
- WARN: degraded/recoverable abnormal behavior
- INFO: meaningful business/operational milestones
- DEBUG: development diagnostics
- TRACE: çok detaylı ve default kapalı

## 6. Sensitive logging
Loglanmaz:
- password
- access token
- refresh token
- secret
- credential
- full sensitive payload

PII yalnızca gerçekten gerekirse ve masking ile.

## 7. Business logging
Business state transition loglanabilir:
- PropertyPublished
- OfferAccepted
- PropertyReserved

Ancak log domain event'in yerine geçmez.

## 8. Metrics taxonomy
Metrics üç gruba ayrılır:
- RED: Rate, Errors, Duration
- USE: Utilization, Saturation, Errors
- Business Metrics

## 9. HTTP metrics
- request count
- error rate
- p50/p95/p99 latency
- status code distribution
- active requests gerektiğinde

## 10. gRPC metrics
- request count
- latency
- status code
- timeout/deadline exceeded

## 11. Messaging metrics
Kafka:
- consumer lag
- consume rate
- processing latency
- retry count
- DLT count

RabbitMQ:
- queue depth
- publish/consume rate
- redelivery
- DLQ count
- unacked messages

## 12. Database metrics
- query latency
- connection pool utilization
- timeout
- error rate
- slow query

Technology-specific:
- Cassandra request latency
- Elasticsearch query latency
- Redis hit/miss
- Mongo operation latency

## 13. Cache metrics
- hit
- miss
- eviction
- load latency
- stale/read-through behavior gerektiğinde

## 14. Business metrics
Örnek:
- listings_submitted_total
- properties_published_total
- offers_created_total
- offers_accepted_total
- reservations_completed_total
- search_requests_total

Business metric teknik log'a bağımlı bırakılmaz.

## 15. Metric naming
Lowercase/snake_case veya tool convention tutarlı kullanılmalıdır.

High-cardinality label kaçınılır.

Yanlış label candidate:
- userId
- propertyId
- offerId

## 16. High cardinality
Metric label olarak unique identifier kullanılmaz.
Unique identifiers log/trace'de tutulur.

## 17. Distributed Tracing
OpenTelemetry target standard olacaktır.

Mevcut Micrometer/Zipkin baseline korunabilir; Day 18'de OpenTelemetry + Tempo yönüne genişletilir.

## 18. Span design
Span yalnızca gerçek operation boundary için oluşturulur.

Candidate:
- HTTP request
- gRPC call
- Kafka publish/consume
- RabbitMQ publish/consume
- datastore call
- critical application use-case

## 19. Span attributes
Low-cardinality semantic attribute kullanılır.

Sensitive data span attribute olarak eklenmez.

## 20. Manual instrumentation
Framework auto-instrumentation yetiyorsa gereksiz manual span oluşturulmaz.

Business-critical operation için manual span değerlendirilebilir.

## 21. OpenTelemetry
Day 18 hedef:
- OpenTelemetry SDK/agent approach değerlendirme
- OTLP export
- trace/metric propagation

## 22. Prometheus
Metrics collection standardı.

Actuator/Micrometer üzerinden scrape endpoint güvenli şekilde expose edilir.

## 23. Grafana
Dashboard candidate'ları:
- service overview
- API latency/error
- messaging
- datastore
- business KPI

## 24. Loki
Centralized log aggregation.

Structured log parsing ile correlationId/traceId üzerinden filtre yapılabilir.

## 25. Tempo
Distributed tracing backend.

Grafana ile trace -> log -> metric correlation hedeflenir.

## 26. Existing Zipkin
Mevcut Zipkin setup baseline olarak korunur.
Day 18'de OpenTelemetry/Tempo ile karşılaştırmalı öğrenme yapılabilir.

## 27. Health
Actuator health:
- liveness
- readiness

semantics doğru kullanılmalıdır.

## 28. Liveness
Process çalışıyor mu?

Dependency down diye container gereksiz restart edilmemelidir.

## 29. Readiness
Service traffic almaya hazır mı?

Critical dependency durumu readiness'e yansıtılabilir.

## 30. Dependency health
Her dependency health check'e blindly eklenmez.
Failure behavior ve cascading restart riski değerlendirilir.

## 31. SLI
Service Level Indicator candidate'ları:
- availability
- success rate
- latency
- freshness
- consumer lag

## 32. SLO
Lab için gerçek production commitment değil, öğrenme amaçlı objective tanımlanabilir.

Örnek:
- p95 search latency < X ms
- API success rate > X%
- search projection freshness < X sec

Exact değer benchmark sonrası belirlenir.

## 33. Error Budget
SLO kavramını öğrenmek için error budget teorik/pratik olarak değerlendirilebilir.

## 34. Alerting
Alert symptom-oriented olmalıdır.

Candidate:
- sustained error rate
- p95 latency spike
- Kafka lag
- RabbitMQ queue depth
- DLT/DLQ growth
- DB pool saturation
- dependency unavailable

## 35. Alert fatigue
Her warning alert değildir.

Actionable olmayan alert oluşturulmaz.

## 36. Dashboard
Dashboard decoration değil operational question cevaplamalıdır:
- Service sağlıklı mı?
- Nerede yavaşlık var?
- Hangi dependency sorunlu?
- Message backlog var mı?
- Business throughput düştü mü?

## 37. Golden Signals
- latency
- traffic
- errors
- saturation

dashboard baseline'ında bulunur.

## 38. Correlation workflow
Operational debugging hedefi:
Grafana metric -> Tempo trace -> Loki log.

## 39. Exception logging
Exception iki kez gereksiz loglanmaz.

Boundary'de loglanıp tekrar rethrow edilen aynı exception her layer'da loglanmaz.

## 40. Stack trace
Unexpected error'da stack trace loglanabilir.
Client'a dönmez.

## 41. Logging ownership
Domain model logging framework'e bağımlı olmak zorunda değildir.

Application/infrastructure boundary logging için daha uygundur.

## 42. Audit vs Observability
Audit log:
- kim
- ne yaptı
- hangi resource
- sonuç

Observability log:
- sistem nasıl davrandı

Aynı şey değildir.

## 43. Sampling
High-volume tracing'de sampling değerlendirilebilir.

Error/rare critical trace'lerin kaçırılmaması için policy gerekir.

## 44. Retention
Log/metric/trace retention tool ve environment'a göre belirlenir.

## 45. Cost awareness
High-cardinality metric, excessive DEBUG log ve full-payload trace telemetry maliyetini artırır.

## 46. Environment tags
Telemetry'de:
- service.name
- environment
- version

gibi resource attributes bulunur.

## 47. Deployment version
Trace/log/metric üzerinden hangi application version'un çalıştığı görülebilmelidir.

## 48. Messaging trace
Producer ve consumer span'ları correlation/trace context ile bağlanır.

## 49. Event processing duration
Consumer için yalnızca broker lag değil application processing duration da ölçülür.

## 50. Projection freshness
SearchService için önemli business/technical metric:
- Property event occurredAt ile projection updatedAt farkı.

## 51. Idempotency metric
Duplicate message count faydalı olabilir.

## 52. Retry metric
Retry count ve retry exhausted count izlenir.

## 53. Rate limit metric
- allowed
- rejected

izlenebilir.

## 54. Security observability
- authentication failures
- authorization failures
- suspicious rate limit events

metric/audit ile izlenebilir fakat sensitive credential loglanmaz.

## 55. Testing Observability
Integration test'lerde:
- trace context propagation
- correlationId propagation
- metric registration
- health endpoint behavior

kritik kısımlar doğrulanabilir.

## 56. Failure debugging scenario
Bir offer flow başarısız olduğunda şu zincir bulunabilmelidir:
HTTP request -> OfferRequested -> PropertyHeld -> Seller decision -> final state.

## 57. Anti-Pattern'ler
- her satırı INFO loglamak
- full request/response payload loglamak
- userId/propertyId metric label yapmak
- log'u event store sanmak
- health check'i tüm dependency restart mekanizmasına çevirmek
- metric olmadan sadece log kullanmak
- actionable olmayan alert
- traceId/correlationId propagate etmemek
- aynı exception'ı her layer'da loglamak

## 58. Observability Review Checklist
- Log structured mı?
- Sensitive data var mı?
- traceId/correlationId propagate ediliyor mu?
- RED metrics var mı?
- Critical dependency metric'i var mı?
- Messaging lag/queue depth gözleniyor mu?
- Business metric gerekli mi?
- High-cardinality label var mı?
- Health liveness/readiness doğru mu?
- Alert actionable mı?
- Trace -> log -> metric correlation mümkün mü?