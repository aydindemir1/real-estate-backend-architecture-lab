package com.aydindemir.agent.presentation.rest;

import com.aydindemir.agent.application.query.GetAgentQuery;
import com.aydindemir.agent.application.result.AgentResult;
import com.aydindemir.agent.application.usecase.ChangeAvailabilityUseCase;
import com.aydindemir.agent.application.usecase.CreateAgentUseCase;
import com.aydindemir.agent.application.usecase.GetAgentUseCase;
import com.aydindemir.agent.presentation.rest.mapper.AgentRestMapper;
import com.aydindemir.agent.presentation.rest.request.ChangeAvailabilityRequest;
import com.aydindemir.agent.presentation.rest.request.CreateAgentRequest;
import com.aydindemir.agent.presentation.rest.response.AgentResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/agents")
public class AgentController {

    private final CreateAgentUseCase createAgentUseCase;
    private final GetAgentUseCase getAgentUseCase;
    private final ChangeAvailabilityUseCase changeAvailabilityUseCase;
    private final AgentRestMapper mapper;

    public AgentController(
            CreateAgentUseCase createAgentUseCase,
            GetAgentUseCase getAgentUseCase,
            ChangeAvailabilityUseCase changeAvailabilityUseCase,
            AgentRestMapper mapper
    ) {
        this.createAgentUseCase = createAgentUseCase;
        this.getAgentUseCase = getAgentUseCase;
        this.changeAvailabilityUseCase = changeAvailabilityUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<AgentResponse> createAgent(
            @Valid @RequestBody CreateAgentRequest request
    ) {
        AgentResult result = createAgentUseCase.createAgent(mapper.toCommand(request));
        AgentResponse response = mapper.toResponse(result);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{agentId}")
                .buildAndExpand(response.agentId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{agentId}")
    public ResponseEntity<AgentResponse> getAgent(
            @PathVariable("agentId") UUID agentId
    ) {
        AgentResult result = getAgentUseCase.getAgent(new GetAgentQuery(agentId));
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @PatchMapping("/{agentId}/availability")
    public ResponseEntity<AgentResponse> changeAvailability(
            @PathVariable UUID agentId,
            @Valid @RequestBody ChangeAvailabilityRequest request
    ) {
        AgentResult result = changeAvailabilityUseCase.changeAvailability(
                mapper.toCommand(agentId, request)
        );

        return ResponseEntity.ok(mapper.toResponse(result));
    }
}
