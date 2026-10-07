# Day 80 — HPA ve capacity doğrulaması

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Ölçülen yük altında scaling davranışını ve sınırlarını görmek.

Önkoşullar: [Day 56](day-56-exact-file-plan.md), [Day 79](day-79-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/80-hpa-capacity`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Metrics Server; HPA stabilization; readiness; resource saturation; backpressure

## Uygulama görevleri

1. Seçilmiş stateless service için CPU/request/queue yük modelini tanımla; başlangıçta CPU HPA seç, custom metric eklemeyi ihtiyaçla gerekçelendir.
2. Metrics Server/HPA ve resource request ilişkisini doğrula; missing metric davranışını açıkla.
3. Min/max replica, scaling step ve stabilization window’u ölçüme göre seç; local node kapasitesini aşan Pending davranışını gizleme.
4. Load artışı/azalışında replica, latency/error ve canonical DB/broker downstream saturation ilişkisini gözle.
5. Scale-in shutdown/message handling’i Day 55 ile test et; replica artışı aynı key/lock/invariant için correctness bozmamalı.
6. VPA/cluster autoscaler karşılaştırma dokümanı hazırla; paid node autoscaling veya ikinci autoscaler zorunlu değildir.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/kubernetes/base/autoscaling/`
- `infra/kubernetes/platform/metrics-server/`
- `docs/testing/hpa-capacity.md`
- `docs/runbooks/scaling-saturation.md`

- `docs/evidence/day-80/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(scaling): workload ve kapasite varsayımını tanımla`
2. `config(hpa): request metric ve bounded scaling ekle`
3. `test(hpa): scale-out scale-in ve missing metric doğrula`
4. `docs(scaling): downstream saturation ve karşılaştırmayı kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Yük artınca bounded HPA scale-out, azalınca stabilization’a uygun scale-in görülür.
2. Missing metrics/node capacity sınırlaması görünür; sahte scale başarı iddiası yok.
3. Scale-in ve concurrent replica duplicate side-effect üretmez.

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

Local HPA çalışması sınırsız horizontal scaling veya physical HA kanıtı değildir.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
