# Day 13 — Kesin dosya, sınıf ve commit planı

## 0. Scope

Day 13 yalnızca Redis infrastructure foundation içindir.

Hedef:
- Redis dependency/config
- typed properties
- serializer strategy
- key naming
- TTL conventions
- minimal connectivity/ephemeral adapter
- integration tests
- architecture role guard

Day 13 içinde:
- Offer idempotency yok
- RateLimiter business implementation yok
- seçilen mevcut read use-case üzerinde sınırlı cache-aside akışı vardır
- Saga state yok
- canonical business state yok

## Task 1 — Redis ownership audit

Review:
- hangi service'ler ileride Redis kullanacak
- current root/common dependency policy
- config ownership

Kullanım zamanlaması:
- Day 13 seçilen mevcut read path → cache-aside
- Day 22 BuyerService → durable idempotency üzerinde yalnız Redis accelerator
- Day 23 ApiGatewayService → rate limiting

Day 13'te shared cross-service Redis library oluşturulmaz.

Each service may later own its own Redis adapter/config while sharing conventions only.

## Task 2 — Dependency catalog

Verify root dependencies.gradle already contains Spring Data Redis alias from Day 7.

If missing, add:
- spring-boot-starter-data-redis
- Testcontainers JUnit Jupiter if not present

Do not add Redis dependency to every service globally.

Commit if needed: build(redis): add Redis dependency catalog

## Task 3 — Choose foundation owner module

Redis is not a standalone business service.

Day 13 implementation should avoid creating a fake RedisService microservice.

Preferred approach:
- create a small infrastructure/demo foundation in one existing module only if needed for executable proof
or
- create infrastructure tests/config in a neutral technical module if repo already has such a pattern

If no suitable neutral module exists, use BuyerService only for connectivity/config proof without implementing idempotency behavior yet.

Important:
No shared business abstraction across services.

## Task 4 — Typed Redis configuration

Create in chosen module:
- infrastructure/configuration/RedisProperties.java

Suggested fields:
- host/URI if not using Spring standard properties directly
- port
- connectTimeout
- commandTimeout
- defaultTtl
- keyPrefix/environment prefix if useful

Prefer Spring Boot standard spring.data.redis.* where sufficient.

Custom @ConfigurationProperties only for application-specific conventions such as default TTL/prefix.

Commit: config(redis): add typed Redis configuration

## Task 5 — Redis connection configuration

Create only if Boot auto-configuration is insufficient:
- infrastructure/configuration/RedisConfiguration.java

Possible beans:
- RedisTemplate<String, Object> only if generic template truly needed
- StringRedisTemplate for string/simple payloads

Preferred:
Use narrow typed operations/serializer instead of a global Object template that accepts anything.

Do not create configuration class merely for ceremony.

Commit if needed: feat(redis): configure Redis connectivity

## Task 6 — Serialization strategy

Decision:
- no Java native serialization
- prefer String keys
- JSON or explicit String payload depending use-case

For generic foundation:
- keys -> String serializer
- values -> JSON serializer only if typed object proof is needed

Create test value DTO only under test sources if necessary.

Document schema/version awareness for JSON payloads.

Commit: feat(redis): define serialization strategy

## Task 7 — Key naming standard in code

Create technical utility only if useful:
- RedisKeyFactory.java

Candidate namespaces:
- cache:property-summary:{propertyId}
- idempotency:offer:{key}
- rate-limit:{subject}:{route}:{window}

Foundation smoke anahtarı test-only olabilir; gerçek cache use-case kendi namespace’ini kullanır. Örnek smoke anahtarı:
- lab:redis:smoke:{id}

Do not create future business keys in production code unless corresponding feature exists.

Rule:
- lowercase
- colon-delimited
- ownership/capability clear
- no raw secret/token in key

Commit: feat(redis): add Redis key naming foundation

## Task 8 — TTL policy

Create:
- RedisTtlPolicy.java or typed config-backed helper only if current implementation benefits

Rules:
- ephemeral key must have explicit TTL
- permanent Redis business state prohibited
- TTL uses Duration
- no magic milliseconds

Day 13 default TTL is only a foundation/test value, not a future business contract.

Commit: feat(redis): add TTL convention

## Task 9 — Minimal Redis port

If chosen module needs clean architecture separation, create a technical port with narrow semantics.

Example foundation interface:
- EphemeralKeyValueStore.java

Methods only for smoke/foundation:
- put(String key, String value, Duration ttl)
- Optional<String> get(String key)
- delete(String key)

Do not name it CacheService/IdempotencyService yet.

Important:
This is technical foundation, not domain port.

Commit: feat(redis): add minimal ephemeral key-value port

## Task 10 — Redis adapter

Create:
- RedisEphemeralKeyValueAdapter.java

