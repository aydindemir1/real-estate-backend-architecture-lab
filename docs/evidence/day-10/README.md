# Day 10 — SellerService Runtime Evidence

Bu klasör, `day/10-seller-cassandra-onion` branch'inde Day 10 SellerService çalışmalarının lokal runtime doğrulama kayıtlarını tutar.

## Doğrulanan runtime zinciri

```text
ConfigServerLocal :8888
        ↓
SellerService :9095
        ↓
Cassandra :9042
        ↓
EurekaServer :8761
        ↓
Postman acceptance
```

## Lokal ortam

- Java: `21.0.8`
- Spring Boot: `4.1.1`
- SellerService: `9095`
- ConfigServerLocal: `8888`
- Eureka: `8761`
- Cassandra: `localhost:9042`
- Cassandra image: `cassandra:5.0.9`
- keyspace: `seller_service`
- Cassandra Admin: `localhost:8002`

## Cassandra schema

Version-controlled schema:

`SellerService/src/main/resources/cassandra/schema/V1__seller_tables.cql`

Doğrulanan tablolar:

- `seller_by_id`
- `listing_submissions_by_seller_and_month`

Listing partition key:

```text
(seller_id, year_month)
```

Clustering:

```text
created_at DESC, submission_id ASC
```

`ALLOW FILTERING` kullanılmamaktadır.

## SellerService startup

Başarılı runtime oturumunda:

- config `http://localhost:8888` üzerinden alındı
- iki Cassandra repository bulundu
- Cassandra session açıldı
- Tomcat `9095` portunda başladı
- `SELLER-SERVICE` Eureka'ya `UP` olarak register edildi
- Eureka registration status `204` oldu

Ham log:

- `seller-service-startup-success.log`

## Postman success acceptance

Collection:

- `docs/collections/day-10/Day-10-SellerService.postman_collection.json`

Başarılı senaryolar:

| # | Senaryo | Sonuç |
|---|---|---:|
| 1 | POST Create Seller | 201 |
| 2 | GET Seller | 200 |
| 3 | POST Create Listing Submission | 201 |
| 4 | GET List Seller Submissions | 200 |
| 5 | POST Submit Listing | 200 |
| 6 | GET List After Submit | 200 |

Create/submit akışında `CREATED -> SUBMITTED` state transition runtime'da doğrulandı.

## Postman error acceptance

| # | Senaryo | Sonuç / code |
|---|---|---|
| 1 | Missing Seller | 404 / SELLER_NOT_FOUND |
| 2 | Seller Validation | 400 / VALIDATION_ERROR |
| 3 | Listing for Missing Seller | 404 / SELLER_NOT_FOUND |
| 4 | Listing Validation | 400 / VALIDATION_ERROR |
| 5 | Invalid pageSize | 400 / VALIDATION_ERROR |
| 6 | Invalid yearMonth | 400 / VALIDATION_ERROR |
| 7 | Submit path/body mismatch | 400 / BAD_REQUEST |
| 8 | Missing submission | 404 / LISTING_SUBMISSION_NOT_FOUND |
| 9 | Submit same listing again | 409 / INVALID_LISTING_SUBMISSION_STATE |
| 10 | Malformed JSON | 400 / MALFORMED_REQUEST_BODY |

## Cassandra persistence doğrulaması

`seller_by_id` tablosunda oluşturulan ACTIVE seller Cassandra Admin üzerinden görüldü.

Listing kaydı ayrıca doğrudan partition-key CQL sorgusuyla doğrulandı:

```sql
SELECT *
FROM seller_service.listing_submissions_by_seller_and_month
WHERE seller_id = 1359ae82-5f8e-4e73-9b75-3d2cff518a51
  AND year_month = '2026-10';
```

Sonuç:

- 1 row
- submission: `1c0f567f-c36a-4309-ad1d-d535a42adbe0`
- title: `Gebze Merkez Daire`
- status create aşamasında: `CREATED`

Daha sonra REST submit ve tekrar list çağrısı ile persisted durum `SUBMITTED` olarak doğrulandı.

## Runtime sırasında bulunan defect'ler

### Cassandra credentials

Auth kullanmayan lokal Cassandra için boş username/password property'leri startup'ı bozuyordu. Property'ler kaldırıldı.

### Eksik keyspace

`Invalid keyspace seller_service` hatası version-controlled CQL schema'nın lokal Cassandra'ya bootstrap edilmesiyle çözüldü.

### REST parameter binding

`GET /sellers/{sellerId}` ilk lokal acceptance sırasında:

```text
Name for argument of type [java.util.UUID] not specified...
```

