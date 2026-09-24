# Java 21 Engineering Standard

Bu doküman projede Java 21 kullanımına ilişkin language-level engineering standard'larını tanımlar.

Amaç yalnızca modern Java syntax kullanmak değil; okunabilir, type-safe, immutable olduğu yerde immutable, concurrency-safe ve framework'ten bağımsız core code üretmektir.

## 1. Java 21 baseline
- Source/target Java 21.
- Unsupported preview feature production-like code'a alınmaz.
- Language feature sırf yeni olduğu için kullanılmaz; readability ve correctness önceliklidir.

## 2. var policy
- Local variable type inference yalnızca type açıkça anlaşılabiliyorsa kullanılır.
- Complex generic/stream chain sonucunda var okunabilirliği düşürüyorsa explicit type tercih edilir.
- Public API, field veya parameter type için var kullanılamaz.

## 3. record kullanımı
record iyi candidate:
- immutable DTO
- Command
- Query
- Result
- Value Object, invariant basitse
- event payload

record kullanılmamalı:
- mutable Aggregate
- JPA entity
- lifecycle/state behavior yoğun entity

Compact constructor ile invariant doğrulanabilir.

## 4. Sealed types
Closed hierarchy gerçekten business domain tarafından sınırlandırılmışsa sealed interface/class kullanılabilir.

Candidate:
- type-specific Property attributes
- closed result/error hierarchy

Dynamic plugin/extensibility gereken yerde sealed kullanılmaz.

## 5. Enum
Finite domain state için enum tercih edilir.

Örnek:
- PropertyStatus
- OfferStatus
- AgentStatus

Enum yalnızca constant bag değil, küçük state-specific behavior taşıyabilir.

## 6. Switch expression
Exhaustive enum/sealed type branching için switch expression tercih edilebilir.

Default branch gerçek model hatasını gizliyorsa kullanılmaz.

## 7. Pattern matching
instanceof pattern matching type cast noise azaltmak için kullanılabilir.

Type branching business polymorphism yerine aşırı kullanılırsa design smell olabilir.

## 8. Immutability
Default yaklaşım:
- immutable Value Object
- immutable Command/Query/Result
- defensive collection exposure

Mutable state yalnızca lifecycle gerektiren Aggregate/Entity'de kontrollü behavior üzerinden.

## 9. final
Field'lar mümkün olduğunca final.

Method/local variable için her yerde final zorunluluğu yoktur; noise yaratmamalıdır.

## 10. Collection immutability
Mutable internal collection dışarı expose edilmez.

Tercih:
- List.copyOf
- Set.copyOf
- Map.copyOf

Collections.unmodifiableX view ile copy semantics farkı bilinmelidir.

## 11. Collection type seçimi
Public contract mümkün olan en genel anlamlı interface'i kullanır:
- List
- Set
- Map

Concrete ArrayList/HashMap API contract olarak expose edilmez.

## 12. Set semantic
Uniqueness domain semantic ise Set düşünülebilir.

Sadece duplicate çıkmasın diye order semantics'i bozacak şekilde Set kullanılmaz.

## 13. Map semantic
Map key business semantic'i açık olmalıdır.

Map'i typed object yerine schema-less data bag olarak kullanmaktan kaçınılır.

## 14. Stream API
Stream declarative transformation için uygundur.

İyi use-case:
- map/filter/reduce
- immutable transformation

Kötü use-case:
- side-effect ağırlıklı pipeline
- complex branching
- exception handling karmaşası
- debug edilmesi zor 10+ step chain

## 15. Stream side-effect
forEach içinde persistence/event publish gibi side-effect mümkün olduğunca kaçınılır.

## 16. Parallel Stream
Default olarak kullanılmaz.

Threading, ForkJoinPool ve blocking behavior bilinmeden parallelStream kullanılmaz.

## 17. Optional
Optional return type'ta absent value semantic'i için kullanılabilir.

Kaçınılacak:
- entity field
- DTO field
- method parameter
- collection içinde Optional

Optional.get() kör kullanılmaz.

## 18. Null policy
- Public/application contract mümkün olduğunca null-free.
- required value constructor/factory'de doğrulanır.
- collection için null yerine empty collection.
- null business semantic taşıyorsa açık modellenir.

