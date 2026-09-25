# SpringDoc OpenAPI

**Category:** Technology  
**Introduced:** Day 1  
**Project status:** Implemented  
**Scope:** OpenAPI documentation generation for Spring HTTP APIs

## 1. Nedir?

SpringDoc OpenAPI, Spring MVC/WebFlux endpoint'lerinden OpenAPI specification üretmeyi ve Swagger UI üzerinden API'leri keşfetmeyi sağlayan library'dir.

## 2. OpenAPI nedir?

OpenAPI, HTTP API contract'ını machine-readable şekilde tanımlayan specification'dır.

Tanımlanabilenler:
- paths
- methods
- request schemas
- response schemas
- parameters
- security schemes
- error responses

## 3. SpringDoc nasıl çalışır?

Spring endpoint metadata'sını ve annotation'ları tarar.

```text
Spring Controllers
      |
      v
SpringDoc Scanner
      |
      v
OpenAPI Document
      |
      v
Swagger UI / JSON
```

## 4. Ne işe yarar?

- API keşfi
- interactive documentation
- contract visibility
- frontend/backend collaboration
- testing kolaylığı
- client generation foundation

## 5. Annotation'lar

Yaygın:
- @Operation
- @Parameter
- @Schema
- @ApiResponse
- @SecurityScheme

## 6. Generated docs yeterli midir?

Hayır.

Sadece controller signature'dan oluşan docs:
- business semantics
- error model
- security requirements
- examples

konusunda yetersiz kalabilir.

## 7. Bu projede nasıl kullanılıyor?

İlk service'lerden itibaren SpringDoc OpenAPI dependency'si kullanılmaktadır.

Day 7'de root build'den global OpenAPI dependency zorlaması kaldırılıp module-local ownership'e geçirilmiştir.

## 8. Avantajları

- hızlı documentation
- standard format
- Swagger UI
- ecosystem compatibility
- contract visibility

## 9. Dezavantajları

- generated docs ile gerçek behavior drift edebilir
- annotation clutter
- internal endpoint exposure riski
- security details yanlış dokümante edilebilir

## 10. Production considerations

- public/internal API ayrımı
- actuator ile karıştırmama
- auth scheme doğru tanımlama
- sensitive endpoint gizleme
- versioning
- stable error schema

## 11. Alternatifleri

- manual OpenAPI YAML
- API-first design
- Spring REST Docs
- AsyncAPI messaging için

## 12. İleri öğrenme konuları

- API-first vs code-first
- schema reuse
- generated clients
- contract testing
- API governance
