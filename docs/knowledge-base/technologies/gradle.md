# Gradle

**Category:** Technology  
**Introduced:** Day 1  
**Project status:** Implemented / Verified  
**Scope:** Build automation, dependency management and multi-module project orchestration

## 1. Nedir?

Gradle, JVM ekosisteminde yaygın kullanılan build automation tool'udur.

Bu projede:
- compile
- test
- dependency resolution
- plugin management
- multi-module orchestration

için kullanılır.

## 2. Build lifecycle mantığı

Gradle build üç ana fazdan geçer:

```text
Initialization
    |
    v
Configuration
    |
    v
Execution
```

### Initialization

Hangi project/module'lerin build'e dahil olduğunu belirler.

### Configuration

Build script'leri değerlendirilir ve task graph hazırlanır.

### Execution

İstenen task'lar ve dependency task'ları çalıştırılır.

## 3. Temel kavramlar

- Project
- Task
- Plugin
- Dependency
- Configuration
- Repository
- Wrapper
- Toolchain
- Build Cache
- Configuration Cache

## 4. Multi-module build

Bu proje çok sayıda Spring Boot module içerir.

Root project:
- common plugin/version governance
- dependency management
- common test baseline

sağlayabilir.

Module'ler ise yalnız ihtiyaç duydukları dependency'leri seçmelidir.

## 5. Day 7 dependency ownership düzeltmesi

Day 7'de root `build.gradle` içindeki Web, OpenAPI, MapStruct, JWT ve OpenFeign gibi dependency'lerin tüm subproject'lere zorla uygulanması temizlenmiştir.

Prensip:

> Dependency centrally versioned olabilir; fakat module-local seçilmelidir.

Bu build-level Separation of Concerns ve dependency governance örneğidir.

## 6. Gradle Wrapper

`gradlew` ve `gradlew.bat`, developer makinesinde global Gradle kurulumu gerektirmeden belirli Gradle sürümünü kullanmayı sağlar.

Avantaj:
- reproducible builds
- CI/local parity
- version pinning

## 7. Dependency management

Bu projede dependency version'ları merkezi catalog niteliğindeki `dependencies.gradle` üzerinden yönetilir.

Spring dependency'lerinin bir bölümü BOM/plugin management üzerinden resolve edilir.

## 8. Toolchain

Java toolchain build'in hangi Java sürümünü hedefleyeceğini tanımlar.

Bu projede Java 21 baseline kullanılır.

## 9. Sık kullanılan task'lar

```powershell
.\gradlew.bat projects
.\gradlew.bat dependencies
.\gradlew.bat compileJava
.\gradlew.bat test
.\gradlew.bat check
.\gradlew.bat build
```

## 10. Avantajları

- flexible build model
- incremental build
- multi-project support
- strong JVM ecosystem integration
- dependency management
- build cache support
- custom task/plugin extensibility

## 11. Dezavantajları

- DSL complexity
- configuration-time surprises
- plugin compatibility
- dependency resolution complexity
- large builds'de performance tuning ihtiyacı

## 12. Bu projede nasıl kullanılıyor?

Root multi-project build altında:
- AuthService
- UserProfileService
- AgentService
- BuyerService
- SellerService
- PropertyService
- SearchService
- Spring Cloud infrastructure modules

yönetilir.

Day 7 regression:
- `check`
- `compileJava`

ile doğrulanmıştır.

## 13. Mevcut teknik borç

Gradle 9.7.1 build sırasında Gradle 10 ile uyumsuz olacak deprecated feature warning'i görülmektedir.

Bu Day 7 failure değildir; ayrı build tooling hardening konusu olarak ele alınmalıdır.

## 14. Production / CI considerations

- wrapper kullan
- dynamic version kullanma
- dependency version pinle
- reproducible build hedefle
- cache stratejisini ölç
- CI'da clean verification yap
- plugin upgrade'lerini kontrollü yap

## 15. İleri öğrenme konuları

- convention plugins
- version catalogs
- dependency locking
- build cache
- configuration cache
- composite builds
- custom plugins
