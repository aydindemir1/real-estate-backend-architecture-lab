# Resilience4j

**Category:** Technology  
**Introduced:** Day 5A  
**Project status:** Implemented / Verified  
**Scope:** Fault tolerance and resilience library for Java applications

## 1. Nedir?

Resilience4j, Java uygulamalarında remote call ve distributed system failure'larına karşı dayanıklılık mekanizmaları sağlayan lightweight resilience library'dir.

Spring Cloud Circuit Breaker gibi abstraction'ların altında concrete implementation olarak kullanılabilir.

## 2. Hangi problemi çözer?

Distributed system'lerde:
- timeout,
- transient failure,
- downstream outage,
- saturation,
- overload,
- cascading failure

kaçınılmazdır.

Resilience4j bu failure türlerine karşı kontrollü davranış sağlamaya yardımcı olur.

## 3. Ana modüller

Resilience4j aşağıdaki temel resilience primitive'lerini sağlar:

- CircuitBreaker
- Retry
- RateLimiter
- Bulkhead
- TimeLimiter
- Cache

Bu primitive'ler birbirinden bağımsızdır; her biri farklı failure mode'u hedefler.

## 4. Circuit Breaker

Circuit Breaker, başarısız çağrıların sürekli downstream'e gönderilmesini engeller.

State modeli:

```text
CLOSED
  |
  | failure threshold exceeded
  v
OPEN
  |
  | wait duration elapsed
  v
HALF_OPEN
  |
  +--> success -> CLOSED
  +--> failure -> OPEN
```

## 5. Retry

Retry, transient failure ihtimalinde operation'ı tekrar dener.

Dikkat:
- her failure retry edilmemelidir
- retry sayısı sınırlı olmalıdır
- retry storm oluşturulmamalıdır
- idempotency değerlendirilmelidir

## 6. Rate Limiter

Rate Limiter, belirli zaman aralığında izin verilen request sayısını sınırlar.

Amaç:
- overload koruması
- fair usage
- downstream protection

## 7. Bulkhead

Bulkhead, failure'ın tüm thread/resource pool'u tüketmesini engellemek için resource isolation sağlar.

İki yaygın yaklaşım:
- semaphore-based bulkhead
- thread-pool bulkhead

## 8. Time Limiter

Uzun süren operation'ların sınırsız beklemesini önler.

Timeout policy'nin caller/downstream chain içinde tek ve tutarlı ownership'i olmalıdır.

## 9. Neden tek başına Circuit Breaker yeterli değildir?

Circuit Breaker yalnız belirli failure pattern'ini çözer.

Gerçek resilience tasarımı genellikle:
- timeout
- retry
- circuit breaker
- bulkhead
- rate limiting
- fallback

kombinasyonunu gerektirir.

Bu mekanizmaların hepsini aynı anda ve rastgele eklemek doğru değildir.

## 10. Spring Cloud Circuit Breaker ile ilişkisi

Katman:

```text
Spring Cloud Circuit Breaker
        |
        v
Resilience4j
        |
        v
Concrete resilience behavior
```

Spring Cloud Circuit Breaker abstraction/integration katmanıdır.

Resilience4j concrete resilience implementation'dır.

## 11. Bu projede nasıl kullanılıyor?

Day 5A'da Spring Cloud Gateway route'ları:

- Spring Cloud Circuit Breaker
- Resilience4j
- fallback

ile korunmuştur.

Bu aşamada odak Circuit Breaker capability'sidir.

Retry, TimeLimiter, Bulkhead ve RateLimiter ileri resilience milestone'larında daha sistematik biçimde ele alınacaktır.

## 12. Configuration

Resilience4j davranışı şu parametrelerle şekillendirilebilir:

- failureRateThreshold
- slowCallRateThreshold
- slidingWindowType
- slidingWindowSize
- minimumNumberOfCalls
- waitDurationInOpenState
- permittedNumberOfCallsInHalfOpenState

Yanlış tuning sistem davranışını kötüleştirebilir.

## 13. Failure classification

Her exception circuit breaker failure sayılmamalıdır.

Örneğin:
- validation error
- business rejection
- client 4xx error

ile:
- timeout
- connection failure
- 5xx dependency failure

aynı şekilde değerlendirilmemelidir.

## 14. Metrics ve observability

Resilience kararları görünür olmalıdır.

İzlenebilecek metrikler:
- circuit breaker state
- successful calls
- failed calls
- slow calls
- rejected calls
- retry attempts

Actuator/Micrometer entegrasyonu bu görünürlüğü sağlar.

## 15. Avantajları

- lightweight
- modular
- Java/Spring ecosystem integration
- explicit resilience primitives
- metrics integration
- functional/decorator style support

## 16. Dezavantajları / trade-off'ları

- yanlış configuration false protection yaratabilir
- retry + circuit breaker kombinasyonu karmaşıktır
- fallback gerçek failure'ı gizleyebilir
- her service'in kendi policy'sini yönetmesi governance ihtiyacı doğurur

## 17. Anti-pattern'ler

- timeout olmadan retry
- tüm exception'ları retry etmek
- her layer'da retry yapmak
- circuit breaker threshold'larını ölçmeden seçmek
- business error'ları infrastructure failure saymak
- fallback ile başarısız operation'ı success gibi göstermek

## 18. Production considerations

- timeout budget
- retry ownership
- idempotency
- circuit breaker tuning
- slow-call thresholds
- metrics/alerts
- bulkhead sizing
- rate limit policy
- downstream SLO
- chaos/failure testing

## 19. Alternatifleri

- Spring Retry
- Envoy/service mesh resilience
- API Gateway level policies
- cloud provider resilience policies

## 20. İleri öğrenme konuları

- Retry
- TimeLimiter
- SemaphoreBulkhead
- ThreadPoolBulkhead
- RateLimiter
- composition ordering
- retry budget
- adaptive resilience
- failure injection
