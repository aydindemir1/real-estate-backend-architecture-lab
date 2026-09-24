# Day 14 — Exact File / Class / Commit Plan

## 0. Scope

Day 14 yalnızca Security/Identity foundation içindir.

Hedef:
- Keycloak
- OAuth2 / OIDC
- Spring Security Resource Server
- RBAC
- scope/authority mapping
- ownership checks
- service-to-service Client Credentials
- 401/403 semantics
- security tests

Day 14 içinde:
- full audit pipeline yok
- mTLS yok
- Vault yok
- API Gateway advanced rate limiting yok
- fine-grained ABAC policy engine yok

## Task 1 — Existing security baseline audit

Review:
- AuthService custom JWT flow
- ApiGatewayService security config
- downstream service security config
- existing login/register endpoints
- current token claims

Goal:
Day 14 sonrası primary auth path Keycloak olsun.

Legacy custom JWT implementation eğitim karşılaştırması için korunabilir fakat new service authorization Keycloak token modeline bağlanır.

Commit: docs(security): record current auth baseline and migration intent

## Task 2 — Keycloak local infrastructure

Modify: docker-compose.yml

Add service:
- keycloak

Required:
- explicit image version
- admin username/password from env
- persistent volume only if desired for local stability
- healthcheck/readiness
- dedicated port

Add .env.example variables:
- KEYCLOAK_ADMIN
- KEYCLOAK_ADMIN_PASSWORD

Commit: infra(security): add local Keycloak service

## Task 3 — Realm model

Create version-controlled realm config candidate:
- infra/keycloak/real-estate-realm.json

Realm:
- real-estate

Realm-level decisions:
- access token lifespan
- refresh token baseline
- brute-force detection candidate
- email/login policy only if local auth users are created

Do not over-customize theme/UI.

Commit: feat(security): add Keycloak realm baseline

## Task 4 — Client model

Define clients:
- api-gateway
- internal-service-client candidate for service-to-service

Optional individual service clients only if needed by client-credentials ownership model.

Gateway client:
- confidential/public decision based on flow
- bearer-only is not a Keycloak client mode in newer models; configure correct OIDC client semantics for resource server use

Internal service client:
- service account enabled
- client credentials grant

Commit: feat(security): add Keycloak client configuration

## Task 5 — Role model

Realm/application roles:
- BUYER
- SELLER
- AGENT
- ADMIN
- SERVICE

Rule:
SERVICE must not become universal super-role.

Service identities should receive only needed authorities/scopes.

Commit: feat(security): add application role model

## Task 6 — Scope / permission model

Candidate scopes:
- property.read
- property.write
- buyer.read
- buyer.write
- seller.read
- seller.write
- agent.read
- agent.write
- search.read

Do not create every possible scope if no endpoint currently needs it.

Day 14 minimum scopes should cover current service endpoints.

Commit: feat(security): add API scope model

## Task 7 — Identity claim model

Decide normalized claims used by application:
- sub
- preferred_username/email if needed
- roles
- scopes

Create architecture mapping:
Keycloak subject -> UserProfile authAccountId/user identity -> Buyer/Seller/Agent profile IDs

Important:
Role-specific resource ID must not be assumed equal to Keycloak subject unless explicitly designed.

Document lookup strategy:
- CurrentActor contains Keycloak subject/user identity
- application layer resolves owned SellerId/BuyerId/AgentId via local/profile mapping where needed

Commit: docs(security): finalize identity mapping model

## Task 8 — Common security authority converter design

Potential class per service or reusable technical helper only if stable:
- KeycloakJwtAuthenticationConverter.java

Responsibilities:
- map realm/client roles to Spring GrantedAuthority
- map OAuth scopes

Do not put business ownership logic here.

Commit: feat(security): add Keycloak authority mapping

## Task 9 — ApiGateway Resource Server

Modify ApiGatewayService build/config.

Add:
- spring-boot-starter-oauth2-resource-server

Create/modify:
- SecurityConfiguration.java

Rules:
- public health/openapi endpoints explicitly listed
- business routes authenticated by default
- coarse route-level role/scope rules where appropriate

Gateway validates token but downstream validates again.

Commit: feat(gateway): add OAuth2 resource server security

## Task 10 — Gateway token relay/propagation

Verify access token propagated to downstream services.

Use supported Gateway/OAuth2 mechanism.

Do not manually copy Authorization header in ad-hoc filters if framework support is sufficient.

Commit if change needed:
feat(gateway): propagate authenticated bearer token downstream

## Task 11 — Downstream Resource Server configuration

Affected services:
- AuthService only if still exposed and relevant
- UserProfileService
- AgentService
- BuyerService
- SellerService
- PropertyService
- SearchService

Each service:
- add oauth2-resource-server dependency if needed
- configure issuer-uri
- configure JWT validation
- authority converter
- default deny/authenticated policy

Do not trust Gateway network position alone.

Commit strategy:
one commit per service or logical small batch.

Examples:
- feat(agent): add resource server security
- feat(buyer): add resource server security

