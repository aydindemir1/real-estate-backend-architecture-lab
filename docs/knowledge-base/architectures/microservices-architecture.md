# Microservices Architecture

**Category:** Architecture  
**Introduced:** Day 3  
**Project status:** Implemented / Integrated / Verified  
**Scope:** Service decomposition, independently owned service boundaries, distributed communication

## 1. Nedir?

Microservices Architecture, bir uygulamanın tek bir büyük deployable yapı yerine, belirli business capability veya bounded responsibility'ler etrafında ayrılmış küçük ve bağımsız servislerden oluşacak şekilde tasarlanmasıdır.

Her servis:
- kendi responsibility alanına,
- kendi runtime lifecycle'ına,
- kendi configuration'ına,
- mümkün olduğunda kendi datastore ownership'ına,
- açık communication contract'larına

sahip olur.

Amaç yalnızca kodu küçük parçalara bölmek değildir. Asıl amaç service autonomy, independent evolution, fault isolation ve business capability ownership sağlamaktır.

## 2. Hangi problemi çözer?

Büyük monolithic sistemlerde zamanla şu problemler oluşabilir:

- değişikliklerin birbirini fazla etkilemesi,
- tek deployable unit nedeniyle küçük değişikliklerde tüm sistemin deploy edilmesi,
- domain boundary'lerinin belirsizleşmesi,
- ekiplerin aynı codebase üzerinde sıkı bağlı çalışması,
- farklı workload'ların bağımsız scale edilememesi,
- tek bir hata alanının tüm sistemi etkilemesi,
- farklı business capability'lerin farklı teknik ihtiyaçlarının karşılanmasının zorlaşması.

Microservices Architecture bu problemlere service boundary ve bağımsız runtime ownership ile cevap verir.

## 3. Ne işe yarar?

Başlıca amaçları:

- business capability'leri bağımsız servis sınırlarına ayırmak,
- servislerin bağımsız geliştirilebilmesini sağlamak,
- bağımsız deployment potansiyeli oluşturmak,
- workload bazlı scaling yapabilmek,
- farklı datastore ve teknik stratejilere izin vermek,
- fault isolation sağlamak,
- ekip ownership'ını netleştirmek,
- distributed system öğrenme ve uygulama alanı oluşturmak.

## 4. Hangi senaryolarda kullanılır?

Microservices Architecture genellikle şu senaryolarda anlamlıdır:

- domain büyük ve birden fazla business capability içeriyorsa,
- servislerin farklı scaling ihtiyacı varsa,
- ekipler bağımsız çalışma ihtiyacı taşıyorsa,
- bağımsız release cadence isteniyorsa,
- farklı datastore veya teknoloji ihtiyaçları varsa,
- fault isolation önemliyse,
- sistem uzun ömürlü ve sürekli gelişen bir platformsa,
- distributed systems problemlerini çözmenin maliyeti kabul edilebiliyorsa.

## 5. Hangi senaryolarda kullanılmamalıdır?

Şu durumlarda gereksiz karmaşıklık oluşturabilir:

- küçük ve basit CRUD uygulamaları,
- tek ekipli erken aşama ürünler,
- domain sınırları henüz bilinmiyorsa,
- deployment/observability/operations altyapısı zayıfsa,
- service-to-service communication maliyeti iş değerinden yüksekse,
- yalnız "modern görünmek" için seçiliyorsa.

Microservices, başlangıç noktası olmak zorunda değildir. Birçok sistem için iyi tasarlanmış modular monolith daha uygun olabilir.

## 6. Temel kavramlar

### Service Boundary

Her servis belirli bir responsibility veya business capability alanına sahiptir.

### Service Autonomy

Bir servis mümkün olduğunca başka bir servisin internal implementation detaylarına bağımlı olmamalıdır.

### Database Ownership

Bir servisin kendi canonical verisinin sahibi olması tercih edilir. Başka servisler bu veriye doğrudan database seviyesinde bağlanmamalıdır.

### Contract

Servisler REST, messaging, gRPC veya diğer protokoller üzerinden explicit contract'larla haberleşir.

### Independent Deployment

İdeal durumda bir servis diğer servisleri rebuild/deploy etmeden yayınlanabilir.

### Fault Isolation

Bir servisteki problem tüm sistemi doğrudan çökertmemelidir.

## 7. İç mimarisi

Tipik bir microservices sistemi şu bileşenlerden oluşur:

```text
                     Client
                       |
                       v
                  API Gateway
                       |
          +------------+------------+
          |            |            |
          v            v            v
      Service A    Service B    Service C
          |            |            |
          v            v            v
        DB A          DB B          DB C

          <------ Sync / Async ------>

         Service Registry / Discovery
         Central Configuration
         Messaging Infrastructure
         Observability
         Security / Identity
```

