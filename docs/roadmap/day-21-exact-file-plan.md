# Day 21 — Exact File / Secret / Bus / Commit Plan

## 0. Scope

Day 21 yalnızca Spring Cloud Vault + Spring Cloud Bus içindir.

Hedef:
- Config vs Secret ownership ayrımı
- Vault local infrastructure
- secret taxonomy
- service secret paths
- Spring Vault integration
- Spring Cloud Bus
- refresh-safe configuration
- secret rotation baseline
- fail-fast behavior
- integration tests
- operational runbook

Day 21 içinde:
- production HSM/KMS yok
- mTLS yok
- dynamic DB credentials zorunlu değil
- Kubernetes Secret yok
- full secret rotation automation yok

## Task 1 — Secret inventory

Create:
- docs/security/secret-inventory.md

Inventory candidate secrets:
- PostgreSQL credentials
- MySQL credentials
- Couchbase credentials
- Cassandra credentials
- MongoDB credentials
- Redis password if enabled
- RabbitMQ credentials
- Kafka credentials only if auth later enabled
- Keycloak client secrets
- JWT/signing material only if still applicable
- internal client credentials

Classify:
- credential
- token/client secret
- signing/encryption material
- local-only bootstrap secret

Commit: docs(security): inventory application secrets

## Task 2 — Config ownership matrix

Create/update:
- docs/architecture/configuration-ownership.md

Ownership:
- repository defaults -> application config
- environment non-secret distributed config -> Config Server
- secret -> Vault
- runtime dynamic refresh -> Bus only for refresh-safe config

Rule:
Config Server must not store secret values.

Commit: docs(config): define Config Server and Vault ownership

## Task 3 — Vault local infrastructure

Modify docker-compose.yml.

Add:
- vault

Required:
- explicit image version
- local dev mode or file backend only for learning
- root/bootstrap token via env
- healthcheck
- explicit port

Use dev mode only if clearly marked local-only.

Commit: infra(vault): add local Vault service

## Task 4 — Vault bootstrap environment

Update .env.example:
- VAULT_ADDR
- VAULT_TOKEN or dev root token variable

Do not commit real token.

Commit: chore(vault): add local Vault environment example

## Task 5 — Secret path taxonomy

Define KV paths.

Candidate:
- secret/real-estate/auth-service
- secret/real-estate/user-profile-service
- secret/real-estate/agent-service
- secret/real-estate/buyer-service
- secret/real-estate/seller-service
- secret/real-estate/property-service
- secret/real-estate/search-service
- secret/real-estate/api-gateway

Per-service keys only.

Do not create one giant shared secret blob.

Commit: docs(vault): define service secret path taxonomy

## Task 6 — Vault policy model

Create local policy files candidate:
- infra/vault/policies/auth-service.hcl
- ... per service as needed

Each service reads only its own path plus explicitly shared path if unavoidable.

