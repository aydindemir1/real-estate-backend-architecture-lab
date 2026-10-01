package com.aydindemir.property.getbyid;

import com.aydindemir.property.shared.domain.exception.PropertyNotFoundException;
import com.aydindemir.property.shared.web.error.PropertyExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = GetPropertyController.class,
        properties = {
                "spring.cloud.config.enabled=false",
                "spring.config.import=optional:classpath:/property-test.properties",
                "eureka.client.enabled=false"
        }
)
@Import(PropertyExceptionHandler.class)
class GetPropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetPropertyHandler handler;

    @Test
    void returns200ForExistingProperty() throws Exception {
        String propertyId = UUID.randomUUID().toString();
        when(handler.handle(any())).thenReturn(result(propertyId));

        mockMvc.perform(get("/properties/{propertyId}", propertyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.propertyId").value(propertyId))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void returns404ForMissingProperty() throws Exception {
        String propertyId = UUID.randomUUID().toString();
        when(handler.handle(any())).thenThrow(new PropertyNotFoundException(propertyId));

        mockMvc.perform(get("/properties/{propertyId}", propertyId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROPERTY_NOT_FOUND"));
    }

    @Test
    void returns400ForMalformedPropertyId() throws Exception {
        mockMvc.perform(get("/properties/not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"));
    }

    private GetPropertyResult result(String propertyId) {
        Instant now = Instant.parse("2026-10-02T00:00:00Z");
        return new GetPropertyResult(
                propertyId,
                UUID.randomUUID().toString(),
                null,
                "Kadikoy apartment",
                "Description",
                "APARTMENT",
                new GetPropertyResult.AddressResult("Istanbul", "Kadikoy", "Example Street 10"),
                null,
                new BigDecimal("6500000"),
                "TRY",
                new BigDecimal("125.50"),
                3,
                Set.of("BALCONY"),
                "DRAFT",
                now,
                now,
                null,
                0L);
    }
}
