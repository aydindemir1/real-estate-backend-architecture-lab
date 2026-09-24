# Spring Boot Engineering Standard

Bu doküman projede Spring Boot kullanımına ilişkin engineering standard'larını tanımlar.

Amaç Spring Boot annotation'larını rastgele kullanmak değil; dependency injection, configuration, transaction, validation, web, data, security ve testing davranışlarını framework semantics'iyle uyumlu ve öngörülebilir hale getirmektir.

## 1. Constructor Injection
- Field injection kullanılmaz.
- Constructor injection default yaklaşımdır.
- Dependency explicit görünür.
- Testability artar.

## 2. Optional Dependency
Optional bean dependency design smell olabilir.
`Optional<SomeBean>` injection yerine capability'nin gerçekten optional olup olmadığı tasarlanır.

## 3. Bean Scope
Default singleton bean stateless olmalıdır.

Mutable shared state singleton bean içinde tutulmaz.

## 4. Bean Lifecycle
`@PostConstruct` ağır business initialization için kullanılmaz.

Startup logic gerekiyorsa ApplicationRunner/CommandLineRunner bilinçli kullanılabilir.

## 5. Configuration Class
`@Configuration` sınıfları coherent infrastructure configuration taşımalıdır.

GodConfiguration oluşturulmaz.

## 6. @Bean
Framework/external library object'leri bean olarak açıkça tanımlamak için uygundur.

Business class sırf bean yapmak için configuration class içinde new edilmez, gerekiyorsa component scanning veya explicit wiring kullanılır.

## 7. @ConfigurationProperties
Configuration value'lar typed object olarak bağlanır.

Tercih:
- immutable record/class
- validation
- prefix-based grouping

Dağınık `@Value` kullanımı azaltılır.

## 8. @Value
Küçük, tekil legacy config için kabul edilebilir.

Complex config için `@ConfigurationProperties` tercih edilir.

## 9. Profiles
Profile environment-specific behavior için sınırlı kullanılır.

Kaçınılacak:
- business behavior'ı profile ile değiştirmek
- yüzlerce profile conditional

Tercih:
- local
- test
- dev
- prod-like

## 10. Config precedence
Environment variable, Config Server, application yaml/properties precedence bilinçli olmalıdır.

## 11. Secret
Secret application.yml içinde commit edilmez.

Vault / environment / secret store kullanılır.

## 12. Auto-Configuration Awareness
Spring Boot auto-configuration neyin neden açıldığını bilmeden kör kullanılmaz.

Critical infra için condition report/debug gerektiğinde incelenir.

## 13. Starter kullanımı
Starter convenience sağlar ama gereksiz starter dependency eklenmez.

## 14. Component Scanning
Package root dar ve service boundary'ye uygun olmalıdır.

Cross-module accidental component scan engellenir.

## 15. Stereotype Annotation
`@Service`, `@Repository`, `@Component`, `@Controller` semantik anlamına göre kullanılır.

Her class `@Component` yapılmaz.

## 16. Domain Framework Independence
Clean/Hexagonal/Onion domain package'lerinde Spring annotation kullanılmaz.

## 17. Validation
Boundary validation için Jakarta Bean Validation kullanılır.

Örnek:
- @NotNull
- @NotBlank
- @Positive
- @Size

Business invariant Bean Validation'a gömülmez.

## 18. Custom Validator
Cross-field syntactic validation için custom constraint olabilir.

Domain rule için domain model tercih edilir.

## 19. @Validated
Method-level validation gerçekten gerekiyorsa.

## 20. Spring MVC
Controller thin kalır.

Controller yalnızca:
- request mapping
- validation
- auth context
- use-case invocation
- response mapping

## 21. ResponseEntity
Status/header control gerekiyorsa kullanılır.

Her endpoint zorunlu olarak ResponseEntity dönmek zorunda değildir.

## 22. @RestControllerAdvice
Global error mapping için kullanılır.

Domain/application exception -> API error contract burada map edilir.

## 23. Error Leakage
Stack trace, DB detail, internal class name client'a dönmez.

## 24. MessageSource
Internationalized validation/error message gerekiyorsa değerlendirilebilir.

## 25. Jackson
Transport serialization boundary'dir.

Domain model doğrudan Jackson annotation ile doldurulmaz.

## 26. Jackson Date
ISO-8601 default.

## 27. Unknown JSON Field
Backward compatibility policy'ye göre ignore/fail davranışı açık olmalıdır.

