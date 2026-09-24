# Day 13 — Exact File / Class / Commit Plan

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
- cache-aside business flow yok
- Saga state yok
- canonical business state yok

## Task 1 — Redis ownership audit

Review:
- hangi service'ler ileride Redis kullanacak
- current root/common dependency policy
- config ownership

Initial consumers later:
- BuyerService -> idempotency
- ApiGatewayService/resilience layer -> rate limiting
- selected read path -> cache candidate

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

Day 13 actual executable key should be neutral/test-only, for example:
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

Day 24 owns full metrics.

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

No cache/idempotency/rate-limit endpoint added.

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

1. build(redis): add Redis dependency catalog — only if missing
2. config(redis): add typed Redis configuration
3. feat(redis): define serialization strategy
4. feat(redis): add Redis key naming foundation
5. feat(redis): add TTL convention
6. feat(redis): add minimal ephemeral key-value port
7. feat(redis): add Redis ephemeral adapter
8. config(redis): add external Redis settings
9. test(redis): add connectivity integration test
10. test(redis): verify TTL behavior
11. test(redis): verify serialization behavior
12. test(redis): enforce non-canonical Redis role
13. docs(redis): document Redis infrastructure conventions

Adjacent technical commits may be merged when small and cohesive.

## Explicitly Deferred from Day 13

Do not implement:
- Offer idempotency store
- Idempotency-Key workflow
- Gateway rate limiting
- Property cache
- Seller cache
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
- no idempotency/rate-limit/cache feature leaks into Day 13
- docs match actual implementation