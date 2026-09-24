# Architecture Fitness & Governance Standard

Bu doküman tasarlanan Architecture, dependency direction ve engineering standard'larının kod seviyesinde korunması için governance kurallarını tanımlar.

## 1. Amaç
- Architecture dokümanda kalmayacak.
- Dependency rule'ları test edilebilir olacak.
- Forbidden dependency'ler otomatik yakalanacak.
- ADR, code review ve CI quality gate ile mimari erozyon azaltılacak.

## 2. Architecture Fitness Function
Architecture Fitness Function, sistemin istenen architectural characteristic'lerini sürekli doğrulayan otomatik kontroldür.

Örnek:
- domain infrastructure'a bağımlı mı?
- controller repository'ye direkt gidiyor mu?
- Vertical Slice başka slice'ın internal package'ına erişiyor mu?
- SearchService write-side dependency taşıyor mu?

## 3. ArchUnit
Java package/layer dependency rule'larını test etmek için ArchUnit kullanılacaktır.

Candidate kontroller:
- domain -> infrastructure yasak
- application -> presentation yasak
- controller -> repository direct dependency yasak
- domain Spring annotation bağımlılığı yasak Clean/Hexagonal/Onion service'lerde

## 4. AgentService — Clean Architecture Rules
Allowed direction:
presentation -> application -> domain
infrastructure -> application/domain

Forbidden:
- domain -> infrastructure
- domain -> presentation
- application -> presentation
- domain -> Spring Data/JPA annotation

## 5. BuyerService — Hexagonal Architecture Rules
Allowed:
- adapter.in -> application.port.in
- application -> domain
- adapter.out implements application.port.out

Forbidden:
- application -> adapter
- domain -> adapter
- inbound adapter -> outbound adapter direct call

## 6. SellerService — Onion Architecture Rules
Allowed:
- presentation/infrastructure -> application -> domain

Forbidden:
- domain -> Cassandra
- domain -> RabbitMQ/Kafka
- application -> presentation

## 7. PropertyService — Vertical Slice Rules
Each slice:
- kendi command/query/handler/controller/consumer'ını taşır.

Forbidden:
- slice A -> slice B internal class
- feature handler -> another feature controller
- shared package'in feature-specific logic taşıması

Cross-slice ortak behavior yalnızca gerçekten shared domain/infrastructure ise shared altına alınır.

## 8. SearchService — CQRS Query Side Rules
Forbidden:
- canonical Property write
- MongoDB write-side dependency
- business Aggregate mutation

Allowed:
- Elasticsearch projection/update
- query handler
- Kafka event consumer

## 9. N-Layer baseline rules
AuthService/UserProfileService için:
controller -> service -> repository

Forbidden:
- controller -> repository
- entity -> controller

## 10. Gateway Rules
Forbidden:
- domain entity
- business repository
- business use-case
- persistence layer

Gateway yalnızca edge/cross-cutting responsibility taşır.

## 11. Package Visibility
Mümkünse implementation detayları package-private tutulur.

Public API yalnızca gerçekten external package tarafından gereken class/method'lardır.

## 12. Module Boundary
Her service bağımsız module/service boundary olarak ele alınır.

Cross-service direct source dependency yapılmaz.

## 13. Shared Library Policy
Shared business/domain library oluşturulmaz.

Shared technical library ancak:
- stable
- generic
- cross-cutting
- coupling yaratmayan
bir ihtiyaç varsa değerlendirilir.

Örnek candidate:
- observability helper
- test support

## 14. Forbidden Dependencies
Genel olarak yasak candidate'lar:
- domain -> Spring MVC
- domain -> KafkaTemplate
- domain -> RedisTemplate
- domain -> JPA repository
- controller -> persistence adapter
- service A -> service B entity package

## 15. Dependency Cycles
Package/module dependency cycle yasaktır.

Cycle detection ArchUnit/Build tooling ile yapılabilir.

## 16. Layer Annotation Abuse
@Service/@Component ile her class framework bean yapılmaz.

Domain object framework bean değildir.

## 17. Static Analysis
Candidate araçlar:
- SonarQube
- Error Prone/SpotBugs candidate
- Checkstyle/formatter candidate

Tool seçimi ileriki CI/CD fazında finalize edilir.

## 18. SonarQube Quality Gate
Candidate gate:
- new blocker/critical issue = 0
- new security hotspot reviewed
- new duplicated code threshold
- test coverage on new code meaningful threshold

Blind metric chasing yapılmaz.

## 19. Code Style
Format/style otomatik enforce edilmelidir.

Manual style debate azaltılır.

## 20. ADR Governance
Önemli architectural decision ADR ile kaydedilir.

ADR gerektiren örnekler:
- database per service
- RabbitMQ vs Kafka responsibility
- Saga choreography
- Elasticsearch as query side
- Keycloak
- Outbox strategy

## 21. ADR format
- Context
- Decision
- Alternatives
- Consequences
- Status

ADR kısa ve decision-oriented olmalıdır.

## 22. ADR Lifecycle
Status:
- Proposed
- Accepted
- Superseded
- Deprecated

