# Day 51 — Minikube ve cluster mimarisi

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Yerel Kubernetes cluster’ını kontrollü ve tekrar üretilebilir başlatmak.

Önkoşullar: [Day 50](day-50-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/51-minikube-foundation`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Control plane; kubelet; etcd; CRI/containerd; kubectl contexts; Minikube

## Uygulama görevleri

1. Minikube driver, Kubernetes/runtime sürümü ve CPU/RAM/disk bütçesini belgelerle uyumlu seç; ücretli cloud cluster açma.
2. Calico ihtiyacını baştan değerlendirip cluster bootstrap’ta CNI seçimini explicit yap; default CNI’nin policy enforce ettiğini varsayma.
3. API server/controller/scheduler/kubelet/etcd ilişkisini namespace/node/pod envanteriyle göster.
4. kubectl context/namespace guard ve scoped cleanup komutlarını oluştur; kubeconfig credential’ını repository’ye koyma.
5. Cluster/node restart ve image pull erişimi için runbook hazırla; containerd/CRI ile Docker image build rolünü ayır.
6. Day 52 için ilk stateless servis image’ını cluster’da erişilebilir kıl; tüm datastore’ları bu güne yığma.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/kubernetes/lab/`
- `scripts/devops/start-minikube.sh`
- `docs/devops/kubernetes-architecture.md`
- `docs/runbooks/minikube.md`

- `docs/evidence/day-51/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(kubernetes): runtime CNI ve lab bütçesini tanımla`
2. `infra(minikube): explicit local bootstrap ekle`
3. `test(cluster): restart context ve image erişimi doğrula`
4. `docs(cluster): mimari ve troubleshoot sonuçlarını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Context doğru cluster’ı hedefler; health/node readiness görünür.
2. Minikube yeniden başlatılınca beklenen namespace/state davranışı kaydedilir.
3. Image pull hatası ve kubeconfig/yanlış context senaryosu teşhis edilir.

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

Minikube çok node desteği ayrı fiziksel failure domain veya HA control plane anlamına gelmez.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
