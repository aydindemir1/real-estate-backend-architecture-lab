# Day 20 — Advanced Resilience

## Goal
Synchronous ve asynchronous dependency failure'larını bounded ve observable hale getirmek.

## Tasks
1. dependency-by-dependency timeout matrix çıkar.
2. retry ownership belirle.
3. Circuit Breaker configs ekle.
4. TimeLimiter ekle.
5. Bulkhead ekle.
6. Redis-backed RateLimiter ekle.
7. Gateway coarse policy'leri review et.
8. fallback semantics ekle.
9. retry storm önleme testleri yaz.
10. timeout/downstream 503 tests yaz.
11. bulkhead saturation test et.
12. rate limit tests yaz.
13. resilience metrics ekle.
14. docs güncelle.

## Suggested commits
1. docs(resilience): define timeout and retry ownership
2. feat(resilience): add dependency circuit breakers
3. feat(resilience): add time limiter and bulkheads
4. feat(gateway): add coarse edge resilience policies
5. feat(rate-limit): add Redis-backed rate limiting
6. test(resilience): add timeout and saturation tests
7. feat(observability): expose resilience metrics
8. docs(resilience): finalize failure policies

## Done
Timeout/retry/circuit/bulkhead/rate limit policies bounded, tested ve dependency-specific olur.
