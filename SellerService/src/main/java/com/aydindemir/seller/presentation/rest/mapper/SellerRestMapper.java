package com.aydindemir.seller.presentation.rest.mapper;

import com.aydindemir.seller.application.command.CreateSellerCommand;
import com.aydindemir.seller.application.service.SellerResult;
import com.aydindemir.seller.domain.model.UserId;
import com.aydindemir.seller.presentation.rest.request.CreateSellerRequest;
import com.aydindemir.seller.presentation.rest.response.SellerResponse;

import java.util.Objects;

public final class SellerRestMapper {

    public CreateSellerCommand toCommand(CreateSellerRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        return new CreateSellerCommand(
                UserId.of(request.userId()),
                request.displayName());
    }

    public SellerResponse toResponse(SellerResult result) {
        Objects.requireNonNull(result, "result must not be null");

        return new SellerResponse(
                result.sellerId().value(),
                result.userId().value(),
                result.displayName(),
                result.status().name(),
                result.createdAt(),
                result.updatedAt());
    }
}
