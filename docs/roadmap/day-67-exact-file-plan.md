# Day 67 — Jenkins agent ve credential isolation

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Build yetkilerini ve container erişimini kontrollü tutmak.

Önkoşullar: [Day 58](day-58-exact-file-plan.md), [Day 66](day-66-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/67-jenkins-agent-isolation`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Ephemeral agents; credentials binding; least privilege; untrusted PR; Docker socket

## Uygulama görevleri

1. Trusted branch build ile untrusted PR build trust boundary’sini açıkça ayır; untrusted kodu publish credential’ıyla koşturma.
2. Agent image/runtime ve workspace lifecycle’ını tanımla; controller executor’larını kapatma/limit kararını uygula.
3. BuildKit/Docker erişim yöntemini seç; Docker socket mount’un host-level güçlü yetki verdiğini belgeleyip trusted dedicated lab host’ta sınırla.
4. SCM, registry ve artifact credential scope’unu ayrı ID/permission ile tasarla; henüz ürünler kurulmadıysa fixture ile binding/redaction test et.
5. Agent timeout/concurrency/cache/workspace cleanup kurallarını uygula; credential’ı global environment olarak bırakma.
6. Kubernetes agent kullanılırsa dar ServiceAccount/namespace ve workload policy tanımla; ikinci agent modeli zorunlu değildir.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/jenkins/agents/`
- `Jenkinsfile`
- `docs/security/ci-trust-boundaries.md`
- `docs/testing/jenkins-agent-isolation.md`

- `docs/evidence/day-67/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(ci-security): trust ve permission sınırını tanımla`
2. `config(jenkins): dar agent ve credential scope ekle`
3. `test(ci-security): untrusted build ve cleanup doğrula`
4. `docs(ci-security): Docker yetki ve izolasyon kararını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Untrusted build publish secret’a erişemez.
2. Log/archived workspace’te credential bulunmaz.
3. Agent crash/cancel sonrası secret/workspace cleanup ve pipeline failure açık.

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

Agent isolation host access riskini otomatik kaldırmaz; privileged build yalnız explicit trusted lab scope’ta.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
