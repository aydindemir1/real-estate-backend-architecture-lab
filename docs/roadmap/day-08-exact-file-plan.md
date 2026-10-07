# Day 8 — Kesin File / Class / Commit Planı

## 0. Kapsam

Day 8 yalnızca AgentService içindir.

Hedef:
- PostgreSQL -> MySQL
- Clean Architecture
- CreateAgent
- GetAgent
- ChangeAvailability
- migration
- REST API
- unit + application + integration + architecture tests

Day 8 içinde Buyer/Seller/Property/Search implementation yapılmaz.

---

# Görev 1 — Mevcut AgentService source audit

## Doğrulama

Mevcut dosyalar:
- `AgentService/build.gradle`
- `AgentService/src/main/resources/application.yml`

Hedef bootstrap path:
- `AgentService/src/main/java/com/aydindemir/agent/AgentServiceApplication.java`

Main branch'te bu exact path bulunmadığı için implementation başında mevcut bootstrap class/package tespit edilir.

## Karar

Hedef base package:
`com.aydindemir.agent`

Mevcut bootstrap class farklı package'taysa:
- move/refactor yapılır
- component scan root target package olur

## Commit

`refactor(agent): align application base package`

---

# Görev 2 — Build dependency'leri

## Değiştir
`AgentService/build.gradle`

## Kaldır
- PostgreSQL runtime driver

## Koru
- Spring Cloud Eureka Client
- Spring Cloud Config Client
- Spring Boot Actuator
- existing tracing baseline
- Spring Data JPA

## Ekle
- MySQL JDBC driver
- migration tool selected for relational schema
- Testcontainers JUnit Jupiter
- MySQL Testcontainers integration dependency where needed
- ArchUnit test dependency if not already globally available

## Önemli
Migration tool project genelinde tek bir seçim olmalıdır.

If Flyway is selected:
- do not mix Liquibase in AgentService.

If Liquibase is already established as project-wide baseline:
- use Liquibase.

Nihai tool seçimi implementation commit'inden önce kesinleştirilir.

## Commit

`build(agent): switch persistence dependencies to MySQL`

---

# Görev 3 — Konfigürasyon

## Değiştir
`AgentService/src/main/resources/application.yml`

Keep only bootstrap-level config:
- spring.application.name
- configserver import
- CONFIG_SERVER_URL fallback

Buraya gerçek DB secret koyma.

## Config Server target

Create/update AgentService external config in current Config Server source.

Required non-secret config:
- datasource URL template
- driver class if required
- JPA ddl-auto = validate/none
- JPA open-in-view = false
- migration enablement
- datasource pool/timeouts
- actuator exposure baseline

Vault Day 21'e kadar secret'lar environment variable üzerinden sağlanır:
- AGENT_DB_USER
- AGENT_DB_PASSWORD

## Commit

`config(agent): add MySQL datasource configuration`

---

# Görev 4 — Clean Architecture package iskeleti

## Hedef package'ları oluştur

```text
AgentService/src/main/java/com/aydindemir/agent/
├── AgentServiceApplication.java
├── domain/
│   ├── model/
│   ├── repository/
│   └── exception/
├── application/
│   ├── usecase/
│   ├── command/
│   ├── query/
│   ├── result/
│   └── service/
├── infrastructure/
│   ├── persistence/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── mapper/
│   │   └── adapter/
│   └── configuration/
└── presentation/
    └── rest/
        ├── request/
        ├── response/
        └── mapper/
```

## Kural
Yaklaşan görevler için gerekli olmadıkça boş placeholder class oluşturma.

## Commit

`refactor(agent): establish Clean Architecture package boundaries`

---

# Görev 5 — Domain Value Object'lar

## Oluştur

`domain/model/AgentId.java`
- record/value object
- non-null UUID

`domain/model/UserId.java`
- record/value object

`domain/model/LicenseNumber.java`
- immutable
- non-blank
- normalized if business-safe

`domain/model/AgencyInfo.java`
- agencyName
- registrationNumber optional if model allows
- officePhone optional if model allows

