# Spring Cloud Engineering Standard

Bu doküman projede Spring Cloud bileşenlerinin kullanım standardını tanımlar.

Amaç Spring Cloud ekosistemini feature checklist gibi kullanmak değil; her capability'yi doğru architecture problemine bağlamak, overlapping responsibility'leri ayırmak ve platform-native alternatiflerle ilişkisini bilinçli yönetmektir.

## 1. Genel yaklaşım
- Spring Cloud capability odaklı kullanılır.
- Her component gerçek distributed systems problemi çözmelidir.
- Aynı responsibility iki tool ile gereksiz duplicate edilmez.
- Platform-native capability varsa ownership açıkça belirlenir.

## 2. Spring Boot vs Spring Cloud
Spring Boot application framework'tür.
Spring Cloud distributed-system integration abstraction ve pattern'leri sağlar.

Spring Cloud, kötü service boundary veya kötü domain design'i düzeltmez.

## 3. Mevcut baseline
Projede mevcut:
- Spring Cloud Gateway
- Spring Cloud Config
- Netflix Eureka
- OpenFeign
- LoadBalancer
- Circuit Breaker

Planlanan:
- Stream
- Function
- Bus
- Vault integration
- Contract
- Task
- Kubernetes

## 4. Gateway
Spring Cloud Gateway edge responsibility taşır.

Allowed:
- routing
- authentication boundary
- rate limiting
- correlation/context propagation
- coarse resilience
- header transformation

Forbidden:
- business rule
- business persistence
- domain orchestration

## 5. Gateway route design
Route:
- explicit
- service ownership ile uyumlu
- public API versioning ile tutarlı
olmalıdır.

## 6. Gateway filter
Filter yalnızca cross-cutting concern için.

Business validation Gateway filter'a yazılmaz.

## 7. Global vs route-specific filter
Global filter sadece gerçekten bütün route'lara uygulanacak concern için.

Rate limit gibi capability route-specific policy taşıyabilir.

## 8. Gateway timeout
Gateway timeout upstream budget ile uyumlu.

Gateway downstream service'den daha uzun ama client budget'tan kısa olmalıdır.

## 9. Gateway retry
Gateway retry default değildir.

Write request'lerde retry idempotency olmadan yapılmaz.

## 10. Gateway fallback
Fallback sahte success response üretmez.

## 11. Config Server
Non-secret distributed configuration için.

Config Server:
- normal application config
- environment-specific distributed config
taşır.

Secret store değildir.

## 12. Config repository
Config changes version-controlled olmalıdır.

## 13. Config refresh
Runtime refresh yalnızca güvenli config için.

Her config hot-refresh edilmez.

## 14. Immutable config
Restart gerektiren config immutable kabul edilir.

## 15. Config failure
Config Server unavailable startup davranışı service criticality'e göre bilinçli olmalıdır.

## 16. Secret boundary
Secret Spring Cloud Config repository'ye commit edilmez.

Vault ayrı capability.

## 17. Eureka
Service registry/discovery için mevcut learning baseline.

Service instance:
- register
- heartbeat
- deregister

semantic'i anlaşılmalıdır.

## 18. Eureka client
Hard-coded host yerine logical service name kullanılır.

## 19. Eureka health
Registry membership application readiness ile karıştırılmamalıdır.

## 20. Eureka self-preservation
Behavior bilinmelidir.

## 21. Eureka vs platform discovery
Kubernetes sonrası Service/DNS native discovery ile karşılaştırılır.

Production platform Kubernetes ise Eureka zorunlu olmayabilir.

## 22. Consul vs ZooKeeper
Karşılaştırma öğrenme/dokümantasyon kapsamındadır.

- Eureka: service registry odaklı
- Consul: discovery + KV + health/network ecosystem
- ZooKeeper: coordination primitive odaklı

Her üçünü aynı projede çalıştırmak hedef değildir.

## 23. OpenFeign
Declarative internal REST client.

Use-case:
- existing synchronous REST integration

## 24. Feign interface
Client contract business capability'ye göre isimlendirilir.

Generic ApiClient oluşmaz.

## 25. Feign DTO
Remote service persistence/domain class import edilmez.

Dedicated integration DTO kullanılır.

## 26. Feign timeout
Connect/read timeout explicit.

## 27. Feign retry
Implicit/default retry'ya güvenilmez.

Retry ownership resilience policy ile belirlenir.

## 28. Feign error decoding
HTTP error infrastructure exception olarak yukarı sızdırılmaz.

Semantic application failure'a translate edilir.

## 29. Feign observability
Trace/correlation propagate edilir.

## 30. LoadBalancer
Logical service name -> instance selection.

Client-side load balancing behavior gözlemlenir.

