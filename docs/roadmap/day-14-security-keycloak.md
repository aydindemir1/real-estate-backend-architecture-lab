# Day 14 — Spring Security + OAuth2/OIDC + Keycloak

## Amaç
Identity ve authorization modelini Keycloak tabanlı hale getirmek.

## Görevler
1. Keycloak local infrastructure ekle.
2. realm/client/role design'i netleştir.
3. Keycloak subject -> UserProfile -> role-specific profile identity mapping'i finalize et.
4. Gateway Resource Server/security config ekle.
5. downstream services token validation ekle.
6. role/scope mapping oluştur.
7. BUYER/SELLER/AGENT/ADMIN authorization rules ekle.
8. ownership check application boundary'de uygula.
9. service-to-service Client Credentials foundation ekle.
10. 401/403 error contract uygula.
11. actuator/security endpoint policy ekle.
12. security tests: no token, bad token, wrong role, wrong owner.
13. audit-sensitive operation hooks foundation ekle.
14. docs/ADR güncelle.

## Önerilen commit'ler
1. infra(security): add Keycloak local setup
2. docs(security): finalize identity and role model
3. feat(gateway): add OAuth2 resource server security
4. feat(security): add downstream token validation
5. feat(security): add role and scope authorization
6. feat(security): enforce ownership rules
7. feat(security): add service-to-service client credentials
8. test(security): add authentication and authorization tests
9. docs(security): finalize Keycloak integration

## Tamamlanma durumu
Authentication/authorization Gateway + downstream defense-in-depth ile çalışır; ownership test edilmiştir.

## Kesin security/file planı

Implementation için source of truth: `docs/roadmap/day-14-exact-file-plan.md`

## Onaylanan ek öğrenme ve uygulama kapsamı

- docs/standards/api-design.md ve security.md kapsamını endpoint authorization matrisiyle birleştir; mevcut RBAC/ownership/CORS/CSRF/validation kurallarını çoğaltmadan Secure API Design checklist’i oluştur.
- Input/body/page sınırları, alan bazlı response minimization, hassas hata redaction ve actuator erişimini mevcut endpoint’lere uygula; başka kullanıcının resource ID’siyle IDOR testlerini genişlet.
- Encryption in transit ile at rest arasındaki farkı ve sorumluluk matrisini yaz: HTTP/gRPC, broker/datastore bağlantıları, disk/volume/backup, certificate/key ownership. JWT imzası şifreleme değildir; Vault secret yönetimi at-rest encryption’ın tamamı değildir.
- Mevcut HTTP endpoint üzerinde izole local/test TLS handshake doğrulaması planla: doğru CA/hostname kabulü, untrusted certificate veya hostname uyuşmazlığında reddetme. Sertifika doğrulamasını kapatıp başarı gösterme; gerçek private key repository’ye koyma.
- At-rest için mevcut datastore/disk/backup seçeneklerini tasarım seviyesinde karşılaştır; uygulama kanıtı yoksa Design Only olarak işaretle. Production certificate yönetimi, mTLS ve platform deployment bu güne eklenmez.
- Day 23 abuse prevention ve Day 24 Vault secret rotation sınırlarına bağlantı ver; docs/security/secure-api-checklist.md, docs/security/encryption-responsibilities.md ve Knowledge Base etkisini güncelle.

Güncel görevler, commit sırası ve ek kabul ölçütleri [kesin planda](day-14-exact-file-plan.md) yer alır. Bu ek kapsam **planlıdır**; doğrulama kanıtı oluşmadan tamamlandı olarak işaretlenmez.
