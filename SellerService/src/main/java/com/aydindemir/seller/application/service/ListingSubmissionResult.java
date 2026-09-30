package com.aydindemir.seller.application.service;

import com.aydindemir.seller.domain.model.ListingSubmission;
import com.aydindemir.seller.domain.model.ListingSubmissionId;
import com.aydindemir.seller.domain.model.ListingSubmissionStatus;
import com.aydindemir.seller.domain.model.PropertyDraftData;
import com.aydindemir.seller.domain.model.SellerId;

import java.time.Instant;
import java.util.Objects;

public record ListingSubmissionResult(
        ListingSubmissionId submissionId,
        SellerId sellerId,
        PropertyDraftData propertyDraftData,
        ListingSubmissionStatus status,
        Instant createdAt,
        Instant updatedAt) {

    public ListingSubmissionResult {
        Objects.requireNonNull(submissionId, "submissionId must not be null");
        Objects.requireNonNull(sellerId, "sellerId must not be null");
        Objects.requireNonNull(propertyDraftData, "propertyDraftData must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    public static ListingSubmissionResult from(ListingSubmission submission) {
        Objects.requireNonNull(submission, "submission must not be null");
        return new ListingSubmissionResult(
                submission.submissionId(),
                submission.sellerId(),
                submission.propertyDraftData(),
                submission.status(),
                submission.createdAt(),
                submission.updatedAt());
    }
}
