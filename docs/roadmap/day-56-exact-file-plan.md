# Day 56 — Kaynak yönetimi, JVM ve scheduling

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Container kaynak sınırının JVM, latency ve availability etkisini ölçmek.

Önkoşullar: [Day 55](day-55-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/56-resources-jvm-scheduling`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Requests/limits; QoS; CPU throttling; heap/native memory; scheduling/PDB

## Uygulama görevleri

1. Seçilmiş stateless Java service için idle/load heap/native memory/thread/connection bütçesini ölç.
2. Requests/limits’i ölçüme göre tanımla; JVM heap’i container limit’in tamamına eşitleme.
3. CPU throttling, OOMKilled ve scheduling Pending senaryolarını izole sınırlarla oluştur.
4. Replica/node placement, affinity/anti-affinity ve topology spread gereksinimini local node sayısıyla hizala.
5. PDB ile voluntary disruption sınırını test et; PDB’nin hardware/node failure’ı engellediğini iddia etme.
6. ResourceQuota/LimitRange adayını staging/prod-like namespace’de uygula; HPA Day 80’e önkoşul hazırla.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/kubernetes/base/resources/`
- `infra/kubernetes/overlays/lab/`
- `docs/testing/jvm-container-budget.md`
- `docs/runbooks/kubernetes-resource-pressure.md`

- `docs/evidence/day-56/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(resources): JVM ve scheduling bütçesini tanımla`
2. `config(resources): request limit ve namespace sınırlarını ekle`
3. `config(scheduling): gerekçeli placement ve PDB ekle`
4. `test(resources): throttling OOM ve disruption doğrula`
5. `docs(resources): ölçümleri ve scaling önkoşullarını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. CPU limit altında tail latency ve throttling raporlanır.
2. Memory pressure ve Pending nedeni events/metrics ile açıklanır.
3. Voluntary eviction/PDB testinde beklenen availability sınırı doğrulanır.

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

Çok node local test aynı host kaynaklarını paylaşır; fiziksel zone resiliency kanıtı değildir.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
