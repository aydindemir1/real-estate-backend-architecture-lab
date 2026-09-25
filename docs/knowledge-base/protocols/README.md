# Protocols & Formats

Servislerin ve bileşenlerin iletişiminde kullanılan protocol, architectural style, data format ve token standard'ları.

## Day 1–7

- [HTTP](http.md) — Protocol
- [REST](rest.md) — Architectural Style
- [JSON](json.md) — Data Format
- [AMQP](amqp.md) — Messaging Protocol / Model
- [JWT](jwt.md) — Token Format / Standard

## Önemli sınıflandırma

Bu başlıkların hepsi "protocol" değildir.

- HTTP -> protocol
- REST -> architectural style
- JSON -> data format
- AMQP -> messaging protocol/model family
- JWT -> token format/standard

Bu ayrım Knowledge Base boyunca korunur.

## Cross-reference

Concrete teknolojiler:
- REST/HTTP implementation -> Spring MVC
- internal HTTP client -> Spring Cloud OpenFeign
- AMQP broker -> RabbitMQ
- AMQP Spring integration -> Spring AMQP
- JWT Java implementation -> Auth0 java-jwt
