# Build, Dependency & Configuration Governance

Bu doküman projenin build, dependency management, Gradle multi-module yapısı ve configuration governance standartlarını tanımlar.

## 1. Amaç
- reproducible build
- predictable dependency graph
- environment-independent artifact
- config/secret separation
- CI/local parity
- version drift kontrolü

## 2. Gradle Wrapper
- Her build Gradle Wrapper ile çalıştırılır.
- Developer local Gradle installation'a bağımlı değildir.
- Wrapper version repository'de version-controlled.

## 3. Wrapper upgrade
Gradle upgrade ayrı controlled change olarak yapılır.

Business feature ile Gradle major upgrade aynı PR'da karıştırılmaz.

## 4. Multi-module structure
Root build common convention taşır.

Service module kendi runtime dependency'sini seçer.

Root her service'e gereksiz starter inject etmez.

## 5. Root responsibility
Root candidate:
- plugin version management
- repository definitions
- Java toolchain
- testing conventions
- common quality plugins

## 6. Module responsibility
Module:
- own application dependency
- datastore driver
- messaging client
- service-specific plugin/config

taşır.

## 7. Convention Plugin
Repeated Gradle config büyürse buildSrc veya included build convention plugin değerlendirilebilir.

Premature build framework oluşturulmaz.

## 8. Java Toolchain
Java 21 toolchain explicit tanımlanır.

Developer machine JDK drift azaltılır.

## 9. Dependency BOM
Spring Boot dependency management primary baseline.

Spring Cloud için Spring Cloud BOM kullanılır.

## 10. Version Alignment
Spring ecosystem dependency version'ları compatible release train/BOM ile align edilir.

Random per-module version pin edilmez.

## 11. Explicit version
Spring BOM dışında kalan library version explicit ve central yönetilebilir.

## 12. Version Catalog
Gradle version catalog dependency set büyürse değerlendirilebilir.

Tek source of truth sağlar.

## 13. Dependency locking
Reproducibility ihtiyacı için Gradle dependency locking candidate.

Lockfile update intentional change olmalıdır.

## 14. Dynamic version
`1.+`, `latest.release`, SNAPSHOT dependency production-like build'de kullanılmaz.

## 15. Snapshot
Internal learning artifact dışında minimize edilir.

## 16. Repositories
Trusted repository list explicit.

Untrusted random Maven repository eklenmez.

## 17. Repository ordering
Repository resolution order bilinçli.

## 18. Maven Central
Primary public dependency source.

## 19. Nexus future
CI/CD fazında proxy/hosted repository olarak Nexus kullanılacaktır.

## 20. Transitive dependency
Critical dependency transitive diye bilinmeden bırakılmaz.

Dependency insight ile graph incelenir.

## 21. Dependency conflict
Force/strictly resolution son çare.

Önce BOM/alignment ile çözülür.

## 22. Exclusion
Transitive exclusion gerekçeli olmalı.

Blind exclude yapılmaz.

## 23. Duplicate library
Aynı capability için iki logging/json/http library gereksiz taşınmaz.

## 24. Dependency scope
implementation/api/runtimeOnly/testImplementation doğru seçilir.

## 25. Runtime driver
JDBC driver runtimeOnly olabilir.

## 26. Test dependency
Production artifact test library taşımaz.

## 27. Annotation Processor
Lombok/MapStruct processor config central ve deterministic.

## 28. Generated code
Generated source build output'tur; gerekiyorsa proto/client code policy ayrı.

## 29. Reproducible build
Aynı source + lock/config -> aynı dependency set.

Build timestamp/random data artifact içeriğini gereksiz değiştirmemelidir.

## 30. Build cache
Gradle build cache local/CI için değerlendirilebilir.

Cache correctness priority.

## 31. Parallel build
Module independence sağlanıyorsa Gradle parallel execution değerlendirilebilir.

## 32. Build performance
Build tuning ölçülerek.

## 33. CI command
CI ve local mümkün olduğunca aynı wrapper task'larını kullanır.

Örnek:
`./gradlew clean check`

## 34. Clean task
Her local build için clean zorunlu değildir.

CI clean environment zaten sağlayabilir.

## 35. Build lifecycle
Candidate:
- compile
- test
- integrationTest
- architectureTest
- check
- bootJar

## 36. Integration test source set
Integration test sayısı büyürse ayrı source set/task.

## 37. Architecture test source set
ArchUnit ayrı task veya test tag ile ayrılabilir.

## 38. Test tagging
Unit/integration/contract/e2e tag/classification kullanılabilir.

## 39. Failing tests
Test failure ignored edilmez.

## 40. Warnings
Compiler warning policy kademeli sıkılaştırılabilir.

## 41. Configuration hierarchy
Config source precedence açık olmalıdır.

Örnek mantık:
defaults -> application config -> profile config -> Config Server -> environment override -> runtime secret source.

Actual Spring precedence implementation sırasında doğrulanır.

## 42. application.yml
Safe default ve local-neutral config.

Secret içermez.

## 43. Profile files
Environment-specific non-secret override.

Profile proliferation yapılmaz.

## 44. Environment variable
Deployment-time override için.

Secret için geçici/local olabilir; Vault sonrası primary secret source değildir.

## 45. Config Server
Distributed non-secret config.

## 46. Vault
Secret.

## 47. ConfigMap
Kubernetes fazında platform config.

Config Server ile overlap yeniden değerlendirilir.

## 48. Secret
- repository'de yok
- Docker image içinde yok
- log'da yok
- sample config'de fake/example value olabilir

## 49. .env
Local developer convenience için olabilir.

