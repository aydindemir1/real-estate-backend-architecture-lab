# Circuit Breaker Pattern

**Category:** Pattern  
**Introduced:** Day 5A  
**Project status:** Implemented / Verified  
**Scope:** Failure isolation for remote calls

## 1. Nedir?

Circuit Breaker, başarısız remote call'ların sürekli tekrar edilmesini engelleyerek downstream failure'ın sisteme yayılmasını sınırlayan resilience pattern'idir.

## 2. Problem

Bir downstream service erişilemez olduğunda sürekli request göndermek:
- thread tüketir
- latency artırır
- retry storm oluşturabilir
- cascading failure yaratabilir

## 3. State'ler

```text
CLOSED
  |
  | failures exceed threshold
  v
OPEN
  |
  | wait duration
  v
HALF_OPEN
  |
  +--> success -> CLOSED
  +--> failure -> OPEN
```

## 4. CLOSED

Request'ler normal geçer. Failure metric toplanır.

## 5. OPEN

Request downstream'e gönderilmez; fail-fast yapılır.

## 6. HALF_OPEN

Sınırlı test request'i gönderilerek downstream recovery kontrol edilir.

## 7. Ne işe yarar?

- fail-fast
- resource protection
- cascading failure azaltma
- recovery window oluşturma

## 8. Bu projede nasıl kullanılıyor?

Day 5A'da Gateway route'ları Spring Cloud Circuit Breaker + Resilience4j ile korunmuştur.

Downstream erişilemezse fallback endpoint'e yönlendirme yapılır.

## 9. Circuit Breaker retry değildir

Retry:
> Aynı operation'ı tekrar dene.

Circuit Breaker:
> Downstream zaten bozuk görünüyorsa request gönderme.

Birlikte kullanılabilirler ama policy ownership dikkatle tasarlanmalıdır.

## 10. Avantajları

- fast failure
- resource conservation
- fault isolation
- recovery control

## 11. Riskler

- yanlış threshold
- gereksiz open state
- hidden failures
- fallback ile gerçek problem gizleme
- retry + CB interaction complexity

## 12. Production considerations

- failure rate threshold
- slow call threshold
- minimum call count
- open wait duration
- half-open permitted calls
- metrics
- alerting

## 13. İlgili pattern'ler

- Retry
- Timeout
- Bulkhead
- Fallback
- Rate Limiting

## 14. İleri öğrenme konuları

- adaptive resilience
- retry budget
- failure classification
- slow-call circuit breaking