## 19. Objects.requireNonNull
Low-level programmer error/precondition için kullanılabilir.

Domain validation için semantic exception/Value Object rule daha anlamlı olabilir.

## 20. Equality
Entity equality ile Value Object equality ayrılır.

Value Object:
- tüm anlamlı immutable field'lar

Entity:
- stable identity

Mutable field equals/hashCode içine alınmaz.

## 21. JPA equals/hashCode
JPA proxy/generated ID caveat'leri nedeniyle entity equality bilinçli tasarlanır.

AgentService domain entity ile JPA entity ayrı olduğu için domain equality framework'ten bağımsız kalır.

## 22. BigDecimal
Money için float/double kullanılmaz.

Kurallar:
- BigDecimal(String) veya valueOf
- scale/rounding açık
- compareTo semantic'i bilinçli
- equals scale-sensitive olduğu bilinmeli

## 23. Currency
Currency code ISO-4217.

Money = amount + currency.

## 24. java.time
Tercih:
- Instant: persisted/system timestamp
- LocalDate: timezone-independent calendar date
- LocalDateTime yalnızca timezone gerçekten anlamsızsa
- ZonedDateTime/OffsetDateTime external timezone requirement varsa

Legacy Date/Calendar kullanılmaz.

## 25. UTC
System timestamp UTC.

API ISO-8601.

## 26. Clock
Time-dependent business logic test edilebilir olmalıdır.

Clock injection candidate:
- offer expiry
- hold timeout
- activity timestamp

## 27. Duration
Timeout/TTL numeric millis olarak dağılmamalı.

Duration kullanılır.

## 28. UUID
Opaque identifier olarak kullanılabilir.

UUID creation domain'e infrastructure coupling yaratmıyorsa static generation kabul edilebilir; testability/business requirement varsa IdGenerator abstraction değerlendirilebilir.

## 29. Generics
Generic abstraction gerçek type-safety sağlıyorsa kullanılır.

Kaçınılacak:
- generic BaseService<T, ID>
- generic domain framework
- unreadable nested generic hierarchy

## 30. Wildcards
PECS:
- Producer extends
- Consumer super

API readability bozuluyorsa generic design yeniden değerlendirilir.

## 31. Raw type
Raw generic type kullanılmaz.

## 32. Exception hierarchy
Checked exception default tercih değildir.

Business/application exception semantic runtime exception olabilir.

Checked exception yalnızca caller'ın compile-time recover kararı gerçek değer taşıyorsa değerlendirilir.

## 33. Catch policy
Catch yalnızca:
- recover
- translate
- enrich context
- cleanup
için.

Catch-and-ignore yok.

## 34. Exception translation
Infrastructure exception application/domain semantic'e çevrilebilir.

Örnek:
OptimisticLockingFailureException -> PropertyConcurrentModificationException.

## 35. InterruptedException
Thread interrupt swallow edilmez.

Catch edilirse interrupt status restore edilir veya uygun şekilde propagate edilir.

## 36. Try-with-resources
AutoCloseable resource için zorunlu tercih.

## 37. String
String concatenation loop içinde yoğun ise StringBuilder.

Log için string concatenation yerine parameterized logging.

## 38. Text blocks
Multi-line test fixture, SQL veya JSON için readability artırıyorsa kullanılabilir.

Production SQL büyük text block olarak service içine gömülmez.

## 39. Formatter
String.format yerine readability/performance ihtiyacına göre formatted() kullanılabilir.

## 40. Static utility
Pure stateless helper küçük ve coherent ise static utility olabilir.

Utility dumping ground yasaktır.

## 41. Lombok policy
Lombok tamamen yasak değildir ancak bilinçli kullanılır.

Allowed candidate:
- @Slf4j
- @Getter sınırlı
- @RequiredArgsConstructor framework necessity varsa

Kaçınılacak:
- @Data domain entity'de
- @Setter aggregate'da
- @Builder invariant bypass eden domain model'de
- @EqualsAndHashCode JPA/domain equality düşünmeden

## 42. Constructor injection
Spring tarafında explicit constructor tercih edilir.

Lombok constructor convenience readability'yi düşürmüyorsa kullanılabilir; core design'i gizlememeli.

