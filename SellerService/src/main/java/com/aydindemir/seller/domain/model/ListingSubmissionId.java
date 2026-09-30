package com.aydindemir.seller.domain.model;

import java.util.Objects;
import java.util.UUID;

public record ListingSubmissionId(UUID value) {

    public ListingSubmissionId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static ListingSubmissionId of(UUID value) {
        return new ListingSubmissionId(value);
    }

    public static ListingSubmissionId from(String value) {
        Objects.requireNonNull(value, "value must not be null");
        return new ListingSubmissionId(UUID.fromString(value));
    }

    public static ListingSubmissionId newId() {
        return new ListingSubmissionId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
