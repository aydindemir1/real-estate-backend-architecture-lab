# Day 15 — REST + gRPC + GraphQL

## Amaç
Protocol'leri gerçek use-case'lere göre ayırmak.

## Görevler
1. AgentAvailability gRPC proto contract finalize et.
2. proto build/generation setup ekle.
3. AgentService gRPC server adapter oluştur.
4. BuyerService AgentAvailability outbound port adapter oluştur.
5. deadline/timeout ekle.
6. gRPC auth/context propagation ekle.
7. gRPC error mapping test et.
8. GraphQL read use-case seç.
9. SearchService GraphQL schema/resolver ekle.
10. N+1/depth/complexity policy ekle.
11. REST baseline'ın primary public API olarak kaldığını doğrula.
12. protocol integration tests yaz.
13. docs güncelle.

## Önerilen commit'ler
1. build(grpc): add protobuf and gRPC build support
2. feat(agent): expose availability gRPC service
3. feat(buyer): add AgentAvailability gRPC adapter
4. test(grpc): add timeout and contract tests
5. feat(search): add GraphQL read schema
6. test(graphql): add query/security tests
7. docs: document protocol boundaries

## Tamamlanma durumu
REST, gRPC ve GraphQL ayrı ve gerekçeli responsibilities ile çalışır.

## Kesin protocol/file planı

Implementation için source of truth: `docs/roadmap/day-15-exact-file-plan.md`
