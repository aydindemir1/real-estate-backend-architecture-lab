# Authorization Matrix

Roller:

- GUEST
- BUYER
- SELLER
- AGENT
- ADMIN
- SERVICE

`SERVICE`, service-to-service authentication için logical role/scope olarak ele alınır.

| Use-case / Endpoint | Guest | Buyer | Seller | Agent | Admin | Service |
|---|---:|---:|---:|---:|---:|---:|
| Search published properties | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Get public property detail | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Create/Update BuyerPreferences |  | ✓ own |  |  | ✓ |  |
| Create Offer |  | ✓ own |  |  | ✓ |  |
| Cancel Offer |  | ✓ own |  |  | ✓ |  |
| List Buyer Offers |  | ✓ own |  |  | ✓ |  |
| Submit Listing |  |  | ✓ own |  | ✓ |  |
| List Seller Submissions |  |  | ✓ own |  | ✓ |  |
| List Pending Seller Offers |  |  | ✓ own |  | ✓ |  |
| Accept/Reject Offer |  |  | ✓ own |  | ✓ |  |
| Update/Publish/Withdraw Property |  |  | ✓ owner | assigned? | ✓ | ✓ internal |
| Assign Agent |  |  | ✓ owner |  | ✓ |  |
| Create/Update Agent Profile |  |  |  | ✓ own | ✓ |  |
| Change Agent Availability |  |  |  | ✓ own | ✓ |  |
| Check Agent Availability |  | ✓ via service | ✓ via service | ✓ | ✓ | ✓ |
| Admin operational endpoint |  |  |  |  | ✓ |  |

## Ownership rule'ları

- `own`: token subject / user mapping ilgili resource owner ile eşleşmelidir.
- Seller yalnızca kendi listing/property/offer decision scope'unda işlem yapar.
- Buyer yalnızca kendi Offer ve BuyerPreferences verisini yönetir.
- Agent yalnızca kendi profile/availability verisini değiştirir.
- Admin ownership bypass yapabilir; bu yetki audit edilmelidir.
- Service-to-service çağrılarda human role yerine client/service scope kullanılmalıdır.

## Scope candidate'ları

- `property.read`
- `property.write`
- `buyer.read`
- `buyer.write`
- `seller.read`
- `seller.write`
- `agent.read`
- `agent.write`
- `search.read`

Kesin Keycloak role/scope mapping Day 8'de finalize edilir.

## Security prensipleri

- Authentication Gateway'de başlar ama downstream service de token doğrular.
- Authorization yalnızca Gateway'e bırakılmaz.
- Business ownership check application/domain seviyesinde yapılır.
- 401 ve 403 ayrımı korunur.
