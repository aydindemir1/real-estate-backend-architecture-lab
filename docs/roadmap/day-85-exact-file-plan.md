# Day 85 — Failure tatbikatı ve DevOps final audit

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Day 46–84 capability’lerini incident/release/recovery kanıtıyla kapatmak.

Önkoşullar: [Day 84](day-84-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/85-devops-final-audit`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Game Day; operational fitness; postmortem; evidence; capability status

## Uygulama görevleri

1. Day 46–84 Planlandı/Infrastructure Ready/Implemented/Integrated/Verified/Design Only matrisini service/platform bazında oluştur.
2. Release→stage→promotion→canary abort→recovery zincirinden bir tam E2E senaryo seç; ayrıca Vault/registry/network/broker failure tatbikatlarını bounded sırayla çalıştır.
3. Steady state, hipotez, blast radius, abort ve cleanup ölçütlerini önceden yaz; gerçek müşteri sistemi veya toplu veri silme yok.
4. Incident timeline/impact/root cause/mitigation/permanent fix ile teknik postmortem hazırla; monitoring ve runbook yetersizliğini düzelt.
5. Privilege/config/resource owner drift, secret leakage, image provenance, backup restore ve unsupported paid feature iddialarını denetle.
6. Exact build/render/security/CI/CD/ops verification komutları ve evidence linkleriyle DEVOPS-COMPLETION-REPORT oluştur; README/roadmap/Knowledge Base’i gerçek duruma göre hizala.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `docs/DEVOPS-COMPLETION-REPORT.md`
- `docs/testing/devops-final-verification.md`
- `docs/devops/capability-status.md`
- `docs/incidents/`
- `README.md`
- `ROADMAP.md`

- `docs/evidence/day-85/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(audit): DevOps capability ve evidence matrisini oluştur`
2. `test(gameday): release ve dependency failure recovery doğrula`
3. `fix(operations): tatbikatta bulunan somut eksikleri gider`
4. `docs(postmortem): timeline root cause ve prevention kaydet`
5. `docs(completion): doğrulanmış DevOps kapsamını ve Knowledge Basei kapat`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Release rollback/recovery sırasında canonical data/invariant ve idempotency korunur.
2. Permission/quality/signature gates negative senaryolarda gerçekten engeller.
3. Local prod-like ve production HA sınırı açık; kanıt olmadan hiçbir capability Verified değil.

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

Senior/expert/principal/staff unvanı otomatik kazanılmış iddia edilmez; hedef ileri teknik bilgi/beceri ve kanıtlı öğrenmedir.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
