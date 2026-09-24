# Security Standard

Bu doküman projede uygulanacak authentication, authorization, service identity, secret management ve secure coding standartlarını tanımlar.

## 1. Security by Design
- Security yalnızca endpoint'e authentication eklemek değildir.
- Tasarım aşamasında identity, ownership, least privilege, sensitive data, secret ve abuse senaryoları değerlendirilir.

## 2. Identity Provider
- Day 8 sonrasında primary identity provider Keycloak olacaktır.
- OAuth2 / OpenID Connect kullanılacaktır.
- Mevcut custom JWT implementation eğitim karşılaştırması için legacy baseline olarak korunabilir.

## 3. Authentication
- Resource Server'lar access token doğrular.
- Gateway authentication boundary'nin ilk noktasıdır.
- Downstream service'ler de token validation yapar; yalnızca Gateway'e güvenilmez.

## 4. Authorization
- RBAC + gerektiğinde scope/permission birlikte kullanılır.
- Role tek başına ownership kontrolünün yerine geçmez.

## 5. Ownership
- Buyer yalnızca kendi BuyerPreferences ve Offer kayıtlarını yönetir.
- Seller yalnızca kendi listing/property/offer decision scope'unda işlem yapar.
- Agent yalnızca kendi profile/availability verisini değiştirir.
- Admin bypass yetkisi audit edilmelidir.

## 6. Service-to-Service Authentication
- Internal service call'larda human user token kullanmak zorunlu değildir.
- Client Credentials flow değerlendirilecektir.
- Service account/client identity ayrı tutulur.

## 7. Least Privilege
- Her role/client yalnızca gerekli scope ve endpoint'e erişir.
- Broad admin/service token kullanılmaz.

## 8. Scope Candidate'ları
- property.read
- property.write
- buyer.read
- buyer.write
- seller.read
- seller.write
- agent.read
- agent.write
- search.read

## 9. Token Validation
- issuer
- audience gerektiğinde
- expiry
- signature
- scope/role
- token type
kontrol edilir.

## 10. Token Handling
- Access token loglanmaz.
- Token query parameter'da taşınmaz.
- Bearer token HTTPS üzerinden taşınır.

## 11. Password
- Plaintext password persist edilmez.
- Keycloak dışında legacy credential varsa BCrypt/Argon2 gibi güvenli password hashing kullanılır.
- Password policy IdP tarafında yönetilir.

## 12. Secret Management
- Secret source code'a yazılmaz.
- Environment variable sadece başlangıç için kabul edilebilir.
- Spring Cloud Vault Day 15'te secret management için uygulanacaktır.

## 13. Configuration Secrets
- DB password
- broker credential
- Keycloak client secret
- API key
- signing material
Git repository'ye commit edilmez.

## 14. Secure Defaults
- endpoint default deny yaklaşımına yakın tasarlanır.
- gereksiz actuator endpoint public olmaz.
- development-only config production-benzeri profile'a taşınmaz.

## 15. Input Validation
- DTO validation zorunludur.
- whitelist yaklaşımı tercih edilir.
- arbitrary sort/filter field expose edilmez.

## 16. Injection Risk
- SQL string concatenation yapılmaz.
- parameterized query / repository abstraction kullanılır.
- Elasticsearch query input doğrudan raw DSL'e bağlanmaz.

## 17. Mass Assignment
- Entity doğrudan request body olarak kullanılmaz.
- Dedicated Request DTO kullanılır.

## 18. Sensitive Response
- Password hash
- secret
- token
- internal credential
- private infrastructure metadata
response'a konmaz.

## 19. CORS
- Allowed origin explicit olmalıdır.
- wildcard production-benzeri kullanımda kaçınılır.

## 20. CSRF
- Stateless bearer-token API'lerde CSRF gereksinimi kullanım modeline göre değerlendirilir.
- Cookie-based auth varsa yeniden değerlendirilir.

## 21. Session
- API'ler stateless tasarlanır.
- Server-side session default değildir.

## 22. Security Headers
Gateway veya edge katmanında uygun security headers uygulanır.

## 23. Rate Limiting
- login/auth
- offer creation
- search/autocomplete
- expensive endpoint
için Rate Limiting uygulanabilir.

## 24. Brute Force
- Keycloak brute-force protection gibi IdP özellikleri değerlendirilecektir.

