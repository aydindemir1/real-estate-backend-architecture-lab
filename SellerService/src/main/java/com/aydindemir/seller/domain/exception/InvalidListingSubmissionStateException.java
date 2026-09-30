package com.aydindemir.seller.domain.exception;

import com.aydindemir.seller.domain.model.ListingSubmissionId;
import com.aydindemir.seller.domain.model.ListingSubmissionStatus;

public class InvalidListingSubmissionStateException extends RuntimeException {

    public InvalidListingSubmissionStateException(
            ListingSubmissionId submissionId,
            ListingSubmissionStatus currentStatus,
            ListingSubmissionStatus targetStatus) {
        super("Invalid listing submission state transition for "
                + submissionId
                + ": "
                + currentStatus
                + " -> "
                + targetStatus);
    }
}
