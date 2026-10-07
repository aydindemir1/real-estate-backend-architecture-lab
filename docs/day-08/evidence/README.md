# Day 08 — AgentService Doğrulama Kanıtları

Bu klasör, `day/08-agent-mysql-clean` branch'inde Day 8 AgentService çalışmalarının lokal runtime ve CI doğrulama sonuçlarını kayıt altına alır.

## Doğrulanan çalışma zinciri

```text
ConfigServerLocal :8888
        ↓
EurekaServer :8761
        ↓
ApiGatewayService :8080
        ↓
AgentService :9092
        ↓
MySQL :3307
```

## Runtime doğrulamaları

- Config Server, `agent-service/default` konfigürasyonunu başarıyla döndürdü.
- AgentService Config Server'a `localhost:8888` üzerinden bağlandı.
- HikariCP MySQL bağlantısını başarıyla açtı.
- Flyway migration doğrulaması başarılı oldu ve şema güncel bulundu.
- JPA EntityManagerFactory başarıyla başlatıldı.
- AgentService Eureka'ya `UP` olarak kaydoldu.
- API Gateway Eureka'da `UP` olarak görüldü.
- AgentService Tomcat üzerinde `9092` portunda başladı.
- Gateway health endpoint'i `200 OK / UP` döndürdü.
- MySQL `agents` tablosunda oluşturulan Agent kaydı doğrulandı.

## Postman — başarılı senaryolar

| Senaryo | Beklenen | Sonuç |
|---|---:|---:|
| Create Agent | 201 Created | ✅ |
| Get Agent — API Gateway | 200 OK | ✅ |
| Get Agent — Direct AgentService | 200 OK | ✅ |
| Availability → AVAILABLE | 200 OK | ✅ |
| Availability → BUSY | 200 OK | ✅ |
| Availability → OFFLINE | 200 OK | ✅ |

## Postman — hata senaryoları

| Senaryo | Beklenen code/status | Sonuç |
|---|---|---:|
| Duplicate User | 409 / AGENT_ALREADY_EXISTS_FOR_USER | ✅ |
| Duplicate License | 409 / DUPLICATE_LICENSE_NUMBER | ✅ |
| Validation Error | 400 / VALIDATION_ERROR | ✅ |
| Malformed JSON | 400 / MALFORMED_REQUEST_BODY | ✅ |
| Missing Availability | 400 / VALIDATION_ERROR | ✅ |
| Invalid UUID | 400 / TYPE_MISMATCH | ✅ |
| Invalid Availability Enum | 400 / MALFORMED_REQUEST_BODY | ✅ |
| Agent Not Found | 404 / AGENT_NOT_FOUND | ✅ |

## CI

AgentService GitHub Actions doğrulaması başarıyla tamamlandı. Path variable binding düzeltmesini içeren Run #18 de başarılıdır.

## Kanıt dosyaları

Bu doğrulama oturumunda aşağıdaki ekran görüntüleri üretildi:

- `eureka-services-up.png`
- `mysql-agent-record.png`
- `postman-config-server-agent-service-200.png`
- `postman-gateway-health-200.png`
- `postman-create-agent-201.png`
- `postman-get-agent-gateway-200.png`
- `postman-get-agent-direct-200.png`
- `postman-availability-available-200.png`
- `postman-availability-busy-200.png`
- `postman-availability-offline-200.png`
- `postman-duplicate-user-409.png`
- `postman-duplicate-license-409.png`
- `postman-validation-error-400.png`
- `postman-malformed-json-400.png`
- `postman-missing-availability-400.png`
- `postman-invalid-uuid-400.png`
- `postman-invalid-availability-400.png`
- `postman-agent-not-found-404.png`

Ayrıca `agent-service-startup.log` uygulamanın Config Server → MySQL/Flyway/JPA → Eureka → Tomcat başlangıç zincirini kayıt altına alır.

## Ekran görüntüsü SHA-256 manifesti

```text
65fba354543ebeeb59d8f4bd3ffeb5d6091c01fd0098afc0c28d92d3ce4190de  eureka-services-up.png
aa310ae9b83803bbd24e9e73a8e2af5a0935ee42826e5c71bcc9fd7ebaaaa143  mysql-agent-record.png
715306f79ba0c1e49bcbb6c7c6b5018a3f3e18610db9750ca2d24c38ea153c68  postman-config-server-agent-service-200.png
571c7638774469fa2c45e9ad437fb576dc1ea6c43748e120437e7afff36c5ce6  postman-gateway-health-200.png
18b8d8436e9ae1290f5145acad9a1e07b26e3930e0050ebaead3ad11f0593ed0  postman-create-agent-201.png
fe5ac7511ff4c69a852d599d11d93ee2d5391bda0c3d5365a49e79121b11d0a3  postman-get-agent-gateway-200.png
7fcbaa26ee347f5bb3dde7503dd297ec45dcc456b9e7c48b80072cdd3d918d91  postman-get-agent-direct-200.png
945cc636fbaaba5714b76888b5eab2e20db96516046daeed2881af6b082830ea  postman-availability-available-200.png
166cc29713ccef5caeb538cbca1b7d5b0c5cc9ac3ab7996881c6c110a497e18f  postman-availability-busy-200.png
3640471c09f712dbf77b69e2d7cc4a72a25d13a10d8ff00bec0b28c46c1aa273  postman-availability-offline-200.png
b2964cadce11a2204623794504825c74391fe68cb6771dee0b2f376ecdf11dc6  postman-duplicate-user-409.png
590ef1d6bd7255c12953d1585d7ab981c0cfe2b749f7447818ab377989420b1b  postman-duplicate-license-409.png
bd07800b8146eb92823832facf190fcbcddb13952616d518d51930c174d1e337  postman-validation-error-400.png
d944bd8f18b4aab45b0397d0eb7be044adc8427d57c02f2bb18e86fecf82c750  postman-malformed-json-400.png
858ee6c0745d610debaed4104ba2c26dacd98526b7ffc631e0be6f9bf43fe00d  postman-missing-availability-400.png
dcd1b2ad8dfdc9b911d644fd22a65e17295284e8d117770c2a883bc3271c5042  postman-invalid-uuid-400.png
58fd6470eeb3dbb390a4b48d77818855c8ec6b88928d6d672d4f9ba492b52208  postman-invalid-availability-400.png
2c16bf30995297d111eddd3fbfcb0cf9097e4810beb8d2f0ad4eecb14573d8c8  postman-agent-not-found-404.png
e5ac228c3b37a1c16732c75bbf2b297ae7ecbcb4511232f8312ce6f3cefa00e6  agent-service-startup.log
```



## Sonuç

Day 8 AgentService doğrulaması tamamlandı.

- CI: ✅
- Local runtime: ✅
- Config Server: ✅
- Eureka registration: ✅
- API Gateway: ✅
- MySQL persistence: ✅
- Flyway: ✅
- Success scenarios: ✅
- Error scenarios: ✅

Durum: **Completed / Verified**
