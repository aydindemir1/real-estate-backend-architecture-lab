# Centralized Configuration

**Category:** Approach  
**Introduced:** Day 4  
**Project status:** Implemented / Integrated / Verified  
**Scope:** Shared external service configuration management

## 1. Nedir?

Centralized Configuration, birden fazla service'in configuration değerlerinin her application içine ayrı ayrı gömülmesi yerine merkezi bir configuration kaynağından yönetilmesi yaklaşımıdır.

## 2. Hangi problemi çözer?

Microservices sayısı arttıkça:
- duplicate config
- inconsistent values
- environment drift
- manual update
- deployment bağımlılığı

gibi problemler oluşur.

Centralized Configuration bunları tek kontrollü source üzerinden yönetmeyi sağlar.

## 3. Ne işe yarar?

- service configuration'ını merkezileştirir
- environment-specific config yönetimini kolaylaştırır
- config duplication azaltır
- service artifact ile environment config'i ayırır
- merkezi governance sağlar

## 4. Tipik mimari

```text
        Config Repository
              |
              v
        Config Server
          /   |   \
         v    v    v
      Service Service Service
```

## 5. Bu projede kullanılan model

Day 4'te iki Config Server yaklaşımı kurulmuştur:

```text
ConfigServerLocal :8888
  -> native/classpath config repo

ConfigServerRemote :8889
  -> Git-based config repo
```

Business service'ler config client olarak bu server'lardan configuration alır.

## 6. Centralized Configuration ile secrets aynı şey değildir

Config Server:
- port
- feature configuration
- service URL
- timeout
- non-secret application setting

için uygundur.

Secret:
- password
- token
- private key
- credential

için ayrı secrets management sistemi tercih edilmelidir.

Bu nedenle roadmap'te Spring Cloud Vault ayrı bir milestone'dır.

## 7. Avantajları

- configuration consistency
- environment management
- merkezi versioning
- config governance
- artifact/config separation
- operational visibility

## 8. Dezavantajları

- merkezi dependency oluşabilir
- Config Server availability önemli hale gelir
- bootstrap complexity artar
- secret ile config yanlış karıştırılabilir
- yanlış config çok sayıda servisi aynı anda etkileyebilir

## 9. Production considerations

- config repository access control
- versioning
- rollback
- secret separation
- startup fail-fast policy
- config validation
- change audit
- refresh strategy
- blast radius

## 10. Bu projedeki ilgili teknolojiler

- Spring Cloud Config
- Git
- application.yml
- environment variables

## 11. Alternatifleri

- Kubernetes ConfigMap
- Consul KV
- Vault KV
- environment-only configuration
- platform-native configuration services

## 12. İleri öğrenme konuları

- Spring Cloud Bus refresh
- config refresh semantics
- Vault integration
- Kubernetes ConfigMap/Secret
- immutable deployment configuration
