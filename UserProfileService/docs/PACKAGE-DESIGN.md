# UserProfileService — Package / Class-Level Design

## Architecture
N-Layer Architecture

```text
com.aydindemir.userprofile
├── controller
│   └── UserProfileController
├── dto
│   ├── request
│   │   └── UpdateUserProfileRequest
│   └── response
│       └── UserProfileResponse
├── entity
│   └── UserProfile
├── repository
│   └── UserProfileRepository
├── service
│   ├── UserProfileService
│   └── UserProfileServiceImpl
├── mapper
│   └── UserProfileMapper
├── messaging
│   └── CreateUserProfileCommandConsumer
├── exception
└── configuration
```

Mevcut N-Layer structure eğitim karşılaştırması için korunur.
