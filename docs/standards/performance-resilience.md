# Performance & Resilience Standard

Bu doküman projedeki performance, scalability ve resilience standartlarını tanımlar.

## 1. Ölçmeden optimize etme
- Önce metric, trace, profiler, query plan veya load test ile bottleneck doğrulanır.
- Premature optimization yapılmaz.

## 2. Latency Budget
Her synchronous request zinciri için toplam latency budget düşünülür.
Örnek:
Gateway -> BuyerService -> AgentService gRPC
Toplam timeout, alt çağrı timeout'larından daha büyük ve kontrollü olmalıdır.

## 3. Timeout
- Her remote call için explicit timeout.
- Infinite wait yok.
- Connect timeout ve read/request timeout ayrı değerlendirilebilir.

## 4. Timeout hierarchy
Downstream timeout upstream timeout'tan daha kısa olmalıdır.
Örnek:
Client 3s -> Gateway 2.5s -> Service 2s -> downstream 1s.
Exact değerler ölçüm sonrası belirlenir.

## 5. Retry
- Sadece transient failure için.
- Idempotent/safe operation'da.
- Retry count küçük ve bounded.
- Exponential backoff + jitter.

## 6. Retry amplification
Birden fazla layer aynı çağrıyı retry etmemelidir.
Gateway + service + client aynı anda retry ederse retry storm oluşabilir.

## 7. Circuit Breaker
Downstream sürekli hata veriyorsa fail-fast için kullanılır.
State:
- CLOSED
- OPEN
- HALF_OPEN

## 8. Circuit Breaker metric
- failure rate
- slow-call rate
- open state duration
gözlenir.

## 9. TimeLimiter
Async/remote operation için bounded execution time sağlar.

## 10. Bulkhead
Bir dependency failure'ının tüm thread/connection kaynağını tüketmesini engeller.
Thread pool veya semaphore isolation değerlendirilebilir.

## 11. RateLimiter
Abuse ve overload kontrolü için.
Candidate:
- login
- offer creation
- search/autocomplete
- expensive endpoint

## 12. Graceful Degradation
Dependency unavailable olduğunda her zaman 500 vermek zorunda değiliz.
Örnek:
- SearchService unavailable -> search özelliği degraded
- non-critical cache unavailable -> DB fallback

Ancak stale/partial response business olarak güvenli olmalıdır.

## 13. Fallback
Fallback gerçek business semantic taşımalıdır.
Dummy/sahte başarı response'u verilmez.

## 14. Backpressure
Producer hızı consumer kapasitesini aşarsa kontrol gerekir.
Kafka/RabbitMQ consumer concurrency, prefetch, pause/resume, queue depth/lag izlenir.

## 15. Load Shedding
Overload altında kontrollü request rejection yapılabilir.
429/503 kullanımı policy'ye göre.

## 16. Connection Pool
DB, HTTP ve gRPC connection pool sizing ölçülerek yapılır.
Pool'u aşırı büyütmek throughput garantisi değildir.

## 17. Thread Pool
Blocking workload için thread pool saturation izlenir.
CPU-bound ve IO-bound workload ayrımı yapılır.

## 18. Cache
Cache yalnızca ölçülen read bottleneck varsa.
Önce:
- cache key
- TTL
- invalidation
- stale tolerance
tasarlanır.

## 19. Cache-aside
Primary yaklaşım candidate:
read -> cache miss -> source of truth -> cache set.

## 20. Cache stampede
High traffic key'lerde jitter, single-flight/lock veya stale-while-revalidate değerlendirilebilir.

## 21. Hot Key
Redis/Cassandra/Kafka partition hotspot riskleri değerlendirilir.

## 22. Pagination
Unbounded query yok.
Deep pagination için datastore-specific yaklaşım.

## 23. Query Optimization
Relational:
- EXPLAIN/EXPLAIN ANALYZE
- index
- N+1

Elasticsearch:
- mapping
- filter context
- search_after

Cassandra:
- partition design
- ALLOW FILTERING yok

## 24. Payload Size
REST/gRPC/message payload küçük tutulur.
Gereksiz nested data taşınmaz.

## 25. Compression
Large network payload varsa HTTP/gRPC compression değerlendirilebilir.
CPU trade-off ölçülür.

