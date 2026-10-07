# Day 33 — Kesin E2E + Recovery + Backend Completion Planı

## Kapsam
- critical business E2E
- security/service identity E2E
- outage/recovery exercise'ları
- controlled replay
- final Gradle/Compose verification
- backend completion report
- README/ROADMAP closeout

## Task'ler
1. Yalnızca gerekli infrastructure'ı içeren reproducible backend E2E Compose profile ekle.
2. Registration/Profile E2E: identity/account → UserProfile → role-specific profile mapping.
3. Seller Listing E2E: listing submission → durable pending command → RabbitMQ → Property DRAFT; duplicate safe.
4. Property→Search E2E: publish → Outbox → Kafka → Elasticsearch → REST search; opsiyonel GraphQL parity.
5. Offer Acceptance E2E end-to-end.
6. Offer Rejection E2E end-to-end.
7. Concurrent Offer E2E: tam olarak bir hold, double reservation yok.
8. Buyer/Seller/Agent için Security ownership E2E ve izin verilen yerde explicit Admin override.
9. Wrong scope → 403 olacak şekilde Service Client Credentials E2E.
10. Representative dependency-failure E2E: Agent unavailable, bounded timeout/circuit ve truthful error.
11. Kafka outage recovery: Outbox pending → broker restored → Search catches up.
12. RabbitMQ outage recovery: pending Seller outbound survives → broker restored → bir Property.
13. Day 31 full rebuild/alias switch kullanarak Elasticsearch loss recovery.
14. Vault outage runbook/tabletop check'i gerçek behavior'a karşı doğrula.
15. Bir Kafka DLT ve bir RabbitMQ DLQ controlled replay exercise yap.
16. Zaten yoksa `docs/runbooks/README.md` completeness index oluştur.
17. Test, architecture, integration, contract, failure ve E2E için exact Gradle command'larını içeren `docs/testing/final-verification.md` oluştur.
18. Final `clean test`, architecture, integration, contract, failure, E2E ve `check` çalıştır; hidden skipped failure olmasın.
19. `docker compose config` ve gerekli profile health validation çalıştır.
20. `docs/BACKEND-COMPLETION-REPORT.md` oluştur.
21. Root README'yi yalnızca completed capability'leri içerecek şekilde güncelle; Kubernetes/CI/CD future phase'i açıkça ayır.
22. Backend roadmap phase'i complete olarak işaretle.
23. Opsiyonel learning outcomes dokümanı.
24. Opsiyonel milestone tag yalnızca main merge + green verification sonrasında.

## Completion report bölümleri
- service/architecture matrix
- persistence technologies
- protocols
- messaging/reliability
- security
- resilience
- testing
- observability
- recovery/operations
- intentionally deferred work
- known limitations

## Final gate
- bütün critical E2E'ler green
- recovery exercise'ları green
- replay doğrulanmış
- final Gradle verification green
- Compose valid/healthy
- completion report mevcut
- README unimplemented claim içermiyor
- sonraki Docker/Kubernetes/Spring Cloud Kubernetes/Jenkins/SonarQube/Nexus/Harbor/Argo CD phase açıkça ayrılmış
