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
1. docs(traffic): load balancing ve abuse prevention sınırlarını tanımla
2. `docs(resilience): define dependency timeout and retry ownership`
3. `feat(buyer): add Circuit Breaker to Agent availability dependency`
4. `feat(resilience): add bounded retry and bulkhead policies`
5. `refactor(gateway): align edge resilience ownership`
6. `feat(gateway): add Redis-backed rate limiting`
7. `feat(resilience): translate resilience failures`
8. `test(resilience): verify circuit retry timeout and bulkhead behavior`
9. `test(rate-limit): verify distributed rate limiting`
10. `test(resilience): prevent nested retry storms`
11. `docs(resilience): finalize outage runbook and policies`
12. test(load-balancer): iki instance dağılımını ve kayıp instance davranışını doğrula
13. feat(gateway): mevcut abuse maliyet ve kimlik sınırlarını tamamla
14. test(abuse): limit isolation ve Redis outage politikasını doğrula
15. docs(traffic): algoritma ve test sonuçlarını kaydet

Küçük ve aynı sorumluluğa ait komşu commit’ler birleştirilebilir; karar, uygulama ve doğrulama ayrı incelenebilir kalır.

## Final gate
- retry ownership explicit
- nested retry storm yok
- timeout bounded
- seçilen yerlerde Circuit Breaker/Bulkhead çalışıyor
- TimeLimiter gereksiz yere duplicate edilmiyor
- rate limiting distributed ve route-sensitive
- fallback outage'ı success gibi gizlemiyor
- domain layer Resilience4j dependency taşımıyor

## Onaylanan system design ek kapsamı — Load balancing algoritmaları ve abuse prevention

Durum: **Planlandı**. Bu bölüm günün mevcut temel görevlerine eklenir; tamamlanmış implementation iddiası değildir. Ek görevler foundation kurulduktan sonra ve günün dokümantasyon/kapanış adımından önce uygulanır. Yukarıdaki commit sırası bu kapsamı içerir.

### Ek görevler ve çıktı belgeleri

1. Round-robin, random, weighted, least-connections ve consistent hashing yaklaşımlarını öğrenme karşılaştırması olarak değerlendir; mevcut Spring Cloud LoadBalancer’ın gerçekten desteklenen/etkin algoritmasını envanterle doğrula.
2. Mevcut idempotent read endpoint için kontrollü iki instance testinde çağrı dağılımını gözle; default algoritmayı ve discovery güncellenme davranışını belgeleyip algoritma kataloğunun tamamını implemente etme.
3. Instance kaybında retry/timeout/circuit ownership’i koruyan bounded behavior testi ekle; client-side service load balancing ile global/DNS/edge traffic management farklıdır. Global trafik/NGINX/Envoy kurulumunu sonraki platform fazına bırak.
4. Rate limiting’in abuse prevention’ın yalnız bir parçası olduğunu açıkla; current endpoint’lerde body/page/query maliyet sınırları, authenticated subject+route anahtarları ve hassas endpoint kısıtlarını hizala.
5. 429/Retry-After policy, farklı subject isolation, limit anahtarına client-controlled input/token sızıntısı ve Redis fail-open/fail-closed davranışını doğrula; yeni CAPTCHA/WAF/policy engine ekleme.
6. docs/architecture/load-balancing.md ve docs/security/abuse-prevention.md belgelerine testler ve uygulanan/yalnız karşılaştırılan kontrolleri kaydet.

### Ek kabul ölçütleri

- Etkin algoritma ve iki instance testi kayıtlı.
- Rate limiting abuse prevention’ın tamamı olarak sunulmuyor.
- Nested retry ve sınırsız fan-out yok; platform/global trafik kapsamı eklenmemiş.

Kapanışta ilgili service ROADMAP/DESIGN belgeleri ve Knowledge Base gerçek implementation/kanıtlarla güncellenir. Önce ilgili GitHub CI başarılı olur; ardından local runtime/API doğrulaması yapılır. Bir Day bir milestone’dır; kapsam gerektiğinde birden fazla takvim gününde tamamlanabilir.
