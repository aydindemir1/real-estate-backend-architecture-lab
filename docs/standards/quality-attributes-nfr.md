# Quality Attributes & Non-Functional Requirements

Bu doküman sistemin kalite özelliklerini ve ölçülebilir Non-Functional Requirement'larını tanımlar.

## 1. Amaç
Functional requirement sistemin ne yaptığını; quality attribute sistemin bunu ne kadar iyi, güvenli, hızlı, dayanıklı ve işletilebilir yaptığını tanımlar.

Bu projede NFR'ler implementation sonuna bırakılmaz.

## 2. Scenario Template
Her quality attribute gerektiğinde şu formatla düşünülür:
- Source
- Stimulus
- Environment
- Artifact
- Response
- Response Measure

## 3. Baseline Measurement
Kesin sayısal SLO'lar benchmark/load test olmadan uydurulmaz.

Önce baseline ölçülür, sonra target belirlenir.

## 4. Priority Classes
Quality attribute önem seviyesi:
- Critical
- High
- Medium
- Contextual

Her service aynı attribute'u aynı seviyede taşımaz.

## 5. Availability
Tanım:
Sistemin istenen zamanda usable olması.

Critical candidate:
- Auth/identity
- Property write
- Offer workflow

Search capability degradable olabilir.

## 6. Availability Scenario
Stimulus:
SearchService unavailable.

Response:
Property write flow çalışmaya devam eder; search endpoint degraded/503 olabilir.

Measure:
Search failure canonical Property write availability'sini etkilemez.

## 7. Dependency Classification
Dependency:
- critical
- degradable
- optional

olarak sınıflandırılır.

## 8. Reliability
Correct operation over time.

Messaging duplicate delivery, retry ve transient failure altında correctness korunmalıdır.

## 9. Reliability Scenario
Kafka aynı OfferRequested event'ini iki kez teslim eder.

Response:
Property yalnızca bir kez hold edilir veya ikinci işlem idempotent no-op/conflict olur.

Measure:
Duplicate business side-effect = 0.

## 10. Latency
Primary ölçüm:
- p50
- p95
- p99

Average latency tek başına yeterli değildir.

## 11. API Latency Scenario
Stimulus:
Normal load altında GET property/search request.

Response:
Bounded latency.

Measure:
Target değer Day 18/benchmark sonrası belirlenir.

## 12. Internal Call Latency
gRPC/Feign call kendi timeout budget'ına sahip olmalıdır.

## 13. Tail Latency
p99 spike dependency saturation veya queueing göstergesi olabilir.

## 14. Throughput
Ölçü:
- requests/sec
- messages/sec
- successful transactions/sec

## 15. Search Throughput
Search read workload write workload'dan bağımsız scale edebilmelidir.

## 16. Scalability
Stateless services horizontal scale edilebilir olmalıdır.

Canonical state local memory'ye bağlanmaz.

## 17. Scalability Scenario
Search traffic 5x artar.

Response:
SearchService instance/consumer capacity artırılabilir.

Property write model etkilenmemelidir.

## 18. Elasticity
Automatic scaling backend sonrası Kubernetes fazında değerlendirilecektir.

## 19. Consistency
Consistency modeli use-case bazında tanımlanır.

Strong/local consistency:
- Property state transition
- uniqueness
- optimistic concurrency

Eventual consistency:
- Property -> Search projection
- Saga local state synchronization
- Seller projections

## 20. Consistency Scenario
PropertyPublished event sonrası Elasticsearch henüz güncel değil.

Response:
Search kısa süre stale olabilir.

Measure:
Projection freshness ölçülür.

## 21. Data Freshness
Search Projection için first-class NFR.

Metric:
`projection_updated_at - event_occurred_at`

Exact target baseline sonrası.

## 22. Durability
Canonical datastore committed business state kaybolmamalıdır normal single-node/process restart'ta.

Lab environment production multi-AZ durability iddiası yapmaz.

## 23. Messaging Durability
Critical message broker persistence/config bilinçli olmalıdır.

## 24. Security
Security quality attribute ayrı standard ile enforce edilir.

Core NFR:
- authentication
- authorization
- ownership
- confidentiality
- integrity
- auditability

## 25. Confidentiality
Secret/password/token plain/log exposure = 0 hedef.

## 26. Integrity
Unauthorized state mutation engellenmelidir.

## 27. Auditability
Critical business/security action actor + resource + outcome ile izlenebilir.

## 28. Maintainability
Change cost düşük ve localized olmalıdır.

## 29. Maintainability Scenario
Agent persistence MySQL adapter değişir.

Response:
Domain/application code minimum etkilenir.

Measure:
Architecture boundary breach olmadan adapter-level change.

## 30. Modifiability
Yeni search filter eklemek canonical Property domain'i gereksiz değiştirmemelidir.

## 31. Testability
Domain business rule Spring context olmadan test edilebilir.

External dependency port/adapter üzerinden replace edilebilir.

