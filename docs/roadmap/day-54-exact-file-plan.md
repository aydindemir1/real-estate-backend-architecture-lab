# Day 54 — ConfigMap, Secrets ve Vault entegrasyonu

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Konfigürasyonun ve secret’ın sahipliğini Kubernetes’te korumak.

Önkoşullar: [Day 53](day-53-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/54-kubernetes-config-secrets`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- ConfigMap/Secret; runtime config; Vault Kubernetes auth; rotation; RBAC

## Uygulama görevleri

1. Day 24 Vault ve Config Server kararını Kubernetes configuration/secret ownership matrisiyle eşleştir; ConfigMap’i ikinci canonical config kaynağı yapma.
2. Bootstrap config ve non-secret settings için ConfigMap kullan; secret plaintext veya base64 manifest’i Git’e koyma.
3. Vault’un Kubernetes auth yöntemini desteklenen sürümle doğrula; bound service account/namespace ve dar policy kur.
4. Vault Agent Injector yolunu tek delivery yaklaşımı olarak kullan; Spring Cloud Vault ile aynı secret için iki eşzamanlı fetch owner bırakma, seçimi ADR ile yap.
5. Rotation sonrası uygulama reload/restart davranışını ve kritik secret yokluğunda fail-fast sınırını belirle.
6. Başlangıçta gerekli RBAC izinlerini dar tut; tam platform RBAC denetimi Day 58’de yapılır.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/kubernetes/base/config/`
- `infra/kubernetes/platform/vault/`
- `docs/security/kubernetes-secret-ownership.md`
- `docs/runbooks/kubernetes-secret-rotation.md`

- `docs/evidence/day-54/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(secrets): config ve secret sahipliğini kilitle`
2. `config(kubernetes): bootstrap ConfigMap sınırını ekle`
3. `feat(vault): dar Kubernetes auth ve delivery yolunu kur`
4. `test(secrets): denial rotation ve outage doğrula`
5. `docs(secrets): injection ve reload kararını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Wrong service account/namespace secret’a erişemez.
2. Secret log/manifest/image’a sızmaz; rotation kanıtlı.
3. Vault outage/expired credential sırasında belirlenen fail-fast/degradation davranışı görülür.

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

Vault Dev Mode gerçek durable prod-like setup diye sunulmaz; unseal/recovery key repository’ye yazılmaz.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
