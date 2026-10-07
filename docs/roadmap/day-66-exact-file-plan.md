# Day 66 — Jenkins Pipeline-as-Code temeli

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Mevcut backend’in build/test lifecycle’ını self-hosted CI’a taşımak.

Önkoşullar: [Day 50](day-50-exact-file-plan.md), [Day 65](day-65-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/66-jenkins-foundation`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Declarative pipeline; controller/agent; Jenkinsfile; SCM credentials; reports

## Uygulama görevleri

1. Local self-hosted Jenkins kurulum, version/plugin/config inventory ve persistence modelini tanımla.
2. GitHub checkout için minimum credential ve private/public repo erişimini belirle; local webhook erişilemiyorsa SCM polling/manual trigger kullan.
3. Jenkinsfile’de Gradle Wrapper, toolchain, checkout SHA ve failure propagation temelini kur.
4. Controller üzerinde keyfi build koşturmayı sınırlayıp agent sınırını Day 67 için hazırla.
5. JUnit/build log/artifact raporunu secretsız sakla; timeout ve build cancellation davranışını tanımla.
6. Mevcut GitHub Actions kontrollerini silme; aynı kapsamlı pipeline’ı ikinci sistemde yeniden yazmak yerine rol ayrımını belgele.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `Jenkinsfile`
- `infra/jenkins/`
- `docs/devops/ci-responsibilities.md`
- `docs/runbooks/jenkins-bootstrap.md`

- `docs/evidence/day-66/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `infra(jenkins): local controller ve persistence temelini kur`
2. `feat(ci): checkout Wrapper ve test pipelineını ekle`
3. `test(ci): failure cancellation ve rapor davranışını doğrula`
4. `docs(ci): Actions Jenkins ayrımını ve trigger modelini kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Doğru SCM SHA checkout ve Wrapper build kanıtlı.
2. Build/test failure pipeline’ı failed yapar; swallowed exception yok.
3. SCM outage/credential hatası bounded ve redacted.

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

Jenkins ücretli agent/cloud servisi kullanmaz; localhost webhook erişimini varmış gibi iddia etme.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
