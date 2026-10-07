# Day 58 — RBAC, ServiceAccount ve Pod Security

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Platform ve workload erişimini least privilege ile sınırlandırmak.

Önkoşullar: [Day 54](day-54-exact-file-plan.md), [Day 57](day-57-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/58-rbac-pod-security`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Role/ClusterRole; RoleBinding; service account token; Pod Security Admission; quota

## Uygulama görevleri

1. Uygulama, platform operator, CI ve insan erişimi için permission matrisi çıkar; Jenkins/Argo hesaplarının gelecekteki kapsamını ayrı tut.
2. Namespace-scoped Role/Binding oluştur; cluster-admin’i default workflow yapma.
3. ServiceAccount token automount ihtiyacını her workload için değerlendir; ihtiyaç yoksa kapat.
4. Pod Security Admission seviyesini namespace rolüne göre seç; hostPath/privileged ihtiyacı olan platform bileşenlerini ayrı gerekçelendir.
5. Kubernetes API audit imkânı ve erişim denetimi kanıtını planla; sensitive secret output/log kapsamını daralt.
6. kubectl auth can-i, forbidden API isteği ve privileged Pod reddi ile kontrolleri doğrula.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/kubernetes/platform/rbac/`
- `infra/kubernetes/base/security/`
- `docs/security/kubernetes-permission-matrix.md`

- `docs/evidence/day-58/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(rbac): hesap ve permission matrisini tanımla`
2. `config(rbac): dar ServiceAccount Role ve Binding ekle`
3. `config(security): namespace Pod Security sınırlarını ekle`
4. `test(rbac): forbidden erişim ve privileged Pod reddini doğrula`
5. `docs(security): platform istisnalarını ve audit kanıtını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Uygulama hesabı başka namespace secret/deployment okuyamaz/değiştiremez.
2. Yetkisiz privileged workload reddedilir; gerekçeli platform istisnası ayrı.
3. Token/log/kubeconfig hassas değerleri evidence’e girmez.

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

RBAC network izolasyonu yerine geçmez; kullanıcı auth modelindeki Keycloak/RBAC korunur.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
