# Hexagonal Architecture

**Category:** Architecture  
**İlk eklendiği gün:** Day 9  
**Proje durumu:** Uygulandı / Doğrulandı  
**Kapsam:** BuyerService

## Nedir?

Hexagonal Architecture, application core'u dış dünya detaylarından ayıran ve bağımlılıkları port/adapters sınırları üzerinden yöneten bir architecture yaklaşımıdır.

Merkezde domain ve application logic bulunur. REST, database, messaging veya başka dış sistemler adapter olarak çevrede konumlanır.

```text
         Inbound Adapter
              |
              v
         Inbound Port
              |
              v
        Application Core
              |
              v
         Outbound Port
              ^
              |
       Outbound Adapter
```

## Temel kavramlar

### Inbound Port
Uygulamanın dışarıya sunduğu use-case contract'ıdır.

BuyerService örnekleri:
- `UpdateBuyerPreferencesUseCase`
- `GetBuyerPreferencesUseCase`
- `AddSavedSearchUseCase`

### Outbound Port
Application core'un dış bir capability'den ihtiyaç duyduğu contract'tır.

BuyerService örnekleri:
- `SaveBuyerPreferencesPort`
- `LoadBuyerPreferencesPort`

### Inbound Adapter
Dış request'i application use-case'ine çevirir.

BuyerService:
- REST controller
- REST request/response mapper

### Outbound Adapter
Application'ın ihtiyaç duyduğu dış capability'yi implement eder.

BuyerService:
- Couchbase persistence adapter
- Spring Data Couchbase repository
- persistence document model

## Dependency Rule

Day 9 BuyerService için:

- domain -> application yok
- domain -> adapter yok
- domain -> Spring/Couchbase/Jakarta yok
- application -> adapter yok
- inbound adapter -> outbound adapter yok
- outbound adapter -> outbound port implement eder

Bu kurallar `BuyerHexagonalArchitectureTest` ile ArchUnit üzerinden executable hale getirilmiştir.

## Domain / persistence ayrımı

`BuyerPreferences` domain Aggregate'tir.

`BuyerPreferencesDocument` Couchbase persistence modelidir.

İki model mapper ile çevrilir. Böylece persistence annotation ve datastore detail'leri domain'e sızmaz.

## Bu projede

Day 9 BuyerService:
- framework-independent domain
- inbound/outbound port ayrımı
- application service orchestration
- REST inbound adapter
- Couchbase outbound adapter
- explicit mapper boundaries
- ArchUnit dependency rules

ile Hexagonal Architecture'ı gerçek implementation olarak kullanır.

## Avantajlar

- framework ve datastore bağımlılığının core'dan ayrılması
- application/domain testability
- adapter değişimlerinin lokalize edilmesi
- dependency direction'ın açık olması

## Trade-off'lar

- daha fazla class ve mapping
- küçük CRUD servislerde ceremony riski
- port abstraction'larının gerçek boundary ihtiyacına dayanması gerekir

## İlgili dokümanlar

- `BuyerService/docs/DESIGN.md`
- `BuyerService/docs/PACKAGE-DESIGN.md`
- `docs/roadmap/day-09-buyer-couchbase-hexagonal.md`
