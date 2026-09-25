# Loose Coupling

**Category:** Principle  
**Introduced:** Day 3  
**Project status:** Implemented / Strengthened over time  
**Scope:** Reducing unnecessary dependency between components and services

## 1. Nedir?

Loose Coupling, iki component'in birbirinin internal implementation detaylarına mümkün olduğunca az bağımlı olması prensibidir. Bir component değiştiğinde diğerinin değişmek zorunda kalmaması hedeflenir.

## 2. Tight coupling örnekleri

- başka service'in database'ine doğrudan bağlanmak
- hard-coded service URL
- shared mutable entity model
- cyclic service dependency
- gereksiz uzun synchronous call chain

## 3. Nasıl sağlanır?

- explicit API contract
- interface / port
- DTO boundary
- messaging
- service discovery
- configuration externalization
- database ownership
- dependency inversion

## 4. Coupling türleri

- Compile-time coupling: module başka implementation'a doğrudan bağlıdır.
- Runtime coupling: service başka service'in anlık availability'sine bağımlıdır.
- Data coupling: service başka service'in database schema'sına bağımlıdır.
- Deployment coupling: bir değişiklik diğer service'lerin de deploy edilmesini gerektirir.

## 5. Synchronous ve asynchronous iletişimle ilişkisi

Synchronous REST interface coupling'i azaltabilir ama temporal/availability coupling oluşturabilir. Asynchronous messaging temporal coupling'i azaltır; buna karşılık message schema, broker topology ve delivery semantics gibi yeni contract bağımlılıkları getirir.

## 6. Avantajları

- independent evolution
- resilience
- testability
- replaceability
- clearer ownership

## 7. Trade-off'ları

Loose coupling çoğu zaman abstraction, contract governance, versioning ve messaging gibi ek complexity getirir.

## 8. Bu projede nasıl uygulanıyor?

Auth, UserProfile veritabanına doğrudan erişmez; OpenFeign contract üzerinden iletişim kurar. Day 6C ile hard-coded URL yerine Eureka + Spring Cloud LoadBalancer ile service-name resolution kullanılmıştır. Day 6A'da alternatif async RabbitMQ flow eklenmiştir. Day 7 datastore ownership ayrımı coupling'i daha da azaltacak foundation'ı hazırlamıştır.

## 9. Distributed Monolith riski

Microservice sayısının fazla olması loose coupling anlamına gelmez. Servisler aynı anda deploy olmak zorundaysa, birbirinin database'ine erişiyorsa veya uzun synchronous chain'ler oluşturuyorsa sistem distributed monolith'e dönüşebilir.

## 10. İlgili kavramlar

- Service Autonomy
- Database per Service
- Event-Driven Architecture
- Dependency Inversion
- Interface Segregation

## 11. İleri öğrenme konuları

- temporal coupling
- contract coupling
- event-driven decoupling
- bounded context
- coupling metrics