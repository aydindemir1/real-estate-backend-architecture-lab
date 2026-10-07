# Day 76 — Staging doğrulaması ve release promotion

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Test edilmiş digest’i review/kanıtla production-like desired state’e geçirmek.

Önkoşullar: [Day 75](day-75-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/76-staging-release-promotion`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Smoke/E2E gate; promotion; approval/audit; release concurrency; rollback pointer

## Uygulama görevleri

1. Staging acceptance checklist ve Day 73 release metadata/provenance ilişkisini kur.
2. Deployment sonrası selected HTTP/gRPC/messaging/security smoke/E2E kontrollerini çalıştır; deploy success tek başına pass değildir.
3. Başarısız smoke/scan/signature/gate durumda production-like update yapılmamasını uygula.
4. Promotion PR/review veya documented manual lab gate ile aynı digest’i prod-like overlay’e taşır; ücretli GitHub environment özelliği şart değildir.
5. Concurrent/stale release ve config-only update için desired-state sequencing kuralını belirle.
6. Previous healthy digest/config revision’a geri dönüş prosedürünü ve migration caveat’ını belgeleyerek Day 78’e bağla.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/releases/`
- `infra/gitops/promotion/`
- `scripts/devops/staging-smoke.sh`
- `docs/runbooks/release-promotion.md`

- `docs/evidence/day-76/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(promotion): gate ve revision sözleşmesini kilitle`
2. `feat(release): staging sonrası smoke E2E doğrulama bağla`
3. `feat(promotion): aynı digest için kontrollü desired-state güncelle`
4. `test(promotion): fail stale ve concurrency senaryolarını doğrula`
5. `docs(promotion): audit ve rollback pointer kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Stage’de test edilen digest prod-like’a aynen geçer.
2. Başarısız/unsigned/stale candidate promotion yapamaz.
3. Release→Git revision→cluster rollout→telemetry izlenebilir.

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

Bu plan canlı müşteriye otomatik publish izni değildir; local prod-like lab açıkça işaretlenir.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
