# JSON

**Category:** Data Format  
**Introduced:** Day 1  
**Project status:** Implemented / Verified  
**Scope:** Primary HTTP and early messaging serialization format

## 1. Nedir?

JSON (JavaScript Object Notation), structured data'yı text tabanlı olarak temsil eden data interchange format'ıdır.

## 2. Temel veri türleri

JSON:
- object
- array
- string
- number
- boolean
- null

tiplerini destekler.

## 3. Örnek

```json
{
  "id": 42,
  "email": "user@example.com",
  "roles": ["BUYER"],
  "active": true
}
```

## 4. Java ile mapping

Spring Boot/Spring MVC ekosisteminde JSON serialization/deserialization çoğunlukla Jackson üzerinden yapılır.

```text
Java DTO
  |
  v
Jackson
  |
  v
JSON
```

## 5. JSON schema değildir

JSON yalnız data format'tır.

Field'ların:
- zorunluluğu,
- type contract'ı,
- semantic validation'ı

ayrı contract/schema mekanizması gerektirir.

## 6. HTTP ile ilişkisi

Yaygın content type:

```text
application/json
```

Client ve server Content-Type/Accept header'larıyla representation seçebilir.

## 7. Messaging ile ilişkisi

Day 6 RabbitMQ flow'unda Java message object'leri JSON'a serialize edilerek taşınmıştır.

Bu, message schema governance ihtiyacını ortadan kaldırmaz.

## 8. Avantajları

- human-readable
- language-neutral
- broad tooling
- web ecosystem standardı
- flexible object model

## 9. Trade-off'ları

- verbose olabilir
- schema enforcement built-in değildir
- number/date semantics dikkat ister
- binary data için verimsiz olabilir
- evolution discipline gerekir

## 10. Serialization boundary

Domain object'ini doğrudan dış contract olarak serialize etmek coupling oluşturabilir.

Tercih:
- API DTO
- event DTO
- explicit schema/version

## 11. Security considerations

Untrusted JSON:
- validate edilmeli
- payload size sınırlandırılmalı
- unknown field policy düşünülmeli
- polymorphic deserialization dikkatle yönetilmelidir.

## 12. Bu projede nasıl kullanılıyor?

REST API request/response payload'ları ve Day 6 RabbitMQ message payload'larında JSON kullanılır.

## 13. Alternatifleri

- Protocol Buffers
- Avro
- MessagePack
- CBOR
- XML

## 14. İleri öğrenme konuları

- JSON Schema
- canonical JSON
- streaming parsers
- schema evolution
- serialization security
