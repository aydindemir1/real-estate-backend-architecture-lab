# Day 25 — Kesin Spring Cloud Bus Planı

## Kapsam
- RabbitMQ üzerinden Spring Cloud Bus
- refresh-safe property inventory
- targeted RefreshScope
- protected distributed refresh
- Bus outage testleri
- control-plane separation

## Task'ler
1. Gereken yerlere Spring Cloud Bus AMQP dependency ekle.
2. Bus transport olarak RabbitMQ kullan; Bus destination'ı business exchange'lerden ayrı tut.
3. Bus broker credential'ını Vault'tan yükle.
4. `docs/config/refreshable-properties.md` oluştur.
5. Runtime-refresh-safe ve restart-required property'leri sınıflandır.
6. `@RefreshScope` yalnızca gerçekten safe refresh destekleyen bean'lere ekle.
7. Controlled Config Server change → busrefresh → service refresh flow implemente et.
8. Refresh/bus actuator endpoint'lerini admin/ops authorization ile koru.
9. Deterministic Bus refresh integration testi ekle.
10. Non-refresh-safe critical infrastructure config'in sessizce mutate olmadığını kanıtlayan test ekle.
11. RabbitMQ/Bus outage test et: mevcut service behavior var olan config ile devam etsin.
12. Control-plane separation uygula: Bus event'leri domain/business event değildir.
13. `docs/runbooks/config-refresh.md` ekle.
14. Config Server outage ile Bus outage arasındaki farkları dokümante et.

## Commit sırası
1. `build(bus): add Spring Cloud Bus over RabbitMQ`
2. `config(bus): add distributed config refresh channel`
3. `docs(config): classify refresh-safe properties`
4. `feat(config): add targeted refresh scope`
5. `feat(bus): add controlled distributed refresh`
6. `feat(security): protect Bus refresh endpoints`
7. `test(bus): verify distributed refresh and broker outage`
8. `test(bus): enforce control-plane separation`
9. `docs(config): add config refresh runbook`

## Final gate
- Bus ayrı bir control plane'dir
- approved property'ler live refresh olur
- unsafe infrastructure setting'leri restart-bound kalır
- busrefresh endpoint korunur
- broker outage steady-state request'leri bozmaz
