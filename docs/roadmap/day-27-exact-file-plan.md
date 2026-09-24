# Day 27 — Exact Contract Testing Plan

## Scope
- Spring Cloud Contract for selected REST boundaries
- provider verification
- generated stubs/consumer tests
- Kafka event compatibility fixtures
- gRPC protobuf compatibility

## Tasks
1. Create `docs/contracts/contract-test-inventory.md`.
2. Select only meaningful cross-service REST boundaries for Spring Cloud Contract.
3. Add plugin/dependencies only to selected providers.
4. Define standard contract directory organized by capability.
5. Add provider contracts for selected Agent/Property/other real service boundaries.
6. Generate provider verification tests against controller/application boundary.
7. Generate local stub artifacts.
8. Verify real REST consumers/OpenFeign clients against stubs where applicable.
9. Document REST backward compatibility: additive optional fields okay; rename/remove/type change breaking.
10. Inventory Kafka event schemas and versions.
11. Store previous JSON fixtures under `src/test/resources/events/v1`.
12. Test current code can deserialize supported previous fixtures.
13. Test unsupported schemaVersion takes safe non-retryable path.
14. Add protobuf compatibility checks: never reuse field numbers; reserve removed numbers; prefer additive fields.
15. Add Buyer↔Agent gRPC contract test for request/response/status mapping.
16. Protect stable critical API error contracts.
17. Add contract-test Gradle/CI-ready task/report.

## Commit sequence
1. `docs(contract): inventory critical service contracts`
2. `build(contract): add Spring Cloud Contract to selected providers`
3. `test(contract): add provider contracts and verification`
4. `build(contract): generate stub artifacts`
5. `test(contract): verify REST consumers against stubs`
6. `test(contract): add Kafka compatibility fixtures`
7. `test(contract): add protobuf compatibility checks`
8. `docs(contract): define backward-compatible contract evolution`

## Final gate
- contracts protect service boundaries, not internal classes
- selected providers verified
- consumer stubs work where relevant
- previous supported Kafka event fixture compatible
- protobuf evolution rules enforced
