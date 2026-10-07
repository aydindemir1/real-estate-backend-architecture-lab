# Day 21 — Spring Cloud Bus + Vault

## Amaç
Config ve secret lifecycle'ını ayırmak ve kontrollü config propagation eklemek.

## Görevler
1. Vault local setup ekle.
2. secret taxonomy çıkar.
3. DB/broker/client secret'ları Vault'a taşı.
4. service Vault integration ekle.
5. fail-fast secret loading test et.
6. Config Server non-secret boundary'yi temizle.
7. Spring Cloud Bus dependency/config ekle.
8. refresh-safe properties listesi çıkar.
9. controlled refresh flow oluştur.
10. secret rotation test/candidate flow oluştur.
11. security/config tests yaz.
12. docs/runbook güncelle.

## Önerilen commit'ler
1. infra(vault): add local Vault setup
2. feat(secrets): migrate service secrets to Vault
3. refactor(config): keep Config Server non-secret only
4. feat(bus): add Spring Cloud Bus
5. feat(config): add controlled refresh policy
6. test(config): add secret and refresh tests
7. docs(config): add Vault/Bus runbooks

## Tamamlanma durumu
Secrets Vault'ta, normal config Config Server'da; refresh sadece güvenli property'lerde çalışır.

## Kesin Vault/Bus planı

Implementation için source of truth: `docs/roadmap/day-21-exact-file-plan.md`
