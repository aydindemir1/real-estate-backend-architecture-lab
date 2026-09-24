# ApiGatewayService — Package / Class-Level Design

## Rol
Edge / Infrastructure Service

```text
com.aydindemir.gateway
├── ApiGatewayServiceApplication
├── configuration
│   ├── RouteConfiguration
│   ├── SecurityConfiguration
│   ├── RateLimitConfiguration
│   └── ResilienceConfiguration
├── filter
│   ├── CorrelationIdFilter
│   ├── AuthenticationContextFilter
│   └── RequestLoggingFilter
├── fallback
│   └── GatewayFallbackController
├── security
│   └── JwtAuthenticationConverter
└── observability
    └── GatewayObservationConfiguration
```

## Kural
Gateway business rule, domain entity veya persistence repository içermez.
