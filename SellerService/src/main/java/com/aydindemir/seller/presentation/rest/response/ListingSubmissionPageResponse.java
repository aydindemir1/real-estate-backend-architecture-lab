package com.aydindemir.seller.presentation.rest.response;

import java.util.List;

public record ListingSubmissionPageResponse(
        List<ListingSubmissionResponse> items,
        String nextPageState) {

    public ListingSubmissionPageResponse {
        items = List.copyOf(items);
    }
}
