# Spring MVC

**Category:** Technology  
**Introduced:** Day 1  
**Project status:** Implemented / Verified  
**Scope:** Servlet-based HTTP request/response web framework

## 1. Nedir?

Spring MVC, Spring Framework'ün Servlet tabanlı web framework'üdür.

HTTP request'lerini controller method'larına map eder, request/response serialization, validation ve exception handling gibi web concerns sağlar.

## 2. Temel mimari

Merkezde DispatcherServlet bulunur.

```text
HTTP Request
    |
    v
DispatcherServlet
    |
    v
HandlerMapping
    |
    v
Controller
    |
    v
Service
    |
    v
Response
    |
    v
HttpMessageConverter
```

## 3. DispatcherServlet

Front Controller görevi görür.

Request'i alır ve uygun handler/controller'a yönlendirir.

## 4. HandlerMapping

Request path, HTTP method ve mapping annotation'larına göre controller method'unu bulur.

## 5. Controller

Yaygın annotation'lar:
- @RestController
- @RequestMapping
- @GetMapping
- @PostMapping
- @PutMapping
- @DeleteMapping

## 6. Serialization

JSON request/response conversion çoğunlukla Jackson tabanlı HttpMessageConverter üzerinden gerçekleşir.

## 7. Validation

Request DTO'larında Jakarta Validation ile validation yapılabilir.

Controller'ın business rule merkezi olmaması gerekir.

## 8. Exception Handling

- @ExceptionHandler
- @ControllerAdvice
- @RestControllerAdvice

ile merkezi HTTP error mapping yapılabilir.

## 9. Request Lifecycle

```text
Client
  -> Servlet Container
  -> Filter Chain
  -> DispatcherServlet
  -> HandlerMapping
  -> Controller
  -> Application Service
  -> Controller
  -> Message Converter
  -> HTTP Response
```

## 10. Blocking model

Spring MVC klasik olarak thread-per-request Servlet modeline dayanır.

Bu nedenle:
- blocking JDBC
- blocking HTTP
- thread pool capacity

önemlidir.

## 11. Spring WebFlux farkı

MVC:
- Servlet
- imperative
- blocking-friendly

WebFlux:
- reactive
- non-blocking
- event-loop/reactive streams

Bu projede primary model Spring MVC'dir.

## 12. Avantajları

- mature
- anlaşılır
- Spring ekosistemiyle güçlü entegrasyon
- imperative programming model
- geniş production kullanımı

## 13. Trade-off'ları

- thread-per-request resource cost
- blocking dependency'lere hassasiyet
- yüksek concurrency için capacity planning ihtiyacı

## 14. Bu projede nasıl kullanılıyor?

REST endpoint'leri Spring MVC controller'ları ile sunulur.

Auth, UserProfile ve diğer service baseline'ları Spring MVC tabanlıdır.

## 15. Production considerations

- request timeout
- validation
- payload size
- error contract
- filter ordering
- thread pool
- connection pool
- graceful shutdown

## 16. İleri öğrenme konuları

- DispatcherServlet internals
- HandlerAdapter
- filters vs interceptors
- argument resolvers
- message converters
- async MVC