`domain/model/AgentStatus.java`
- ACTIVE
- SUSPENDED
- INACTIVE

`domain/model/AvailabilityStatus.java`
- AVAILABLE
- BUSY
- OFFLINE

## Testler

`src/test/java/com/aydindemir/agent/domain/model/LicenseNumberTest.java`

`src/test/java/com/aydindemir/agent/domain/model/AgencyInfoTest.java`

## Commit

`feat(agent): add Agent value objects and status types`

---

# Görev 6 — Agent Aggregate

## Oluştur

`domain/model/Agent.java`

Fields:
- AgentId id
- UserId userId
- LicenseNumber licenseNumber
- AgencyInfo agencyInfo
- AgentStatus status
- AvailabilityStatus availability
- Instant createdAt
- Instant updatedAt
- long/int version representation only if domain needs it

## Oluşturma

Prefer static factory:
`Agent.create(..., Clock clock)`

or constructor/factory equivalent.

## Davranış

- changeAvailability(...)
- changeStatus(...)
- updateProfile(...) only if Day 8 endpoint needs it; otherwise defer

## Invariant'lar

- SUSPENDED agent cannot be AVAILABLE
- INACTIVE agent cannot be AVAILABLE
- required identity/license data cannot be null
- state changed through behavior, not public setter

## Exception'lar

Create:
- `domain/exception/InvalidAgentStateException.java`
- `domain/exception/DuplicateLicenseNumberException.java`
- `domain/exception/AgentNotFoundException.java`

## Testler

`AgentTest.java`

Scenarios:
- create active agent
- ACTIVE -> AVAILABLE allowed
- SUSPENDED -> AVAILABLE rejected
- INACTIVE -> AVAILABLE rejected
- status change enforces availability semantics

## Commit

`feat(agent): add Agent aggregate and invariants`

---

# Görev 7 — Domain repository abstraction

## Oluştur

`domain/repository/AgentRepository.java`

Minimum operations:
- save(Agent)
- Optional<Agent> findById(AgentId)
- boolean existsByLicenseNumber(LicenseNumber)
- boolean existsByUserId(UserId)

Do not expose:
- Spring Data types
- JpaRepository
- Pageable
- JPA entity

## Commit

`feat(agent): add domain repository contract`

---

# Görev 8 — Application contract'ları

## Oluştur commands

`application/command/CreateAgentCommand.java`
Fields:
- userId
- licenseNumber
- agency info fields

`application/command/ChangeAvailabilityCommand.java`
Fields:
- agentId
- availability

Use record where appropriate.

## Oluştur query

`application/query/GetAgentQuery.java`

## Oluştur result

`application/result/AgentResult.java`

Do not return domain Agent directly from presentation.

## Commit

`feat(agent): add application commands queries and results`

---

# Görev 9 — Use-case interface'leri

## Oluştur

`application/usecase/CreateAgentUseCase.java`

`application/usecase/GetAgentUseCase.java`

`application/usecase/ChangeAvailabilityUseCase.java`

Her interface generic CRUD yerine business capability ifade etmelidir.

## Commit

`feat(agent): define Agent application use cases`

---

# Görev 10 — AgentApplicationService

## Oluştur

`application/service/AgentApplicationService.java`

Implements:
- CreateAgentUseCase
- GetAgentUseCase
- ChangeAvailabilityUseCase

## Oluştur flow

### OluşturAgent
1. validate duplicate license
2. validate duplicate user
3. create Aggregate
4. save
5. return AgentResult

### GetAgent
1. find
2. not-found semantic
3. return result

### ChangeAvailability
1. load aggregate
2. call domain behavior
3. save
4. return result

## Transaction boundary
Write use-case'leri application boundary'de transactional olmalıdır.

## Önemli
Logical transaction burada tanımlanır.

Physical Spring `@Transactional` placement follows agreed pragmatic Spring approach while domain remains framework-free.

## Testler

`AgentApplicationServiceTest.java`

Mocks/fakes only repository boundary.

## Commit

`feat(agent): implement Agent application services`

