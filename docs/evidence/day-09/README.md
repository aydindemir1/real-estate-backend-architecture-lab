# Day 09 - BuyerService Local Runtime Evidence

This directory records the local runtime acceptance evidence for **BuyerService - Couchbase + Hexagonal Architecture**.

## Environment

- BuyerService: `http://localhost:9093`
- Config Server: `http://localhost:8888`
- Eureka Server: `http://localhost:8761`
- Couchbase Web Console: `http://localhost:8091`
- Couchbase bucket: `buyer`
- Couchbase scope: `buyer_service`
- Couchbase collection: `preferences`
- Local runtime: STS / Spring Boot App
- Couchbase edition: Community 8.0.2

## Startup evidence

The captured BuyerService startup log proves that:

- configuration is loaded from Config Server on port 8888,
- the Couchbase `buyer` bucket opens successfully,
- BuyerService starts on port 9093,
- BuyerService registers with Eureka as `UP`.

Raw evidence:

- `buyer-service-startup-success.log`

## Couchbase persistence evidence

Before Postman runtime tests:

- `buyer_service.preferences` existed,
- item count was `0`.

After the first successful preferences PUT:

- item count changed from `0` to `1`,
- subsequent GET returned the same persisted preferences,
- a saved search was added,
- a later GET returned the saved search from persistence.

This confirms a real Couchbase write/read cycle rather than only an in-memory API response.

## Eureka evidence

The Eureka dashboard was captured during local runtime.

Registered instances shown as `UP`:

- `API-GATEWAY-SERVICE` on port `8080`
- `BUYER-SERVICE` on port `9093`

The development Eureka dashboard also displayed its normal low-renewal/self-preservation warning for the small local instance count; this does not invalidate the recorded `UP` registrations.

## Postman acceptance results

Collection:

- `Day-09-BuyerService.postman_collection.json`

### Success scenarios

| Scenario | Expected | Result |
|---|---:|---:|
| PUT `/buyers/{buyerId}/preferences` | 200 | PASS - 3/3 tests |
| GET `/buyers/{buyerId}/preferences` | 200 | PASS - 2/2 tests |
| POST `/buyers/{buyerId}/saved-searches` | 201 | PASS - 3/3 tests |
| GET preferences after saved search | 200 | PASS - 2/2 tests |

### Error scenarios

| Scenario | Expected | Error code | Result |
|---|---:|---|---:|
| Missing preferences | 404 | `BUYER_PREFERENCES_NOT_FOUND` | PASS - 2/2 |
| Bean Validation failure | 400 | `VALIDATION_ERROR` | PASS - 3/3 |
| Semantic range violation | 422 | `INVALID_BUYER_PREFERENCES` | PASS - 2/2 |
| Invalid UUID path variable | 400 | `INVALID_REQUEST` | PASS - 2/2 |
| Malformed JSON | 400 | `INVALID_REQUEST` | PASS - 2/2 |

## Runtime defect found during acceptance

The initial PUT request exposed a runtime issue:

```
Name for argument of type [java.util.UUID] not specified,
and parameter name information not available via reflection.
```

The controller had relied on implicit path-variable parameter-name discovery.

It was fixed by declaring the path variable explicitly:

```java
@PathVariable("buyerId") UUID buyerId
```

for all three BuyerService REST operations.

After the fix, the complete success and error acceptance set passed.

## Acceptance status

Day 09 BuyerService runtime acceptance:

- Config Server integration: **PASS**
- Couchbase authentication: **PASS**
- Couchbase bucket open: **PASS**
- Couchbase persistence write/read: **PASS**
- Eureka registration: **PASS**
- REST success scenarios: **PASS**
- REST validation/error scenarios: **PASS**
- Correlation/trace error envelope: **observed**
