# Day 59 — Calico ve NetworkPolicy

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Servislerin network erişimini izin verilen dependency grafiğiyle sınırlandırmak.

Önkoşullar: [Day 51](day-51-exact-file-plan.md), [Day 53](day-53-exact-file-plan.md), [Day 58](day-58-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/59-calico-network-policy`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- CNI; ingress/egress policy; selectors; DNS exceptions; default deny

## Uygulama görevleri

1. Day 51 CNI kurulumunun Calico olduğunu ve policy enforcement’ın gerçekten etkin olduğunu doğrula.
2. Servis→datastore/broker/config/Vault/telemetry DNS ve port dependency matrisini çıkar.
3. Scoped default-deny ingress/egress ve gereken açık allow kurallarını oluştur; CoreDNS erişimini unutma.
4. Gateway→downstream ve izin verilen internal HTTP/gRPC bağlantılarını public API authorization’dan ayrı değerlendir.
5. Policy değişikliği için deny-first failure ve rollback prosedürünü hazırla; telemetry/CI/platform egress’ini kontrolsüz all-allow yapma.
6. Allow/deny pod fixtures üzerinden gerçek bağlantı testleri yaz; YAML lint’i network isolation kanıtı sayma.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/kubernetes/base/network-policy/`
- `infra/kubernetes/platform/calico/`
- `docs/security/network-access-matrix.md`
- `docs/testing/network-policy.md`

- `docs/evidence/day-59/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(network-policy): dependency erişim grafiğini tanımla`
2. `config(calico): CNI enforcement baselineını doğrula`
3. `config(network-policy): default deny ve dar allow kuralları ekle`
4. `test(network-policy): gerçek allow deny bağlantılarını doğrula`
5. `docs(network-policy): rollback ve troubleshooting kanıtını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. İzinli Service bağlantısı çalışır; aynı endpoint’e yetkisiz workload bağlanamaz.
2. DNS/Vault/OTel izinleri doğru; gereksiz dış egress engellenir.
3. Yanlış policy rollout kontrollü geri alınır ve teşhis kaydı oluşur.

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

Cilium/başka CNI kurulmaz; alternatifler karşılaştırılır. NetworkPolicy L7/identity/mTLS’nin tamamı değildir.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
