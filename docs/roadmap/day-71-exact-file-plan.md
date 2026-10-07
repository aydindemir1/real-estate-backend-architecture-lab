# Day 71 — Harbor ve OCI image lifecycle

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Image’ların kimlik, izin ve retention yönetimini yerel registry’de kurmak.

Önkoşullar: [Day 70](day-70-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/71-harbor-image-registry`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Harbor projects; robot accounts; digest/tag; TLS trust; retention/immutability

## Uygulama görevleri

1. Harbor local kurulumu, storage ve certificate trust’ı Docker/Jenkins/Minikube için planla.
2. Project/robot read/push izinlerini ve image pull secret ownership’ini least privilege ile oluştur.
3. Image tag/git SHA ile digest ilişkisini kaydet; deployment identity digest olur, mutable latest olmaz.
4. Image retention/immutability ve garbage collection politikasını aktif rollback digest’lerini koruyarak seç.
5. Day 72 Trivy/Cosign entegrasyonu için registry scan/sign metadata sınırını hazırla.
6. Push/pull/TLS/credential outage ve retry behavior’ını scoped testlerle doğrula.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/harbor/`
- `Jenkinsfile`
- `infra/kubernetes/base/image-pull/`
- `docs/runbooks/harbor-registry.md`

- `docs/evidence/day-71/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `infra(harbor): local registry TLS ve persistence temelini kur`
2. `config(registry): project robot ve pull permissionlarını ayır`
3. `feat(ci-image): digest metadata ile image publish bağla`
4. `test(registry): TLS permission ve retention doğrula`
5. `docs(harbor): recovery ve rollback image politikasını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Jenkins push ve Kubernetes pull yalnız izinli project’te çalışır.
2. Yanlış CA/credential/project denied; secretsız diagnostic vardır.
3. Retention rollback için gereken image’ı kontrolsüz kaldırmaz.

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

Harbor OCI, Nexus Java artifact rolünü korur; paid registry veya public secret yok.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
