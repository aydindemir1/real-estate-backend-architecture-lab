# Day 49 — Container güvenliği ve kaynak sınırları

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Non-root ve sınırlı runtime ile JVM/servis davranışını doğrulamak.

Önkoşullar: [Day 47](day-47-exact-file-plan.md), [Day 48](day-48-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/49-container-hardening`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- UID/GID; read-only filesystem; capabilities; signals; JVM/container memory

## Uygulama görevleri

1. Runtime user ve yazılabilir tmp/log/volume ihtiyacını seç; non-root çalışmayı her uygulama için doğrula.
2. Read-only root filesystem, minimum capability ve no-new-privileges gibi desteklenen hardening ayarlarını gerekçelendir.
3. Secret’ların image’a gömülmediğini; stdout structured logging ve mask kurallarını kontrol et.
4. Memory/CPU sınırında JVM heap/native memory/headroom davranışını ölç; rastgele GC flag’leri ekleme.
5. Entrypoint signal forwarding ve graceful shutdown’ı in-flight HTTP/messaging işlemiyle test et.
6. Crash/OOM sonrası exit/restart davranışını kaydet; healthcheck ile container çalışıyor durumunu servis hazır durumundan ayır.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `Servis Dockerfile dosyaları`
- `infra/compose/container-hardening.yaml`
- `docs/runbooks/container-oom-shutdown.md`
- `docs/standards/container-security.md`

- `docs/evidence/day-49/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `feat(container): non-root ve dar filesystem izinleri uygula`
2. `config(container): kaynak ve security sınırlarını ekle`
3. `test(container): shutdown ve OOM davranışını doğrula`
4. `docs(container): hardening kararlarını ve rollbacki kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Non-root/read-only container gerekli read/write akışını tamamlar.
2. SIGTERM sırasında bounded shutdown ve mesaj idempotency korunur.
3. İzole memory limit/OOM tatbikatı teşhis edilir; secret sızıntısı yoktur.

## Kapanış ölçütleri

- [ ] Öğrenme konuları proje üzerinden açıklanabiliyor; karşılaştırma kararları gerekçeli.
- [ ] Görevler tamamlandı; hedef dosyalar actual implementation'a uyarlanıp commit edildi.
- [ ] Yukarıdaki doğrulamalar gerçek yerel ortamda çalıştırıldı; başarısız sonuçlar çözüm veya açık sınırlama olarak kaydedildi.
- [ ] Secret, token, private key ve kişisel veri evidence veya Git geçmişine girmedi.
- [ ] Kurulum, tekrar çalıştırma ve güvenli temizlik/runbook adımları doğrulandı; veri silme kapsamı açık.
- [ ] Gerekli CI kontrolleri geçti; sürüm/digest ve kaynak tüketimi kaydedildi.
- [ ] `Planlandı / Infrastructure Ready / Implemented / Integrated / Verified / Design Only` durumları yetenek bazında güncellendi; doğrulanmamış kapsam Verified olmadı.
- [ ] ADR, servis belgeleri, evidence ve Knowledge Base etki incelemesi tamamlandı.

## Kapsam sınırı

Container hardening service authorization yerine geçmez; distroless/Alpine/JRE varyantlarını paralel ürün olarak işletme.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
