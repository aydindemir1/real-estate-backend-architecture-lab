package com.aydindemir.seller.domain.exception;

import com.aydindemir.seller.domain.model.ListingSubmissionId;

public class ListingSubmissionNotFoundException extends RuntimeException {

    public ListingSubmissionNotFoundException(ListingSubmissionId submissionId) {
        super("Listing submission not found: " + submissionId);
    }
}