## Task 12 — CurrentActor abstraction

Create per service/application boundary if needed:
- CurrentActor.java
- CurrentActorProvider.java

Normalized fields:
- subject
- roles
- scopes

Domain model must not depend on SecurityContext.

Spring-specific implementation lives in presentation/infrastructure security adapter.

Commit: feat(security): add normalized current-actor boundary

## Task 13 — Ownership authorization pattern

Implement ownership in application layer.

Examples:
- Buyer can update only own BuyerPreferences
- Seller can create/list only own submissions
- Agent can change only own availability

Pattern:
1. authenticate
2. derive actor identity
3. resolve owned profile/resource identity
4. compare resource owner
5. authorize or throw ownership exception

Do not encode all ownership in @PreAuthorize SpEL expressions.

Commit per service:
- feat(buyer): enforce Buyer ownership
- feat(seller): enforce Seller ownership
- feat(agent): enforce Agent ownership

## Task 14 — Admin override policy

Define:
- ADMIN may bypass specific ownership checks
- bypass is explicit in application authorization service
- admin-sensitive actions should be auditable later

Do not scatter `hasRole('ADMIN')` through domain code.

Commit: feat(security): define admin ownership override policy

## Task 15 — Service-to-service Client Credentials

Initial use-case:
- internal service call where human user context is absent/insufficient

Create client credentials config in caller service.

Create technical token provider/client registration config.

Downstream:
- validate client identity/scope

Do not reuse ADMIN credentials.

Commit: feat(security): add client-credentials service identity

## Task 16 — Service authority scope

Assign minimal scope to service account.

Example future Agent availability call:
- agent.read or specific internal scope candidate

Do not grant broad SERVICE role without endpoint scope check.

Commit can group with Client Credentials.

## Task 17 — 401/403 semantics

Create/modify exception handling:
- unauthenticated -> 401
- authenticated but forbidden -> 403

Stable error codes:
- UNAUTHENTICATED
- INVALID_TOKEN
- TOKEN_EXPIRED candidate if distinguishable
- ACCESS_DENIED
- RESOURCE_OWNERSHIP_REQUIRED

Stack trace/internal auth details must not leak.

Commit: feat(security): standardize authentication and authorization errors

## Task 18 — Endpoint authorization matrix implementation

Use docs/security/authorization-matrix.md as source of truth.

Verify current endpoints:
- AgentService
- BuyerService
- SellerService
- PropertyService
- SearchService

Example:
- search read may allow authenticated/general scope based on design
- property publish requires seller/admin capability

Do not add rules for endpoints not implemented yet.

Commit: feat(security): apply endpoint role and scope policies

## Task 19 — Actuator security

Policy:
- liveness/readiness minimal exposure
- sensitive actuator endpoints restricted

Gateway/downstream both configured appropriately.

Commit: feat(security): restrict actuator endpoint access

## Task 20 — CORS / CSRF / session policy

For stateless bearer API:
- SessionCreationPolicy.STATELESS
- CSRF policy explicit
- CORS allowed origins explicit for current environment

No wildcard production-like origin.

Commit: feat(security): harden stateless API defaults

## Task 21 — Security config properties

Create typed application-specific config only if needed:
- SecurityProperties.java

Potential fields:
- issuer
- audience candidate
- internal client registration names

Prefer standard Spring Security properties where enough.

No client secret in source.

Commit: config(security): externalize identity provider settings

## Task 22 — Audience validation decision

Decide whether audience claim is required in current Keycloak token model.

If yes:
- add JwtDecoder validator
- test wrong audience

If not:
- document why issuer + signature + scope is sufficient for current lab.

Do not pretend audience validation exists if not configured.

Commit if implemented:
feat(security): validate JWT audience

## Task 23 — Security test infrastructure

Create test support:
- JwtTestTokenFactory.java or Spring Security mock-JWT utilities

Use synthetic JWT claims.

Do not require live Keycloak for every controller slice test.

Commit: test(security): add JWT test support

## Task 24 — Gateway security tests

Create:
- ApiGatewaySecurityTest.java

Cases:
- no token -> 401
- invalid token -> 401
- valid token route accepted
- wrong scope/role -> 403 where applicable

Commit: test(gateway): add resource server security tests

## Task 25 — Agent ownership tests

Create/update:
- AgentSecurityIntegrationTest.java

Cases:
- AGENT owns resource -> allowed
- AGENT other resource -> 403
- ADMIN override -> allowed

## Task 26 — Buyer ownership tests

Create/update:
- BuyerSecurityIntegrationTest.java

Cases:
- BUYER own preferences -> allowed
- another buyer -> 403
- wrong role -> 403

## Task 27 — Seller ownership tests

Create/update:
- SellerSecurityIntegrationTest.java

Cases:
- SELLER own submission -> allowed
- other seller -> 403
- inactive seller business error remains 422, not confused with auth failure

Commit options:
- test(agent): add ownership authorization tests
- test(buyer): add ownership authorization tests
- test(seller): add ownership authorization tests

## Task 28 — Downstream validation test

