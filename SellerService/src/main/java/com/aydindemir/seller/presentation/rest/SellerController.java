package com.aydindemir.seller.presentation.rest;

import com.aydindemir.seller.application.query.GetSellerQuery;
import com.aydindemir.seller.application.service.SellerApplicationService;
import com.aydindemir.seller.domain.model.SellerId;
import com.aydindemir.seller.presentation.rest.mapper.SellerRestMapper;
import com.aydindemir.seller.presentation.rest.request.CreateSellerRequest;
import com.aydindemir.seller.presentation.rest.response.SellerResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/sellers")
public class SellerController {

    private final SellerApplicationService sellerApplicationService;
    private final SellerRestMapper sellerRestMapper;

    public SellerController(
            SellerApplicationService sellerApplicationService,
            SellerRestMapper sellerRestMapper) {
        this.sellerApplicationService = sellerApplicationService;
        this.sellerRestMapper = sellerRestMapper;
    }

    @PostMapping
    public ResponseEntity<SellerResponse> createSeller(
            @Valid @RequestBody CreateSellerRequest request) {
        SellerResponse response = sellerRestMapper.toResponse(
                sellerApplicationService.createSeller(sellerRestMapper.toCommand(request)));

        return ResponseEntity
                .created(URI.create("/sellers/" + response.sellerId()))
                .body(response);
    }

    @GetMapping("/{sellerId}")
    public ResponseEntity<SellerResponse> getSeller(
            @PathVariable("sellerId") UUID sellerId) {
        SellerResponse response = sellerRestMapper.toResponse(
                sellerApplicationService.getSeller(
                        new GetSellerQuery(SellerId.of(sellerId))));

        return ResponseEntity.ok(response);
    }
}
