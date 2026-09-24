# Operational Readiness & Runbook Standard

Bu doküman servislerin yalnızca geliştirilmiş değil, işletilebilir ve arıza durumunda teşhis/iyileştirme yapılabilir olması için operational readiness standard'ını tanımlar.

## 1. Operational Readiness amacı
Bir service production-benzeri kabul edilmeden önce:
- nasıl başlatılır
- nasıl durdurulur
- sağlıklı olduğu nasıl anlaşılır
- dependency failure'da nasıl davranır
- data/messaging nasıl recover edilir
- incident nasıl diagnose edilir
bilinmelidir.

## 2. Runbook principle
Runbook teori değil, action-oriented olmalıdır.

Her runbook:
- symptom
- likely causes
- checks
- recovery steps
- verification
- escalation/follow-up
içermelidir.

## 3. Service startup
Startup sırası implicit assumption olmamalıdır.

Service gerekli dependency unavailable ise:
- fail-fast
- bounded retry/backoff
- degraded startup
yaklaşımlarından hangisini kullandığı açık olmalıdır.

## 4. Critical dependency startup
DB/identity/config gibi critical dependency yoksa çoğu service fail-fast veya bounded retry ile başlamalıdır.

## 5. Optional dependency startup
Observability backend veya cache gibi non-critical dependency unavailable ise business service mümkünse ayağa kalkabilmelidir.

## 6. Startup timeout
Dependency bekleme sonsuz olmamalıdır.

## 7. Startup log
Startup sonunda minimum operational metadata:
- service name
- version
- environment
- active profile
- listening port
- critical integration readiness

görülebilir olmalıdır.

## 8. Graceful shutdown
Shutdown sırasında:
- readiness false
- yeni traffic alma durur
- in-flight request'ler bounded süre tamamlanır
- consumer'lar controlled stop eder
- resource'lar kapanır

## 9. Shutdown timeout
Grace period explicit olmalıdır.

## 10. Messaging shutdown
Consumer shutdown sırasında in-flight message:
- başarıyla tamamlanmalı
- veya broker semantics'e uygun redelivery olmalı.

## 11. Liveness
Liveness process'in çalışıp çalışmadığını gösterir.

Dependency down diye process'i otomatik restart ettiren yanlış liveness tasarlanmaz.

## 12. Readiness
Readiness service'in traffic almaya uygunluğunu gösterir.

Critical dependency state readiness'e dahil edilebilir.

## 13. Health endpoint security
Health endpoint minimum bilgi expose eder.

Sensitive dependency detail public değildir.

## 14. Health semantics
UP/DOWN tek başına troubleshooting için yeterli değildir; metrics/log/trace ile desteklenir.

## 15. Config Server outage
Symptom:
Service startup config alamıyor veya refresh başarısız.

Checks:
- Config Server health
- network/discovery
- repo availability
- requested application/profile/label

Recovery:
- restore Config Server/repo
- validate config
- controlled restart/refresh

## 16. Vault outage
Secret alınamıyorsa critical client/service fail-fast olabilir.

Existing lease/cache behavior Vault capability'ye göre değerlendirilir.

## 17. Eureka outage
Existing registered instance behavior ve new discovery failure ayrı düşünülür.

Runbook:
- registry health
- client registration
- stale instance
- fallback DNS/static config sadece explicit design varsa

## 18. Gateway outage
Gateway critical edge component'tir.

Checks:
- routes
- discovery
- auth
- rate limit
- downstream health

## 19. Downstream service outage
Symptoms:
- timeout
- 503
- circuit open

Checks:
- dependency health
- error rate
- p95/p99
- circuit state
- connection pool

## 20. Database outage
Runbook:
- connectivity
- credentials
- pool saturation
- datastore process health
- disk/resource state
- migration state

Recovery sonrası write/read verification yapılır.

## 21. Connection pool exhaustion
Symptoms:
- timeout
- increasing latency
- active connections max

Checks:
- slow query
- long transaction
- connection leak
- traffic spike

Pool'u kör büyütmek recovery değildir.

## 22. Migration failure
Migration failure startup/deployment blocker olabilir.

Runbook:
- failed migration version
- checksum/history
- DB partial state
- compatibility
- backup/restore need
- forward-fix vs rollback

## 23. Forward-fix preference
Production-benzeri schema migration'da destructive rollback her zaman güvenli değildir.

Forward-fix çoğu durumda tercih edilebilir.

## 24. Migration lock
Flyway/Liquibase lock/history behavior bilinmelidir.

## 25. Mongo concurrency incident
Optimistic locking conflict normal concurrency olabilir.

Retry sadece use-case safe ise.

## 26. Cassandra incident
Checks:
- node availability
- consistency level
- timeout
- partition hotspot
- tombstone/large partition

## 27. Couchbase incident
Checks:
- cluster/node health
- bucket/scope/collection
- index availability
- CAS conflict
- query timeout

## 28. Elasticsearch incident
Checks:
- cluster health
- index existence
- mapping
- shard allocation
- disk watermark
- query latency

