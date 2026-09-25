# Data Ownership

**Category:** Principle  
**Introduced:** Day 3  
**Project status:** Implemented as architecture rule / Strengthened in Day 7  
**Scope:** Canonical data ownership by service

## 1. Nedir?

Data Ownership, belirli bir data set'inin canonical source of truth sorumluluğunun açıkça bir service veya bounded context'e ait olması prensibidir.

## 2. Neden önemlidir?

Birden fazla service aynı tablo veya schema'yı doğrudan değiştirirse ownership belirsizleşir, schema/deployment coupling artar ve business invariant'lar farklı yerlere dağılır.

## 3. Canonical data

Bir business fact'in authoritative kaynağıdır. Örneğin Account AuthService'e, Property lifecycle PropertyService'e aittir. Search document ise derived projection'dır ve canonical değildir.

## 4. Read ve write ownership

Başka servislerin verisi API, event veya projection üzerinden okunabilir. Başka service'in database'ine doğrudan write yapmak ownership boundary'sini bozar.

## 5. Database per Service ile ilişkisi

Data Ownership bir prensiptir: 'Bu verinin sahibi kim?' Database per Service ise bunu destekleyen bir pattern'dir: 'Sahibi olan service kendi datastore boundary'sine sahip olsun.'

## 6. Projection

Derived read model source of truth değildir ve canonical state'ten yeniden üretilebilir. Bu projede SearchService Elasticsearch modeli bu role sahiptir.

## 7. Bu projedeki ownership

- Auth -> account / credentials
- UserProfile -> common profile
- Agent -> professional agent data
- Buyer -> preferences / buyer-side offer state
- Seller -> seller profile / listing submission / seller projections
- Property -> canonical property lifecycle
- Search -> derived search projection

## 8. Day 7 ile güçlenen yapı

Agent, Buyer, Seller ve Property için geçici PostgreSQL Compose servisleri kaldırılıp target datastore infrastructure ayrıştırılmıştır.

## 9. Anti-pattern'ler

- shared tables
- cross-service joins
- direct database writes
- duplicated canonical ownership
- search index'i source of truth kabul etmek
- Redis'i correctness store olarak kullanmak

## 10. Redis ve Elasticsearch sınırı

Redis cache, idempotency acceleration, rate limiting ve ephemeral state içindir. Elasticsearch SearchService derived CQRS query projection'ıdır. İkisi de canonical business source of truth değildir.

## 11. Production considerations

Backup, schema migration, retention, access control, audit, recovery ve replication sorumluluğu data ownership ile birlikte tanımlanmalıdır.

## 12. İleri öğrenme konuları

- bounded context
- source of truth
- materialized views
- CDC
- reconciliation