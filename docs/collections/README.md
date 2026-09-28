# Postman Collections

Bu klasör, `docs/backend-roadmap-design` branch'inde gün bazlı Postman collection arşivini tutar.

## Kural

- Her Day için ayrı collection dosyası tutulur.
- Collection dosyaları ilgili gün klasörü altında saklanır.
- Bu branch planning/documentation branch'idir; Day 6 sonrasındaki uygulama kodları buraya taşınmaz.
- Day branch'lerinde o güne ait collection proje kökünde veya çalışma düzenine uygun yerde bulunabilir; bu branch'te ise arşiv düzeni korunur.

## Mevcut collection'lar

- `day-08/Real-Estate-Day08-AgentService.postman_collection.json` — Day 8 AgentService runtime ve API doğrulama collection'ı.

Yeni günler tamamlandıkça aynı yapı devam ettirilir:

```text
docs/collections/
├── day-08/
│   └── Real-Estate-Day08-AgentService.postman_collection.json
├── day-09/
│   └── ...
└── day-10/
    └── ...
```
