# Day 7 — Implementation Hazırlık Denetimi

## Karar

**ZORUNLU IMPLEMENTATION ÖNCESİ KONTROLLERLE HAZIR**

Day 7 başlayabilir. Herhangi bir architecture blocker yoktur; ancak milestone kapatılmadan önce implementation aşağıdaki düzeltmeleri uygulamalıdır.

## Doğrulanan mevcut main baseline

### Build / modules
- Java 21 toolchain.
- Spring Boot 4.1.1.
- Spring Cloud 2025.1.3.
- SearchService henüz `settings.gradle` içine kayıtlı değildir.
- Root `build.gradle` şu anda Web MVC, OpenAPI, MapStruct, Auth0 JWT ve OpenFeign dependency'lerini her subproject'e zorunlu olarak uygular.

### Service persistence
Mevcut service build dosyaları hâlâ aşağıdakiler için JPA + PostgreSQL kullanmaktadır:
- AgentService
- BuyerService
- SellerService
- PropertyService

Bu durum Day 7 planıyla uyumludur: Day 7 yalnızca infrastructure hazırlığı yapar; datastore kod migration'ı sonraki Day'lerde her seferinde tek bir service için gerçekleştirilir.

### Mevcut Docker Compose
Mevcut:
- Auth PostgreSQL
- UserProfile PostgreSQL
- Agent PostgreSQL
- Buyer PostgreSQL
- Property PostgreSQL
- Seller PostgreSQL
- RabbitMQ

Eksik ve bu nedenle Day 7 için doğru şekilde planlanmış:
- MySQL
- Couchbase
- Cassandra
- MongoDB
- Elasticsearch
- Redis

### Secret hygiene
`.gitignore` zaten aşağıdakileri doğru şekilde ignore eder:
- `.env`
- `.env.*`
ve şuna izin verir:
- `.env.example`

Ancak şu anda aşağıdaki yerlerde literal secret benzeri default değerler bulunmaktadır:
- `docker-compose.yml`
- ConfigServerLocal service config files

Örnekler arasında local database password'leri, RabbitMQ credential'ları ve Auth JWT secret fallback değeri bulunur.

Bu nedenle Day 7 secret cleanup hem Compose hem de local Config Server repository'sini kapsamalıdır.

## Zorunlu implementation kontrolleri

### 1. Implementation branch oluştur
Branch şu anda mevcut değildir.

Mevcut `main` üzerinden oluştur:
`day/07-build-data-infra`

Day 7 implementation'ını planning branch üzerinde yapma.

### 2. Day 1–6 davranışını koru
Dependency cleanup yalnızca etkilenen tüm module'ler hâlâ compile/test oluyorsa yapılabilir.

Root dependency kaldırma işlemi kademeli olmalıdır:
1. identify actual owner modules
2. add dependency locally
3. remove from root
4. run verification

Tüm global dependency'leri önce kaldırıp daha sonra düzeltme yapma.

### 3. Environment variable isimlerini normalize et
Mevcut config şu tür isimler kullanır:
- `RABBITMQ_USERNAME`
- `RABBITMQ_PASSWORD`

Day 7 `.env.example`, Compose ve Config Server local config tek bir canonical naming scheme kullanmalıdır. Hem `RABBITMQ_USER` hem de `RABBITMQ_USERNAME` oluşturma.

### 4. Config Server secret cleanup
The following must lose secret literal fallbacks:
- Auth DB password
- UserProfile DB password
- Agent temporary DB password
- Buyer temporary DB password
- Seller temporary DB password
- Property temporary DB password
- RabbitMQ password
- Auth JWT secret

Local developer values belong in uncommitted `.env` until Vault Day 24.

### 5. SearchService yalnızca foundation seviyesinde kalır
Day 7 creates:
- Gradle module
- bootstrap class
- application.yml
- baseline Config/Eureka/Actuator/tracing dependencies

Search domain, controller, document, repository, query handler veya index mapping oluşturulmaz.

### 6. Temporary PostgreSQL kaldırma zamanı
Remove Agent/Buyer/Seller/Property PostgreSQL **Compose services** only after their replacement datastore containers are defined and Compose validates.

Do not remove JPA/PostgreSQL code dependencies on Day 7. Those switch on Days 8–11.

### 7. Ağır local infrastructure
Couchbase + Cassandra + Elasticsearch can be resource-heavy.

Use Compose profiles or documented selective startup. Day 7 final gate does not require every heavy datastore to remain running simultaneously if each is individually verified healthy.

### 8. Version seçimi
Before implementation commit, verify each selected container image and any new Testcontainers dependency against the actual project stack.

Pin explicit versions. No `latest`, wildcard or dynamic versions.

## Önerilen kesin execution sırası

1. create `day/07-build-data-infra` from `main`
2. dependency ownership matrix
3. datastore dependency aliases
4. safe root dependency cleanup
5. SearchService foundation
6. secret/config hygiene + `.env.example`
7. MySQL
8. MongoDB
9. Redis
10. Elasticsearch
11. Couchbase
12. Cassandra
13. remove temporary service PostgreSQL Compose entries
14. selective profiles/resource strategy
15. Compose validation
16. Gradle regression
17. documentation

This order keeps cheaper/easier infrastructure checks before the heavier Couchbase/Cassandra startup work.

## Day 7 readiness checklist

- [x] Day 7 scope is isolated from business implementation
- [x] exact file-level plan exists
- [x] current main matches expected pre-Day-7 state
- [x] SearchService absence confirmed
- [x] temporary PostgreSQL services confirmed
- [x] secret-literal issue identified
- [x] root dependency overreach identified
- [x] `.gitignore` env policy already correct
- [ ] implementation branch created
- [ ] exact container versions verified
- [ ] dependency ownership matrix executed
- [ ] secret env names normalized

## Karar

Implementation öncesinde yeni bir architecture-planning turuna ihtiyaç yoktur.

The next action is to create `day/07-build-data-infra` from `main` and execute Day 7 task-by-task with small commits.
