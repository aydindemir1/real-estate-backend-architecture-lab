# Layered / N-Layer Architecture

**Category:** Architecture  
**Introduced:** Day 1  
**Project status:** Implemented / Verified  
**Scope:** Service-internal organization for early project baseline

## 1. Nedir?

Layered Architecture, application code'un farklı responsibility alanlarına göre katmanlara ayrıldığı mimari yaklaşımdır.

Tipik katmanlar:

```text
Presentation / Controller
          |
          v
Application / Service
          |
          v
Persistence / Repository
          |
          v
Database
```

"N-Layer" ifadesi katman sayısının sabit olmadığını ifade eder. Örneğin DTO, mapper, integration veya domain katmanları ayrıca ayrılabilir.

## 2. Hangi problemi çözer?

Tek bir package veya class kümesi içinde:

- HTTP handling,
- business logic,
- persistence,
- mapping,
- infrastructure

karıştığında bakım maliyeti hızla artar.

Layered Architecture responsibility'leri ayrı katmanlara bölerek bu karmaşıklığı azaltmayı hedefler.

## 3. Ne işe yarar?

- Separation of Concerns sağlar.
- Controller'ın persistence detayını bilmesini engeller.
- Persistence erişimini repository boundary arkasına alır.
- Service katmanında use-case/business logic toplanmasına imkan verir.
- Test edilebilirliği artırır.
- Kod organizasyonunu standartlaştırır.

## 4. Tipik katmanlar

### Presentation Layer

HTTP request/response sorumluluğunu taşır.

Spring MVC karşılığı çoğunlukla:

- `@RestController`
- request mapping
- request validation
- HTTP status mapping

### Application / Service Layer

Use-case orchestration ve application flow burada bulunur.

Sorumluluklar:

- repository çağrıları
- mapper kullanımı
- transaction boundary
- external service orchestration
- business workflow coordination

### Persistence Layer

Database erişimini soyutlar.

Spring Data JPA ile çoğunlukla:

- Repository interface
- query methods
- persistence operations

### Domain / Model

Basit N-Layer uygulamalarda persistence entity ile domain model aynı object olabilir.

Daha olgun architecture'larda domain model persistence model'den ayrılabilir.

## 5. İç mimari

Bu projedeki ilk baseline yaklaşık şu yapıdadır:

```text
Controller
   |
   v
Service
   |
   v
Repository
   |
   v
JPA / Hibernate
   |
   v
PostgreSQL
```

Ek yardımcı katmanlar:

```text
DTO
Mapper
Exception
Utility
Configuration
Integration Client
```

## 6. Request akışı

Örnek:

```text
HTTP Request
    |
    v
Controller
    |
    v
Request DTO
    |
    v
Service
    |
    v
Mapper / Validation
    |
    v
Repository
    |
    v
Database
    |
    v
Response DTO
```

## 7. Temel özellikleri

- üst katman alt katmanı çağırır
- responsibility bazlı package organizasyonu
- dependency direction genellikle top-down'dır
- anlaşılması kolaydır
- CRUD sistemleri için hızlıdır
- framework ile doğal uyum sağlar
- domain complexity büyüdüğünde sınırları zorlanabilir

## 8. Avantajları

- öğrenmesi kolay
- uygulaması hızlı
- package yapısı nettir
- Spring MVC/JPA ile doğal uyum
- CRUD ağırlıklı servislerde verimli
- yeni ekip üyeleri için tanıdık yapı
- düşük başlangıç maliyeti

## 9. Dezavantajları

- business logic zamanla service class'larında yoğunlaşabilir
- persistence model domain model haline gelebilir
- framework bağımlılığı domain'e sızabilir
- yatay katmanlar use-case boundary'lerini gizleyebilir
- büyük service class'ları oluşabilir
- generic BaseService/BaseRepository abstraction'ları aşırı kullanılabilir
- complex domain'lerde coupling artabilir

