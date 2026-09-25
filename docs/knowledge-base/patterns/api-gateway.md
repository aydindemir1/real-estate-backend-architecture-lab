# API Gateway Pattern

**Category:** Pattern  
**Introduced:** Day 5A  
**Project status:** Implemented / Integrated / Verified  
**Scope:** Single entry point for external clients

## 1. Nedir?

API Gateway Pattern, external client'ların doğrudan tüm microservice'lere erişmesi yerine tek bir edge component üzerinden sisteme girmesini sağlar.

## 2. Hangi problemi çözer?

Client her service'in:
- adresini,
- portunu,
- routing bilgisini,
- protocol detayını

bilmek zorunda kalırsa coupling artar.

Gateway bu karmaşıklığı edge katmanında toplar.

## 3. Temel mimari

```text
Client
  |
  v
API Gateway
  |
  +--> AuthService
  +--> UserProfileService
  +--> AgentService
  +--> BuyerService
  +--> PropertyService
  +--> SellerService
```

## 4. Ne işe yarar?

- centralized routing
- edge security
- rate limiting
- protocol adaptation
- request filtering
- response transformation
- cross-cutting concern centralization

## 5. Gateway ne yapmamalıdır?

Gateway business logic merkezi olmamalıdır.

Kaçınılması gerekenler:
- domain rule çalıştırmak
- business transaction yönetmek
- canonical data tutmak
- service ownership'i bozmak

## 6. Route kavramı

Route genellikle:
- Predicate
- Filter
- Target URI

bileşenlerinden oluşur.

## 7. Bu projede nasıl kullanılıyor?

Day 5A'da ApiGatewayService eklenmiştir.

Day 6C itibarıyla route'lar sabit URL yerine:
- Eureka
- Spring Cloud LoadBalancer
- `lb://service-name`

üzerinden çözülmektedir.

## 8. Avantajları

- client coupling azalır
- centralized edge policy
- service topology gizlenir
- routing yönetimi kolaylaşır

## 9. Dezavantajları

- gateway critical dependency olabilir
- yanlış tasarım bottleneck yaratabilir
- business logic gateway'e sızabilir
- operational complexity artar

## 10. Production considerations

- HA
- rate limiting
- TLS termination
- auth
- timeout
- circuit breaker
- observability
- request size limits
- abuse protection

## 11. İlgili pattern'ler

- Service Discovery
- Circuit Breaker
- Rate Limiting
- Backend for Frontend
- Edge Authentication

## 12. Alternatifleri

- direct service exposure
- service mesh ingress
- reverse proxy

## 13. İleri öğrenme konuları

- BFF
- API composition
- gateway caching
- zero-trust edge
- WAF integration
