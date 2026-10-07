# Day 64 — Terraform ile somut yerel kaynak provisioning

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

IaC plan/state/provider yaşam döngüsünü ücretsiz yerel hedefte öğrenmek.

Önkoşullar: [Day 63](day-63-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/64-terraform-local-provisioning`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- HCL; providers/modules; plan/apply; state; dependency graph; lifecycle

## Uygulama görevleri

1. Terraform Community CLI ve provider sürümlerini pinle; primary IaC Terraform, OpenTofu/Pulumi karşılaştırma kapsamındadır.
2. Yerel Docker provider ile disposable lab network/volume ve teknik test container’ını somut hedef seç; production-like app/Compose/Argo kaynaklarını çift sahipli yapma.
3. Module input/output ve dependency graph kur; provider credential/secret’ını state/output/Git riskleriyle değerlendir.
4. plan→review→apply→refresh/plan akışını executable lab prosedürüne dönüştür.
5. State’i gitignore dışı güvenli local alanda sakla; snapshot/backuplar ve erişim izinlerini tanımla. Remote backend/local locking desteğini varsaymadan belgede doğrula.
6. Destroy scope’unu yalnız disposable kaynaklara daralt; canonical datastore volume’unu IaC demo cleanup’ına bağlama.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/terraform/modules/local-lab/`
- `infra/terraform/environments/lab/`
- `docs/adr/terraform-local-resource-ownership.md`
- `docs/runbooks/terraform-state.md`

- `docs/evidence/day-64/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(terraform): provider ve somut local resource sınırını tanımla`
2. `feat(terraform): disposable local provisioning modülünü ekle`
3. `test(terraform): plan apply no-op ve scoped destroy doğrula`
4. `docs(terraform): state security ve alternatif karşılaştırmasını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. İlk plan kaynakları oluşturur; ikinci plan no-op.
2. Out-of-band lab değişikliği sonraki plan’da görünür.
3. Destroy yalnız tanımlı disposable kaynakları etkiler; state/secret Git’e girmez.

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

Ücretli HCP Terraform/cloud provider kaynakları yok; Kubernetes uygulamalarının sahibi Argo CD olacak.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
