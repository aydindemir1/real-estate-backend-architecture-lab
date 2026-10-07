# Day 48 — BuildKit, cache ve build secrets

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Cache avantajını ölçerken build correctness ve secret isolation korumak.

Önkoşullar: [Day 47](day-47-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/48-buildkit-cache`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- BuildKit cache mount; layer invalidation; dependency verification; secret mount

## Uygulama görevleri

1. Day 47 image katmanlarını dependency/code değişiklikleri açısından incele; hatalı cache reuse pencerelerini belirle.
2. Gradle dependency ve build cache için dar BuildKit cache mount kullan; cache key/ownership ve concurrency davranışını yaz.
3. Cold/warm build sürelerini aynı ortamda ölç; sadece süreye bakıp test/config değişikliklerini cache ile atlama.
4. Gereken özel repository credential’ını ARG/ENV yerine build secret ile aktar; henüz Nexus yoksa test fixture ile sınırı doğrula.
5. Wrapper/plugin/BOM pinning ve dependency verification adayını değerlendir; verification metadata’nın bakım politikasını belirle.
6. Cache loss, repository outage ve dependency mismatch için açık failure/fallback sınırlarını belgeleyerek Jenkins kullanımına hazırla.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `Seçilen servis/Dockerfile`
- `gradle/verification-metadata.xml`
- `docs/devops/build-cache.md`
- `docs/testing/build-cache-verification.md`

- `docs/evidence/day-48/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(build): cache ve dependency doğrulama politikasını tanımla`
2. `build(buildkit): Gradle cache ve secret mount ekle`
3. `build(gradle): gerekçeli verification temelini ekle`
4. `test(build): cache invalidation ve secret isolation doğrula`
5. `docs(build): cold warm ölçümleri ve outage sınırlarını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Source/dependency/config değişince gerekli katman yeniden build edilir.
2. Cache silindikten sonra build doğruluğu korunur.
3. Build log/image history içinde secret bulunmaz; mismatch gate başarısız olur.

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

Remote paid cache gerekmez; cache yokluğu doğruluğu etkilemez. Aynı cache alanına kontrolsüz untrusted build erişimi verilmez.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
