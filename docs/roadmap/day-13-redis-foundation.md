# Day 13 — Redis Infrastructure Foundation

## Amaç
Redis'i canonical datastore olmadan ortak ephemeral infrastructure capability olarak hazırlamak.

## Görevler
1. Redis dependency alias/config ekle.
2. Redis container health doğrula.
3. typed Redis config oluştur.
4. serializer seçimini netleştir.
5. key namespace standardını kod/dokümana yansıt.
6. TTL convention oluştur.
7. Minimal Redis adapter ve seçilen mevcut read use-case için cache-aside sınırını oluştur.
8. connectivity integration test yaz.
9. TTL expiry test yaz.
10. serialization round-trip test yaz.
11. Redis'in canonical source olmadığını architecture test/docs ile doğrula.
12. Mevcut read cache kullanımını uygula; Offer idempotency Day 22, rate limiting Day 23 kapsamındadır.
13. docs güncelle.

## Önerilen commit'ler
1. build(redis): add Redis dependency
2. feat(redis): add typed Redis configuration
3. feat(redis): add key and serialization foundation
4. test(redis): add connectivity and TTL integration tests
5. docs(redis): document Redis role and conventions

## Doğrulama
- connect/read/write
- TTL expires
- serializer deterministic
- no business aggregate owned by Redis

## Tamamlanma durumu
Hedef kapanış: Redis foundation ve seçilen read cache doğrulanır; Redis canonical state sahiplenmez. Offer idempotency ve Gateway rate limiting sonraki günlerde uygulanır.

## Kesin file/config planı

Implementation için source of truth: `docs/roadmap/day-13-exact-file-plan.md`

## Onaylanan ek öğrenme ve uygulama kapsamı

- Cache-aside, write-through ve write-around stratejilerini karşılaştır; cache invalidation, TTL/eviction ayrımı ve cache hierarchy konularını Redis üzerinde öğren. CDN ve platform cache dağıtımı bu backend gününde kurulmaz.
- Day 9’da mevcut GetBuyerPreferences read use-case’ini aday olarak değerlendir; gerçek endpoint adı implementation envanterinden doğrulanır. Seçilirse BuyerService kendi cache adapter/config’ini sahiplenir; uygun değilse mevcut bir read use-case ADR ile seçilir. Yeni domain veya RedisService oluşturma.
- Cache port/adapter ve namespace’i dar tut: ortam + capability + owner/resource + payload version; token/secret anahtara yazma. Day 14 ownership kontrollerinden önce başka kullanıcı verisini paylaşan ortak anahtar kullanma; Day 43 tenant-scoped kaynaklarda tenant namespace eklenir.
- Cache-aside akışını uygula: hit → derived response; miss → canonical datastore read → TTL ile cache. Redis başarısızsa canonical datastore’a bounded fallback yap; datastore hatasını sahte cache success/empty response ile gizleme.
- Mevcut preferences update use-case’iyle invalidation bağlantısını kur: başarılı durable commit sonrasında key silme/version stratejisi. Update yoksa TTL staleness sınırını açıkça belgeleyip invalidation uygulandı iddiasında bulunma.
- Concurrent read/update → stale refill penceresini değerlendir; version/conditional write ya da kabul edilen bounded-staleness politikasını açıkça seç. Cache doğruluğu business invariant/Offer idempotency yerine geçmez.
- Eviction için maxmemory ve seçilen politika etkisini izole test config’inde incele; eviction ile TTL expiration farklıdır. Çok katmanlı cache yalnız karşılaştırma/tasarım; sırf göstermek için L1 ürün ekleme.
- Hit/miss/TTL expiry, mutation sonrası invalidation, Redis restart/loss, canonical store failure, serializer ve ownership-key isolation entegrasyon testlerini ekle. Eviction testi paylaşılan developer Redis konfigürasyonunu değiştirmez.
- docs/infrastructure/redis-conventions.md ve docs/architecture/cache-strategy.md içinde staleness/failure/ownership sınırlarını yaz; service DESIGN/ROADMAP ve Knowledge Base’i güncelle.

Güncel görevler, commit sırası ve ek kabul ölçütleri [kesin planda](day-13-exact-file-plan.md) yer alır. Bu ek kapsam **planlıdır**; doğrulama kanıtı oluşmadan tamamlandı olarak işaretlenmez.
