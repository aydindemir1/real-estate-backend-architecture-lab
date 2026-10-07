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
- unit + application + integration + architecture testleri

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
- component scan root, target package olur

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
- mevcut tracing baseline
- Spring Data JPA

## Ekle
- MySQL JDBC driver
- relational schema için seçilmiş migration tool
- Testcontainers JUnit Jupiter
- MySQL Testcontainers integration dependency where needed
- global olarak mevcut değilse ArchUnit test dependency

## Önemli
Migration tool project genelinde tek bir seçim olmalıdır.

Flyway seçildiyse:
- do not mix Liquibase in AgentService.

Liquibase zaten project-wide baseline olarak belirlenmişse:
- use Liquibase.

Nihai tool seçimi implementation commit'inden önce kesinleştirilir.

## Commit

`build(agent): switch persistence dependencies to MySQL`

---

# Görev 3 — Konfigürasyon

## Değiştir
`AgentService/src/main/resources/application.yml`

Yalnızca bootstrap-level config'i koru:
- spring.application.name
- configserver import
- CONFIG_SERVER_URL fallback

Buraya gerçek DB secret koyma.

## Config Server hedefi

Mevcut Config Server source içinde AgentService external config'ini oluştur/güncelle.

Gerekli non-secret config:
- datasource URL template
- gerekliyse driver class
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
- business-safe ise normalize edilir

`domain/model/AgencyInfo.java`
- agencyName
- model izin veriyorsa registrationNumber opsiyonel
- model izin veriyorsa officePhone opsiyonel

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

Alanlar:
- AgentId id
- UserId userId
- LicenseNumber licenseNumber
- AgencyInfo agencyInfo
- AgentStatus status
- AvailabilityStatus availability
- Instant createdAt
- Instant updatedAt
- yalnızca domain ihtiyaç duyuyorsa long/int version representation

## Oluşturma

Prefer static factory:
`Agent.create(..., Clock clock)`

veya eşdeğer constructor/factory.

## Davranış

- changeAvailability(...)
- changeStatus(...)
- `updateProfile(...)` yalnız Day 8 endpoint'i ihtiyaç duyuyorsa eklenir; aksi halde ertelenir

## Invariant'lar

- SUSPENDED agent cannot be AVAILABLE
- INACTIVE agent cannot be AVAILABLE
- zorunlu identity/license verisi null olamaz
- state changed through behavior, not public setter

## Exception'lar

Create:
- `domain/exception/InvalidAgentStateException.java`
- `domain/exception/DuplicateLicenseNumberException.java`
- `domain/exception/AgentNotFoundException.java`

## Testler

`AgentTest.java`

Scenarios:
- active agent oluşturma
- ACTIVE -> AVAILABLE izinli
- SUSPENDED -> AVAILABLE reddedilir
- INACTIVE -> AVAILABLE reddedilir
- status değişikliği availability semantics'i enforce eder

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
Alanlar:
- userId
- licenseNumber
- agency info alanları

`application/command/ChangeAvailabilityCommand.java`
Alanlar:
- agentId
- availability

Use record where appropriate.

## Oluştur query

`application/query/GetAgentQuery.java`

## Oluştur result

`application/result/AgentResult.java`

Presentation'dan domain Agent'i doğrudan döndürme.

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
3. Aggregate oluştur
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

Mock/fake yalnızca repository boundary için kullanılmalı.

## Commit

`feat(agent): implement Agent application services`

---

# Görev 11 — JPA persistence entity

## Oluştur

`infrastructure/persistence/entity/AgentJpaEntity.java`

Table:
`agents`

Alanlar:
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

Ancak domain reconstruction, timestamp'leri sıfırlayan create-new behavior'ı yanlışlıkla çağırmadan invariant'ları korumalıdır.

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

`@Repository` yalnızca infrastructure class'a eklenmelidir.

## Commit

`feat(agent): add MySQL repository adapter`

---

# Görev 15 — MySQL migration

