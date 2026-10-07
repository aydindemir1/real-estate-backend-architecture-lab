# Day 19 — Kesin Retry + DLT/DLQ + Replay Safety Planı

## Kapsam
- retryable/non-retryable classification
- Kafka bounded retry + DLT
- RabbitMQ DLX/DLQ
- poison-message handling
- replay metadata/runbook
- replay-safety testleri

## Task'ler

1. `RetryableMessagingException` ve `NonRetryableMessagingException` tanımla.
2. Transient network/timeout/unavailable hatalarını retryable; malformed payload, unsupported schema ve permanent validation hatalarını non-retryable olarak sınıflandır.
3. Kafka retry policy'yi bounded attempts/backoff ile configure et.
4. Yalnızca aktif oldukları yerde `property.events.retry`, `property.events.dlt`, `offer.events.retry`, `offer.events.dlt` ekle.
5. Original topic, message/event id, correlationId, type, attempt count ve failure metadata'yı koru.
6. Bad record'ın partition'ı süresiz bloklamaması için deserialization/poison-message handling configure et.
7. Listing command'ları için RabbitMQ DLX/DLQ configure et:
   - `real-estate.listing.dlx`
   - `property.listing.submit.dlq`
   - routing `property.listing.submit.dead`
8. Hot requeue/infinite redelivery'yi engelle.
9. Direct RabbitMQ publisher reliability bunları kullanıyorsa publisher confirms/returns aktif et.
10. `Duration` kullanarak external retry config ekle: max attempts, initial delay, multiplier, max delay ve destekleniyorsa jitter.
11. Kafka retry/DLT integration testleri ekle.
12. RabbitMQ DLQ integration testleri ekle.
13. Kafka ve RabbitMQ için poison-message testleri ekle.
14. Replay-safety testi ekle: fail → DLT/DLQ → düzelt → controlled replay → tam olarak bir business effect.
15. `docs/runbooks/messaging-replay.md` ekle.
16. Messaging topology ve command/event catalog'u güncelle.

## Commit sırası
1. `feat(messaging): classify retryable messaging failures`
2. `feat(kafka): add bounded retry and DLT policy`
3. `feat(rabbitmq): add DLQ topology and failure policy`
4. `feat(rabbitmq): add publisher confirm handling`
5. `config(messaging): add bounded retry backoff`
6. `test(kafka): add retry and DLT tests`
7. `test(rabbitmq): add DLQ integration tests`
8. `test(failure): add poison-message scenarios`
9. `test(failure): verify controlled replay safety`
10. `docs(messaging): add replay runbook`

## Final gate
- infinite retry/requeue yok
- retry classification explicit
- DLT/DLQ metadata korunuyor
- poison message'lar processing'i bloklamıyor
- replay kontrollü ve idempotent
- raw broker error'ları business/API layer'larına sızmıyor
