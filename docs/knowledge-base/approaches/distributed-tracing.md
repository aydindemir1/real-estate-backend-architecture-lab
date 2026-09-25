# Distributed Tracing

**Category:** Approach  
**Introduced:** Day 5B  
**Project status:** Implemented / Integrated / Verified  
**Scope:** End-to-end request visibility across distributed components

## 1. Nedir?

Distributed Tracing, tek bir logical request'in birden fazla service, network call ve messaging boundary boyunca nasıl ilerlediğini trace/span ilişkisiyle izleme yaklaşımıdır.

## 2. Hangi problemi çözer?

Microservices sisteminde tek request:

```text
Gateway -> Auth -> UserProfile -> Broker -> Consumer
```

gibi birden fazla bileşenden geçebilir.

Tek service log'una bakmak artık yeterli değildir.

Distributed tracing:
- request path
- latency
- dependency
- error location

görünürlüğü sağlar.

## 3. Temel kavramlar

### Trace

Bir logical transaction'ın tamamı.

### Span

Trace içindeki tek operation.

### Trace ID

Aynı distributed transaction'a ait span'leri ilişkilendirir.

### Span ID

Tek span'i tanımlar.

### Parent / Child Relationship

Operation'ların birbirinden nasıl türediğini gösterir.

## 4. İç işleyiş

```text
Client Request
    |
    v
Gateway Span
    |
    v
HTTP Client Span
    |
    v
Auth Server Span
    |
    v
Feign Client Span
    |
    v
UserProfile Server Span
```

Trace context HTTP header veya message header ile taşınır.

## 5. Ne işe yarar?

- latency breakdown
- dependency visualization
- bottleneck detection
- failure path analysis
- request correlation
- async flow visibility
- distributed debugging

## 6. Logging ile farkı

Logging:
> "Bu service ne yaptı?"

Tracing:
> "Bu request sistem boyunca nereden geçti?"

Metrics:
> "Sistem genel olarak nasıl davranıyor?"

Üçü birlikte observability oluşturur.

## 7. Bu projede nasıl kullanılıyor?

Day 5B'de:
- Micrometer Tracing
- Brave
- Zipkin

ile tracing eklenmiştir.

Gateway -> Auth -> UserProfile synchronous flow aynı trace altında doğrulanmıştır.

Day 6A'da RabbitMQ producer/consumer trace propagation da doğrulanmıştır.

## 8. Avantajları

- cross-service visibility
- latency attribution
- dependency graph
- faster debugging
- async boundary gözlemi

## 9. Dezavantajları

- storage cost
- sampling ihtiyacı
- instrumentation overhead
- high-cardinality risk
- trace context propagation complexity

## 10. Sampling

Her request'in trace edilmesi production'da pahalı olabilir.

Common strategies:
- head sampling
- tail sampling
- probability sampling
- error-biased sampling

Projede local learning amacıyla sampling probability 1.0 kullanılmıştır.

## 11. Production considerations

- sampling strategy
- PII redaction
- trace retention
- exporter failure behavior
- high-cardinality tags
- correlation ID policy
- context propagation
- async instrumentation
- storage sizing

## 12. İlgili teknolojiler

- Micrometer Tracing
- Brave
- Zipkin
- OpenTelemetry (roadmap'te ileri milestone)

## 13. İleri öğrenme konuları

- OpenTelemetry
- baggage
- exemplars
- tail sampling
- trace-log correlation
- trace-metric correlation
