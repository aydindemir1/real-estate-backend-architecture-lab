# Day 09 - BuyerService Lokal Runtime Kanıtları

Bu klasör, **BuyerService - Couchbase + Hexagonal Architecture** çalışmasının lokal runtime kabul testlerine ait gerçek kanıtları içerir.

## Ortam

- BuyerService: `http://localhost:9093`
- Config Server: `http://localhost:8888`
- Eureka Server: `http://localhost:8761`
- Couchbase Web Console: `http://localhost:8091`
- Couchbase bucket: `buyer`
- Couchbase scope: `buyer_service`
- Couchbase collection: `preferences`
- Lokal çalışma şekli: STS / Spring Boot App
- Couchbase sürümü: Community 8.0.2

## Orijinal ekran görüntüleri

Aşağıdaki kanıtlar, test sırasında paylaşılan ekran görüntülerinin **orijinal çözünürlükleri korunarak** ayrı ayrı arşivlenmiş halleridir.

### Couchbase ve altyapı

- [01 - Postman öncesi boş preferences collection](png/01-couchbase-preferences-baslangic-bos.png)
- [04 - İlk başarılı PUT sonrası preferences collection: 1 item](png/04-couchbase-preferences-1-item.png)
- [14 - Eureka üzerinde BUYER-SERVICE UP](png/14-eureka-buyer-service-up.png)
- [15 - buyer bucket durumu: 1 item, CouchStore](png/15-couchbase-buyer-bucket.png)

### Başarılı Postman senaryoları

- [03 - PUT preferences - 200 OK](png/03-put-preferences-200.png)
- [05 - GET preferences - 200 OK](png/05-get-preferences-200.png)
- [06 - POST saved search - 201 Created](png/06-post-saved-search-201.png)
- [07 - POST saved search - response detayı](png/07-post-saved-search-201-detay.png)
- [08 - Saved search sonrası GET - 200 OK](png/08-get-after-saved-search-200.png)

### Hata senaryoları

- [09 - Olmayan preferences - 404](png/09-get-missing-preferences-404.png)
- [10 - Bean Validation hatası - 400](png/10-put-validation-error-400.png)
- [11 - Semantic range ihlali - 422](png/11-put-semantic-range-error-422.png)
- [12 - Geçersiz UUID - 400](png/12-get-invalid-uuid-400.png)
- [13 - Bozuk JSON - 400](png/13-put-malformed-json-400.png)

### Runtime sırasında bulunan hata

- [02 - İlk PUT denemesinde PathVariable runtime hatası](png/02-ilk-put-path-variable-hatasi.png)

## Startup log kanıtı

Kaydedilen BuyerService startup logu aşağıdaki noktaları doğrular:

- Config Server konfigürasyonu `8888` portundan başarıyla alınmıştır.
- Couchbase `buyer` bucket başarıyla açılmıştır.
- BuyerService `9093` portunda başlamıştır.
- BuyerService Eureka'ya `UP` olarak kayıt olmuştur.

Ham log:

- `buyer-service-startup-success.log`

## Couchbase persistence kanıtı

Postman testlerinden önce:

- `buyer_service.preferences` collection mevcuttu.
- item sayısı `0` idi.

İlk başarılı preferences PUT işleminden sonra:

- item sayısı `0 -> 1` oldu.
- sonraki GET isteği aynı preferences verisini Couchbase üzerinden geri okudu.
- saved search eklendi.
- sonraki GET isteğinde saved search kalıcı veriden geri döndü.

Bu sonuç, yalnızca API response üretildiğini değil, gerçek Couchbase write/read persistence döngüsünün çalıştığını doğrular.

## Eureka kanıtı

Lokal runtime sırasında Eureka Dashboard üzerinde aşağıdaki servisler `UP` olarak görüntülendi:

- `API-GATEWAY-SERVICE` - port `8080`
- `BUYER-SERVICE` - port `9093`

Lokal geliştirme ortamında az sayıda instance bulunduğu için Eureka self-preservation/renewal uyarısı görüntülenmiştir. Bu uyarı, ekranda görülen `UP` registration durumunu geçersiz kılmaz.

## Postman kabul testleri

Collection:

- `Day-09-BuyerService.postman_collection.json`

### Başarılı senaryolar

| Senaryo | Beklenen HTTP | Sonuç |
|---|---:|---:|
| PUT `/buyers/{buyerId}/preferences` | 200 | PASS - 3/3 test |
| GET `/buyers/{buyerId}/preferences` | 200 | PASS - 2/2 test |
| POST `/buyers/{buyerId}/saved-searches` | 201 | PASS - 3/3 test |
| Saved search sonrası GET preferences | 200 | PASS - 2/2 test |

### Hata senaryoları

| Senaryo | Beklenen HTTP | Error code | Sonuç |
|---|---:|---|---:|
| Preferences bulunamadı | 404 | `BUYER_PREFERENCES_NOT_FOUND` | PASS - 2/2 |
| Bean Validation hatası | 400 | `VALIDATION_ERROR` | PASS - 3/3 |
| Semantic range ihlali | 422 | `INVALID_BUYER_PREFERENCES` | PASS - 2/2 |
| Geçersiz UUID path variable | 400 | `INVALID_REQUEST` | PASS - 2/2 |
| Bozuk JSON | 400 | `INVALID_REQUEST` | PASS - 2/2 |

## Kabul testi sırasında bulunan runtime problemi

İlk PUT isteği sırasında aşağıdaki hata görüldü:

```text
Name for argument of type [java.util.UUID] not specified,
and parameter name information not available via reflection.
```

Controller, path variable adının implicit parameter-name discovery ile çözülmesine güveniyordu.

Sorun tüm ilgili endpoint'lerde path variable adının açıkça belirtilmesiyle giderildi:

```java
@PathVariable("buyerId") UUID buyerId
```

Düzeltmeden sonra tüm başarılı ve hata senaryoları geçti.

## Day 09 runtime kabul durumu

- Config Server entegrasyonu: **PASS**
- Couchbase authentication: **PASS**
- Couchbase bucket açılışı: **PASS**
- Couchbase persistence write/read: **PASS**
- Eureka registration: **PASS**
- REST başarılı senaryolar: **PASS**
- REST validation/error senaryoları: **PASS**
- Correlation/trace error envelope: **gözlemlendi**
