# AgentService Roadmap

## Durum

Day 8 kapsamında AgentService için MySQL persistence ve Clean Architecture implementation tamamlandı.

- Implementation: Uygulandı
- Verification: Full build/test execution ayrıca doğrulanmalıdır.

## Day 8 — AgentService / MySQL / Clean Architecture

Uygulanan kapsam:
- MySQL JDBC
- Flyway schema migration
- `ddl-auto=validate`
- `open-in-view=false`
- Clean Architecture package boundaries
- Agent Aggregate + Value Objects
- CreateAgent
- GetAgent
- ChangeAvailability
- Stable REST error contract
- Optimistic locking
- MySQL Testcontainers
- Controller slice tests
- ArchUnit
- Smoke tests

## Sonraki AgentService milestone'ları

- Day 14: OAuth2 Resource Server / Keycloak / authorization
- Day 15: gRPC
- Sonraki roadmap günleri: resilience, observability ve ileri testing genişletmeleri

Detaylı tasarım: `docs/DESIGN.md`
