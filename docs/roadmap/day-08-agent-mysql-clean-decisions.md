# Day 8 — AgentService / MySQL / Clean Architecture — Kesinleştirilmiş Kararlar

## Durum

Bu doküman Day 8 implementation öncesi bağlayıcı Pre-Day karar setidir.

- Planning source: `docs/backend-roadmap-design`
- Implementation branch: `day/08-agent-mysql-clean`
- Branch kuralı: her yeni Day branch'i bir önceki Day branch'inden türetilir; `main` baseline olarak korunur ve değiştirilmez.
- Kapsam: yalnız AgentService.

## 1. Kapsam

Day 8 hedefleri:

- AgentService persistence: PostgreSQL -> MySQL
- Clean Architecture
- Flyway schema migration
- CreateAgent
- GetAgent
- ChangeAvailability
- REST API
- optimistic concurrency
- Unit / Application / REST Slice / MySQL Testcontainers / ArchUnit testleri

Day 8 içinde BuyerService, SellerService, PropertyService veya SearchService implementation yapılmaz.

## 2. Identity ownership

`Agent.userId`, UserProfileService içindeki user/profile identity'yi temsil eder.

- Keycloak subject doğrudan Agent domain ID'si değildir.
- Day 8'de synchronous UserProfile existence check eklenmez.
- `userId` AgentService içinde external identity reference olarak tutulur.
- Keycloak subject -> UserProfile -> Agent mapping'i Day 14 Security milestone'ında tamamlanır.
- Cross-service database foreign key kullanılmaz.

## 3. Relational migration standard

Project-wide relational schema migration standardı Flyway'dir.

- Liquibase ile karıştırılmaz.
- AgentService migration path:
  `src/main/resources/db/migration/V1__create_agents_table.sql`
- Schema ownership Flyway'dedir.
- Hibernate runtime schema mutation yapmaz.
- `spring.jpa.hibernate.ddl-auto=validate`
- `spring.jpa.open-in-view=false`

## 4. Agent Aggregate

Alanlar:

- AgentId id
- UserId userId
- LicenseNumber licenseNumber
- AgencyInfo agencyInfo
- AgentStatus status
- AvailabilityStatus availability
- Instant createdAt
- Instant updatedAt
- long version

Yeni Agent:

- `status = ACTIVE`
- `availability = OFFLINE`

Public setter kullanılmaz. State değişimleri domain behavior üzerinden yapılır:

- `Agent.create(...)`
- `Agent.reconstitute(...)`
- `suspend()`
- `activate()`
- `deactivate()`
- `changeAvailability(...)`

## 5. AgentStatus state machine

İzin verilen geçişler:

- ACTIVE -> SUSPENDED
- SUSPENDED -> ACTIVE
- ACTIVE -> INACTIVE
- SUSPENDED -> INACTIVE

`INACTIVE` terminal state'tir.

Kurallar:

- SUSPENDED veya INACTIVE olduğunda availability otomatik OFFLINE olur.
- SUSPENDED -> ACTIVE sonrası availability OFFLINE kalır.
- Aynı status'a tekrar geçiş idempotent no-op olabilir.

## 6. Availability rules

ACTIVE Agent:

- OFFLINE -> AVAILABLE
- OFFLINE -> BUSY
- AVAILABLE -> BUSY
- AVAILABLE -> OFFLINE
- BUSY -> AVAILABLE
- BUSY -> OFFLINE

Kurallar:

- aynı availability'ye geçiş idempotent no-op
- SUSPENDED veya INACTIVE Agent AVAILABLE/BUSY olamaz
- gerçek state değişikliğinde `updatedAt` güncellenir
- no-op durumda `updatedAt` değiştirilmez

## 7. Value Objects

### LicenseNumber

- immutable
- null olamaz
- blank olamaz
- güvenli whitespace normalization uygulanabilir
- doğrulanmış gerçek business requirement olmadan regex formatı uydurulmaz
- uniqueness application pre-check + MySQL unique constraint ile korunur

### AgencyInfo

Day 8'de Value Object'tir.

Alanlar:

- agencyName: required
- agencyRegistrationNumber: optional
- officePhone: optional

