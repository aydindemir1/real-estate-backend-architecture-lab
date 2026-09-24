# ADR-006 — Eureka, Consul and ZooKeeper Scope

**Status:** Accepted

## Decision
Keep Eureka as the implemented service registry for the backend phase. Compare Consul and ZooKeeper in documentation; do not add all three to the running architecture.

## Comparison topics
- service discovery model
- health checking
- configuration / KV capabilities
- coordination semantics
- operational complexity
- relationship to Kubernetes-native discovery

## Rationale
Implementing three registries would add operational noise without proportional learning value.

## When to revisit
During the Kubernetes/platform phase, compare Eureka-based discovery with Kubernetes-native Service/DNS discovery and Spring Cloud Kubernetes.
