# Day 53 — Service, DNS ve internal communication

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Servisler arası HTTP/gRPC/broker bağlantılarını Kubernetes DNS ile kurmak.

Önkoşullar: [Day 52](day-52-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/53-kubernetes-networking`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- ClusterIP/headless; EndpointSlice; CoreDNS; ports; client/platform discovery

## Uygulama görevleri

1. Servis port/targetPort ve selector sözleşmesini mevcut backend envanteriyle eşleştir.
2. ClusterIP Service oluştur; Pod IP yerine stable DNS üzerinden bağlantı kur.
3. HTTP/gRPC ve Kafka/RabbitMQ bootstrap/advertised endpoint farklarını configuration inventory’de göster.
4. Başarısız DNS/yanlış selector/boş endpoints/yanlış port için diagnostic komutlarını belgeleyip dene.
5. Kubernetes Service load balancing ile Spring Cloud LoadBalancer/Eureka rollerini karşılaştır; Day 62’de discovery kararını kesinleştir.
6. Başlangıçta yalnız internal connectivity uygula; external routing Day 60’ta, NetworkPolicy enforcement Day 59’da tamamlanır.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/kubernetes/base/services/`
- `docs/architecture/kubernetes-networking.md`
- `docs/runbooks/kubernetes-dns-endpoints.md`

- `docs/evidence/day-53/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `feat(network): Service ve internal DNS sözleşmelerini ekle`
2. `config(network): HTTP gRPC ve broker endpointlerini hizala`
3. `test(network): DNS endpoint ve port failure doğrula`
4. `docs(network): discovery ve traffic sorumluluklarını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Pod değişse de Service DNS erişimi devam eder.
2. Yanlış selector/port/DNS açık hata ve bounded timeout üretir.
3. gRPC deadline ve nested retry ownership korunur.

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

Cloud LoadBalancer/DNS satın alınmaz; global traffic yönetimi ayrı tasarım karşılaştırmasıdır.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