Day 43 Multi-Tenancy milestone'ındaki gerçek `Agency` tenant/domain entity'si ile karıştırılmaz.

## 8. CreateAgent

Akış:

1. request/command structural validation
2. duplicate userId pre-check
3. duplicate licenseNumber pre-check
4. Value Object creation
5. `Agent.create(...)`
6. repository save
7. DB unique constraints final correctness guard
8. AgentResult

Request client tarafından status/availability göndermez.

Day 8'de UserProfileService'e synchronous existence validation çağrısı yapılmaz.

Stable conflicts:

- `AGENT_ALREADY_EXISTS_FOR_USER` -> 409
- `DUPLICATE_LICENSE_NUMBER` -> 409

## 9. GetAgent

- `GET /agents/{agentId}`
- success -> 200
- bulunamazsa -> 404 `AGENT_NOT_FOUND`
- JPA entity veya domain aggregate public API'ye dönmez
- `version` public response'a çıkmaz

Day 8'de get-by-userId, list/filter/pagination eklenmez.

## 10. ChangeAvailability

- `PATCH /agents/{agentId}/availability`
- success -> 200 + updated AgentResponse
- aggregate load edilir
- `Agent.changeAvailability(...)` çağrılır
- save edilir
- updated result döner

Invalid lifecycle/availability transition:

- 409 `INVALID_AGENT_STATE`

Optimistic locking Day 8'de gerçek integration test ile doğrulanır.

Concurrency conflict:

- 409 `AGENT_CONCURRENT_UPDATE`

## 11. Persistence model

MySQL table:

```text
agents
--------------------------------
id                           CHAR(36) PK
user_id                      CHAR(36) UNIQUE NOT NULL
license_number               VARCHAR(128) UNIQUE NOT NULL
agency_name                  VARCHAR(255) NOT NULL
agency_registration_number   VARCHAR(128) NULL
office_phone                 VARCHAR(64) NULL
status                       VARCHAR(32) NOT NULL
availability_status          VARCHAR(32) NOT NULL
created_at                   TIMESTAMP(6) NOT NULL
updated_at                   TIMESTAMP(6) NOT NULL
version                      BIGINT NOT NULL
```

Kurallar:

- cross-service FK yok
- MySQL native ENUM yok
- status/availability string olarak saklanır
- speculative secondary index yok
- yalnız PK ve gerçek uniqueness/query ihtiyacına dayalı index/constraint
- timestamps application/domain tarafından üretilir

## 12. Domain/JPA mapping

`domain.model.Agent` framework-free kalır.

`AgentJpaEntity` yalnız infrastructure persistence katmanındadır.

- new aggregate: `Agent.create(...)`
- persistence hydration: `Agent.reconstitute(...)`
- `reconstitute(...)` persisted state consistency'yi doğrular
- timestamps ve version korunur
- Day 8 persistence mapper explicit Java mapper'dır
- MapStruct bu non-trivial reconstruction için zorlanmaz
- JPA entity domain enum'larını kullanabilir
- `@Version` increment Hibernate sorumluluğudur
- domain behavior version artırmaz
- domain repository `save(Agent)` updated Agent döner

Raw SQL/JPA/Spring persistence exception'ları public API'ye sızmaz.

## 13. Application Layer

Use-case interfaces:

- CreateAgentUseCase
- GetAgentUseCase
- ChangeAvailabilityUseCase

Commands/queries immutable Java record olabilir.

`AgentApplicationService` sorumlulukları:

- orchestration
- repository interaction
- transaction boundary
- domain Value Object creation
- AgentResult mapping

Business invariant'lar application service'e taşınmaz; domain'de kalır.

Transactions:

- CreateAgent -> `@Transactional`
- ChangeAvailability -> `@Transactional`
- GetAgent -> `@Transactional(readOnly = true)`

Exception ownership:

Domain:
- invalid Value Object / aggregate state
- InvalidAgentStateException

Application:
- AgentNotFoundException
- AgentAlreadyExistsForUserException
- DuplicateLicenseNumberException
- AgentConcurrentUpdateException

