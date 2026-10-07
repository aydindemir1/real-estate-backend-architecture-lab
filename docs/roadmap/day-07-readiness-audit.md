# Day 7 — Implementation Hazırlık Denetimi

## Karar

**ZORUNLU IMPLEMENTATION ÖNCESİ KONTROLLERLE HAZIR**

Day 7 başlayabilir. Herhangi bir architecture blocker yoktur; ancak milestone kapatılmadan önce implementation aşağıdaki düzeltmeleri uygulamalıdır.

## Doğrulanan mevcut main baseline

### Build / modules
- Java 21 toolchain.
- Spring Boot 4.1.1.
- Spring Cloud 2025.1.3.
- SearchService henüz `settings.gradle` içine kayıtlı değildir.
- Root `build.gradle` şu anda Web MVC, OpenAPI, MapStruct, Auth0 JWT ve OpenFeign dependency'lerini her subproject'e zorunlu olarak uygular.

### Service persistence
Mevcut service build dosyaları hâlâ aşağıdakiler için JPA + PostgreSQL kullanmaktadır:
- AgentService
- BuyerService
- SellerService
- PropertyService

Bu durum Day 7 planıyla uyumludur: Day 7 yalnızca infrastructure hazırlığı yapar; datastore kod migration'ı sonraki Day'lerde her seferinde tek bir service için gerçekleştirilir.

### Mevcut Docker Compose
Mevcut:
- Auth PostgreSQL
- UserProfile PostgreSQL
- Agent PostgreSQL
- Buyer PostgreSQL
- Property PostgreSQL
- Seller PostgreSQL
- RabbitMQ

Eksik ve bu nedenle Day 7 için doğru şekilde planlanmış:
- MySQL
- Couchbase
- Cassandra
- MongoDB
- Elasticsearch
- Redis

### Secret hygiene
`.gitignore` zaten aşağıdakileri doğru şekilde ignore eder:
- `.env`
- `.env.*`

ve şuna izin verir:
- `.env.example`

Ancak şu anda aşağıdaki yerlerde literal secret benzeri default değerler bulunmaktadır:
- `docker-compose.yml`
- ConfigServerLocal service config dosyaları

Örnekler arasında local database password'leri, RabbitMQ credential'ları ve Auth JWT secret fallback değeri bulunur.

Bu nedenle Day 7 secret cleanup hem Compose hem de local Config Server repository'sini kapsamalıdır.

## Zorunlu implementation kontrolleri

### 1. Implementation branch oluştur
Branch şu anda mevcut değildir.

Mevcut `main` üzerinden oluştur:
`day/07-build-data-infra`

Day 7 implementation'ını planning branch üzerinde yapma.

### 2. Day 1–6 davranışını koru
Dependency cleanup yalnızca etkilenen tüm module'ler hâlâ compile/test oluyorsa yapılabilir.

Root dependency kaldırma işlemi kademeli olmalıdır:
1. gerçek owner module'leri belirle
2. dependency'yi ilgili module'e ekle
3. root'tan kaldır
4. verification çalıştır

Tüm global dependency'leri önce kaldırıp daha sonra düzeltme yapma.

### 3. Environment variable isimlerini normalize et
Mevcut config şu tür isimler kullanır:
- `RABBITMQ_USERNAME`
- `RABBITMQ_PASSWORD`

Day 7 `.env.example`, Compose ve Config Server local config tek bir canonical naming scheme kullanmalıdır. Hem `RABBITMQ_USER` hem de `RABBITMQ_USERNAME` oluşturma.

### 4. Config Server secret cleanup
Aşağıdakiler secret literal fallback değerlerini kaybetmelidir:
- Auth DB password
- UserProfile DB password
- Agent temporary DB password
- Buyer temporary DB password
- Seller temporary DB password
- Property temporary DB password
- RabbitMQ password
- Auth JWT secret

Local developer değerleri Vault Day 24'e kadar commit edilmemiş `.env` içinde tutulur.

### 5. SearchService yalnızca foundation seviyesinde kalır
Day 7 şunları oluşturur:
- Gradle module
- bootstrap class
- application.yml
- baseline Config/Eureka/Actuator/tracing dependency'leri

Search domain, controller, document, repository, query handler veya index mapping oluşturulmaz.

### 6. Temporary PostgreSQL kaldırma zamanı
Agent/Buyer/Seller/Property PostgreSQL **Compose service'lerini**, yalnızca replacement datastore container'ları tanımlandıktan ve Compose doğrulandıktan sonra kaldır.

Day 7'de JPA/PostgreSQL kod dependency'lerini kaldırma. Bunlar Day 8–11'de değiştirilir.

### 7. Ağır local infrastructure
Couchbase + Cassandra + Elasticsearch yüksek kaynak tüketebilir.

Compose profiles veya dokümante edilmiş selective startup kullan. Day 7 final gate, her biri ayrı ayrı sağlıklı şekilde doğrulanmışsa bütün ağır datastore'ların aynı anda sürekli çalışmasını gerektirmez.

### 8. Version seçimi
Implementation commit'inden önce seçilen her container image'ı ve yeni Testcontainers dependency'lerini gerçek proje stack'iyle doğrula.

Explicit version kullan. `latest`, wildcard veya dynamic version kullanma.

## Önerilen kesin execution sırası

1. `main` üzerinden `day/07-build-data-infra` oluştur
2. dependency ownership matrix
3. datastore dependency alias'ları
4. güvenli root dependency cleanup
5. SearchService foundation
6. secret/config hygiene + `.env.example`
7. MySQL
8. MongoDB
9. Redis
10. Elasticsearch
11. Couchbase
12. Cassandra
13. temporary service PostgreSQL Compose entry'lerini kaldır
14. selective profiles/resource strategy
15. Compose validation
16. Gradle regression
17. documentation

Bu sıra, daha ağır Couchbase/Cassandra startup çalışmalarından önce daha ucuz/kolay infrastructure kontrollerini tamamlar.

## Day 7 readiness checklist

- [x] Day 7 scope business implementation'dan ayrıştırıldı
- [x] exact file-level plan mevcut
- [x] mevcut main beklenen Day 7 öncesi state ile eşleşiyor
- [x] SearchService yokluğu doğrulandı
- [x] temporary PostgreSQL service'leri doğrulandı
- [x] secret-literal sorunu belirlendi
- [x] root dependency kapsam fazlalığı belirlendi
- [x] `.gitignore` env policy zaten doğru
- [ ] implementation branch oluşturuldu
- [ ] exact container version'ları doğrulandı
- [ ] dependency ownership matrix uygulandı
- [ ] secret env isimleri normalize edildi

## Karar

Implementation öncesinde yeni bir architecture-planning turuna ihtiyaç yoktur.

Bir sonraki adım, `main` üzerinden `day/07-build-data-infra` oluşturmak ve Day 7'yi küçük commit'lerle task-by-task uygulamaktır.
