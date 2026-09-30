package com.aydindemir.seller.application.service;

import com.aydindemir.seller.domain.repository.ListingSubmissionPage;

import java.util.List;
import java.util.Objects;

public record ListingSubmissionPageResult(
        List<ListingSubmissionResult> items,
        String nextPageState) {

    public ListingSubmissionPageResult {
        items = List.copyOf(Objects.requireNonNull(items, "items must not be null"));

        if (nextPageState != null && nextPageState.isBlank()) {
            nextPageState = null;
        }
    }

    public static ListingSubmissionPageResult from(ListingSubmissionPage page) {
        Objects.requireNonNull(page, "page must not be null");

        return new ListingSubmissionPageResult(
                page.items().stream()
                        .map(ListingSubmissionResult::from)
                        .toList(),
                page.nextPageState());
    }
}
