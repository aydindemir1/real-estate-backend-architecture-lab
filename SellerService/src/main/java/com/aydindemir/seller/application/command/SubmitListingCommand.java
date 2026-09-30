package com.aydindemir.seller.application.command;

import com.aydindemir.seller.domain.model.ListingSubmissionId;
import com.aydindemir.seller.domain.model.SellerId;

import java.time.YearMonth;
import java.util.Objects;

public record SubmitListingCommand(
        SellerId sellerId,
        YearMonth yearMonth,
        ListingSubmissionId submissionId) {

    public SubmitListingCommand {
        Objects.requireNonNull(sellerId, "sellerId must not be null");
        Objects.requireNonNull(yearMonth, "yearMonth must not be null");
        Objects.requireNonNull(submissionId, "submissionId must not be null");
    }
}