Do not grant wildcard read to secret/real-estate/* for every service.

Commit: feat(vault): add least-privilege service policies

## Task 7 — Vault auth method decision

For local Day 21:
- token auth may be acceptable for learning

For production-like direction:
- AppRole or platform identity should be documented as next step

Do not pretend dev root token is production-grade.

Commit: docs(vault): document local and production-like auth modes

## Task 8 — Spring Cloud Vault dependency

Add only to services that consume secrets.

Dependency:
- spring-cloud-starter-vault-config

Do not globally force if some modules do not need it.

Commit: build(vault): add Spring Cloud Vault dependencies

## Task 9 — Service Vault bootstrap config

Per service configure:
- Vault URI
- authentication method
- KV backend
- application context/path
- fail-fast

Use standard Spring Cloud Vault properties where possible.

No custom secret fetch code.

Commit per small batch:
- config(agent): load secrets from Vault
- config(buyer): load secrets from Vault
- etc.

## Task 10 — Move datastore credentials to Vault

For each service remove literal/env-only runtime credential dependency where Vault is now primary.

Examples:
- agent-service -> MySQL user/password
- buyer-service -> Couchbase user/password
- seller-service -> Cassandra user/password
- property-service -> Mongo user/password
- search-service -> Elasticsearch auth if enabled

Environment variables may remain as local bootstrap/fallback only if explicitly documented.

Commit: feat(secrets): migrate datastore credentials to Vault

## Task 11 — Move messaging credentials to Vault

Move:
- RabbitMQ username/password
- Kafka auth secret only if broker auth enabled

Do not move non-secret host/port/topic names.

Commit: feat(secrets): migrate messaging credentials to Vault

## Task 12 — Move Keycloak client secrets to Vault

Move:
- service-to-service client secret
- Gateway confidential client secret if applicable

Keep:
- issuer URI
- client ID if not secret

Commit: feat(secrets): migrate OAuth client secrets to Vault

## Task 13 — Secret access verification

Each service startup must fail if critical secret missing/invalid.

Test:
- valid secret -> starts
- missing critical secret -> fail-fast

Commit: test(vault): verify critical secret loading

## Task 14 — Secret logging guard

Review logs/config debug behavior.

Ensure:
- passwords/tokens not logged
- actuator env/configprops sanitizes secrets

Add sanitization patterns only if framework defaults insufficient.

Commit: feat(security): harden secret redaction

## Task 15 — Spring Cloud Bus dependency

Add:
- spring-cloud-starter-bus-amqp or Kafka binder choice

Decision:
Prefer existing RabbitMQ infrastructure for Config Bus unless there is a strong reason to use Kafka.

Reason:
separate business Kafka event semantics from configuration bus traffic.

Commit: build(bus): add Spring Cloud Bus over RabbitMQ

## Task 16 — Bus configuration

Configure:
- Bus destination
- service ID
- broker credentials from Vault

Do not mix Bus destination with business command exchanges.

Commit: config(bus): add distributed config refresh channel

## Task 17 — Refresh-safe property inventory

Create:
- docs/config/refreshable-properties.md

Classify:
Refresh-safe candidate:
- feature/display tuning
- some timeout/retry thresholds
- log level via management endpoint if appropriate

Not refresh-safe by default:
- datasource driver
- DB URL
- broker bootstrap servers
- security issuer
- bean topology

Commit: docs(config): classify refresh-safe properties

## Task 18 — @RefreshScope usage

Use only on beans that genuinely need runtime refresh.

Do not annotate whole application/services indiscriminately.

Prefer typed @ConfigurationProperties with controlled refresh.

Commit: feat(config): add targeted refresh scope

## Task 19 — Bus refresh flow

Flow:
1. Config repository change
2. Config Server serves new value
3. busrefresh/refresh trigger
4. Bus event propagated
5. refresh-safe bean updates

Do not auto-refresh every config change without control.

Commit: feat(bus): add controlled distributed refresh flow

## Task 20 — Refresh authorization

Actuator bus endpoints must be restricted.

Only admin/ops context can trigger.

Do not expose publicly through Gateway without explicit protection.

Commit: feat(security): protect Bus refresh endpoints

## Task 21 — Config refresh integration test

Create:
- BusRefreshIntegrationTest.java

Scenario:
- service starts with value A
- Config Server source changes to B in test-controlled setup
- trigger Bus refresh
- refresh-safe bean observes B

Keep test deterministic.

Commit: test(bus): verify distributed config refresh

## Task 22 — Non-refresh-safe property test

Verify changing non-refresh-safe config does not silently mutate critical runtime infrastructure.

Document restart required.

Commit: test(config): verify immutable runtime configuration

## Task 23 — Secret rotation model

Select one client secret or password for rotation demonstration.

Candidate:
- Keycloak service client secret
or
- RabbitMQ credential if local setup supports clean rotation

Flow:
1. new secret written to Vault
2. dependent app refresh/restart strategy
3. old secret revoked
4. verify continuity

Do not rotate DB secret dynamically unless local DB/user management supports it cleanly.

Commit: docs(vault): define secret rotation procedure

## Task 24 — Secret rotation test

Automated or controlled integration test:
- app reads initial secret
- Vault secret updated
- refresh/restart applied according to policy
- app uses new secret

Commit: test(vault): verify selected secret rotation path

## Task 25 — Vault outage behavior

Startup:
- critical secret unavailable -> fail-fast

Runtime:
- existing connection may continue until lease/credential invalid
- new secret fetch fails visibly

Do not make repeated tight-loop secret fetches.

Commit: docs(vault): define Vault outage behavior

## Task 26 — Vault outage integration test

Scenario:
- stop Vault after service startup
- verify current safe behavior
- restart-required/refresh behavior documented

Do not overclaim HA resilience in local lab.

Commit: test(vault): verify Vault outage semantics

## Task 27 — Config Server outage interaction

Document difference:
- Config Server unavailable affects non-secret config retrieval
- Vault unavailable affects secret retrieval

Runbooks should not conflate them.

## Task 28 — Bus broker outage

If RabbitMQ unavailable:
- refresh propagation fails
- business service should continue with current config where safe

Bus outage must not stop normal request processing after startup.

Commit: test(bus): verify broker outage does not break steady-state service

## Task 29 — Secret versioning awareness

KV v2 supports versions.

Document:
- current version
- rollback previous secret version as emergency only

Do not build custom secret history UI.

## Task 30 — Actuator endpoint hardening

Review:
- /actuator/refresh if present
- /actuator/busrefresh or equivalent
- env
- configprops

Expose minimum necessary.

Commit: feat(security): harden config management endpoints

## Task 31 — Bus event separation

Architecture rule:
- Spring Cloud Bus events are configuration/control-plane events
- Kafka property/offer events are domain/data-plane events

Do not consume Bus events in business services for business workflow.

Commit: test(bus): enforce control-plane separation

## Task 32 — Vault/Config architecture tests

Possible checks:
- application.yml/config repo contains no known secret keys/values
- no service hard-codes client secret

Static test/script may scan forbidden property names carefully.

Commit: test(security): detect committed secret configuration

## Task 33 — Runbooks

Create:
- docs/runbooks/vault-outage.md
- docs/runbooks/config-refresh.md
- docs/runbooks/secret-rotation.md

Include:
- symptoms
- checks
- recovery
- verification

Commit: docs(runbook): add Vault and Config refresh procedures

## Task 34 — Documentation reconciliation

Modify:
- docs/standards/spring-cloud.md
- docs/standards/build-dependency-configuration.md only if actual nuance requires
- docs/roadmap/day-21-bus-vault.md
- affected service DESIGN docs

Record actual:
- Vault image/version
- auth mode
- secret paths
- Bus transport
- refresh-safe list
- rotation strategy
- outage semantics

Commit: docs(config): finalize Vault and Bus architecture

## Recommended Commit Sequence

1. docs(security): inventory application secrets
2. docs(config): define Config Server and Vault ownership
3. infra(vault): add local Vault service
4. chore(vault): add local Vault environment example
5. docs(vault): define service secret path taxonomy
6. feat(vault): add least-privilege service policies
7. docs(vault): document local and production-like auth modes
8. build(vault): add Spring Cloud Vault dependencies
9. feat(secrets): migrate datastore credentials to Vault
10. feat(secrets): migrate messaging credentials to Vault
11. feat(secrets): migrate OAuth client secrets to Vault
12. test(vault): verify critical secret loading
13. feat(security): harden secret redaction
14. build(bus): add Spring Cloud Bus over RabbitMQ
15. config(bus): add distributed config refresh channel
16. docs(config): classify refresh-safe properties
17. feat(config): add targeted refresh scope
18. feat(bus): add controlled distributed refresh flow
19. feat(security): protect Bus refresh endpoints
20. test(bus): verify distributed config refresh
21. test(config): verify immutable runtime configuration
22. docs(vault): define secret rotation procedure
23. test(vault): verify selected secret rotation path
24. docs(vault): define Vault outage behavior
25. test(vault): verify Vault outage semantics
26. test(bus): verify broker outage does not break steady-state service
27. feat(security): harden config management endpoints
28. test(bus): enforce control-plane separation
29. test(security): detect committed secret configuration
30. docs(runbook): add Vault and Config refresh procedures
31. docs(config): finalize Vault and Bus architecture

Small adjacent secret-migration commits may be grouped by service type, but Vault infra, secret migration, Bus refresh and tests should remain independently reviewable.

## Explicitly Deferred from Day 21

Do not implement:
- Kubernetes Secrets
- cloud KMS/HSM
- production Vault HA cluster
- PKI engine
- database dynamic credentials unless cleanly supported
- mTLS certificates
- full zero-downtime credential rotation for every dependency

## Critical Design Note — Config vs Secret

Config Server carries non-secret configuration.
Vault carries secrets.

Do not duplicate the same secret in both.

## Critical Design Note — Bus is Control Plane

Spring Cloud Bus is for configuration/control propagation.

It is not a Domain Event bus.

## Critical Design Note — Refresh

Only explicitly refresh-safe properties may change live.

Critical infrastructure config may require restart.

## Day 21 Final Gate

Day 21 closes only if:
- Vault starts locally
- secret inventory exists
- service secret paths are explicit
- least-privilege Vault policies exist
- critical service credentials are read from Vault
- secrets are absent from committed config
- missing critical secret fails startup predictably
- secret values are redacted from logs/Actuator
- Spring Cloud Bus uses separate control-plane channel
- refresh-safe properties are documented
- distributed refresh works for approved property
- non-refresh-safe config remains restart-bound
- Bus refresh endpoints are protected
- one secret rotation path is demonstrated/tested
- Vault outage behavior is documented/tested
- Bus outage does not break normal steady-state service
- no Kubernetes/KMS/mTLS scope leaks into Day 21
- docs match actual implementation