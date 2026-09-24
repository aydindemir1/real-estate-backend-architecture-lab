# API Design Standard

Bu doküman REST, GraphQL ve gRPC contract tasarımı için ortak kalite standardını tanımlar.

Amaç yalnızca endpoint üretmek değil; predictable, evolvable, secure, observable ve backward-compatible API tasarlamaktır.

# 1. REST Resource Naming

URL'ler business resource isimleriyle tasarlanır.

Tercih:

```text
GET  /properties/{propertyId}
POST /sellers/{sellerId}/listing-submissions
POST /buyers/{buyerId}/offers
```

Kaçınılacak:

```text
POST /createProperty
POST /doOffer
GET  /getAllSellers
```

URL'de fiil yalnızca gerçek state transition/action endpoint gerektiğinde kullanılabilir:

```text
POST /properties/{propertyId}/publish
POST /properties/{propertyId}/withdraw
POST /sellers/{sellerId}/offers/{offerId}/accept
```

# 2. HTTP Method Semantics

## GET
Read-only ve side-effect free olmalıdır.

## POST
Yeni resource oluşturma veya non-idempotent command.

## PUT
Resource representation'ın idempotent replacement/upsert senaryosu.

Örnek:
`PUT /buyers/{buyerId}/preferences`

## PATCH
Partial update.

## DELETE
Resource deletion yalnızca business semantics gerçekten deletion ise kullanılır.

`withdraw`, `cancel`, `disable` gibi state transition'lar DELETE ile zorlanmaz.

# 3. HTTP Status Code Standardı

## Success
- 200 OK — başarılı read/update
- 201 Created — yeni resource
- 202 Accepted — asynchronous işlem kabul edildi
- 204 No Content — body gerekmeyen başarılı command

## Client errors
- 400 Bad Request — malformed/validation
- 401 Unauthorized — authentication yok/geçersiz
- 403 Forbidden — authorization başarısız
- 404 Not Found — resource yok
- 409 Conflict — state conflict, uniqueness, idempotency conflict
- 422 Unprocessable Content — yalnızca project standardı gerektirirse semantic validation; default olarak 409 tercih edilir
- 429 Too Many Requests — Rate Limiting

## Server/dependency
- 500 Internal Server Error
- 503 Service Unavailable
- 504 Gateway Timeout

# 4. Request / Response DTO

Domain Entity doğrudan API contract olarak expose edilmez.

REST boundary:
```text
Request DTO -> Application Command/Query -> Domain
Domain/Application Result -> Response DTO
```

DTO field'ları consumer ihtiyacına göre tasarlanır.

# 5. Validation

Validation üç seviyede ayrılır.

## Syntactic validation
- required
- format
- length
- positive number

Boundary'de Bean Validation ile uygulanabilir.

## Business validation
- Seller ACTIVE mi?
- Property PUBLISHED mı?
- Offer transition geçerli mi?

Domain/Application seviyesinde.

## Authorization/ownership validation
- resource owner doğru mu?

Application/security boundary'de.

# 6. Pagination

List endpoint'lerinde unbounded response yasaktır.

Default model:

```text
?page=0&size=20
```

Response:
- items
- page
- size
- totalElements
- totalPages gerekirse

Deep pagination maliyetli datastore'larda cursor/search_after yaklaşımı değerlendirilebilir.

Elasticsearch için ileri aşamada `search_after` tercih edilebilir.

# 7. Sorting

Explicit whitelist kullanılır.

Örnek:

```text
?sort=price,asc
?sort=publishedAt,desc
```

Client arbitrary DB field adı gönderemez.

# 8. Filtering

Filter'lar business-oriented isimlerle expose edilir.

Örnek:

```text
?city=Istanbul
&district=Kadikoy
&propertyType=APARTMENT
&minPrice=3000000
&maxPrice=8000000
```

Internal Elasticsearch/Mongo field isimleri public contract değildir.

# 9. Idempotency

Critical POST operation'larda:

```text
Idempotency-Key
```

header kullanılabilir.

Primary candidate:
`POST /buyers/{buyerId}/offers`

Aynı key + aynı payload:
aynı sonucu dönmelidir.

Aynı key + farklı payload:
`409 IDEMPOTENCY_CONFLICT`

Server key'i TTL ile saklar.

# 10. Optimistic Concurrency

Concurrent modification riski olan resource'larda version kullanılmalıdır.

Candidate:
Property.

REST seviyesinde ileride:
- ETag
- If-Match

kullanımı değerlendirilebilir.

İlk implementation'da persistence-level `@Version` ile başlanabilir.

# 11. Partial Update

PATCH semantics açık olmalıdır.

Null:
- "değeri null yap"
- "field gönderilmedi"

ayrımını bozuyorsa JSON Merge Patch / JSON Patch veya dedicated request modeli değerlendirilebilir.

Basit senaryoda explicit nullable field + presence handling yeterli olabilir.

# 12. API Versioning

İlk scope'ta:

```text
/api/v1/...
```

path versioning kullanılabilir.