Eski ADR silinmez; superseded olarak işaretlenir.

## 23. Code Review Gate
PR review yalnızca syntax değil şu alanları kontrol eder:
- architecture boundary
- business invariant
- transaction boundary
- security
- testing
- observability
- resilience
- persistence/query

## 24. Review Checklist
Her PR için uygun olanlar:
- doğru layer/slice?
- forbidden dependency var mı?
- new abstraction gerekli mi?
- error handling standarda uygun mu?
- test coverage meaningful mi?
- logging sensitive data içeriyor mu?
- API breaking change var mı?
- ADR gerekiyor mu?

## 25. Definition of Done Governance
Feature tamamlanmış sayılmadan önce:
- build green
- unit/integration tests green
- ArchUnit green
- static analysis acceptable
- docs updated
- ADR gerekiyorsa added
- no known critical security issue

## 26. CI Architecture Stage
CI candidate stages:
1. compile
2. unit tests
3. architecture tests
4. integration tests
5. static analysis
6. contract tests
7. package/container build

## 27. Fast Feedback
Architecture tests hızlı çalışmalıdır.

Developer local'de CI'dan önce çalıştırabilmelidir.

## 28. Quality Gate vs Velocity
Governance development'ı gereksiz yavaşlatmamalıdır.

Rule yalnızca gerçek quality/risk faydası varsa eklenir.

## 29. Exception Process
Bir architecture rule geçici olarak ihlal edilecekse:
- gerekçe
- risk
- follow-up
belgelenmelidir.

Silent exception yoktur.

## 30. Technical Debt
Known architecture debt görünür tutulur.

Candidate:
- TODO değil issue/roadmap item
- owner
- reason
- intended resolution

## 31. Dependency Management
Version management central olmalıdır.

Duplicate/conflicting dependency version azaltılır.

## 32. Dependency Upgrade
Major framework upgrade ayrı change olarak ele alınır.

Business feature ile büyük dependency upgrade aynı PR'da karıştırılmaz.

## 33. API Governance
Breaking API change review gerektirir.

OpenAPI diff veya contract test kullanılabilir.

## 34. Event Governance
Event schema değişikliği:
- backward compatibility
- schemaVersion
- consumer impact
kontrolü gerektirir.

## 35. Database Governance
Schema migration:
- version controlled
- backward-compatible rollout
- rollback/forward-fix strategy
ile ele alınır.

## 36. Security Governance
Security-sensitive change:
- authorization impact
- secret impact
- attack surface
review edilir.

## 37. Observability Governance
Yeni critical flow:
- log
- metric
- trace
coverage açısından değerlendirilir.

## 38. Performance Governance
Critical query/API değişikliği performance regression riskine göre değerlendirilir.

## 39. Architecture Drift
Package ve dependency pattern zamanla değişirse docs/ADR ile kod arasında fark oluşmamalıdır.

Regular review ile drift kontrol edilir.

## 40. Fitness Test Examples
Örnek test isimleri:
- DomainMustNotDependOnInfrastructureTest
- ControllersMustNotAccessRepositoriesTest
- HexagonalAdaptersMustDependOnPortsTest
- VerticalSlicesMustNotDependOnEachOtherTest
- SearchServiceMustRemainQuerySideTest

## 41. Architecture Test Naming
Test adı violated rule'u açıkça anlatmalıdır.

## 42. Generated Code
Generated client/proto code architecture rule'lardan kontrollü exclude edilebilir.

## 43. Framework Exceptions
Spring generated/proxy classes false positive üretirse explicit exclude yapılır; rule gevşetilmez.

## 44. Monorepo Governance
Repo monorepo olabilir ama service autonomy korunur.

Shared root build/dependency management service coupling anlamına gelmez.

## 45. Branch Governance
Plan/design kararları docs branch'te kalıcılaştırılır.

Implementation Day branch'lerinde yapılır.

## 46. Commit Discipline
Small meaningful commits tercih edilir.

Tek devasa commit yerine:
- model
- persistence
- API
- tests
- docs
gibi mantıksal parçalar.

## 47. Documentation as Source of Truth
Architecture, standard ve roadmap dokümanları kararlar kesinleşince güncellenir.

Memory yerine repository source of truth olarak kullanılır.

## 48. Anti-Pattern'ler
- architecture yalnızca README'de
- forbidden dependency'nin manuel review'e bırakılması
- her rule'u annotation ile çözmek
- quality gate'i sırf yüzde için kullanmak
- ADR silmek
- known debt'i gizlemek
- architecture test'i çok yavaş yapmak
- exception'ı sessizce kalıcılaştırmak

## 49. Architecture Fitness Review Checklist
- Dependency direction enforce ediliyor mu?
- Forbidden dependency otomatik testte mi?
- Cycle var mı?
- Module boundary korunuyor mu?
- Shared library coupling yaratıyor mu?
- ADR gerekli mi?
- Static analysis gate var mı?
- Breaking contract change tespit ediliyor mu?
- Docs ile code uyumlu mu?
- Architecture drift oluşmuş mu?