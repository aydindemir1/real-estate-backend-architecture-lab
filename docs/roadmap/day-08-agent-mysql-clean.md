# Day 8 — AgentService: MySQL + Clean Architecture

## Goal
AgentService'i MySQL ve Clean Architecture ile gerçek bir vertical path üzerinden kurmak.

## Tasks
1. AgentService build'de PostgreSQL/JPA driver cleanup yap.
2. MySQL dependency ve migration dependency ekle.
3. Agent package boundaries oluştur.
4. AgentId, UserId, LicenseNumber, AgencyInfo, AgentStatus, AvailabilityStatus oluştur.
5. Agent Aggregate oluştur.
6. Domain invariants ekle.
7. AgentRepository domain abstraction oluştur.
8. CreateAgentUseCase, GetAgentUseCase, ChangeAvailabilityUseCase oluştur.
9. Commands/Queries/Results oluştur.
10. AgentApplicationService oluştur.
11. AgentJpaEntity oluştur.
12. SpringDataAgentRepository oluştur.
13. Persistence mapper oluştur.
14. AgentRepositoryAdapter oluştur.
15. MySQL migration script oluştur.
16. MySQL config'i typed/config-server uyumlu hale getir.
17. REST request/response DTO'ları oluştur.
18. AgentController oluştur.
19. Global error model ile mapping yap.
20. Domain unit tests yaz.
21. Application tests yaz.
22. MySQL Testcontainers integration tests yaz.
23. ArchUnit Clean Architecture testleri yaz.
24. README/DESIGN/ROADMAP güncelle.

## Suggested commits
1. build(agent): switch persistence dependencies to MySQL
2. refactor(agent): establish Clean Architecture packages
3. feat(agent): add Agent domain model and invariants
4. feat(agent): add application use cases
5. feat(agent): add MySQL persistence adapter
6. db(agent): add initial schema migration
7. feat(agent): expose minimal REST API
8. test(agent): add domain and application tests
9. test(agent): add MySQL Testcontainers tests
10. test(agent): add Clean Architecture fitness rules
11. docs(agent): finalize Day 8 design

## Verification
- Create agent
- Load agent
- Change availability
- duplicate license rejected
- suspended/inactive cannot become AVAILABLE
- no domain -> infrastructure dependency

## Done
AgentService MySQL üzerinde çalışır, Clean Architecture dependency direction korunur ve unit/integration/architecture tests green olur.

## Exact file/class plan

Implementation source of truth: `docs/roadmap/day-08-exact-file-plan.md`
