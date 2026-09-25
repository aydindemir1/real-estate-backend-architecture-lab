# Java 21

**Category:** Technology  
**Introduced:** Day 1  
**Project status:** Implemented / Verified  
**Scope:** Primary programming language and JVM runtime baseline

## 1. Nedir?

Java 21, Java platformunun Long-Term Support (LTS) sürümlerinden biridir ve bu projenin ana programming language/runtime baseline'ıdır.

Java uygulamaları source code'dan bytecode'a compile edilir ve JVM üzerinde çalışır.

```text
.java source
    |
    v
javac
    |
    v
.class bytecode
    |
    v
JVM
    |
    v
Operating System
```

## 2. Java platformunun ana bileşenleri

### JDK

Development için gerekli araçları içerir:
- javac
- java
- javadoc
- jar
- debugging/profiling araçları

### JVM

Bytecode'u çalıştırır.

Temel sorumlulukları:
- class loading
- memory management
- garbage collection
- JIT compilation
- thread execution
- runtime verification

### Standard Library

Collections, I/O, concurrency, networking, time API ve çok sayıda temel capability sağlar.

## 3. JVM iç mimarisi

Basitleştirilmiş görünüm:

```text
Class Loader
    |
    v
Runtime Data Areas
  - Heap
  - Thread Stacks
  - Metaspace
  - PC Registers
    |
    v
Execution Engine
  - Interpreter
  - JIT Compiler
  - Garbage Collector
```

## 4. Java 21 neden kullanılıyor?

- modern LTS baseline
- Spring Boot 4.x ile uyumlu modern Java seviyesi
- records
- sealed types
- pattern matching improvements
- modern switch
- virtual threads
- mature JVM ecosystem

## 5. Records

Data carrier tiplerinde boilerplate azaltır.

Uygun alanlar:
- immutable request/response DTO
- value carrier
- event payload

Dikkat:
JPA entity için varsayılan tercih değildir; lifecycle/proxy/mutability semantics farklıdır.

## 6. Sealed classes/interfaces

Type hierarchy'yi kontrollü sınırlar.

Domain outcome, command result veya restricted inheritance model'lerinde kullanılabilir.

## 7. Virtual Threads

Çok sayıda blocking I/O işi için lightweight thread modeli sunar.

Ancak:
- her uygulamada varsayılan çözüm değildir
- downstream connection pool capacity kaybolmaz
- CPU-bound workload hızlanmaz
- concurrency limit ihtiyacı ortadan kalkmaz

Bu projede virtual threads bilinçli değerlendirme konusudur, otomatik olarak açılmaz.

## 8. Memory ve Garbage Collection

Object'ler büyük ölçüde heap üzerinde yaşar.

GC kullanılamayan object'leri temizler.

Production'da önemli konular:
- heap sizing
- allocation rate
- GC pauses
- memory leak
- object retention
- container memory limits

## 9. Concurrency

Java şu araçları sağlar:
- Thread
- ExecutorService
- CompletableFuture
- synchronized
- locks
- atomic types
- concurrent collections
- virtual threads

Concurrency seçimi workload'a göre yapılmalıdır.

## 10. Avantajları

- mature ecosystem
- strong typing
- JVM portability
- excellent tooling
- concurrency support
- large backend ecosystem
- Spring ile güçlü entegrasyon
- production maturity

## 11. Dezavantajları / trade-off'ları

- JVM warm-up
- memory footprint
- abstraction-heavy framework kullanımında complexity
- GC tuning ihtiyacı
- yanlış concurrency kullanımında ciddi hata riski

## 12. Bu projede nasıl kullanılıyor?

Tüm Spring Boot service'leri Java 21 baseline üzerinde çalışır.

Build tarafında Gradle Java toolchain ile Java 21 hedeflenir.

Project engineering standard'ı:
`docs/standards/java-21.md`

## 13. Production considerations

- toolchain pinning
- supported JDK distribution
- JVM flags
- container memory awareness
- GC selection
- thread/concurrency model
- observability
- security patching

## 14. İleri öğrenme konuları

- JVM internals
- JIT compilation
- GC algorithms
- Java Memory Model
- virtual threads internals
- structured concurrency
- profiling with JFR/JMC
- class loading
