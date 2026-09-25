# Spring Cloud Gateway

**Category:** Technology  
**Introduced:** Day 5A  
**Project status:** Implemented / Integrated / Verified  
**Scope:** API Gateway implementation

## 1. Nedir?

Spring Cloud Gateway, Spring ekosisteminde API Gateway ve routing capability'leri sağlayan Spring Cloud bileşenidir.

Bu projede Gateway Server Web MVC yaklaşımı kullanılmaktadır.

## 2. Ana görevleri

- request routing
- predicate evaluation
- filter execution
- cross-cutting edge concerns
- service-name based routing
- resilience integration

## 3. Temel kavramlar

### Route

Bir request'in hangi target'a yönleneceğini tanımlar.

### Predicate

Route'un hangi request'lerde eşleşeceğini belirler.

### Filter

Request/response üzerinde işlem yapar.

## 4. Runtime flow

```text
Client
  |
  v
Gateway
  |
  v
Predicate Match
  |
  v
Filter Chain
  |
  v
LoadBalancer / Service Discovery
  |
  v
Target Service
```

## 5. Bu projede nasıl kullanılıyor?

Day 5A'da ApiGatewayService eklenmiştir.

Routes:
- /auth/**
- /user/**
- /agent/**
- /buyer/**
- /property/**
- /seller/**

ile ilgili service'lere yönlendirme yapılmıştır.

Day 6C itibarıyla target URI'lar:

```text
lb://auth-service
lb://user-profile-service
...
```

şeklindedir.

## 6. Circuit Breaker entegrasyonu

Gateway route'ları Spring Cloud Circuit Breaker ile sarılmıştır.

Failure durumunda fallback handler çalışabilir.

## 7. Gateway ne yapmamalıdır?

- domain business logic
- canonical data ownership
- cross-service transaction
- domain invariant

Gateway yalnız edge responsibility taşımalıdır.

## 8. Avantajları

- centralized routing
- client topology isolation
- security/rate-limit için uygun edge point
- observability için ortak ingress
- service discovery integration

## 9. Dezavantajları

- bottleneck riski
- central dependency
- routing complexity
- edge logic'in büyüme riski

## 10. Production considerations

- horizontal scale
- TLS
- rate limiting
- auth
- request size limit
- timeout
- circuit breaker
- metrics
- tracing
- correlation ID
- security headers

## 11. Alternatifleri

- NGINX
- Envoy
- APISIX
- Kubernetes Ingress/Gateway API
- managed API gateways

## 12. İleri öğrenme konuları

- global filters
- custom predicates
- rate limiting
- token relay
- CORS
- gateway observability
