# Spring Boot

**Category:** Technology  
**Introduced:** Day 1  
**Project status:** Implemented / Verified  
**Scope:** Application framework and runtime foundation

## 1. Nedir?

Spring Boot, Spring Framework tabanlı uygulamaların hızlı, opinionated ve production-oriented biçimde geliştirilmesini kolaylaştıran application framework katmanıdır.

Temel amacı Spring uygulaması başlatmak için gereken boilerplate configuration'ı azaltmak ve yaygın ihtiyaçları auto-configuration ile hazır hale getirmektir.

## 2. Hangi problemi çözer?

Klasik Spring uygulamalarında:
- bean configuration
- dependency wiring
- server setup
- serialization
- actuator
- environment configuration

gibi pek çok konu manual olarak yönetilebilir.

Spring Boot starter ve auto-configuration yaklaşımıyla bu yükü azaltır.

## 3. Temel bileşenler

### Starter'lar

Belirli capability için uygun dependency set'i sağlar.

Örnek:
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-actuator

### Auto-Configuration

Classpath ve configuration'a bakarak uygun bean'leri otomatik oluşturur.

### Embedded Server

Tomcat/Jetty/Undertow benzeri embedded web server yaklaşımını destekler.

### Externalized Configuration

application.yml, environment variable ve diğer property source'larını destekler.

### Actuator

Health, metrics ve management endpoint'leri sağlar.

## 4. İç mimari

Basitleştirilmiş startup:

```text
main()
  |
  v
SpringApplication.run()
  |
  v
Environment hazırlanır
  |
  v
ApplicationContext oluşturulur
  |
  v
Auto-Configuration uygulanır
  |
  v
Bean'ler oluşturulur
  |
  v
Embedded Server başlar
```

## 5. @SpringBootApplication

Üç ana annotation'ın birleşimidir:

- @Configuration
- @EnableAutoConfiguration
- @ComponentScan

## 6. Dependency Injection

Spring container object lifecycle ve dependency wiring'i yönetir.

Constructor injection tercih edilir.

## 7. Bean Lifecycle

Temel lifecycle:
- instantiate
- dependency injection
- post processing
- initialization
- ready
- destruction

## 8. Auto-Configuration nasıl çalışır?

Spring Boot classpath, property ve condition'lara bakar.

Örnek mantık:
> DataSource classpath'te mi? JDBC dependency var mı? URL tanımlı mı? O halde DataSource configuration oluştur.

## 9. Avantajları

- hızlı başlangıç
- convention over configuration
- production-ready integrations
- mature ecosystem
- externalized configuration
- test support
- observability integration

## 10. Trade-off'ları

- auto-configuration görünmeyen davranış yaratabilir
- yanlış starter gereksiz dependency yükleyebilir
- framework internals bilinmezse debugging zorlaşabilir
- proxy ve lifecycle davranışları yanlış anlaşılabilir

## 11. Bu projede nasıl kullanılıyor?

Tüm business service ve Spring Cloud infrastructure module'leri Spring Boot application olarak çalışır.

Java 21 + Spring Boot 4.1.1 baseline kullanılmaktadır.

## 12. Production considerations

- configuration validation
- graceful shutdown
- actuator security
- profile governance
- startup failure policy
- resource limits
- dependency version alignment
- logging/metrics/tracing

## 13. İlgili proje standardı

`docs/standards/spring-boot.md`

## 14. İleri öğrenme konuları

- ApplicationContext internals
- BeanFactory
- auto-configuration conditions
- AOP proxies
- lifecycle hooks
- configuration binding
- native/AOT
