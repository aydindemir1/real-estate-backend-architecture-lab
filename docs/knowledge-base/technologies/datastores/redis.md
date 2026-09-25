# Redis

**Category:** Technology  
**Introduced:** Day 7  
**Project status:** Infrastructure Ready  
**Scope:** Ephemeral cache, rate limiting and acceleration layer

## 1. Nedir?

Redis, in-memory data structure store'dur.

Key-value kullanımının ötesinde çeşitli veri yapıları ve atomic operations sağlar.

## 2. Temel data structures

- String
- Hash
- List
- Set
- Sorted Set
- Stream
- Bitmap
- HyperLogLog

## 3. In-memory model

Data büyük ölçüde RAM üzerinde tutulur.

Bu nedenle çok düşük latency sağlar.

Ancak memory capacity ve eviction policy kritik hale gelir.

## 4. Event loop

Redis command processing uzun süre single-threaded event-loop modeliyle anılmıştır.

Modern Redis bazı I/O/auxiliary işlerde thread kullanabilir; fakat command execution semantics'i büyük ölçüde sequential atomic operation avantajı sağlar.

## 5. Atomic operations

Tek Redis command'ları atomic execution semantics'e sahiptir.

Bu özellik:
- counters
- rate limiting
- locks
- idempotency acceleration

gibi use-case'lerde faydalıdır.

## 6. Expiration

Key'lere TTL verilebilir.

Bu nedenle ephemeral state için uygundur.

## 7. Eviction

Memory limit dolduğunda policy'ye göre key eviction yapılabilir.

Bu yüzden Redis'in canonical correctness store olarak kullanılması dikkat ister.

## 8. Persistence seçenekleri

Redis:
- RDB snapshot
- AOF

gibi persistence seçenekleri sunabilir.

Ancak bu, her business use-case için relational/database durability eşdeğeri olduğu anlamına gelmez.

## 9. Replication

Primary-replica modeli kullanılabilir.

HA için Sentinel veya cluster seçenekleri değerlendirilebilir.

## 10. Redis Cluster

Data slot'lar üzerinden node'lara partition edilebilir.

Horizontal scale ve availability sağlar.

## 11. Bu projede nasıl kullanılıyor?

Day 7'de:
- Redis 8.x container
- persistent local volume
- redis-cli ping healthcheck
- dedicated Compose profile

hazırlanmıştır.

Application integration Day 13'e aittir.

## 12. Target use-case'ler

Redis:
- cache
- rate limiting
- idempotency acceleration
- ephemeral state

için kullanılacaktır.

## 13. Kritik architecture kuralı

Redis canonical business source of truth değildir.

Özellikle Offer creation idempotency correctness yalnız Redis'e bırakılmayacaktır.

Durable ownership BuyerService/Couchbase içinde tutulacaktır; Redis acceleration katmanı olabilir.

## 14. Cache kullanımında riskler

- stale data
- cache stampede
- invalidation
- eviction
- hot key
- memory pressure

## 15. Rate limiting

Atomic increment + TTL gibi primitive'ler distributed rate limit implementation için kullanılabilir.

Ancak exact algorithm:
- fixed window
- sliding window
- token bucket

seçimine göre değişir.

## 16. Avantajları

- çok düşük latency
- rich data structures
- TTL
- atomic operations
- mature ecosystem

## 17. Trade-off'ları

- memory cost
- eviction
- persistence semantics dikkat ister
- hot key
- cluster complexity
- cache consistency

## 18. Production considerations

- maxmemory
- eviction policy
- persistence
- replication
- Sentinel/Cluster
- key naming
- TTL
- monitoring
- security
- hot key analysis

## 19. Anti-pattern'ler

- Redis'i primary database gibi kullanmak
- TTL'siz unbounded cache
- giant values
- wildcard KEYS in production
- idempotency correctness'i yalnız volatile cache'e bırakmak

## 20. İleri öğrenme konuları

- RDB
- AOF
- replication
- Sentinel
- Cluster
- Lua/functions
- cache patterns
- distributed locks
