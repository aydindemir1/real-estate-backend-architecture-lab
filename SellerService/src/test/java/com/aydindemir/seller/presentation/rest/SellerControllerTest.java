package com.aydindemir.seller.presentation.rest;

import com.aydindemir.seller.application.command.CreateSellerCommand;
import com.aydindemir.seller.application.query.GetSellerQuery;
import com.aydindemir.seller.application.service.SellerApplicationService;
import com.aydindemir.seller.application.service.SellerResult;
import com.aydindemir.seller.domain.exception.SellerNotFoundException;
import com.aydindemir.seller.domain.model.SellerId;
import com.aydindemir.seller.domain.model.SellerStatus;
import com.aydindemir.seller.domain.model.UserId;
import com.aydindemir.seller.presentation.rest.mapper.SellerRestMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = SellerController.class,
        properties = {
                "spring.cloud.config.enabled=false",
                "spring.config.import=optional:configserver:",
                "eureka.client.enabled=false"
        }
)
@Import({SellerRestMapper.class, SellerApiExceptionHandler.class})
class SellerControllerTest {

    private static final UUID SELLER_ID =
            UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID USER_ID =
            UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final Instant CREATED_AT =
            Instant.parse("2026-09-30T18:00:00Z");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SellerApplicationService sellerApplicationService;

    @Test
    void createSellerShouldReturn201WithLocation() throws Exception {
        when(sellerApplicationService.createSeller(any(CreateSellerCommand.class)))
                .thenReturn(sellerResult());

        mockMvc.perform(post("/sellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "22222222-2222-2222-2222-222222222222",
                                  "displayName": "Aydın Demir"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/sellers/" + SELLER_ID))
                .andExpect(jsonPath("$.sellerId").value(SELLER_ID.toString()))
                .andExpect(jsonPath("$.userId").value(USER_ID.toString()))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void getSellerShouldReturn200() throws Exception {
        when(sellerApplicationService.getSeller(any(GetSellerQuery.class)))
                .thenReturn(sellerResult());

        mockMvc.perform(get("/sellers/{sellerId}", SELLER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellerId").value(SELLER_ID.toString()))
                .andExpect(jsonPath("$.displayName").value("Aydın Demir"));
    }

    @Test
    void getSellerShouldReturn404WhenMissing() throws Exception {
        when(sellerApplicationService.getSeller(any(GetSellerQuery.class)))
                .thenThrow(new SellerNotFoundException(SellerId.of(SELLER_ID)));

        mockMvc.perform(get("/sellers/{sellerId}", SELLER_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SELLER_NOT_FOUND"));
    }

    @Test
    void createSellerShouldReturn400ForInvalidRequest() throws Exception {
        mockMvc.perform(post("/sellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": null,
                                  "displayName": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    private SellerResult sellerResult() {
        return new SellerResult(
                SellerId.of(SELLER_ID),
                UserId.of(USER_ID),
                "Aydın Demir",
                SellerStatus.ACTIVE,
                CREATED_AT,
                CREATED_AT);
    }
}
