# ADR-006 — Eureka, Consul ve ZooKeeper Kapsamı

**Status:** Accepted

## Decision
Backend fazında implemented service registry olarak Eureka korunacaktır. Consul ve ZooKeeper dokümantasyon üzerinden karşılaştırılacak; üçünü birden runtime architecture'a eklemeyeceğiz.

## Karşılaştırma başlıkları
- service discovery model
- health checking
- configuration / KV capabilities
- coordination semantics
- operational complexity
- Kubernetes-native discovery ile ilişki

## Rationale
Üç registry'yi birlikte implement etmek learning value'dan fazla operational noise üretir.

## When to Revisit
Kubernetes/platform fazında Eureka-based discovery ile Kubernetes Service/DNS ve Spring Cloud Kubernetes karşılaştırılacaktır.
