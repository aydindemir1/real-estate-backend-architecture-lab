# ADR-001 — Database per Service

**Status:** Accepted

## Context
Laboratory projesinde service ownership sınırlarının net olması ve persistence kararlarının bağımsız verilebilmesi istenmektedir. Day 1–6 baseline korunmalıdır.

## Decision
Yeni business service'ler kendi primary datastore'larının sahibi olacaktır. AuthService ve UserProfileService mevcut stabil yapı oldukları için PostgreSQL üzerinde kalır.

Hiçbir service başka bir service'in datastore'una doğrudan yazmaz.

## Rationale
- ownership sınırlarını öğretir
- Polyglot Persistence yaklaşımını görünür kılar
- independent evolution sağlar
- Eventual Consistency gibi distributed systems problemlerini gerçek hale getirir

## Alternatives Considered
- tek shared relational database
- tek database içinde schema-per-service

## Consequences
Cross-service join yapılmaz. Integration API veya event üzerinden gerçekleşir. Distributed consistency açık biçimde ele alınmalıdır.
