# Spring Cloud LoadBalancer

**Category:** Technology  
**Introduced:** Day 6C  
**Project status:** Implemented / Integrated / Verified  
**Scope:** Client-side service instance selection

## 1. Nedir?

Spring Cloud LoadBalancer, service discovery'den elde edilen instance listesi içinden target instance seçimini yapan client-side load balancing çözümüdür.

## 2. Problem

Bir service birden fazla instance ile çalışabilir.

```text
auth-service
  -> instance A
  -> instance B
  -> instance C
```

Caller hangi instance'a gideceğini seçmelidir.

## 3. Runtime flow

```text
Caller
  |
  v
Service Name
  |
  v
DiscoveryClient
  |
  v
Instances
  |
  v
Spring Cloud LoadBalancer
  |
  v
Selected Instance
```

## 4. Bu projede nasıl kullanılıyor?

Day 6C'de:

### Gateway

```text
lb://auth-service
```

### OpenFeign

```text
@FeignClient(name = "user-profile-service")
```

service-name resolution ile kullanılmaktadır.

## 5. Eureka ile ilişkisi

Eureka:
> Instance listesi ve registry.

LoadBalancer:
> Bu listeden instance seçimi.

Aynı teknoloji değildir.

## 6. Client-side load balancing

Instance seçimini caller/client library yapar.

Server-side modelde ise external proxy/load balancer seçim yapar.

## 7. Avantajları

- Spring Cloud entegrasyonu
- dynamic discovery
- Feign/Gateway integration
- central proxy zorunluluğu yok

## 8. Dezavantajları

- client library dependency
- policy her client'ta çalışır
- health/cache semantics dikkat ister

## 9. Load balancing algoritmaları

Spring Cloud LoadBalancer farklı strategy'ler destekleyebilir.

Yaygın yaklaşım:
- round robin
- random

Advanced strategy ihtiyacında custom implementation değerlendirilebilir.

## 10. Production considerations

- health awareness
- instance cache
- retry interaction
- connection pooling
- zone/locality
- metrics
- service discovery freshness

## 11. Ribbon ile ilişkisi

Netflix Ribbon eski Spring Cloud stack'inde client-side load balancing için kullanılıyordu.

Modern Spring Cloud yaklaşımında Spring Cloud LoadBalancer kullanılmalıdır.

## 12. İleri öğrenme konuları

- custom ServiceInstanceListSupplier
- caching
- zone preference
- health-check supplier
- weighted load balancing