## 43. Serialization boundary
Domain object doğrudan JSON/Kafka serialization'a bağlanmaz.

Transport DTO/payload ayrı.

## 44. Serializable
java.io.Serializable default olarak kullanılmaz.

Framework zorunluluğu yoksa eklenmez.

## 45. Reflection
Application/business code'da custom reflection framework oluşturulmaz.

Framework reflection behavior bilinmeli ama domain design reflection'a dayandırılmaz.

## 46. Annotations
Annotation behavior'ı görünmez magic haline getirmemelidir.

Critical behavior yalnızca custom annotation ile saklanmaz.

## 47. Concurrency baseline
Mutable shared state minimize edilir.

Thread-safety varsayılmaz.

## 48. synchronized
İn-process lock yalnızca aynı JVM içi coordination sağlar.

Distributed consistency çözümü değildir.

## 49. Atomic types
AtomicInteger/AtomicReference lock-free atomic state gerektiğinde.

Business datastore consistency yerine kullanılmaz.

## 50. Concurrent collections
ConcurrentHashMap gibi yapılar yalnızca gerçekten shared concurrent in-memory state varsa.

Canonical business state local memory'de tutulmaz.

## 51. Virtual Threads
Java 21 virtual threads blocking I/O workload için güçlü candidate'dır.

Ancak projede default olarak her service'te açılmayacaktır.

Değerlendirme kriterleri:
- blocking stack kullanılıyor mu?
- thread-per-request bottleneck var mı?
- library pinning/blocking behavior?
- connection pool gerçek bottleneck mi?
- observability/tooling uyumu?

Day 7 foundation için zorunlu değildir.

## 52. Virtual Thread caveat
Virtual thread sayısını artırmak DB connection pool kapasitesini artırmaz.

Downstream saturation korunmalıdır.

## 53. Platform thread
CPU-bound workload veya bounded executor gereken yerde platform thread pool uygun olabilir.

## 54. Executor
Executor creation scattered yapılmaz.

Lifecycle Spring/configuration tarafından yönetilir.

## 55. CompletableFuture
Gerçek independent asynchronous composition varsa kullanılabilir.

Kaçınılacak:
- sync code'u sadece modern görünsün diye CompletableFuture'a sarmak
- common ForkJoinPool'a blocking work atmak

## 56. CompletableFuture exception handling
exceptionally/handle/whenComplete semantic farkları bilinçli kullanılır.

Exception swallow edilmez.

## 57. Structured Concurrency
Java 21'de preview durumundaki feature'lar production-like code baseline'a alınmaz.

Stable olduğunda ileride değerlendirilebilir.

## 58. ThreadLocal
Manual ThreadLocal kullanımından kaçınılır.

Virtual thread / async boundary context propagation açısından risklidir.

Tracing/security context framework mekanizmaları tercih edilir.

## 59. Synchronization boundary
Distributed service concurrency:
- optimistic locking
- broker ordering
- idempotency
- datastore atomicity
ile çözülür.

## 60. Blocking call awareness
REST, JDBC/JPA, Spring Data gibi blocking API'ler bilinçli kullanılır.

Reactive stack sırf non-blocking olsun diye projeye eklenmez.

## 61. Reactive vs Imperative
Bu proje primary olarak imperative Spring MVC stack'tir.

Reactive programming ayrı learning objective olmadığı sürece eklenmez.

## 62. Memory allocation
Premature object pooling yapılmaz.

GC pressure metric/profile ile doğrulanır.

## 63. Defensive copying
Mutable input collection constructor'da copy edilir.

## 64. Arrays
Public API'de array yalnızca binary/fixed-semantics gerekiyorsa.

Collection çoğu business contract için daha uygundur.

## 65. Locale
String lowercase/uppercase business normalization'da Locale.ROOT gerektiğinde kullanılır.

## 66. Charset
Explicit UTF-8 tercih edilir.

Default platform charset'a güvenilmez.

## 67. Number parsing
User input parsing boundary'de yapılır; domain'e String number taşınmaz.

## 68. Validation helper
Generic ValidationUtils dumping ground oluşturulmaz.

Rule mümkünse Value Object/Aggregate'a yakın.

## 69. Method design
- az parameter
- clear return type
- no boolean flag explosion
- side-effect açık

