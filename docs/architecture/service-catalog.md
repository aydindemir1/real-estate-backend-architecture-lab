# Service Catalog

| Service | Responsibility | Owns data | Does not own |
|---|---|---|---|
| AuthService | Authentication/account baseline | Auth/account persistence | User profile, property, search |
| UserProfileService | User profile baseline | User profile data | Auth credentials, property |
| AgentService | Agent identity, license, agency-facing business data | Agent relational data | Buyer preferences, property search |
| BuyerService | Buyer preferences and buyer-side behavior | Buyer document data | Canonical property data |
| SellerService | Seller profile, seller activity, listing submission history | Seller-oriented wide-column data | Canonical property document |
| PropertyService | Canonical property lifecycle and write model | Property MongoDB documents | Search index |
| SearchService | Search projection and query model | Elasticsearch index only | Canonical property source of truth |
| ApiGatewayService | Edge routing, auth boundary, rate limiting | No business data | Domain persistence |
| Config Servers | Central configuration | Configuration | Business data |
| EurekaServer | Service registry/discovery | Registry state | Business data |

## Core ownership rule

A service may consume another service's data through an API or event, but it must not write directly to another service's datastore.

## Search rule

SearchService is a derived projection. PropertyService/MongoDB remains canonical. Elasticsearch can be rebuilt.