Ensure a downstream service rejects request with no/invalid JWT even if request bypasses Gateway.

This proves defense in depth.

Create one representative integration test per security configuration style, then reuse pattern across services.

Commit: test(security): verify downstream token validation

## Task 29 — Client Credentials integration test

Use live/local Keycloak integration test or controlled test container if practical.

Scenario:
1. obtain token using service client
2. call allowed internal endpoint
3. verify allowed
4. call endpoint outside granted scope
5. verify 403

Commit: test(security): verify service-to-service client credentials

## Task 30 — Keycloak configuration reproducibility test

Verify realm import starts cleanly.

Optional automated smoke:
- Keycloak container starts
- realm exists
- expected clients/roles exist

Do not make every unit test depend on Keycloak.

Commit: test(security): verify Keycloak realm bootstrap

## Task 31 — Legacy AuthService migration note

Document:
- custom JWT login/register baseline
- Keycloak is new primary identity provider
- whether AuthService remains account domain facade, gets reduced, or becomes legacy example

Do not delete working baseline until migration path is explicit.

Commit: docs(auth): document Keycloak migration boundary

## Task 32 — Audit logging hooks

Day 14 only foundation:
- mark sensitive operations that require audit
- no full audit pipeline yet

Potential interface:
- SecurityAuditPort only if a current action needs it now

Otherwise documentation only.

Do not create dead abstractions.

## Task 33 — Security documentation

Modify:
- docs/security/authorization-matrix.md
- docs/standards/security.md only if implementation requires clarification
- docs/roadmap/day-14-security-keycloak.md
- ApiGatewayService DESIGN/README
- affected service DESIGN docs

Create candidate:
- docs/security/identity-model.md

Document:
- realm
- clients
- roles
- scopes
- claim mapping
- ownership pattern
- service identity
- legacy auth boundary

Commit: docs(security): finalize Keycloak identity and authorization model

## Recommended Commit Sequence

1. docs(security): record current auth baseline and migration intent
2. infra(security): add local Keycloak service
3. feat(security): add Keycloak realm baseline
4. feat(security): add Keycloak client configuration
5. feat(security): add application role model
6. feat(security): add API scope model
7. docs(security): finalize identity mapping model
8. feat(security): add Keycloak authority mapping
9. feat(gateway): add OAuth2 resource server security
10. feat(gateway): propagate authenticated bearer token downstream — if needed
11. feat(agent): add resource server security
12. feat(buyer): add resource server security
13. feat(seller): add resource server security
14. feat(property): add resource server security
15. feat(search): add resource server security
16. feat(security): add normalized current-actor boundary
17. feat(agent): enforce Agent ownership
18. feat(buyer): enforce Buyer ownership
19. feat(seller): enforce Seller ownership
20. feat(security): define admin ownership override policy
21. feat(security): add client-credentials service identity
22. feat(security): standardize authentication and authorization errors
23. feat(security): apply endpoint role and scope policies
24. feat(security): restrict actuator endpoint access
25. feat(security): harden stateless API defaults
26. config(security): externalize identity provider settings
27. test(security): add JWT test support
28. test(gateway): add resource server security tests
29. test(agent): add ownership authorization tests
30. test(buyer): add ownership authorization tests
31. test(seller): add ownership authorization tests
32. test(security): verify downstream token validation
33. test(security): verify service-to-service client credentials
34. test(security): verify Keycloak realm bootstrap
35. docs(auth): document Keycloak migration boundary
36. docs(security): finalize Keycloak identity and authorization model

Adjacent service-security commits may be combined only if review remains clear. Do not collapse realm, Gateway, ownership and tests into one giant security commit.

## Explicitly Deferred from Day 14

Do not implement:
- Vault
- mTLS
- SPI/custom Keycloak provider
- external social login
- full audit event storage
- advanced policy engine
- fine-grained authorization server
- production certificate management
- rate limiting

## Critical Design Note — Gateway is not enough

Gateway validates token, but every downstream service also validates JWT.

This prevents accidental trust of network position.

## Critical Design Note — Role != Ownership

SELLER role does not grant access to every seller resource.

Ownership remains application-level authorization.

## Critical Design Note — Subject Mapping

Keycloak sub is authentication identity.

BuyerId, SellerId and AgentId are domain identities.

They must be mapped explicitly rather than assumed equal.

## Day 14 Final Gate

Day 14 closes only if:
- Keycloak starts reproducibly
- realm/client/role config is version-controlled
- Gateway validates JWT
- downstream services validate JWT independently
- role/scope mapping works
- CurrentActor/application identity boundary exists where needed
- Buyer ownership works
- Seller ownership works
- Agent ownership works
- admin override is explicit
- service-to-service Client Credentials works
- service identity is least-privilege
- 401/403 semantics are correct
- actuator/security defaults are hardened
- no secrets are committed
- security tests cover no-token/wrong-role/wrong-owner
- legacy custom JWT boundary is documented
- no Vault/mTLS/audit-pipeline/rate-limit scope leaks into Day 14
- docs match actual implementation