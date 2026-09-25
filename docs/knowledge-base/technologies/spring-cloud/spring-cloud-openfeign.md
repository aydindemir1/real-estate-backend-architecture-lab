# Spring Cloud OpenFeign

**Category:** Technology  
**Introduced:** Day 3  
**Project status:** Implemented / Integrated / Verified  
**Scope:** Declarative synchronous HTTP client for service-to-service communication

## 1. Nedir?

Spring Cloud OpenFeign, HTTP client çağrılarını interface tabanlı declarative bir modelle tanımlamayı sağlayan Spring Cloud bileşenidir.

Developer doğrudan low-level HTTP client kodu yazmak yerine service contract'ını Java interface olarak ifade eder.

## 2. Hangi problemi çözer?

Manual HTTP client kullanımında:
- URL oluşturma
- request serialization
- response deserialization
- HTTP method mapping
- boilerplate error handling

gibi tekrar eden kodlar oluşabilir.

OpenFeign bu konuları declarative contract ile sadeleştirir.

## 3. Temel kullanım

```java
@FeignClient(name = "user-profile-service")
public interface IUserProfileManager {

    @PostMapping("/user/save")
    ResponseEntity<Boolean> save(@RequestBody UserProfileSaveRequestDto dto);
}
```

## 4. Çalışma mekanizması

```text
Application Service
       |
       v
Feign Proxy
       |
       v
Request Encoder
       |
       v
HTTP Client
       |
       v
Target Service
       |
       v
Response Decoder
```

Spring runtime'da interface için proxy implementation oluşturur.

## 5. Service Discovery ile entegrasyon

OpenFeign yalnız static URL ile kullanılmak zorunda değildir.

Bu projede Day 6C itibarıyla:

```text
@FeignClient(name = "user-profile-service")
        |
        v
Eureka
        |
        v
Spring Cloud LoadBalancer
        |
        v
UserProfileService Instance
```

şeklinde çalışır.

## 6. Ne işe yarar?

- declarative client
- service contract readability
- Spring MVC annotation reuse
- serialization integration
- LoadBalancer integration
- tracing/observation integration

## 7. Avantajları

- düşük boilerplate
- readable contract
- Spring ecosystem integration
- service-name based invocation
- interceptor ve observation desteği

## 8. Dezavantajları / trade-off'ları

- network call local method call gibi görünebilir
- hidden latency riski
- timeout yanlış configure edilebilir
- retry davranışı yanlış anlaşılabilir
- chatty service communication kolaylaşabilir

## 9. En önemli engineering kuralı

Feign method çağrısı normal Java method çağrısı değildir.

Arkasında:
- network
- serialization
- timeout
- remote failure
- service discovery

vardır.

## 10. Bu projede nasıl kullanılıyor?

Day 3'te AuthService -> UserProfileService synchronous register flow'unda eklenmiştir.

Day 6C'de hard-coded UserProfile URL kaldırılmış, service-name resolution + Eureka + Spring Cloud LoadBalancer entegrasyonu yapılmıştır.

## 11. Tracing entegrasyonu

Day 5B'de Feign çağrılarında trace propagation doğrulanmıştır.

Bu proje baseline'ında `feign-micrometer` ile observation/tracing entegrasyonu kullanılmıştır.

## 12. Production considerations

- connect timeout
- read timeout
- retry ownership
- circuit breaker
- authentication propagation
- tracing headers
- connection pooling
- error decoder
- contract versioning

## 13. Alternatifleri

- RestClient
- WebClient
- RestTemplate (legacy)
- gRPC
- messaging

## 14. İleri öğrenme konuları

- custom interceptors
- ErrorDecoder
- Feign capabilities
- compression
- Micrometer observation
- client-side resilience
