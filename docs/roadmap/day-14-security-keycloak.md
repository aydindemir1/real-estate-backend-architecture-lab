# Day 14 — Spring Security + OAuth2/OIDC + Keycloak

## Goal
Identity ve authorization modelini Keycloak tabanlı hale getirmek.

## Tasks
1. Keycloak local infrastructure ekle.
2. realm/client/role design'i netleştir.
3. Keycloak subject -> UserProfile -> role-specific profile identity mapping'i finalize et.
4. Gateway Resource Server/security config ekle.
5. downstream services token validation ekle.
6. role/scope mapping oluştur.
7. BUYER/SELLER/AGENT/ADMIN authorization rules ekle.
8. ownership check application boundary'de uygula.
9. service-to-service Client Credentials foundation ekle.
10. 401/403 error contract uygula.
11. actuator/security endpoint policy ekle.
12. security tests: no token, bad token, wrong role, wrong owner.
13. audit-sensitive operation hooks foundation ekle.
14. docs/ADR güncelle.

## Suggested commits
1. infra(security): add Keycloak local setup
2. docs(security): finalize identity and role model
3. feat(gateway): add OAuth2 resource server security
4. feat(security): add downstream token validation
5. feat(security): add role and scope authorization
6. feat(security): enforce ownership rules
7. feat(security): add service-to-service client credentials
8. test(security): add authentication and authorization tests
9. docs(security): finalize Keycloak integration

## Done
Authentication/authorization Gateway + downstream defense-in-depth ile çalışır; ownership test edilmiştir.

## Exact security/file plan

Implementation source of truth: `docs/roadmap/day-14-exact-file-plan.md`