## 28. Spring Data
Spring Data repository infrastructure boundary'dir.

Domain/business logic repository interface'e sızmaz.

## 29. Derived Query
Basit query için method-name derived query uygundur.

Complex query için readability düşüyorsa custom repository/query tercih edilir.

## 30. @Query
Complex ama stable query için kullanılabilir.

Raw persistence query business service içine konmaz.

## 31. Projection
Read-only optimized query için projection değerlendirilebilir.

## 32. Pagination
`Pageable` external API contract'a direkt expose edilmemelidir.

Public API kendi pagination contract'ını taşır.

## 33. Entity Manager
JPA infrastructure'da gerektiğinde kullanılabilir.

Application layer EntityManager bilmez.

## 34. Open Session in View
OSIV default davranışı bilinçli değerlendirilmelidir.

Production-like API service'lerde lazy loading'i controller serialization'a bırakmamak için kapatılması tercih edilir.

## 35. @Transactional
Application service/handler seviyesinde.

Domain'e konmaz.

Controller'a konmaz.

## 36. Transaction Propagation
Default REQUIRED çoğu use-case için yeterlidir.

REQUIRES_NEW gibi propagation yalnızca güçlü gerekçe varsa.

## 37. Transaction ReadOnly
Read-only transaction query use-case'lerde kullanılabilir.

Bu tek başına DB optimization garantisi değildir.

## 38. Transaction Rollback
Runtime exception default rollback semantics bilinmelidir.

Checked exception rollback gerekiyorsa explicit policy.

## 39. Proxy Semantics
`@Transactional`, `@Async`, method security gibi annotation'lar proxy tabanlıdır.

Self-invocation caveat bilinmelidir.

## 40. Self Invocation
Aynı bean içinden `this.someTransactionalMethod()` çağrısı proxy interception'ı bypass edebilir.

Design buna göre yapılır; annotation magic'e güvenilmez.

## 41. Final Method/Class Caveat
Proxy strategy ile final method/class behavior bilinmelidir.

## 42. @Async
Default olarak kullanılmaz.

Gerçek async requirement varsa executor, context propagation, error handling açık olmalıdır.

## 43. Executor
Custom executor explicit bean/config üzerinden yönetilir.

## 44. Scheduling
`@Scheduled` maintenance/task use-case için kullanılabilir.

Critical distributed job coordination gerekiyorsa single-node assumption yapılmaz.

## 45. Spring Cloud Task
Short-lived task için Spring Cloud Task ileride tercih edilebilir.

## 46. Application Events
Spring ApplicationEvent yalnızca in-process event'tir.

Kafka/RabbitMQ integration event yerine geçmez.

## 47. Event Listener
`@EventListener` domain/integration semantics'i karıştırmamalıdır.

## 48. Transactional Event Listener
`@TransactionalEventListener` local transaction phase için kullanılabilir.

Outbox yerine reliability garantisi varsayılmaz.

## 49. Actuator
Minimum useful endpoints:
- health
- info
- metrics
- prometheus gerektiğinde

Sensitive endpoint public olmaz.

## 50. Health Indicator
Custom health indicator sadece gerçekten operational value sağlıyorsa.

## 51. Readiness/Liveness
Kubernetes semantics'ine uygun tasarlanır.

## 52. Metrics
Micrometer standard instrumentation.

Custom metric low-cardinality label policy'ye uyar.

## 53. Logging
SLF4J facade kullanılır.

System.out.println kullanılmaz.

## 54. MDC
Correlation context için kullanılabilir.

Async/virtual thread context propagation caveat'leri bilinmelidir.

## 55. HTTP Client
OpenFeign mevcut standard.

Client timeout/retry config explicit olmalıdır.

## 56. RestClient/WebClient
Yeni internal HTTP client gerekiyorsa use-case'e göre değerlendirilebilir.

Reactive stack'e geçiş anlamına gelmez.

## 57. Feign Error Decoder
External error application semantic'e translate edilir.

## 58. Feign Retry
Default/implicit retry'ya güvenilmez.

Retry ownership resilience standardına göre explicit.

## 59. Connection Pool
Underlying HTTP client pool config gözlemlenir.

## 60. Multipart
Bu scope'ta primary değil.

Eklenirse size limit/config zorunlu.

## 61. File Upload Security
Type/size/content validation.

## 62. CORS
Centralized security/edge policy.

## 63. Spring Security
Filter chain açık ve minimal olmalı.

Authorization rule'lar readable olmalıdır.

