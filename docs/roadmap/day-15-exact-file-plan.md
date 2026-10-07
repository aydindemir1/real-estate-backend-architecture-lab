# Day 15 — Kesin gRPC Internal Communication Planı

## Kapsam

- AgentAvailability protobuf contract
- AgentService gRPC server adapter
- BuyerService outbound port ve client adapter
- deadline/auth/status mapping
- unit ve integration testleri

## Task 1 — Protocol boundary denetimi

Doğrula:
- REST public/business API baseline olarak kalır
- gRPC yalnızca internal Agent availability use-case için kullanılır
- GraphQL yalnızca flexible read/search use-case için kullanılır

Gerekirse gerekçeyi communication architecture dokümanına ekle.

Commit: docs(protocol): confirm REST gRPC GraphQL boundaries

## Task 2 — gRPC build desteği

Yalnızca gerekliyse root/dependency governance'ı değiştir.

İlgili module'lere ekle:
- protobuf plugin
- protobuf-java
- grpc-stub
- grpc-protobuf
- yalnızca seçilmiş ve compatible ise grpc-spring integration library

Module'ler:
- AgentService
- BuyerService

gRPC dependency'sini global olarak bütün service'lere ekleme.

Commit: build(grpc): add protobuf and gRPC support

## Task 3 — Proto source yerleşimi

Shared contract location adayı oluştur:
- contracts/grpc/agent-availability.proto

veya repository convention provider ownership tercih ediyorsa module-owned contract kullan.

Önerilen package:
- realestate.agent.v1

Java package seçeneği:
- com.aydindemir.contract.agent.v1

## Task 4 — AgentAvailabilityService proto

Tanımla:
- service AgentAvailabilityService
- rpc CheckAvailability(CheckAvailabilityRequest) returns (CheckAvailabilityResponse)

Request alanları:
- string agent_id
- yalnızca mevcut business use-case gerçekten gerektiriyorsa optional requested_time

Response alanları:
- bool available
- string status
- optional reason_code

Compatibility kuralları:
- field number'lar asla yeniden kullanılmaz
- additive change tercih edilir
- domain entity dump yapılmaz

Commit: feat(contract): add AgentAvailability gRPC contract

## Task 5 — Proto generation configuration

Generated source directory'lerini configure et.

Generated code'un build output olduğundan ve manuel olarak düzenlenmediğinden emin ol.

Doğrula:
- AgentService compile
- BuyerService compile

Commit: build(grpc): configure protobuf code generation

## Task 6 — AgentService application query/use-case

Henüz temsil edilmiyorsa oluştur:
- application/query/CheckAgentAvailabilityQuery.java
- application/result/AgentAvailabilityResult.java
- application/usecase/CheckAgentAvailabilityUseCase.java

Implementation, AgentApplicationService'i yeniden kullanabilir veya focused query service kullanabilir.

Kural:
gRPC adapter persistence'a doğrudan query atamaz.

Commit: feat(agent): add availability query use case

## Task 7 — AgentService gRPC server adapter

Mevcut Clean Architecture convention'a göre package oluştur:
- infrastructure/grpc/ veya presentation/grpc/

Tercih edilen:
- presentation/grpc/AgentAvailabilityGrpcService.java

Sorumluluklar:
- protobuf request -> application query mapping
- use-case çağırma
- result -> protobuf response mapping
- application exception -> gRPC status mapping

Adapter içinde business rule bulunmaz.

Commit: feat(agent): expose availability gRPC service

## Task 8 — gRPC status mapping

Map et:
- invalid id/input -> INVALID_ARGUMENT
- agent not found -> NOT_FOUND
- auth missing -> UNAUTHENTICATED
- forbidden -> PERMISSION_DENIED
- invalid/precondition state -> FAILED_PRECONDITION
- timeout -> uygun olduğunda DEADLINE_EXCEEDED
- temporary dependency failure -> UNAVAILABLE

Yararlıysa oluştur:
- AgentGrpcExceptionMapper.java

Commit: feat(agent): standardize gRPC error mapping

