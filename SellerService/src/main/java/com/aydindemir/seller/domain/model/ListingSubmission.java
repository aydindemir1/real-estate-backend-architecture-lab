package com.aydindemir.seller.domain.model;

import com.aydindemir.seller.domain.exception.InvalidListingSubmissionStateException;

import java.time.Instant;
import java.util.Objects;

public final class ListingSubmission {

    private final ListingSubmissionId submissionId;
    private final SellerId sellerId;
    private final PropertyDraftData propertyDraftData;
    private ListingSubmissionStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private ListingSubmission(
            ListingSubmissionId submissionId,
            SellerId sellerId,
            PropertyDraftData propertyDraftData,
            ListingSubmissionStatus status,
            Instant createdAt,
            Instant updatedAt) {
        this.submissionId = Objects.requireNonNull(submissionId, "submissionId must not be null");
        this.sellerId = Objects.requireNonNull(sellerId, "sellerId must not be null");
        this.propertyDraftData = Objects.requireNonNull(propertyDraftData, "propertyDraftData must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    public static ListingSubmission create(SellerId sellerId, PropertyDraftData propertyDraftData) {
        Instant now = Instant.now();
        return new ListingSubmission(
                ListingSubmissionId.newId(),
                sellerId,
                propertyDraftData,
                ListingSubmissionStatus.CREATED,
                now,
                now);
    }

    public static ListingSubmission rehydrate(
            ListingSubmissionId submissionId,
            SellerId sellerId,
            PropertyDraftData propertyDraftData,
            ListingSubmissionStatus status,
            Instant createdAt,
            Instant updatedAt) {
        return new ListingSubmission(
                submissionId,
                sellerId,
                propertyDraftData,
                status,
                createdAt,
                updatedAt);
    }

    public void submit() {
        transitionFromCreatedTo(ListingSubmissionStatus.SUBMITTED);
    }

    public void markPropertyCreated() {
        transitionFromSubmittedTo(ListingSubmissionStatus.PROPERTY_CREATED);
    }

    public void reject() {
        transitionFromSubmittedTo(ListingSubmissionStatus.REJECTED);
    }

    public void fail() {
        transitionFromSubmittedTo(ListingSubmissionStatus.FAILED);
    }

    public ListingSubmissionId submissionId() {
        return submissionId;
    }

    public SellerId sellerId() {
        return sellerId;
    }

    public PropertyDraftData propertyDraftData() {
        return propertyDraftData;
    }

    public ListingSubmissionStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    private void transitionFromCreatedTo(ListingSubmissionStatus targetStatus) {
        if (status != ListingSubmissionStatus.CREATED) {
            throw invalidTransition(targetStatus);
        }

        transitionTo(targetStatus);
    }

    private void transitionFromSubmittedTo(ListingSubmissionStatus targetStatus) {
        if (status != ListingSubmissionStatus.SUBMITTED) {
            throw invalidTransition(targetStatus);
        }

        transitionTo(targetStatus);
    }

    private void transitionTo(ListingSubmissionStatus targetStatus) {
        status = targetStatus;
        updatedAt = Instant.now();
    }

    private InvalidListingSubmissionStateException invalidTransition(ListingSubmissionStatus targetStatus) {
        return new InvalidListingSubmissionStateException(submissionId, status, targetStatus);
    }
}