---

# Görev 11 — JPA persistence entity

## Oluştur

`infrastructure/persistence/entity/AgentJpaEntity.java`

Table:
`agents`

Fields:
- UUID id
- UUID userId
- String licenseNumber
- String agencyName
- String agencyRegistrationNumber
- String officePhone
- String status
- String availabilityStatus
- Instant createdAt
- Instant updatedAt
- Long version

Annotations:
- @Entity
- @Table
- @Id
- @Version

Constraints mirrored in migration.

## Kural
Bu class domain Agent değildir.

API serialization yapılmaz.

## Commit

`feat(agent): add JPA persistence entity`

---

# Görev 12 — Spring Data repository

## Oluştur

`infrastructure/persistence/repository/SpringDataAgentRepository.java`

Extends:
`JpaRepository<AgentJpaEntity, UUID>`

Methods:
- existsByLicenseNumber(...)
- existsByUserId(...)

Business method naming noise oluşturma.

## Commit

`feat(agent): add Spring Data Agent repository`

---

# Görev 13 — Persistence mapper

## Oluştur

`infrastructure/persistence/mapper/AgentPersistenceMapper.java`

Mappings:
- domain -> JPA entity
- JPA entity -> domain

## Karar
Project dependency policy destekliyorsa MapStruct kullanılabilir.

However domain reconstruction must preserve invariants without accidentally calling create-new behavior that resets timestamps.

Mapping semantics basit değilse magical generated mapping yerine explicit Java mapper tercih edilir.

## Commit

`feat(agent): add Agent persistence mapping`

---

# Görev 14 — Repository adapter

## Oluştur

`infrastructure/persistence/adapter/AgentRepositoryAdapter.java`

Implements:
`domain.repository.AgentRepository`

Responsibilities:
- delegate Spring Data
- map
- translate persistence-specific behavior where necessary

Add `@Repository` only infrastructure class.

## Commit

`feat(agent): add MySQL repository adapter`

---

# Görev 15 — MySQL migration

## Oluştur based on chosen tool

Flyway candidate:
`AgentService/src/main/resources/db/migration/V1__create_agents_table.sql`

If Liquibase selected:
equivalent changelog path.

## Schema

```text
agents
- id
- user_id
- license_number
- agency_name
- agency_registration_number
- office_phone
- status
- availability_status
- created_at
- updated_at
- version
```

Constraints:
- PK(id)
- UNIQUE(user_id)
- UNIQUE(license_number)
- NOT NULL required fields

Indexes:
Only query-backed indexes.

Uniqueness/basic query gereksinimleri dışında speculative index ekleme.

## Commit

`db(agent): add initial MySQL agent schema`

---

# Görev 16 — Infrastructure konfigürasyonu

## Oluştur if necessary

`infrastructure/configuration/PersistenceKonfigürasyon.java`

Yalnızca explicit bean wiring gerekiyorsa.

Sırf ceremony için configuration class oluşturma.

## JPA ayarları
- ddl-auto validate/none
- open-in-view false

## Commit

Only if file/change exists:
`config(agent): configure MySQL persistence behavior`

---

# Görev 17 — REST request DTO'ları

## Oluştur

`presentation/rest/request/CreateAgentRequest.java`

Fields:
- userId
- licenseNumber
- agencyName
- agencyRegistrationNumber
- officePhone

Jakarta validation:
- @NotNull/@NotBlank
- format constraints only if contract confirmed

`presentation/rest/request/ChangeAvailabilityRequest.java`

Field:
- availability

## Kural
Entity/domain binding yapma.

## Commit

`feat(agent): add Agent REST request contracts`

---

# Görev 18 — REST response DTO

## Oluştur

`presentation/rest/response/AgentResponse.java`

Fields:
- agentId
- userId
- licenseNumber
- agency info
- status
- availability
- createdAt
- updatedAt

Do not expose JPA version unless public concurrency contract specifically requires it later.

## Commit

Can be grouped with request contract commit if small:
`feat(agent): add Agent REST contracts`

---

# Görev 19 — REST mapper

