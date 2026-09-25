# HTTP

**Category:** Protocol  
**Introduced:** Day 1  
**Project status:** Implemented / Verified  
**Scope:** Request/response application protocol for REST APIs and internal service calls

## 1. Nedir?

HTTP (Hypertext Transfer Protocol), client ve server arasında request/response tabanlı iletişim sağlayan application-layer protocol'dür.

Bu projede:
- public REST API,
- Gateway routing,
- OpenFeign service-to-service calls,
- Actuator endpoints,
- Config Server access

gibi çok sayıda iletişim HTTP üzerinden gerçekleşir.

## 2. Temel request yapısı

Bir HTTP request tipik olarak:
- method
- target/path
- headers
- optional body

içerir.

Örnek:

```http
POST /api/v1/auth/login
Content-Type: application/json
Authorization: Bearer ...
```

## 3. Temel response yapısı

Response:
- status code
- headers
- optional body

taşır.

## 4. HTTP method'ları

Yaygın:
- GET
- POST
- PUT
- PATCH
- DELETE
- HEAD
- OPTIONS

Method semantics bilinçli kullanılmalıdır.

## 5. Safe ve idempotent method kavramları

Safe method:
> State değiştirmemesi beklenir.

Idempotent method:
> Aynı request'in birden fazla kez uygulanması, tek uygulamayla aynı intended state sonucunu üretir.

Bu kavramlar retry safety açısından önemlidir.

## 6. Status code sınıfları

- 1xx -> informational
- 2xx -> success
- 3xx -> redirection
- 4xx -> client/request problem
- 5xx -> server/dependency problem

Bu projede API error semantics ayrıca standardize edilmiştir.

## 7. Header'lar

Header'lar:
- content negotiation
- authentication
- caching
- tracing
- correlation
- conditional requests

gibi metadata taşır.

Örnek:
- Content-Type
- Accept
- Authorization
- Traceparent
- Cache-Control

## 8. Statelessness ile ilişkisi

HTTP'nin kendisi request/response protokolüdür.

Application'ın stateless olup olmaması ayrı architectural karardır.

## 9. HTTP/1.1, HTTP/2, HTTP/3

### HTTP/1.1
Persistent connections ve request/response modeli.

### HTTP/2
Multiplexing, header compression ve binary framing sağlar.

### HTTP/3
QUIC üzerinde çalışır ve transport-level latency/failure özelliklerini değiştirir.

## 10. Bu projede nasıl kullanılıyor?

Spring MVC endpoint'leri HTTP API sunar.

OpenFeign internal synchronous HTTP çağrıları yapar.

Spring Cloud Gateway HTTP request'lerini downstream service'lere route eder.

## 11. Timeout

Network call sonsuza kadar beklememelidir.

Connection ve read timeout'lar ayrı düşünülmelidir.

## 12. Retry

HTTP retry yalnız:
- transient failure,
- idempotency,
- operation semantics

değerlendirildikten sonra uygulanmalıdır.

## 13. Security

Production HTTP communication:
- TLS
- authentication
- authorization
- secure headers
- input validation

ile korunmalıdır.

## 14. Observability

HTTP boundary üzerinde:
- trace context
- correlation id
- latency
- status code
- request volume

izlenebilir.

## 15. İlgili kavramlar

- REST
- JSON
- OpenFeign
- Spring MVC
- API Gateway
- OAuth2 bearer token

## 16. İleri öğrenme konuları

- HTTP caching
- conditional requests
- HTTP/2 multiplexing
- HTTP/3/QUIC
- connection pooling
- keep-alive
- proxy semantics
