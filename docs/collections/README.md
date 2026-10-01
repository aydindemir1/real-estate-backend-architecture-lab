# Postman Collections

Bu klasör, `docs/backend-roadmap-design` branch'inde gün bazlı Postman collection arşivini tutar.

## Kural

- Her Day için ayrı collection dosyası tutulur.
- Collection dosyaları ilgili gün klasörü altında saklanır.
- Bir Day'de Postman collection üretilmişse planning branch'inde historical artifact olarak korunur; sonraki Day collection'ı eski collection'ın üzerine yazmaz.
- Bu branch planning/documentation branch'idir; Day 6 sonrasındaki uygulama kodları buraya taşınmaz.
- Day branch'lerinde collection o güne ait çalışma düzenine uygun yerde bulunabilir; bu branch'te ise gün bazlı arşiv düzeni korunur.

## Mevcut collection'lar

- `day-03/Microservices-Project.postman_collection.json` — Day 3 mikroservis/API çalışma collection'ı.
- `day-08/Real-Estate-Day08-AgentService.postman_collection.json` — Day 8 AgentService runtime ve API doğrulama collection'ı.
- `day-09/Day-09-BuyerService.postman_collection.json` — Day 9 BuyerService Couchbase/Hexagonal runtime ve API kabul collection'ı.
- `day-10/Day-10-SellerService.postman_collection.json` — Day 10 SellerService Cassandra/Onion runtime ve API kabul collection'ı.

Yeni günler tamamlandıkça aynı yapı devam ettirilir:

```text
docs/collections/
├── day-03/
│   └── Microservices-Project.postman_collection.json
├── day-08/
│   └── Real-Estate-Day08-AgentService.postman_collection.json
├── day-09/
│   └── ...
└── day-10/
    └── ...
```
