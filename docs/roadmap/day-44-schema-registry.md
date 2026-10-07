# Day 44 — Avro ve Schema Registry ile event evolution

Durum: **Planlandı**. Branch: `day/44-schema-registry`.

## Amaç ve önkoşullar

Day 17–21 reliable Kafka ve Search consumer; mevcut event contract catalog.

## Kapsam ve sahiplik sınırları

- Kafka event serialization Avro + Schema Registry; gRPC Protobuf korunur.
- Preferred registry Apicurio; exact version/integration uyumluluğu milestone başında doğrulanır.
- JSON→Avro aynı topic’te ani serializer swap değildir; versioned migration ve gerekirse geçici dual publishing.
- Dual publishing reliable outbound/outbox yolunu bypass etmez.
- Business event contract version ile Registry schema version ayrı kavramlardır.

## Görevler

1. PropertyPublished için gerçek additive evolution senaryosunu, consumer matrisini ve rollout/rollback sırasını tanımla.
2. Apicurio runtime/client/serializer/binder uyumluluğunu doğrula; local setup ve secrets/config sahipliğini belirle.
3. Avro schema, subject/artifact naming ve registration ownership kararını oluştur.
4. Backward/forward/full compatibility kurallarını registry’nin gerçek semantics’iyle test et; sonra policy’yi kilitle.
5. Producer serializer’ını ve schema metadata’yı reliable outbound akışına bağla.
6. SearchService’i migration consumer olarak hazırla; eski JSON ve yeni Avro geçiş sınırını açık tut.
7. Versioned topic/contract routing seç; dual publishing gerekiyorsa duplicate/stale guard ve kaldırma koşulunu belirle.
8. Compatible additive ve deliberately incompatible schema değişikliği testlerini yaz.
9. Old-schema replay, unknown schema, malformed payload ve Registry outage davranışını test et; kritik event kaybını engelle.
10. Migration/rollback runbook, schema lifecycle ADR ve Knowledge Base’i güncelle; eski yolun kapatılma kanıtını kaydet.

## Kapanış ölçütleri

- Incompatible change seçilen policy ile engelleniyor.
- Migration kritik event kaybı/çift uygulama üretmiyor.
- Eski event replay ve Registry outage davranışı doğrulanmış.

Ayrıntılı dosya/adapter planı, bağımlılık sırası, commit’ler ve doğrulama: [Day 44 kesin planı](day-44-exact-file-plan.md). Kapanış kanıtı oluşmadan bu capability tamamlandı veya Verified olarak işaretlenmez.