## 25. Audit Logging
Security-sensitive action audit edilir:
- login failure gerektiğinde
- role/admin action
- seller offer accept/reject
- property state critical transition
- account disable/lock

Audit log ile application debug log ayrıdır.

## 26. Audit Data
- actor
- action
- resource
- timestamp
- correlationId
- outcome
tutulabilir.

Sensitive payload audit log'a yazılmaz.

## 27. Authorization Placement
- coarse-grained security: Spring Security / method security
- ownership/business permission: application/domain boundary

## 28. Method Security
@PreAuthorize benzeri method security gerektiğinde kullanılır fakat bütün business ownership expression'ı annotation içine gömülmez.

## 29. SecurityContext
- Domain model SecurityContext bilmez.
- Application layer normalized CurrentActor benzeri context kullanabilir.

## 30. Error Semantics
- 401: unauthenticated
- 403: authenticated but forbidden
- 404 gerektiğinde resource enumeration riskini azaltmak için policy bazlı değerlendirilebilir.

## 31. Resource Enumeration
- Sequential predictable identifier'a business meaning bağlanmaz.
- Unauthorized caller'a gereksiz existence bilgisi verilmez.

## 32. SSRF / External URL
User-supplied arbitrary URL fetch use-case'i eklenirse allowlist ve network restriction gerekir.

## 33. File Upload
Bu scope'ta zorunlu değil. Eklenirse size/type/content validation ve malware scanning düşünülür.

## 34. Messaging Security
- Kafka/RabbitMQ authentication
- TLS gerektiğinde
- topic/queue permission
- least privilege producer/consumer identity
- secret management

## 35. gRPC Security
- service-to-service token/mTLS candidate
- deadline
- authorization
- trace context

## 36. GraphQL Security
- resolver authorization
- query depth/complexity limit
- introspection policy environment'a göre
- N+1/DoS riskleri

## 37. Actuator Security
- health/liveness/readiness exposure kontrollü
- sensitive actuator endpoint public değildir.

## 38. Dependency Security
- dependency vulnerability scanning yapılacaktır.
- Dependabot veya benzeri update mekanizması değerlendirilebilir.
- SonarQube security rules ileriki CI/CD fazında kullanılır.

## 39. Supply Chain
- dependency version pinning/management
- trusted repository
- Nexus
- Harbor image registry
- image scanning
ileriki DevSecOps fazında uygulanacaktır.

## 40. Container Security
- non-root container
- minimal base image
- secret image içine bake edilmez
- unnecessary package azaltılır
post-backend Docker hardening fazında uygulanır.

## 41. Logging Security
- token/password/secret loglanmaz.
- PII gereksiz loglanmaz.
- structured log field mask uygulanır.

## 42. Error Leakage
- stack trace client'a dönmez.
- SQL/DB/broker internal detail client'a sızmaz.

## 43. Security Testing
- unauthenticated request
- wrong role
- ownership violation
- expired token
- invalid issuer
- invalid scope
- service credential failure
- rate limit
- sensitive response
test edilir.

## 44. Threat Modeling
Critical flow'lar için hafif threat modeling uygulanır.
Örnek sorular:
- Kim saldırabilir?
- Hangi asset korunuyor?
- Trust boundary nerede?
- Abuse case nedir?
- Mitigation nedir?

## 45. STRIDE Awareness
Formal süreç zorunlu değil ama Spoofing, Tampering, Repudiation, Information Disclosure, Denial of Service, Elevation of Privilege perspektifi kullanılabilir.

## 46. Security Anti-Pattern'leri
- yalnızca Gateway auth'a güvenmek
- hard-coded secret
- plaintext password
- token loglamak
- wildcard authorization
- role = ownership varsaymak
- entity'yi request body olarak bind etmek
- broad SERVICE role
- actuator'u public açmak
- security exception detail sızdırmak
- dependency vulnerability'yi görmezden gelmek

## 47. Security Review Checklist
- Endpoint authenticated mı?
- Hangi role/scope gerekli?
- Ownership kontrolü var mı?
- Service-to-service identity doğru mu?
- Secret nerede tutuluyor?
- Token validation eksiksiz mi?
- Sensitive data expose/log ediliyor mu?
- Rate limit gerekli mi?
- Audit gerekli mi?
- Dependency risk var mı?
- Failure 401/403 doğru mu?
- Threat/abuse senaryosu düşünüldü mü?