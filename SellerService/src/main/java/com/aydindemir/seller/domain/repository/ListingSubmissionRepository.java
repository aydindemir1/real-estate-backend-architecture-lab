package com.aydindemir.seller.domain.repository;

import com.aydindemir.seller.domain.model.ListingSubmission;
import com.aydindemir.seller.domain.model.ListingSubmissionId;
import com.aydindemir.seller.domain.model.SellerId;

import java.time.YearMonth;
import java.util.Optional;

public interface ListingSubmissionRepository {

    ListingSubmission save(ListingSubmission submission);

    Optional<ListingSubmission> findById(ListingSubmissionId submissionId);

    ListingSubmissionPage listBySellerAndMonth(
            SellerId sellerId,
            YearMonth yearMonth,
            ListingSubmissionPageRequest pageRequest);
}