## 32. Testability Measure
Critical domain test'ler infrastructure boot gerektirmez.

## 33. Observability
System internal state telemetry üzerinden anlaşılabilir olmalıdır.

## 34. Observability Scenario
Offer reservation flow başarısız.

Response:
traceId/correlationId ile request -> event -> consumer -> datastore chain bulunabilir.

## 35. Diagnosability
Mean time to understand failure azaltılmalıdır.

Exact MTTR target production environment olmadığı için numeric verilmez.

## 36. Recoverability
Failure sonrası correct state'e dönme kapasitesi.

## 37. Recovery Scenario — Search
Elasticsearch index corrupt/delete.

Response:
Mongo source-of-truth'tan reindex veya event replay.

Measure:
Canonical data loss = 0.

## 38. Recovery Scenario — DLQ
Poison/transiently failed message DLQ/DLT'de.

Response:
Root cause fix sonrası controlled replay.

## 39. Backup Awareness
Canonical vs reconstructable vs disposable data ayrımı:
- canonical DB -> backup concern
- Elasticsearch projection -> reconstructable
- Redis cache -> disposable

## 40. RPO
Recovery Point Objective production commitment değildir.

Lab'de concept olarak canonical store bazında belgelenir.

## 41. RTO
Recovery Time Objective numeric commitment değildir.

Runbook/recovery path ile düşünülür.

## 42. Deployability
Artifact environment-independent olmalıdır.

Config/secret runtime'da.

## 43. Deployability Scenario
Aynı artifact dev ve prod-like environment'a deploy edilir.

Response:
Code rebuild gerekmeden config değişir.

## 44. Rollbackability
Deployment/migration failure sonrası rollback veya forward-fix strategy bulunmalıdır.

## 45. Backward Compatibility
Rolling deployment sırasında API/event/schema eski-yeni version overlap'ini mümkün olduğunca desteklemelidir.

## 46. Database Migration NFR
Migration startup/deployment'ı uzun süre bloklamamalı.

Destructive change aşamalı.

## 47. Interoperability
REST, gRPC, Kafka contract'ları explicit ve versionable.

## 48. Portability
Domain/application framework/storage coupling minimize edilir.

Ancak full vendor-neutral abstraction uğruna lowest-common-denominator design yapılmaz.

## 49. Resource Efficiency
CPU, memory, connection, thread ve storage kaynakları bounded olmalıdır.

## 50. Memory NFR
Unbounded in-memory collection/cache yasak.

## 51. Connection NFR
Pool saturation observable.

## 52. Queue NFR
Queue depth/consumer lag bounded operation için monitored.

## 53. Capacity
Capacity claim load test olmadan yapılmaz.

## 54. Concurrency Correctness
Concurrent Offer requests aynı Property için invariant'ı bozmamalıdır.

## 55. Concurrency Scenario
İki buyer aynı PUBLISHED Property'ye eşzamanlı OfferRequested gönderir.

Response:
Property sadece bir active hold kabul eder.

Measure:
At most one successful hold.

## 56. Idempotency
Retry/duplicate altında duplicate resource/state mutation oluşmamalıdır.

## 57. Idempotency Scenario
Aynı Idempotency-Key ile aynı create offer request tekrar gelir.

Response:
Aynı logical result.

## 58. Conflict Scenario
Aynı Idempotency-Key farklı payload.

Response:
409 IDEMPOTENCY_CONFLICT.

## 59. Resilience
Partial dependency failure tüm sistemi çökertmemelidir.

## 60. Resilience Scenario
AgentService timeout.

Response:
Viewing availability request bounded timeout ile fail/degrade olur; BuyerService thread pool tükenmez.

## 61. Graceful Degradation
Degraded response yalnızca correctness bozulmuyorsa.

## 62. Fault Isolation
Search, cache, observability backend gibi non-critical dependency failure canonical write flow'u etkilememelidir.

## 63. Fault Containment
Bulkhead/queue/client pool ile failure blast radius sınırlandırılabilir.

## 64. Operability
Operator/developer service'i anlayabilmeli ve yönetebilmelidir.

## 65. Operability Requirements
- health
- readiness
- metrics
- logs
- traces
- version metadata
- runbook

## 66. Supportability
Local troubleshooting documented olmalıdır.

## 67. Simplicity
Complexity quality attribute'tur.

Technology/pattern yalnızca gerçek problem için eklenir.

## 68. Learnability
Educational project olduğu için Architecture intent dokümanda açık olmalıdır.

## 69. Readability
Code review olmadan da package/use-case responsibility anlaşılır olmalıdır.

## 70. Change Isolation
Bir datastore adapter change'i cross-service code cascade yaratmamalıdır.

## 71. Data Ownership
Cross-service DB write = 0.

## 72. API Stability
Stable error code ve versioning policy.

## 73. Event Stability
Backward-compatible schema evolution.

## 74. Availability vs Consistency Trade-off
Search için availability/eventual consistency tercih edilebilir.

