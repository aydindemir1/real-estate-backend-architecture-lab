# Messaging Topology

## Temel ayrım

Bu proje messaging teknolojilerini birbirinin kopyası olarak kullanmaz.

- RabbitMQ -> Command / Work Queue ağırlıklı kullanım
- Kafka -> Domain Event Streaming, CQRS Projection ve Saga event akışları

## RabbitMQ

### Mevcut
AuthService -> UserProfileService async profile creation flow korunur.

### Yeni target flow
SellerService -> PropertyService listing submission command.

Exchange:
`real-estate.listing.commands`

Type:
`direct`

Queue:
`property.listing.submit`

Routing Key:
`property.listing.submit`

Message:
`SubmitPropertyListingCommand`

Temel envelope alanları:
- commandId
- submissionId
- sellerId
- propertyDraft
- createdAt
- correlationId

### Dead Letter
DLX:
`real-estate.listing.dlx`

DLQ:
`property.listing.submit.dlq`

Dead routing key:
`property.listing.submit.dead`

### Reliability notu
SellerService Cassandra state write ile RabbitMQ publish arasındaki reliable publication stratejisi Day 11 öncesi ayrıca finalize edilecektir. Day 7 yalnızca datastore/architecture foundation kapsamındadır.

## Kafka

### Topic yaklaşımı
- `property.events`
- `offer.events`

### property.events
Message Key: `propertyId`

Initial event set:
- PropertyCreated
- PropertyPublished
- PropertyUpdated
- PropertyPriceChanged
- PropertyHeld
- PropertyHoldReleased
- PropertyReserved
- PropertyWithdrawn
- PropertySold

### offer.events
Message Key: `offerId`

Initial event set:
- OfferRequested
- SellerAccepted
- SellerRejected
- OfferExpired

`OfferAccepted` ve `OfferRejected` başlangıç event setinde yoktur. Gerçek downstream consumer ihtiyacı oluşursa eklenir.

### Event envelope
- eventId
- eventType
- aggregateId
- aggregateType
- occurredAt
- correlationId
- causationId
- schemaVersion
- payload

## Öğrenilecek konular

- Topic / Partition / Message Key
- Consumer Group / Offset / Ordering / Replay / Retention
- At-least-once delivery
- Duplicate Event
- Retry Topic / DLT
- Spring Cloud Stream
- Spring Cloud Function
- Idempotent Consumer

## Önemli kural

Aynı business flow yalnızca teknoloji göstermek için hem RabbitMQ hem Kafka ile tekrar edilmez. Her broker'ın ayrı semantic rolü vardır.