## 64. SecurityFilterChain
Deprecated config style kullanılmaz.

## 65. Method Security
`@PreAuthorize` coarse-grained authorization için.

Ownership logic annotation expression içine aşırı gömülmez.

## 66. Resource Server
JWT validation issuer/audience/scope policy ile.

## 67. PasswordEncoder
Legacy/local auth varsa explicit bean.

Plain comparison yok.

## 68. CSRF
Stateless bearer token API kullanım modeline göre disable edilebilir.

Cookie auth varsa yeniden değerlendir.

## 69. SessionCreationPolicy
Stateless API için STATELESS.

## 70. ObjectMapper Bean
Global customization centralized ve kontrollü.

Per-service random ObjectMapper oluşturulmaz.

## 71. Configuration Validation
`@ConfigurationProperties` `@Validated` ile startup fail-fast.

## 72. Startup Fail Fast
Critical invalid config startup'ta fail etmelidir.

## 73. Optional Dependency Degradation
Non-critical dependency unavailable ise startup behavior bilinçli olmalıdır.

## 74. Graceful Shutdown
Spring graceful shutdown config kullanılacaktır.

## 75. Shutdown Timeout
In-flight request/message tamamlanma budget'ı explicit.

## 76. Thread Model
Spring MVC blocking model baseline.

Virtual thread enablement ayrı benchmark/design decision.

## 77. Virtual Threads with Spring Boot
Spring Boot support available olsa bile default enable edilmez.

DB pool/downstream capacity ölçülür.

## 78. Reactive Stack
WebFlux eklenmez, gerçek reactive use-case yoksa.

## 79. Test Slice
Candidate:
- @WebMvcTest
- @DataJpaTest
- @JsonTest

Full context yalnızca gerektiğinde.

## 80. @SpringBootTest
Integration/E2E composition için.

Her unit test'te kullanılmaz.

## 81. MockBean / MockitoBean
Framework context test'te boundary dependency replace etmek için sınırlı.

Domain unit test için plain Mockito/fake yeterli.

## 82. Testcontainers Integration
`@ServiceConnection` gibi Spring Boot convenience özellikleri değerlendirilebilir.

Explicitness kaybolmamalıdır.

## 83. Dynamic Property
Testcontainer endpoint config için dynamic property registration kullanılabilir.

## 84. Slice Test Limitation
Slice test production wiring'in tamamını doğrulamaz.

## 85. Context Caching
Test speed için Spring test context cache'i gereksiz kirletilmez.

## 86. DirtiesContext
Default çözüm değildir.

## 87. Auto Configuration Test
Custom starter/config yazılırsa ApplicationContextRunner değerlendirilebilir.

## 88. Bean Override
Production config test için hacklenmez.

## 89. ApplicationRunner
Business migration logic startup runner içine gömülmez.

## 90. Database Migration
Flyway/Liquibase startup ordering bilinçli.

## 91. ddl-auto
Production-like profile'da validate/none.

update kullanılmaz.

## 92. Initialization Script
schema.sql/data.sql yalnızca uygun environment/test use-case.

## 93. Exception Translation
`@Repository` persistence exception translation sağlar.

Application API'ye doğrudan DataAccessException sızdırılmaz.

## 94. Bean Validation Message
Client-facing message stable code'un yerine geçmez.

## 95. HttpMessageConverter
Custom converter yalnızca gerçek media type use-case.

## 96. API Version
Gateway/public route `/api/v1` standardı.

Controller package içinde version duplication gerekmeden routing strategy olabilir.

## 97. OpenAPI
SpringDoc documentation contract ile uyumlu.

## 98. OpenAPI Annotation
Business code'u annotation noise ile doldurmamak için gerektiğinde interface/config separation.

## 99. Management Port
Operational endpoint'ler için ayrı management port ileri ortamda değerlendirilebilir.

## 100. Spring Boot Review Checklist
- Constructor injection var mı?
- Bean stateless mı?
- Config typed mı?
- Secret dışarıda mı?
- Domain Spring annotation taşıyor mu?
- Controller thin mi?
- Validation boundary doğru mu?
- Transaction boundary application layer'da mı?
- Proxy/self-invocation riski var mı?
- OSIV bilinçli mi?
- Spring Data query maintainable mı?
- Exception global contract'a map ediliyor mu?
- Actuator secure mu?
- Graceful shutdown var mı?
- Timeout/retry explicit mi?
- Test doğru slice seviyesinde mi?
- Auto-configuration davranışı biliniyor mu?