## Task 9 — gRPC server configuration

Configure et:
- library gerektiriyorsa HTTP'den ayrı port
- bounded max message size
- kullanılıyorsa reflection yalnızca local/dev
- auth/tracing interceptor'ları

Review edilmemiş unlimited default kullanma.

Commit: config(agent): configure gRPC server

## Task 10 — BuyerService outbound port

Oluştur:
- application/port/out/AgentAvailabilityPort.java

Method:
- AgentAvailabilityResult checkAvailability(AgentId or external id abstraction)

Protobuf type'larını expose etme.

Commit: feat(buyer): add AgentAvailability outbound port

## Task 11 — BuyerService gRPC client adapter

Oluştur:
- adapter/out/grpc/GrpcAgentAvailabilityAdapter.java

Sorumluluklar:
- protobuf request oluşturma
- deadline uygulama
- stub çağırma
- response mapping
- gRPC status'u application semantic'e çevirme

Commit: feat(buyer): add AgentAvailability gRPC adapter

## Task 12 — gRPC client configuration

Gerekirse oluştur/configure et:
- AgentGrpcClientProperties.java
- yalnızca library auto-config yetersizse GrpcClientConfiguration.java

External config:
- logical target/service name
- port
- deadline Duration

Temiz şekilde destekleniyorsa service discovery integration tercih et; aksi halde learning environment için explicit local target kullan.

Commit: config(buyer): configure Agent gRPC client

## Task 13 — gRPC deadline

Her call explicit deadline kullanır.

Infinite default'a güvenme.

Örnek target adapter içinde hard-code edilmez; configuration üzerinden alınır.

Commit client adapter/config ile birleştirilebilir.

## Task 14 — gRPC authentication

Day 14 Keycloak foundation yeniden kullanılır.

Internal call mode'a karar ver:
- user context gerekiyorsa propagated user token
- machine identity uygunsa service Client Credentials

Availability lookup için business authorization end-user context gerektirmiyorsa service identity + gerekli scope tercih edilir.

Yalnızca framework desteği bunu zaten karşılamıyorsa client interceptor/token supplier oluştur.

Commit: feat(grpc): secure Agent availability calls

## Task 15 — gRPC tracing/context propagation

Şunlardan emin ol:
- trace context propagate edilir
- metadata üzerinden destekleniyorsa correlationId propagate edilir

Custom tracing stack oluşturma; mevcut Micrometer/OTel-compatible hook'ları kullan.

Kod gerekiyorsa commit:
feat(grpc): propagate tracing context

## Task 16 — Buyer application integration

Yalnızca mevcut bir Buyer flow buna ihtiyaç duyuyorsa port'u kullanan minimal use-case ekle.

Aday:
- CheckAssignedAgentAvailability

Tam viewing scheduler uydurma.

Gerekirse oluştur:
- application/port/in/CheckAssignedAgentAvailabilityUseCase.java
- corresponding command/query/result

Commit: feat(buyer): add agent availability application flow

## Task 17 — gRPC unit testleri

Agent tarafı:
- AgentAvailabilityGrpcServiceTest.java

Senaryolar:
- available
- unavailable
- not found mapping
- invalid request mapping

Buyer tarafı:
- GrpcAgentAvailabilityAdapterTest.java

Senaryolar:
- success mapping
- DEADLINE_EXCEEDED translation
- NOT_FOUND translation
- UNAVAILABLE translation

Commit: test(grpc): add adapter unit tests

## Task 18 — gRPC integration test

Oluştur:
- AgentAvailabilityGrpcIntegrationTest.java

Test framework'e göre gerçek in-process veya containerized/local server çalıştır.

Doğrula:
- protobuf serialization
- server/client compatibility
- deadline
- uygunsa auth metadata

Commit: test(grpc): add availability integration test

## Source-of-truth notu

Bu dosya final Day 15–33 roadmap'i izler. Önceki birleşik Day numaralandırması `docs/roadmap/LEGACY-DAY-MAPPING.md` ile superseded edilmiştir.
