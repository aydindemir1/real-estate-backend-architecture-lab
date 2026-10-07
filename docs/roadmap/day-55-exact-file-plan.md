# Day 55 — Probes, rollout ve graceful shutdown

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Yeni sürüme geçerken trafik ve mesaj correctness korumak.

Önkoşullar: [Day 52](day-52-exact-file-plan.md), [Day 54](day-54-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/55-probes-graceful-shutdown`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Startup/readiness/liveness; terminationGracePeriod; draining; consumer shutdown

## Uygulama görevleri

1. Spring Actuator probe endpoint’lerini ve authorization/exposure sınırını seç; dependency outage’ı her zaman liveness failure yapma.
2. Startup/readiness/liveness ayarlarını gerçek startup ölçümüyle belirle; uydurma süreleri final kabul etme.
3. Readiness düşürme, HTTP keep-alive/SSE/gRPC connection draining ve graceful shutdown akışını teste bağla.
4. Kafka/RabbitMQ tüketicisinde in-flight iş/ack/retry ve Outbox dispatch kapanışını doğrula.
5. Rolling update sırasında maxUnavailable ve termination grace bütçesini uygulama shutdown süresiyle hizala.
6. Bad readiness/slow startup/hung process için bounded teşhis ve önceki sürüme dönüş runbook oluştur.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/kubernetes/base/*/deployment.yaml`
- `Servis application configuration`
- `docs/runbooks/kubernetes-probes-shutdown.md`
- `docs/testing/rolling-update-correctness.md`

- `docs/evidence/day-55/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `config(probes): health ve startup ownership tanımla`
2. `feat(runtime): gerekli graceful shutdown davranışını tamamla`
3. `config(rollout): termination ve availability bütçesini hizala`
4. `test(rollout): request ve messaging correctness doğrula`
5. `docs(rollout): başarısız probe recoverysini kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Yavaş startup gereksiz restart döngüsüne girmez.
2. Dependency outage canonical service’i gereksiz liveness restart’a zorlamaz.
3. SIGTERM/rollout sırasında double side-effect ve kontrolsüz request loss yok; SSE reconnect sınırı belgeli.

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

Readiness başarı yanıtı business correctness veya bütün dependency’lerin sağlığı anlamına gelmez.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
