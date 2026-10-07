# Backend mimari ve DevOps planı — ayrıntılı gün dizini

Bu dizin [ana ROADMAP.md](../../ROADMAP.md) dosyasındaki 85 milestone sırasına erişim sağlar. Day 1–6 stabil `main` baseline’dır; mevcut durum ve kanıtı ilgili implementation branch’inden okunur. Bu belge future planları tamamlanmış çalışma gibi göstermez.

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
- Day 46–85 DevOps programı, mevcut Day 1–45 kapsamından sonra uygulanır; ortak kararlar [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md) belgesindedir.


## Day 46–85 — DevOps eğitim programı

Bu günler Planlandı durumundadır. Ücretsiz yerel lab, seçilmiş tek alternatif ve ölçülebilir doğrulama ilkeleri [DevOps Engineering Plan](../DEVOPS-ENGINEERING-PLAN.md) belgesinde tanımlıdır.

| Gün | Konu | Ayrıntılı plan | Özet |
|---|---|---|---|
| Day 46 | Linux, process ve network temeli | [Kesin plan](day-46-exact-file-plan.md) | [Özet](day-46-linux-runtime-foundation.md) |
| Day 47 | Dockerfile ve multi-stage Java build | [Kesin plan](day-47-exact-file-plan.md) | [Özet](day-47-docker-multistage-build.md) |
| Day 48 | BuildKit, cache ve build secrets | [Kesin plan](day-48-exact-file-plan.md) | [Özet](day-48-buildkit-cache.md) |
| Day 49 | Container güvenliği ve kaynak sınırları | [Kesin plan](day-49-exact-file-plan.md) | [Özet](day-49-container-hardening.md) |
| Day 50 | Compose ortamı ve troubleshooting | [Kesin plan](day-50-exact-file-plan.md) | [Özet](day-50-compose-prodlike.md) |
| Day 51 | Minikube ve cluster mimarisi | [Kesin plan](day-51-exact-file-plan.md) | [Özet](day-51-minikube-foundation.md) |
| Day 52 | Deployment, ReplicaSet ve rollout temeli | [Kesin plan](day-52-exact-file-plan.md) | [Özet](day-52-kubernetes-workloads.md) |
| Day 53 | Service, DNS ve internal communication | [Kesin plan](day-53-exact-file-plan.md) | [Özet](day-53-kubernetes-networking.md) |
| Day 54 | ConfigMap, Secrets ve Vault entegrasyonu | [Kesin plan](day-54-exact-file-plan.md) | [Özet](day-54-kubernetes-config-secrets.md) |
| Day 55 | Probes, rollout ve graceful shutdown | [Kesin plan](day-55-exact-file-plan.md) | [Özet](day-55-probes-graceful-shutdown.md) |
| Day 56 | Kaynak yönetimi, JVM ve scheduling | [Kesin plan](day-56-exact-file-plan.md) | [Özet](day-56-resources-jvm-scheduling.md) |
| Day 57 | StatefulSet, storage ve veri yaşam döngüsü | [Kesin plan](day-57-exact-file-plan.md) | [Özet](day-57-stateful-storage.md) |
| Day 58 | RBAC, ServiceAccount ve Pod Security | [Kesin plan](day-58-exact-file-plan.md) | [Özet](day-58-rbac-pod-security.md) |
| Day 59 | Calico ve NetworkPolicy | [Kesin plan](day-59-exact-file-plan.md) | [Özet](day-59-calico-network-policy.md) |
| Day 60 | Gateway API, Traefik ve TLS lifecycle | [Kesin plan](day-60-exact-file-plan.md) | [Özet](day-60-gateway-api-tls.md) |
| Day 61 | Helm ve Kustomize ile deployment sahipliği | [Kesin plan](day-61-exact-file-plan.md) | [Özet](day-61-helm-kustomize.md) |
| Day 62 | Spring Cloud Kubernetes ve discovery/config sınırları | [Kesin plan](day-62-exact-file-plan.md) | [Özet](day-62-spring-cloud-kubernetes.md) |
| Day 63 | Ansible ile yerel host konfigürasyonu | [Kesin plan](day-63-exact-file-plan.md) | [Özet](day-63-ansible-host-configuration.md) |
| Day 64 | Terraform ile somut yerel kaynak provisioning | [Kesin plan](day-64-exact-file-plan.md) | [Özet](day-64-terraform-local-provisioning.md) |
| Day 65 | IaC drift, state ve lab yeniden oluşturma | [Kesin plan](day-65-exact-file-plan.md) | [Özet](day-65-iac-drift-rebuild.md) |
| Day 66 | Jenkins Pipeline-as-Code temeli | [Kesin plan](day-66-exact-file-plan.md) | [Özet](day-66-jenkins-foundation.md) |
| Day 67 | Jenkins agent ve credential isolation | [Kesin plan](day-67-exact-file-plan.md) | [Özet](day-67-jenkins-agent-isolation.md) |
| Day 68 | Test pipeline ve raporlama | [Kesin plan](day-68-exact-file-plan.md) | [Özet](day-68-ci-test-pipeline.md) |
| Day 69 | SonarQube Community Build ve JaCoCo | [Kesin plan](day-69-exact-file-plan.md) | [Özet](day-69-sonarqube-quality-gate.md) |
| Day 70 | Nexus ile Java artifact ve dependency yönetimi | [Kesin plan](day-70-exact-file-plan.md) | [Özet](day-70-nexus-artifact-management.md) |
| Day 71 | Harbor ve OCI image lifecycle | [Kesin plan](day-71-exact-file-plan.md) | [Özet](day-71-harbor-image-registry.md) |
| Day 72 | Trivy, SBOM, Cosign ve provenance | [Kesin plan](day-72-exact-file-plan.md) | [Özet](day-72-supply-chain-security.md) |
| Day 73 | Build once ve release contract | [Kesin plan](day-73-exact-file-plan.md) | [Özet](day-73-build-once-artifact-promotion.md) |
| Day 74 | Argo CD ve GitOps temeli | [Kesin plan](day-74-exact-file-plan.md) | [Özet](day-74-argocd-gitops-foundation.md) |
| Day 75 | Ortam izolasyonu, drift ve reconciliation | [Kesin plan](day-75-exact-file-plan.md) | [Özet](day-75-gitops-environments-drift.md) |
| Day 76 | Staging doğrulaması ve release promotion | [Kesin plan](day-76-exact-file-plan.md) | [Özet](day-76-staging-release-promotion.md) |
| Day 77 | Canary, blue-green ve Argo Rollouts | [Kesin plan](day-77-exact-file-plan.md) | [Özet](day-77-argo-rollouts-progressive-delivery.md) |
| Day 78 | Migration uyumluluğu ve rollback/forward-fix | [Kesin plan](day-78-exact-file-plan.md) | [Özet](day-78-migration-compatible-release.md) |
| Day 79 | Istio, service identity ve mTLS | [Kesin plan](day-79-exact-file-plan.md) | [Özet](day-79-istio-service-mesh.md) |
| Day 80 | HPA ve capacity doğrulaması | [Kesin plan](day-80-exact-file-plan.md) | [Özet](day-80-hpa-capacity.md) |
| Day 81 | Kubernetes observability, SLI/SLO ve alerting | [Kesin plan](day-81-exact-file-plan.md) | [Özet](day-81-kubernetes-observability-slo.md) |
| Day 82 | k6 ile performans ve release doğrulaması | [Kesin plan](day-82-exact-file-plan.md) | [Özet](day-82-k6-performance-validation.md) |
| Day 83 | Backup/restore ve veri recovery tatbikatı | [Kesin plan](day-83-exact-file-plan.md) | [Özet](day-83-kubernetes-backup-restore.md) |
| Day 84 | Platform upgrade ve yeniden kurulum | [Kesin plan](day-84-exact-file-plan.md) | [Özet](day-84-platform-upgrade-recovery.md) |
| Day 85 | Failure tatbikatı ve DevOps final audit | [Kesin plan](day-85-exact-file-plan.md) | [Özet](day-85-devops-final-audit.md) |
