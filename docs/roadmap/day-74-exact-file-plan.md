# Day 74 — Argo CD ve GitOps temeli

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Desired state’i Git’ten okuyarak cluster’da kontrollü reconcile etmek.

Önkoşullar: [Day 61](day-61-exact-file-plan.md), [Day 73](day-73-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/74-argocd-gitops-foundation`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Argo Application/AppProject; desired state; sync/health; RBAC; repo credentials

## Uygulama görevleri

1. Runtime GitOps kaynağını uygulama implementation/deployment branch’i olarak seç; docs/backend-roadmap-design yalnız plan taşır ve Argo tarafından deploy edilmez.
2. Argo CD local kurulum, repo erişimi ve AppProject namespace/kind allowlist tasarla.
3. Kustomize uygulama overlay’leri ve Helm platform chart ownership’i için Application modeli oluştur.
4. Başlangıçta manual sync ve dar permission kullan; prune/self-heal davranışlarını next milestone testinden sonra aç.
5. Jenkins release metadata’sından image digest desired-state değişikliği öner; cluster apply’ı Jenkins ile paralel çalıştırma.
6. Git→render→diff→sync→health zincirinde failure/log/audit kanıtlarını bağla.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/gitops/argocd/`
- `infra/gitops/applications/`
- `docs/architecture/gitops-ownership.md`
- `docs/runbooks/argocd-bootstrap.md`

- `docs/evidence/day-74/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(gitops): runtime repo branch ve permission kararını tanımla`
2. `infra(argocd): dar local controller ve project kur`
3. `feat(gitops): uygulama desired-state kaynaklarını bağla`
4. `test(gitops): sync health ve forbidden target doğrula`
5. `docs(gitops): Jenkins Argo ownership kanıtını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Argo doğru immutable revision/rendered manifest’i uygular.
2. Yanlış repo/path/namespace/kind explicit rejected veya sync failed olur.
3. Jenkins hesabı doğrudan production-like cluster deploy edemez.

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

Planning branch veya GitHub main runtime deploy kaynağı yapılmaz; Flux ikinci GitOps controller olarak kurulmaz.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
