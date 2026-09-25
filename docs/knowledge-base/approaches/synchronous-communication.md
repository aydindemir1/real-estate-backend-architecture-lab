# Synchronous Communication

**Category:** Approach  
**Introduced:** Day 3  
**Project status:** Implemented / Integrated / Verified  
**Scope:** Request/response service-to-service communication

## 1. Nedir?

Synchronous Communication, bir bileşenin başka bir bileşene request gönderip response dönene kadar sonucu beklediği iletişim yaklaşımıdır.

Distributed systems bağlamında en yaygın örnek HTTP/REST çağrısıdır.

```text
Service A
   |
   | request
   v
Service B
   |
   | response
   v
Service A continues
```

## 2. Hangi problemi çözer?

Bir servis başka bir servisten o anda:
- veri,
- doğrulama,
- karar,
- işlem sonucu

almak zorundaysa request/response modeli doğal ve anlaşılır bir çözüm sunar.

## 3. Ne işe yarar?

- immediate response sağlar
- caller'ın sonucu aynı flow içinde kullanmasına izin verir
- API contract'larını açık hale getirir
- business request zincirlerini kolay anlaşılır yapar
- debugging'i başlangıçta basitleştirir

## 4. Hangi senaryolarda kullanılır?

- kullanıcı response bekliyorsa
- downstream sonucu olmadan işlem devam edemiyorsa
- düşük latency kritikse
- request/response semantiği doğal ise
- consistency beklentisi aynı request boundary içinde ise

Örnek:
- AuthService kayıt sonrası UserProfileService çağrısı
- Gateway'in downstream service'e yönlendirmesi

## 5. Hangi senaryolarda dikkatli kullanılmalıdır?

Uzun service chain'lerde:

```text
A -> B -> C -> D
```

şu riskler oluşur:
- latency accumulation
- cascading failure
- timeout propagation
- thread/resource blocking
- availability dependency

## 6. Temel kavramlar

- Request
- Response
- Timeout
- Connection
- Retry
- Circuit Breaker
- Client-side Load Balancing
- Service Discovery
- Contract

## 7. Çalışma mekanizması

Tipik akış:

```text
Caller
  |
  | HTTP request
  v
Target Service
  |
  | business processing
  v
Response
  |
  v
Caller resumes
```

Caller target service'in response süresine bağımlıdır.

## 8. Avantajları

- basit mental model
- immediate feedback
- hata semantiği doğrudan caller'a dönebilir
- debugging görece kolay
- transactional-looking workflow'lar için anlaşılır

## 9. Dezavantajları ve trade-off'ları

- temporal coupling
- availability coupling
- network failure
- latency
- timeout yönetimi
- retry amplification
- cascading failure riski

## 10. Reliability ile ilişkisi

Synchronous communication tek başına güvenilir değildir.

Genellikle şu mekanizmalar gerekir:
- timeout
- circuit breaker
- bulkhead
- retry
- fallback
- idempotency
- service discovery
- load balancing

## 11. Bu projede nasıl kullanılıyor?

Day 3'te AuthService -> UserProfileService iletişimi Spring Cloud OpenFeign ile synchronous HTTP olarak kurulmuştur.

Day 6C'de sabit URL yerine:
- Eureka
- Spring Cloud LoadBalancer
- service-name resolution

kullanılmıştır.

Gateway -> service communication da synchronous HTTP'dir.

## 12. İlgili teknolojiler

- HTTP
- REST
- Spring MVC
- Spring Cloud OpenFeign
- Spring Cloud Gateway
- Eureka
- Spring Cloud LoadBalancer
- Resilience4j

## 13. Alternatifleri

- Asynchronous Messaging
- Event Streaming
- gRPC
- GraphQL aggregation
- shared read model

## 14. Production considerations

- explicit timeout zorunlu olmalıdır
- retry her katmanda yapılmamalıdır
- retry owner tanımlanmalıdır
- idempotent operation tercih edilmelidir
- correlation/trace propagation korunmalıdır
- downstream availability dependency ölçülmelidir

## 15. İleri öğrenme konuları

- timeout budgeting
- hedging
- retry storms
- backpressure
- client-side vs server-side load balancing
- service mesh
