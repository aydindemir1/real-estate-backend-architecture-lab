# REST

**Category:** Architectural Style  
**Introduced:** Day 1  
**Project status:** Implemented / Verified  
**Scope:** Resource-oriented HTTP API design

## 1. Nedir?

REST (Representational State Transfer), distributed hypermedia systems için tanımlanmış architectural style'dır.

REST bir network protocol değildir.

Bu projede HTTP tabanlı API'ler REST-oriented endpoint tasarımıyla geliştirilir.

## 2. Temel constraint'ler

REST'in klasik constraint'leri:
- Client-Server
- Stateless
- Cacheable
- Uniform Interface
- Layered System
- Code-on-Demand (optional)

## 3. Resource kavramı

REST'te odak operation isminden çok resource üzerindedir.

Örnek:

```text
/users/{id}
/properties/{id}
/offers/{id}
```

## 4. Representation

Resource farklı representation ile taşınabilir.

Bu projede yaygın representation JSON'dur.

## 5. Uniform Interface

Uniform interface:
- resource identification
- representation-based manipulation
- self-descriptive messages
- hypermedia as engine of application state

gibi prensipleri kapsar.

Pratik REST API'lerde HATEOAS her zaman tam uygulanmaz.

## 6. HTTP method semantics

Yaygın mapping:
- GET -> read
- POST -> create/command
- PUT -> replace/idempotent update
- PATCH -> partial update
- DELETE -> remove

Business semantics'e göre bilinçli tasarım gerekir.

## 7. Status code semantics

API yalnız 200/500 kullanmamalıdır.

Bu proje error modelinde:
- 400 malformed request
- 401 unauthenticated
- 403 forbidden
- 404 not found
- 409 conflict
- 422 semantic validation
- 429 rate limit
- 500/503/504 infrastructure/server failure

ayrımı benimsenmiştir.

## 8. REST ile RPC farkı

REST resource-oriented'dır.

RPC operation/function çağrısı modeline daha yakındır.

Örnek:

REST:
```text
POST /offers
```

RPC:
```text
POST /createOffer
```

Her iki stilin de uygun kullanım alanları vardır.

## 9. REST ile HTTP farkı

HTTP:
> Protocol.

REST:
> Architectural style.

REST çoğunlukla HTTP üzerinde uygulanır ama aynı şey değildir.

## 10. Bu projede nasıl kullanılıyor?

Auth, UserProfile ve diğer business service'ler HTTP REST API sunar.

Gateway external entry point olarak bu endpoint'lere route eder.

OpenFeign internal REST çağrıları için kullanılır.

## 11. API versioning

Public API boundary'de versioning planlanabilir.

Örnek:
```text
/api/v1/...
```

Internal service endpoint'leri her zaman aynı public prefix'i taşımak zorunda değildir.

## 12. Avantajları

- ubiquitous HTTP tooling
- simple interoperability
- cache semantics
- human-readable API
- broad ecosystem

## 13. Trade-off'ları

- chatty APIs
- over-fetch/under-fetch
- complex workflows için endpoint proliferation
- strict REST purity business ergonomisini bozabilir

## 14. Production considerations

- pagination
- versioning
- idempotency
- error model
- security
- caching
- rate limiting
- observability
- API contract governance

## 15. Alternatifleri / tamamlayıcıları

- gRPC
- GraphQL
- messaging
- event streaming

## 16. İleri öğrenme konuları

- HATEOAS
- conditional requests
- ETags
- idempotency keys
- API maturity models
