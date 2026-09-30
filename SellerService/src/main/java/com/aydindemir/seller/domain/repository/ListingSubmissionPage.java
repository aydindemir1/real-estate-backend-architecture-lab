package com.aydindemir.seller.domain.repository;

import com.aydindemir.seller.domain.model.ListingSubmission;

import java.util.List;
import java.util.Objects;

public record ListingSubmissionPage(
        List<ListingSubmission> items,
        String nextPageState) {

    public ListingSubmissionPage {
        items = List.copyOf(Objects.requireNonNull(items, "items must not be null"));

        if (nextPageState != null && nextPageState.isBlank()) {
            nextPageState = null;
        }
    }
}