## 70. Boolean flag
Method behavior ciddi değişiyorsa boolean param yerine separate method/strategy.

## 71. Return null
Collection için null dönülmez.

Single absent value için Optional veya explicit not-found semantic.

## 72. Record DTO evolution
Record constructor positional coupling nedeniyle çok büyük public DTO record'ları dikkatli kullanılmalıdır.

Transport layer'da serialization compatibility kontrol edilir.

## 73. Sealed error/result hierarchy
Closed result set varsa sealed interface readability artırabilir.

Ancak exception/error model zaten yeterliyse ayrıca hierarchy zorlanmaz.

## 74. Annotation processor
MapStruct/Lombok annotation processing build reproducibility kapsamında yönetilir.

## 75. MapStruct
Boundary mapping value kattığı yerde.

Complex business mapping mapper'a gömülmez.

## 76. Equals/HashCode tests
Critical Value Object equality test edilir.

## 77. Immutability tests
Value Object invariant ve collection defensive copy davranışı test edilir.

## 78. JavaDoc
Public/internal API self-explanatory ise JavaDoc zorunlu değil.

JavaDoc gerekli:
- non-obvious contract
- concurrency guarantee
- tricky lifecycle
- public library-like interface

## 79. TODO/FIXME
Kalıcı design debt TODO ile gizlenmez; issue/roadmap/ADR.

## 80. Deprecated
Deprecated API'nin replacement ve removal planı olmalıdır.

## 81. Package naming
Lowercase, business/architecture oriented.

Generic misc/common/util package büyümesine izin verilmez.

## 82. Access modifier
Minimum visibility.

public yalnızca gerektiğinde.

## 83. Static mutable state
Yasak.

## 84. Constants
Business constant typed Value Object/enum/config olabilir.

Magic string/number dağılmaz.

## 85. Configuration value
Timeout, TTL, retry count code constant olarak hard-code edilmez; @ConfigurationProperties tarafında yönetilir.

## 86. Determinism
Domain test'leri random/time/global state bağımlı olmamalı.

## 87. Random
Business random gereksinimi varsa generator abstraction veya injected source değerlendirilebilir.

## 88. Security-sensitive random
Token/secret üretiminde SecureRandom.

## 89. Hashing
Security hashing için custom algorithm yazılmaz.

## 90. Class size
Satır sayısı tek başına kalite metriği değildir.

Responsibility cohesion önceliklidir.

## 91. Method reference/lambda
Readability artırıyorsa.

Complex lambda named method'a çıkarılır.

## 92. Functional interface
Gerçek behavior abstraction varsa.

Her tek-method interface sırf lambda için oluşturulmaz.

## 93. Comparator
Sorting business semantics explicit comparator ile modellenebilir.

## 94. Records and persistence
JPA entity için record kullanılmaz.

Couchbase/Mongo persistence model framework compatibility kontrol edilmeden record'a çevrilmez.

## 95. Serialization date format
Transport DTO'da ISO-8601.

Custom formatter yalnızca contract gerektirirse.

## 96. Decimal scale
Money scale/currency-specific rule açık olmalı.

Blind setScale her yerde uygulanmaz.

## 97. Exception message
Developer/debug context taşır.

Client error contract ayrı stable code üzerinden.

## 98. Cause preservation
Exception translate edilirken root cause kaybedilmez.

## 99. Suppressed exception
Resource cleanup behavior bilinmeli; try-with-resources tercih edilmelidir.

## 100. Java 21 Review Checklist
- record burada doğru mu?
- sealed hierarchy gerçek closed set mi?
- Value Object immutable mı?
- Optional doğru yerde mi?
- null semantic açık mı?
- equals/hashCode doğru mu?
- BigDecimal doğru mu?
- Instant/Clock kullanımı doğru mu?
- Stream okunabilir mi, side-effect var mı?
- parallelStream gereksiz mi?
- Lombok invariant'ı gizliyor mu?
- exception semantic doğru mu?
- interrupt swallow ediliyor mu?
- concurrency shared-state yaratıyor mu?
- virtual thread gerçekten fayda sağlıyor mu?
- CompletableFuture gerçek async composition mı?
- domain serialization/framework detayına bağlı mı?
- public visibility minimum mu?