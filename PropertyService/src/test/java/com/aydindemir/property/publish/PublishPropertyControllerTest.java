package com.aydindemir.property.publish;

import com.aydindemir.property.shared.domain.exception.InvalidPropertyStateException;
import com.aydindemir.property.shared.domain.exception.PropertyNotFoundException;
import com.aydindemir.property.shared.web.error.PropertyExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = PublishPropertyController.class,
        properties = {
                "spring.cloud.config.enabled=false",
                "spring.config.import=optional:classpath:/property-test.properties",
                "eureka.client.enabled=false"
        }
)
@Import(PropertyExceptionHandler.class)
class PublishPropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PublishPropertyHandler handler;

    @Test
    void returns200WhenDraftIsPublished() throws Exception {
        String propertyId = UUID.randomUUID().toString();
        Instant now = Instant.parse("2026-10-02T00:00:00Z");
        when(handler.handle(any())).thenReturn(
                new PublishPropertyResult(propertyId, "PUBLISHED", now, now, 1L));

        mockMvc.perform(post("/properties/{propertyId}/publish", propertyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"))
                .andExpect(jsonPath("$.version").value(1));
    }

    @Test
    void returns404WhenPropertyDoesNotExist() throws Exception {
        String propertyId = UUID.randomUUID().toString();
        when(handler.handle(any())).thenThrow(new PropertyNotFoundException(propertyId));

        mockMvc.perform(post("/properties/{propertyId}/publish", propertyId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROPERTY_NOT_FOUND"));
    }

    @Test
    void returns409WhenPropertyCannotBeRepublished() throws Exception {
        String propertyId = UUID.randomUUID().toString();
        when(handler.handle(any())).thenThrow(new InvalidPropertyStateException("Only a DRAFT property can be published"));

        mockMvc.perform(post("/properties/{propertyId}/publish", propertyId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INVALID_PROPERTY_STATE"));
    }

    @Test
    void returns400ForMalformedPropertyId() throws Exception {
        mockMvc.perform(post("/properties/not-a-uuid/publish"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"));
    }
}
