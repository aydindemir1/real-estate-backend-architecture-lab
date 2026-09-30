package com.aydindemir.seller.application.command;

import com.aydindemir.seller.domain.model.ListingSubmissionId;
import com.aydindemir.seller.domain.model.SellerId;

import java.util.Objects;

public record SubmitListingCommand(
        SellerId sellerId,
        ListingSubmissionId submissionId) {

    public SubmitListingCommand {
        Objects.requireNonNull(sellerId, "sellerId must not be null");
        Objects.requireNonNull(submissionId, "submissionId must not be null");
    }
}
