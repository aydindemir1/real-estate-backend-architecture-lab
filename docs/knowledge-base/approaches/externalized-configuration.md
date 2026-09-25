# Externalized Configuration

**Category:** Approach  
**Introduced:** Day 1  
**Project status:** Implemented / Strengthened in Day 7  
**Scope:** Separating runtime configuration from application source code

## 1. Nedir?

Externalized Configuration, application'ın environment'a göre değişen configuration değerlerinin source code içine hard-code edilmemesi yaklaşımıdır.

## 2. Amaç

Aynı application artifact'ının:
- local
- test
- staging
- production

ortamlarında farklı configuration ile çalışabilmesini sağlar.

## 3. Neler externalize edilir?

Örnekler:
- database URL
- username
- password
- service endpoint
- JWT settings
- broker address
- feature toggle
- timeout
- profile
- port

## 4. Yaygın kaynaklar

- environment variables
- application.yml
- profile-specific files
- Config Server
- secret manager
- command line args
- mounted configuration files

## 5. Bu projede nasıl kullanılıyor?

Day 1'den itibaren database connection gibi değerler environment variable ile override edilebilir hale getirilmiştir.

Day 4'te Spring Cloud Config ile merkezi configuration eklenmiştir.

Day 7'de:
- local credential'lar `.env` içine alınmış
- `.env.example` contract olarak tutulmuş
- gerçek `.env` Git tarafından ignore edilmiş
- literal secret fallback'ler azaltılmıştır

## 6. Centralized Configuration ile farkı

Externalized Configuration daha geniş kavramdır.

Bir config'in source code dışında olması externalization'dır.

Centralized Configuration ise bu external config'in merkezi bir sistemden yönetilmesidir.

## 7. Avantajları

- source code ile environment ayrılır
- secret leak riski azalır
- aynı artifact farklı ortamda kullanılabilir
- deployment flexibility artar
- immutable artifact yaklaşımını destekler

## 8. Riskler

- env var sprawl
- config naming inconsistency
- missing variable
- unsafe default value
- secret'in yanlış loglanması
- local config drift

## 9. İyi uygulamalar

- typed configuration
- validation
- safe defaults
- secret için fallback kullanmama
- naming convention
- sample env file
- config documentation
- environment-specific ownership

## 10. İlgili teknolojiler

- Spring Boot Configuration
- Spring Cloud Config
- Docker Compose
- .env
- environment variables
- Spring Cloud Vault

## 11. İleri öğrenme konuları

- @ConfigurationProperties
- config validation
- secret rotation
- config reload
- immutable infrastructure
