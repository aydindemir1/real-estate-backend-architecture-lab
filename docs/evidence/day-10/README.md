# Day 10 — SellerService Runtime Evidence

Bu klasör, `day/10-seller-cassandra-onion` branch'inde Day 10 SellerService çalışmalarının lokal runtime doğrulama kanıtlarını kayıt altına alır.

> Bu kayıt yalnızca şu ana kadar gerçekten doğrulanan runtime adımlarını içerir. Eureka ekran görüntüsü kanıtı eklenmiştir; Postman kabul testleri ve Cassandra ekran görüntüsü henüz tamamlanmamıştır.

## Doğrulanan çalışma zinciri

```text
ConfigServerLocal :8888
        ↓
SellerService :9095
        ↓
Cassandra :9042
        ↓
EurekaServer :8761
```

## Lokal ortam

- Spring Boot: `4.1.1`
- Java: `21.0.8`
- SellerService portu: `9095`
- ConfigServerLocal: `http://localhost:8888`
- Cassandra: `localhost:9042`
- Cassandra image: `cassandra:5.0.9`
- Cassandra keyspace: `seller_service`
- Eureka: `http://localhost:8761/eureka/`

## Cassandra bootstrap doğrulaması

Lokal Cassandra container aşağıdaki komutla doğrulandı:

```cmd
docker compose --profile seller up -d seller-cassandra
docker compose ps seller-cassandra
```

Container durumu:

```text
real-estate-seller-cassandra   cassandra:5.0.9   Up (...) (healthy)   0.0.0.0:9042->9042/tcp
```

İlk kontrolde yalnızca Cassandra system keyspace'leri bulunuyordu ve `seller_service` henüz mevcut değildi.

Version-controlled Day 10 CQL schema aşağıdaki komutla uygulandı:

```cmd
type SellerService\src\main\resources\cassandra\schema\V1__seller_tables.cql | docker compose exec -T seller-cassandra cqlsh
```

Ardından:

```cmd
docker compose exec seller-cassandra cqlsh -e "DESCRIBE KEYSPACE seller_service;"
```

ile aşağıdaki fiziksel model doğrulandı:

- keyspace: `seller_service`
- table: `seller_by_id`
- table: `listing_submissions_by_seller_and_month`
- composite partition key: `(seller_id, year_month)`
- clustering columns: `created_at, submission_id`
- clustering order: `created_at DESC, submission_id ASC`

## SellerService startup doğrulaması

Başarılı startup oturumunda aşağıdaki noktalar doğrulandı:

- `SellerServiceApplication` Java 21 ile başladı.
- Config Server konfigürasyonu `http://localhost:8888` üzerinden alındı.
- `seller-service/default` environment başarıyla bulundu.
- Spring Data Cassandra iki repository interface'i buldu.
- Cassandra driver `localhost:9042` contact point'ine bağlandı.
- SellerService Eureka'dan registry bilgisini `200` ile aldı.
- `SELLER-SERVICE` Eureka'ya `UP` olarak register edildi.
- Eureka registration sonucu `204` oldu.
- Tomcat `9095` portunda başladı.
- `SellerServiceApplication` başarılı şekilde startup tamamladı.

Ham başarı logu:

- `seller-service-startup-success.log`

## Eureka screenshot kanıtı

Lokal Eureka Dashboard üzerinde aşağıdaki servisler `UP` olarak doğrulandı:

- `API-GATEWAY-SERVICE` — port `8080`
- `SELLER-SERVICE` — port `9095`

Orijinal PNG kanıtı kırpılmadan, yeniden boyutlandırılmadan ve yeniden encode edilmeden saklanmıştır:

- [01-eureka-seller-service-up.png](png/01-eureka-seller-service-up.png)

Dashboard üzerinde görülen Eureka renewal/self-preservation uyarısı lokal geliştirme ortamındaki düşük instance/renewal sayısıyla ilişkilidir; ekrandaki `SELLER-SERVICE = UP` registration durumunu geçersiz kılmaz.

## Runtime sırasında bulunan ve çözülen problemler

### 1. Boş Cassandra credential ayarları

İlk runtime denemesinde Config Server üzerinden aşağıdaki boş credential property'leri gönderiliyordu:

```yaml
username: ${SELLER_CASSANDRA_USERNAME:}
password: ${SELLER_CASSANDRA_PASSWORD:}
```

Spring Boot / Cassandra driver boş username değerini geçerli credential konfigürasyonu olarak yorumladı ve startup şu hata ile durdu:

```text
username cannot be empty
```

Lokal Cassandra authentication kullanmadığı için username/password property'leri hem ConfigServerLocal hem ConfigServerRemote SellerService konfigürasyonundan tamamen kaldırıldı.

### 2. Eksik Cassandra keyspace

Credential problemi çözüldükten sonra SellerService Cassandra'ya erişebildi ancak:

```text
Invalid keyspace seller_service
```

hatası alındı.

Sebep lokal Cassandra volume'ünde Day 10 CQL schema'nın henüz uygulanmamış olmasıydı. `V1__seller_tables.cql` manuel bootstrap edilerek keyspace ve tablolar oluşturuldu. Sonraki startup başarılı oldu.

## Gözlemlenen warning

Cassandra driver, `localhost` adresinin hem IPv4 hem IPv6'ya resolve olması nedeniyle local datacenter ile ilgili bir warning üretti. Bu warning startup'ı engellemedi; uygulama Cassandra bağlantısını kurdu ve başarıyla ayağa kalktı.

## Mevcut doğrulama durumu

| Kontrol | Durum |
|---|---:|
| GitHub CI | ✅ PASS |
| ConfigServerLocal → SellerService config | ✅ PASS |
| Cassandra container health | ✅ PASS |
| Cassandra schema bootstrap | ✅ PASS |
| `seller_service` keyspace | ✅ PASS |
| Cassandra repository initialization | ✅ PASS |
| SellerService port 9095 startup | ✅ PASS |
| Eureka registry fetch | ✅ PASS |
| Eureka `SELLER-SERVICE = UP` registration | ✅ PASS |
| Postman success scenarios | ⏳ Pending |
| Postman error scenarios | ⏳ Pending |
| Eureka screenshot evidence | ✅ PASS |
| Cassandra screenshot evidence | ⏳ Pending |

## Day 10 runtime durumu

SellerService'in temel lokal runtime zinciri başarıyla doğrulanmıştır.

Day 10 henüz tamamen kapatılmamıştır. Postman kabul testleri, ekran görüntüsü evidence'ları ve final dokümantasyon senkronizasyonu tamamlandıktan sonra Day 10 `Completed / Verified` olarak işaretlenecektir.
