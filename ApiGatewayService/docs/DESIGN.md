# ApiGatewayService Design

## Purpose
Provide edge routing and cross-cutting request controls.

## Existing responsibilities
- route requests by service name
- use Eureka + Spring Cloud LoadBalancer
- apply Circuit Breaker/fallback
- propagate trace context

## Planned responsibilities
- validate/propagate OAuth2 identity context
- apply rate limiting
- expose resilience/observability signals

## Non-responsibilities
- business rules
- domain persistence
- canonical business data
