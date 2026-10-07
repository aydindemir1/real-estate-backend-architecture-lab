# Day 70 — Nexus ile Java artifact ve dependency yönetimi

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Dependency proxy ve snapshot/release artifact sözleşmesini işletmek.

Önkoşullar: [Day 69](day-69-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/70-nexus-artifact-management`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Maven-format hosted/proxy/group; Gradle publish; immutable releases; retention

## Uygulama görevleri

1. Nexus’un ücretsiz self-hosted dağıtımının güncel kullanım sınırlarını ve local kaynak bütçesini implementation başlangıcında doğrula.
2. Dependency proxy/group ve snapshot/release hosted repository rollerini ayrı tanımla; Docker registry olarak Harbor rolünü devralma.
3. Gradle dependency resolution ve artifact publish endpoint’lerini repository ownership ile hizala.
4. Jenkins read/publish credential permission’ını ayır; production-like release üzerine overwrite etmeme politikasını seç.
5. GAV/version/git SHA/checksum metadata ve artifact retention/cleanup politikasını oluştur.
6. Proxy cache/repository outage ve yanlış publish permission için failure testleri ekle; dependency verification korunur.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/nexus/`
- `Gradle repository/publishing configuration`
- `Jenkinsfile`
- `docs/runbooks/nexus-artifact-recovery.md`

- `docs/evidence/day-70/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `infra(nexus): repository ve permission temelini kur`
2. `build(artifact): Gradle proxy ve publish akışını bağla`
3. `feat(ci-artifact): metadata ve snapshot release gate ekle`
4. `test(artifact): overwrite permission ve outage doğrula`
5. `docs(nexus): retention ve recovery kararını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Gradle gerekli dependency’yi proxy/group üzerinden çözer.
2. Snapshot/release hedefi ve tekrar publish politikası testli.
3. Unprivileged account publish yapamaz; outage fake success üretmez.

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

Harbor ile aynı OCI registry rolü iki kez işletilmez; ücretli Nexus özelliği planın önkoşulu yapılmaz.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
