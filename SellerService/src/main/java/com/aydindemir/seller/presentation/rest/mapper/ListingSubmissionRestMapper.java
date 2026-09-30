package com.aydindemir.seller.presentation.rest.mapper;

import com.aydindemir.seller.application.command.CreateListingSubmissionCommand;
import com.aydindemir.seller.application.command.SubmitListingCommand;
import com.aydindemir.seller.application.service.ListingSubmissionPageResult;
import com.aydindemir.seller.application.service.ListingSubmissionResult;
import com.aydindemir.seller.domain.model.ListingSubmissionId;
import com.aydindemir.seller.domain.model.PropertyDraftData;
import com.aydindemir.seller.domain.model.SellerId;
import com.aydindemir.seller.presentation.rest.request.CreateListingSubmissionRequest;
import com.aydindemir.seller.presentation.rest.request.SubmitListingRequest;
import com.aydindemir.seller.presentation.rest.response.ListingSubmissionPageResponse;
import com.aydindemir.seller.presentation.rest.response.ListingSubmissionResponse;

import java.time.YearMonth;
import java.util.Objects;

public final class ListingSubmissionRestMapper {

    public CreateListingSubmissionCommand toCreateCommand(
            SellerId sellerId,
            CreateListingSubmissionRequest request) {
        Objects.requireNonNull(sellerId, "sellerId must not be null");
        Objects.requireNonNull(request, "request must not be null");

        PropertyDraftData draft = new PropertyDraftData(
                request.title(),
                request.description(),
                request.propertyType(),
                request.city(),
                request.district(),
                request.addressLine(),
                request.priceAmount(),
                request.currency(),
                request.area(),
                request.roomCount());

        return new CreateListingSubmissionCommand(sellerId, draft);
    }

    public SubmitListingCommand toSubmitCommand(
            SellerId sellerId,
            SubmitListingRequest request) {
        Objects.requireNonNull(sellerId, "sellerId must not be null");
        Objects.requireNonNull(request, "request must not be null");

        return new SubmitListingCommand(
                sellerId,
                YearMonth.parse(request.yearMonth()),
                request.createdAt(),
                ListingSubmissionId.of(request.submissionId()));
    }

    public ListingSubmissionResponse toResponse(ListingSubmissionResult result) {
        Objects.requireNonNull(result, "result must not be null");

        PropertyDraftData draft = result.propertyDraftData();

        return new ListingSubmissionResponse(
                result.submissionId().value(),
                result.sellerId().value(),
                result.status().name(),
                result.createdAt(),
                result.updatedAt(),
                draft.title(),
                draft.description(),
                draft.propertyType(),
                draft.city(),
                draft.district(),
                draft.addressLine(),
                draft.priceAmount(),
                draft.currency(),
                draft.area(),
                draft.roomCount());
    }

    public ListingSubmissionPageResponse toPageResponse(ListingSubmissionPageResult result) {
        Objects.requireNonNull(result, "result must not be null");

        return new ListingSubmissionPageResponse(
                result.items().stream()
                        .map(this::toResponse)
                        .toList(),
                result.nextPageState());
    }
}
