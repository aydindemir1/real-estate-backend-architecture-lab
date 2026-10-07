# Day 57 — StatefulSet, storage ve veri yaşam döngüsü

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Stateful altyapının identity, persistence ve recovery sınırlarını öğrenmek.

Önkoşullar: [Day 54](day-54-exact-file-plan.md), [Day 56](day-56-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/57-stateful-storage`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- StatefulSet; PVC/PV/StorageClass; access mode; reclaim policy; broker/DB lifecycle

## Uygulama görevleri

1. Canonical datastore/broker envanterinden bir temsilciyi StatefulSet/PVC ile çalıştır; her ürünü tek günde özel operator’a dönüştürme.
2. StorageClass/PVC access/reclaim ve local provisioner’ın node bağımlılığını açıkça yaz.
3. Stable identity/headless Service ve startup/termination sırasını ilgili ürün contract’ıyla eşleştir.
4. Pod restart ile volume persistence’ı, volume/node loss ile farklı failure boundary’yi göster.
5. MySQL migration, Mongo Outbox, Couchbase idempotency, Cassandra outbound ve Kafka/RabbitMQ durability kurallarına işletim matrisi hazırla.
6. Tüm veri ürünlerinin rollout/backup/operator ihtiyaçlarını planla; version upgrade ve restore Day 83–84’te doğrulanır.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `infra/kubernetes/platform/stateful/`
- `docs/devops/stateful-ownership-matrix.md`
- `docs/runbooks/pvc-stateful-recovery.md`

- `docs/evidence/day-57/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(storage): stateful ürün ve volume sahipliğini tanımla`
2. `feat(stateful): temsilci StatefulSet ve PVC ekle`
3. `test(storage): restart mount ve volume failure sınırını doğrula`
4. `docs(storage): tüm datastore broker operasyon matrisini kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Pod recreate sonrası temsilci durable data korunur.
2. PVC Pending/mount permission hatası runbook ile teşhis edilir.
3. PVC/volume kaybı restart ile çözülmüş gibi gösterilmez; restore gereksinimi açık.

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

Local PV tek başına replicated durable storage değildir; bütün DB’ler HA olarak işaretlenmez.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
