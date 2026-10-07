# Day 38 — PropertyWatchService için Reactive Architecture

Durum: **Planlandı**. Branch: `day/38-reactive-webflux`.

## Amaç ve önkoşullar

Day 21 güvenilir Property projection/event akışı ve Day 14 identity modeli.

## Kapsam ve sahiplik sınırları

- PropertyWatchService yalnız WatchSubscription capability sahibi; Property sahipliği PropertyService’te kalır.
- Spring WebFlux + Project Reactor; persistence ihtiyacına göre gerçekten reactive datastore adapter seçilir; R2DBC zorunlu değil.
- Reactive path içinde blocking JDBC/OpenFeign, block(), blockFirst(), Thread.sleep() yok.
- SearchService imperative baseline korunur; virtual threads yalnız karşılaştırma konusu olabilir.

## Görevler

1. WatchSubscription create/remove/read gereksinimini ve Buyer ownership sınırını tanımla.
2. PropertyWatchService build/runtime, WebFlux ve Reactor bağımlılıklarını hazırlayıp MVC/blocking bağımlılık sızıntısını kontrol et.
3. WatchSubscription modelini ve reactive application port’larını oluştur; persistence seçiminde mevcut altyapıyı önce değerlendir.
4. Reactive datastore adapter’ını ve config’i oluştur; seçilen driver’ın gerçekten non-blocking olduğunu doğrula.
5. Property price/status integration event consumer’ını oluştur; broker teslim modelini Reactor pipeline ve acknowledge/retry davranışıyla hizala.
6. Filtering/matching pipeline’ını oluştur; duplicate event safety, ordering ve concurrency sınırlarını belirle.
7. Authentication ve subscription ownership kontrollerini reactive request context üzerinden uygula.
8. Blocking çağrı denetimi, bounded concurrency/backpressure ve error propagation testlerini ekle.
9. Aynı iş yükü/veri/ortam altında imperative vs reactive karşılaştırmalı load test yap; latency percentile, throughput ve kaynak kullanımını ölç.
10. Reactive seçim ADR’sini, ölçüm koşullarını ve SSE’nin Day 39’a ait olduğunu belgeleyerek Knowledge Base’i güncelle.

## Kapanış ölçütleri

- WatchSubscription dışında canonical sahiplik yok.
- Reactive path blocking çağrı içermiyor.
- Load test koşulları ve sonuçları kayıtlı; ölçülmemiş scale iddiası yok.

Ayrıntılı dosya/adapter planı, bağımlılık sırası, commit’ler ve doğrulama: [Day 38 kesin planı](day-38-exact-file-plan.md). Kapanış kanıtı oluşmadan bu capability tamamlandı veya Verified olarak işaretlenmez.
