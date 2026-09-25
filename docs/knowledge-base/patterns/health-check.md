# Health Check Pattern

**Category:** Pattern  
**Introduced:** Day 5A / Extended Day 7  
**Project status:** Implemented / Verified  
**Scope:** Determining runtime component health

## 1. Nedir?

Health Check Pattern, bir application veya infrastructure component'in çalışabilir durumda olup olmadığını programatik olarak bildirmesini sağlar.

## 2. Hangi problemi çözer?

Process'in ayakta olması service'in gerçekten usable olduğu anlamına gelmez.

Örneğin:
- database erişilemiyor olabilir
- broker connection bozuk olabilir
- disk problemi olabilir

## 3. Health türleri

### Liveness

Process hayatta mı?

### Readiness

Traffic almaya hazır mı?

### Dependency Health

Database/broker gibi dependency'ler kullanılabilir mi?

## 4. Bu projede nasıl kullanılıyor?

Day 5A'da Spring Boot Actuator ile application health endpoint'leri eklenmiştir.

Day 7'de Docker Compose infrastructure için:
- PostgreSQL
- MySQL
- MongoDB
- Couchbase
- Cassandra
- Elasticsearch
- Redis
- RabbitMQ

healthcheck'leri kullanılmıştır.

## 5. Docker healthcheck

Container runtime belirli komutu periyodik çalıştırır.

Örnek:
- `redis-cli ping`
- `pg_isready`
- `mysqladmin ping`

## 6. Avantajları

- orchestration kararları
- monitoring
- startup validation
- automated recovery

## 7. Riskler

Yanlış healthcheck:
- çok pahalı olabilir
- dependency cascade yaratabilir
- false positive/negative üretebilir

## 8. Production considerations

- liveness ve readiness ayrılmalıdır
- timeout kısa tutulmalıdır
- check lightweight olmalıdır
- external dependency health dikkatli modellenmelidir

## 9. İlgili teknolojiler

- Spring Boot Actuator
- Docker Compose Healthcheck
- Kubernetes Probes

## 10. İleri öğrenme konuları

- readiness groups
- startup probes
- graceful shutdown
- synthetic health checks
