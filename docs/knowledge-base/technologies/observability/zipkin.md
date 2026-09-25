# Zipkin

**Category:** Technology  
**Introduced:** Day 5B  
**Project status:** Implemented / Verified  
**Scope:** Distributed tracing backend and visualization

## 1. Nedir?

Zipkin, distributed trace verilerini toplayan, sorgulayan ve görselleştiren tracing backend sistemidir.

Application tarafında span üretmez; application tracer'ları tarafından üretilen trace verilerini alır.

## 2. Temel mimari

```text
Service A ----\
Service B -----\
Service C ------> Zipkin Collector
Service D -----/        |
                     Storage
                        |
                        v
                    Zipkin UI
```

## 3. Ana bileşenler

### Collector

Trace/span verisini ingest eder.

### Storage

Trace verisini saklar.

### Query Service

Trace sorgularını işler.

### UI

Trace timeline ve service dependency görünümü sağlar.

## 4. Span verisi

Bir span tipik olarak:
- traceId
- spanId
- parentId
- name
- start/end time
- tags
- endpoint/service info

taşır.

## 5. Ne işe yarar?

- end-to-end request path görmek
- latency breakdown
- error localization
- service dependency gözlemlemek
- asynchronous flow'ları takip etmek

## 6. Bu projede nasıl kullanılıyor?

Day 5B'de Gateway -> Auth -> UserProfile synchronous request zinciri Zipkin üzerinde aynı trace altında doğrulanmıştır.

Day 6A'da RabbitMQ producer ve consumer span'leri de distributed trace içinde doğrulanmıştır.

## 7. Zipkin log sistemi değildir

Zipkin trace backend'dir.

- logs -> farklı log backend'i
- metrics -> Prometheus benzeri metric system
- traces -> Zipkin/Tempo gibi tracing backend

Observability bu sinyallerin birlikte kullanılmasıyla güçlenir.

## 8. Avantajları

- basit local setup
- trace visualization
- service dependency visibility
- Java/Spring ecosystem compatibility
- learning için düşük entry cost

## 9. Trade-off'ları

- trace retention/storage büyüyebilir
- high sampling maliyetlidir
- production scale için backend/storage planlaması gerekir
- log correlation ayrıca tasarlanmalıdır

## 10. Production considerations

- sampling
- retention
- storage backend
- authentication/network isolation
- PII
- exporter queue/backpressure
- high availability

## 11. Roadmap'teki geleceği

Zipkin mevcut learning baseline'ıdır.

İleri observability milestone'larında:
- OpenTelemetry
- Prometheus
- Grafana
- Loki
- Tempo

eklenecektir.

Zipkin'in öğrenilmesi trace concepts için temel sağlar; ileride Tempo/OpenTelemetry ile karşılaştırma yapılabilir.

## 12. Alternatifleri

- Grafana Tempo
- Jaeger
- commercial APM platforms

## 13. İleri öğrenme konuları

- collector architecture
- storage models
- dependency graph
- trace retention
- sampling strategies
