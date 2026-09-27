package com.aydindemir.agent.presentation.rest;

import com.aydindemir.agent.application.command.ChangeAvailabilityCommand;
import com.aydindemir.agent.application.command.CreateAgentCommand;
import com.aydindemir.agent.application.exception.AgentConcurrentUpdateException;
import com.aydindemir.agent.application.exception.AgentNotFoundException;
import com.aydindemir.agent.application.exception.DuplicateLicenseNumberException;
import com.aydindemir.agent.application.query.GetAgentQuery;
import com.aydindemir.agent.application.result.AgentResult;
import com.aydindemir.agent.application.usecase.ChangeAvailabilityUseCase;
import com.aydindemir.agent.application.usecase.CreateAgentUseCase;
import com.aydindemir.agent.application.usecase.GetAgentUseCase;
import com.aydindemir.agent.domain.exception.InvalidAgentStateException;
import com.aydindemir.agent.domain.model.AgentId;
import com.aydindemir.agent.domain.model.AgentStatus;
import com.aydindemir.agent.domain.model.AvailabilityStatus;
import com.aydindemir.agent.domain.model.LicenseNumber;
import com.aydindemir.agent.presentation.error.AgentExceptionHandler;
import com.aydindemir.agent.presentation.rest.mapper.AgentRestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AgentController.class)
@Import({AgentRestMapper.class, AgentExceptionHandler.class})
class AgentControllerTest {

    private static final UUID AGENT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final Instant CREATED_AT = Instant.parse("2026-09-27T18:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2026-09-27T18:05:00Z");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateAgentUseCase createAgentUseCase;

    @MockitoBean
    private GetAgentUseCase getAgentUseCase;

    @MockitoBean
    private ChangeAvailabilityUseCase changeAvailabilityUseCase;

    @Test
    void createAgentShouldReturnCreatedWithLocationAndResponse() throws Exception {
        when(createAgentUseCase.createAgent(any(CreateAgentCommand.class)))
                .thenReturn(agentResult(AvailabilityStatus.OFFLINE));

        mockMvc.perform(post("/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "22222222-2222-2222-2222-222222222222",
                                  "licenseNumber": "LIC-100",
                                  "agencyName": "North Realty",
                                  "agencyRegistrationNumber": "REG-100",
                                  "officePhone": "+90 555 111 22 33"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/agents/" + AGENT_ID))
                .andExpect(jsonPath("$.agentId").value(AGENT_ID.toString()))
                .andExpect(jsonPath("$.userId").value(USER_ID.toString()))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.availability").value("OFFLINE"))
                .andExpect(jsonPath("$.version").doesNotExist());
    }

    @Test
    void getAgentShouldReturnAgentResponse() throws Exception {
        when(getAgentUseCase.getAgent(new GetAgentQuery(AGENT_ID)))
                .thenReturn(agentResult(AvailabilityStatus.AVAILABLE));

        mockMvc.perform(get("/agents/{agentId}", AGENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agentId").value(AGENT_ID.toString()))
                .andExpect(jsonPath("$.availability").value("AVAILABLE"));
    }

    @Test
    void changeAvailabilityShouldReturnUpdatedAgent() throws Exception {
        when(changeAvailabilityUseCase.changeAvailability(
                new ChangeAvailabilityCommand(AGENT_ID, AvailabilityStatus.BUSY)
        )).thenReturn(agentResult(AvailabilityStatus.BUSY));

        mockMvc.perform(patch("/agents/{agentId}/availability", AGENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "availability": "BUSY"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agentId").value(AGENT_ID.toString()))
                .andExpect(jsonPath("$.availability").value("BUSY"));
    }

    @Test
    void createAgentShouldReturnValidationErrorForInvalidRequest() throws Exception {
        mockMvc.perform(post("/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "licenseNumber": " ",
                                  "agencyName": " "
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fields").isArray());
    }

    @Test
    void getAgentShouldReturnStableNotFoundError() throws Exception {
        when(getAgentUseCase.getAgent(new GetAgentQuery(AGENT_ID)))
                .thenThrow(new AgentNotFoundException(new AgentId(AGENT_ID)));

        mockMvc.perform(get("/agents/{agentId}", AGENT_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("AGENT_NOT_FOUND"))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"));
    }

    @Test
    void createAgentShouldReturnStableDuplicateLicenseError() throws Exception {
        when(createAgentUseCase.createAgent(any(CreateAgentCommand.class)))
                .thenThrow(new DuplicateLicenseNumberException(new LicenseNumber("LIC-100")));

        mockMvc.perform(post("/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "22222222-2222-2222-2222-222222222222",
                                  "licenseNumber": "LIC-100",
                                  "agencyName": "North Realty"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_LICENSE_NUMBER"))
                .andExpect(jsonPath("$.status").value("CONFLICT"));
    }

    @Test
    void changeAvailabilityShouldReturnStableInvalidStateError() throws Exception {
        when(changeAvailabilityUseCase.changeAvailability(
                new ChangeAvailabilityCommand(AGENT_ID, AvailabilityStatus.AVAILABLE)
        )).thenThrow(new InvalidAgentStateException("Only active agents can be available or busy"));

        mockMvc.perform(patch("/agents/{agentId}/availability", AGENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "availability": "AVAILABLE"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INVALID_AGENT_STATE"));
    }

    @Test
    void changeAvailabilityShouldReturnStableConcurrentUpdateError() throws Exception {
        when(changeAvailabilityUseCase.changeAvailability(
                new ChangeAvailabilityCommand(AGENT_ID, AvailabilityStatus.BUSY)
        )).thenThrow(new AgentConcurrentUpdateException(
                new AgentId(AGENT_ID),
                new RuntimeException("stale version")
        ));

        mockMvc.perform(patch("/agents/{agentId}/availability", AGENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "availability": "BUSY"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("AGENT_CONCURRENT_UPDATE"));
    }

    @Test
    void malformedAvailabilityShouldReturnStableMalformedBodyError() throws Exception {
        mockMvc.perform(patch("/agents/{agentId}/availability", AGENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "availability": "UNKNOWN"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MALFORMED_REQUEST_BODY"));
    }

    private AgentResult agentResult(AvailabilityStatus availability) {
        return new AgentResult(
                AGENT_ID,
                USER_ID,
                "LIC-100",
                "North Realty",
                "REG-100",
                "+90 555 111 22 33",
                AgentStatus.ACTIVE,
                availability,
                CREATED_AT,
                UPDATED_AT
        );
    }
}
