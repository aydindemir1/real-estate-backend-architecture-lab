# Architecture Decision Records

ADR documents are intentionally short. Each record uses:

- Status
- Context
- Decision
- Rationale
- Alternatives considered
- Consequences
- When to revisit

Initial decisions:

1. Database per service
2. Polyglot persistence
3. SellerService on Cassandra
4. SearchService as Elasticsearch projection
5. RabbitMQ vs Kafka responsibilities
6. Eureka vs Consul vs ZooKeeper comparison

Additional ADRs will be added only when a decision has meaningful trade-offs.
