# Day 24 — Kesin Spring Cloud Vault Planı

## Kapsam
- secret inventory
- Config Server vs Vault ownership
- Vault local infrastructure
- per-service paths/policies
- secret migration
- fail-fast/redaction
- rotation ve outage testleri

## Task'ler
1. `docs/security/secret-inventory.md` oluştur.
2. `docs/architecture/configuration-ownership.md` oluştur: defaults/repo, Config Server non-secret, Vault secret, Bus refresh-safe config.
3. Docker Compose'a explicit version, healthcheck ve env üzerinden local-only bootstrap token ile Vault ekle.
4. `.env.example` dosyasını Vault bootstrap variable'larıyla güncelle; gerçek token asla commit etme.
5. `secret/real-estate/{service}` altında per-service KV path'leri tanımla.
6. Least-privilege HCL policy'leri ekle; bütün service'ler için wildcard access verme.
7. Local token auth ile production-like AppRole/platform identity farkını dokümante et.
8. `spring-cloud-starter-vault-config` yalnızca consuming service'lere ekle.
9. Standard Spring Cloud Vault property'leri kullanarak URI/auth/KV context/fail-fast configure et.
10. DB, broker ve Keycloak client secret'larını Vault'a taşı; host/port/topic'leri Config Server'da tut.
11. Eksik critical secret'ın startup'ı öngörülebilir biçimde fail ettiğini doğrula.
12. Actuator/logging'in secret'ları sanitize ettiğini doğrula.
13. Bir secret rotation path'i göster.
14. Startup sonrası Vault outage semantics'i test et.
15. KV v2 version/rollback awareness'ı dokümante et.
16. `docs/runbooks/vault-outage.md` ve `secret-rotation.md` ekle.
17. Commit edilmiş secret value'lar için static/config check ekle.

## Commit sırası
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
- committed config içinde secret yok
- per-service least privilege uygulanmış
- eksik critical secret fail-fast davranıyor
- secret değerleri redacted
- en az bir rotation path çalışıyor
- Vault outage behavior test edilmiş
