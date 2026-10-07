# Day 75 — Ortam izolasyonu, drift ve reconciliation

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

GitOps’un fark, prune ve self-heal davranışını kontrollü işletmek.

Önkoşullar: [Day 74](day-74-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/75-gitops-environments-drift`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- dev/staging/prod-like; namespaces; sync waves; prune/self-heal; drift

## Uygulama görevleri

1. dev/staging/prod-like overlay’lerini namespace/data/credential/resource isolation ile tanımla; aynı cluster farklı namespace production fiziksel izolasyonu değildir.
2. Helm release/resource ownership’i Argo altında render/reconcile modeline geçir; CLI Helm ve Argo aynı resource’u birlikte yönetmesin.
3. Config/secret/platform/app dependency için health ve sync ordering belirle; waves stateful readiness’i sihirli çözmez.
4. Manual drift oluşturup diff görünürlüğünü göster; seçilmiş stateless resource’larda self-heal uygula.
5. Prune politikasını PVC/namespace/critical stateful resource için ayrı koruma ve review ile tanımla.
6. Repo veya Argo outage sırasında çalışan app ile yeni deployment availability’sini ayır; recovery runbook yaz.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/kubernetes/overlays/`
- `infra/gitops/applications/`
- `docs/security/environment-isolation.md`
- `docs/runbooks/gitops-drift-prune.md`

- `docs/evidence/day-75/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(environments): isolation ve prune sınırını tanımla`
2. `config(gitops): overlay ownership ve sync ordering ekle`
3. `feat(gitops): seçilmiş self-heal ve drift policy uygula`
4. `test(gitops): drift prune ve repo outage doğrula`
5. `docs(gitops): recovery ve ortam kanıtlarını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Staging/prod-like credential ve datastore state’i yanlışlıkla paylaşılmaz.
2. Drift tespit ve self-heal beklenen alanla sınırlı.
3. Prune critical PVC’yi kontrolsüz silmez; repo outage çalışan workload’u gereksiz durdurmaz.

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

Namespace ayrımı ayrı cluster/host HA veya güvenlik garantisi olarak sunulmaz.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
