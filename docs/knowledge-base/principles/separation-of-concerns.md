# Separation of Concerns

**Category:** Principle  
**Introduced:** Day 1  
**Project status:** Implemented / Ongoing  
**Scope:** Responsibility separation across classes, layers, services and infrastructure

## 1. Nedir?

Separation of Concerns (SoC), bir sistemde farklı sorumlulukların birbirinden ayrılması prensibidir. Tek bir component'in birbirinden bağımsız değişim sebeplerini aynı yerde taşımaması hedeflenir.

## 2. Hangi problemi çözer?

HTTP handling, business logic, persistence, mapping, configuration, security ve messaging aynı yerde toplandığında coupling ve bakım maliyeti artar. SoC bu sorumlulukları anlamlı boundary'lere ayırır.

## 3. Uygulama seviyeleri

- Class: Controller HTTP, Repository persistence sorumluluğunu taşır.
- Layer: Presentation, application ve persistence ayrılır.
- Service: Auth, UserProfile, Agent, Buyer, Seller ve Property ayrı responsibility sınırlarına sahiptir.
- Infrastructure: Config, messaging, tracing ve datastore sorumlulukları ayrı tutulur.

## 4. Ne işe yarar?

- değişiklik etkisini sınırlar
- okunabilirliği artırır
- test edilebilirliği artırır
- ownership'i netleştirir
- architecture boundary'lerini güçlendirir
- cohesion'i artırıp gereksiz coupling'i azaltır

## 5. Single Responsibility Principle ile ilişkisi

SRP, SoC'nin object/class seviyesinde daha spesifik bir ifadesi olarak düşünülebilir. SoC package, module, service ve platform seviyesinde de uygulanır.

## 6. Avantajları ve trade-off'ları

Doğru uygulandığında bakım ve test kolaylığı sağlar. Aşırı uygulanırsa gereksiz abstraction, çok fazla class ve navigation cost oluşturabilir. Her ayrım gerçek bir responsibility boundary'ye dayanmalıdır.

## 7. Anti-pattern'ler

- God Class
- Fat Controller
- God Service
- Repository içinde business logic
- DTO'nun domain model yerine kullanılması
- Infrastructure detayının domain'e sızması

## 8. Bu projede nasıl uygulanıyor?

İlk günlerden itibaren controller, service, repository, model, dto, mapper ve exception sorumlulukları ayrılmıştır. Microservice seviyesinde business responsibility'ler farklı servislere bölünmüştür. Day 7 build dependency ownership temizliği de aynı prensibin build seviyesindeki uygulamasıdır: dependency yalnız ihtiyaç duyan module'de bulunur.

## 9. İlgili kavramlar

- Single Responsibility Principle
- High Cohesion
- Loose Coupling
- Layered Architecture
- Clean Architecture
- Hexagonal Architecture
- Service Autonomy

## 10. Production considerations

Boundary'ler yalnız package isimleriyle değil dependency rules, tests, module boundaries, ArchUnit ve code review ile korunmalıdır.

## 11. İleri öğrenme konuları

- cohesion metrics
- modularity
- dependency inversion
- package-by-feature
- architecture fitness functions