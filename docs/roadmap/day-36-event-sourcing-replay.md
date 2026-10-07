# Day 36 — Event Sourcing replay, audit ve koşullu snapshot

Durum: **Planlandı**. Branch: `day/36-event-sourcing-replay`.

## Amaç ve önkoşullar

Day 35 event store ve deterministik rebuild doğrulanmış olmalı.

## Kapsam ve sahiplik sınırları

- Audit/history read API ham event store’u public API olarak açmaz.
- Snapshot bir optimizasyondur; stream uzunluğu/performance ihtiyacı olmadan production default değildir.
- Mevcut reliable outbound yolu korunur; replay integration event’lerini yeniden yayınlamaz.

## Görevler

1. Full replay ile current state karşılaştırmasının veri setini ve doğrulama ölçütlerini tanımla.
2. Aggregate rebuild doğrulama aracını mevcut uygulama sınırında oluştur; bozuk stream’i sessizce düzeltme.
3. Audit/history response modelini oluştur; pagination, field redaction ve ownership authorization uygula.
4. Eksik/tekrarlı sıra, tanınmayan event sürümü ve corruption için açık hata/runbook politikası belirle.
5. Event schema değişimi varsa eski stream’in okunma uyumluluğunu test et; migration/upcasting kararını ihtiyaçla ver.
6. Snapshot ihtiyacını mevcut stream uzunluğu ve rebuild süresiyle değerlendir; gerekmiyorsa uygulanmadığını belgeleyerek bırak.
7. Snapshot seçilirse snapshot version + kalan event’lerden rebuild yap; eski/bozuk snapshot için full replay fallback tanımla.
8. Snapshot sonucu ile full replay sonucunun eşitliğini, yanlış aggregate/version ve concurrency sınırlarını test et.
9. Audit API için başka kullanıcı, yetkisiz rol, eksik aggregate ve pagination testlerini ekle.
10. Replay prosedürünü, veri kaybı sınırlarını ve Knowledge Base etkisini belgeleyerek actual status kaydet.

## Kapanış ölçütleri

- Full replay deterministik ve yan etkisiz.
- Audit API ownership ve hassas veri sınırlarını koruyor.
- Snapshot uygulanmışsa full replay ile eşit; uygulanmamışsa durum açık.

Ayrıntılı dosya/adapter planı, bağımlılık sırası, commit’ler ve doğrulama: [Day 36 kesin planı](day-36-exact-file-plan.md). Kapanış kanıtı oluşmadan bu capability tamamlandı veya Verified olarak işaretlenmez.