## 29. Search projection stale
Symptom:
Mongo Property güncel ama Search sonucu eski.

Checks:
- Kafka event produced?
- consumer lag
- consumer failure
- DLT
- projection timestamp

Recovery:
- replay relevant event
- reindex single property
- reconcile/rebuild index

## 30. Redis outage
Cache/idempotency/rate limit impact ayrı değerlendirilir.

Canonical state Redis'e bağlı değildir.

## 31. Cache outage
Safe fallback varsa source-of-truth DB.

Cache miss storm riski değerlendirilir.

## 32. Idempotency store outage
Critical write idempotency garanti edilemiyorsa fail-open vs fail-closed explicit policy gerekir.

Create Offer gibi kritik write için fail-closed daha güvenli olabilir.

## 33. Rate limit store outage
Security/availability trade-off.

Fail-open/fail-closed endpoint sensitivity'ye göre.

## 34. RabbitMQ outage
Checks:
- broker process
- exchange/queue existence
- connection/channel
- publisher confirm
- queue depth

## 35. RabbitMQ backlog
Symptoms:
- queue depth increasing
- unacked high

Checks:
- consumer alive
- processing latency
- downstream dependency
- prefetch/concurrency

## 36. RabbitMQ DLQ
DLQ growth alertable olmalıdır.

Replay öncesi root cause düzeltilir.

## 37. DLQ replay
Controlled procedure:
1. sample message inspect
2. classify cause
3. fix producer/consumer/data
4. validate idempotency
5. replay limited batch
6. monitor

## 38. Kafka outage
Checks:
- broker availability
- topic metadata
- producer errors
- consumer connectivity
- ISR/partition state learning environment'e göre

## 39. Kafka consumer lag
Symptoms:
- lag increasing
- freshness degradation

Checks:
- processing time
- consumer count
- partition count
- downstream bottleneck
- poison/retry loop

## 40. Kafka DLT
DLT message replay root cause çözülmeden yapılmaz.

## 41. Duplicate event incident
Idempotent consumer duplicate side-effect üretmemelidir.

Duplicate rate metric/log ile incelenir.

## 42. Outbox backlog
Outbox records unpublished büyüyorsa:
- publisher health
- broker connectivity
- retry status
- stuck record

kontrol edilir.

## 43. Inbox growth
Retention cleanup ve index/partition strategy izlenir.

## 44. Saga stuck
Offer flow intermediate state'te kalabilir.

Checks:
- last event
- correlationId
- expected next consumer
- DLT/DLQ
- local aggregate state

## 45. Saga recovery
Blind manual DB update yapılmaz.

Controlled compensating action/replay/reconciliation kullanılır.

## 46. Property hold stuck
ON_HOLD state expiration/reconciliation strategy Day 13/19 scope'unda netleştirilir.

## 47. Reconciliation job
Canonical state ile projection/local derived state karşılaştırır.

Idempotent ve rerunnable olmalıdır.

## 48. Reindex
Single property, partial veya full rebuild modları düşünülebilir.

## 49. Full Elasticsearch rebuild
Procedure:
- create new index
- load/replay
- verify count/sample/freshness
- alias switch
- retain old index temporarily
- cleanup

## 50. Replay safety
Replay öncesi:
- idempotency
- ordering
- side-effect
- external notification
riski değerlendirilir.

## 51. Incident severity
Lab için formal enterprise scheme şart değil, fakat:
- critical business flow down
- partial degradation
- non-critical issue
ayrımı kullanılabilir.

## 52. Incident triage
İlk sorular:
- ne bozuldu?
- ne zaman başladı?
- hangi service/version?
- blast radius?
- recent deploy/config change?
- error/latency/lag?

## 53. Evidence preservation
Incident sırasında log/trace/metric context kaybedilmemelidir.

## 54. Correlation-first debugging
Known correlationId/traceId varsa ilk giriş noktasıdır.

## 55. Golden signals triage
- latency
- traffic
- errors
- saturation

## 56. Change correlation
Deployment/config/migration timestamp telemetry ile karşılaştırılır.

## 57. Rollback
Application artifact rollback yalnız schema/event/API backward-compatible ise güvenlidir.

## 58. Rollback decision
Kontrol:
- DB migration backward-compatible mi?
- event schema compatibility?
- config compatibility?
- external contract?

## 59. Forward-fix
Rollback riskli ise minimal corrective change tercih edilebilir.

## 60. Feature disable
Feature toggle ancak önceden tasarlanmışsa emergency mitigation olabilir.

## 61. Manual data correction
Son çare.

Audit edilir, script/version-controlled ve reversible/verified olmalıdır.

## 62. Backup classification
Canonical store backup priority.

Projection/cache yeniden üretilebilir.

## 63. Backup test
Backup var demek recovery var demek değildir.

Restore procedure test edilmelidir production-benzeri ortamda mümkünse.

## 64. RPO/RTO awareness
Numeric commitment yoksa bile hangi data kaybının kabul edilemez olduğu bilinmelidir.

## 65. Data corruption
Suspected corruption halinde writes durdurma/isolating gerekebilir.

