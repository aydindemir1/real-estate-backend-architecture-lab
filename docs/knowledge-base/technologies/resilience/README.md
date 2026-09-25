# Resilience

Failure isolation, graceful degradation ve remote-call dayanıklılığı için kullanılan teknolojiler.

## Day 1–7

- [Resilience4j](resilience4j.md)

## Cross-reference

Spring Cloud Circuit Breaker canonical dokümanı:

`../spring-cloud/spring-cloud-circuit-breaker.md`

Bu ayrım bilinçlidir:

- Circuit Breaker -> Pattern
- Spring Cloud Circuit Breaker -> Spring abstraction/integration
- Resilience4j -> Concrete resilience library

## Sonraki roadmap kapsamı

İleri resilience milestone'larında:
- Retry
- TimeLimiter
- Bulkhead
- RateLimiter
- timeout/retry ownership
- failure classification
- resilience metrics

daha derin uygulanacak ve mevcut canonical doküman gerektiğinde genişletilecektir.