Microservices Architecture yalnız application service'lerden ibaret değildir. Runtime olarak çoğu zaman aşağıdaki platform capability'leri de gerekir:

- API Gateway
- Service Discovery
- Centralized Configuration
- Load Balancing
- Resilience
- Messaging
- Distributed Tracing
- Centralized Logging
- Metrics
- Security / Identity
- Deployment orchestration

## 8. Çalışma mekanizması

Bir request tipik olarak şu şekilde ilerleyebilir:

```text
Client
  |
  v
API Gateway
  |
  v
Service Discovery
  |
  v
Target Service
  |
  +--> Own Database
  |
  +--> Internal REST / Messaging
            |
            v
        Another Service
```

Senkron communication düşük latency ve request/response ihtiyacında kullanılabilir.

Asenkron communication ise:
- loose coupling,
- deferred processing,
- event propagation,
- workload buffering

gibi ihtiyaçlarda kullanılabilir.

## 9. Temel özellikleri

- loosely coupled services
- explicit service boundaries
- decentralized data ownership
- distributed communication
- independent lifecycle
- horizontal scaling capability
- independent failure domains
- infrastructure automation ihtiyacı
- observability zorunluluğu
- network failure awareness

## 10. Avantajları

- bağımsız deployment imkanı
- bağımsız scaling
- daha net service ownership
- fault isolation
- technology flexibility
- bounded responsibility
- büyük ekiplerde parallel development
- polyglot persistence imkanı

## 11. Dezavantajları ve trade-off'ları

Microservices ücretsiz değildir.

Getirdiği maliyetler:

- network latency
- partial failure
- distributed transaction zorluğu
- data consistency karmaşıklığı
- observability ihtiyacı
- deployment complexity
- configuration/secrets complexity
- contract versioning
- retry/idempotency ihtiyacı
- local development maliyeti
- testing complexity
- operational burden

Monolith içindeki method call, microservices sisteminde network call olur. Bu nedenle failure model tamamen değişir.

## 12. İlgili pattern'ler

- API Gateway
- Database per Service
- Service Registry
- Service Discovery
- Client-Side Load Balancing
- Circuit Breaker
- Retry
- Saga
- Outbox
- CQRS
- Event-Driven Architecture
- Health Check

## 13. Alternatifleri

- Monolithic Architecture
- Modular Monolith
- Service-Oriented Architecture
- Serverless / Function-oriented systems

Alternatif seçimi domain, ekip, operasyonel kapasite ve deployment ihtiyaçlarına göre yapılmalıdır.

## 14. Production considerations

Production microservices sistemi için yalnız service kodu yeterli değildir.

Dikkate alınması gerekenler:

- service discovery
- timeout policy
- retry ownership
- circuit breaking
- rate limiting
- authentication/authorization
- secrets management
- centralized observability
- schema/contract governance
- deployment automation
- rollback strategy
- health/readiness
- resource limits
- graceful shutdown
- idempotency
- eventual consistency
- disaster recovery

## 15. Bu projede nasıl kullanılıyor?

Proje Day 3 itibarıyla birden fazla Spring Boot service'e ayrılmıştır.

İlk business service set'i:

- AuthService
- UserProfileService
- AgentService
- BuyerService
- PropertyService
- SellerService

Sonraki aşamada SearchService de eklenmiştir.

Day 4–6 arasında microservices runtime capability'leri genişletilmiştir:

- Spring Cloud Config
- API Gateway
- Circuit Breaker
- Distributed Tracing
- RabbitMQ
- Eureka
- Spring Cloud LoadBalancer

Day 7 ile data ownership ve polyglot persistence foundation güçlendirilmiştir.

## 16. Bu projedeki önemli sınır

Proje service sayısını artırmak için microservices kullanmaz.

Her service için:
- business ownership,
- datastore ownership,
- communication semantics,
- architecture style

bilinçli olarak ayrı değerlendirilir.

## 17. İlgili proje dokümanları

- `docs/architecture/service-catalog.md`
- `docs/architecture/data-architecture.md`
- `docs/architecture/communication-architecture.md`
- `docs/architecture/messaging-topology.md`
- `docs/MASTER-ENGINEERING-PLAN.md`

## 18. İleri öğrenme konuları

- bounded context decomposition
- service granularity
- distributed transactions
- saga orchestration/choreography
- consistency models
- contract evolution
- service mesh
- platform engineering
- Kubernetes-based service discovery
- zero-downtime deployment
- multi-region architecture
- distributed systems failure modes
