# Backend Roadmap Master Audit

## Audit sonucu

Day 7–26 planları architecture, business workflow, persistence, communication, security, reliability, testing, observability ve operations açısından çapraz kontrol edildi.

Sonuç: temel tasarım güçlü ve büyük ölçüde tutarlı; ancak implementation öncesi düzeltilmesi gereken birkaç önemli konu bulundu.

## 1. Milestone yoğunluğu

Önceki roadmap'te şu Day'ler birden fazla büyük konuyu aynı milestone'a topluyordu:

- gRPC + GraphQL
- Vault + Bus
- Contract Testing + Failure-Path Testing
- OpenTelemetry + Prometheus + Grafana + Loki + Tempo
- Architecture Audit + tüm E2E + recovery + completion

Bu, kabul edilen **bir Day = tek service veya tek ana konu** ilkesine aykırıydı.

### Düzeltme
Backend roadmap Day 33'e genişletildi. İçerik azaltılmadı; yalnız daha küçük milestone'lara bölündü.

## 2. Premature event implementation

Eski Kafka foundation planı Offer Aggregate oluşmadan Offer publisher/producer hazırlamaya yaklaşıyordu.

### Düzeltme
- Day 17 Kafka/Stream/Function yalnız event-streaming foundation kurar.
- Offer event publication Day 22 gerçek Offer workflow ile aktive edilir.
- Critical business publish reliability layer'ı bypass edemez.

## 3. BuyerService Offer idempotency correctness gap

Eski Saga planında Redis, `Idempotency-Key` sonucu için ana store gibi kullanılabilirdi.

Bu durumda:
1. Offer Couchbase'e kaydolur.
2. Redis idempotency kaydı yazılamadan process çöker.
3. Client aynı key ile retry eder.
4. İkinci Offer oluşma riski doğar.

### Düzeltme
- Durable idempotency ownership BuyerService/Couchbase'tedir.
- Idempotency record request hash + Offer/result association ile durable tutulur.
- Redis yalnız accelerator/cache olabilir.
- Aynı key + aynı payload -> aynı result.
- Aynı key + farklı payload -> conflict.

## 4. Buyer/Couchbase reliable event publication gap

Property/Mongo Outbox ve Seller/Cassandra pending dispatch tasarlanmıştı; fakat BuyerService'in critical OfferRequested event'inin durable publication stratejisi açık değildi.

### Düzeltme
Day 18 reliable-messaging milestone'ında Couchbase için reliable-publication strategy design kararı zorunludur. Implementation Day 22 Offer workflow'da bu stratejiyi kullanır.

Acceptable solution must guarantee that committed Offer state cannot silently lose its required outbound event.

## 5. Seller/Cassandra outbound reliability scope too narrow

Eski plan Cassandra reliability'yi yalnız Seller -> RabbitMQ listing command için ele alıyordu. Fakat Day 22'de SellerAccepted/SellerRejected Kafka event'leri de critical outbound messages.

### Düzeltme
Day 20 Cassandra reliable outbound foundation generic outbound-message semantics'e genişletildi:
- RabbitMQ listing commands
- later Kafka Seller decision events

Destination/type broker-specific adapter'da ayrılır; Cassandra durable pending state broker API bilmez.

## 6. Master plan stale Day references

Eski Master Plan:
- Cassandra reliability -> Day 11 öncesi
- Identity mapping -> Day 8 öncesi
- Search rebuild -> Day 19

şeklinde eski numbering taşıyordu.

### Düzeltme
- Identity mapping -> Day 14
- reliable messaging -> Day 18–20
- Search reindex/reconciliation -> Day 31
- backend completion -> Day 33

## 7. Spring Cloud mandatory coverage

Güncel roadmap mandatory Spring Cloud learning kapsamını koruyor:

- Spring Boot
- Gateway
- Config
- Netflix Eureka
- OpenFeign
- LoadBalancer
- Circuit Breaker
- Stream
- Function
- Vault
- Bus
- Contract
- Task
- Spring Cloud Kubernetes (backend sonrası)
- Eureka vs Consul vs ZooKeeper comparison

Spring Cloud Bus resmi güncel dokümantasyonda RabbitMQ veya Kafka binder ile çalışabiliyor ve `/actuator/busrefresh` endpoint'i mevcut; bu nedenle RabbitMQ control-plane seçimi geçerlidir. Spring Cloud Vault güncel olarak KV secrets, fail-fast ve çeşitli secret backends'i destekliyor. Spring Cloud Contract da provider/stub tabanlı contract testing için güncel olarak mevcut.

## 8. Architecture consistency

Final ordering:
- datastore/architecture foundations first
- identity/security before specialized internal protocols
- event streaming before reliability
- reliability before CQRS/Saga
- Saga before resilience hardening
- Vault/Bus after core runtime behavior
- test/contract/failure hardening before final observability/recovery completion
- architecture audit before final E2E completion

Bu sıra dependency açısından tutarlıdır.

## 9. Remaining implementation-time decisions

Bunlar blocker değildir fakat ilgili Day başlamadan finalize edilmelidir:

- exact Spring Boot/Spring Cloud-compatible dependency versions
- MySQL migration tool choice
- Couchbase durable idempotency/reliable publication mechanics
- Cassandra pending outbound partition strategy
- exact Vault auth method for local vs production-like documentation
- supported Loki log shipper at implementation time
- numeric resilience thresholds after local measurement

## 10. Final verdict

Day 7 implementation'a geçmeden önce master architecture açısından blocker kalmamıştır.

Yeni source of truth:
1. root `ROADMAP.md`
2. `docs/MASTER-ENGINEERING-PLAN.md`
3. this audit
4. per-topic exact plans, which must follow the new Day 15–33 numbering when implemented.

Old Day 15–26 numbering is superseded by the Day 15–33 sequence.
