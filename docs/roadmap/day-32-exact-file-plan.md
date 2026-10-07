# Day 32 — Kesin Architecture Fitness + Documentation Audit Planı

## Kapsam
- ArchUnit consolidation
- dependency/module drift
- build/config/contract/persistence drift
- ADR reconciliation
- API/gRPC/GraphQL/messaging/security/observability docs audit
- static/dependency/secret/privacy review
- Eureka vs Consul vs ZooKeeper karşılaştırması

## Task'ler
1. `docs/architecture/architecture-fitness-inventory.md` oluştur.
2. Service-specific Clean/Hexagonal/Onion/Vertical Slice/CQRS rule'larını flatten etmeden consolidate et.
3. Package/module cycle audit çalıştır; prohibited cross-service source dependency'lerini kaldır.
4. Root/common helper'ları accidental shared domain model ve global dependency bloat açısından audit et.
5. Kullanılmayan module-specific dependency'leri kaldır.
6. Service name, port, Config key, Vault path, topic/queue name ve resilience property name'lerini reconcile et.
7. REST docs ile implementation'ı reconcile et.
8. Proto ile gRPC adapter'ları reconcile et.
9. GraphQL schema ile resolver/security'yi reconcile et.
10. Kafka/RabbitMQ contract catalog ile actual message'ları reconcile et.
11. Persistence docs ile migration/table/document/index yapılarını reconcile et.
12. Tüm ADR status'larını audit et ve yalnızca gerçekten eksik decision'ları ekle.
13. Runbook coverage'ı audit et.
14. Authorization matrix/identity/Vault docs'u audit et.
15. Observability dashboard/metrics/alerts docs'u audit et.
16. Mevcut compiler/static analysis'i çalıştır; yalnızca high-value issue'ları düzelt.
17. Bilinen vulnerable direct dependency'leri review et; audit gününde riskli major upgrade yapma.
18. Repository'de gerçek password, token, client secret, JWT key ve broker credential ara.
19. Log/privacy'yi Authorization, secret veya excessive personal payload açısından review et.
20. Yeni runtime dependency eklemeden güncel Eureka vs Consul vs ZooKeeper karşılaştırması ve decision context ekle.
21. Final architecture checklist'i doğrula:
   - database per service
   - cross-service DB access yok
   - Redis non-canonical
   - Search derived
   - Property reservation concurrency'nin owner'ı
   - broker/framework API'leri domain dışında
22. Code-quality checklist'i doğrula: God Service yok, generic base abuse yok, DTO=Entity yok, empty catch yok, magic secret yok, controller/listener/mapper içinde business logic yok.

## Commit sırası
1. `docs(architecture): inventory architecture fitness rules`
2. `test(architecture): consolidate architecture suites`
3. `refactor(architecture): remove dependency cycles if needed`
4. `build: remove unused module dependencies`
5. `config: reconcile distributed configuration names`
6. `docs(contract): reconcile implemented contracts`
7. `docs(data): reconcile persistence models`
8. `docs(adr): reconcile architecture decisions`
9. `docs(security): reconcile security documentation`
10. `docs(observability): reconcile telemetry documentation`
11. `refactor: resolve high-value static findings`
12. `docs(architecture): add Eureka Consul ZooKeeper comparison`

## Final gate
- architecture testleri green
- prohibited module/package cycle yok
- docs implementation ile eşleşiyor
- ADR/runbook inventory güncel
- dependency/secret/privacy audit'leri geçiyor veya bilinen riskler dokümante edilmiş
- yeni feature scope eklenmemiş
