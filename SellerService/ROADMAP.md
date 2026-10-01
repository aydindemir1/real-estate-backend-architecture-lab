# SellerService Roadmap

## Day 10 — Completed implementation / verified runtime

Implemented:
- Cassandra
- Onion Architecture
- Seller Aggregate
- ListingSubmission state model
- query-first physical data model
- REST API
- Testcontainers
- ArchUnit
- Config Client
- Eureka Client
- Postman acceptance

Verified:
- seller create/get
- listing create/list/submit
- CREATED → SUBMITTED
- duplicate submit → 409
- Cassandra persistence
- monthly partition query
- error semantics

## Deferred

Future milestone:
- RabbitMQ SubmitPropertyListingCommand
- reliable dispatch / publisher confirms / outbox-equivalent strategy
- downstream PropertyService integration
- offer projections
- Saga/process coordination
