# Day 7 — Implementation Readiness Audit

## Verdict

**READY WITH MANDATORY PRE-IMPLEMENTATION CHECKS**

Day 7 can begin. No architecture blocker exists, but the implementation must apply the corrections below before the milestone can be closed.

## Verified current main baseline

### Build / modules
- Java 21 toolchain.
- Spring Boot 4.1.1.
- Spring Cloud 2025.1.3.
- SearchService is not yet registered in `settings.gradle`.
- Root `build.gradle` currently forces Web MVC, OpenAPI, MapStruct, Auth0 JWT and OpenFeign into every subproject.

### Service persistence
Current service build files still use JPA + PostgreSQL for:
- AgentService
- BuyerService
- SellerService
- PropertyService

This matches the Day 7 plan: Day 7 prepares infrastructure only; datastore code migration occurs one service per later Day.

### Current Docker Compose
Present:
- Auth PostgreSQL
- UserProfile PostgreSQL
- Agent PostgreSQL
- Buyer PostgreSQL
- Property PostgreSQL
- Seller PostgreSQL
- RabbitMQ

Missing and therefore correctly planned for Day 7:
- MySQL
- Couchbase
- Cassandra
- MongoDB
- Elasticsearch
- Redis

### Secret hygiene
`.gitignore` already correctly ignores:
- `.env`
- `.env.*`
and allows:
- `.env.example`

However literal secret-like defaults currently exist in:
- `docker-compose.yml`
- ConfigServerLocal service config files

Examples include local database passwords, RabbitMQ credentials and Auth JWT secret fallback.

Therefore Day 7 secret cleanup must cover both Compose and the local Config Server repository.

## Mandatory implementation checks

### 1. Create implementation branch
Branch does not currently exist.

Create from current `main`:
`day/07-build-data-infra`

Do not implement Day 7 on the planning branch.

### 2. Preserve Day 1–6 behavior
Dependency cleanup is allowed only if all affected modules still compile/test.

Root dependency removal must be incremental:
1. identify actual owner modules
2. add dependency locally
3. remove from root
4. run verification

Never remove all global dependencies first and repair later.

### 3. Normalize environment variable names
Current config uses names such as:
- `RABBITMQ_USERNAME`
- `RABBITMQ_PASSWORD`

Day 7 `.env.example`, Compose and Config Server local config must use one canonical naming scheme. Do not introduce both `RABBITMQ_USER` and `RABBITMQ_USERNAME`.

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

### 5. SearchService remains foundation-only
Day 7 creates:
- Gradle module
- bootstrap class
- application.yml
- baseline Config/Eureka/Actuator/tracing dependencies

No search domain, controller, document, repository, query handler or index mapping.

### 6. Temporary PostgreSQL removal timing
Remove Agent/Buyer/Seller/Property PostgreSQL **Compose services** only after their replacement datastore containers are defined and Compose validates.

Do not remove JPA/PostgreSQL code dependencies on Day 7. Those switch on Days 8–11.

### 7. Heavy local infrastructure
Couchbase + Cassandra + Elasticsearch can be resource-heavy.

Use Compose profiles or documented selective startup. Day 7 final gate does not require every heavy datastore to remain running simultaneously if each is individually verified healthy.

### 8. Version selection
Before implementation commit, verify each selected container image and any new Testcontainers dependency against the actual project stack.

Pin explicit versions. No `latest`, wildcard or dynamic versions.

## Recommended exact execution order

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

## Decision

There is no need for another architecture-planning round before implementation.

The next action is to create `day/07-build-data-infra` from `main` and execute Day 7 task-by-task with small commits.
