# Couchbase

**Kategori:** Technology  
**İlk eklendiği gün:** Day 7  
**Proje durumu:** Uygulandı / Doğrulandı  
**Kapsam:** BuyerService  

## 1. Nedir?

Couchbase, distributed document/key-value database platformudur.

JSON document storage ile key-value access ve SQL++ query capability'lerini birlikte sunar.

## 2. Veri modeli

```text
Cluster
  └── Bucket
      └── Scope
          └── Collection
              └── Document
```

## 3. Bu projede nasıl kullanılıyor?

Day 7'de BuyerService için Couchbase infrastructure hazırlanmıştır.

Day 9'da application-level persistence tamamlanmıştır.

Gerçek logical hierarchy:

```text
Cluster
└── bucket: buyer
    └── scope: buyer_service
        └── collection: preferences
```

Couchbase sürümü:
- Community 8.0.2

Storage backend:
- `couchstore`

## 4. Document key

BuyerPreferences için deterministic key:

```text
buyer-preferences::{buyerId}
```

Primary access path direct document-key lookup'tır.

Bu nedenle Day 9 use-case'leri için secondary index eklenmemiştir.

## 5. Spring Data Couchbase

BuyerService:
- `BuyerPreferencesDocument`
- `SpringDataBuyerPreferencesRepository`
- `BuyerPreferencesDocumentMapper`
- `CouchbaseBuyerPreferencesAdapter`

kullanır.

Domain Aggregate Couchbase document değildir.

## 6. Bootstrap

Script:
- `infra/couchbase/bootstrap-buyer.ps1`

Script:
- cluster init
- bucket oluşturma
- scope oluşturma
- collection oluşturma
- application user oluşturma

işlemlerini idempotent şekilde yürütür.

Application role:
- `bucket_full_access[buyer]`

Credentials:
- `BUYER_DB_USERNAME`
- `BUYER_DB_PASSWORD`

Secret değerler repository'ye yazılmaz.

## 7. Testcontainers

Persistence integration testleri shared lokal Couchbase yerine Testcontainers foundation kullanır.

Kapsam:
- save/load
- deterministic key
- same-document update
- nested value round-trip
- saved search round-trip
- missing document

## 8. Runtime doğrulaması

Day 9'da:
- bucket açıldı
- preferences collection başlangıçta 0 item idi
- başarılı PUT sonrası item sayısı 1 oldu
- GET ile veri geri okundu
- saved search persistence üzerinden geri döndü

Evidence:
- `docs/evidence/day-09/`

## 9. CAS / optimistic concurrency

Day 9'da bilinçli olarak ertelenmiştir.

Neden:
- mevcut Hexagonal port contract CAS token'ını application/domain boyunca taşımıyor

Detay:
- `docs/day-09/cas-concurrency-decision.md`

## 10. Production considerations

- bucket sizing
- replicas
- durability level
- backup
- rebalance
- failover
- index design
- query consistency
- CAS/concurrency
- monitoring

## 11. İleri öğrenme konuları

- vBuckets
- DCP
- durability
- rebalance
- SQL++ optimizer
- scopes/collections
- XDCR
