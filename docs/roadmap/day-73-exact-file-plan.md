# Day 73 — Build once ve release contract

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Aynı doğrulanmış artifact’i bütün ortamlara taşıyacak release metadata kurmak.

Önkoşullar: [Day 72](day-72-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/73-build-once-artifact-promotion`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Immutable artifact; release manifest; digest; traceability; CI/CD separation

## Uygulama görevleri

1. JAR GAV/checksum→image digest→SBOM/signature/provenance→SCM SHA→test evidence release sözleşmesini oluştur.
2. Staging/prod-like ortamları için config/resource farkını artifact rebuild’den ayır.
3. Jenkins’in release candidate üretme ve GitOps desired-state değişikliği önerme yetkisini tanımla; cluster-admin deploy yetkisi verme.
4. Release metadata dosyasını atomic/izlenebilir yayınla; secret veya kişisel payload koyma.
5. Duplicate build/retry/concurrent release version conflict politikasını belirle; eski candidate yeni desired state’i ezmesin.
6. Sonraki GitOps fazına aynı digest ve kanıt aktarımını test et; Day 76 promotion için stage contract kilitle.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/releases/`
- `Jenkinsfile`
- `docs/devops/release-contract.md`
- `docs/testing/build-once-promotion.md`

- `docs/evidence/day-73/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(release): artifact metadata ve stage sözleşmesini tanımla`
2. `feat(ci-release): immutable candidate manifest üret`
3. `test(release): digest retry ve version conflict doğrula`
4. `docs(release): CI CD permission ayrımını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. İki ortamda aynı image digest ve uygulama version görülebilir.
2. Failed gate artifact’i candidate/promotion olarak seçilemez.
3. Release retry idempotent; stale candidate hedef version’ı ezmez.

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

CD artefact’i yeniden build etmez; environment config için separate binary üretilmez.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
