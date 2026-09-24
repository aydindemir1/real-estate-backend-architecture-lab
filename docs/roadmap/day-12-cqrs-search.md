# Day 12 — CQRS + Elasticsearch Search Projection

**Status:** Planned  
**Implementation branch:** `day/12-cqrs-search`

## Amaç

Day 1–6 baseline capability'lerini yeniden uygulamadan bu milestone'un hedeflerini tamamlamak.

## Planlanan kapsam

- PropertyService/MongoDB source of truth
- Kafka property event'leri
- SearchService/Elasticsearch projection
- Full-text, filter, facet ve geo query
- Eventual Consistency

## Dokümantasyon kuralı

Service'e özel package structure, data model ve dependency değişiklikleri ilgili service'in `docs/DESIGN.md` dosyasında tutulur. Bu dosya yalnızca milestone kapsamını ve cross-service çalışmaları tanımlar.

## Implementation öncesi zorunlu tasarım

- Etkilenen service DESIGN dokümanlarını gözden geçir.
- Bu milestone için gereken API/event contract'larını kesinleştir.
- Infrastructure/configuration değişikliklerini kesinleştir.
- Positive ve Failure-Path Testing senaryolarını tanımla.
- Definition of Done maddelerini doğrula.

## Definition of Done

- Implementation ilgili Day branch'inde çalışıyor olmalı.
- Milestone için tanımlanan automated test'ler geçmeli.
- Mevcut baseline regression test'leri bozulmamalı.
- İlgili DESIGN/ROADMAP/ADR dosyaları aynı branch'te güncellenmeli.
- Bu koşullar tamamlandıktan sonra branch `main` ile merge edilmeye uygun kabul edilir.
