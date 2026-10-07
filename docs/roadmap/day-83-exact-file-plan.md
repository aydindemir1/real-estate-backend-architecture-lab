# Day 83 — Backup/restore ve veri recovery tatbikatı

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Cluster resource backup ile application-consistent DB backup farkını kanıtlamak.

Önkoşullar: [Day 33](day-33-exact-file-plan.md), [Day 57](day-57-exact-file-plan.md), [Day 81](day-81-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/83-kubernetes-backup-restore`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Velero; object store; logical/native DB backup; PVC limits; RPO/RTO

## Uygulama görevleri

1. Day 33 canonical/derived/disposable envanterini Kubernetes resource/volume/secret lifecycle ile genişlet.
2. Velero ve ücretsiz local S3-compatible backup hedefi seçimini uyum/lisans/support kontrolüyle ADR’de kesinleştir; cloud hesabı açma.
3. CRD/namespace/RBAC/manifest backup scope ile datastore native/tutarlı backup yöntemini ayır; local PVC’yi CSI snapshot destekliymiş gibi varsayma.
4. Temsilci Mongo Property+Outbox ve bir diğer datastore/broker için uygun quiesce/native backup ve restore önkoşullarını kilitle.
5. Backup metadata/checksum/access/encryption/retention ve secret/key recovery sahipliğini yaz; anahtarları aynı açık backup içine koyma.
6. İzole namespace/target restore yap; count/version/domain invariant/reliable outbound/replay/projection rebuild doğrula, ölçülen RPO/RTO kaydet.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/kubernetes/platform/backup/`
- `scripts/devops/backup-restore/`
- `docs/runbooks/kubernetes-data-recovery.md`
- `docs/testing/restore-evidence-matrix.md`

- `docs/evidence/day-83/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(backup): datastore resource ve key sahipliğini tanımla`
2. `infra(backup): Velero ve local backup hedefini kur`
3. `feat(backup): temsilci tutarlı datastore prosedürlerini bağla`
4. `test(restore): isolated resource ve DB restore doğrula`
5. `docs(recovery): ölçülen RPO RTO ve doğrulanmayan kapsamı kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Velero resource restore ve temsilci gerçek DB restore ayrı kanıtlı.
2. Data+outbound/inbox/idempotency ilişkisi duplicate/lost side-effect üretmiyor.
3. Backup store/credential/volume failure açık; test restore aktifteki developer verisini silmiyor.

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

Velero tek başına DB consistency veya local PV replication sağlamaz; her datastore verified işaretlenmez.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
