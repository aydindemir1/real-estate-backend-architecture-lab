# SellerService — Day 10 Design

## Architecture

SellerService Day 10 implementation uses Onion Architecture.

```text
presentation
    ↓
application
    ↓
domain

infrastructure → application/domain
```

Domain layer Spring, Cassandra, REST and messaging technologiesinden bağımsızdır.

## Domain

### Seller Aggregate

State:
- ACTIVE
- SUSPENDED
- INACTIVE

Rule:
- yalnızca ACTIVE seller listing oluşturabilir / submit edebilir.

### ListingSubmission

States:
- CREATED
- SUBMITTED
- PROPERTY_CREATED
- REJECTED
- FAILED

Day 10 active runtime flow:
- CREATED → SUBMITTED

Second SUBMITTED → SUBMITTED transition invalid'dir ve 409 döner.

## Persistence

Cassandra query-first model:

- `seller_by_id`
- `listing_submissions_by_seller_and_month`

Listing partition:
`(seller_id, year_month)`

Clustering:
`created_at DESC, submission_id ASC`

No relational joins, no cross-partition scan, no `ALLOW FILTERING`.

## Schema ownership

Schema source of truth:

`src/main/resources/cassandra/schema/V1__seller_tables.cql`

Lokal bootstrap explicit `cqlsh` ile yapılır.

## Runtime

Verified integrations:
- Config Client
- Eureka Client
- Cassandra
- REST
- validation/error mapping
- Testcontainers
- ArchUnit

Messaging intentionally deferred.
