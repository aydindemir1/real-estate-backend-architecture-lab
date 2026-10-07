# Day 81 — Kubernetes observability, SLI/SLO ve alerting

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

App/cluster symptoms üzerinden deployment ve incident kararını ölçmek.

Önkoşullar: [Day 29](day-29-exact-file-plan.md), [Day 30](day-30-exact-file-plan.md), [Day 80](day-80-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/81-kubernetes-observability-slo`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- OTel Collector; Prometheus; Grafana; Loki/Tempo; SLI/SLO; Alertmanager; cardinality

## Uygulama görevleri

1. Backend OTel/metrics/logs/traces standardını K8s service/pod/node context ile zenginleştir; secret ve high-cardinality guard koru.
2. Prometheus service discovery/scrape ve OTel Collector pipeline kur; Grafana/Loki/Tempo mevcut seçimleri taşınır.
3. Pod restart/OOM/Pending, throttling, HTTP/gRPC latency/error, Kafka lag/Rabbit queue ve projection freshness dashboard’larını ilişkilendir.
4. Temsilci capability için SLI/SLO/error budget ölçümünü workload/ortam/süreyle tanımla; lab sonucu production SLO commitment değildir.
5. Alertmanager routing/grouping/inhibition ve runbook linkli actionable alert kur; dış kişiye e-posta/Slack göndermek zorunlu değil, local sink kullan.
6. Telemetry backend outage ve exporter backpressure’ın business flow’u bozmadığını test et.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/kubernetes/platform/observability/`
- `infra/observability/dashboards/`
- `infra/observability/alerts/`
- `docs/devops/sli-slo-error-budget.md`

- `docs/evidence/day-81/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `infra(observability): mevcut stacki Kubernetes ortamına bağla`
2. `feat(metrics): cluster app ve release bağlamını hizala`
3. `feat(alerts): SLI SLO ve actionable alert kur`
4. `test(telemetry): symptom ve backend outage davranışını doğrula`
5. `docs(sre): ölçüm varsayımları ve runbookları kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Git SHA/image digest ile trace/dashboard aynı release’i gösterir.
2. İzole bad release/backlog/OOM doğru symptom alert’i üretir.
3. OTel/Loki/Tempo outage canonical write’ı fail etmez; cardinality ve buffer bounded.

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

ELK/Datadog/New Relic/Zabbix ikinci monitoring stack’i olarak eklenmez; Alertmanager Prometheus alert delivery rolündedir.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