hatası verdi.

Controller'lardaki path/query parametreleri explicit isimlendirildi:

- `@PathVariable("sellerId")`
- `@PathVariable("submissionId")`
- `@RequestParam("yearMonth")`
- explicit `pageSize` / `pageState`

Aynı GET çağrısı fix sonrası `200 OK` ve Postman `3/3` ile geçti.

## Cassandra Admin notu

Cassandra Admin:
- `seller_service` keyspace'i ve iki tabloyu gösterdi
- `seller_by_id` gerçek satırını doğru gösterdi

Ancak `listing_submissions_by_seller_and_month` için UI stale/boş görünüm üretti. Veri yok varsayımı yapılmadı; aynı kayıt doğrudan `cqlsh` partition query ile kesin olarak doğrulandı.

Bu davranış application persistence hatası değil, Cassandra Admin UI limitation/compatibility observation olarak kaydedildi.

## Verification matrix

| Kontrol | Durum |
|---|---:|
| GitHub CI | ✅ PASS |
| Config Server | ✅ PASS |
| Cassandra health | ✅ PASS |
| Schema bootstrap | ✅ PASS |
| SellerService startup | ✅ PASS |
| Eureka registration | ✅ PASS |
| Seller persistence | ✅ PASS |
| Listing persistence | ✅ PASS |
| 6 success Postman scenario | ✅ PASS |
| 10 error Postman scenario | ✅ PASS |
| State transition | ✅ PASS |
| cqlsh partition query | ✅ PASS |
| Original evidence package | ✅ PASS |
| Original PNG GitHub binary sync | ✅ PASS |

## Evidence packaging

Orijinal PNG'ler crop/resize/re-encode yapılmadan ayrı evidence paketinde korunmuştur.

GitHub connector üzerinden ilk binary denemesinde bozulma görüldüğü için orijinal PNG'ler lokal Git üzerinden repository'ye commit edilmiştir.

Day 10 implementation/runtime kabulü ve original binary evidence sync doğrulanmıştır. Canonical planning-branch sync Day Close Protocol kapsamında tamamlanmıştır.


## Orijinal ekran görüntüleri

### Altyapı / Cassandra
- [01 - Eureka SELLER-SERVICE UP](png/01-eureka-seller-service-up.png)
- [02 - Cassandra Admin seller_service schema](png/02-cassandra-admin-seller-service-schema.png)
- [03 - seller_by_id başlangıç görünümü](png/03-cassandra-admin-seller-by-id-empty.png)
- [04 - listing submissions başlangıç görünümü](png/04-cassandra-admin-listing-submissions-empty.png)
- [08 - seller_by_id persisted row](png/08-cassandra-admin-seller-persisted.png)
- [11 - Cassandra Admin listing stale-view observation](png/11-cassandra-admin-listing-stale-view-observation.png)

### Success acceptance
- [05 - Create Seller 201](png/05-postman-create-seller-201.png)
- [07 - Get Seller 200](png/07-postman-get-seller-200.png)
- [09 - Create Listing Submission 201](png/09-postman-create-listing-submission-201.png)
- [10 - List Seller Submissions 200](png/10-postman-list-seller-submissions-200.png)
- [12 - Submit Listing 200](png/12-postman-submit-listing-200.png)
- [13 - List After Submit 200](png/13-postman-list-after-submit-200.png)

### Runtime defect evidence
- [06 - GET Seller parameter binding defect 400](png/06-postman-get-seller-runtime-defect-400.png)

### Error acceptance
- [14 - Missing Seller 404](png/14-postman-get-missing-seller-404.png)
- [15 - Seller Validation 400](png/15-postman-seller-validation-error-400.png)
- [16 - Submit Same Listing Again 409](png/16-postman-submit-same-listing-again-409.png)
- [17 - Listing For Missing Seller 404](png/17-postman-listing-for-missing-seller-404.png)
- [18 - Listing Validation 400](png/18-postman-listing-validation-error-400.png)
- [19 - Invalid Page Size 400](png/19-postman-invalid-page-size-400.png)
- [20 - Invalid Year Month 400](png/20-postman-invalid-year-month-400.png)
- [21 - Submit Path/Body Mismatch 400](png/21-postman-submit-path-body-mismatch-400.png)
- [22 - Submit Missing Submission 404](png/22-postman-submit-missing-submission-404.png)
- [23 - Malformed JSON 400](png/23-postman-malformed-json-400.png)

## Ek ham kanıtlar
- `logs/cassandra-listing-persistence-cqlsh.txt`
- `logs/seller-service-startup-success-original.md`
- `seller-service-startup-success.log`
