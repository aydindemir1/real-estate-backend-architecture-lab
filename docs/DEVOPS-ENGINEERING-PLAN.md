# DevOps Engineering Plan — Day 46–85

**Durum: Onaylanmış eğitim planı; uygulama Planlandı.** Day 1–45 backend/architecture programı korunur. Bu belge Day 46–85'in ortak karar kaynağıdır; [gün dizini](roadmap/README.md) ayrıntılı görev ve commit planlarına bağlanır.

## Amaç ve çalışma modeli

Java, Spring Boot ve Spring Cloud ekosistemindeki mevcut projeyi yerel ortamda build, test, yayınlama, deployment, işletim ve recovery süreçleriyle öğrenmek. Senior/staff/principal/architect düzeyinde karar muhakemesi için failure modes, ownership, güvenlik, maliyet/kaynak, geri dönüş ve ölçülebilir kanıt birlikte ele alınır. Günleri tamamlamak otomatik olarak bir mesleki unvan kazandırmaz.

- 40 milestone, 40 takvim günü zorunluluğu değildir. Yeni backend kapsamı bu aşamaya gizlice eklenmez.
- AWS/Azure/GCP, HCP ve ücretli SaaS gerekmeyecek; servisler yerel/self-hosted çalışacak. Ürünlerin ücretsiz dağıtımının sürüm ve lisans koşulları kurulurken doğrulanır.
- CPU/RAM/disk bütçesi Day 46'da ölçülür. Ağır platformlar ihtiyaç duyulan fazda açılır; tüm stack'in sürekli aynı anda çalışması gerekmez.
- Day 45'in tamamlanmış implementation branch'i başlangıçtır. Bu docs branch'i çalışma planının canonical kaynağıdır; runtime kaynağı değildir.
- Gün branch'i önceki tamamlanmış milestone'dan türetilir. Mevcut test, ADR, canonical datastore ve mesaj güvenilirliği kararları korunur.

## Teknoloji ve sorumluluk kararları

Seçim; ekosistem tanınırlığı, öğrenme değeri, mevcut kararlar ve ücretsiz yerel kullanım üzerinden yapılır. Bu tablo ölçülmüş küresel pazar payı sıralaması değildir. Tam alternatifler ikinci kez uygulanmaz; farklı sorumluluklar birlikte kullanılabilir.

| Sorumluluk | Uygulama seçimi | Karşılaştırma / sınır |
|---|---|---|
| Container ve yerel servis geliştirme | Docker, BuildKit, Docker Compose | Podman karşılaştırma; ikinci container workflow kurulmaz |
| Kubernetes lab | Minikube + Docker driver, kubectl | kind/k3d karşılaştırma; tek ana lab |
| Kubernetes CNI ve policy | Calico | Cilium karşılaştırma; cluster başına tek CNI |
| Edge routing ve TLS | Traefik + Gateway API | HAProxy/NGINX alternatiflerini karşılaştır; sürüm uyumluluğunu doğrula |
| Platform paketleri | Helm | Uygulama overlay'lerini ayrıca Helm ile çoğaltma |
| Proje uygulama overlay'leri | Kustomize | Helm ile aynı resource'un iki bağımsız sahibi olmaz |
| Host konfigürasyonu | Ansible | Puppet/Chef/SaltStack karşılaştırma |
| Yerel kaynak provisioning | Terraform Community CLI | OpenTofu karşılaştırma; HCP/cloud hesabı yok |
| Spring Kubernetes entegrasyonu | Spring Cloud Kubernetes | Eureka/Config ile discovery ve config ownership ADR'de belirlenir |
| Secrets | HashiCorp Vault, Kubernetes auth + Agent Injector | Aynı secret için Spring Cloud Vault/Injector çift fetch yok; managed secrets yok |
| CI ve pipeline | Jenkins, Pipeline-as-Code | GitLab CI/Tekton karşılaştırma; mevcut GitHub Actions hafif repo kontrolleri için korunur |
| Java quality/coverage | SonarQube Community Build, JaCoCo | Ücretli native branch/PR analysis kullanılmaz |
| Maven artifacts/dependency proxy | Nexus Repository ücretsiz self-hosted dağıtım | Artifactory karşılaştırma; OCI registry sorumluluğu Harbor'da |
| OCI image registry | Harbor | Nexus üzerinde aynı image repository workflow'u çoğaltılmaz |
| Vulnerability ve SBOM | Trivy, CycloneDX | Snyk/Grype karşılaştırma; Harbor/CI ortak scan motoru |
| Image imzalama ve provenance | Cosign, yerel anahtar | Secret key Git'e girmez; SLSA sertifikasyonu iddia edilmez |
| GitOps deployment | Argo CD | Flux karşılaştırma; Jenkins doğrudan cluster deploy etmez |
| Progressive delivery | Argo Rollouts | Flagger karşılaştırma; Argo CD ile görevleri farklıdır |
| Service mesh | Istio | Linkerd karşılaştırma; edge ingress'in ikinci sahibi değildir |
| Telemetry | OpenTelemetry, Prometheus, Grafana; mevcut Loki/Tempo kararları | Backend observability kaynakları yeniden incelenir; çalışan eşdeğer stack çoğaltılmaz |
| Load/performance test | k6 | JMeter/Gatling karşılaştırma |
| Kubernetes backup metadata | Velero + datastore-native backup | Kubernetes resource backup tek başına veritabanı tutarlılığı kanıtı değildir |

