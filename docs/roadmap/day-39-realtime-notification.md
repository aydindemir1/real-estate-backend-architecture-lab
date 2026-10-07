# Day 39 — SSE ile gerçek zamanlı Property bildirimleri

Durum: **Planlandı**. Branch: `day/39-realtime-notification`.

## Amaç ve önkoşullar

Day 38 WatchSubscription/reactive matching ve ownership doğrulanmış olmalı.

## Kapsam ve sahiplik sınırları

- Primary client delivery Server-Sent Events; WebSocket yalnız gerçek bidirectional requirement varsa değerlendirilir.
- Kafka event’leri client’a doğrudan açılmaz; client-facing notification sözleşmesine dönüştürülür.
- Ayrı notification-history datastore yalnız gerçek ihtiyaçla seçilir; gösterim için eklenmez.

## Görevler

1. SSE endpoint, authentication yöntemi ve client-facing notification sözleşmesini tanımla; native EventSource header sınırlarını hesaba kat, token’ı URL/loglara sızdırma.
2. Day 38 matching çıktısını safe notification DTO’ya dönüştür; internal payload/kişisel veri sızıntısını engelle.
3. Authenticated Buyer için yalnız kendi WatchSubscription stream’ini aç; başka kullanıcı erişimini reddet.
4. Connect/disconnect/reconnect ve reactive resource cleanup davranışını uygula.
5. Slow-consumer için bounded buffer/backpressure, overflow/disconnect ve reconnect davranışını belirle.
6. Event identity ve Last-Event-ID semantics’i değerlendirilip garanti sınırını yaz; geçmiş tutulmuyorsa kayıpsız replay iddiasında bulunma.
7. Duplicate upstream event ve concurrent bağlantıların delivery davranışını tanımla.
8. Ownership, invalid token, stream cancellation ve subscription removal testlerini ekle.
9. Reconnect, duplicate, slow consumer ve bağlantı sonrası kaynak sızıntısı entegrasyon testlerini yaz.
10. REST/gRPC/Kafka ile SSE sorumluluk farkını ve delivery/runbook sınırlarını Knowledge Base’e yansıt.

## Kapanış ölçütleri

- Başka Buyer’ın bildirimi okunamıyor.
- Slow consumer sınırsız memory büyümesi yaratmıyor.
- Reconnect garantisi testlerle aynı; cancellation kaynakları temizliyor.

Ayrıntılı dosya/adapter planı, bağımlılık sırası, commit’ler ve doğrulama: [Day 39 kesin planı](day-39-exact-file-plan.md). Kapanış kanıtı oluşmadan bu capability tamamlandı veya Verified olarak işaretlenmez.