## 31. Load balancing assumption
Load balancing state consistency çözmez.

## 32. Sticky session
Stateless service baseline olduğundan sticky session default değildir.

## 33. Circuit Breaker abstraction
Spring Cloud Circuit Breaker abstraction kullanılabilir.

Underlying Resilience4j policy bilinmelidir.

## 34. Circuit Breaker ownership
Gateway coarse protection sağlayabilir.
Service-level dependency circuit breaker daha specific policy taşımalıdır.

## 35. Retry + Circuit Breaker order
Operator order bilinçli seçilir.

Retry storm oluşturmamalıdır.

## 36. Spring Cloud Stream
Event/message integration için binder abstraction.

Primary use-case:
- Kafka Domain Event Streaming

## 37. Binder abstraction
Binder portability faydalıdır fakat Kafka semantics tamamen gizlenmez.

Partition, key, consumer group, offset gibi broker concepts bilinmelidir.

## 38. Functional binding model
Supplier / Function / Consumer model tercih edilir.

## 39. Stream consumer
Consumer:
- deserialize
- dedup
- application handler
- failure mapping
yapar.

Business logic lambda içine gömülmez.

## 40. Stream producer
Integration event payload domain model'den ayrıdır.

## 41. Stream destination naming
Topic/binding naming açık ve domain-oriented.

## 42. Stream consumer group
Her logical capability ayrı group.

## 43. Stream partition key
propertyId / offerId gibi ordering requirement taşıyan key kullanılır.

## 44. Stream retry
Binder retry config message semantics ile uyumlu.

## 45. Stream DLQ/DLT
Kafka native DLT/retry topic yaklaşımı ile çakışmamalıdır.

## 46. Spring Cloud Function
Function abstraction message processing'i test edilebilir hale getirebilir.

## 47. Function purity
Pure transformation ideal olabilir fakat application use-case çağıran side-effect function kabul edilebilir.

Side-effect boundary açık olmalıdır.

## 48. Function composition
Composition yalnızca readability ve reuse sağlıyorsa.

## 49. Function routing
Dynamic routing gerekmiyorsa gereksiz complexity eklenmez.

## 50. Spring Cloud Bus
Distributed config/event notification için.

Primary use-case:
Config change propagation.

## 51. Bus boundary
Business Domain Event bus değildir.

Kafka business events ile Config Bus event'leri karıştırılmaz.

## 52. Bus refresh
Runtime refresh yalnızca refresh-safe config için.

## 53. Spring Cloud Vault
Secret management integration için.

Vault:
- DB credential
- client secret
- broker secret
- signing material
gibi secret'ları yönetir.

## 54. Vault lease
Dynamic secret/lease capability öğrenilebilir.

## 55. Vault fail behavior
Critical secret alınamıyorsa fail-fast tercih edilir.

## 56. Secret rotation
Rotation support mümkünse restart/refresh strategy ile birlikte tasarlanır.

## 57. Vault vs Config
Config = non-secret configuration.
Vault = secret.

## 58. Spring Cloud Contract
Consumer/provider contract verification.

Primary use-case:
- REST contract

Event contract için ayrıca schema/consumer test gerekir.

## 59. Contract ownership
Provider change consumer compatibility'yi bozmamalıdır.

## 60. Contract scope
Her DTO için contract testi zorunlu değildir.

Cross-service critical integration'lar önceliklidir.

## 61. Stub
Generated stub integration test'te kullanılabilir.

Stub production dependency değildir.

## 62. Spring Cloud Task
Finite, short-lived application/job için.

Candidate:
- Elasticsearch reindex
- reconciliation
- cache warm

## 63. Task vs @Scheduled
`@Scheduled` long-running service içindeki recurring job.
Spring Cloud Task finite process.

## 64. Task idempotency
Rerun güvenli olmalıdır.

## 65. Task metadata
Execution status, start/end, failure observable olmalıdır.

## 66. Spring Cloud Kubernetes
Kubernetes fundamentals öğrenildikten sonra.

Native Kubernetes capability bilmeden abstraction kullanılmaz.

## 67. Kubernetes discovery
K8s Service/DNS ile Eureka overlap değerlendirilir.

## 68. Kubernetes ConfigMap/Secret
Config Server/Vault ile overlap responsibility açık olmalıdır.

## 69. Spring Cloud Kubernetes Config
Native config integration ancak gerçek value sağlıyorsa.

## 70. Platform lock-in awareness
Spring Cloud abstraction her platform-specific capability'yi gizlemez.

## 71. Service Discovery Ownership
Local/learning environment:
- Eureka

Kubernetes environment:
- native Service/DNS primary candidate