## Oluştur based on chosen tool

Flyway adayı:
`AgentService/src/main/resources/db/migration/V1__create_agents_table.sql`

Liquibase seçildiyse:
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
- zorunlu alanlar NOT NULL

Indexes:
Yalnızca query-backed index'ler.

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

Yalnızca file/change varsa:
`config(agent): configure MySQL persistence behavior`

---

# Görev 17 — REST request DTO'ları

## Oluştur

`presentation/rest/request/CreateAgentRequest.java`

Alanlar:
- userId
- licenseNumber
- agencyName
- agencyRegistrationNumber
- officePhone

Jakarta validation:
- @NotNull/@NotBlank
- yalnız contract doğrulanmışsa format constraint'leri

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

Alanlar:
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

Küçükse request contract commit'i ile birleştirilebilir:
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
- Location header adayı
- response AgentResponse

### GET /agents/{agentId}
- GetAgent
- 200

### PATCH /agents/{agentId}/availability
- ChangeAvailability
- 200 veya 204; project API standard bunlardan birini tutarlı şekilde seçmelidir
- updated AgentResponse dönüyorsa önerilen 200

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
- duplicate user -> stable conflict code; gerekiyorsa ekle
- InvalidAgentStateException -> semantic classification'a göre 409 INVALID_AGENT_STATE veya 422

Stable error code contract kullan.

## Exact class location
Common existing exception handler shared değilse service presentation/error package tercih et.

Day 8'de cross-service shared error library oluşturma.

## Commit

`feat(agent): map Agent domain failures to API errors`

---

# Görev 22 — Domain testleri

## Oluştur

`src/test/java/com/aydindemir/agent/domain/model/AgentTest.java`

`LicenseNumberTest.java`

Aday senaryolar:
- valid create senaryosu
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

Senaryolar:
- create başarılı
- duplicate license
- duplicate user
- get başarılı
- not found
- change availability başarılı
- invalid domain transition propagate edilir

Spring context gerekmez.

## Commit

`test(agent): add application use-case tests`

---

# Görev 24 — MySQL Testcontainers temeli

## Oluştur candidate

`src/test/java/com/aydindemir/agent/infrastructure/persistence/AgentMySqlContainerTestBase.java`

Veya zaten varsa project-standard shared test utility kullan.

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

Senaryolar:
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

Version/concurrent write Day 8 scope içindeyse:

Create:
`AgentOptimisticLockingIntegrationTest.java`

Senaryo:
1. load same entity twice
2. update first
3. update second
4. expect optimistic conflict

Agent concurrency henüz kritik değilse bu test ertelenebilir ve yalnızca version field oluşturulabilir.

Karar explicit olmalı, accidental olmamalıdır.

## Commit
Uygulanırsa:
`test(agent): verify optimistic locking behavior`

---

# Görev 27 — Controller slice testi

## Oluştur

`presentation/rest/AgentControllerTest.java`

Use:
- `@WebMvcTest` veya mevcut Spring Boot eşdeğeri
- mock use-case boundary

Senaryolar:
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
- domain, application/infrastructure/presentation'a bağımlı olmamalı
- application, infrastructure/presentation'a bağımlı olmamalı
- presentation application'a bağımlı olabilir, persistence'a bağımlı olmamalı
- infrastructure domain/application'a bağımlı olabilir
- domain package Spring/JPA package'larına bağımlı olmamalı
- slice/layer'lar arasında cycle olmamalı

## Commit

`test(agent): enforce Clean Architecture boundaries`

---

# Görev 29 — Integration smoke

Run:
- MySQL container/local MySQL
- Config Server
- gerekiyorsa Eureka
- AgentService

Doğrula:
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
- gerçek package adları
- gerçek migration tool
- gerçek config key'leri
- exact test coverage
- plandan sapmalar
- tamamlanma durumu

## Opsiyonel README
AgentService için module README varsa setup/run talimatlarını güncelle.

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
