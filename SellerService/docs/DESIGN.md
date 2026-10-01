# SellerService Tasarımı

## 1. Amaç

SellerService; seller state'i ile listing submission lifecycle'ının sahibidir.

Day 10 capability scope:
- Seller oluşturma
- Seller okuma
- ListingSubmission oluşturma
- seller + month partition üzerinden listing listeleme
- ListingSubmission submit etme

Property creation, RabbitMQ publish, Kafka ve Saga Day 10 scope'unda değildir.

## 2. Architecture

SellerService Day 10 itibarıyla **Onion Architecture** kullanır.

```text
Presentation ------> Application ------> Domain
Infrastructure -----------------------> Domain
Infrastructure -----------------------> Application
```

Temel kurallar:
- Domain Spring, Cassandra, REST veya messaging framework'lerini bilmez.
- Application infrastructure/presentation implementation'larını bilmez.
- Presentation infrastructure persistence class'larına bağımlı değildir.
- Cassandra adapter'ları domain repository contract'larını implement eder.
- Top-level package cycle'ları ArchUnit ile yasaktır.

## 3. Domain modeli

### Seller Aggregate

Alanlar:
- `SellerId`
- `UserId`
- `displayName`
- `SellerStatus`
- `createdAt`
- `updatedAt`

Status:
- ACTIVE
- SUSPENDED
- INACTIVE

Yeni Seller `ACTIVE` başlar.

`assertCanSubmitListing()` yalnız ACTIVE durumda geçer.

### ListingSubmission

Alanlar:
- `ListingSubmissionId`
- `SellerId`
- `PropertyDraftData`
- `ListingSubmissionStatus`
- `createdAt`
- `updatedAt`

State'ler:
- CREATED
- SUBMITTED
- PROPERTY_CREATED
- REJECTED
- FAILED

Transition kuralları:
- CREATED -> SUBMITTED
- SUBMITTED -> PROPERTY_CREATED
- SUBMITTED -> REJECTED
- SUBMITTED -> FAILED

Day 10 application/runtime yalnız create ve submit akışını kullanır.

### PropertyDraftData

Property Aggregate'in kopyası değildir; gelecekte PropertyService'e gönderilecek listing draft snapshot'ını taşır.

Alanlar:
- title
- description
- propertyType
- city
- district
- addressLine
- priceAmount
- currency
- area
- roomCount

## 4. Application layer

Commands:
- `CreateSellerCommand`
- `CreateListingSubmissionCommand`
- `SubmitListingCommand`

Queries:
- `GetSellerQuery`
- `ListSellerSubmissionsQuery`

Services:
- `SellerApplicationService`
- `ListingSubmissionApplicationService`

Create listing: seller yüklenir, ACTIVE doğrulanır, CREATED submission oluşturulup kaydedilir.

Submit listing: seller ACTIVE doğrulanır, exact partition + clustering identity ile submission yüklenir, domain `submit()` transition'ı uygulanır ve SUBMITTED state kaydedilir. RabbitMQ publish yoktur.

## 5. Cassandra persistence

Keyspace: `seller_service`

`seller_by_id` primary query: seller by `seller_id`.

`listing_submissions_by_seller_and_month` primary key:

```text
PRIMARY KEY ((seller_id, year_month), created_at, submission_id)
```

Clustering:

```text
created_at DESC, submission_id ASC
```

Kurallar:
- no `ALLOW FILTERING`
- no cross-partition list scan
- no relational join expectation
- domain model Cassandra table modeli değildir

Domain repository paging contract'ı Cassandra `Page`/`Slice` tiplerini dışarı sızdırmaz.

## 6. Schema ownership

Source of truth:
`src/main/resources/cassandra/schema/V1__seller_tables.cql`

Runtime:
- `spring.cassandra.schema-action: none`

Lokal schema explicit `cqlsh` bootstrap ile uygulanır.

## 7. Configuration

Bootstrap:
- application name: `seller-service`
- root `.env` optional import
- Config Client

`application.yml` default Config Server URL:
- `http://localhost:8889`

Day 10 lokal acceptance:
- `CONFIG_SERVER_URL=http://localhost:8888`

Local Config Server Cassandra keys:
- `SELLER_CASSANDRA_CONTACT_POINTS`
- `SELLER_CASSANDRA_PORT`
- `SELLER_CASSANDRA_KEYSPACE`
- `SELLER_CASSANDRA_LOCAL_DATACENTER`
- `SELLER_CASSANDRA_REQUEST_TIMEOUT`

Lokal Cassandra authentication kullanmadığı için boş username/password property'leri tutulmaz.

## 8. REST API

| Method | Path | Success |
|---|---|---:|
| POST | `/sellers` | 201 |
| GET | `/sellers/{sellerId}` | 200 |
| POST | `/sellers/{sellerId}/listing-submissions` | 201 |
| GET | `/sellers/{sellerId}/listing-submissions` | 200 |
| POST | `/sellers/{sellerId}/listing-submissions/{submissionId}/submit` | 200 |

List query:
- `yearMonth=YYYY-MM`
- `pageSize=1..100`
- optional `pageState`

Submit body'deki `submissionId`, path'teki ID ile eşleşmelidir. Controller path/query parameter isimleri explicit annotation isimleriyle bind edilir.

## 9. Error semantics

- 404 `SELLER_NOT_FOUND`
- 404 `LISTING_SUBMISSION_NOT_FOUND`
- 422 `SELLER_NOT_ACTIVE`
- 409 `INVALID_LISTING_SUBMISSION_STATE`
- 400 `VALIDATION_ERROR`
- 400 `BAD_REQUEST`
- 400 `MALFORMED_REQUEST_BODY`
- 500 `DATABASE_ERROR`
- 500 `INTERNAL_SERVER_ERROR`

Raw Cassandra exception API'ye doğrudan sızdırılmaz.

## 10. User uniqueness sınırı

Day 10 fiziksel modelinde global `user_id` uniqueness'i enforce eden ayrı ownership table veya LWT tasarımı yoktur. Dokümantasyon global user uniqueness garantisi iddia etmez.

## 11. Test stratejisi

- domain unit tests
- application tests
- Cassandra Testcontainers integration
- seller + month partition isolation
- newest-first clustering
- paging
- exact row lookup
- no-ALLOW-FILTERING query guard
- REST tests
- ArchUnit Onion Architecture tests

## 12. Lokal runtime doğrulaması

- Config Server -> PASS
- Cassandra connection -> PASS
- explicit schema bootstrap -> PASS
- SellerService :9095 -> PASS
- Eureka registration -> PASS
- Seller create/get -> PASS
- Listing create/list/submit -> PASS
- Cassandra persistence -> PASS
- Postman 6 success / 10 error scenario -> PASS

Kanıt:
- `docs/evidence/day-10/`
