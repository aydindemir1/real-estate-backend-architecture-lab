# Day 62 — Spring Cloud Kubernetes ve discovery/config sınırları

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Zorunlu Spring Cloud Kubernetes öğrenimini platform-native davranışla uygulamak.

Önkoşullar: [Day 53](day-53-exact-file-plan.md), [Day 54](day-54-exact-file-plan.md), [Day 61](day-61-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/62-spring-cloud-kubernetes`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Kubernetes discovery/config; Service DNS; Eureka/Config coexistence; RBAC

## Uygulama görevleri

1. Spring Boot/Cloud/Kubernetes uyumlu sürüm/BOM ve client seçimini doğrula; kullanılmayan runtime library ekleme.
2. Eureka/client LoadBalancer/Service DNS/discovery seçeneklerini mevcut call-path üzerinden karşılaştır ve bir temsilci path’in primary discovery owner’ını seç.
3. Bir mevcut internal read akışında Spring Cloud Kubernetes discovery capability’sini uygula; fallback/ikinci discovery kaynağını ancak açık geçiş kuralıyla tut.
4. Non-secret ConfigMap config capability’sini sınırlı temsilci setting üzerinde göster; Config Server ile aynı anahtarın precedence/refresh owner’ını kilitle.
5. Kubernetes API access permission, cache/watch/reload davranışı ve startup/outage sınırlarını minimal RBAC ile doğrula.
6. Mevcut Config/Eureka kararlarını topluca silmeden Kubernetes target architecture ADR’sini yaz; local Compose baseline için geçerli konfigürasyonu koru.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `Temsilci servis/build.gradle`
- `Temsilci servis Kubernetes configuration`
- `infra/kubernetes/base/rbac/`
- `docs/adr/spring-cloud-kubernetes-ownership.md`

- `docs/evidence/day-62/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(spring-cloud): discovery ve config sahipliğini belirle`
2. `build(spring-cloud): uyumlu Kubernetes client temelini ekle`
3. `feat(spring-cloud): temsilci discovery config akışını bağla`
4. `test(spring-cloud): RBAC refresh ve API outage doğrula`
5. `docs(spring-cloud): platform-native karşılaştırmasını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Service Pod değişimi sonrası seçilen discovery path çalışır.
2. Forbidden Kubernetes API/config failure belirlenmiş fail-fast/degradation üretir.
3. ConfigMap/Vault/Config Server precedence ve secret sınırı testli.

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

Spring Cloud Kubernetes kullanıldı diye bütün SC bileşenleri gereksiz ilan edilmez; alternatifler sorumluluk bazında değerlendirilir.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
