# Spring Cloud Config

**Category:** Technology  
**Introduced:** Day 4  
**Project status:** Implemented / Integrated / Verified  
**Scope:** Centralized external configuration for distributed applications

## 1. Nedir?

Spring Cloud Config, distributed application'ların configuration değerlerini merkezi bir Config Server üzerinden yönetmeyi sağlayan Spring Cloud bileşenidir.

## 2. Ana bileşenler

### Config Server

Configuration source'u client service'lere HTTP üzerinden sunar.

### Config Client

Application startup sırasında Config Server'dan kendi configuration'ını alır.

## 3. Backend seçenekleri

Config Server farklı backend kullanabilir.

Yaygın:
- Git
- native filesystem/classpath
- Vault ile belirli entegrasyon modelleri
- composite backends

## 4. Bu projedeki yapı

Day 4'te iki Config Server oluşturulmuştur:

```text
ConfigServerLocal :8888
  -> native backend

ConfigServerRemote :8889
  -> Git backend
```

## 5. Runtime flow

```text
Service startup
    |
    v
spring.application.name
    |
    v
Config Server
    |
    v
Environment-specific configuration
    |
    v
Spring Environment
```

## 6. Application/Profile/Label modeli

Config lookup tipik olarak:
- application
- profile
- label

üçlüsüyle organize edilir.

Örnek:
```text
auth-service / dev / main
```

## 7. Ne işe yarar?

- centralized config
- environment consistency
- config versioning
- config reuse
- service artifact/config ayrımı

## 8. Avantajları

- distributed config governance
- Git history
- environment separation
- merkezi değişiklik yönetimi

## 9. Dezavantajları / trade-off'ları

- startup dependency
- central config outage
- config blast radius
- wrong secret usage
- refresh complexity

## 10. Config ile secret farkı

Spring Cloud Config secret manager değildir.

Password, token, private key gibi secret'lar dedicated secret management ile ele alınmalıdır.

Bu projede Spring Cloud Vault ayrı milestone'dır.

## 11. Bu projede nasıl kullanılıyor?

Auth, UserProfile, Agent, Buyer, Property, Seller ve infrastructure service'ler merkezi config'ten configuration alır.

Day 7'de literal secret fallback'ler azaltılmış ve local secret hygiene güçlendirilmiştir.

## 12. Production considerations

- backend repository access control
- fail-fast policy
- retry
- config validation
- rollback
- audit
- branch/label governance
- secret separation

## 13. İlgili Spring Cloud bileşenleri

- Spring Cloud Bus
- Spring Cloud Vault
- Spring Cloud Kubernetes

## 14. Alternatifleri

- Kubernetes ConfigMap
- Consul KV
- environment variables
- platform configuration services

## 15. İleri öğrenme konuları

- composite repositories
- encryption support
- refresh scope
- Spring Cloud Bus refresh
- config fail-fast