Alternatif media-type/header versioning bu lab için gereksiz complexity olabilir.

Version yalnızca breaking contract değişikliğinde artırılır.

# 13. Backward Compatibility

Breaking change örnekleri:
- field rename
- field remove
- required field ekleme
- enum value davranış değişimi
- status code değişimi

Non-breaking:
- optional response field ekleme
- yeni endpoint
- yeni optional request field

Event ve API schema evolution bilinçli yönetilir.

# 14. Deprecation

Bir endpoint kaldırılacaksa:
- documentation'da deprecated işaretlenir
- replacement gösterilir
- migration window tanımlanır

Ani removal yapılmaz.

# 15. Error Contract

Tüm REST API'ler ortak error envelope kullanır:

```json
{
  "timestamp": "...",
  "status": 409,
  "code": "INVALID_PROPERTY_STATE",
  "message": "...",
  "path": "...",
  "correlationId": "..."
}
```

Client logic `message` string'ine değil stable `code` alanına dayanmalıdır.

# 16. Correlation

Her external request için correlation context bulunmalıdır.

Header candidate:
`X-Correlation-Id`

Yoksa Gateway üretebilir.

Downstream REST, gRPC, Kafka ve RabbitMQ context propagation yapılır.

# 17. Security

API design aşamasında:
- Authentication requirement
- Role/scope
- Ownership
- sensitive response field
- Rate Limiting
- audit requirement

belirlenir.

# 18. OpenAPI

REST endpoint'leri SpringDoc/OpenAPI ile dokümante edilir.

Dokümantasyon:
- request
- response
- status code
- error response
- security requirement
- example

içermelidir.

Annotation çöplüğü oluşursa API interface veya central OpenAPI configuration düşünülebilir.

# 19. Async API semantics

Asynchronous command hemen sonuçlanmıyorsa:

`202 Accepted`

dönülebilir.

Örnek:
ListingSubmission RabbitMQ command flow.

Response:
- submissionId
- status=SUBMITTED

Client polling veya event notification ileri fazda değerlendirilebilir.

# 20. Bulk Operations

İlk scope'ta bulk API eklenmez.

Gerçek business ihtiyacı çıkarsa:
- partial success semantics
- per-item error
- idempotency
- payload size

ayrıca tasarlanır.

# 21. Rate Limiting

Public/expensive endpoint'ler için uygulanır.

Örnek:
- search
- offer creation
- login/auth
- autocomplete

429 response ve gerekirse rate-limit headers sunulabilir.

# 22. Cache Semantics

Public GET response cache edilecekse:
- freshness
- invalidation
- ETag
- Cache-Control

bilinçli belirlenmelidir.

İlk aşamada application/Redis cache ile HTTP cache semantics ayrı tutulur.

# 23. REST vs GraphQL vs gRPC sınırı

## REST
Public business API ve state-changing command'lar.

## GraphQL
Flexible read/query use-case'leri.

İlk scope'ta mutation yok.

## gRPC
Internal synchronous service-to-service call.

Örnek:
BuyerService -> AgentService availability.

Aynı use-case'i üç protocol ile tekrar etmeyiz.

# 24. GraphQL Standardı

- query depth/complexity kontrolü
- pagination
- resolver authorization
- N+1 gözlemi
- business-oriented schema
- internal Elasticsearch DSL expose edilmez
- mutation ilk aşamada yok

# 25. gRPC Standardı

- protobuf backward compatibility
- field number reuse yapılmaz
- deadline zorunlu consideration
- status code mapping
- trace context propagation
- service-to-service auth
- retry yalnızca safe operation'da

# 26. API Naming

Consistency:
- plural resource names
- kebab-case URL segment
- JSON field camelCase
- enum UPPER_SNAKE_CASE

# 27. Date / Time

API'de ISO-8601 kullanılır.

Backend'de `Instant` / UTC tercih edilir.

Local timezone business requirement ise açıkça modellenir.

# 28. Money

Amount + currency birlikte taşınır.

Float/double kullanılmaz.

Java:
`BigDecimal`

Currency:
ISO-4217 code.

# 29. IDs

External contract'ta opaque ID kullanılır.

Client ID formatına business logic bağlamaz.

UUID kullanılabilir ancak API semantic'i UUID formatına bağımlı olmamalıdır.

# 30. Sensitive Data

Response'a gereksiz:
- password
- token
- secret
- internal credential
- private infrastructure metadata

konmaz.

# 31. API Review Checklist

Her endpoint için:
- Resource doğru isimlendirilmiş mi?
- HTTP method doğru mu?
- Status code doğru mu?
- Request/Response DTO ayrı mı?
- Validation katmanları ayrılmış mı?
- Ownership kontrolü var mı?
- Pagination gerekli mi?
- Sort/filter whitelist var mı?
- Idempotency gerekiyor mu?
- Concurrency riski var mı?
- Error code stable mı?
- Correlation var mı?
- OpenAPI dokümante mi?
- Breaking change riski var mı?
- REST yerine GraphQL/gRPC daha uygun mu?
