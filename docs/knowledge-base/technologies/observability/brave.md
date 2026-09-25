# Brave

**Category:** Technology  
**Introduced:** Day 5B  
**Project status:** Implemented / Integrated / Verified  
**Scope:** Concrete distributed tracing implementation used through Micrometer bridge

## 1. Nedir?

Brave, distributed tracing instrumentation ve trace context propagation sağlayan Java tracing library'sidir.

Zipkin ecosystem'inden doğmuştur ve trace/span oluşturma, propagation ve sampling capability'leri sağlar.

## 2. Bu projedeki rolü

Projede Brave doğrudan application architecture'ın merkezi değildir.

Katman:

```text
Spring Boot
    |
    v
Micrometer Tracing
    |
    v
Micrometer Brave Bridge
    |
    v
Brave
    |
    v
Zipkin Reporter
```

şeklindedir.

## 3. Temel kavramlar

- Tracing
- Tracer
- Span
- TraceContext
- Propagation
- Sampler
- CurrentTraceContext

## 4. TraceContext

Bir span'in distributed kimliğini taşır.

Tipik bilgiler:
- trace id
- span id
- parent span id
- sampled state

## 5. Propagation

Brave trace context'in HTTP veya messaging boundary boyunca taşınmasını sağlar.

Bu sayede:
- Gateway span'i,
- Feign client span'i,
- downstream server span'i,
- RabbitMQ producer/consumer span'i

aynı trace içinde ilişkilendirilebilir.

## 6. Sampling

Brave Sampler, hangi request'lerin trace edileceğini belirler.

Local learning ortamında yüksek sampling oranı tercih edilebilir.

Production'da maliyet ve troubleshooting ihtiyacı dengelenmelidir.

## 7. Brave ile Zipkin farkı

Brave:
> Application-side tracing library.

Zipkin:
> Trace'leri ingest eden, saklayan ve görselleştiren tracing backend/UI.

Aynı teknoloji değildir.

## 8. Brave ile Micrometer Tracing farkı

Micrometer Tracing:
> abstraction/integration layer.

Brave:
> concrete tracing implementation.

## 9. Bu projede nasıl kullanılıyor?

Day 5B'de Micrometer Brave bridge üzerinden trace/span generation ve propagation için kullanılmaktadır.

Zipkin backend'e trace gönderimi doğrulanmıştır.

## 10. Avantajları

- mature Java tracing library
- Zipkin ecosystem compatibility
- context propagation
- Spring/Micrometer integration

## 11. Trade-off'ları

- concrete library knowledge gerektirir
- future OpenTelemetry migration'ında farklı semantics değerlendirilebilir
- sampling/propagation yanlış ayarlanırsa trace kalitesi düşer

## 12. Production considerations

- propagation format
- sampling
- thread/context propagation
- async boundary handling
- reporter queue
- exporter failure policy

## 13. İleri öğrenme konuları

- Brave instrumentation
- CurrentTraceContext
- propagation factories
- custom span handlers
- OpenTelemetry comparison
