# Day 77 — Canary, blue-green ve Argo Rollouts

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Yeni sürüm etkisini trafik ve metrics ile sınırlayarak doğrulamak.

Önkoşullar: [Day 60](day-60-exact-file-plan.md), [Day 76](day-76-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/77-argo-rollouts-progressive-delivery`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Rollout CRD; blue-green/canary; AnalysisTemplate; traffic weights; abort

## Uygulama görevleri

1. Native RollingUpdate baseline ile blue-green/canary seçim kriterlerini karşılaştır; temsilci stateless service seç.
2. Argo Rollouts/Traefik seçilmiş sürümünün traffic-routing entegrasyonunu doğrula; CRD/provider mismatch’i ADR ile çözmeden weighted routing çalıştı deme.
3. Blue-green preview/active Service ve promotion akışını kur; iki deployment mekanizması aynı workload’u sahiplenmesin.
4. Canary için gerçek traffic weight kullan; replica oranının eşit traffic yüzdesi olduğunu varsayma.
5. Prometheus mevcut local baseline’ını analysis provider olarak bağla; Kubernetes geniş telemetry Day 81’de tamamlanır. Error/latency ve ölçüm sayısı/timeout sınırı tanımla.
6. Failed/insufficient analysis, abort ve rollback tatbikatı yap; missing data automatic success değil.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/gitops/rollouts/`
- `infra/kubernetes/base/analysis/`
- `docs/testing/progressive-delivery.md`
- `docs/runbooks/canary-abort.md`

- `docs/evidence/day-77/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(rollout): strategy ve traffic entegrasyonunu doğrula`
2. `feat(rollouts): preview active ve canary temelini ekle`
3. `feat(analysis): Prometheus promotion gate ekle`
4. `test(rollouts): weighted traffic bad canary ve abort doğrula`
5. `docs(rollouts): rollout kanıtı ve recovery kararını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Request ölçümleri gerçek eski/yeni traffic dağılımını gösterir.
2. Bad canary ve eksik metric analysis promotion’ı durdurur.
3. Blue-green cutover/abort sırasında ownership ve in-flight request correctness korunur.

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

Istio zorunlu traffic router olarak erkenden kurulmaz; seçilmiş Gateway API/provider desteği milestone başlangıcında test edilir.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
