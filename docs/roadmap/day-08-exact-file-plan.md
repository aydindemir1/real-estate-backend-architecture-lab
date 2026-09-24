# Day 8 — Exact File / Class / Commit Plan

## 0. Scope

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

# Task 1 — Existing AgentService source audit

## Verify

Mevcut dosyalar:
- `AgentService/build.gradle`
- `AgentService/src/main/resources/application.yml`

Target bootstrap path:
- `AgentService/src/main/java/com/aydindemir/agent/AgentServiceApplication.java`

Main branch'te bu exact path bulunmadığı için implementation başında mevcut bootstrap class/package tespit edilir.

## Decision

Target base package:
`com.aydindemir.agent`

Mevcut bootstrap class farklı package'taysa:
- move/refactor yapılır
- component scan root target package olur

## Commit

`refactor(agent): align application base package`

---

# Task 2 — Build dependencies

## Modify
`AgentService/build.gradle`

## Remove
- PostgreSQL runtime driver

## Keep
- Spring Cloud Eureka Client
- Spring Cloud Config Client
- Spring Boot Actuator
- existing tracing baseline
- Spring Data JPA

## Add
- MySQL JDBC driver
- migration tool selected for relational schema
- Testcontainers JUnit Jupiter
- MySQL Testcontainers integration dependency where needed
- ArchUnit test dependency if not already globally available

## Important
Migration tool must be one project-wide choice.

If Flyway is selected:
- do not mix Liquibase in AgentService.

If Liquibase is already established as project-wide baseline:
- use Liquibase.

The final tool choice is confirmed before implementation commit.

## Commit

`build(agent): switch persistence dependencies to MySQL`

---

# Task 3 — Configuration

## Modify
`AgentService/src/main/resources/application.yml`

Keep only bootstrap-level config:
- spring.application.name
- configserver import
- CONFIG_SERVER_URL fallback

Do not put real DB secret here.

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

Secrets via environment variables until Vault Day 21:
- AGENT_DB_USER
- AGENT_DB_PASSWORD

## Commit

`config(agent): add MySQL datasource configuration`

---

# Task 4 — Clean Architecture package skeleton

## Create target packages

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

## Rule
No empty placeholder class unless needed by upcoming tasks.

## Commit

`refactor(agent): establish Clean Architecture package boundaries`

---

# Task 5 — Domain Value Objects

## Create

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

## Tests

`src/test/java/com/aydindemir/agent/domain/model/LicenseNumberTest.java`

`src/test/java/com/aydindemir/agent/domain/model/AgencyInfoTest.java`

## Commit

`feat(agent): add Agent value objects and status types`

---

# Task 6 — Agent Aggregate

## Create

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

## Construction

Prefer static factory:
`Agent.create(..., Clock clock)`

or constructor/factory equivalent.

## Behavior

- changeAvailability(...)
- changeStatus(...)
- updateProfile(...) only if Day 8 endpoint needs it; otherwise defer

## Invariants

- SUSPENDED agent cannot be AVAILABLE
- INACTIVE agent cannot be AVAILABLE
- required identity/license data cannot be null
- state changed through behavior, not public setter

## Exceptions

Create:
- `domain/exception/InvalidAgentStateException.java`
- `domain/exception/DuplicateLicenseNumberException.java`
- `domain/exception/AgentNotFoundException.java`

## Tests

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

# Task 7 — Domain repository abstraction

## Create

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

# Task 8 — Application contracts

## Create commands

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

## Create query

`application/query/GetAgentQuery.java`

## Create result

`application/result/AgentResult.java`

Do not return domain Agent directly from presentation.

## Commit

`feat(agent): add application commands queries and results`

---

# Task 9 — Use-case interfaces

## Create

`application/usecase/CreateAgentUseCase.java`

`application/usecase/GetAgentUseCase.java`

`application/usecase/ChangeAvailabilityUseCase.java`

Each interface should express business capability, not generic CRUD.

## Commit

`feat(agent): define Agent application use cases`

---

# Task 10 — AgentApplicationService

## Create

`application/service/AgentApplicationService.java`

Implements:
- CreateAgentUseCase
- GetAgentUseCase
- ChangeAvailabilityUseCase

## Create flow

### CreateAgent
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
Write use-cases transactional at application boundary.

## Important
Logical transaction belongs here.

Physical Spring `@Transactional` placement follows agreed pragmatic Spring approach while domain remains framework-free.

## Tests

`AgentApplicationServiceTest.java`

Mocks/fakes only repository boundary.

## Commit

`feat(agent): implement Agent application services`

---

# Task 11 — JPA persistence entity

## Create

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

## Rule
This class is not domain Agent.

No API serialization.

## Commit

`feat(agent): add JPA persistence entity`

---

# Task 12 — Spring Data repository

## Create

`infrastructure/persistence/repository/SpringDataAgentRepository.java`

Extends:
`JpaRepository<AgentJpaEntity, UUID>`

Methods:
- existsByLicenseNumber(...)
- existsByUserId(...)

No business method naming noise.

## Commit

`feat(agent): add Spring Data Agent repository`

---

# Task 13 — Persistence mapper

## Create

`infrastructure/persistence/mapper/AgentPersistenceMapper.java`

Mappings:
- domain -> JPA entity
- JPA entity -> domain