## 26. Serialization
Serialization cost benchmark edilmeden format değiştirilmez.

## 27. Batch
Batching throughput artırabilir ancak latency ve memory trade-off'u vardır.

## 28. Async Processing
User'ın hemen sonuç beklemediği workload async yapılabilir.
Örnek listing processing / reindex task.

## 29. Eventual Consistency
Latency ve availability avantajı için kabul edilir; UI/API semantics açık olmalıdır.

## 30. Scalability
Stateless service horizontal scale için tercih edilir.
Session/local in-memory canonical state tutulmaz.

## 31. Horizontal vs Vertical
Önce bottleneck belirlenir.
Her problemi horizontal scale ile çözmeye çalışılmaz.

## 32. Capacity Planning
Candidate ölçüler:
- requests/sec
- messages/sec
- concurrent requests
- DB connections
- partition count
- storage growth

## 33. Load Test
Critical flow'lar:
- search
- create offer
- publish property
- messaging throughput

load test candidate'larıdır.

## 34. Load Test Metrics
- throughput
- p50/p95/p99 latency
- error rate
- saturation
- queue/lag

## 35. Stress Test
Breaking point ve degradation behavior gözlemlenir.

## 36. Soak Test
Long-running memory leak, pool leak ve gradual degradation için.

## 37. Spike Test
Ani traffic artışında behavior ölçülür.

## 38. Performance regression
Critical benchmark'lar CI/CD'de veya release öncesi karşılaştırılabilir.

## 39. Memory
- large object
- unbounded cache
- unbounded collection
- serialization buffer
riskleri izlenir.

## 40. GC
Java GC tuning default ilk adım değildir.
Önce allocation ve heap behavior ölçülür.

## 41. CPU
CPU profiling ile hot path doğrulanır.

## 42. Resilience4j
Day 14'te:
- Retry
- CircuitBreaker
- TimeLimiter
- Bulkhead
- RateLimiter
bilinçli policy ile uygulanacaktır.

## 43. Policy per dependency
Tek global resilience config yerine dependency/use-case bazlı policy tercih edilir.

## 44. Non-retryable errors
- 4xx business error
- validation
- authorization
- duplicate conflict
retry edilmez.

## 45. Retryable candidate
- timeout
- connection reset
- temporary 503
ancak operation safe ise.

## 46. Idempotency and resilience
Retry uygulanıyorsa write operation idempotency tasarımı zorunlu consideration.

## 47. Graceful shutdown
Service shutdown sırasında:
- yeni traffic alma durdurulur
- in-flight request/message mümkün olduğunca tamamlanır
- consumer controlled stop eder.

## 48. Readiness
Service hazır değilse traffic almamalıdır.

## 49. Startup dependency
Dependency gelmeden service sonsuz crash-loop'a girmemeli; retry/backoff policy düşünülür.

## 50. Resilience observability
Her policy için:
- retry count
- circuit state
- timeout count
- rate limit rejection
- bulkhead rejection
metric olmalıdır.

## 51. Failure isolation
Search failure property write'ı bozmamalı.
Cache failure canonical DB write'ı bozmamalı.
Non-critical observability backend failure business request'i fail etmemeli.

## 52. Critical dependency classification
Dependency:
- critical
- degradable
- optional
olarak sınıflandırılabilir.

## 53. SLA/SLO awareness
Performance target SLO ile ilişkilendirilir.
Exact hedefler benchmark sonrası.

## 54. Anti-Pattern'ler
- timeout'suz remote call
- unlimited retry
- nested retry storm
- fallback ile sahte başarı
- unbounded cache
- index ekleyip ölçmemek
- pool'u rastgele büyütmek
- her şeyi sync yapmak
- her şeyi async yapmak
- p95/p99 bakmadan average latency ile karar vermek
- load test olmadan capacity iddiası

## 55. Review Checklist
- Latency budget var mı?
- Her remote call timeout'lu mu?
- Retry safe/idempotent mi?
- Circuit Breaker gerekli mi?
- Bulkhead gerekli mi?
- Rate limit gerekli mi?
- Backpressure var mı?
- Cache invalidation belli mi?
- Query/index ölçülmüş mü?
- p95/p99 izleniyor mu?
- Failure degradation behavior belli mi?
- Resilience metrics var mı?