## 10. Layered Architecture ile Clean Architecture farkı

Layered Architecture'da dependency çoğunlukla:

```text
Controller -> Service -> Repository -> Database
```

şeklindedir.

Clean Architecture'da ise domain/application core dış infrastructure'dan bağımsız tutulmaya çalışılır.

```text
Infrastructure --> Application --> Domain
```

Dependency direction inward olacak şekilde tasarlanır.

Bu nedenle Clean Architecture yalnız daha fazla layer demek değildir; dependency rule farklıdır.

## 11. Layered Architecture ile Hexagonal Architecture farkı

Hexagonal Architecture, application core'un external adapter'lardan ayrılmasına odaklanır.

Temel kavramlar:

- Port
- Adapter
- Application Core

Layered Architecture daha çok technical responsibility ayrımı yapar.

Hexagonal Architecture ise interaction boundary ayrımını ön plana çıkarır.

## 12. Hangi senaryolarda kullanılır?

- CRUD ağırlıklı uygulamalar
- küçük/orta ölçekli servisler
- domain complexity düşükse
- hızlı delivery önemliyse
- ekip klasik Spring katmanlarına alışkınsa
- architecture overhead düşük tutulmak isteniyorsa

## 13. Hangi senaryolarda yetersiz kalabilir?

- complex domain rules
- çok sayıda external adapter
- yoğun messaging
- framework bağımsız domain ihtiyacı
- çok farklı use-case davranışları
- persistence model ile domain model'in ayrılması gerekiyorsa
- uzun vadeli architecture boundary enforcement gerekiyorsa

## 14. Yaygın anti-pattern'ler

### Fat Controller

Business logic controller içinde birikir.

### God Service

Tüm business logic tek service class'ında toplanır.

### Anemic Pass-Through Service

Service yalnız repository çağrısını iletir ve herhangi bir application responsibility taşımaz.

### Generic BaseService Abuse

Her domain'i tek generic CRUD abstraction'a zorlamak domain semantics'i yok eder.

### Repository Leakage

Repository veya JPA entity doğrudan controller/API contract haline gelir.

## 15. Production considerations

Layered Architecture production'da tamamen geçerlidir.

Önemli olan katman sayısı değil:

- responsibility boundary,
- coupling,
- transaction ownership,
- error handling,
- validation,
- domain invariant location,
- persistence abstraction

gibi konuların doğru yönetilmesidir.

## 16. Bu projede nasıl kullanılıyor?

Day 1'de AuthService klasik Spring katmanlarıyla başlamıştır.

Örnek package'lar:

- `controller`
- `service`
- `repository`
- `model`
- `dto`
- `mapper`
- `exception`

Day 3 ile UserProfileService ve diğer service skeleton'larında da benzer baseline kullanılmıştır.

AuthService ve UserProfileService için N-Layer yaklaşımı korunacaktır.

Agent, Buyer, Seller ve Property servisleri ise sonraki Day'lerde farklı architecture style'lara geçirilecektir:

- Agent -> Clean Architecture
- Buyer -> Hexagonal Architecture
- Seller -> Onion Architecture
- Property -> Vertical Slice Architecture

Bu sayede proje aynı problem alanında farklı architecture stillerini karşılaştırmalı öğrenme amacı taşır.

## 17. İlgili prensipler

- Separation of Concerns
- Single Responsibility Principle
- Dependency Management
- Encapsulation
- Explicit Boundaries

## 18. İlgili proje dokümanları

- `docs/MASTER-ENGINEERING-PLAN.md`
- `docs/standards/application-layer.md`
- `docs/standards/persistence.md`
- `docs/standards/engineering-principles.md`

## 19. İleri öğrenme konuları

- package-by-layer vs package-by-feature
- transaction boundary design
- domain model separation
- Clean Architecture
- Hexagonal Architecture
- Onion Architecture
- Vertical Slice Architecture
- ArchUnit ile layer enforcement
