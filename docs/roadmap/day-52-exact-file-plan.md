# Day 52 — Deployment, ReplicaSet ve rollout temeli

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

İlk stateless Spring servisini Kubernetes workload olarak çalıştırmak.

Önkoşullar: [Day 51](day-51-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/52-kubernetes-workloads`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Pod/Deployment/ReplicaSet; labels/selectors; rollout; image digest

## Uygulama görevleri

1. Seçilen mevcut stateless service için Pod→Deployment ilişkisini ve replica/controller reconciliation davranışını tanımla.
2. Image, command, ports, env, labels ve service identity ile minimum manifest oluştur; canonical datastore’u Pod local disk’e taşımama kuralını koru.
3. Replica sayısını değiştirip pod delete sonrası controller replacement’ı gözle.
4. RollingUpdate maxSurge/maxUnavailable ve readiness önkoşullarını belgeleyip Day 55’e bağla.
5. Immutable artifact/digest politikasını tanımla; config farkı için her ortama ayrı image build etme.
6. kubectl rollout status/history ve başarısız image sürümü için geri dönüş runbook oluştur.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/kubernetes/base/seçilen-servis/`
- `docs/devops/workload-model.md`
- `docs/runbooks/basic-rollout.md`

- `docs/evidence/day-52/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `feat(kubernetes): ilk stateless Deployment temelini ekle`
2. `config(workload): replica label ve image sözleşmesini hizala`
3. `test(workload): replacement ve failed rollout doğrula`
4. `docs(workload): reconciliation ve rollback sınırlarını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Deployment istenen replica sayısına converge eder.
2. Yanlış image tag/pull veya startup failure görünür; hazırmış gibi işaretlenmez.
3. Pod replacement canonical data/invariant kaybı üretmez.

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

Argo CD ve progressive delivery bu güne eklenmez; önce native davranış öğrenilir.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
