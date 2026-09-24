# Day 23 — Contract + Failure-Path Testing

## Goal
Cross-service contract ve sistematik negative-path güvenilirliğini doğrulamak.

## Tasks
1. critical REST consumer/provider contracts seç.
2. Spring Cloud Contract setup ekle.
3. provider verification ekle.
4. generated stub consumer tests ekle.
5. Kafka event schema compatibility tests ekle.
6. gRPC protobuf compatibility checks ekle.
7. DB unavailable test.
8. broker unavailable test.
9. downstream 503/timeout test.
10. poison message test.
11. DLT/DLQ replay-safety test.
12. invalid config startup test.
13. docs güncelle.

## Suggested commits
1. build(contract): add Spring Cloud Contract setup
2. test(contract): add critical REST contracts
3. test(contract): add provider and stub verification
4. test(contract): add event and protobuf compatibility tests
5. test(failure): add dependency outage scenarios
6. test(failure): add poison and replay tests
7. docs(test): document failure-path matrix

## Done
Critical contracts breaking change'e karşı korunur; major failure paths automated olarak test edilir.
