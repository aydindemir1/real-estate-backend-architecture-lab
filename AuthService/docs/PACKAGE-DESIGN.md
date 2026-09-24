# AuthService — Package / Class-Level Design

## Architecture
N-Layer Architecture

Mevcut çalışan yapı korunur; yalnızca package sorumlulukları netleştirilir.

```text
com.aydindemir.auth
├── controller
│   └── AuthController
├── dto
│   ├── request
│   │   ├── RegisterRequest
│   │   └── LoginRequest
│   └── response
│       ├── AccountResponse
│       └── LoginResponse
├── entity
│   └── Account
├── repository
│   └── AccountRepository
├── service
│   ├── AuthService
│   └── AuthServiceImpl
├── mapper
│   └── AccountMapper
├── client
│   └── UserProfileFeignClient
├── messaging
│   └── UserProfileCommandPublisher
├── security
│   ├── JwtService
│   └── PasswordEncoderConfiguration
├── exception
└── configuration
```

Day 8'de Keycloak/OAuth2 geldiğinde security package yeniden düzenlenecek; N-Layer baseline korunacaktır.