## 72. Load Balancing Ownership
Client-side LoadBalancer ile platform/service-mesh load balancing overlap bilinmelidir.

## 73. Resilience Ownership
Policy hierarchy:
- client library/service-level primary
- Gateway coarse edge protection
- platform/service mesh varsa overlap review

## 74. Config Ownership
- application defaults -> repo
- environment config -> Config Server
- secret -> Vault
- platform config -> ConfigMap only if platform phase demands

## 75. Messaging Ownership
- RabbitMQ via Spring AMQP for command/work queue
- Kafka via Spring Cloud Stream for event streaming

RabbitMQ'ı sırf Stream binder kullanmak için taşıma zorunluluğu yok.

## 76. Observability
Spring Cloud integration'ların trace/metric context'i korunur.

## 77. Correlation
Gateway -> Feign -> Stream -> consumer zincirinde correlation propagate edilir.

## 78. Failure semantics
Framework exception dış contract değildir.

## 79. Version Compatibility
Spring Boot / Spring Cloud release train compatibility matrix kontrol edilir.

## 80. Dependency Management
Spring Cloud BOM kullanılmalıdır.

Individual Cloud dependency version'ları random pin edilmez.

## 81. Upgrade policy
Spring Cloud release train upgrade ayrı controlled change.

## 82. Deprecated component
Netflix OSS legacy components sırf eski tutorial'da var diye eklenmez.

## 83. Hystrix
Kullanılmaz.

Resilience4j tabanlı modern Circuit Breaker.

## 84. Ribbon
Kullanılmaz.

Spring Cloud LoadBalancer.

## 85. Zuul
Kullanılmaz.

Spring Cloud Gateway.

## 86. Sleuth
Modern stack'te Micrometer Tracing/OpenTelemetry yaklaşımı.

## 87. Config encryption
Config repository encryption secret management'in yerine geçmez.

## 88. Bus broker choice
Bus için broker seçimi mevcut infrastructure ile uyumlu yapılır.

## 89. Config change governance
Config change code change kadar kontrollü olabilir.

Versioning/audit gerekir.

## 90. Dynamic refresh risk
Runtime config change test edilmeden uygulanmaz.

## 91. Client fallback
Feign fallback implementation gerçek degraded semantic taşımalıdır.

## 92. Service registration startup
Service registry available değilken startup/retry/backoff behavior bilinçli.

## 93. Discovery stale entry
Registry stale instance possibility bilinmelidir.

## 94. Health and discovery
Readiness ile registry availability semantics ilişkilendirilir.

## 95. Contract evolution
Backward-compatible REST/event/proto change policy korunur.

## 96. Spring Cloud anti-pattern'leri
- component checklist yapmak
- Gateway'e business logic koymak
- Config'e secret koymak
- Eureka'yı K8s DNS ile gereksiz duplicate etmek
- Feign retry + Gateway retry + Resilience4j retry üst üste koymak
- Stream kullanınca Kafka semantics'i bilmeye gerek yok sanmak
- Bus'ı Domain Event bus gibi kullanmak
- Cloud Task'i recurring scheduler gibi kullanmak
- Spring Cloud Kubernetes'i native K8s öğrenmeden kullanmak

## 97. Day mapping
- Day 8 -> security integration context
- Day 10 -> Stream + Function
- Day 14 -> resilience expansion
- Day 15 -> Bus + Vault
- Day 17 -> Contract
- Day 19 -> Task
- backend sonrası -> Kubernetes

## 98. Architecture Review Questions
- Bu component hangi distributed systems problemini çözüyor?
- Aynı capability başka layer/platform tarafından zaten çözülüyor mu?
- Ownership kimde?
- Failure behavior ne?
- Timeout/retry budget ne?
- Security impact ne?
- Observability var mı?

## 99. Migration principle
Local learning topology ile Kubernetes topology aynı olmak zorunda değildir.

Platform değişince obsolete component kaldırılabilir.

## 100. Spring Cloud Review Checklist
- Gateway yalnızca edge concern mü?
- Config ile secret ayrıldı mı?
- Eureka gerçekten gerekli environment'ta mı?
- Feign contract isolated mı?
- Timeout explicit mi?
- Retry tek ownership'ta mı?
- LoadBalancer behavior bilinçli mi?
- Circuit Breaker dependency-specific mi?
- Stream binding semantic açık mı?
- Kafka partition/group bilinçli mi?
- Function side-effect sınırı açık mı?
- Bus business event için kullanılmıyor mu?
- Vault secret lifecycle doğru mu?
- Contract critical integration'ı koruyor mu?
- Task rerun-safe mi?
- Kubernetes native capability ile overlap değerlendirildi mi?
- Boot/Cloud version compatibility doğrulandı mı?