Responsibilities:
- set with TTL
- get
- delete
- key serialization
- infrastructure exception translation if needed

No business logic.

Commit: feat(redis): add Redis ephemeral adapter

## Task 11 — Failure semantics

Define technical exception only if caller needs semantic distinction:
- RedisUnavailableException

Do not expose Lettuce/Jedis exception directly beyond infrastructure boundary.

Day 13 no fail-open/fail-closed business policy yet; that belongs to idempotency/rate-limit feature Days.

Commit can be grouped with adapter.

## Task 12 — Config Server integration

Add non-secret config:
- Redis host/URI
- port
- timeout
- default foundation TTL if used

Secrets only if Redis auth enabled:
- REDIS_PASSWORD

No literal password committed.

Commit: config(redis): add external Redis settings

## Task 13 — Connectivity integration test

Create:
- RedisConnectivityIntegrationTest.java

Using Testcontainers Redis or GenericContainer if no dedicated module.

Cases:
- connection succeeds
- PING/SET/GET

Do not depend on developer local Redis.

Commit: test(redis): add connectivity integration test

## Task 14 — TTL integration test

Create:
- RedisTtlIntegrationTest.java

Scenario:
1. write key with short TTL
2. verify present
3. wait condition-based, not arbitrary long Thread.sleep
4. verify expired

Use Awaitility if already available or bounded polling helper.

Commit: test(redis): verify TTL behavior

## Task 15 — Serialization round-trip test

Create:
- RedisSerializationIntegrationTest.java

If JSON serializer is used:
- write typed test payload
- read
- verify fields

If string-only foundation chosen, verify UTF-8/string round-trip instead.

Commit: test(redis): verify serialization behavior

## Task 16 — Key namespace test

Create:
- RedisKeyFactoryTest.java

Cases:
- deterministic namespace
- no null/blank segments
- special characters normalized/rejected if policy exists

Commit can group with key foundation.

## Task 17 — Source-of-truth guard

Architecture/documentation rule:
- no Aggregate repository implementation backed only by Redis
- Redis package names indicate cache/idempotency/rate-limit/ephemeral concerns

If ArchUnit can express current module rule, add:
- RedisRoleArchitectureTest.java

Example rule:
classes under domain.repository must not be implemented by Redis adapter unless an explicit ADR says otherwise.

Keep rule practical, not over-generalized.

Commit: test(redis): enforce non-canonical Redis role

## Task 18 — Metrics/observability baseline

Day 29–30 tam telemetry kapsamını sahiplenir.

Day 13 only ensure:
- connection failures visible in logs
- no sensitive Redis credential logged
- Actuator health behavior understood

Do not add custom high-cardinality metrics.

## Task 19 — Local runtime smoke

Start:
- Redis container
- chosen module/service if executable proof is needed

Verify:
- connect
- write/read
- TTL expiry

Mevcut read endpoint cache-aside ile çalışabilir; yeni idempotency/rate-limit endpoint eklenmez.

## Task 20 — Documentation

Modify:
- docs/roadmap/day-13-redis-foundation.md
- docs/standards/persistence.md only if actual implementation reveals a needed clarification
- chosen service DESIGN doc only if Redis technical dependency is introduced there

Create candidate:
- docs/infrastructure/redis-conventions.md

Document:
- Redis is non-canonical
- key naming
- TTL policy
- serializer policy
- future use-case ownership
- failure policy deferred per feature

Commit: docs(redis): document Redis infrastructure conventions

## Recommended Commit Sequence
1. docs(cache): strateji karşılaştırmasını ve read use-case kararını tanımla
2. build(redis): add Redis dependency catalog — only if missing
3. config(redis): add typed Redis configuration
4. feat(redis): define serialization strategy
5. feat(redis): add Redis key naming foundation
6. feat(redis): add TTL convention
7. feat(redis): add minimal ephemeral key-value port
8. feat(redis): add Redis ephemeral adapter
9. config(redis): add external Redis settings
10. test(redis): add connectivity integration test
11. test(redis): verify TTL behavior
12. test(redis): verify serialization behavior
13. test(redis): enforce non-canonical Redis role
14. feat(buyer): seçilen read akışına cache-aside adapterı ekle
15. feat(cache): başarılı mutation sonrası invalidation politikasını bağla
16. test(cache): hit miss stale refill ve Redis loss davranışını doğrula
17. test(redis): izole eviction ve TTL farkını doğrula
18. docs(cache): doğruluk sahiplik ve staleness sınırlarını kaydet
19. docs(redis): document Redis infrastructure conventions

Küçük ve aynı sorumluluğa ait komşu commit’ler birleştirilebilir; karar, uygulama ve doğrulama ayrı incelenebilir kalır.

## Explicitly Deferred from Day 13

