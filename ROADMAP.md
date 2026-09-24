# Real Estate Backend Architecture Lab — Backend Roadmap

## Kapsam

`main` branch'indeki Day 1–6 yapısı stabil baseline olarak kabul edilir. Bu roadmap Day 7 ile başlar ve backend tarafını yaklaşık Day 20 civarında tamamlamayı hedefler. Bir Day gerçek takvim günü olmak zorunda değildir; bir milestone birkaç oturum sürebilir.

## Temel ilkeler

- Day 1–6 kapsamında doğrulanmış teknolojileri yeniden uygulamayacağız.
- AuthService ve UserProfileService üzerindeki çalışan PostgreSQL akışlarını koruyacağız.
- Polyglot Persistence yalnızca farklı bir veri modeli veya distributed systems problemi öğrettiği yerde kullanılacak.
- Her Day kendi branch'inde geliştirilecek.
- Kod, test, service design ve roadmap durumu aynı Day branch'inde birlikte güncellenecek.
- Tek bir çok uzun doküman yerine kısa ve odaklı dokümanlar kullanılacak.

## Service / datastore / architecture matrisi

| Service | Primary datastore | Spring data stack | Architecture |
|---|---|---|---|
| AuthService | PostgreSQL | Spring Data JPA + Hibernate | Mevcut N-Layer baseline |
| UserProfileService | PostgreSQL | Spring Data JPA + Hibernate | Mevcut N-Layer baseline |
| AgentService | MySQL | Spring Data JPA + Hibernate | Clean Architecture |
| BuyerService | Couchbase | Spring Data Couchbase | Hexagonal Architecture |
| SellerService | Cassandra | Spring Data Cassandra | Onion Architecture |
| PropertyService | MongoDB | Spring Data MongoDB | Vertical Slice Architecture |
| SearchService | Elasticsearch | Spring Data Elasticsearch | Vertical Slice + CQRS Query Side |
| Shared infrastructure | Redis | Spring Data Redis | Cache / Idempotency / Rate Limiting / Ephemeral State |

## Day 7–20

### Day 7 — Polyglot Persistence & Architecture Foundations
Branch: `day/07-polyglot-persistence`

MySQL, Couchbase, Cassandra, MongoDB, Elasticsearch ve Redis temelleri oluşturulacak; Clean Architecture, Hexagonal Architecture, Onion Architecture ve Vertical Slice Architecture sınırları kurulacak. SearchService foundation eklenecek.

### Day 8 — Spring Security + OAuth2 + OIDC + Keycloak
Branch: `day/08-security-keycloak`

Keycloak tabanlı authentication/authorization, RBAC ve service-to-service authentication uygulanacak. Mevcut custom JWT yalnızca eğitimsel karşılaştırma için gerektiği ölçüde korunacak.

### Day 9 — REST + gRPC + GraphQL
Branch: `day/09-grpc-graphql`

REST public API baseline olarak korunacak; internal service-to-service use-case'lerde gRPC, esnek query/read use-case'lerinde GraphQL uygulanacak.

### Day 10 — Kafka + Spring Cloud Stream + Spring Cloud Function
Branch: `day/10-kafka-stream-function`

RabbitMQ command/task messaging rolünde korunacak, Kafka domain-event streaming için eklenecek.

### Day 11 — Outbox + Inbox + Idempotency + Retry + DLQ
Branch: `day/11-outbox-inbox-idempotency`

Reliable messaging pattern'leri uygulanacak. Cassandra'ya RDBMS ile aynı transactional semantics varmış gibi davranılmayacak.

### Day 12 — CQRS + Elasticsearch Search Projection
Branch: `day/12-cqrs-search`

PropertyService/MongoDB source of truth olacak; property event'leri Kafka üzerinden SearchService/Elasticsearch projection'ına aktarılacak.

### Day 13 — Saga + Eventual Consistency
Branch: `day/13-saga`

Property offer/reservation workflow'u Saga Choreography ve compensating action'larla uygulanacak.

### Day 14 — Advanced Resilience
Branch: `day/14-resilience`

Mevcut Circuit Breaker yapısı Retry, TimeLimiter, Bulkhead ve Redis-backed Rate Limiting ile genişletilecek.

### Day 15 — Spring Cloud Bus + Vault
Branch: `day/15-bus-vault`

Normal configuration için Spring Cloud Config, secret yönetimi için Vault, değişiklik yayılımı için Spring Cloud Bus kullanılacak.

### Day 16 — Unit + Integration + Testcontainers
Branch: `day/16-testing`

Datastore, messaging, security ve architecture odaklı otomatik testler geliştirilecek.

### Day 17 — Contract + Failure-Path Testing
Branch: `day/17-contract-testing`

Contract verification ve sistematik negative/failure-path testleri eklenecek.

### Day 18 — OpenTelemetry + Prometheus + Grafana + Loki + Tempo
Branch: `day/18-observability`

Mevcut Micrometer/Zipkin baseline logs, metrics ve traces ile tam observability yapısına genişletilecek.

### Day 19 — Spring Cloud Task + Reindex / Reconciliation
Branch: `day/19-cloud-task`

Elasticsearch reindex ve MongoDB → Elasticsearch reconciliation gibi kısa ömürlü maintenance job'ları uygulanacak.

### Day 20 — Architecture Fitness + E2E + Backend Completion
Branch: `day/20-backend-completion`

ArchUnit architecture fitness kuralları, end-to-end doğrulama, nihai ADR'ler ve backend completion dokümantasyonu tamamlanacak.

## Spring Cloud kapsamı

Zaten mevcut: Gateway, Config, Eureka, OpenFeign, LoadBalancer, Circuit Breaker.

Planlanan: Stream, Function, Bus, Vault, Contract, Task.

Backend sonrasında: Spring Cloud Kubernetes.

Dokümantasyon karşılaştırması: Eureka vs Consul vs ZooKeeper.

## Backend sonrası faz

Docker hardening → Kubernetes native fundamentals → Spring Cloud Kubernetes → Jenkins → SonarQube → Nexus → Harbor → Argo CD / GitOps.