Real secret file gitignored.

## 50. Sample config
`.env.example` veya example yaml gerçek credential içermez.

## 51. Config validation
Startup'ta typed validation.

Missing critical config fail-fast.

## 52. Config naming
Consistent prefix.

Örnek:
`app.messaging.*`
`app.security.*`
`app.resilience.*`

## 53. Duration config
String/Duration semantic:
`500ms`, `2s` gibi typed binding.

Magic millisecond yok.

## 54. Size config
DataSize typed config gerektiğinde.

## 55. Boolean config
Feature toggle için limited.

Business workflow permanent branching'i config flag'a dönüştürülmez.

## 56. Feature flag
Gerçek rollout/experimentation ihtiyacı yoksa dedicated platform eklenmez.

## 57. Port config
Service port environment configurable.

## 58. Hostname
Hard-coded localhost production code/config yok.

Local profile'da olabilir.

## 59. Service URL
Discovery kullanılan yerde hard-coded URL yok.

## 60. Datastore config
URI/host/credential/pool/timeout grouped configuration.

## 61. Broker config
Bootstrap/server/queue/topic/consumer group explicit.

## 62. Security config
Issuer/client id/scope typed.

Client secret secret source.

## 63. Resilience config
Per-dependency property group.

## 64. Observability config
OTLP endpoint, sampling, service name environment aware.

## 65. Logging config
Level environment-specific olabilir.

Production-like DEBUG default olmaz.

## 66. Local profile
Developer convenience sağlar.

Production semantics'i tamamen farklı hale getirmez.

## 67. Test profile
Testcontainers endpoint'leri test runtime sağlar.

Hard-coded shared test DB kullanılmaz.

## 68. Dev profile
Team-shared development environment candidate.

## 69. Prod-like profile
Secure defaults:
- ddl-auto update yok
- debug yok
- actuator restricted
- secret external

## 70. Config drift
Environment config farkları görünür ve version-controlled olmalıdır mümkün olduğunca.

## 71. Config change review
Critical timeout/retry/security config code review kadar önemlidir.

## 72. Runtime refresh
Refreshable property list explicit.

## 73. Immutable config at startup
DB driver/pool gibi config restart gerektirebilir.

## 74. Build metadata
Artifact version, git commit candidate info endpoint/telemetry'de bulunabilir.

## 75. Versioning
Project version semantic veya milestone-based olabilir.

Snapshot/release policy ileriki CI/CD fazında netleşir.

## 76. Docker build input
Artifact deterministic build output'tan alınır.

Local IDE artifact'a bağımlı Docker build yok.

## 77. Dependency vulnerability
Dependency scanning CI/CD fazında.

## 78. SBOM
İleri DevSecOps fazında Software Bill of Materials üretimi değerlendirilebilir.

## 79. License awareness
Third-party dependency license awareness ileride automated olabilir.

## 80. Plugin governance
Gradle plugin version'ları central ve controlled.

## 81. Plugin minimization
Her convenience için plugin eklenmez.

## 82. Repositories mode
Central repository declaration enforce edilebilir.

## 83. Dependency verification
Gradle dependency verification/checksum ileri hardening candidate.

## 84. Offline/cache behavior
CI reproducibility remote repo transient failure'dan tamamen bağımsız değildir; Nexus sonrası iyileşir.

## 85. Build secret
Repository publishing credential Gradle file'a yazılmaz.

## 86. Gradle properties
Non-secret build tuning için.

Sensitive value user home/env/secret store.

## 87. JVM args
Build JVM memory tuning central, ölçülü.

## 88. Application JVM args
Runtime JVM options deployment concern.

## 89. GC flags
Default-first; profiling sonrası.

## 90. Environment parity
Local Docker Compose, CI Testcontainers, production-like topology semantic olarak yakın olmalı.

## 91. Compose config
Local infra version pin edilir.

`latest` image kullanılmaz.

## 92. Container image version
PostgreSQL/MySQL/Kafka vb image version explicit.

## 93. Port collision
Local port mapping documented.

## 94. Config documentation
Her service DESIGN/README gerekli env/config key'lerini belgelemeli.

## 95. Default value
Security/critical timeout için unsafe default verilmez.

## 96. Fail-open vs fail-closed
Security config eksikse fail-closed/fail-fast.

## 97. Build anti-pattern'leri
- global local Gradle'a bağımlılık
- dynamic dependency version
- every module random version
- secret application.yml'e yazmak
- localhost hard-code
- profile explosion
- business logic config flag
- `latest` Docker image
- CI ile local tamamen farklı task
- transitive dependency'yi bilmeden kullanmak

## 98. Dependency Review Checklist
- BOM ile align mı?
- Version neden explicit?
- Transitive graph biliniyor mu?
- Gereksiz duplicate library var mı?
- Scope doğru mu?
- CVE/license risk var mı?

## 99. Configuration Review Checklist
- Secret dışarıda mı?
- Typed config mi?
- Fail-fast validation var mı?
- Environment-specific fark gerçekten gerekli mi?
- Timeout/TTL Duration mı?
- Hard-coded host/port var mı?
- Runtime refresh güvenli mi?

## 100. Build Governance Review Checklist
- Wrapper kullanılıyor mu?
- Java toolchain 21 mi?
- Build reproducible mı?
- Dependency locking gerekli mi?
- Spring Boot/Cloud BOM uyumlu mu?
- Multi-module ownership doğru mu?
- CI/local command uyumlu mu?
- Integration/architecture tests build lifecycle'da mı?
- Artifact environment-independent mı?
- Config/secret ayrımı korunuyor mu?