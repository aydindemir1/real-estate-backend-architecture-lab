# Testing Standard

Bu doküman projede uygulanacak test stratejisini tanımlar.

Amaç yalnızca coverage artırmak değil; business rule, architecture boundary, persistence semantics, messaging reliability, security ve failure behavior'ını güvence altına almaktır.

## 1. Test Pyramid
- Unit Test
- Slice / Component Test
- Integration Test
- Contract Test
- E2E Test

E2E test sayısı düşük; Unit ve focused Integration test sayısı daha yüksek tutulur.

## 2. Test amacı
Her test şu sorulardan birine net cevap vermelidir:
- Business rule doğru mu?
- Integration contract doğru mu?
- Persistence davranışı doğru mu?
- Failure path doğru mu?
- Security boundary doğru mu?
- Architecture rule korunuyor mu?

## 3. Unit Test
Unit test:
- hızlı
- deterministic
- isolated
- infrastructure bağımsız
olmalıdır.

Domain test'lerde Spring context açılmaz.

## 4. Domain Test
Örnekler:
- Property publish transition
- Property hold/reserve rules
- Offer invalid transition
- Agent suspended -> unavailable rule
- Money / PriceRange validation

## 5. Application Service Test
Repository/port fake veya mock ile use-case orchestration test edilir.

Test edilir:
- doğru dependency çağrısı
- doğru domain behavior
- ownership check
- failure propagation
- side-effect order

## 6. Mock kullanımı
- Mock yalnızca external dependency boundary'de.
- Domain object mock edilmez.
- Her class interaction testine dönüştürülmez.
- Behavior yerine implementation detail test edilmez.

## 7. Fake tercih edilebilecek yerler
Hexagonal service'lerde:
- InMemoryOfferRepository
- FakeAgentAvailabilityPort
- FakeEventPublisher

test okunabilirliğini artırıyorsa kullanılabilir.

## 8. Slice Test
Spring slice test'leri gerektiğinde:
- @WebMvcTest
- repository slice
- security slice
gibi dar context ile çalıştırılır.

Her test için @SpringBootTest kullanılmaz.

## 9. Integration Test
Gerçek infrastructure semantics doğrulanır.

Örnek:
- MySQL uniqueness
- Couchbase document persistence
- Cassandra partition query
- Mongo @Version concurrency
- Elasticsearch mapping/query
- Redis TTL/idempotency

## 10. Testcontainers
Primary integration test yaklaşımıdır.

Kullanılacak candidate container'lar:
- PostgreSQL
- MySQL
- Couchbase
- Cassandra
- MongoDB
- Elasticsearch
- Redis
- Kafka
- RabbitMQ
- Keycloak gerektiğinde

## 11. Testcontainer lifecycle
- mümkün olduğunca reusable/shared test container setup
- deterministic initialization
- test isolation
- temiz test data

sağlanır.

## 12. Persistence Test
Repository adapter test'i gerçek datastore ile çalışır.

Mock repository ile:
database index, unique constraint, query order, partition semantics test edilmiş sayılmaz.

## 13. Messaging Test
Test edilir:
- producer serialization
- consumer deserialization
- duplicate delivery
- retryable failure
- non-retryable failure
- DLQ/DLT
- ordering assumption
- message key
- schema compatibility

## 14. RabbitMQ Test
Özellikle:
- exchange/queue/binding
- routing key
- ACK/NACK
- DLQ
- redelivery
- idempotency

## 15. Kafka Test
Özellikle:
- topic
- partition key
- consumer group
- offset behavior
- duplicate event
- retry/DLT
- projection update

## 16. Contract Test
Service contract breaking change riskini azaltır.

Candidate:
- REST consumer/provider contract
- Kafka event contract
- gRPC protobuf compatibility

Spring Cloud Contract Day 17'de learning lab olarak uygulanacaktır.

## 17. API Test
Her public endpoint için en az:
- success
- validation error
- unauthorized
- forbidden
- not found/conflict

senaryoları değerlendirilir.

## 18. Security Test
- missing token
- expired token
- invalid issuer
- wrong role
- missing scope
- ownership violation
- admin behavior
- service credential failure

test edilir.

## 19. Authorization test
Role test etmek ownership test etmek değildir.

Örnek:
SELLER role var ama resource başka seller'a ait -> 403.

## 20. Concurrency Test
Critical state transition'lar için:
- concurrent offers
- optimistic locking conflict
- duplicate command
- duplicate event

test edilir.

## 21. Idempotency Test
Aynı Idempotency-Key + aynı payload:
- aynı result

Aynı key + farklı payload:
- conflict

## 22. Failure-Path Test
Happy path kadar önemlidir.

Örnek:
- DB unavailable
- broker unavailable
- gRPC timeout
- downstream 503
- poison message
- retry exhausted

