package com.aydindemir.seller.domain.repository;

public record ListingSubmissionPageRequest(
        int pageSize,
        String pageState) {

    public ListingSubmissionPageRequest {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize must be greater than zero");
        }

        if (pageState != null && pageState.isBlank()) {
            pageState = null;
        }
    }

    public static ListingSubmissionPageRequest firstPage(int pageSize) {
        return new ListingSubmissionPageRequest(pageSize, null);
    }
}
