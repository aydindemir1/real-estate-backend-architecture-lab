# Day 60 — Gateway API, Traefik ve TLS lifecycle

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Cluster edge erişimini uygulama Gateway sorumluluğundan ayırmak.

Önkoşullar: [Day 59](day-59-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/60-gateway-api-tls`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- GatewayClass/Gateway/HTTPRoute; Traefik; cert-manager; local CA; TLS routing

## Uygulama görevleri

1. Traefik/Gateway API/cert-manager sürüm uyumluluğunu doğrula; deprecated community ingress-nginx ekleme.
2. GatewayClass/Gateway/HTTPRoute ile local host/path erişimini Spring Cloud Gateway’e yönlendir.
3. Cluster edge TLS/routing ile Spring Cloud Gateway auth/rate-limit/app routing sahipliğini belgeleyerek duplicate retry/auth policy önle.
4. cert-manager ile yerel test CA/issuer kur; private key’i Git’e yazma, external domain/sertifika satın alma.
5. Certificate trust/hostname/expiry/renewal ve route permission sınırlarını test et.
6. Weighted routing capability/Argo Rollouts entegrasyonunu implementation gününde doğrulanacak karar olarak kaydet; Day 77 için uyumluluk önkoşulu oluştur.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/kubernetes/platform/traefik/`
- `infra/kubernetes/platform/cert-manager/`
- `infra/kubernetes/base/gateway/`
- `docs/architecture/edge-routing-ownership.md`

- `docs/evidence/day-60/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(edge): platform ve application Gateway sınırını tanımla`
2. `infra(gateway-api): Traefik local routing temelini ekle`
3. `infra(tls): cert-manager local CA lifecycle kur`
4. `test(edge): routing trust ve renewal davranışını doğrula`
5. `docs(edge): traffic uyumluluğunu ve TLS runbookunu kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Doğru CA/hostname kabul edilir; yanlış hostname/untrusted CA reddedilir.
2. Route doğru Gateway’e gider; owning downstream authorization korunur.
3. Certificate rotation sonrası routing çalışır; secret masking doğrulanır.

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

Traefik Enterprise/Hub gerekmez; global DNS/CDN veya ücretli domain kurulmaz.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
