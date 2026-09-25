# Docker Compose Profiles

**Category:** Technology Feature  
**Introduced:** Day 7  
**Project status:** Implemented / Verified  
**Scope:** Selective local infrastructure activation

## 1. Nedir?

Docker Compose Profiles, Compose içindeki service'lerin yalnız belirli profile aktif olduğunda çalıştırılmasını sağlayan selective activation özelliğidir.

## 2. Hangi problemi çözer?

Bu projede aynı anda:
- Cassandra
- Elasticsearch
- Couchbase
- Redis
- MySQL
- MongoDB
- PostgreSQL
- RabbitMQ

çalıştırmak geliştirici makinesinde gereksiz CPU/RAM tüketebilir.

Profiles yalnız ihtiyaç duyulan infrastructure'ı ayağa kaldırmayı sağlar.

## 3. Temel kullanım

Compose service:

```yaml
services:
  agent-mysql:
    profiles:
      - agent
```

Çalıştırma:

```powershell
docker compose --profile agent up -d
```

## 4. Bu projedeki profile modeli

Day 7 sonunda:

- core
- agent
- buyer
- seller
- property
- search
- redis

profile'ları tanımlanmıştır.

## 5. Core profile

`core` şu service'leri kapsar:

- Auth PostgreSQL
- UserProfile PostgreSQL
- RabbitMQ

Bu service'ler erken baseline microservice flow'ları için gerekli temel infrastructure'dır.

## 6. Service-specific profiles

```text
agent    -> MySQL
buyer    -> Couchbase
seller   -> Cassandra
property -> MongoDB
search   -> Elasticsearch
redis    -> Redis
```

## 7. Plain docker compose config neden services: {} gösterir?

Day 7'de bütün Compose service'leri profile-gated hale getirilmiştir.

Bu nedenle profile belirtmeden:

```powershell
docker compose config
```

çıktısında service listesi boş olabilir.

Bu hata değildir.

## 8. Validation

Belirli profile service'lerini görmek için:

```powershell
docker compose --profile core config --services
docker compose --profile agent config --services
```

Birden fazla profile aynı anda verilebilir.

## 9. Resource management faydası

Profiles:
- heavy datastore'ların gereksiz çalışmasını önler
- local RAM tüketimini azaltır
- startup time azaltır
- task-focused environment sağlar

## 10. Heavy infrastructure

Bu projede özellikle:
- Cassandra
- Elasticsearch
- Couchbase

daha ağır local resource tüketebilir.

Bu nedenle selective startup bilinçli engineering kararıdır.

## 11. Profiles orchestration isolation değildir

Profile:
> Hangi service aktif?

sorusunu çözer.

Şunları tek başına çözmez:
- CPU limit
- memory limit
- dependency health
- security isolation
- production scheduling

## 12. Bu projede neden önemli?

Roadmap ilerledikçe:
- Kafka
- observability stack
- Jenkins
- SonarQube
- Nexus
- Harbor
- Kubernetes

gibi ek infrastructure gelecektir.

Hepsinin sürekli birlikte çalıştırılması local development için uygun değildir.

Profiles yaklaşımı bu büyümeyi kontrollü tutar.

## 13. Avantajları

- selective startup
- düşük local resource pressure
- role/use-case based stack
- daha hızlı developer workflow
- daha kolay troubleshooting

## 14. Trade-off'ları

- profile kombinasyonları yönetilmelidir
- required dependency yanlış profile'da unutulabilir
- documentation gerekir
- CI profile selection açık olmalıdır

## 15. Engineering rule

Her yeni heavy infrastructure service eklenirken şu soru sorulmalıdır:

> Bu service her local workflow'da gerekli mi?

Cevap hayırsa uygun Compose profile değerlendirilmelidir.

## 16. İleri öğrenme konuları

- Compose overrides
- resource limits
- profile matrices
- CI integration
- local developer scripts
