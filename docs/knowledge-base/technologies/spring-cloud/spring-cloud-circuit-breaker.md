# Spring Cloud Circuit Breaker

**Category:** Technology  
**Introduced:** Day 5A  
**Project status:** Implemented / Verified  
**Scope:** Abstraction for circuit breaker implementations

## 1. Nedir?

Spring Cloud Circuit Breaker, farklı circuit breaker implementation'larını ortak Spring abstraction arkasında kullanmayı sağlayan Spring Cloud projesidir.

Kendisi circuit breaker algorithm'ının tek implementation'ı değildir.

Bu projede Resilience4j implementation olarak kullanılır.

## 2. Katman ilişkisi

```text
Application / Gateway
       |
       v
Spring Cloud Circuit Breaker API
       |
       v
Resilience4j
       |
       v
Circuit Breaker State Machine
```

## 3. Neden abstraction vardır?

Application code'un belirli resilience library'sine sıkı bağımlılığını azaltır ve Spring Cloud integrations için ortak model sağlar.

## 4. Bu projede nasıl kullanılıyor?

Day 5A'da Gateway route'larında:
- Spring Cloud Circuit Breaker
- Resilience4j
- fallback

birlikte kullanılmıştır.

## 5. Pattern ile technology farkı

Circuit Breaker:
> Pattern.

Spring Cloud Circuit Breaker:
> Bu pattern için Spring abstraction/integration teknolojisi.

Resilience4j:
> Concrete resilience library.

## 6. Avantajları

- Spring integration
- implementation abstraction
- Gateway entegrasyonu
- configuration management

## 7. Trade-off'ları

Abstraction altında kullanılan concrete implementation'ın:
- metrics
- state machine
- threshold semantics
- configuration

detayları yine bilinmelidir.

## 8. Production considerations

- failure classification
- threshold tuning
- metrics
- fallback semantics
- retry interaction
- timeout ownership

## 9. İlgili teknolojiler

- Resilience4j
- Spring Cloud Gateway
- Spring Boot Actuator

## 10. İleri öğrenme konuları

- CircuitBreakerFactory
- customizer
- reactive vs imperative integration
- metrics tagging