## Oluştur

`presentation/rest/mapper/AgentRestMapper.java`

Mappings:
- CreateAgentRequest -> CreateAgentCommand
- AgentResult -> AgentResponse
- ChangeAvailabilityRequest + path id -> command

Business rule ekleme.

## Commit

`feat(agent): add Agent REST mapping`

---

# Görev 20 — AgentController

## Oluştur

`presentation/rest/AgentController.java`

Endpoints:

### POST /agents
- CreateAgent
- 201 Created
- Location header candidate
- response AgentResponse

### GET /agents/{agentId}
- GetAgent
- 200

### PATCH /agents/{agentId}/availability
- ChangeAvailability
- 200 or 204; project API standard must choose one consistently
- recommended 200 if returning updated AgentResponse

## Version
Service-local path prefixsiz kalır; Gateway external prefix ayrı ele alınır.

## Commit

`feat(agent): expose minimal Agent REST API`

---

# Görev 21 — Error mapping

## Değiştir/create project-consistent global error handling

Agent-specific mappings:
- AgentNotFoundException -> 404 AGENT_NOT_FOUND
- DuplicateLicenseNumberException -> 409 DUPLICATE_LICENSE_NUMBER
- duplicate user -> stable conflict code, add if needed
- InvalidAgentStateException -> 409 INVALID_AGENT_STATE or 422 depending semantic classification

Stable error code contract kullan.

## Exact class location
Prefer service presentation/error package only if common existing exception handler is not shared.

Day 8'de cross-service shared error library oluşturma.

## Commit

`feat(agent): map Agent domain failures to API errors`

---

# Görev 22 — Domain testleri

## Oluştur

`src/test/java/com/aydindemir/agent/domain/model/AgentTest.java`

`LicenseNumberTest.java`

Candidate cases:
- valid create
- blank license
- suspended -> AVAILABLE rejected
- inactive -> AVAILABLE rejected
- ACTIVE availability transitions

## Commit

`test(agent): add domain invariant tests`

---

# Görev 23 — Application testleri

## Oluştur

`src/test/java/com/aydindemir/agent/application/service/AgentApplicationServiceTest.java`

Cases:
- create success
- duplicate license
- duplicate user
- get success
- not found
- change availability success
- invalid domain transition propagated

Spring context gerekmez.

## Commit

`test(agent): add application use-case tests`

---

# Görev 24 — MySQL Testcontainers temeli

## Oluştur candidate

`src/test/java/com/aydindemir/agent/infrastructure/persistence/AgentMySqlContainerTestBase.java`

Or project-standard shared test utility if already exists.

Use:
- MySQL container
- dynamic datasource properties
- migration runs against container

## Kural
Testler için hard-coded shared local MySQL kullanma.

## Commit

`test(agent): add MySQL Testcontainers foundation`

---

# Görev 25 — Persistence integration testleri

## Oluştur

`AgentRepositoryAdapterIntegrationTest.java`

Cases:
- save/load round-trip
- unique license constraint
- unique user constraint
- mapper round-trip
- enum persistence
- timestamp/version behavior

## Commit

`test(agent): add MySQL persistence integration tests`

---

# Görev 26 — Optimistic locking testi

If version/concurrent write is in Day 8 scope:

Create:
`AgentOptimisticLockingIntegrationTest.java`

Scenario:
1. load same entity twice
2. update first
3. update second
4. expect optimistic conflict

If Agent concurrency is not critical yet, this test may be deferred and version field simply established.

Karar explicit olmalı, accidental olmamalıdır.

## Commit
If implemented:
`test(agent): verify optimistic locking behavior`

---

# Görev 27 — Controller slice testi

## Oluştur

`presentation/rest/AgentControllerTest.java`

Use:
- @WebMvcTest or current Spring Boot equivalent
- mock use-case boundary

Cases:
- validation 400
- create 201
- get 200
- not found mapping
- invalid availability body

## Commit

`test(agent): add REST slice tests`

---

# Görev 28 — Architecture fitness testleri

## Oluştur

