# Service Registry Pattern

**Category:** Pattern  
**Introduced:** Day 6B  
**Project status:** Implemented / Verified  
**Scope:** Runtime registry of service instances

## 1. Nedir?

Service Registry, çalışan service instance'larının network location bilgilerinin merkezi veya dağıtık bir registry içinde tutulduğu pattern'dir.

## 2. Problem

Dynamic environment'ta service instance:
- IP değiştirebilir
- port değiştirebilir
- scale olabilir
- restart olabilir

Hard-coded address bu yapıya uymaz.

## 3. Temel mimari

```text
Service Instance
   |
   | register / heartbeat
   v
Service Registry
   |
   | query
   v
Client / Gateway
```

## 4. Temel kavramlar

- registration
- lease
- heartbeat
- instance metadata
- deregistration
- health
- registry replication

## 5. Bu projede nasıl kullanılıyor?

Day 6B'de Spring Cloud Netflix Eureka Server eklenmiştir.

Şu servisler Eureka Client olarak register edilmiştir:
- ApiGatewayService
- AuthService
- UserProfileService
- AgentService
- BuyerService
- PropertyService
- SellerService

## 6. Service Registry ile Service Discovery farkı

Registry:
> Instance bilgilerini tutan sistem.

Discovery:
> Bu registry kullanılarak instance bulma süreci/pattern'i.

## 7. Avantajları

- dynamic addressing
- scaling support
- decoupled location management
- failover candidate discovery

## 8. Dezavantajları

- registry dependency
- stale registration
- heartbeat/lease tuning
- split-brain considerations

## 9. Production considerations

- HA
- lease expiry
- stale instance eviction
- registry replication
- startup ordering
- observability

## 10. Alternatifleri

- Kubernetes Service/DNS
- Consul
- ZooKeeper
- static DNS

## 11. İleri öğrenme konuları

- self-registration vs third-party registration
- platform-native discovery
- registry consistency
