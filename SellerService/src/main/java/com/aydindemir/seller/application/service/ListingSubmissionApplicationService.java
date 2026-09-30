package com.aydindemir.seller.application.service;

import com.aydindemir.seller.application.command.CreateListingSubmissionCommand;
import com.aydindemir.seller.application.command.SubmitListingCommand;
import com.aydindemir.seller.application.query.ListSellerSubmissionsQuery;
import com.aydindemir.seller.domain.exception.ListingSubmissionNotFoundException;
import com.aydindemir.seller.domain.exception.SellerNotFoundException;
import com.aydindemir.seller.domain.model.ListingSubmission;
import com.aydindemir.seller.domain.model.Seller;
import com.aydindemir.seller.domain.repository.ListingSubmissionPage;
import com.aydindemir.seller.domain.repository.ListingSubmissionPageRequest;
import com.aydindemir.seller.domain.repository.ListingSubmissionRepository;
import com.aydindemir.seller.domain.repository.SellerRepository;

import java.util.Objects;

public final class ListingSubmissionApplicationService {

    private final SellerRepository sellerRepository;
    private final ListingSubmissionRepository listingSubmissionRepository;

    public ListingSubmissionApplicationService(
            SellerRepository sellerRepository,
            ListingSubmissionRepository listingSubmissionRepository) {
        this.sellerRepository = Objects.requireNonNull(sellerRepository, "sellerRepository must not be null");
        this.listingSubmissionRepository = Objects.requireNonNull(
                listingSubmissionRepository,
                "listingSubmissionRepository must not be null");
    }

    public ListingSubmissionResult createListingSubmission(CreateListingSubmissionCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        Seller seller = loadActiveSeller(command.sellerId());
        ListingSubmission submission = ListingSubmission.create(
                seller.sellerId(),
                command.propertyDraftData());

        ListingSubmission savedSubmission = listingSubmissionRepository.save(submission);
        return ListingSubmissionResult.from(savedSubmission);
    }

    public ListingSubmissionResult submitListing(SubmitListingCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        loadActiveSeller(command.sellerId());

        ListingSubmission submission = listingSubmissionRepository
                .findBySellerAndMonthAndId(
                        command.sellerId(),
                        command.yearMonth(),
                        command.createdAt(),
                        command.submissionId())
                .orElseThrow(() -> new ListingSubmissionNotFoundException(command.submissionId()));

        submission.submit();

        ListingSubmission savedSubmission = listingSubmissionRepository.save(submission);
        return ListingSubmissionResult.from(savedSubmission);
    }

    public ListingSubmissionPageResult listSellerSubmissions(ListSellerSubmissionsQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        ListingSubmissionPage page = listingSubmissionRepository.listBySellerAndMonth(
                query.sellerId(),
                query.yearMonth(),
                new ListingSubmissionPageRequest(query.pageSize(), query.pageState()));

        return ListingSubmissionPageResult.from(page);
    }

    private Seller loadActiveSeller(com.aydindemir.seller.domain.model.SellerId sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new SellerNotFoundException(sellerId));

        seller.assertCanSubmitListing();
        return seller;
    }
}