Blind repair yapılmaz.

## 66. Security incident
Candidate:
- leaked secret
- suspicious auth failure
- unauthorized access

Actions:
- contain
- rotate credential
- inspect audit
- invalidate token/client
- document impact

## 67. Secret rotation
Keycloak client secret/broker/db credential rotation procedure olmalıdır.

## 68. Certificate expiry
TLS/mTLS ileride kullanılırsa expiry monitoring/runbook gerekir.

## 69. Rate limit incident
Unexpected 429:
- config
- Redis
- key strategy
- traffic spike

incelenir.

## 70. Memory pressure
Checks:
- heap
- GC
- unbounded collection/cache
- payload size

## 71. CPU saturation
Checks:
- hot method
- serialization
- tight loop
- excessive logging
- traffic

## 72. Thread saturation
Checks:
- blocked threads
- downstream waits
- executor size
- virtual/platform thread behavior

## 73. Disk pressure
Relevant:
- Elasticsearch
- Kafka
- DB
- logs

## 74. Log explosion
DEBUG/full payload logging incident olabilir.

Runtime level change controlled.

## 75. Telemetry backend outage
Loki/Tempo/Prometheus outage business flow'u fail etmemelidir.

Telemetry exporter backpressure resource exhaustion yaratmamalıdır.

## 76. Alert design
Runbook linki olan actionable alert tercih edilir.

## 77. Alert content
Minimum:
- service
- symptom
- environment
- threshold/context
- runbook

## 78. Local developer runbook
Common:
- port occupied
- container not running
- DB auth failure
- stale Docker volume
- migration mismatch
- service discovery missing
- config not found

## 79. Docker Compose troubleshooting
Checks:
- docker ps
- container logs
- health
- network
- port mappings
- volume

## 80. Clean reset
Destructive local reset command clearly marked.

Production runbook ile karıştırılmaz.

## 81. Test environment troubleshooting
Testcontainers failure:
- Docker availability
- image pull
- port/resource
- container logs

## 82. CI failure runbook
Classify:
- deterministic test regression
- flaky test
- infrastructure failure
- dependency repository failure
- container startup failure

## 83. Flaky test
Rerun ile green olması çözüm değildir.

Root cause fix.

## 84. Dependency repository outage
Nexus phase sonrası proxy/cache avantajı.

## 85. Build failure after upgrade
Gradle/JDK/Spring upgrade separately bisectable olmalıdır.

## 86. Version metadata
Running service version/commit observable.

## 87. Configuration snapshot
Incident sırasında effective config'in sensitive olmayan subset'i anlaşılabilmelidir.

## 88. Runbook ownership
Runbook service DESIGN/ops docs ile birlikte güncellenir.

## 89. Runbook versioning
Code ile aynı repository'de version-controlled.

## 90. Runbook test
Critical runbook en az bir kez dry-run/tabletop veya integration exercise ile doğrulanabilir.

## 91. Game Day candidate
İleri aşama:
- kill SearchService
- stop Kafka consumer
- Redis unavailable
- DB timeout

gibi controlled failure exercise.

## 92. Operational Definition of Ready
- health semantics defined
- dependencies classified
- failure mode known
- recovery path documented
- telemetry planned

## 93. Operational Definition of Done
- health works
- graceful shutdown verified
- critical failure test exists
- runbook exists
- alert/metric candidate exists
- recovery/replay safe

## 94. Service Runbook Template
Her critical service için:
- Overview
- Dependencies
- Startup
- Health
- Key metrics
- Common failures
- Recovery
- Data recovery
- Messaging recovery
- Security notes
- Verification

## 95. Incident Record
Significant incident sonrası kısa record:
- timeline
- impact
- root cause
- mitigation
- permanent fix
- prevention

## 96. Post-incident principle
Blame-free teknik analiz; process/system fix öncelikli.

## 97. Operational anti-pattern'ler
- sadece restart et
- pool'u büyüt
- DLQ'yu sil
- DB'yi elle düzelt
- alert'i kapat
- root cause çözmeden replay
- health endpoint varsa operable sanmak
- backup restore test etmemek

## 98. Runbook Review Questions
- Symptom açık mı?
- Checks executable mı?
- Recovery destructive mi?
- Replay idempotent mi?
- Verification step var mı?
- Security impact var mı?

## 99. Operational Readiness Checklist
- Startup deterministic mi?
- Graceful shutdown var mı?
- Liveness/readiness doğru mu?
- Critical dependency outage davranışı belli mi?
- DB migration failure runbook var mı?
- Kafka/RabbitMQ lag/DLQ recovery var mı?
- Search reindex/reconciliation var mı?
- Rollback/forward-fix koşulları belli mi?
- Backup/recovery classification var mı?
- Incident trace/correlation ile diagnose edilebilir mi?
- Local developer troubleshooting dokümante mi?

## 100. Completion Gate
Bu standard ile implementation öncesi Engineering Standards fazı tamamlanır.

Day 7 başlamadan önce Master Engineering Plan yeniden kontrol edilir ve Day 7 için exact implementation breakdown hazırlanır.