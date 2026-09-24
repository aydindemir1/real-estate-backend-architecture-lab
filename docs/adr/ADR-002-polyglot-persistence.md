# ADR-002 — Polyglot Persistence

**Status:** Accepted

## Decision
Her datastore farklı bir backend problemini öğretmek için kullanılacaktır:

- MySQL → AgentService
- Couchbase → BuyerService
- Cassandra → SellerService
- MongoDB → PropertyService
- Elasticsearch → SearchService
- Redis → cross-cutting infrastructure

## Rationale
Proje education/portfolio laboratory niteliğindedir; ancak teknolojiler checklist doldurmak için değil gerçek sorumluluklara karşılık gelecek şekilde kullanılmalıdır.

## Consequences
Local infrastructure ve testing daha karmaşık hale gelir. Bu nedenle Testcontainers ve açık service boundary'leri roadmap'in zorunlu parçalarıdır.
