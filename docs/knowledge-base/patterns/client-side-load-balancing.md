# Client-Side Load Balancing Pattern

**Category:** Pattern  
**Introduced:** Day 6C  
**Project status:** Implemented / Verified  
**Scope:** Selecting one instance from discovered service instances

## 1. Nedir?

Client-Side Load Balancing, caller'ın service registry'den aldığı instance listesi içinden hedef instance'ı kendisinin seçmesi yaklaşımıdır.

## 2. Problem

Bir service birden fazla instance ile çalışıyorsa caller hangi instance'a request göndereceğini seçmelidir.

## 3. Akış

```text
Client
  |
  v
Service Registry
  |
  v
Instance List
  |
  v
Client-Side Load Balancer
  |
  v
Selected Instance
```

## 4. Bu projede nasıl kullanılıyor?

Day 6C'de Spring Cloud LoadBalancer:
- Gateway route'larında
- OpenFeign çağrılarında

Eureka registry ile birlikte kullanılmıştır.

## 5. Avantajları

- central proxy zorunlu değildir
- instance awareness client'tadır
- dynamic discovery ile doğal çalışır

## 6. Dezavantajları

- client library dependency
- policy consistency problemi
- her caller'ın load balancing davranışını yönetme ihtiyacı

## 7. Algoritmalar

Yaygın:
- round-robin
- random
- weighted
- least-connections benzeri stratejiler

Framework desteğine göre değişir.

## 8. Server-Side Load Balancing farkı

Client-side:
> caller seçer.

Server-side:
> reverse proxy/load balancer seçer.

## 9. Production considerations

- health awareness
- zone awareness
- retries
- sticky sessions
- connection pooling
- balancing metrics

## 10. İleri öğrenme konuları

- weighted routing
- locality-aware routing
- adaptive load balancing
- service mesh
