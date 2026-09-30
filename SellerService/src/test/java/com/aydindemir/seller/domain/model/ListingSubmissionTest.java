package com.aydindemir.seller.domain.model;

import com.aydindemir.seller.domain.exception.InvalidListingSubmissionStateException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ListingSubmissionTest {

    @Test
    void createdSubmissionCanBeSubmitted() {
        ListingSubmission submission = newSubmission();

        submission.submit();

        assertEquals(ListingSubmissionStatus.SUBMITTED, submission.status());
    }

    @Test
    void submittedSubmissionCannotBeSubmittedAgain() {
        ListingSubmission submission = newSubmission();
        submission.submit();

        assertThrows(InvalidListingSubmissionStateException.class, submission::submit);
    }

    @Test
    void submittedSubmissionCanBeMarkedPropertyCreated() {
        ListingSubmission submission = newSubmission();
        submission.submit();

        submission.markPropertyCreated();

        assertEquals(ListingSubmissionStatus.PROPERTY_CREATED, submission.status());
    }

    @Test
    void submittedSubmissionCanBeRejected() {
        ListingSubmission submission = newSubmission();
        submission.submit();

        submission.reject();

        assertEquals(ListingSubmissionStatus.REJECTED, submission.status());
    }

    @Test
    void submittedSubmissionCanFail() {
        ListingSubmission submission = newSubmission();
        submission.submit();

        submission.fail();

        assertEquals(ListingSubmissionStatus.FAILED, submission.status());
    }

    @Test
    void createdSubmissionCannotBeRejectedBeforeSubmit() {
        ListingSubmission submission = newSubmission();

        assertThrows(InvalidListingSubmissionStateException.class, submission::reject);
    }

    private ListingSubmission newSubmission() {
        return ListingSubmission.create(
                SellerId.newId(),
                new PropertyDraftData(
                        "Daire",
                        "Merkezi konum",
                        "APARTMENT",
                        "Kocaeli",
                        "Gebze",
                        "Örnek Mah. No:1",
                        new BigDecimal("3500000"),
                        "TRY",
                        new BigDecimal("120"),
                        3));
    }
}
