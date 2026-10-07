# Day 72 — Trivy, SBOM, Cosign ve provenance

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Release edilen artifact’in güvenlik ve kaynak izlenebilirliğini doğrulamak.

Önkoşullar: [Day 71](day-71-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/72-supply-chain-security`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Trivy; CycloneDX; Cosign; signatures; provenance; vulnerability policy

## Uygulama görevleri

1. SBOM scope/format ve dependency license inventory’yi tanımla; Gradle/CycloneDX ile uygulama envanteri, Trivy ile image bulgularını eşleştir.
2. Harbor Trivy scanner ve CI scan rollerini aynı scan motoru ile ayır; farklı scanner ürünleri paralel ekleme.
3. Severity/fix availability/exception owner/expiry politikasını yaz; bilinmeyen scanner failure’ı clean rapor sayma.
4. Cosign local key-backed imza akışını kur; private key’i Vault/korumalı credential yolunda tut, ücretsiz eğitim için keyless public servis zorunlu yapma.
5. Git SHA/JDK/build recipe/test/scan/SBOM digest ve image digest ilişkisinden provenance üret; SLSA sertifikasyonu iddia etme.
6. Publish/promotion öncesi signature/provenance doğrulama gate’ini ekle; cluster admission enforcement bu gün uygulanmadıysa ayrı durum belirt.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `Jenkinsfile`
- `infra/harbor/scanning/`
- `docs/security/supply-chain-policy.md`
- `docs/testing/signature-sbom-provenance.md`

- `docs/evidence/day-72/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(supply-chain): scan exception ve provenance politikasını tanımla`
2. `feat(sbom): artifact ve image envanterini bağla`
3. `feat(signing): Cosign digest imza ve verify akışı ekle`
4. `feat(ci-security): scan signature ve provenance gate ekle`
5. `test(supply-chain): tamper unsigned ve scanner failure doğrula`
6. `docs(supply-chain): kanıtları ve admission sınırını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Tampered/unsigned image promotion gate’inden geçmez.
2. Scanner timeout/DB failure release’i policy’ye göre durdurur.
3. SBOM ve provenance ilgili digest’i gösterir; exception süresi ve imza key rotasyonu kayıtlı.

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

Ücretli Snyk/Sonar özelliği zorunlu değil; imzalı artifact zararsız artifact garantisi değildir.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
