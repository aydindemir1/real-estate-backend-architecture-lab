# Day 84 — Platform upgrade ve yeniden kurulum

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Platform version değişikliği ve bileşen kaybında tekrar çalışır lab üretmek.

Önkoşullar: [Day 65](day-65-exact-file-plan.md), [Day 83](day-83-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/84-platform-upgrade-recovery`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Version compatibility; CRD upgrades; controller recovery; rebuild; rollback boundaries

## Uygulama görevleri

1. Kubernetes/JDK/Spring/Helm/Argo/Istio/Vault/registry version compatibility ve pinning envanterini güncelle.
2. Bir temsilci platform bileşeni için upgrade diff, CRD/schema/storage migration ve rollback sınırını belirle.
3. Minikube cluster rebuild ile gerçek HA control-plane/etcd recovery farkını belgeleyerek yalnız uygulanabilir lab tatbikatını seç.
4. Jenkins/Nexus/Harbor/Argo/Vault metadata/credential/state recovery envanterini oluştur; önce temsilci restore, sonra desteklenmiş diğer bileşenler.
5. Terraform/Ansible/bootstrap/Helm/GitOps zincirini clean local target’ta sırayla yürüt; Git’te olmayan secret/backup önkoşullarını açıklaştır.
6. Bad upgrade veya controller outage sonrası known-good version/state ve business verification akışını test et; salt startup pass yeterli değildir.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/kubernetes/platform/versions/`
- `infra/ansible/`
- `infra/terraform/`
- `docs/runbooks/platform-upgrade-rebuild.md`
- `docs/testing/platform-recovery.md`

- `docs/evidence/day-84/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(platform): version ve recovery compatibility matrisini tanımla`
2. `config(platform): gerekçeli pinning ve upgrade prosedürü ekle`
3. `test(platform): bad upgrade ve controller recovery doğrula`
4. `test(lab): sıralı clean rebuild tatbikatını çalıştır`
5. `docs(platform): actual recovery ve bilinen sınırları kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Temsilci upgrade ve geri dönüş compatibility/evidence ile doğrulanır.
2. Argo/controller restart sonrası ownership ve reconciliation korunur.
3. Clean rebuild selected E2E’yi tamamlar; kanıtlanmayan full-platform restore açık ayrılır.

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

Production control-plane HA, multi-zone failover veya bütün ürün upgrade’i tek local testten çıkarılmaz.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
