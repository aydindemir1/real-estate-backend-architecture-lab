# JWT

**Category:** Token Format / Standard  
**Introduced:** Day 2  
**Project status:** Implemented / Verified  
**Scope:** Signed authentication token format in current baseline

## 1. Nedir?

JWT (JSON Web Token), claim'leri compact ve URL-safe biçimde taşıyan token format standardıdır.

JWT authentication protocol değildir.

## 2. Yapısı

JWT tipik olarak üç bölümden oluşur:

```text
header.payload.signature
```

JWS compact serialization bağlamında bu üç bölüm nokta ile ayrılır.

## 3. Header

Header:
- token type
- signing algorithm

gibi metadata taşır.

## 4. Payload

Payload claim'leri taşır.

Registered claim örnekleri:
- iss
- sub
- aud
- exp
- nbf
- iat
- jti

Custom claim de kullanılabilir.

## 5. Signature

Signature token'ın integrity/authenticity kontrolüne yardımcı olur.

Signature payload'ı encrypt etmez.

## 6. Encoding encryption değildir

JWT bölümlerinde Base64URL encoding kullanılması veriyi gizlemez.

Payload kolayca decode edilebilir.

Bu nedenle secret veya hassas bilgi token içine konmamalıdır.

## 7. JWS ve JWE

JWT:
- signed formda JWS
- encrypted formda JWE

ile temsil edilebilir.

Bu projedeki baseline signed JWT kullanır.

## 8. Symmetric signing

HMAC ile aynı shared secret sign ve verify için kullanılır.

Basit ama distributed verifier'lara secret dağıtımı gerekir.

## 9. Asymmetric signing

RSA/ECDSA gibi modelde:
- private key sign eder
- public key verify eder

Bu, verifier service'lerin signing key'e sahip olmamasını sağlar.

## 10. Verification

Bir token'a güvenmeden önce:
- signature
- algorithm
- expiration
- issuer
- audience
- relevant claims

doğrulanmalıdır.

## 11. Expiration

Access token kısa ömürlü tasarlanabilir.

Expiration compromise impact'i sınırlar ancak refresh/re-authentication lifecycle ihtiyacı oluşturur.

## 12. Revocation

JWT stateless verification avantajı taşır ancak revocation doğal olarak zor olabilir.

Stratejiler:
- short-lived access token
- denylist
- token version
- refresh token rotation
- identity provider session

## 13. Bu projede nasıl kullanılıyor?

Day 2'de AuthService custom register/login flow içinde JWT üretip doğrulamaktadır.

Concrete Java library:
- Auth0 java-jwt

## 14. JWT ile OAuth2 farkı

JWT:
> Token formatı.

OAuth2:
> Authorization framework.

OAuth2 token JWT olabilir ama olmak zorunda değildir.

## 15. JWT ile OIDC farkı

OIDC:
> Identity/authentication protocol layer.

OIDC'de ID Token genellikle JWT'dir.

Ama JWT tek başına OIDC değildir.

## 16. Security considerations

- algorithm pinning
- strong key
- key rotation
- short expiration
- issuer/audience validation
- clock skew
- token redaction in logs
- no sensitive payload

## 17. Current project boundary

Custom JWT baseline final security architecture değildir.

İleri roadmap:
- Spring Security
- OAuth2
- OIDC
- Keycloak
- RBAC

ile daha olgun identity architecture kurulacaktır.

## 18. İleri öğrenme konuları

- RFC 7519
- JWS
- JWE
- JWKS
- key rotation
- token introspection
- refresh token
