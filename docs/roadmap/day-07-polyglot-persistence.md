# Day 7 — Polyglot Persistence & Architecture Foundations

**Status:** Planned  
**Implementation branch:** `day/07-polyglot-persistence`

## Amaç

Day 1–6 baseline capability'lerini yeniden uygulamadan bu milestone'un hedeflerini tamamlamak.

## Planlanan kapsam

- AgentService → MySQL + Clean Architecture
- BuyerService → Couchbase + Hexagonal Architecture
- SellerService → Cassandra + Onion Architecture
- PropertyService → MongoDB + Vertical Slice Architecture
- SearchService foundation + Elasticsearch connectivity
- Redis infrastructure foundation

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
