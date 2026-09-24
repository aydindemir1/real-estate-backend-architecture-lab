# Error Model

## Amaç

REST, gRPC, messaging ve application katmanlarında tutarlı hata semantics'i oluşturmak.

## Error kategorileri

### Validation Error
Client input format veya field validation hatası.

HTTP:
`400 Bad Request`

Örnek code:
- VALIDATION_ERROR
- INVALID_PRICE
- INVALID_EMAIL

### Authentication Error
Token yok veya geçersiz.

HTTP:
`401 Unauthorized`

Code:
- UNAUTHENTICATED
- TOKEN_EXPIRED
- INVALID_TOKEN

### Authorization Error
Authenticated user yetkili değil.

HTTP:
`403 Forbidden`

Code:
- ACCESS_DENIED
- RESOURCE_OWNERSHIP_REQUIRED

### Not Found
Resource bulunamadı.

HTTP:
`404 Not Found`

Code:
- PROPERTY_NOT_FOUND
- OFFER_NOT_FOUND
- AGENT_NOT_FOUND
- SELLER_NOT_FOUND

### Conflict / State Violation
Resource mevcut ancak requested transition uygulanamaz.

HTTP:
`409 Conflict`

Code:
- PROPERTY_NOT_AVAILABLE
- INVALID_PROPERTY_STATE
- INVALID_OFFER_STATE
- DUPLICATE_LICENSE_NUMBER
- DUPLICATE_EMAIL
- IDEMPOTENCY_CONFLICT

### Business Rule Violation
Domain rule ihlali.

Çoğu durumda `409 Conflict` veya bazı validation senaryolarında `422 Unprocessable Content` değerlendirilebilir. Proje standardı implementation öncesinde tekleştirilecektir; default tercih `409` olacaktır.

Code:
- BUYER_CANNOT_OFFER_OWN_PROPERTY
- SELLER_NOT_ACTIVE
- AGENT_NOT_ACTIVE
- OFFER_AMOUNT_MUST_BE_POSITIVE

### Downstream / Dependency Error
Başka service veya infrastructure erişilemiyor.

HTTP:
- `503 Service Unavailable`
- gerekiyorsa `504 Gateway Timeout`

Code:
- DOWNSTREAM_UNAVAILABLE
- AGENT_SERVICE_UNAVAILABLE
- SEARCH_UNAVAILABLE
- TIMEOUT

## REST error response

Önerilen yapı:

```json
{
  "timestamp": "2026-09-24T18:00:00Z",
  "status": 409,
  "code": "INVALID_PROPERTY_STATE",
  "message": "Property mevcut durumda publish edilemez.",
  "path": "/properties/123/publish",
  "correlationId": "..."
}
```

Validation için ek alan:

```json
{
  "errors": [
    {
      "field": "amount",
      "code": "POSITIVE",
      "message": "Amount sıfırdan büyük olmalıdır."
    }
  ]
}
```

## gRPC mapping

- INVALID_ARGUMENT -> validation
- UNAUTHENTICATED -> authentication
- PERMISSION_DENIED -> authorization
- NOT_FOUND -> missing resource
- FAILED_PRECONDITION -> invalid business state
- ALREADY_EXISTS -> uniqueness/conflict
- UNAVAILABLE -> downstream/service unavailable
- DEADLINE_EXCEEDED -> timeout

## Messaging error semantics

### Retryable
- temporary network failure
- broker/internal transient issue
- downstream temporary unavailable

### Non-retryable
- invalid schema
- impossible business transition
- malformed command
- permanent authorization/config problem

Non-retryable message doğrudan DLQ/DLT'ye yönlendirilebilir.

## Correlation

Her error log ve response mümkün olduğunda:
- correlationId
- traceId

ile ilişkilendirilmelidir.

## Kural

Exception class isimleri dış API contract değildir. Dış contract stable `code` alanı üzerinden yönetilir.