Terraform Docker provider ile yalnız kendisine tahsis edilmiş yerel network/volume/test container kaynaklarını yönetir. Compose uygulamaları, Minikube veya Argo CD resource'ları Terraform'a ikinci sahip olarak verilmez. Provider/CLI sürümü ve lock file commit edilir; state secrets içeriyorsa Git dışı, erişimi sınırlı saklanır.

Backup için yerel S3-compatible hedefin ürün, ücretsiz lisans, bakım ve Velero/plugin uyumluluğu Day 83 ADR'sinde doğrulanıp seçilir; bugün doğrulanmamış bir ürün zorunlu tutulmaz.

## Kaynak sahipliği

| Kaynak / veri | Yetkili sahip | Çakışmayı engelleyen karar |
|---|---|---|
| Host paket ve ayarları | Ansible | Terraform bu ayarları tekrar yönetmez |
| Ayrılmış yerel provisioning kaynakları | Terraform | Compose/Argo kapsamından ayrı isim ve lifecycle |
| Platform paketleri | Başlangıçta Helm; GitOps geçişinden sonra Argo CD | Day 74 geçişinde aynı release için bağımsız CLI reconcile bırakılmaz |
| Uygulama Kubernetes manifest'leri | Kustomize; runtime reconcile Argo CD | Jenkins artifact üretir, deploy yetkisi taşımaz |
| Runtime secret değerleri | Vault | ConfigMap ve Git'te plaintext secret yok |
| Config ve discovery | Day 54/62 ownership ADR'si | Spring Config/Eureka/Kubernetes aynı anahtar veya servis için yarışmaz |
| Business canonical veri | Mevcut datastore sahibi servis | Cache, mesh, registry ve backup yeni canonical kaynak olmaz |
| Java binary / container image | Nexus / Harbor | Aynı image digest staging ve prod-like lab ortamına promote edilir |
| Trafik katmanları | Traefik edge, Spring Cloud Gateway uygulama, Istio internal | Retry/timeout ve identity sınırları ADR'de ayrılır |

## Fazlar ve sıralama

| Günler | Faz | Kapanış hedefi |
|---|---|---|
| 46–50 | Linux ve container | Tekrarlanabilir, secretsız, kaynak sınırı doğrulanmış servis build/runtime |
| 51–62 | Kubernetes ve Spring entegrasyonu | Policy, TLS, probes, storage, config/discovery sınırları doğrulanmış lab |
| 63–65 | Infrastructure as Code | Ansible/Terraform sahipliği, drift ve yeniden oluşturma |
| 66–73 | CI ve software supply chain | Test/quality/security gate, imzalı artifact ve build-once contract |
| 74–78 | GitOps ve release | Ortam promotion, canary/blue-green, migration ve rollback/forward-fix |
| 79–82 | Mesh, scale ve telemetry | Identity, kapasite, SLO ve performans kanıtı |
| 83–85 | Recovery ve final audit | Gerçek restore, upgrade ve failure tatbikatı |

