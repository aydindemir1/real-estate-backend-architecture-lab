# Day 82 — k6 ile performans ve release doğrulaması

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Deployment/scaling kararlarını tekrar üretilebilir workload ile değerlendirmek.

Önkoşullar: [Day 81](day-81-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/82-k6-performance-validation`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- k6 workload; p95/p99; throughput; error rate; warm-up; saturation

## Uygulama görevleri

1. Search/internal read/critical command için temsilci workload seç; mutation test data/idempotency ve cleanup sınırını tanımla.
2. k6 scenario, concurrency/arrival modeli, warm-up/süre ve threshold koşullarını gerçek baseline üzerinden belirle.
3. Local kaynak bütçesiyle normal/spike/soak testlerini bounded çalıştır; uzun soak ayrı opt-in olabilir.
4. HPA/canary/mesh before-after sonuçlarını aynı veri ve ortamda karşılaştır; farklı koşulları doğrudan üstünlük kanıtı sayma.
5. JVM/container/DB/pool/broker saturation nedenlerini trace/metrics ile ilişkilendir; bottleneck’i kör replica artışıyla çözme.
6. Release gate için uygun kısa workload seç; ağır load testini her PR’a zorla koyma; JMeter/Gatling karşılaştırması hazırla.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `tests/performance/k6/`
- `Jenkinsfile veya deployment doğrulama job`
- `docs/testing/performance-lab.md`
- `docs/adr/load-testing-tool.md`

- `docs/evidence/day-82/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(performance): workload baseline ve kaynak bütçesini tanımla`
2. `test(k6): temsilci load ve threshold scenariolarını ekle`
3. `test(performance): HPA canary ve mesh etkisini ölç`
4. `docs(performance): bottleneck ve JMeter Gatling karşılaştırmasını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Threshold failure deployment/promotion gate’e görünür yansır.
2. Measured latency/throughput/error ve resources aynı zaman aralığında raporlanır.
3. Load cleanup canonical dataset dışına zarar vermez; retries toplam yükte hesaba katılır.

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

k6 yanındaki JS test script’i frontend değildir; ücretli Grafana Cloud k6 gerekmez.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
