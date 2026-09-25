# Docker

**Category:** Technology  
**Introduced:** Day 7 infrastructure phase  
**Project status:** Implemented / Verified  
**Scope:** Containerization runtime and image-based application packaging

## 1. Nedir?

Docker, application ve dependency'lerini image adı verilen immutable paketler halinde tanımlayıp container olarak çalıştırmayı sağlayan container platformudur.

Container, işletim sistemi seviyesinde process isolation kullanır.

## 2. Container ile virtual machine farkı

Virtual machine:

```text
Hardware
  |
Host OS
  |
Hypervisor
  |
Guest OS
  |
Application
```

Container:

```text
Hardware
  |
Host OS / Kernel
  |
Container Runtime
  |
Containerized Processes
```

Container'lar ayrı bir guest kernel taşımaz; host kernel capability'lerini paylaşır.

## 3. Ana kavramlar

- Dockerfile
- Image
- Container
- Layer
- Registry
- Volume
- Network
- Port Mapping
- Environment Variable
- Healthcheck

## 4. Image nedir?

Image, container oluşturmak için kullanılan immutable template'tir.

Layered filesystem mantığı kullanır.

```text
Base Image
   |
   +--> dependency layer
   |
   +--> application layer
   |
   +--> metadata
```

## 5. Container nedir?

Container, image'in runtime instance'ıdır.

Container:
- process çalıştırır,
- network namespace kullanır,
- filesystem layer alır,
- environment/config ile başlatılır.

Image ile container aynı şey değildir.

## 6. Docker Engine mimarisi

Basitleştirilmiş yapı:

```text
Docker CLI
   |
   v
Docker Daemon
   |
   v
containerd
   |
   v
OCI Runtime
   |
   v
Container Process
```

Modern Docker architecture altında containerd ve OCI runtime katmanları bulunur.

## 7. Dockerfile

Dockerfile image build tarifidir.

Yaygın instruction'lar:
- FROM
- WORKDIR
- COPY
- RUN
- ENV
- EXPOSE
- ENTRYPOINT
- CMD

## 8. Layer caching

Her Dockerfile instruction yeni layer oluşturabilir.

Doğru layer sıralaması:
- build cache
- image size
- rebuild speed

üzerinde etkilidir.

## 9. Multi-stage build

Build toolchain ile runtime image ayrılabilir.

```text
Builder Stage
  |
  v
Artifact
  |
  v
Runtime Stage
```

Avantaj:
- daha küçük image
- daha az attack surface
- runtime'da build tool gerektirmez

## 10. Network

Docker container'ları network üzerinden birbirine bağlanabilir.

Aynı Compose network'ünde service name DNS hostname gibi kullanılabilir.

## 11. Port mapping

Örnek:

```text
Host 3307 -> Container 3306
```

Container internal port ile host-exposed port aynı olmak zorunda değildir.

## 12. Volume

Container filesystem ephemeral olabilir.

Persistent data için volume kullanılır.

Örnek:
- database data
- broker data

Volume container silinse bile korunabilir.

## 13. Environment Variables

Runtime configuration container'a environment variable üzerinden verilebilir.

Bu proje Day 7'de credentials ve datastore settings için environment-driven configuration kullanmaktadır.

## 14. Healthcheck

Docker container'ın runtime health durumunu belirleyebilir.

Healthcheck:
- process'in başlamasını
- service'in gerçekten usable olmasını

ayırt etmeye yardımcı olur.

## 15. Registry

Docker image'ları registry'de tutulabilir.

Örnek:
- Docker Hub
- Harbor
- private registry

Roadmap'in ileri DevOps aşamasında Harbor ayrıca ele alınacaktır.

## 16. Bu projede nasıl kullanılıyor?

Day 7 local infrastructure foundation'da şu teknolojiler container olarak çalıştırılmıştır:

- PostgreSQL
- MySQL
- MongoDB
- Couchbase
- Cassandra
- Elasticsearch
- Redis
- RabbitMQ

Her service için explicit image version kullanılmıştır.

## 17. Avantajları

- reproducible environment
- dependency isolation
- portable packaging
- fast local setup
- CI/CD friendliness
- immutable image model

## 18. Trade-off'ları

- image management
- registry management
- container security
- storage/network complexity
- host resource consumption
- stateful workload operasyonu

## 19. Security considerations

- root user'dan kaçınma
- minimal base image
- secret'i image içine koymama
- read-only filesystem consideration
- vulnerability scanning
- image provenance
- version pinning

## 20. Production considerations

- resource limits
- healthcheck
- graceful shutdown
- immutable image
- logging
- secrets
- registry
- image scanning
- restart policy
- persistent storage

## 21. Anti-pattern'ler

- latest tag kullanmak
- secret'i Dockerfile'a yazmak
- database data'yı ephemeral layer'da bırakmak
- container'ı VM gibi kullanmak
- bir container içine çok sayıda unrelated process koymak
- image'i gereksiz tool'larla şişirmek

## 22. Bu projedeki sonraki aşama

Docker yalnız Day 7 local database foundation değildir.

Roadmap'in backend sonrası DevOps aşamasında:
- image hardening
- multi-stage builds
- registry
- CI/CD
- Kubernetes

ile daha ileri seviyeye taşınacaktır.

## 23. İleri öğrenme konuları

- namespaces
- cgroups
- containerd
- OCI
- overlay filesystem
- rootless containers
- BuildKit
- image signing
- SBOM
