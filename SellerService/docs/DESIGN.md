# SellerService Design

## Amaç
Seller profile, listing submission history ve seller activity view'larının ownership'ini taşımak.

## Architecture
Onion Architecture.

## Primary datastore
Cassandra + Spring Data Cassandra.

## Hedef package structure
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

## Önce Access Pattern
- Q1: sellerId ile seller getir
- Q2: bir seller'ın son listing submission kayıtlarını getir
- Q3: seller activity geçmişini zaman sırasıyla getir
- Q4: seller'ın belirli bir aya ait listing submission kayıtlarını getir

## Candidate table'lar
- seller_by_id
- listing_submissions_by_seller
- listing_submissions_by_seller_and_month
- seller_activity_by_seller_and_time

Partition key ve clustering key tasarımları implementation öncesinde kesinleştirilecek.

## Temel kısıt
Cassandra JPA gibi modellenmeyecek. Denormalization bilinçli bir tasarım tercihi olabilir.

## Day 7 dependency değişiklikleri
Spring Data Cassandra kullanılacak; bu module için gereksiz JPA/PostgreSQL dependency'leri kaldırılacak.

## Gerekli infrastructure
Cassandra.

## Planlanan testler
Repository/query testleri, partition key davranışı, ordering testleri ve Cassandra Testcontainers.
