# Observability

Runtime visibility için health, metric ve distributed tracing teknolojileri.

## Day 1–7

- [Spring Boot Actuator](spring-boot-actuator.md)
- [Micrometer Tracing](micrometer-tracing.md)
- [Brave](brave.md)
- [Zipkin](zipkin.md)

## Kavramsal ayrım

- Spring Boot Actuator -> management/health/metric exposure
- Micrometer Tracing -> tracing abstraction ve Spring observation integration
- Brave -> concrete tracing implementation
- Zipkin -> trace backend, storage/query/UI

Bu teknolojiler birbirinin alternatifi değildir; farklı observability katmanlarında görev yaparlar.

## Sonraki roadmap kapsamı

İleri milestone'larda:
- OpenTelemetry
- Prometheus
- Grafana
- Loki
- Tempo

canonical Knowledge Base dokümanları olarak eklenecektir.
