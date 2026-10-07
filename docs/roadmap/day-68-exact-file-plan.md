# Day 68 — Test pipeline ve raporlama

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Backend testlerini hızlı feedback ve gerçek correctness gate olarak CI’a bağlamak.

Önkoşullar: [Day 67](day-67-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/68-ci-test-pipeline`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Unit/ArchUnit/integration/contract/failure/E2E; parallelism; deterministic tests

## Uygulama görevleri

1. Day 26–33/45 Gradle test taxonomy’sini CI stage ve ağır test kaynak bütçesiyle eşleştir.
2. Unit/architecture hızlı stage, integration/contract/failure ve scoped E2E ağır stage olarak sırala.
3. Bağımsız suite’leri bounded parallel çalıştır; shared datastore/port/fixture çakışmasını engelle.
4. Testcontainers Docker erişimi ve image version/client compatibility’yi agent üzerinde doğrula.
5. JUnit/coverage/evidence/artifact retention politikasını kur; flaky rerun’u correctness çözümü olarak kullanma.
6. Negative test, timeout, skipped suite ve cancellation failure raporunu yayınla; publish stage test gate’e bağımlı olur.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `Jenkinsfile`
- `Gradle test task configuration`
- `docs/testing/ci-test-matrix.md`
- `docs/runbooks/ci-test-failure.md`

- `docs/evidence/day-68/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(ci-test): suite ve kaynak matrisini tanımla`
2. `feat(ci): hızlı ve ağır test stagelerini bağla`
3. `feat(ci): failure raporu ve artifact retention ekle`
4. `test(ci): negative flaky ve isolation gate doğrula`
5. `docs(ci-test): exact komutları ve teşhisi kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Bilerek bozulan domain/architecture/contract testi publish’i durdurur.
2. Ağır suite’ler izole dataset/port ile çalışır.
3. Testcontainers startup failure ve skipped critical suite başarılı sayılmaz.

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

JUnit/Testcontainers yerine JS/browser test stack’i eklenmez; mevcut testler implementation branch’inden alınır.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
