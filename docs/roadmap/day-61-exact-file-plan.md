# Day 61 — Helm ve Kustomize ile deployment sahipliği

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Manifest paketleme ve ortam farklarını tek sahiplikle yönetmek.

Önkoşullar: [Day 60](day-60-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/61-helm-kustomize`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Helm release/values; Kustomize base/overlay; rendering; version pinning

## Uygulama görevleri

1. Kendi service manifest’leri için Kustomize base/overlay; üçüncü taraf platform ürünleri için Helm kararını kilitle.
2. Day 52–60 manifest’lerini bu sahiplik modeline taşı; aynı resource’u Helm ve Kustomize birlikte yönetmez.
3. Helm chart/values sürümünü pinle; chart default’una kör güvenmeden rendered manifest incele.
4. dev/staging/prod-like ortam farklarını image/config/resource olarak ayır; business behavior farkı için profile çoğaltma.
5. Render/lint/schema validation komutlarını ve kube version compatibility matrisini hazırla.
6. Temiz kurulum/upgrade/rollback testlerini scoped lab namespace’de yap; Argo CD ile ownership devri Day 74’te tamamlanır.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/kubernetes/base/`
- `infra/kubernetes/overlays/`
- `infra/kubernetes/platform/helm-values/`
- `docs/devops/manifest-ownership.md`

- `docs/evidence/day-61/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(manifests): Helm Kustomize sahiplik kararını tanımla`
2. `refactor(manifests): uygulama base ve overlaylerini düzenle`
3. `config(helm): platform chart ve values sürümlerini pinle`
4. `test(manifests): render validate install ve rollback doğrula`
5. `docs(manifests): ownership ve upgrade sınırlarını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Kustomize overlay rendering aynı base üzerinden deterministik.
2. Helm chart değişikliği review edilebilir; secrets rendered Git çıktısında yok.
3. Bir resource’un tek manager/owner’ı var; rollback veri kaybı gibi sunulmuyor.

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

Helm/Jsonnet/Kustomize alternatiflerine paralel app deployment sistemleri kurulmaz.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
