# Day 33 — Kesin E2E + Recovery + Backend Completion Planı

## Kapsam
- critical business E2E
- security/service identity E2E
- outage/recovery exercise'ları
- controlled replay
- final Gradle/Compose verification
- backend completion report
- README/ROADMAP closeout

## Task'ler
1. Yalnızca gerekli infrastructure'ı içeren reproducible backend E2E Compose profile ekle.
2. Registration/Profile E2E: identity/account → UserProfile → role-specific profile mapping.
3. Seller Listing E2E: listing submission → durable pending command → RabbitMQ → Property DRAFT; duplicate safe.
4. Property→Search E2E: publish → Outbox → Kafka → Elasticsearch → REST search; opsiyonel GraphQL parity.
5. Offer Acceptance E2E end-to-end.
6. Offer Rejection E2E end-to-end.
7. Concurrent Offer E2E: tam olarak bir hold, double reservation yok.
8. Buyer/Seller/Agent için Security ownership E2E ve izin verilen yerde explicit Admin override.
9. Wrong scope → 403 olacak şekilde Service Client Credentials E2E.
10. Representative dependency-failure E2E: Agent unavailable, bounded timeout/circuit ve truthful error.
11. Kafka outage recovery: Outbox pending → broker restored → Search catches up.
12. RabbitMQ outage recovery: pending Seller outbound survives → broker restored → bir Property.
13. Day 31 full rebuild/alias switch kullanarak Elasticsearch loss recovery.
14. Vault outage runbook/tabletop check'i gerçek behavior'a karşı doğrula.
15. Bir Kafka DLT ve bir RabbitMQ DLQ controlled replay exercise yap.
16. Zaten yoksa `docs/runbooks/README.md` completeness index oluştur.
17. Test, architecture, integration, contract, failure ve E2E için exact Gradle command'larını içeren `docs/testing/final-verification.md` oluştur.
18. Final `clean test`, architecture, integration, contract, failure, E2E ve `check` çalıştır; hidden skipped failure olmasın.
19. `docker compose config` ve gerekli profile health validation çalıştır.
20. `docs/BACKEND-COMPLETION-REPORT.md` oluştur.
21. Root README'yi yalnızca completed capability'leri içerecek şekilde güncelle; Kubernetes/CI/CD future phase'i açıkça ayır.
22. Day 7–33 temel backend baseline’ını yalnız doğrulanmış kanıtlarla kapat; Day 34–45 extended mimari kapsamını future/planlı olarak ayrı tut.
23. Opsiyonel learning outcomes dokümanı.
24. Opsiyonel milestone tag yalnızca main merge + green verification sonrasında.

## Completion report bölümleri
- service/architecture matrix
- persistence technologies
- protocols
- messaging/reliability
- security
- resilience
- testing
- observability
- recovery/operations
- intentionally deferred work
- known limitations

## Final gate
- bütün critical E2E'ler green
- recovery exercise'ları green
- replay doğrulanmış
- final Gradle verification green
- Compose valid/healthy
- completion report mevcut
- README unimplemented claim içermiyor
- sonraki Docker/Kubernetes/Spring Cloud Kubernetes/Jenkins/SonarQube/Nexus/Harbor/Argo CD phase açıkça ayrılmış

## Commit sırası
1. docs(recovery): canonical backup envanterini ve restore planını tanımla
2. docs(e2e): temel backend doğrulama sözleşmesini tanımla
3. test(e2e): kritik iş akışlarını ve service identityyi doğrula
4. test(recovery): broker kesintisi sonrası toparlanmayı doğrula
5. test(recovery): Search rebuild ve kontrollü replay doğrula
6. test(recovery): izole Mongo backup ve restore tatbikatını ekle
7. test(recovery): restore sonrası state Outbox ve replay güvenliğini doğrula
8. docs(recovery): ölçülen RPO RTO ve kapsam sınırlarını kaydet
9. docs(completion): temel backend raporunu ve gerçek durumunu kaydet

Küçük ve aynı sorumluluğa ait komşu commit’ler birleştirilebilir; karar, uygulama ve doğrulama ayrı incelenebilir kalır.

## Onaylanan system design ek kapsamı — Canonical datastore backup/restore doğrulaması

Durum: **Planlandı**. Bu bölüm günün mevcut temel görevlerine eklenir; tamamlanmış implementation iddiası değildir. Ek görevler foundation kurulduktan sonra ve günün dokümantasyon/kapanış adımından önce uygulanır. Yukarıdaki commit sırası bu kapsamı içerir.

### Ek görevler ve çıktı belgeleri

1. Canonical ve derived veriyi ayıran backup/restore envanteri oluştur: Auth/UserProfile PostgreSQL, Agent MySQL, Buyer Couchbase, Seller Cassandra ve Property MongoDB; Elasticsearch rebuild, Redis yeniden üretilebilir state rolündedir.
2. Mevcut canonical PropertyService/MongoDB üzerinde sınırlı gerçek backup/restore tatbikatı seç; tutarlı snapshot/backup önkoşullarını kullanılan runtime/topology için doğrula. Tek datastore kanıtı bütün datastore’ların restore kanıtı değildir.
3. Day 18 Outbox ile Property state birlikte ele alınır: backup sırasında yazmaları durdurma/quiesce sınırı veya desteklenen tutarlı yöntem açıkça seçilir; broker/inbox/idempotency/replay sınırları belgelenir.
4. Backup checksum/metadata ve restore hedefini kaydet; aktif developer DB’yi silmek yerine izole hedefe restore et. Kaynak/veri miktarı ve Mongo version/config uyumluluğunu doğrula.
5. Restore sonrası record count/kimlik/version/domain state ve pending Outbox ilişkisini test et; isolated broker/consumer testinde kritik event kaybı ve duplicate uygulama olmadığını doğrula.
6. RPO/RTO hedeflerini varsayım olarak, ölçülen veri kaybı/zamanı ayrı raporla; düzenli scheduler, multi-node failover ve platform backup otomasyonunu bu güne ekleme.
7. docs/runbooks/canonical-backup-restore.md ve baseline completion raporuna kapsam/kanıt/sınırlamaları ekle; Day 33 temel backend kapanışı, Day 45 extended kapanışıdır.

### Ek kabul ölçütleri

- Bir canonical datastore gerçek izole restore ile doğrulanmış.
- Property/Outbox ilişkisi ve replay correctness korunmuş.
- Diğer store’ların doğrulama durumu ayrı; Day 45 işi Day 33 completed sayılmamış.

Kapanışta ilgili service ROADMAP/DESIGN belgeleri ve Knowledge Base gerçek implementation/kanıtlarla güncellenir. Önce ilgili GitHub CI başarılı olur; ardından local runtime/API doğrulaması yapılır. Bir Day bir milestone’dır; kapsam gerektiğinde birden fazla takvim gününde tamamlanabilir.
