# Day 9 — Couchbase CAS / Optimistic Concurrency Kararı

## Durum

**Bilinçli olarak ertelendi.**

## Bağlam

Spring Data Couchbase, persistence document üzerindeki bir alana `@Version` ekleyerek Couchbase CAS tabanlı optimistic locking desteği sağlayabilir.

Spring Data üzerinden yüklenen bir document mevcut CAS değerini taşır. Stale bir representation ile yapılan save işlemi `OptimisticLockingFailureException` üretebilir.

Day 9 Hexagonal persistence contract'ı şu şekildedir:

```text
LoadBuyerPreferencesPort.load(BuyerId) -> Optional<BuyerPreferences>
SaveBuyerPreferencesPort.save(BuyerPreferences) -> BuyerPreferences
```

Persistence document ile domain Aggregate bilinçli olarak birbirinden ayrılmıştır.

## Day 9'da CAS neden etkinleştirilmedi?

Yalnızca `BuyerPreferencesDocument` üzerine `@Version` eklemek mevcut port tasarımında doğru optimistic concurrency sağlamaz.

Load sırasında CAS değeri Couchbase document üzerinde bulunur. Ancak:

```text
BuyerPreferencesDocumentMapper.toDomain(...)
```

yalnızca domain Aggregate'i döndürür.

Böylece CAS değeri application katmanı Aggregate'i değiştirip tekrar save etmeden önce kaybolur.

Her save işleminde version değeri `0` veya boş olan yeni bir document üretmek, daha önce yüklenmiş CAS token'ını korumaz ve yanıltıcı bir concurrency koruması oluşturur.

Day 9:
- persistence-specific gizli token'ı domain'e sokmaz,
- mevcut port contract'larını sessizce değiştirmez,
- çalışmayan optimistic locking'i varmış gibi göstermez.

## Gelecekteki doğru seçenekler

Concurrency odaklı sonraki bir milestone aşağıdaki tasarımlardan birini açıkça seçebilir:

1. `BuyerPreferences` içine persistence bağımsız bir aggregate version / concurrency token eklemek ve bunu port'lardan taşımak.
2. Persistence port contract'ını Aggregate + concurrency metadata taşıyan bir wrapper döndürecek/kaydedecek şekilde değiştirmek.
3. Load-modify-save CAS döngüsünü adapter'ın yönettiği dedicated update port tasarlamak.

Seçilen yaklaşım mutlaka iki bağımsız stale representation'ın birbirini overwrite edemediğini kanıtlayan integration test içermelidir.

## Day 9 kararı

- `@Version` eklenmedi.
- Fake optimistic-locking test eklenmedi.
- Deterministic-key Couchbase persistence Day 9 baseline olarak korundu.

Bu, eksik implementation iddiası değil; açıkça verilmiş bir **defer kararıdır**.
