# Day 79 — Istio, service identity ve mTLS

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Mesh katmanında transport identity ve access policy öğrenmek.

Önkoşullar: [Day 58](day-58-exact-file-plan.md), [Day 59](day-59-exact-file-plan.md), [Day 60](day-60-exact-file-plan.md), [Day 78](day-78-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/79-istio-service-mesh`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Istio; workload identity; mTLS; AuthorizationPolicy; mesh/app retry boundaries

## Uygulama görevleri

1. Mesh’e alınacak iki mevcut internal service ve gerçek güvenlik use-case seç; bütün datastore/broker’ı zorla mesh’e alıp davranışı belirsizleştirme.
2. Sidecar/ambient seçeneklerini comparison ADR’de değerlendir; uyumlu tek model seç ve resource bütçesini ölç.
3. Workload identity/mTLS strictness ve authorization policy kur; JWT/business ownership owning app’te kalır.
4. Traefik cluster edge, Spring Cloud Gateway app edge, Istio internal mesh rollerini ayrı tut; ikinci ingress controller yolunu gereksiz açma.
5. Retry/timeout/circuit ownership’i mevcut Resilience4j/gRPC deadline ile hizala; duplicate retry storm yaratma.
6. Identity denial, non-mesh plaintext, certificate lifecycle ve mesh control-plane outage davranışını test et.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/kubernetes/platform/istio/`
- `infra/kubernetes/base/mesh-policy/`
- `docs/adr/service-mesh-boundaries.md`
- `docs/testing/mesh-identity.md`

- `docs/evidence/day-79/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(mesh): kapsam injection modeli ve policy ownerını tanımla`
2. `infra(istio): temsilci workload mesh temelini kur`
3. `feat(mesh): mTLS identity ve dar authorization ekle`
4. `test(mesh): denial plaintext retry ve outage doğrula`
5. `docs(mesh): overhead ve platform app ayrımını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. İzinli workload mTLS ile iletişim kurar; yanlış identity reddedilir.
2. Business role/ownership auth hâlâ owning service’te doğrulanır.
3. Mesh eklenince nested retry veya kaynak tüketimi kontrolsüz artmaz; outage davranışı kayıtlı.

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

Linkerd ikinci mesh olarak kurulmaz; Istio business auth/Vault/Calico’nun bütün sorumluluklarını devralmaz.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