Do not implement:
- Offer idempotency store
- Idempotency-Key workflow
- Gateway rate limiting
- Saga state
- distributed lock
- session storage
- pub/sub
- Redis Streams

## Critical Design Note — No Fake Redis Microservice

Redis is infrastructure, not a business bounded context.

Do not create a standalone RedisService just to display the technology.

## Critical Design Note — Canonical State

Redis must never become the only source of truth for Property, Offer, Seller, BuyerPreferences or Agent.

Future idempotency/rate-limit/cache state is ephemeral/derived/operational.

## Day 13 Final Gate

Day 13 closes only if:
- Redis dependency/config is explicit
- no dependency is globally forced on unrelated modules
- Redis connection works
- typed/custom config is used only where value exists
- Java native serialization is not used
- key naming convention is documented
- TTL uses Duration and is explicit
- connectivity integration test passes
- TTL expiry integration test passes
- serialization round-trip is verified
- Redis errors do not leak raw client details
- no business Aggregate repository is moved to Redis
- Offer idempotency, rate limiting ve Saga kapsamı bu güne taşınmaz; seçilen read cache açıkça kapsam içindedir
- docs match actual implementation

## Onaylanan system design ek kapsamı — Redis cache stratejileri ve bir read use-case

Durum: **Planlandı**. Bu bölüm günün mevcut temel görevlerine eklenir; tamamlanmış implementation iddiası değildir. Ek görevler foundation kurulduktan sonra ve günün dokümantasyon/kapanış adımından önce uygulanır. Yukarıdaki commit sırası bu kapsamı içerir.

### Ek görevler ve çıktı belgeleri

1. Cache-aside, write-through ve write-around stratejilerini karşılaştır; cache invalidation, TTL/eviction ayrımı ve cache hierarchy konularını Redis üzerinde öğren. CDN ve platform cache dağıtımı bu backend gününde kurulmaz.
2. Day 9’da mevcut GetBuyerPreferences read use-case’ini aday olarak değerlendir; gerçek endpoint adı implementation envanterinden doğrulanır. Seçilirse BuyerService kendi cache adapter/config’ini sahiplenir; uygun değilse mevcut bir read use-case ADR ile seçilir. Yeni domain veya RedisService oluşturma.
3. Cache port/adapter ve namespace’i dar tut: ortam + capability + owner/resource + payload version; token/secret anahtara yazma. Day 14 ownership kontrollerinden önce başka kullanıcı verisini paylaşan ortak anahtar kullanma; Day 43 tenant-scoped kaynaklarda tenant namespace eklenir.
4. Cache-aside akışını uygula: hit → derived response; miss → canonical datastore read → TTL ile cache. Redis başarısızsa canonical datastore’a bounded fallback yap; datastore hatasını sahte cache success/empty response ile gizleme.
5. Mevcut preferences update use-case’iyle invalidation bağlantısını kur: başarılı durable commit sonrasında key silme/version stratejisi. Update yoksa TTL staleness sınırını açıkça belgeleyip invalidation uygulandı iddiasında bulunma.
6. Concurrent read/update → stale refill penceresini değerlendir; version/conditional write ya da kabul edilen bounded-staleness politikasını açıkça seç. Cache doğruluğu business invariant/Offer idempotency yerine geçmez.
7. Eviction için maxmemory ve seçilen politika etkisini izole test config’inde incele; eviction ile TTL expiration farklıdır. Çok katmanlı cache yalnız karşılaştırma/tasarım; sırf göstermek için L1 ürün ekleme.
8. Hit/miss/TTL expiry, mutation sonrası invalidation, Redis restart/loss, canonical store failure, serializer ve ownership-key isolation entegrasyon testlerini ekle. Eviction testi paylaşılan developer Redis konfigürasyonunu değiştirmez.
9. docs/infrastructure/redis-conventions.md ve docs/architecture/cache-strategy.md içinde staleness/failure/ownership sınırlarını yaz; service DESIGN/ROADMAP ve Knowledge Base’i güncelle.

### Ek kabul ölçütleri

- Bir mevcut read use-case cache ile çalışıyor; canonical datastore korunuyor.
- Hit/miss/expiry ve Redis loss güvenli; mevcut mutation varsa invalidation testli.
- Write-through/write-around/hierarchy öğrenme karşılaştırması yapılmış; uygulanmayan stratejiler açık.
- Offer idempotency/rate limiting/Saga/distributed lock bu güne taşınmamış.

Kapanışta ilgili service ROADMAP/DESIGN belgeleri ve Knowledge Base gerçek implementation/kanıtlarla güncellenir. Önce ilgili GitHub CI başarılı olur; ardından local runtime/API doğrulaması yapılır. Bir Day bir milestone’dır; kapsam gerektiğinde birden fazla takvim gününde tamamlanabilir.
