# Spring Boot Actuator

**Category:** Technology  
**Introduced:** Day 5A  
**Project status:** Implemented / Verified  
**Scope:** Runtime management, health and operational endpoints

## 1. Nedir?

Spring Boot Actuator, Spring Boot uygulamalarına production-oriented management ve observability endpoint'leri ekleyen Spring Boot modülüdür.

Uygulamanın yalnız business API'lerini değil, runtime durumunu da görünür hale getirir.

## 2. Hangi problemi çözer?

Bir service process olarak ayakta olsa bile gerçekten sağlıklı olmayabilir.

Örneğin:
- database bağlantısı bozulmuş olabilir,
- broker erişilemiyor olabilir,
- disk alanı kritik olabilir,
- readiness koşulları sağlanmıyor olabilir.

Actuator runtime state'i programatik olarak izlenebilir hale getirir.

## 3. Temel endpoint'ler

Yaygın endpoint'ler:
- `/actuator/health`
- `/actuator/info`
- `/actuator/metrics`
- `/actuator/prometheus`
- `/actuator/env`
- `/actuator/configprops`
- `/actuator/loggers`

Tüm endpoint'lerin production'da public edilmesi doğru değildir.

## 4. Health sistemi

Actuator health sistemi çeşitli HealthIndicator bileşenlerinden veri toplar.

```text
HTTP Request
    |
    v
/actuator/health
    |
    v
HealthEndpoint
    |
    +--> Database HealthIndicator
    +--> DiskSpace HealthIndicator
    +--> Messaging HealthIndicator
    +--> Custom HealthIndicator
```

## 5. Liveness ve Readiness

Actuator, orchestration platformlarıyla birlikte liveness/readiness state'lerinin yönetilmesine yardımcı olabilir.

- Liveness: process yeniden başlatılmalı mı?
- Readiness: traffic almaya hazır mı?

Bu iki kavram aynı değildir.

## 6. Metrics ile ilişkisi

Actuator metrics endpoint'i Micrometer tarafından toplanan metric'leri expose edebilir.

Actuator metric üretiminin kendisi değildir; management exposure katmanı sağlar.

## 7. Security

Management endpoint'leri hassas bilgi içerebilir.

Özellikle:
- env
- configprops
- heapdump
- mappings

gibi endpoint'ler production'da kontrollü açılmalıdır.

## 8. Bu projede nasıl kullanılıyor?

Day 5A'da Gateway ve ilgili Spring Boot service'lerde Actuator dependency'si observability/resilience foundation'ın bir parçası olarak kullanılmıştır.

Day 7 infrastructure healthcheck yaklaşımıyla birlikte application-level health visibility'nin temelini oluşturur.

## 9. Avantajları

- standard health endpoint
- metrics exposure
- runtime diagnostics
- orchestration integration
- Spring Boot native support

## 10. Trade-off'ları

- yanlış exposure security riski yaratır
- pahalı diagnostic endpoint'ler runtime impact oluşturabilir
- health dependency modeli yanlış tasarlanırsa false unhealthy state üretilebilir

## 11. Production considerations

- endpoint exposure allowlist
- authentication/authorization
- separate management port ihtiyacı
- readiness/liveness group tasarımı
- sensitive value masking
- metric cardinality
- health check timeout

## 12. İlgili kavramlar

- Health Check Pattern
- Micrometer
- Prometheus
- Kubernetes Probes
- Operational Readiness

## 13. İleri öğrenme konuları

- custom HealthIndicator
- health groups
- management server configuration
- Prometheus endpoint
- custom actuator endpoints
