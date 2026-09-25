# Fallback Pattern

**Category:** Pattern  
**Introduced:** Day 5A  
**Project status:** Implemented / Verified  
**Scope:** Alternative response when dependency fails

## 1. Nedir?

Fallback Pattern, primary operation başarısız olduğunda kontrollü alternatif davranış uygulanmasını sağlar.

## 2. Amaç

Failure durumunda:
- controlled response
- degraded functionality
- cached/default result
- alternative path

sunabilmektir.

## 3. Örnek

```text
Client
  |
  v
Gateway
  |
  v
Target Service unavailable
  |
  v
Fallback Handler
  |
  v
Controlled Response
```

## 4. Fallback ne değildir?

Fallback her failure'ı başarılı response gibi göstermemelidir.

Yanlış fallback:
- stale data'yı doğrulamadan döndürmek
- critical business failure'ı saklamak
- yanlış success status üretmek

## 5. Bu projede nasıl kullanılıyor?

Day 5A'da Gateway route'ları için fallback endpoint'leri oluşturulmuştur.

Service erişilemez olduğunda Gateway controlled fallback response üretir.

## 6. Avantajları

- graceful degradation
- better user experience
- predictable failure behavior

## 7. Dezavantajları

- gerçek failure'ın gizlenmesi
- stale/incorrect response riski
- fallback logic complexity

## 8. Production considerations

Fallback:
- business criticality
- data freshness
- failure type
- user expectation

gözetilerek tasarlanmalıdır.

## 9. İlgili pattern'ler

- Circuit Breaker
- Retry
- Cache-Aside
- Graceful Degradation

## 10. İleri öğrenme konuları

- semantic fallback
- stale-while-revalidate
- partial response design
