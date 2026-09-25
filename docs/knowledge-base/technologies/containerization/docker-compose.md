# Docker Compose

**Category:** Technology  
**Introduced:** Day 7 infrastructure phase  
**Project status:** Implemented / Verified  
**Scope:** Local multi-container orchestration

## 1. Nedir?

Docker Compose, birden fazla Docker container'ını declarative YAML configuration üzerinden birlikte tanımlayıp çalıştırmayı sağlayan local orchestration aracıdır.

## 2. Hangi problemi çözer?

Birden fazla container'ı manual olarak şu şekilde yönetmek:
- docker run
- port
- environment
- volume
- network
- healthcheck

komutlarıyla yürütmek hızla karmaşık hale gelir.

Compose bunları tek configuration altında toplar.

## 3. Ana kavramlar

- services
- image
- build
- environment
- ports
- volumes
- networks
- depends_on
- healthcheck
- profiles

## 4. Compose modeli

```text
docker-compose.yml
    |
    v
Compose Project
    |
    +--> Service A -> Container
    +--> Service B -> Container
    +--> Service C -> Container
    |
    +--> Networks
    +--> Volumes
```

## 5. Service

Compose service bir container runtime definition'ıdır.

Aynı service scale edildiğinde birden fazla container instance oluşabilir.

## 6. Network

Compose varsayılan olarak project-scoped network oluşturabilir.

Service'ler birbirine service name ile erişebilir.

Örnek:

```text
agent-mysql:3306
rabbitmq:5672
```

## 7. Volume

Named volume ile persistent state declarative biçimde tanımlanabilir.

Bu projede datastore data'ları named volume'larda tutulmaktadır.

## 8. Environment

Environment values:
- inline
- shell environment
- .env

üzerinden resolve edilebilir.

Bu projede gerçek secret'lar repo dışındaki `.env` ile sağlanır; `.env.example` contract/documentation amacıyla tutulur.

## 9. Healthcheck

Compose healthcheck, service readiness için runtime command çalıştırabilir.

Day 7 örnekleri:
- PostgreSQL -> pg_isready
- MySQL -> mysqladmin ping
- MongoDB -> mongosh ping
- Cassandra -> cqlsh
- Elasticsearch -> cluster health
- Redis -> redis-cli ping

## 10. depends_on

Container startup order konusunda yardımcı olabilir.

Ancak yalnız process start order, business readiness garantisi değildir.

Health condition kullanımı daha güvenli olabilir.

## 11. Bu projede nasıl kullanılıyor?

Day 7 sonunda local infrastructure şu Compose service'lerinden oluşmaktadır:

- postgres
- user-profile-postgres
- rabbitmq
- agent-mysql
- buyer-couchbase
- seller-cassandra
- property-mongodb
- elasticsearch
- redis

## 12. Day 7 cleanup

Agent, Buyer, Seller ve Property için temporary PostgreSQL Compose service'leri kaldırılmıştır.

Böylece target persistence topology ile local infrastructure hizalanmıştır.

## 13. Explicit image versions

Compose file'da image tag'leri explicit tutulur.

Amaç:
- reproducibility
- controlled upgrades
- predictable local environment

## 14. Avantajları

- declarative local stack
- repeatable startup
- network/volume automation
- environment management
- healthcheck integration
- onboarding kolaylığı

## 15. Trade-off'ları

- production orchestrator değildir
- büyük stack resource-heavy olabilir
- stateful volume lifecycle dikkat ister
- local-only assumptions production'a taşınmamalıdır

## 16. Docker Compose vs Kubernetes

Compose:
- local development
- small integration environment
- simple orchestration

Kubernetes:
- cluster scheduling
- self-healing
- rollout
- service discovery
- autoscaling
- production platform capability

Bu projede önce Docker/Compose, daha sonra native Kubernetes öğrenilecektir.

## 17. Sık kullanılan komutlar

```powershell
docker compose config
docker compose up -d
docker compose ps
docker compose logs
docker compose down
```

Profile ile kullanım ayrıca ayrı canonical dokümanda anlatılır.

## 18. Production considerations

Compose production için kullanılabilir senaryolar bulunsa da bu projede production target orchestration olarak değerlendirilmez.

Roadmap production-like orchestration için Kubernetes'e ilerler.

## 19. Anti-pattern'ler

- tüm service'leri sürekli birlikte açmak
- secret'i compose file'a hard-code etmek
- latest image
- healthcheck olmadan heavy datastore çalıştırmak
- volume lifecycle'ı bilmeden `down -v` kullanmak

## 20. İleri öğrenme konuları

- override files
- profiles
- dependency conditions
- custom networks
- resource limits
- Compose Watch
