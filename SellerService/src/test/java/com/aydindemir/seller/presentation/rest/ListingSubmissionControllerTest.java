package com.aydindemir.seller.presentation.rest;

import com.aydindemir.seller.application.command.CreateListingSubmissionCommand;
import com.aydindemir.seller.application.command.SubmitListingCommand;
import com.aydindemir.seller.application.query.ListSellerSubmissionsQuery;
import com.aydindemir.seller.application.service.ListingSubmissionApplicationService;
import com.aydindemir.seller.application.service.ListingSubmissionPageResult;
import com.aydindemir.seller.application.service.ListingSubmissionResult;
import com.aydindemir.seller.domain.exception.InvalidListingSubmissionStateException;
import com.aydindemir.seller.domain.model.ListingSubmissionId;
import com.aydindemir.seller.domain.model.ListingSubmissionStatus;
import com.aydindemir.seller.domain.model.PropertyDraftData;
import com.aydindemir.seller.domain.model.SellerId;
import com.aydindemir.seller.presentation.rest.mapper.ListingSubmissionRestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ListingSubmissionController.class,
        properties = {
                "spring.cloud.config.enabled=false",
                "spring.config.import=optional:configserver:",
                "eureka.client.enabled=false"
        }
)
@Import({ListingSubmissionRestMapper.class, SellerApiExceptionHandler.class})
class ListingSubmissionControllerTest {

    private static final UUID SELLER_ID =
            UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID SUBMISSION_ID =
            UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final Instant CREATED_AT =
            Instant.parse("2026-09-30T18:00:00Z");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListingSubmissionApplicationService applicationService;

    @Test
    void createListingSubmissionShouldReturn201() throws Exception {
        when(applicationService.createListingSubmission(any(CreateListingSubmissionCommand.class)))
                .thenReturn(result(ListingSubmissionStatus.CREATED));

        mockMvc.perform(post("/sellers/{sellerId}/listing-submissions", SELLER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateRequest()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.submissionId").value(SUBMISSION_ID.toString()))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void submitListingShouldReturn200() throws Exception {
        when(applicationService.submitListing(any(SubmitListingCommand.class)))
                .thenReturn(result(ListingSubmissionStatus.SUBMITTED));

        mockMvc.perform(post(
                                "/sellers/{sellerId}/listing-submissions/{submissionId}/submit",
                                SELLER_ID,
                                SUBMISSION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "yearMonth": "2026-09",
                                  "createdAt": "2026-09-30T18:00:00Z",
                                  "submissionId": "33333333-3333-3333-3333-333333333333"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUBMITTED"));
    }

    @Test
    void submitListingShouldReturn409ForInvalidState() throws Exception {
        when(applicationService.submitListing(any(SubmitListingCommand.class)))
                .thenThrow(new InvalidListingSubmissionStateException(
                        ListingSubmissionId.of(SUBMISSION_ID),
                        ListingSubmissionStatus.SUBMITTED,
                        ListingSubmissionStatus.SUBMITTED));

        mockMvc.perform(post(
                                "/sellers/{sellerId}/listing-submissions/{submissionId}/submit",
                                SELLER_ID,
                                SUBMISSION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "yearMonth": "2026-09",
                                  "createdAt": "2026-09-30T18:00:00Z",
                                  "submissionId": "33333333-3333-3333-3333-333333333333"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INVALID_LISTING_SUBMISSION_STATE"));
    }

    @Test
    void listSellerSubmissionsShouldReturn200() throws Exception {
        when(applicationService.listSellerSubmissions(any(ListSellerSubmissionsQuery.class)))
                .thenReturn(new ListingSubmissionPageResult(
                        List.of(result(ListingSubmissionStatus.CREATED)),
                        null));

        mockMvc.perform(get("/sellers/{sellerId}/listing-submissions", SELLER_ID)
                        .param("yearMonth", "2026-09")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].submissionId").value(SUBMISSION_ID.toString()))
                .andExpect(jsonPath("$.items[0].status").value("CREATED"));
    }

    @Test
    void listSellerSubmissionsShouldReturn400ForInvalidPageSize() throws Exception {
        mockMvc.perform(get("/sellers/{sellerId}/listing-submissions", SELLER_ID)
                        .param("yearMonth", "2026-09")
                        .param("pageSize", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void createListingSubmissionShouldReturn400ForInvalidDraft() throws Exception {
        mockMvc.perform(post("/sellers/{sellerId}/listing-submissions", SELLER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "",
                                  "description": "",
                                  "propertyType": "",
                                  "city": "",
                                  "district": "",
                                  "addressLine": "",
                                  "priceAmount": 0,
                                  "currency": "",
                                  "area": 0,
                                  "roomCount": -1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    private ListingSubmissionResult result(ListingSubmissionStatus status) {
        return new ListingSubmissionResult(
                ListingSubmissionId.of(SUBMISSION_ID),
                SellerId.of(SELLER_ID),
                new PropertyDraftData(
                        "Daire",
                        "Merkezi konum",
                        "APARTMENT",
                        "Kocaeli",
                        "Gebze",
                        "Örnek Mah. No:1",
                        new BigDecimal("3500000"),
                        "TRY",
                        new BigDecimal("120"),
                        3),
                status,
                CREATED_AT,
                CREATED_AT);
    }

    private String validCreateRequest() {
        return """
                {
                  "title": "Daire",
                  "description": "Merkezi konum",
                  "propertyType": "APARTMENT",
                  "city": "Kocaeli",
                  "district": "Gebze",
                  "addressLine": "Örnek Mah. No:1",
                  "priceAmount": 3500000,
                  "currency": "TRY",
                  "area": 120,
                  "roomCount": 3
                }
                """;
    }
}
