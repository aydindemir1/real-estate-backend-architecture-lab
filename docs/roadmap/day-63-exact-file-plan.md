# Day 63 — Ansible ile yerel host konfigürasyonu

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Yerel lab/CI host setup’ını idempotent ve gözden geçirilebilir hale getirmek.

Önkoşullar: [Day 46](day-46-exact-file-plan.md), [Day 62](day-62-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/63-ansible-host-configuration`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Inventory; roles/playbooks; handlers; become; check/diff; idempotency

## Uygulama görevleri

1. Yerel Linux/WSL2 veya disposable VM inventory oluştur; gerçek laptop global config’ini kontrolsüz mutate etme.
2. JDK/Gradle Wrapper önkoşulları, Docker/Minikube CLI ve gerekli dizin/permission kurulumunu rol sınırlarına ayır.
3. Explicit sürüm ve checksum kaynağını seç; curl|sh adımlarını kontrolsüz varsayılan yapma.
4. Ansible variable/secret ayırımını mevcut Vault policy’siyle hizala; broad become kullanma.
5. Check/diff ve ikinci run idempotency için geçerli değişiklik ölçütünü belirle.
6. Host setup ile Terraform provisioning/Kustomize app deployment sahipliğini ayır; Chef/Puppet karşılaştırmasını hazırla.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/ansible/inventory/`
- `infra/ansible/roles/`
- `infra/ansible/playbooks/`
- `docs/adr/ansible-configuration-management.md`

- `docs/evidence/day-63/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(ansible): host ownership ve inventory sınırını tanımla`
2. `feat(ansible): idempotent lab hazırlama rollerini ekle`
3. `test(ansible): check diff ve tekrar çalıştırma doğrula`
4. `docs(ansible): Chef Puppet karşılaştırmasını ve rollbacki kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Disposable host ilk run sonrası gerekli araçlarla hazır.
2. İkinci run gerekçesiz değişiklik yapmaz.
3. Eksik permission/offline download/yanlış checksum fail görünür; cleanup scoped.

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

Ansible Kubernetes app resources’ı Argo CD ile birlikte yönetmez; paid host/service yok.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
