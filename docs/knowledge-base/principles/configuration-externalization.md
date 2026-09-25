# Configuration Externalization Principle

**Category:** Principle  
**Introduced:** Day 1  
**Project status:** Implemented / Strengthened in Day 7  
**Scope:** Keeping environment-specific runtime configuration outside source code

## 1. Nedir?

Configuration Externalization Principle, environment'a göre değişen değerlerin application source code içine gömülmemesi gerektiğini söyler.

## 2. Neden önemlidir?

Kod business behavior ve application logic taşır. Host, port, credential, URL, secret ve timeout gibi environment-specific değerler runtime/deployment environment tarafından sağlanmalıdır.

## 3. Amaç

- same artifact, different environment
- reproducible deployment
- secret hygiene
- config governance
- environment independence

## 4. Hard-coded configuration problemi

Hard-coded password, URL veya environment değeri security risk, deployment coupling ve environment dependency oluşturur.

## 5. Doğru yaklaşım

Application configuration abstraction üzerinden environment variables, Config Server, secret manager veya profile-specific configuration kaynaklarına bağlanır.

## 6. Externalized Configuration approach ile ilişkisi

Bu dosya prensibi açıklar. approaches/externalized-configuration.md ise bu prensibin pratik mühendislik yaklaşımını ve mekanizmalarını açıklar.

## 7. Bu projede nasıl uygulanıyor?

Datasource ayarları environment variable ile override edilebilir. Day 4'te Spring Cloud Config eklenmiştir. Day 7'de local .env kullanımı ve .env.example contract'ı ile secret/config hygiene güçlendirilmiş, literal secret fallback'ler azaltılmıştır.

## 8. Secret ile normal config ayrımı

Port, timeout ve feature setting normal config olabilir. Password, token ve private key secret'tır. Secret'ler için dedicated secret manager tercih edilir; roadmap'te bunun karşılığı Spring Cloud Vault milestone'ıdır.

## 9. Riskler

- missing config
- invalid config
- unsafe default
- environment drift
- secret exposure in logs
- naming inconsistency

## 10. İyi uygulamalar

- typed configuration
- startup validation
- secret masking
- no secret defaults
- documented variables
- consistent naming
- critical config missing ise fail-fast

## 11. Production considerations

Secret rotation, audit, encryption, RBAC, config rollout, rollback ve blast radius değerlendirilmelidir.

## 12. İleri öğrenme konuları

- config hierarchy
- secret management
- immutable infrastructure
- dynamic configuration
- Kubernetes ConfigMap/Secret