# Service Catalog

| Service | Sorumluluk | Sahip olduğu veri | Sahip olmadığı veri |
|---|---|---|---|
| AuthService | Authentication/account baseline | Auth/account persistence | User profile, property, search |
| UserProfileService | User profile baseline | User profile data | Auth credentials, property |
| AgentService | Agent identity, license ve agency-facing business data | Agent relational data | Buyer preferences, property search |
| BuyerService | Buyer preferences ve buyer-side behavior | Buyer document data | Canonical property data |
| SellerService | Seller profile, seller activity, listing submission history | Seller-oriented wide-column data | Canonical property document |
| PropertyService | Canonical property lifecycle ve write model | Property MongoDB documents | Search index |
| SearchService | Search projection ve query model | Elasticsearch index | Canonical property source of truth |
| ApiGatewayService | Edge routing, auth boundary, rate limiting | Business data yok | Domain persistence |
| Config Servers | Central configuration | Configuration | Business data |
| EurekaServer | Service registry/discovery | Registry state | Business data |

## Temel ownership kuralı

Bir service başka bir service'in verisini API veya event üzerinden tüketebilir; ancak başka bir service'in datastore'una doğrudan yazamaz.

## Search kuralı

SearchService derived projection'dır. PropertyService/MongoDB canonical source olarak kalır. Elasticsearch index'i yeniden üretilebilir olmalıdır.
