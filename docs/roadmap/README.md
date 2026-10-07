# Backend mimari planı — ayrıntılı gün dizini

Bu dizin [ana ROADMAP.md](../../ROADMAP.md) dosyasındaki 45 milestone sırasına erişim sağlar. Day 1–6 stabil `main` baseline’dır; mevcut durum ve kanıtı ilgili implementation branch’inden okunur. Bu belge future planları tamamlanmış çalışma gibi göstermez.

## Canonical belgeler

1. [Ana roadmap](../../ROADMAP.md): konu sırası ve kapsam kararları.
2. [Master Engineering Plan](../MASTER-ENGINEERING-PLAN.md): ortak engineering ilkeleri ve sahiplik.
3. İlgili günün kesin planı: görev, dosya/adapter hedefleri, commit sırası, doğrulama ve kapanış.
4. Gün özeti: kesin plana erişim; çelişki varsa yukarıdaki belgeler esas alınır.

[Eski gün eşlemesi](LEGACY-DAY-MAPPING.md) Day 15–26’daki tarihsel summary dosyalarını açıklar. [Master audit](MASTER-AUDIT.md) baseline tasarım denetimini ve güncel ek kapsamı ayırır.

## Gün gün planlar

| Gün | Ana konu | Kesin plan | Güncel özet |
|---|---|---|---|
| Day 7 | Build ve yerel veri altyapısı | [Dosya, görev ve commit planı](day-07-exact-file-plan.md) | [Özet](day-07-build-data-infra.md) |
| Day 8 | AgentService / MySQL / Clean | [Dosya, görev ve commit planı](day-08-exact-file-plan.md) | [Özet](day-08-agent-mysql-clean.md) |
| Day 9 | BuyerService / Couchbase / Hexagonal | [Dosya, görev ve commit planı](day-09-exact-file-plan.md) | [Özet](day-09-buyer-couchbase-hexagonal.md) |
| Day 10 | SellerService / Cassandra / Onion | [Dosya, görev ve commit planı](day-10-exact-file-plan.md) | [Özet](day-10-seller-cassandra-onion.md) |
| Day 11 | PropertyService / MongoDB / Vertical Slice | [Dosya, görev ve commit planı](day-11-exact-file-plan.md) | [Özet](day-11-property-mongodb-vertical-slice.md) |
| Day 12 | SearchService / Elasticsearch / CQRS | [Dosya, görev ve commit planı](day-12-exact-file-plan.md) | [Özet](day-12-search-elasticsearch-cqrs.md) |
| Day 13 | Redis ve sınırlı read cache | [Dosya, görev ve commit planı](day-13-exact-file-plan.md) | [Özet](day-13-redis-foundation.md) |
| Day 14 | Security / Keycloak | [Dosya, görev ve commit planı](day-14-exact-file-plan.md) | [Özet](day-14-security-keycloak.md) |
| Day 15 | gRPC internal communication | [Dosya, görev ve commit planı](day-15-exact-file-plan.md) | Kesin plan esas alınır; eski summary eşlemesine bakılır. |
| Day 16 | GraphQL read API | [Dosya, görev ve commit planı](day-16-exact-file-plan.md) | Kesin plan esas alınır; eski summary eşlemesine bakılır. |
| Day 17 | Kafka / Stream / Function | [Dosya, görev ve commit planı](day-17-exact-file-plan.md) | Kesin plan esas alınır; eski summary eşlemesine bakılır. |
| Day 18 | Outbox / Inbox | [Dosya, görev ve commit planı](day-18-exact-file-plan.md) | Kesin plan esas alınır; eski summary eşlemesine bakılır. |
| Day 19 | Retry / DLT / DLQ | [Dosya, görev ve commit planı](day-19-exact-file-plan.md) | Kesin plan esas alınır; eski summary eşlemesine bakılır. |
| Day 20 | Cassandra reliable outbound | [Dosya, görev ve commit planı](day-20-exact-file-plan.md) | Kesin plan esas alınır; eski summary eşlemesine bakılır. |
| Day 21 | CQRS event projection | [Dosya, görev ve commit planı](day-21-exact-file-plan.md) | Kesin plan esas alınır; eski summary eşlemesine bakılır. |
| Day 22 | Offer / Reservation Saga | [Dosya, görev ve commit planı](day-22-exact-file-plan.md) | Kesin plan esas alınır; eski summary eşlemesine bakılır. |
| Day 23 | Resilience / load balancing | [Dosya, görev ve commit planı](day-23-exact-file-plan.md) | Kesin plan esas alınır; eski summary eşlemesine bakılır. |
| Day 24 | Spring Cloud Vault | [Dosya, görev ve commit planı](day-24-exact-file-plan.md) | Kesin plan esas alınır; eski summary eşlemesine bakılır. |
| Day 25 | Spring Cloud Bus | [Dosya, görev ve commit planı](day-25-exact-file-plan.md) | Kesin plan esas alınır; eski summary eşlemesine bakılır. |
| Day 26 | Test hardening | [Dosya, görev ve commit planı](day-26-exact-file-plan.md) | Kesin plan esas alınır; eski summary eşlemesine bakılır. |
| Day 27 | Contract testing | [Dosya, görev ve commit planı](day-27-exact-file-plan.md) | Kesin plan esas alınır; eski summary eşlemesine bakılır. |
| Day 28 | Failure testleri ve sınırlı chaos deneyi | [Dosya, görev ve commit planı](day-28-exact-file-plan.md) | Kesin plan esas alınır; eski summary eşlemesine bakılır. |
| Day 29 | OpenTelemetry | [Dosya, görev ve commit planı](day-29-exact-file-plan.md) | Kesin plan esas alınır; eski summary eşlemesine bakılır. |
| Day 30 | Observability stack | [Dosya, görev ve commit planı](day-30-exact-file-plan.md) | Kesin plan esas alınır; eski summary eşlemesine bakılır. |
| Day 31 | Cloud Task / reindex | [Dosya, görev ve commit planı](day-31-exact-file-plan.md) | Kesin plan esas alınır; eski summary eşlemesine bakılır. |
| Day 32 | Architecture / system design audit | [Dosya, görev ve commit planı](day-32-exact-file-plan.md) | Kesin plan esas alınır; eski summary eşlemesine bakılır. |
| Day 33 | E2E / recovery / baseline kapanışı | [Dosya, görev ve commit planı](day-33-exact-file-plan.md) | Kesin plan esas alınır; eski summary eşlemesine bakılır. |
| Day 34 | Anti-Corruption Layer ve koşullu Strangler Fig | [Dosya, görev ve commit planı](day-34-exact-file-plan.md) | [Özet](day-34-acl-strangler-fig.md) |
| Day 35 | Offer write-side için Event Sourcing temeli | [Dosya, görev ve commit planı](day-35-exact-file-plan.md) | [Özet](day-35-event-sourcing-foundation.md) |
| Day 36 | Event Sourcing replay, audit ve koşullu snapshot | [Dosya, görev ve commit planı](day-36-exact-file-plan.md) | [Özet](day-36-event-sourcing-replay.md) |
| Day 37 | Property Purchase Completion için Saga Orchestration | [Dosya, görev ve commit planı](day-37-exact-file-plan.md) | [Özet](day-37-saga-orchestration.md) |
| Day 38 | PropertyWatchService için Reactive Architecture | [Dosya, görev ve commit planı](day-38-exact-file-plan.md) | [Özet](day-38-reactive-webflux.md) |
| Day 39 | SSE ile gerçek zamanlı Property bildirimleri | [Dosya, görev ve commit planı](day-39-exact-file-plan.md) | [Özet](day-39-realtime-notification.md) |
| Day 40 | Web ve Mobile için Backend-for-Frontend | [Dosya, görev ve commit planı](day-40-exact-file-plan.md) | [Özet](day-40-bff.md) |
| Day 41 | GraphQL Federation ve read composition | [Dosya, görev ve commit planı](day-41-exact-file-plan.md) | [Özet](day-41-graphql-federation.md) |
| Day 42 | Spring Batch ile raporlama ve stale listing tespiti | [Dosya, görev ve commit planı](day-42-exact-file-plan.md) | [Özet](day-42-spring-batch.md) |
| Day 43 | Agency tenant isolation ve Multi-Tenancy | [Dosya, görev ve commit planı](day-43-exact-file-plan.md) | [Özet](day-43-multi-tenancy.md) |
| Day 44 | Avro ve Schema Registry ile event evolution | [Dosya, görev ve commit planı](day-44-exact-file-plan.md) | [Özet](day-44-schema-registry.md) |
| Day 45 | Genişletilmiş mimari uygunluk ve kapanış denetimi | [Dosya, görev ve commit planı](day-45-exact-file-plan.md) | [Özet](day-45-extended-architecture-audit.md) |

