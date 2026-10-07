# Day 42 — Spring Batch ile raporlama ve stale listing tespiti

Durum: **Planlandı**. Branch: `day/42-spring-batch`.

## Amaç ve önkoşullar

Day 33 backend; Day 31 Cloud Task sorumluluk sınırı.

## Kapsam ve sahiplik sınırları

- Ana use-case market statistics reporting; gerçek Job/Step/ItemReader/ItemProcessor/ItemWriter/chunk semantics uygulanır.
- JobRepository, restartability/checkpoint, bounded retry/skip, job parameters ve idempotent rerun kapsamdadır.
- Stale listing önce tespit/candidate processing; otomatik withdraw yalnız açık iş politikası varsa.
- Spring Batch processing ile Spring Cloud Task kısa ömürlü execution/orchestration ayrı sorumluluklardır.

## Görevler

1. Rapor tüketicisi, tarih aralığı, kaynak verinin consistency/freshness sınırı ve çıktı sahipliğini belirle.
2. Job’un hangi mevcut modülde veya gerçekten gerekli reporting modülünde yaşadığını gerekçelendir; yeni scheduler/datastore zorunlu tutma.
3. Spring Batch bağımlılık/config ve JobRepository execution metadata persistence kararını oluştur.
4. Reader/Processor/Writer sınırlarını, chunk büyüklüğünü ve pagination yaklaşımını seç.
5. Job parameters için benzersizlik/aynı işin tekrar çalışması ve rapor idempotency politikasını uygula.
6. Transient error retry ve bozuk item skip sınırlarını uygula; yanlış veriyi sessizce yutma.
7. Checkpoint/restart sonrası tamamlanmış chunk’ların etkilerini iki kez üretmeyen recovery akışı kur.
8. Stale listing candidate üret; domain-state mutation yapma, otomatik withdraw için ayrı açık business policy şartını koru.
9. Job success/failure/restart, aynı parameter rerun, skip limit ve candidate-only behavior testlerini ekle.
10. Cloud Task/Batch sorumluluk ADR’sini ve rapor freshness/runbook/Knowledge Base belgelerini güncelle.

## Kapanış ölçütleri

- Gerçek Batch Job/Step/chunk execution doğrulanmış.
- Restart/rerun duplicate output üretmiyor.
- Stale listing tespiti açık politika olmadan domain state değiştirmiyor.

Ayrıntılı dosya/adapter planı, bağımlılık sırası, commit’ler ve doğrulama: [Day 42 kesin planı](day-42-exact-file-plan.md). Kapanış kanıtı oluşmadan bu capability tamamlandı veya Verified olarak işaretlenmez.
