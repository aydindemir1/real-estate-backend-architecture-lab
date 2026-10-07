# Day 47 — Dockerfile ve multi-stage Java build

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Gradle çıktısından tekrarlanabilir, çalışır servis image’ı üretmek.

Önkoşullar: [Day 46](day-46-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/47-docker-multistage-build`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- OCI image; Dockerfile; multi-stage; Java toolchain; build context

## Uygulama görevleri

1. Mevcut servislerin build/runtime gereksinimlerini çıkar; canonical implementation kodu Day 45 branch’inden gelir, planning branch’inin Day 1–6 kodu image kaynağı değildir.
2. Gradle Wrapper ve Java toolchain ile seçilen bir mevcut servis için builder/runtime stage oluştur; diğer servislere ortak standardı kademeli uygula.
3. Runtime image’a yalnız gereken artifact’i koy; debug source, build credential ve Gradle cache taşıma.
4. .dockerignore ile gereksiz context’i azalt; env/secret dosyalarını image/context’e dahil etme.
5. Image label, git SHA ve runtime version bilgisini standardize et; latest yerine explicit version/digest politikası seç.
6. Entrypoint ve executable artifact formatını gerçek Spring Boot çıktısıyla doğrula; IDE build çıktısına bağımlılık bırakma.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `Seçilen servis/Dockerfile`
- `Seçilen servis/.dockerignore`
- `docs/standards/container-build.md`
- `docs/testing/container-build.md`

- `docs/evidence/day-47/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(container): artifact ve image sözleşmesini tanımla`
2. `build(docker): multi-stage servis imageını ekle`
3. `build(docker): context ve runtime katmanlarını daralt`
4. `test(container): temiz build ve startup doğrula`
5. `docs(container): image standardını ve kanıtları kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Temiz checkout/Gradle Wrapper’dan image üret ve servisi başlat.
2. Image içinde secret, source ve build cache olmadığını incele.
3. Bozuk artifact/entrypoint’in görünür startup failure oluşturduğunu doğrula.

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

Cloud build servisi veya ikinci build sistemi eklenmez; Gradle korunur.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
