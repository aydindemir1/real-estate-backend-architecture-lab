# Day 24 — Exact Spring Cloud Vault Plan

## Scope
- secret inventory
- Config Server vs Vault ownership
- Vault local infrastructure
- per-service paths/policies
- secret migration
- fail-fast/redaction
- rotation and outage tests

## Tasks
1. Create `docs/security/secret-inventory.md`.
2. Create `docs/architecture/configuration-ownership.md`: defaults/repo, Config Server non-secret, Vault secret, Bus refresh-safe config.
3. Add Vault to Docker Compose with explicit version, healthcheck and local-only bootstrap token from env.
4. Update `.env.example` with Vault bootstrap variables; never commit a real token.
5. Define per-service KV paths under `secret/real-estate/{service}`.
6. Add least-privilege HCL policies; no wildcard access for all services.
7. Document local token auth vs production-like AppRole/platform identity.
8. Add `spring-cloud-starter-vault-config` only to consuming services.
9. Configure URI/auth/KV context/fail-fast using standard Spring Cloud Vault properties.
10. Migrate DB, broker and Keycloak client secrets into Vault; keep hosts/ports/topics in Config Server.
11. Verify missing critical secret fails startup predictably.
12. Verify Actuator/logging sanitizes secrets.
13. Demonstrate one secret rotation path.
14. Test Vault outage semantics after startup.
15. Document KV v2 version/rollback awareness.
16. Add `docs/runbooks/vault-outage.md` and `secret-rotation.md`.
17. Add static/config checks for committed secret values.

## Commit sequence
1. `docs(security): inventory application secrets`
2. `docs(config): define Config Server and Vault ownership`
3. `infra(vault): add local Vault service`
4. `feat(vault): add least-privilege service policies`
5. `build(vault): add Spring Cloud Vault dependencies`
6. `feat(secrets): migrate critical service secrets to Vault`
7. `feat(security): harden secret redaction`
8. `test(vault): verify secret loading rotation and outage`
9. `docs(vault): add Vault operational runbooks`

## Final gate
- secrets absent from committed config
- per-service least privilege
- missing critical secret fails fast
- secret values redacted
- one rotation path works
- Vault outage behavior tested