## 23. Resilience Test
- Circuit Breaker open
- Retry
- TimeLimiter
- Bulkhead
- RateLimiter

behavior test edilir.

## 24. E2E Test
Az sayıda kritik business flow:
- registration/profile
- listing submission -> property creation
- publish -> search projection
- offer -> hold -> seller decision -> reservation

test edilir.

## 25. E2E test anti-pattern
Tüm business rule yalnızca E2E ile test edilmez.

## 26. Architecture-specific test strategy

### AgentService — Clean Architecture
- domain pure unit tests
- application use-case tests
- persistence adapter integration test
- ArchUnit dependency test

### BuyerService — Hexagonal Architecture
- inbound port test
- outbound port fake/mock
- Couchbase adapter test
- gRPC adapter test
- Kafka adapter test

### SellerService — Onion Architecture
- domain tests
- application orchestration
- Cassandra query table integration
- RabbitMQ/Kafka adapter tests

### PropertyService — Vertical Slice Architecture
- feature handler tests
- slice-level integration tests
- Mongo concurrency tests
- messaging consumer/outbox tests

### SearchService — CQRS Query Side
- query handler tests
- Elasticsearch integration tests
- projection consumer tests
- reindex tests

## 27. Test naming
Test adı behavior anlatmalıdır.

Örnek:
`shouldRejectOfferWhenPropertyIsAlreadyReserved`

Kaçınılacak:
`testOffer1`

## 28. Given / When / Then
Test okunabilirliği için Given-When-Then veya Arrange-Act-Assert kullanılabilir.

## 29. Test data
- random uncontrolled data yerine deterministic data
- test builder/object mother yalnızca fayda sağlıyorsa
- production entity builder invariant bypass etmez

## 30. Time testing
Time-dependent logic için Clock/fixed time kullanılır.

## 31. Async testing
Thread.sleep ile test bekletmekten kaçınılır.

Awaitility veya event-condition based waiting kullanılabilir.

## 32. Flaky Test
Flaky test kabul edilmez.

Sebep:
- timing
- shared state
- order dependency
- real external network

olabilir.

## 33. Test isolation
Testler birbirinin data'sına bağımlı değildir.

## 34. Parallel test
Parallel execution yalnızca shared resource safety sağlanıyorsa açılır.

## 35. Coverage
Coverage kalite metriği değil, yardımcı metriktir.

Blind %100 coverage hedefi yoktur.

Critical domain rule'larda yüksek meaningful coverage hedeflenir.

## 36. Mutation Testing
İleri aşamada domain rule kalitesini ölçmek için mutation testing değerlendirilebilir.

## 37. Performance Test
Functional test'ten ayrıdır.

Critical endpoint / consumer throughput için ileride:
- latency
- throughput
- saturation

ölçülebilir.

## 38. Test environment
Local ve CI test behavior mümkün olduğunca aynı olmalıdır.

## 39. External internet dependency
Testler public internet'e bağlı olmamalıdır.

## 40. Snapshot Test
API contract için sınırlı kullanılabilir.

Large brittle snapshot'lar kaçınılır.

## 41. Golden Master
Legacy behavior characterization gerektiğinde kullanılabilir; yeni domain design için primary yaklaşım değildir.

## 42. Database cleanup
Testcontainers + isolated schema/db tercih edilir.

Manual global cleanup script'e bağımlılık azaltılır.

## 43. Test logging
Başarılı testlerde gereksiz log noise azaltılır.

Failure durumunda correlation/context görülebilmelidir.

## 44. CI quality gate
CI'da en az:
- unit tests
- integration tests
- architecture tests
- static analysis

çalışmalıdır.

Contract/E2E ayrı stage olabilir.

## 45. Test anti-pattern'leri
- her testte @SpringBootTest
- private method test etmek
- implementation detail assertion
- domain entity mocklamak
- Thread.sleep
- flaky test ignore etmek
- gerçek production service'e test call
- test order dependency
- coverage için anlamsız test
- mock repository ile DB behavior test ettiğini sanmak

## 46. Definition of Tested Feature
Bir feature tamamlanmış sayılmadan önce uygun olanlar:
- domain unit test
- application test
- persistence/integration test
- API validation/error test
- security test
- messaging duplicate/failure test
- concurrency test
- architecture rule test

değerlendirilmiş olmalıdır.

## 47. Testing Review Checklist
- Business invariant test edildi mi?
- Invalid transition test edildi mi?
- Failure path var mı?
- Real datastore semantics doğrulandı mı?
- Duplicate delivery/idempotency test edildi mi?
- Security ownership test edildi mi?
- Timeout/retry behavior test edildi mi?
- Test deterministic mi?
- Test doğru seviyede mi?
- Gereksiz Spring context açılıyor mu?
- Test implementation detail'e mi bağlı?