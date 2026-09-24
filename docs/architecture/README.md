# Architecture Dokümantasyonu

Bu klasör sistem seviyesindeki architecture dokümantasyonunu içerir. Service'e özel implementation design, ilgili service'in kendi `docs/DESIGN.md` dosyasında tutulur.

## Dokümanlar

- `service-catalog.md` — service sorumlulukları ve data ownership
- `data-architecture.md` — Database per Service ve datastore rolleri
- `communication-architecture.md` — REST, OpenFeign, gRPC, RabbitMQ ve Kafka sorumlulukları
- `../adr/` — Architecture Decision Record dokümanları

## Dokümantasyon kuralı

- Root `ROADMAP.md` → projenin genel sırası
- `docs/roadmap/day-XX-*.md` → milestone kapsamı
- Service `ROADMAP.md` → service'in zaman içindeki gelişimi
- Service `docs/DESIGN.md` → teknik tasarım