## 14. REST contract

Service-local endpoints:

- `POST /agents`
- `GET /agents/{agentId}`
- `PATCH /agents/{agentId}/availability`

Oluştur:

- 201 Created
- `Location: /agents/{agentId}`
- body: AgentResponse

Get:

- 200 AgentResponse
- 404 AGENT_NOT_FOUND

ChangeAvailability:

- 200 updated AgentResponse

Service-local controller path versionless kalır. External API versioning Gateway concern'dür.

Day 8'de:

- Keycloak/RBAC uygulanmaz
- Idempotency-Key eklenmez

## 15. Stable HTTP/error semantics

- malformed/validation error -> 400
- AGENT_NOT_FOUND -> 404
- AGENT_ALREADY_EXISTS_FOR_USER -> 409
- DUPLICATE_LICENSE_NUMBER -> 409
- INVALID_AGENT_STATE -> 409
- AGENT_CONCURRENT_UPDATE -> 409

Aşağıdakiler client'a sızmaz:

- DataIntegrityViolationException
- ObjectOptimisticLockingFailureException
- SQLState
- MySQL vendor error detail
- Hibernate exception adı
- stack trace

## 16. Test stratejisi

### Domain Unit Testleri

- create -> ACTIVE + OFFLINE
- invalid LicenseNumber
- invalid AgencyInfo
- ACTIVE availability transitions
- SUSPENDED/INACTIVE invalid availability
- suspend/deactivate -> OFFLINE
- SUSPENDED -> ACTIVE -> OFFLINE
- INACTIVE terminal
- same availability no-op
- `reconstitute()` invalid persisted state rejection
- updatedAt change/no-op semantics

### Application Unit Testleri

- create başarılı
- duplicate user
- duplicate license
- get success
- not found
- change availability success
- invalid state propagation
- repository boundary interaction

### MySQL Testcontainers Integration Testleri

- Flyway V1 migration
- save/load round-trip
- UNIQUE(user_id)
- UNIQUE(license_number)
- mapper round-trip
- enum string persistence
- timestamps/version behavior
- optimistic locking conflict
- Hibernate validate against real schema

### REST Slice Testleri

- POST -> 201 + Location
- GET -> 200
- not found -> 404
- PATCH -> 200
- malformed UUID/body -> 400
- duplicate conflicts -> 409
- invalid state -> 409
- optimistic conflict -> 409
- persistence/domain types not leaked

### Architecture Fitness

ArchUnit rules:

- domain -> no Spring/JPA/application/infrastructure/presentation dependency
- application -> no infrastructure/presentation dependency
- presentation -> no persistence/JPA dependency
- infrastructure -> may depend on application/domain
- JPA entity only infrastructure persistence
- Spring Data repository only infrastructure
- no layer/package cycles

### Integration Smoke

Doğrula:

- AgentService startup
- Flyway migration
- Config Client baseline
- Eureka Client baseline
- Actuator baseline
- POST -> GET -> PATCH gerçek flow

Manual/Postman check tek başına acceptance değildir.

## 17. Definition of Done

Day 8 ancak şu koşullarda kapanır:

- AgentService MySQL kullanıyor
- PostgreSQL driver AgentService'den kaldırılmış
- Flyway migration gerçek MySQL üzerinde çalışıyor
- domain Spring/JPA'dan bağımsız
- JPA entity ayrı persistence model
- create/reconstitute ayrımı uygulanmış
- CreateAgent/GetAgent/ChangeAvailability çalışıyor
- status/availability invariant'ları uygulanmış
- uniqueness application + DB katmanında korunuyor
- optimistic locking test ile doğrulanmış
- REST DTO'lar domain/JPA modelinden ayrı
- stable HTTP/error semantics uygulanmış
- OSIV kapalı
- ddl-auto validate
- Config/Eureka/Actuator baseline kırılmamış
- Unit/Application/REST/Testcontainers/ArchUnit testleri green
- Buyer/Seller/Property/Search implementation Day 8'e sızmamış
- docs actual implementation ile güncellenmiş
- Knowledge Base etkisi gözden geçirildi ve güncellendi
- build green