`src/test/java/com/aydindemir/agent/architecture/AgentCleanArchitectureTest.java`

Rules:
- domain must not depend on application/infrastructure/presentation
- application must not depend on infrastructure/presentation
- presentation may depend on application, not persistence
- infrastructure may depend on domain/application
- domain package must not depend on Spring/JPA packages
- no cycles among slices/layers

## Commit

`test(agent): enforce Clean Architecture boundaries`

---

# Görev 29 — Integration smoke

Run:
- MySQL container/local MySQL
- Config Server
- Eureka if needed
- AgentService

Verify:
- startup
- migration
- registration
- POST /agents
- GET /agents/{id}
- PATCH availability

Yalnız Postman'a dayalı acceptance yeterli değildir; automated test'ler primary olmaya devam eder.

## Commit
Fix gerekmedikçe commit gerekmez.

---

# Görev 30 — Dokümantasyon

## Değiştir

- `AgentService/docs/DESIGN.md`
- `AgentService/docs/PACKAGE-DESIGN.md`
- `AgentService/ROADMAP.md`
- `docs/roadmap/day-08-agent-mysql-clean.md`

## Gerçek implementation ile güncelle
- actual package names
- actual migration tool
- actual config keys
- exact test coverage
- deviations from plan
- completed status

## Opsiyonel README
If AgentService has a module README, update setup/run instructions.

## Commit

`docs(agent): finalize MySQL Clean Architecture implementation`

---

# Önerilen Commit Sırası

1. `refactor(agent): align application base package`
2. `build(agent): switch persistence dependencies to MySQL`
3. `config(agent): add MySQL datasource configuration`
4. `refactor(agent): establish Clean Architecture package boundaries`
5. `feat(agent): add Agent value objects and status types`
6. `feat(agent): add Agent aggregate and invariants`
7. `feat(agent): add domain repository contract`
8. `feat(agent): add application commands queries and results`
9. `feat(agent): define Agent application use cases`
10. `feat(agent): implement Agent application services`
11. `feat(agent): add JPA persistence entity`
12. `feat(agent): add Spring Data Agent repository`
13. `feat(agent): add Agent persistence mapping`
14. `feat(agent): add MySQL repository adapter`
15. `db(agent): add initial MySQL agent schema`
16. `feat(agent): add Agent REST contracts`
17. `feat(agent): add Agent REST mapping`
18. `feat(agent): expose minimal Agent REST API`
19. `feat(agent): map Agent domain failures to API errors`
20. `test(agent): add domain invariant tests`
21. `test(agent): add application use-case tests`
22. `test(agent): add MySQL Testcontainers foundation`
23. `test(agent): add MySQL persistence integration tests`
24. `test(agent): add REST slice tests`
25. `test(agent): enforce Clean Architecture boundaries`
26. `docs(agent): finalize MySQL Clean Architecture implementation`

Çok küçük commit'ler tek bir coherent change temsil ediyorsa güvenle birleştirilebilir. Yalnız commit sayısını azaltmak için ilgisiz layer'ları birleştirme.

---

# Day 8 Final Gate

Day 8 yalnızca aşağıdakiler sağlanırsa kapanır:

- AgentService MySQL kullanıyor
- PostgreSQL driver AgentService'ten kaldırılmış
- schema migration çalışıyor
- domain package Spring/JPA dependency taşımıyor
- Agent Aggregate invariant'ları enforce ediyor
- CreateAgent çalışıyor
- GetAgent çalışıyor
- ChangeAvailability çalışıyor
- duplicate license reddediliyor
- duplicate user reddediliyor
- invalid availability state reddediliyor
- REST request/response DTO'ları domain/JPA entity'den ayrı
- OSIV disabled
- ddl-auto update kullanılmıyor
- Testcontainers MySQL semantics'i doğruluyor
- Clean Architecture rule'ları automated
- Config/Eureka/Actuator baseline hâlâ çalışıyor
- docs gerçek kodu yansıtıyor
- Buyer/Seller/Property/Search implementation Day 8'e sızmıyor