Property reservation için correctness/consistency öncelikli.

## 75. Latency vs Consistency Trade-off
Cross-service synchronous strong consistency yerine Saga/eventual consistency.

## 76. Performance vs Maintainability
Premature micro-optimization ile code readability bozulmaz.

## 77. Security vs Usability
Security control business flow'u gereksiz karmaşıklaştırmamalı ancak secure default korunur.

## 78. Cost Awareness
Lab'de cloud cost primary değil fakat resource-heavy stack bilinçli.

## 79. Technology Resource Budget
Cassandra/Couchbase/Elastic/Kafka gibi ağır service'ler local environment kapasitesi göz önünde bulundurularak compose profile veya selective startup ile çalıştırılabilir.

## 80. Local Developer Experience
Yeni developer repo'yu documented steps ile ayağa kaldırabilmelidir.

## 81. Startup Time
Critical productivity metric olabilir.

Exact target yok; unnecessary initialization azaltılır.

## 82. Build Time
Build performance monitored olabilir.

## 83. CI Feedback Time
Fast unit/architecture test stage önce.

Heavy integration/E2E stage ayrılabilir.

## 84. Failure Detection Time
Health/metric/alert ile failure görünür olmalıdır.

## 85. Alertability
Actionable operational symptom ölçülebilir olmalı.

## 86. Data Retention
Canonical, event, audit, dedup/idempotency ve telemetry retention ayrı policy.

## 87. Compliance Awareness
Gerçek kişisel veri/regulated production system değil; yine de least-data ve sensitive logging prensibi uygulanır.

## 88. Accessibility
Backend API context'inde primary değil.

API error/readability/documentation developer accessibility sağlar.

## 89. Internationalization
Business requirement yoksa scope dışı.

Timestamp/currency/location model future-compatible tutulur.

## 90. Quality Attribute Matrix
| Area | Priority | Main Mechanism | Verification |
|---|---|---|---|
| Property correctness | Critical | Aggregate + optimistic locking | concurrency/integration tests |
| Offer idempotency | Critical | Idempotency key + consumer dedup | duplicate tests |
| Security ownership | Critical | RBAC + ownership | security tests |
| Search freshness | High | Kafka projection | freshness metric |
| API latency | High | timeout/query/index | load/metrics |
| Messaging reliability | Critical | Inbox/Outbox/DLQ | failure tests |
| Maintainability | High | architecture boundaries | ArchUnit/review |
| Observability | High | OTel/metrics/logs | integration/ops checks |
| Recoverability | High | reindex/replay/runbook | recovery exercise |
| Deployability | High | external config/migrations | CI/deployment tests |

## 91. Per-Day NFR Gate
Her Day planında ilgili quality attributes seçilir.

Her Day bütün NFR'leri aynı ağırlıkta uygulamak zorunda değildir.

## 92. Definition of Ready NFR
- Critical quality attribute belli mi?
- Failure scenario tanımlı mı?
- Measurement/verification yöntemi belli mi?
- Trade-off biliniyor mu?

## 93. Definition of Done NFR
- Selected NFR test/metric ile doğrulandı mı?
- Failure path var mı?
- Telemetry var mı?
- Regression riski belgeli mi?

## 94. Numeric Targets
Sayısal target koyulacaksa:
- baseline
- environment
- load profile
- percentile
- test duration

ile birlikte yazılır.

## 95. Invalid Numeric Target
`API 100ms altında olmalı` gibi environment/load context'siz hedef kabul edilmez.

## 96. Performance Test Environment
Local developer laptop sonucu production SLO değildir.

## 97. NFR Ownership
Quality attribute yalnızca DevOps responsibility değildir.

Architecture + application + infrastructure birlikte etkiler.

## 98. NFR Anti-Pattern'leri
- SLO sayısı uydurmak
- availability = health endpoint sanmak
- average latency ile yetinmek
- eventual consistency'yi belirsiz bırakmak
- backup ile recovery'yi aynı şey sanmak
- security'yi yalnız auth olarak görmek
- maintainability'yi ölçülemez bırakmak
- NFR'leri projenin sonuna bırakmak

## 99. Architecture Review Questions
- Bu flow için en kritik quality attribute hangisi?
- Hangi failure onu tehdit ediyor?
- Trade-off nedir?
- Nasıl ölçeceğiz?
- Nasıl test edeceğiz?
- Hangi telemetry gösterecek?

## 100. Quality Attribute Review Checklist
- Availability expectation açık mı?
- Consistency modeli açık mı?
- p95/p99 düşünülmüş mü?
- Throughput/capacity iddiası ölçülü mü?
- Concurrency invariant var mı?
- Idempotency gerekli mi?
- Security ownership doğrulanıyor mu?
- Search/data freshness ölçülebilir mi?
- Failure isolation var mı?
- Recovery path var mı?
- Artifact deployable mı?
- Maintainability/testability architecture ile korunuyor mu?
- Numeric target varsa baseline/context var mı?