## Mevcut günlere eklenen system design kapsamı

| Gün | Ek öğrenme/uygulama |
|---|---|
| Day 12 | Veri seçimi, arama API tasarımı ve latency/throughput |
| Day 13 | Redis cache stratejileri ve bir read use-case |
| Day 14 | Secure API Design ve şifreleme sorumlulukları |
| Day 21 | CAP ve consistency modellerinin proje üzerinden öğrenilmesi |
| Day 23 | Load balancing algoritmaları ve abuse prevention |
| Day 28 | Sınırlı chaos engineering deneyi |
| Day 32 | System design süreci ve mimari trade-off değerlendirmesi |
| Day 33 | Canonical datastore backup/restore doğrulaması |

## Çalışma ve kapanış ilkeleri

- Day bir milestone’dır; tek takvim gününde bitme zorunluluğu yoktur.
- Yeni branch önceki kapanmış Day branch’inden türetilir; küçük, anlamlı commit’lerle ilerlenir.
- Önce ilgili GitHub CI başarılı olur; sonra local runtime/protokole uygun API doğrulaması yapılır.
- Her kapanışta service belgeleri, ADR, evidence ve Knowledge Base impact review actual implementation’a göre güncellenir; gerekli dokümantasyon canonical branch’e senkronize edilir.
- `Planlandı`, `Infrastructure Ready`, `Implemented`, `Integrated`, `Verified`, `Design Only` birbirine karıştırılmaz. Bu plan güncellemesi yeni capability’leri Verified yapmaz.
- Day 33 temel backend kapanışı; Day 45 genişletilmiş mimari kapanışıdır.
- Snapshot ve Strangler Fig koşullu; Sharding/Partitioning Design Only kalır.
- Mevcut ihtiyacı karşılayan alternatif araç tekrar eklenmez; gerçek requirement ve gerekçe esas alınır.
- Docker/Kubernetes/CI/CD ayrıntılı programı daha sonra ele alınır; bu güncelleme DevOps kapsamını genişletmez.
