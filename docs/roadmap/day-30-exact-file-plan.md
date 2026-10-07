# Day 30 — Kesin Prometheus + Grafana + Loki + Tempo Stack Planı

## Kapsam
- Prometheus
- Grafana
- Loki
- Tempo
- supported log shipper
- provisioned datasource/dashboard'lar
- sample actionable alert'ler
- opsiyonel observability Compose profile

## Task'ler
1. Explicit image version ve local config ile Tempo ekle.
2. Gateway ve tüm service'ler için explicit scrape config ile Prometheus ekle.
3. Explicit local config ile Loki ekle.
4. Implementation sırasında güncel olarak desteklenen log shipper seç; uygunsa Grafana Alloy tercih et.
5. Provisioned Prometheus, Tempo ve Loki datasource'larıyla Grafana ekle.
6. Unit/integration testlerin stack'e ihtiyaç duymaması için Compose `observability` profile ekle.
7. Service overview dashboard ekle: traffic, errors, p95/p99, JVM, CPU, threads/connections.
8. Messaging dashboard ekle: Kafka lag/errors, RabbitMQ queue depth, DLT/DLQ, outbox backlog.
9. Offer Saga dashboard ekle: outcomes, hold conflicts, intermediate/stuck states/duration candidate'ları.
10. Search dashboard ekle: query latency/errors, Elasticsearch failure'ları, projection freshness.
11. Sustained 5xx, p99, lag, DLT/DLQ, outbox backlog, freshness ve pool saturation için sample Prometheus alert rule'ları ekle.
12. Her alert actionable summary ve runbook link içersin.
13. Grafana datasource'larının healthy, Prometheus target'ların UP, Tempo'nun trace ve Loki'nin log aldığını doğrula.
14. Destekleniyorsa traceId ile log'dan trace'e navigation doğrula.
15. Loki/Tempo/Prometheus unavailable olsa bile application correctness'in etkilenmediğini test et.
16. Uygun local resource/memory guidance ekle.

## Commit sırası
1. `infra(observability): add Tempo`
2. `infra(observability): add Prometheus`
3. `infra(observability): add Loki and supported log pipeline`
4. `infra(observability): add Grafana provisioning`
5. `docs(grafana): add service and messaging dashboards`
6. `docs(grafana): add Saga and Search dashboards`
7. `feat(observability): add sample actionable alerts`
8. `infra(observability): add optional observability profile`
9. `test(observability): verify local stack integration`
10. `docs(observability): finalize local observability guide`

## Final gate
- üç datasource provisioned
- service'ler scraped
- log/trace'ler görünür
- dashboard'lar yükleniyor
- alert'ler actionable, yalnız noise üretmiyor
- observability backend outage business flow'u bozmuyor
