# Elasticsearch

**Category:** Technology  
**Introduced:** Day 7  
**Project status:** Infrastructure Ready  
**Scope:** Target SearchService query datastore and derived projection

## 1. Nedir?

Elasticsearch, Lucene tabanlı distributed search ve analytics engine'dir.

Full-text search, relevance scoring, filtering, aggregation ve near-real-time indexing için kullanılır.

## 2. Veri modeli

Temel kavramlar:
- cluster
- node
- index
- shard
- replica
- document
- field
- mapping

## 3. Lucene ilişkisi

Elasticsearch distributed API/runtime katmanını sağlar.

Lucene:
- inverted index
- segment
- analyzer
- scoring

gibi core search engine mekanizmalarını sağlar.

## 4. Inverted index

Full-text search için term -> document mapping yapısı kullanılır.

```text
"house" -> doc1, doc4, doc9
"garden" -> doc2, doc4
```

Bu yapı text search'i relational LIKE query'lerinden çok daha güçlü hale getirir.

## 5. Analyzer

Text indexing öncesinde:
- tokenizer
- token filters
- character filters

ile normalize edilir.

Search quality analyzer seçimine bağlıdır.

## 6. Mapping

Field type'ları:
- text
- keyword
- numeric
- date
- geo_point

gibi explicit mapping ile tanımlanabilir.

Dynamic mapping production'da dikkatli kullanılmalıdır.

## 7. Shard

Index data shard'lara bölünebilir.

Primary shard data ownership'i, replica shard high availability/read scale'i destekler.

## 8. Near Real-Time

Write sonrası document anında searchable olmak zorunda değildir.

Refresh interval nedeniyle near-real-time visibility vardır.

## 9. Bu projede nasıl kullanılıyor?

Day 7'de SearchService için:
- Elasticsearch 9.x container
- single-node mode
- local security disabled
- persistent volume
- cluster-health healthcheck

hazırlanmıştır.

Application-level Spring Data Elasticsearch integration Day 12'ye aittir.

## 10. Target architecture rolü

Elasticsearch canonical source of truth değildir.

```text
PropertyService / MongoDB
        |
        | events
        v
SearchService
        |
        v
Elasticsearch
```

Search index derived CQRS query projection olacaktır.

## 11. Avantajları

- full-text search
- relevance scoring
- aggregations
- filters
- geo queries
- horizontal search scaling

## 12. Trade-off'ları

- source of truth için uygun default değildir
- index consistency near-real-time olabilir
- mapping changes dikkat ister
- shard sizing gerekir
- heap/storage maliyeti yüksektir

## 13. Production considerations

- shard count
- replicas
- JVM heap
- disk watermarks
- mapping
- analyzer
- refresh interval
- snapshots
- index lifecycle
- security

## 14. Search rebuild

Projection bozulduğunda iki recovery yolu farklıdır:
- Kafka replay
- source-of-truth reindex

Final rebuild/reconciliation strategy sonraki roadmap milestone'ında uygulanacaktır.

## 15. İleri öğrenme konuları

- Lucene segments
- BM25
- analyzers
- doc values
- shard routing
- refresh/merge
- ILM
- snapshots
