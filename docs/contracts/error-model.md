# Error Model

## Amaç

REST, gRPC, messaging ve application katmanlarında tutarlı hata semantics'i oluşturmak.

## HTTP error policy

### 400 Bad Request
Malformed veya syntactic validation:
- required field
- format
- parse
- invalid primitive constraint

Örnek:
- VALIDATION_ERROR
- INVALID_EMAIL

### 401 Unauthorized
Authentication yok/geçersiz:
- UNAUTHENTICATED
- TOKEN_EXPIRED
- INVALID_TOKEN

### 403 Forbidden
Authenticated fakat yetkisiz:
- ACCESS_DENIED
- RESOURCE_OWNERSHIP_REQUIRED

### 404 Not Found
Resource yok:
- PROPERTY_NOT_FOUND
- OFFER_NOT_FOUND
- AGENT_NOT_FOUND
- SELLER_NOT_FOUND

### 409 Conflict
Mevcut resource/state ile conflict:
- PROPERTY_NOT_AVAILABLE
- INVALID_PROPERTY_STATE
- INVALID_OFFER_STATE
- DUPLICATE_LICENSE_NUMBER
- DUPLICATE_EMAIL
- IDEMPOTENCY_CONFLICT
- optimistic locking conflict

### 422 Unprocessable Content
Request syntactically valid fakat domain semantic'i bağımsız business rule nedeniyle kabul edilemiyor:
- BUYER_CANNOT_OFFER_OWN_PROPERTY
- SELLER_NOT_ACTIVE
- AGENT_NOT_ACTIVE
- OFFER_AMOUNT_MUST_BE_POSITIVE

Bir hata mevcut resource state conflict'ine dayanıyorsa 409; request'in business semantic'i kendi başına geçersizse 422 kullanılır.

### 429 Too Many Requests
Rate limit.

### 500 / 503 / 504
- 500 unexpected internal
- 503 dependency/service unavailable
- 504 downstream timeout

## REST error response

```json
{
  "timestamp": "2026-09-24T18:00:00Z",
  "status": 409,
  "code": "INVALID_PROPERTY_STATE",
  "message": "Property mevcut durumda publish edilemez.",
  "path": "/api/v1/properties/123/publish",
  "correlationId": "...",
  "traceId": "..."
}
```

Validation error'larda optional `errors` listesi bulunabilir.

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

Retryable:
- temporary network failure
- broker/internal transient issue
- temporary downstream unavailable

Non-retryable:
- invalid schema
- malformed command
- permanent unsupported business request

Business state race/conflict için consumer semantic'i use-case bazında explicit tasarlanır; kör retry yapılmaz.

## Kural

Exception class isimleri dış API contract değildir. Client stable `code` alanına dayanır.
