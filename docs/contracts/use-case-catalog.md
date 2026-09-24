# Use-Case Catalog

Bu doküman service bazında application use-case sınırlarını tanımlar. İsimler implementation sırasında command/query handler, application service veya use-case interface olarak uygulanabilir; service'in architecture stiline göre klasör yapısı değişebilir.

## AuthService

- RegisterAccount
- Login
- GetAccount
- DisableAccount
- LockAccount
- UnlockAccount

Day 8 sonrası primary authentication Keycloak'a kaydıkça bazı use-case'ler adapter/integration rolüne dönüşebilir.

## UserProfileService

- CreateUserProfile
- GetUserProfile
- UpdateUserProfile

## AgentService

- CreateAgent
- GetAgent
- UpdateAgentProfile
- ChangeAgentStatus
- ChangeAvailability
- CheckAvailability

## BuyerService

### BuyerPreferences
- CreateBuyerPreferences
- UpdateBuyerPreferences
- GetBuyerPreferences
- AddSavedSearch
- RemoveSavedSearch

### Viewing
- RequestViewing
- CheckAssignedAgentAvailability

### Offer
- CreateOffer
- GetOffer
- ListBuyerOffers
- CancelOffer
- MarkOfferPropertyHeld
- MarkOfferAccepted
- MarkOfferRejected
- MarkOfferExpired
- MarkOfferFailed

Son beş use-case doğrudan public endpoint olmak zorunda değildir; Kafka event handler tarafından çağrılabilir.

## SellerService

### Seller
- CreateSeller
- GetSeller
- ChangeSellerStatus

### Listing Submission
- CreateListingSubmission
- SubmitListing
- MarkListingAccepted
- MarkListingRejected
- MarkPropertyCreated
- ListSellerSubmissions

### Offer Projection / Decision
- ProjectPendingOffer
- ListPendingOffers
- AcceptOffer
- RejectOffer
- RemovePendingOffer
- RecordOfferHistory

### Activity
- RecordSellerActivity
- GetSellerActivity

## PropertyService

### Property lifecycle
- CreatePropertyFromListingCommand
- GetProperty
- UpdatePropertyDetails
- PublishProperty
- ChangePropertyPrice
- AssignAgent
- WithdrawProperty

### Offer / Saga
- HoldPropertyForOffer
- ReleasePropertyHold
- ReserveProperty
- MarkPropertySold

### Internal reliability
- DeduplicateListingCommand
- RecordProcessedEvent

## SearchService

- IndexPublishedProperty
- UpdatePropertyProjection
- UpdatePriceProjection
- UpdateStatusProjection
- DeletePropertyProjection
- SearchProperties
- Autocomplete
- FacetSearch
- GeoSearch
- ReindexProperty
- RebuildIndex
- ReconcileIndex

## ApiGatewayService

Business use-case içermez. Cross-cutting policy sağlar:

- RouteRequest
- AuthenticateRequest
- ApplyRateLimit
- PropagateCorrelationContext
- ApplyResiliencePolicy

## Kural

Bir use-case yalnızca gerçek business/application responsibility varsa oluşturulur. CRUD method'larını kör biçimde çoğaltmak yerine domain davranışı isimlendirilir.
