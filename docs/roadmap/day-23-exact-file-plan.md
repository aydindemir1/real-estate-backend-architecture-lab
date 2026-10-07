# Day 23 — Kesin Advanced Resilience Planı

## Kapsam
- dependency inventory + timeout budget
- retry ownership
- Circuit Breaker
- yalnızca non-redundant olduğu yerde TimeLimiter
- Bulkhead
- Redis-backed Gateway Rate Limiting
- truthful fallback/error semantics
- resilience testleri/metrics

## Task'ler

1. `docs/architecture/resilience-matrix.md` oluştur.
2. Her synchronous dependency için connect/read/total budget tanımla.
3. Her call path için tam olarak bir logical retry owner tanımla.
4. Resilience4j dependency'lerinin yalnızca ihtiyaç duyan module'lerde olduğunu doğrula.
5. Yararlı olduğu yerde Buyer→Agent gRPC outbound port'u Circuit Breaker ile decorate et.
6. gRPC deadline primary kalsın; TimeLimiter yalnızca duplicate olmayan ek değer sağlıyorsa ekle.
7. Mevcut Feign client'ları timeout/Circuit Breaker ihtiyacı açısından review et.
8. Retry yalnızca transient ve idempotent-safe call'larda olsun; nested retry yok.
9. Destekleniyorsa bounded backoff ve jitter ekle.
10. Semaphore/thread-pool Bulkhead yalnızca capacity isolation gerekçeli ise ekle.
11. Bulkhead limit'lerini HTTP/DB/gRPC pool capacity ile hizala.
12. Gateway edge Circuit Breaker/fallback ile service-specific ownership'i reconcile et.
13. Asla fake-success fallback döndürme.
14. Redis-backed Gateway rate-limit policy tanımla.
15. Authenticated subject + route/capability key resolver ekle; raw JWT kullanma.
16. 429 `RATE_LIMIT_EXCEEDED` ekle; pratikse Retry-After da ekle.
17. Route-specific Redis outage fail-open/fail-closed davranışını tanımla.
18. Resilience4j exception'larını stable technical/application error'lara çevir.
19. Circuit Breaker open/half-open recovery testleri ekle.
20. Retry-bound testleri ekle.
21. Timeout-budget testleri ekle.
22. Bulkhead saturation/isolation testleri ekle.
23. Redis rate-limit Testcontainers testleri ekle.
24. Nested retry-storm prevention testi ekle.
25. Low-cardinality metric hook'ları ekle.
26. Dependency-outage runbook ekle.
27. ArchUnit ekle: domain/controller'lar Resilience4j policy ownership taşımaz.

## Commit sırası
1. `docs(resilience): define dependency timeout and retry ownership`
2. `feat(buyer): add Circuit Breaker to Agent availability dependency`
3. `feat(resilience): add bounded retry and bulkhead policies`
4. `refactor(gateway): align edge resilience ownership`
5. `feat(gateway): add Redis-backed rate limiting`
6. `feat(resilience): translate resilience failures`
7. `test(resilience): verify circuit retry timeout and bulkhead behavior`
8. `test(rate-limit): verify distributed rate limiting`
9. `test(resilience): prevent nested retry storms`
10. `docs(resilience): finalize outage runbook and policies`

## Final gate
- retry ownership explicit
- nested retry storm yok
- timeout bounded
- seçilen yerlerde Circuit Breaker/Bulkhead çalışıyor
- TimeLimiter gereksiz yere duplicate edilmiyor
- rate limiting distributed ve route-sensitive
- fallback outage'ı success gibi gizlemiyor
- domain layer Resilience4j dependency taşımıyor
