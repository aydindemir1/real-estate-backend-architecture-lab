# SellerService Design

## Purpose
Own seller profile, listing submission history and seller activity views.

## Architecture
Onion Architecture.

## Primary datastore
Cassandra via Spring Data Cassandra.

## Target package structure
```text
com.aydindemir.seller
├── domain
│   ├── model
│   ├── valueobject
│   ├── event
│   └── service
├── application
│   ├── command
│   ├── query
│   ├── service
│   └── port
├── infrastructure
│   ├── cassandra
│   ├── kafka
│   ├── redis
│   └── configuration
└── presentation
    └── rest
```

## Access patterns first
- Q1: find seller by sellerId
- Q2: list recent submissions for a seller
- Q3: list seller activity over time
- Q4: list seller submissions for a month

## Candidate tables
- seller_by_id
- listing_submissions_by_seller
- listing_submissions_by_seller_and_month
- seller_activity_by_seller_and_time

Partition/clustering keys will be finalized before implementation.

## Important constraint
Do not model Cassandra like JPA. Denormalization may be intentional.

## Day 7 dependency changes
Use Spring Data Cassandra and remove unnecessary JPA/PostgreSQL dependencies from this module.

## Required infrastructure
Cassandra.

## Planned tests
Repository/query tests, partition-key behavior, ordering tests and Cassandra Testcontainers.
