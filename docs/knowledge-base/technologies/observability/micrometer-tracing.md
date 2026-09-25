# Micrometer Tracing

**Category:** Technology  
**Introduced:** Day 5B  
**Project status:** Implemented / Integrated / Verified  
**Scope:** Tracing abstraction and observation integration

## 1. Nedir?

Micrometer Tracing, Spring ekosisteminde distributed tracing için ortak abstraction sağlayan Micrometer bileşenidir.

Application code ile concrete tracing implementation arasında abstraction katmanı oluşturur.

## 2. Temel rolü

```text
Spring Application
      |
      v
Micrometer Observation / Tracing API
      |
      v
Tracing Bridge
      |
      v
Brave veya OpenTelemetry
      |
      v
Trace Exporter / Backend
```

## 3. Hangi problemi çözer?

Application'ın doğrudan tek bir tracing library'sine sıkı bağlanmasını azaltır.

Spring framework instrumentation'larının ortak observation/tracing modeli üzerinden çalışmasını sağlar.

## 4. Temel kavramlar

- Observation
- Trace
- Span
- Tracer
- Propagator
- Baggage
- Context
- Sampling

## 5. Observation ile tracing ilişkisi

Micrometer Observation daha genel bir instrumentation abstraction'ıdır.

Bir observation:
- metric üretebilir,
- trace/span üretebilir,
- contextual metadata taşıyabilir.

## 6. Trace propagation

Bir request service sınırını geçtiğinde trace context:
- HTTP header
- messaging header

üzerinden taşınmalıdır.

Aksi durumda trace parçalanır.

## 7. Bu projede nasıl kullanılıyor?

Day 5B'de:
- Micrometer Tracing
- Brave bridge
- Zipkin reporter

ile distributed tracing foundation kurulmuştur.

Gateway -> Auth -> UserProfile synchronous flow aynı trace altında doğrulanmıştır.

Day 6A'da RabbitMQ producer/consumer trace propagation da doğrulanmıştır.

## 8. Sampling

Local learning ortamında bütün trace'leri görmek için sampling probability yüksek tutulabilir.

Production'da tüm request'leri trace etmek:
- storage cost,
- network traffic,
- processing overhead

yaratabilir.

## 9. Avantajları

- vendor-neutral abstraction'a yaklaşır
- Spring integration güçlüdür
- HTTP ve messaging instrumentation ile uyumludur
- tracing backend migration'ını kolaylaştırabilir

## 10. Trade-off'ları

- abstraction altında concrete tracer davranışı yine bilinmelidir
- context propagation hataları hidden olabilir
- yüksek cardinality tag'ler maliyetlidir

## 11. Production considerations

- sampling policy
- PII redaction
- baggage control
- propagation format
- exporter failure handling
- trace retention
- high-cardinality attributes

## 12. Brave ile ilişkisi

Micrometer Tracing abstraction'dır.

Brave bu abstraction'ın altında çalışan concrete tracing implementation/bridge olarak kullanılabilir.

## 13. OpenTelemetry ile ilişkisi

Roadmap'in ileri aşamasında OpenTelemetry'ye geçiş planlanmaktadır.

Bu geçişte Micrometer Observation/Tracing yaklaşımı integration boundary olarak yardımcı olabilir.

## 14. İleri öğrenme konuları

- ObservationRegistry
- custom observations
- baggage
- propagation
- exemplars
- OpenTelemetry bridge
