# ADR-003 — SellerService için Cassandra

**Status:** Accepted

## Context
SellerService, wide-column ve query-driven distributed modeling öğrenmek için seçilen service'tir.

## Decision
Spring Data Cassandra kullanılacaktır. Tablolar seller lookup, listing submission history ve time-ordered seller activity gibi access pattern'lere göre tasarlanacaktır.

## Rationale
Cassandra; partition key, clustering key, denormalization ve distributed data design konularını öğretir. Bir başka relational database eklemek aynı öğrenme değerini sağlamaz.

## Alternatives Considered
MySQL, PostgreSQL, MongoDB.

## Consequences
Join ve JPA-style aggregate navigation kullanılmayacaktır. Denormalized data bilinçli olarak tutulabilir. Classic relational Transactional Outbox varsayımları Cassandra'ya doğrudan taşınmayacaktır.
