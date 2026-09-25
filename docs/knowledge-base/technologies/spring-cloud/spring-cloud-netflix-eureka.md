# Spring Cloud Netflix Eureka

**Category:** Technology  
**Introduced:** Day 6B  
**Project status:** Implemented / Integrated / Verified  
**Scope:** Service Registry and discovery infrastructure

## 1. Nedir?

Spring Cloud Netflix Eureka, Netflix Eureka service registry/discovery sistemini Spring Boot uygulamalarıyla entegre eden Spring Cloud bileşenidir.

## 2. Ana bileşenler

### Eureka Server

Service instance registry'sini tutar.

### Eureka Client

Application'ın registry'ye register olmasını ve diğer service instance bilgilerini almasını sağlar.

## 3. Runtime flow

```text
Service Instance
    |
    | register
    | heartbeat
    v
Eureka Server
    |
    | registry
    v
Client / Gateway
```

## 4. Registration

Service:
- application name
- host
- port
- metadata

ile registry'ye kaydolur.

## 5. Heartbeat / Lease

Client belirli aralıklarla heartbeat gönderir.

Registry belirli lease süresinde heartbeat alamazsa instance'ı unavailable kabul edebilir.

## 6. Registry fetch

Client registry'deki available service instance'larını alabilir ve local cache tutabilir.

## 7. Bu projede nasıl kullanılıyor?

Day 6B'de EurekaServer modülü eklenmiştir.

Registry'de:
- ApiGatewayService
- AuthService
- UserProfileService
- AgentService
- BuyerService
- PropertyService
- SellerService

UP olarak doğrulanmıştır.

## 8. Day 6C entegrasyonu

Eureka yalnız registry olarak kalmamış, Spring Cloud LoadBalancer ile runtime service-name resolution için kullanılmıştır.

## 9. Self-preservation ve consistency

Eureka availability-oriented tasarım yaklaşımına sahiptir.

Network partition veya heartbeat sorunu durumlarında registry davranışı strong-consistency database gibi değerlendirilmemelidir.

## 10. Avantajları

- Spring Cloud entegrasyonu
- easy local microservice discovery
- dashboard
- dynamic instance registration
- LoadBalancer/Feign integration

## 11. Dezavantajları

- Kubernetes gibi platformlarda native discovery redundant olabilir
- registry ayrı operational component'tir
- stale entry ihtimali
- lease tuning

## 12. Production considerations

- HA
- peer servers
- lease tuning
- DNS/network
- secure dashboard
- metrics
- stale registration

## 13. Alternatifleri

- Consul
- ZooKeeper
- Kubernetes Service/DNS
- service mesh

## 14. Bu projedeki roadmap konumu

Local/Spring Cloud learning ortamında Eureka bilinçli olarak kullanılmaktadır.

Native Kubernetes aşamasından sonra platform-native discovery ile karşılaştırılacaktır.

Eureka vs Consul vs ZooKeeper comparison mandatory learning kapsamındadır.

## 15. İleri öğrenme konuları

- peer replication
- self-preservation
- eviction
- registry caching
- zone affinity