## Decision
MapStruct can be used if project dependency policy supports it.

However domain reconstruction must preserve invariants without accidentally calling create-new behavior that resets timestamps.

If mapping semantics are non-trivial, explicit Java mapper is preferred over magical generated mapping.

## Commit

`feat(agent): add Agent persistence mapping`

---

# Task 14 — Repository adapter

## Create

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

# Task 15 — MySQL migration

## Create based on chosen tool

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

No speculative indexes beyond uniqueness/basic query requirements.

## Commit

`db(agent): add initial MySQL agent schema`

---

# Task 16 — Infrastructure configuration

## Create if necessary

`infrastructure/configuration/PersistenceConfiguration.java`

Only if explicit bean wiring is needed.

Do not create configuration class just for ceremony.

## JPA settings
- ddl-auto validate/none
- open-in-view false

## Commit

Only if file/change exists:
`config(agent): configure MySQL persistence behavior`

---

# Task 17 — REST request DTOs

## Create

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

## Rule
No entity/domain binding.

## Commit

`feat(agent): add Agent REST request contracts`

---

# Task 18 — REST response DTO

## Create

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

# Task 19 — REST mapper

## Create

`presentation/rest/mapper/AgentRestMapper.java`

Mappings:
- CreateAgentRequest -> CreateAgentCommand
- AgentResult -> AgentResponse
- ChangeAvailabilityRequest + path id -> command

No business rule.

## Commit

`feat(agent): add Agent REST mapping`

---

# Task 20 — AgentController

## Create

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
Service-local path remains prefixless; Gateway external prefix handled separately.

## Commit

`feat(agent): expose minimal Agent REST API`

---

# Task 21 — Error mapping

## Modify/create project-consistent global error handling

Agent-specific mappings:
- AgentNotFoundException -> 404 AGENT_NOT_FOUND
- DuplicateLicenseNumberException -> 409 DUPLICATE_LICENSE_NUMBER
- duplicate user -> stable conflict code, add if needed
- InvalidAgentStateException -> 409 INVALID_AGENT_STATE or 422 depending semantic classification

Use stable error code contract.

## Exact class location
Prefer service presentation/error package only if common existing exception handler is not shared.

Do not create a cross-service shared error library on Day 8.

## Commit

`feat(agent): map Agent domain failures to API errors`

---

# Task 22 — Domain tests

## Create

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

# Task 23 — Application tests

## Create

`src/test/java/com/aydindemir/agent/application/service/AgentApplicationServiceTest.java`

Cases:
- create success
- duplicate license
- duplicate user
- get success
- not found
- change availability success
- invalid domain transition propagated

No Spring context required.

## Commit

`test(agent): add application use-case tests`

---

# Task 24 — MySQL Testcontainers base

## Create candidate

`src/test/java/com/aydindemir/agent/infrastructure/persistence/AgentMySqlContainerTestBase.java`

Or project-standard shared test utility if already exists.

Use:
- MySQL container
- dynamic datasource properties
- migration runs against container

## Rule
No hard-coded shared local MySQL for tests.

## Commit

`test(agent): add MySQL Testcontainers foundation`

---

# Task 25 — Persistence integration tests

## Create

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

# Task 26 — Optimistic locking test

If version/concurrent write is in Day 8 scope:

Create:
`AgentOptimisticLockingIntegrationTest.java`

Scenario:
1. load same entity twice
2. update first
3. update second
4. expect optimistic conflict

If Agent concurrency is not critical yet, this test may be deferred and version field simply established.

Decision should be explicit, not accidental.

## Commit
If implemented:
`test(agent): verify optimistic locking behavior`

---

# Task 27 — Controller slice test

## Create

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

# Task 28 — Architecture fitness tests

## Create

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

# Task 29 — Integration smoke

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

No Postman-only acceptance; automated tests remain primary.

## Commit
No commit unless fix needed.

---

# Task 30 — Documentation

## Modify

- `AgentService/docs/DESIGN.md`
- `AgentService/docs/PACKAGE-DESIGN.md`
- `AgentService/ROADMAP.md`
- `docs/roadmap/day-08-agent-mysql-clean.md`

## Update with actual implementation
- actual package names
- actual migration tool
- actual config keys
- exact test coverage
- deviations from plan
- completed status

## Optional README
If AgentService has a module README, update setup/run instructions.

## Commit

`docs(agent): finalize MySQL Clean Architecture implementation`

---

# Recommended Commit Sequence

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

Commits that are too tiny may be safely combined if they represent one coherent change. Do not combine unrelated layers merely to reduce commit count.

---

# Day 8 Final Gate

Day 8 closes only if:

- AgentService uses MySQL
- PostgreSQL driver removed from AgentService
- schema migration works
- domain package has no Spring/JPA dependency
- Agent Aggregate enforces invariants
- CreateAgent works
- GetAgent works
- ChangeAvailability works
- duplicate license is rejected
- duplicate user is rejected
- invalid availability state is rejected
- REST request/response DTOs are separate from domain/JPA entity
- OSIV is disabled
- ddl-auto update is not used
- Testcontainers verifies MySQL semantics
- Clean Architecture rules are automated
- Config/Eureka/Actuator baseline still works
- docs reflect actual code
- no Buyer/Seller/Property/Search implementation leaks into Day 8
