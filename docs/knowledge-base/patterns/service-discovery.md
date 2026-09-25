# Service Discovery Pattern

**Category:** Pattern  
**Introduced:** Day 6B / Integrated Day 6C  
**Project status:** Implemented / Integrated / Verified  
**Scope:** Discovering runtime service instances

## 1. Nedir?

Service Discovery, bir caller'ın target service'in network location bilgisini runtime sırasında çözümlemesini sağlayan pattern'dir.

## 2. Problem

Microservice instance'larının IP/port değerleri sabit olmayabilir.

Caller'ın hard-coded adres bilmesi deployment coupling yaratır.

## 3. Discovery modelleri

### Client-Side Discovery

Client registry'yi sorgular ve instance seçer.

### Server-Side Discovery

Client load balancer/proxy'e gider; target instance seçimini proxy yapar.

## 4. Bu projede model

Eureka + Spring Cloud LoadBalancer ile client-side discovery kullanılmaktadır.

Gateway de `lb://service-name` route'ları ile discovery kullanır.

## 5. Runtime akışı

```text
Caller
  |
  v
Service Name
  |
  v
Eureka Registry
  |
  v
Available Instances
  |
  v
LoadBalancer
  |
  v
Selected Instance
```

## 6. Avantajları

- hard-coded endpoint kaldırır
- dynamic scaling destekler
- instance replacement kolaylaşır

## 7. Riskler

- stale registry
- discovery latency
- registry outage
- unhealthy instance selection

## 8. Production considerations

- health integration
- TTL/lease
- cache strategy
- load balancing
- platform discovery compatibility

## 9. Alternatifleri

- Kubernetes Service + DNS
- Consul
- service mesh
- static DNS

## 10. İleri öğrenme konuları

- server-side discovery
- DNS-based discovery
- Kubernetes discovery
- service mesh discovery
