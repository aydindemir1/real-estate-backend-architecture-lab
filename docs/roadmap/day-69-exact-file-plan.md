# Day 69 — SonarQube Community Build ve JaCoCo

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Ücretsiz statik analiz ve coverage gate’i somut failure ile doğrulamak.

Önkoşullar: [Day 68](day-68-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/69-sonarqube-quality-gate`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Quality profile/gate; JaCoCo XML; main-project analysis; scanner credentials

## Uygulama görevleri

1. SonarQube Community Build local kurulumu ve Java/Gradle scanner compatibility’yi doğrula; ücretli PR/multibranch özelliklerini scope’a alma.
2. JaCoCo XML kapsamını service/module testleriyle hizala; generated code exclusion gerekçesini kaydet.
3. Canonical uygulama milestone branch’ini ayrı Sonar project’in main analysis kaynağı olarak belirle; GitHub main branch’i merge/değiştirme zorunluluğu yok.
4. Quality gate’i yeni/ilgili kod ve gerçek risklerle tanımla; uydurma coverage sayısını otomatik başarı ölçütü yapma.
5. Jenkins analysis submit→quality gate wait ve auth/webhook veya desteklenen polling yöntemini local topology’ye göre kur.
6. Temsilci maintainability/security bulgusunu düzelt; coverage eksikliği ve gate failure’da publish durduğunu test et.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/sonarqube/`
- `Jenkinsfile`
- `Gradle JaCoCo/scanner configuration`
- `docs/devops/quality-gate-policy.md`

- `docs/evidence/day-69/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `infra(sonarqube): ücretsiz local analysis temelini kur`
2. `build(coverage): JaCoCo XML raporlarını bağla`
3. `feat(ci-quality): analysis ve gate bekleme stageini ekle`
4. `test(quality): gate failure ve missing coverage doğrula`
5. `docs(quality): Community kapsamını ve düzeltmeleri kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Coverage XML doğru modüllerden okunur; missing report gizlenmez.
2. Bilerek gate fail publish’i durdurur.
3. Community native branch/PR analysis uygulanmış gibi gösterilmez; scanner token redacted.

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

Sonar architecture/authorization correctness’ın tamamını kanıtlamaz; ArchUnit ve security testleri korunur.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
