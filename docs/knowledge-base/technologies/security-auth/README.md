# Security & Authentication

Authentication, authorization, token ve identity/security teknolojileri.

## Day 1–7

- [Auth0 java-jwt](auth0-java-jwt.md)

## Kavramsal ayrım

- JWT -> token format / standard
- Auth0 java-jwt -> Java JWT library
- Authentication -> "Kim?"
- Authorization -> "Neye izinli?"
- OAuth2 -> authorization framework
- OpenID Connect -> identity/authentication layer

Bu kavramlar aynı şey değildir.

## Current project scope

Day 1–7 baseline custom JWT authentication kullanır.

Bu yapı final security architecture değildir.

## Sonraki roadmap kapsamı

Security milestone'ında canonical dokümanlar eklenecektir:

- Spring Security
- OAuth2
- OpenID Connect
- Keycloak
- RBAC
- service-to-service authentication
- token/identity lifecycle
- least privilege

Secret management için Spring Cloud Vault ayrı technology family içinde Spring Cloud altında dokümante edilecektir.
