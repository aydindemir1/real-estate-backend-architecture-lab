# Day 78 — Migration uyumluluğu ve rollback/forward-fix

**Durum: Planlandı.** Bu belge yapılacak çalışmayı tanımlar; uygulama veya runtime doğrulaması yapılmış sayılmaz.

## Amaç ve önkoşullar

Uygulama release’i ile veri değişikliğinin recovery sınırını ayırmak.

Önkoşullar: [Day 77](day-77-exact-file-plan.md). Gün sırası korunur; bu önkoşullar özellikle gerekli yetenekleri gösterir.

- Day bir milestone'dır; tek takvim günü zorunluluğu yoktur.
- Uygulama branch'i önceki tamamlanmış günün birikimli branch'inden türetilir; önerilen ad: `day/78-migration-compatible-release`.
- Day 46 başlangıç kaynağı Day 45'in tamamlanmış implementation branch'idir. Dokümantasyon branch'indeki Day 1–6 kodu deployment kaynağı değildir.
- Ortak kararlar: [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md). Mevcut backend sahiplik ve güvenlik kararları geçerlidir.

## Öğrenilecek konular

- Expand/contract; old/new overlap; migration Jobs; reliable event schema; forward-fix

## Uygulama görevleri

1. Mevcut schema/migration ve Day 44 event schema kararını mixed-version rollout matrisiyle eşleştir.
2. Relational bir temsilci schema üzerinde additive expand→compatible app→backfill→contract sırasını kur; veri kaybı içeren değişikliği otomatik rollback sanma.
3. Migration owner/execution lock seç; startup migration ile bağımsız Job aynı migration’ı yarışarak yönetmez.
4. Argo sync/release sequencing ve Job failure’da promotion blocking davranışını uygula.
5. Eski/yeni app version birlikteyken API/event/datastore uyumluluğunu test et; Avro Registry policy korunur.
6. Image rollback, config rollback, DB forward-fix ve restore seçeneklerini ayrı runbook’larla kaydet.

## Hedef dosya ve belgeler

Aşağıdaki yollar planlanan hedeflerdir; bugün repoda bulunduğu varsayılmaz. Implementation branch'inde mevcut yapı incelenir; eşdeğer dosya varsa ikinci bir sahip oluşturmak yerine mevcut dosya güncellenir.

- `Temsilci servis/db migration`
- `infra/kubernetes/jobs/migration/`
- `docs/adr/migration-release-ownership.md`
- `docs/runbooks/schema-forward-fix.md`

- `docs/evidence/day-78/`: komutlar, secretsız çıktılar, sürümler, ölçümler ve hata/toparlanma kanıtları.
- Günün ADR ve runbook bağlantıları kapanış raporuna eklenir; Knowledge Base etki incelemesi actual implementation'a göre yapılır.

## Commit sırası

Önce karar ve önkoşul, sonra uygulama, ardından hata doğrulaması ve belgeler gelir. Commit'ler aşağıdaki sırayla küçük ve review edilebilir tutulur; görevleri yapmadan boş commit oluşturulmaz.

1. `docs(migration): mixed-version ve execution ownerını tanımla`
2. `feat(migration): temsilci expand contract akışını ekle`
3. `config(release): migration Job gateini bağla`
4. `test(migration): old new compatibility ve failure doğrula`
5. `docs(migration): rollback restore ve forward-fix sınırlarını kaydet`

## Doğrulama ve hata senaryoları

Her senaryoda hipotez, başlangıç durumu, kullanılan komut, beklenen/gözlenen sonuç ve toparlanma kaydedilir. Ölçüm yoksa başarılı kabul edilmez.

1. Expand sırasında eski ve yeni version aynı veri üzerinde doğru çalışır.
2. Migration failure release’i durdurur; duplicate migration güvenli.
3. Destructive contract sonrası eski image’a kör rollback engellenir veya açık unsupported işaretlenir.

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

Canonical veri geri dönüşü image rollback ile eş tutulmaz; her datastore için aynı migration mekaniği varsayılmaz.

Ücretli cloud/SaaS zorunluluğu yoktur. Yerel lab sonucu production SLA, fiziksel HA veya mesleki seviye sertifikası olarak sunulmaz.
