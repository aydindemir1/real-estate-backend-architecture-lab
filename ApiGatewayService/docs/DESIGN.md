# ApiGatewayService Design

## Amaç
Edge routing ve cross-cutting request control sağlamak.

## Mevcut sorumluluklar
- request'leri service-name üzerinden route etmek
- Eureka + Spring Cloud LoadBalancer kullanmak
- Circuit Breaker / fallback uygulamak
- trace context propagation sağlamak

## Planlanan sorumluluklar
- OAuth2 identity context doğrulamak ve gerektiğinde propagate etmek
- Rate Limiting uygulamak
- resilience ve observability signal'larını expose etmek

## Sorumluluk dışı alanlar
- business rule
- domain persistence
- canonical business data
