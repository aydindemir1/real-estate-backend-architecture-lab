# Day 34 — Anti-Corruption Layer ve koşullu Strangler Fig

Durum: **Planlandı**. Branch: `day/34-acl-strangler-fig`.

## Amaç ve önkoşullar

Day 33 doğrulanmış backend baseline; ExternalMLS sözleşmesi ve mevcut import yolu envanteri.

## Kapsam ve sahiplik sınırları

- ListingSyncService yalnız ExternalMLS entegrasyonunu ve çevirisini sahiplenir; PropertyService canonical Property sahibi olarak kalır.
- ExternalMLS modeli, internal canonical command/model ve integration event sözlüğünden ayrılır.
- Strangler Fig yalnız gerçekten mevcut bir legacy import yolu varsa etkinleşir; yeni adapter tek başına Strangler Fig değildir.

## Görevler

1. ExternalMLS örnek sözleşmesini, kimlik eşlemesini, veri kaynağını ve import iş kuralını belgeleyerek kapsamı kilitle.
2. ListingSyncService modülünü, build/config ve uygulama başlangıcını oluştur; domain/persistence seçimini gerçek ihtiyaçla gerekçelendir.
3. ExternalMlsClientPort, dış model DTO’ları ve HTTP adapter’ını oluştur; dış tipleri adapter sınırında tut.
4. CanonicalListingCommand ve çeviri bileşenini oluştur; ExternalMLS durum, para birimi ve zorunlu alanlarını internal modele dönüştür.
5. Kaynak kayıt kimliği + sürüm ile tekrar gönderim ve eski sürüm politikasını belirle; mevcut reliable outbound/inbox kurallarını kullan.
6. PropertyService’e erişimi mevcut command/application sınırı üzerinden yap; servisler arası veritabanı erişimi ekleme.
7. Timeout, authentication, geçersiz veri ve dış sözleşme sürüm uyuşmazlığını kararlı hatalara çevir.
8. Legacy yol gerçekten varsa routing/cutover ve geri dönüş planı oluştur; yoksa Strangler Fig durumunu koşullu/uygulanmadı olarak kaydet.
9. Çeviri birim testleri, dış contract adapter testleri, duplicate/stale kayıt ve dış sistem kesinti testlerini ekle.
10. ArchUnit ile ExternalMLS DTO’larının Property domain’e sızmadığını doğrula; ADR ve Knowledge Base etkisini güncelle.

## Kapanış ölçütleri

- Dış sözlük domain’e sızmıyor; Property sahipliği korunuyor.
- Import tekrarları güvenli; dış sistem hataları sınırlandırılmış.
- Strangler Fig iddiası gerçek legacy yol ve geçiş kanıtına dayanıyor.

Ayrıntılı dosya/adapter planı, bağımlılık sırası, commit’ler ve doğrulama: [Day 34 kesin planı](day-34-exact-file-plan.md). Kapanış kanıtı oluşmadan bu capability tamamlandı veya Verified olarak işaretlenmez.
