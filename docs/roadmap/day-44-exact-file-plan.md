# Day 44 — Avro ve Schema Registry ile event evolution: kesin uygulama ve commit planı

Durum: **Planlandı**. Hedef branch: `day/44-schema-registry`.

## Önkoşullar ve ilerleme

Day 17–21 reliable Kafka ve Search consumer; mevcut event contract catalog.

Yeni Day branch’i bir önceki kapanmış Day branch’inden türetilir. `main` ve kapanmış eski Day branch’leri bu plan güncellemesiyle değiştirilmez. `docs/backend-roadmap-design` canonical plan ve cumulative dokümantasyonu tutar; uygulama kodu ilgili Day branch’inde geliştirilir.

## Kapsam ve karar sınırları

- Kafka event serialization Avro + Schema Registry; gRPC Protobuf korunur.
- Preferred registry Apicurio; exact version/integration uyumluluğu milestone başında doğrulanır.
- JSON→Avro aynı topic’te ani serializer swap değildir; versioned migration ve gerekirse geçici dual publishing.
- Dual publishing reliable outbound/outbox yolunu bypass etmez.
- Business event contract version ile Registry schema version ayrı kavramlardır.

## Dosya ve adapter planı

Aşağıdaki yollar uygulama başlangıcında mevcut package standardıyla kesinleştirilecek hedeflerdir. `...` placeholder’dır; literal package veya dosya adı değildir. Gerçek ihtiyaç yoksa boş sınıf/port oluşturulmaz.

- `infra/schema-registry/`
- `Kafka kullanan servis/src/main/resources/avro/`
- `SearchService/src/main/java/.../projection/`
- `docs/adr/day-44-event-schema-evolution.md`
- `docs/runbooks/json-avro-migration.md`

Service modüllerinde build/config/test ve gerektiği kadar ROADMAP/DESIGN/PACKAGE-DESIGN belgeleri güncellenir. Tam sürüm ve runtime seçimi uyumluluk doğrulamasından sonra yapılır; bu plan yeni bir ürünün gereksiz eklenmesini zorunlu tutmaz.

## Bağımlılık sırasına göre görevler

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

## Önerilen commit sırası

1. `docs(schema): evolution ve migration sözleşmesini tanımla`
2. `infra(registry): uyumlu Apicurio local temelini ekle`
3. `feat(schema): Avro sözleşmesini ve registry kurallarını ekle`
4. `feat(kafka): reliable outbound serializer entegrasyonunu ekle`
5. `feat(search): Avro migration consumerını ekle`
6. `test(schema): compatibility replay ve Registry outage doğrula`
7. `docs(schema): rollout rollback ve eski yol kapanışını kaydet`

Sıra bu milestone’ın implementation commit’leri içindir. Her commit tek tutarlı davranış veya karar içerir; küçük komşu görevler inceleme açıklığı korunuyorsa birleştirilebilir. Koşullu özellik uygulanmazsa ilgili feat/test commit’i atlanır ve karar docs commit’inde gerekçelendirilir.

## Doğrulama ve kabul ölçütleri

- Incompatible change seçilen policy ile engelleniyor.
- Migration kritik event kaybı/çift uygulama üretmiyor.
- Eski event replay ve Registry outage davranışı doğrulanmış.

- Önce ilgili GitHub CI/build/test kontrolleri başarılı olur; ardından local runtime ve protokole uygun API/E2E senaryoları doğrulanır.
- Görevlerde belirtilen happy path yanında failure, concurrency/idempotency ve authorization sınırları gerçek adapter testleriyle doğrulanır.
- Test ve runtime evidence ilgili Day’e bağlanır; atlanan kontrol veya test açıkça belirtilir.
- ADR ve service belgeleri actual implementation ile hizalanır; Knowledge Base impact review yapılır ve canonical branch’e dokümantasyon senkronize edilir.
- `Infrastructure Ready`, `Implemented`, `Integrated`, `Verified` ve `Design Only` ayrı tutulur. Kanıt yoksa Verified yazılmaz.

## Kapsam dışı ve kapanış

Day 44 kapsamını aşan yeni teknolojiler/iş akışları sırf çeşitlilik için eklenmez. Docker/Kubernetes/CI/CD genişletmesi bu backend plan güncellemesinin parçası değildir. [Gün özeti](day-44-schema-registry.md) ve [ana roadmap](../../ROADMAP.md) aynı kararları taşır. Day bir milestone’dır; tek takvim gününde bitme zorunluluğu yoktur.
