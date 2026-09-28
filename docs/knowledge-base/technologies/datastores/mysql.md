# MySQL

**Category:** Technology  
**Introduced:** Day 7  
**Project status:** Implemented  
**Scope:** AgentService relational datastore

## Nedir?

MySQL relational data model, SQL ve transactional storage engine desteği sunan RDBMS'tir.

Bu projede AgentService'in primary datastore'udur.

## InnoDB

InnoDB:
- ACID transactions
- row-level locking
- MVCC
- clustered primary index
- redo/undo logging

sağlar.

## Day 7 -> Day 8 progression

Day 7:
- MySQL 8.4.x local container
- Agent-specific volume
- environment-driven credentials
- healthcheck

Day 8:
- AgentService MySQL JDBC
- Flyway V1 migration
- Spring Data JPA/Hibernate persistence adapter
- unique `user_id`
- unique `license_number`
- `@Version` optimistic locking
- MySQL Testcontainers integration coverage

## Schema characteristics

- UUID -> CHAR(36)
- status/availability -> VARCHAR
- timestamps -> TIMESTAMP(6)
- no MySQL native ENUM
- no cross-service FK
- no speculative secondary indexes

## MVCC vs optimistic locking

MVCC database transaction visibility/isolation concern'idir.

`@Version` optimistic locking ise stale application update'lerini version check ile reddeder.

## Production considerations

- buffer pool
- redo/undo
- slow query log
- EXPLAIN
- index design
- isolation/deadlocks
- replication
- backup/restore
- charset/collation
