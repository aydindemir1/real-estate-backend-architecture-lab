package com.aydindemir.buyer.adapter.in.rest;

import com.aydindemir.buyer.adapter.in.rest.error.BuyerApiExceptionHandler;
import com.aydindemir.buyer.adapter.in.rest.mapper.BuyerPreferencesRestMapper;
import com.aydindemir.buyer.application.exception.BuyerPreferencesNotFoundException;
import com.aydindemir.buyer.application.port.in.AddSavedSearchUseCase;
import com.aydindemir.buyer.application.port.in.GetBuyerPreferencesUseCase;
import com.aydindemir.buyer.application.port.in.UpdateBuyerPreferencesUseCase;
import com.aydindemir.buyer.application.service.BuyerPreferencesResult;
import com.aydindemir.buyer.domain.model.AreaRange;
import com.aydindemir.buyer.domain.model.BuyerId;
import com.aydindemir.buyer.domain.model.LocationPreference;
import com.aydindemir.buyer.domain.model.NotificationSettings;
import com.aydindemir.buyer.domain.model.PriceRange;
import com.aydindemir.buyer.domain.model.RoomRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = BuyerPreferencesController.class,
        properties = {
                "spring.cloud.config.enabled=false",
                "spring.config.import=optional:classpath:/buyer-test.properties",
                "eureka.client.enabled=false"
        }
)
@Import({BuyerPreferencesRestMapper.class, BuyerApiExceptionHandler.class})
class BuyerPreferencesControllerTest {

    private static final Instant NOW = Instant.parse("2026-09-29T17:30:00Z");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UpdateBuyerPreferencesUseCase updateUseCase;

    @MockitoBean
    private GetBuyerPreferencesUseCase getUseCase;

    @MockitoBean
    private AddSavedSearchUseCase addSavedSearchUseCase;

    @MockitoBean
    private Clock clock;

    @BeforeEach
    void configureClock() {
        when(clock.instant()).thenReturn(NOW);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
    }

    @Test
    void putPreferencesShouldReturn200() throws Exception {
        UUID buyerId = UUID.randomUUID();
        when(updateUseCase.update(any())).thenReturn(result(buyerId));

        mockMvc.perform(put("/buyers/{buyerId}/preferences", buyerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPreferencesRequest()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buyerId").value(buyerId.toString()))
                .andExpect(jsonPath("$.priceRange.currency").value("TRY"));
    }

    @Test
    void getPreferencesShouldReturn200() throws Exception {
        UUID buyerId = UUID.randomUUID();
        when(getUseCase.get(any())).thenReturn(result(buyerId));

        mockMvc.perform(get("/buyers/{buyerId}/preferences", buyerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buyerId").value(buyerId.toString()));
    }

    @Test
    void getPreferencesShouldReturn404WhenMissing() throws Exception {
        UUID buyerId = UUID.randomUUID();
        when(getUseCase.get(any()))
                .thenThrow(new BuyerPreferencesNotFoundException(BuyerId.of(buyerId)));

        mockMvc.perform(get("/buyers/{buyerId}/preferences", buyerId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("BUYER_PREFERENCES_NOT_FOUND"));
    }

    @Test
    void putPreferencesShouldReturn400ForSyntacticallyInvalidRequest() throws Exception {
        UUID buyerId = UUID.randomUUID();

        mockMvc.perform(put("/buyers/{buyerId}/preferences", buyerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "minPrice": -1,
                                  "maxPrice": 5000000,
                                  "currency": "",
                                  "notificationSettings": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void putPreferencesShouldReturn422ForSemanticRangeViolation() throws Exception {
        UUID buyerId = UUID.randomUUID();

        mockMvc.perform(put("/buyers/{buyerId}/preferences", buyerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "minPrice": 5000000,
                                  "maxPrice": 1000000,
                                  "currency": "TRY",
                                  "preferredLocations": [],
                                  "propertyTypes": ["APARTMENT"],
                                  "minRooms": 1,
                                  "maxRooms": 3,
                                  "minArea": 70,
                                  "maxArea": 140,
                                  "preferredFeatures": ["BALCONY"],
                                  "notificationSettings": {
                                    "emailEnabled": true,
                                    "pushEnabled": false,
                                    "smsEnabled": false
                                  }
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("INVALID_BUYER_PREFERENCES"));
    }

    @Test
    void postSavedSearchShouldReturn201() throws Exception {
        UUID buyerId = UUID.randomUUID();
        when(addSavedSearchUseCase.add(any())).thenReturn(result(buyerId));

        mockMvc.perform(post("/buyers/{buyerId}/saved-searches", buyerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Kadikoy apartments",
                                  "minPrice": 1500000,
                                  "maxPrice": 4500000,
                                  "currency": "TRY",
                                  "locations": [
                                    {
                                      "city": "Istanbul",
                                      "district": "Kadikoy"
                                    }
                                  ],
                                  "propertyTypes": ["APARTMENT"],
                                  "minRooms": 1,
                                  "maxRooms": 3,
                                  "minArea": 70,
                                  "maxArea": 140,
                                  "preferredFeatures": ["BALCONY"]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.buyerId").value(buyerId.toString()));
    }

    private BuyerPreferencesResult result(UUID buyerId) {
        return new BuyerPreferencesResult(
                BuyerId.of(buyerId),
                new PriceRange(
                        new BigDecimal("1000000"),
                        new BigDecimal("5000000"),
                        Currency.getInstance("TRY")
                ),
                List.of(new LocationPreference("Istanbul", "Kadikoy")),
                Set.of("APARTMENT"),
                new RoomRange(1, 3),
                new AreaRange(new BigDecimal("70"), new BigDecimal("140")),
                Set.of("BALCONY"),
                new NotificationSettings(true, false, false),
                List.of(),
                NOW,
                NOW
        );
    }

    private String validPreferencesRequest() {
        return """
                {
                  "minPrice": 1000000,
                  "maxPrice": 5000000,
                  "currency": "TRY",
                  "preferredLocations": [
                    {
                      "city": "Istanbul",
                      "district": "Kadikoy"
                    }
                  ],
                  "propertyTypes": ["APARTMENT"],
                  "minRooms": 1,
                  "maxRooms": 3,
                  "minArea": 70,
                  "maxArea": 140,
                  "preferredFeatures": ["BALCONY"],
                  "notificationSettings": {
                    "emailEnabled": true,
                    "pushEnabled": false,
                    "smsEnabled": false
                  }
                }
                """;
    }
}
