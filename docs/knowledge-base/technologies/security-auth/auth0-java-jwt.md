# Auth0 java-jwt

**Category:** Technology  
**Introduced:** Day 2  
**Project status:** Implemented / Verified  
**Scope:** Java library for JWT creation and verification

## 1. Nedir?

Auth0 `java-jwt`, Java uygulamalarında JSON Web Token (JWT) oluşturmak, imzalamak, decode etmek ve doğrulamak için kullanılan library'dir.

JWT standardının kendisi değildir.

Bu ayrım önemlidir:

- JWT -> token format / standard
- Auth0 `java-jwt` -> JWT ile çalışmayı sağlayan Java library

## 2. Hangi problemi çözer?

Application tarafında manual olarak:
- Base64URL encoding,
- header/payload oluşturma,
- signature üretme,
- signature doğrulama,
- claim okuma,
- expiration kontrolü

gibi işlemleri yazmak hata riski oluşturur.

`java-jwt` bu işlemleri güvenli ve type-aware API ile kolaylaştırır.

## 3. JWT yapısı

JWT üç bölümden oluşur:

```text
header.payload.signature
```

Örnek yapı:

```text
xxxxx.yyyyy.zzzzz
```

Bu üç bölüm Base64URL ile encode edilir.

## 4. Header

Header tipik olarak:
- token type
- signing algorithm

bilgisini taşır.

Örnek:

```json
{
  "typ": "JWT",
  "alg": "HS256"
}
```

## 5. Payload

Payload claim'leri taşır.

Yaygın registered claim'ler:
- iss
- sub
- aud
- exp
- nbf
- iat
- jti

Application-specific custom claim de eklenebilir.

## 6. Signature

Signature token'ın integrity'sini doğrulamak için kullanılır.

Symmetric örnek:

```text
HMACSHA256(
  base64Url(header) + "." + base64Url(payload),
  secret
)
```

Asymmetric algorithm'larda private/public key modeli kullanılır.

## 7. Token oluşturma akışı

```text
Authentication Success
      |
      v
Claims hazırlanır
      |
      v
Algorithm seçilir
      |
      v
JWT.create()
      |
      v
sign(...)
      |
      v
Signed Token
```

## 8. Token doğrulama akışı

```text
Incoming Token
      |
      v
JWTVerifier
      |
      +--> signature
      +--> expiration
      +--> issuer
      +--> claims
      |
      v
VerifiedJWT
```

## 9. Decode ile verify farkı

Sadece token'ı decode etmek güvenli doğrulama değildir.

Decode:
> Payload'ı okumak.

Verify:
> Signature ve security constraint'leri doğrulamak.

Untrusted token yalnız decode edilip güvenilir kabul edilmemelidir.

## 10. Signing algorithm

Auth0 `java-jwt` farklı algorithm'ları destekler.

Örnek:
- HMAC
- RSA
- ECDSA

Algorithm seçimi:
- key management,
- service topology,
- security model

ile birlikte değerlendirilmelidir.

## 11. Symmetric vs Asymmetric signing

### Symmetric

Aynı secret:
- sign
- verify

işleminde kullanılır.

Avantaj:
- basit

Risk:
- verifier olan her component aynı zamanda token üretebilir.

### Asymmetric

Private key:
- sign

Public key:
- verify

Avantaj:
- verification yapan service private key'e ihtiyaç duymaz.

Distributed systems için daha uygun olabilir.

## 12. Expiration

JWT içinde `exp` claim kullanılabilir.

Token expiration:
- replay window
- session duration
- credential compromise impact

üzerinde önemlidir.

Uzun ömürlü access token güvenlik riskini büyütür.

## 13. Stateless authentication ile ilişkisi

JWT access token server-side session olmadan authentication context taşıyabilir.

Bu yüzden stateless API tasarımında sık kullanılır.

Ancak JWT kullanmak otomatik olarak tamamen stateless architecture anlamına gelmez.

Refresh token, revocation veya session metadata server-side state gerektirebilir.

## 14. Bu projede nasıl kullanılıyor?

Day 2'de AuthService içinde custom JWT register/login flow oluşturulmuştur.

Auth0 `java-jwt`:
- token üretimi
- token doğrulama

için kullanılmıştır.

Day 7 dependency cleanup sırasında JWT dependency yalnız gerçekten ihtiyaç duyan AuthService module'ünde tutulmuştur.

## 15. Current project boundary

Bu Day 1–7 baseline custom JWT implementation'dır.

Roadmap'in ileri security milestone'ında:
- Spring Security
- OAuth2
- OpenID Connect
- Keycloak
- RBAC
- service-to-service authentication

eklenecektir.

Bu nedenle current custom JWT yaklaşımı final identity architecture değildir.

## 16. Secret management

Signing secret:
- source code'a yazılmamalı
- Git'e commit edilmemeli
- güvenli runtime configuration üzerinden sağlanmalıdır

Day 7'de literal JWT secret fallback'leri azaltılmıştır.

İleride Spring Cloud Vault secret management için kullanılacaktır.

## 17. Token contents

JWT payload encrypted değildir.

Base64URL encoding:
> encryption değildir.

Bu nedenle token içine:
- password
- secret
- hassas kişisel veri

konmamalıdır.

## 18. JWT revocation problemi

Signed JWT expiration süresine kadar cryptographically valid kalabilir.

Logout/revocation ihtiyacı varsa ayrıca:
- denylist
- token version
- short-lived access token
- refresh token rotation
- centralized identity provider

gibi stratejiler gerekir.

## 19. Avantajları

- mature Java API
- explicit token creation
- signature verification
- claim validation
- multiple algorithm support
- low integration overhead

## 20. Trade-off'ları

- library kullanmak identity architecture problemini çözmez
- secret/key lifecycle ayrıca yönetilmelidir
- revocation built-in session semantics değildir
- custom auth implementation zamanla security debt oluşturabilir

## 21. Anti-pattern'ler

- token'ı sadece decode edip güvenmek
- hard-coded secret
- çok uzun expiration
- sensitive data'yı payload'a koymak
- algorithm validation yapmamak
- authentication ile authorization'ı aynı şey sanmak
- JWT'yi OAuth2 sanmak

## 22. JWT ile OAuth2 farkı

JWT:
> Token formatıdır.

OAuth2:
> Authorization framework/protocol ailesidir.

OAuth2 access token JWT olabilir ama olmak zorunda değildir.

## 23. JWT ile OIDC farkı

OpenID Connect:
> OAuth2 üzerinde identity/authentication layer'ıdır.

OIDC çoğunlukla JWT-form ID Token kullanır.

Ancak JWT tek başına OIDC değildir.

## 24. Production considerations

- asymmetric signing değerlendirmesi
- key rotation
- issuer validation
- audience validation
- expiration
- clock skew
- secure key storage
- token revocation model
- algorithm pinning
- logging sırasında token redaction

## 25. Alternatifleri / higher-level solutions

- Spring Security OAuth2 Resource Server
- Keycloak
- Auth0 Identity Platform
- cloud identity providers
- opaque access tokens

## 26. İleri öğrenme konuları

- JWS
- JWE
- JWKS
- key rotation
- OAuth2
- OIDC
- refresh token rotation
- token introspection
