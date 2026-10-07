# Day 65 — IaC drift, state ve lab yeniden oluşturma

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Provisioning/config/manifest katmanlarının recovery ve ownership ilişkisini kanıtlamak.

Önkoşullar: [Day 64](day-64-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/65-iac-drift-rebuild`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Drift detection; import/state; state recovery; idempotent rebuild; ownership matrix

## Uygulama görevleri

1. Terraform/Ansible/Helm/Kustomize/Argo için resource ownership matrisi çıkar; aynı resource üzerinde reconcile savaşı yaratma.
2. Day 64 local kaynaklarında kontrollü out-of-band drift oluştur; plan farkını inceleyerek desired state’e dönüşü test et.
3. State import/move/refactor ihtiyacını disposable fixture’da dene; state kaybında kör apply ile duplicate kaynak oluşturma.
4. Güvenli state snapshot restore ve provider version değişimi için rollback prosedürünü test et.
5. Lab rebuild sırasını provisioning→host config→cluster bootstrap→platform→apps olarak yaz; tüm adımları tek gün zorla yeniden kurma.
6. Clean local rebuild tatbikatını kaynak bütçesine göre çalıştır; Day 84 platform recovery’ye sonuç/önkoşul aktar.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/terraform/`
- `infra/ansible/`
- `docs/devops/resource-ownership-matrix.md`
- `docs/runbooks/lab-rebuild.md`

- `docs/evidence/day-65/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(iac): katman ve resource owner matrisini tamamla`
2. `test(terraform): drift import ve state recovery doğrula`
3. `feat(lab): sıralı tekrar üretilebilir rebuild prosedürü ekle`
4. `docs(iac): recovery kanıtını ve sınırlarını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Drift tespit ve fix planı beklenen kaynaklarla sınırlı.
2. State recovery sonrası plan yanlış resource destroy etmiyor.
3. Rebuild komutlarında credential sızıntısı ve owner duplication yok.

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

Local lock/file state davranışı distributed team backend garantisi olarak sunulmaz; ücretli SaaS state gerekmez.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
