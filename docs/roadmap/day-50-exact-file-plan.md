# Day 50 — Compose ortamı ve troubleshooting

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Containerize backend’i production-like yerel davranışla birlikte çalıştırmak.

Önkoşullar: [Day 49](day-49-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/50-compose-prodlike`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Compose profiles; health/dependency; network/volume; environment parity

## Uygulama görevleri

1. Day 45 servis/port/datastore/broker envanterini güncel Compose profillerine yansıt; baseline ve extended kullanımını kaynak bütçesine göre ayır.
2. Secret/non-secret config ayrımını ve Config Server/Vault erişimini container DNS ile kur; localhost hardcode temizliğini denetle.
3. Readiness/startup dependency sınırını yaz; depends_on tek başına sürekli availability garantisi değildir.
4. Volume ownership/retention ve ephemeral reset komutlarını ayır; destructive cleanup açık hedefli olur.
5. Mevcut kritik E2E akışlarını containerize ortamda çalıştır; tracing/correlation ve error semantics’i koru.
6. Port conflict, DNS, credential, startup timeout ve volume persistence runbook’larını gerçek tatbikatla doğrula.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/compose/`
- `docker-compose.yml`
- `docs/runbooks/compose-troubleshooting.md`
- `docs/testing/containerized-backend-e2e.md`

- `docs/evidence/day-50/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `config(compose): backend profillerini ve container DNSi hizala`
2. `config(compose): health volume ve secret sınırlarını tamamla`
3. `test(compose): containerize E2E ve restart doğrula`
4. `docs(compose): parity ve troubleshooting kanıtlarını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. docker compose config geçer; gerekli profile temiz ortamda açılır.
2. Backend kritik akışları container ortamında çalışır.
3. Seçilmiş dependency restart sonrası durable state/reliable outbound korunur.

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

Compose lokal ortamdır; Kubernetes scheduler/HA garantisi iddia edilmez.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
