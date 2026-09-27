# Authorization Matrix

Roller:
- GUEST
- BUYER
- SELLER
- AGENT
- ADMIN
- SERVICE

| Use-case / Endpoint | Guest | Buyer | Seller | Agent | Admin | Service |
|---|---:|---:|---:|---:|---:|---:|
| Search published properties | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Get public property detail | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Create/Update BuyerPreferences |  | ✓ own |  |  | ✓ |  |
| Create Offer |  | ✓ own |  |  | ✓ |  |
| Cancel Offer |  | ✓ own |  |  | ✓ |  |
| Submit Listing |  |  | ✓ own |  | ✓ |  |
| Accept/Reject Offer |  |  | ✓ own |  | ✓ |  |
| Update/Publish/Withdraw Property |  |  | ✓ owner | assigned? | ✓ | ✓ internal |
| Assign Agent |  |  | ✓ owner |  | ✓ |  |
| Create/Update Agent Profile |  |  |  | ✓ own | ✓ |  |
| Change Agent Availability |  |  |  | ✓ own | ✓ |  |
| Check Agent Availability |  | ✓ via service | ✓ via service | ✓ | ✓ | ✓ |

## Ownership rules

- `own`: token subject / user mapping ilgili resource owner ile eşleşmelidir.
- Agent yalnızca kendi profile/availability verisini değiştirir.
- Admin ownership bypass yapabilir; audit edilmelidir.
- Service-to-service çağrılarda client/service scope kullanılmalıdır.

## Scope candidates

- `property.read`
- `property.write`
- `buyer.read`
- `buyer.write`
- `seller.read`
- `seller.write`
- `agent.read`
- `agent.write`
- `search.read`

Kesin Keycloak role/scope mapping Day 14 Security milestone'ında finalize edilir.

Day 8 AgentService implementation'ında Keycloak/RBAC uygulanmaz.
