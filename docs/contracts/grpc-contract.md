# gRPC Contract — BuyerService -> AgentService

## Amaç

BuyerService'in viewing flow sırasında AgentService'ten synchronous availability bilgisi almasını sağlamak.

## Service

`AgentAvailabilityService`

## RPC

### CheckAvailability

Request:
- agentId
- requestedAt
- correlationId

Response:
- agentId
- availability
- checkedAt

Availability:
- AVAILABLE
- UNAVAILABLE

## Proto taslak

```proto
service AgentAvailabilityService {
  rpc CheckAvailability(CheckAvailabilityRequest)
      returns (CheckAvailabilityResponse);
}

message CheckAvailabilityRequest {
  string agent_id = 1;
  string requested_at = 2;
  string correlation_id = 3;
}

message CheckAvailabilityResponse {
  string agent_id = 1;
  Availability availability = 2;
  string checked_at = 3;
}

enum Availability {
  AVAILABILITY_UNSPECIFIED = 0;
  AVAILABLE = 1;
  UNAVAILABLE = 2;
}
```

## Rule'lar

- SUSPENDED veya INACTIVE Agent her zaman UNAVAILABLE kabul edilir.
- Deadline client tarafından belirlenmelidir.
- Timeout durumunda BuyerService indefinite wait yapmamalıdır.
- gRPC call trace context taşımalıdır.
- Authentication Day 8 sonrasında service-to-service credential ile korunmalıdır.