Day 77 progressive delivery, mevcut backend Prometheus baseline'ını kullanır; Day 81 onu Kubernetes seviyesine genişletir. Day 51 bootstrap'ta Calico seçimi yapılır; Day 59 policy enforcement derinleştirilir. Tüm günler önceki kapanmış günün birikimli ortamını kullanır.

## Ücretsiz kullanım ve entegrasyon sınırları

- Jenkins localhost'a gelen webhook'u varsaymaz: erişim sağlanana kadar polling/manual trigger kullanılır.
- Untrusted PR build'leri publishing secret'larına erişmez. Docker socket host seviyesinde yetki taşıdığı için agent isolation kararı ve riskleri belgelenir.
- SonarQube Community Build yalnız main analysis desteklediğinden seçilmiş implementation milestone branch'i ayrı Sonar project'in main source'u olarak analiz edilir. GitHub main'e merge zorunluluğu yoktur; native PR decoration/branch analysis varmış gibi gösterilmez.
- Minikube multi-node aynı host üzerinde fiziksel HA veya control-plane HA kanıtı değildir. HPA kapasite testinde host tavanı ve metrics availability ölçülür.
- Traefik/Gateway API/Argo Rollouts traffic routing uyumluluğu seçilmiş sürümlerde test edilir; replica yüzdesi gerçek trafik yüzdesi yerine geçmez.
- Istio temsilî servislerde uygulanır; tüm stack'in mesh'e alındığı ancak doğrulanırsa söylenir.
- Prod-like lab gerçek production/SLA değildir. Backup aynı makinedeyse host kaybı koruması sağlandığı iddia edilmez.

## Günlük çalışma ve kanıt standardı

Her kesin plan amaç, önkoşullar, konular, görevler, aday dosyalar, commit sırası, failure senaryoları ve kapanış checklist'i içerir. Aday dosya listesi mevcut implementation branch'inde incelenir; mevcut eşdeğer dosya güncellenir. Yeni teknoloji için ADR; seçimin gerekçesi, alternatifleri, ownership, failure mode, kaynak bütçesi ve geri dönüş planını kapsar.

Evidence; komut ve secretsız çıktı, image digest, tool/cluster/chart/CRD sürümü, beklenen/gözlenen davranış, ölçüm ve toparlanmayı içerir. Secret/key/token, kişisel veri ve credentials commit edilmez. Gerçek runtime testi olmadan Verified statüsü verilmez.

Sürümler kurulum gününde resmi uyumluluk belgelerinden seçilir ve pin edilir; floating latest kullanılmaz. Kubernetes API/CRD, Spring Boot–Cloud BOM, Java, plugin ve chart uyumluluğu birlikte kaydedilir. Upgrade'de önce backup/restore provası ve migration notları, sonra kontrollü değişim yapılır.

Day 85 kapanışında capability matrisi, örnek uçtan uca commit→build→gate→registry→GitOps→runtime kanıtı, recovery raporu, incident/postmortem ve açık sınırlamalar teslim edilir. Karşılaştırma dokümanı hazırlanması alternatif ürünün uygulandığı anlamına gelmez.

## Resmi referanslar

- [Terraform dağıtımları](https://developer.hashicorp.com/terraform/intro/terraform-editions)
- [Minikube multi-node](https://minikube.sigs.k8s.io/docs/tutorials/multi_node/)
- [Calico ile Minikube](https://docs.tigera.io/calico/latest/getting-started/kubernetes/minikube)
- [SonarQube Community Build özellik karşılaştırması](https://docs.sonarsource.com/sonarqube-community-build/feature-comparison-table/)
- [Argo CD belgeleri](https://argo-cd.readthedocs.io/en/stable/)
- [Argo Rollouts belgeleri](https://argo-rollouts.readthedocs.io/en/stable/)
- [Traefik Kubernetes Gateway API](https://doc.traefik.io/traefik/providers/kubernetes-gateway/)
- [Velero belgeleri](https://velero.io/docs/)

Referanslar kararın başlangıç noktasıdır; seçilmiş sürümün kısıtları ilgili gün yeniden doğrulanır.
