package com.aydindemir.seller.application.service;

import com.aydindemir.seller.application.command.CreateListingSubmissionCommand;
import com.aydindemir.seller.application.command.SubmitListingCommand;
import com.aydindemir.seller.application.query.ListSellerSubmissionsQuery;
import com.aydindemir.seller.domain.exception.ListingSubmissionNotFoundException;
import com.aydindemir.seller.domain.exception.SellerNotActiveException;
import com.aydindemir.seller.domain.model.ListingSubmission;
import com.aydindemir.seller.domain.model.ListingSubmissionId;
import com.aydindemir.seller.domain.model.ListingSubmissionStatus;
import com.aydindemir.seller.domain.model.PropertyDraftData;
import com.aydindemir.seller.domain.model.Seller;
import com.aydindemir.seller.domain.model.SellerId;
import com.aydindemir.seller.domain.model.SellerStatus;
import com.aydindemir.seller.domain.model.UserId;
import com.aydindemir.seller.domain.repository.ListingSubmissionPage;
import com.aydindemir.seller.domain.repository.ListingSubmissionPageRequest;
import com.aydindemir.seller.domain.repository.ListingSubmissionRepository;
import com.aydindemir.seller.domain.repository.SellerRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ListingSubmissionApplicationServiceTest {

    @Test
    void activeSellerCanCreateSubmission() {
        InMemorySellerRepository sellerRepository = new InMemorySellerRepository();
        Seller seller = Seller.create(UserId.of(UUID.randomUUID()), "Aydın Demir");
        sellerRepository.save(seller);
        InMemoryListingRepository listingRepository = new InMemoryListingRepository();
        ListingSubmissionApplicationService service =
                new ListingSubmissionApplicationService(sellerRepository, listingRepository);

        ListingSubmissionResult result = service.createListingSubmission(
                new CreateListingSubmissionCommand(seller.sellerId(), draft()));

        assertEquals(ListingSubmissionStatus.CREATED, result.status());
        assertEquals(seller.sellerId(), result.sellerId());
        assertEquals(result.submissionId(), listingRepository.savedSubmission.submissionId());
    }

    @Test
    void inactiveSellerCannotCreateSubmission() {
        InMemorySellerRepository sellerRepository = new InMemorySellerRepository();
        Seller seller = Seller.create(UserId.of(UUID.randomUUID()), "Aydın Demir");
        seller.changeStatus(SellerStatus.INACTIVE);
        sellerRepository.save(seller);

        ListingSubmissionApplicationService service =
                new ListingSubmissionApplicationService(
                        sellerRepository,
                        new InMemoryListingRepository());

        assertThrows(
                SellerNotActiveException.class,
                () -> service.createListingSubmission(
                        new CreateListingSubmissionCommand(seller.sellerId(), draft())));
    }

    @Test
    void submitChangesOnlyLocalStateToSubmitted() {
        InMemorySellerRepository sellerRepository = new InMemorySellerRepository();
        Seller seller = Seller.create(UserId.of(UUID.randomUUID()), "Aydın Demir");
        sellerRepository.save(seller);

        ListingSubmission submission =
                ListingSubmission.create(seller.sellerId(), draft());
        InMemoryListingRepository listingRepository = new InMemoryListingRepository();
        listingRepository.save(submission);

        ListingSubmissionApplicationService service =
                new ListingSubmissionApplicationService(sellerRepository, listingRepository);

        YearMonth yearMonth =
                YearMonth.from(submission.createdAt().atZone(ZoneOffset.UTC));

        ListingSubmissionResult result = service.submitListing(
                new SubmitListingCommand(
                        seller.sellerId(),
                        yearMonth,
                        submission.createdAt(),
                        submission.submissionId()));

        assertEquals(ListingSubmissionStatus.SUBMITTED, result.status());
        assertEquals(ListingSubmissionStatus.SUBMITTED, listingRepository.savedSubmission.status());
    }

    @Test
    void submitUsesExactSellerMonthCreatedAtAndIdLookup() {
        InMemorySellerRepository sellerRepository = new InMemorySellerRepository();
        Seller seller = Seller.create(UserId.of(UUID.randomUUID()), "Aydın Demir");
        sellerRepository.save(seller);

        ListingSubmission submission =
                ListingSubmission.create(seller.sellerId(), draft());
        InMemoryListingRepository listingRepository = new InMemoryListingRepository();
        listingRepository.save(submission);

        ListingSubmissionApplicationService service =
                new ListingSubmissionApplicationService(sellerRepository, listingRepository);

        YearMonth yearMonth =
                YearMonth.from(submission.createdAt().atZone(ZoneOffset.UTC));

        service.submitListing(
                new SubmitListingCommand(
                        seller.sellerId(),
                        yearMonth,
                        submission.createdAt(),
                        submission.submissionId()));

        assertEquals(seller.sellerId(), listingRepository.lastLookupSellerId);
        assertEquals(yearMonth, listingRepository.lastLookupYearMonth);
        assertEquals(submission.createdAt(), listingRepository.lastLookupCreatedAt);
        assertEquals(submission.submissionId(), listingRepository.lastLookupSubmissionId);
    }

    @Test
    void throwsWhenSubmissionDoesNotExist() {
        InMemorySellerRepository sellerRepository = new InMemorySellerRepository();
        Seller seller = Seller.create(UserId.of(UUID.randomUUID()), "Aydın Demir");
        sellerRepository.save(seller);

        ListingSubmissionApplicationService service =
                new ListingSubmissionApplicationService(
                        sellerRepository,
                        new InMemoryListingRepository());

        assertThrows(
                ListingSubmissionNotFoundException.class,
                () -> service.submitListing(
                        new SubmitListingCommand(
                                seller.sellerId(),
                                YearMonth.of(2026, 9),
                                Instant.parse("2026-09-30T12:00:00Z"),
                                ListingSubmissionId.newId())));
    }

    @Test
    void listsByExactSellerAndMonthWithBoundedPageRequest() {
        InMemorySellerRepository sellerRepository = new InMemorySellerRepository();
        Seller seller = Seller.create(UserId.of(UUID.randomUUID()), "Aydın Demir");
        sellerRepository.save(seller);

        InMemoryListingRepository listingRepository = new InMemoryListingRepository();
        ListingSubmission submission = ListingSubmission.create(seller.sellerId(), draft());
        listingRepository.save(submission);

        ListingSubmissionApplicationService service =
                new ListingSubmissionApplicationService(sellerRepository, listingRepository);

        YearMonth yearMonth =
                YearMonth.from(submission.createdAt().atZone(ZoneOffset.UTC));

        ListingSubmissionPageResult result = service.listSellerSubmissions(
                new ListSellerSubmissionsQuery(
                        seller.sellerId(),
                        yearMonth,
                        20,
                        null));

        assertEquals(1, result.items().size());
        assertEquals(seller.sellerId(), listingRepository.lastListSellerId);
        assertEquals(yearMonth, listingRepository.lastListYearMonth);
        assertEquals(20, listingRepository.lastPageRequest.pageSize());
    }

    private PropertyDraftData draft() {
        return new PropertyDraftData(
                "Daire",
                "Merkezi konum",
                "APARTMENT",
                "Kocaeli",
                "Gebze",
                "Örnek Mah. No:1",
                new BigDecimal("3500000"),
                "TRY",
                new BigDecimal("120"),
                3);
    }

    private static final class InMemorySellerRepository implements SellerRepository {

        private final Map<SellerId, Seller> sellers = new HashMap<>();

        @Override
        public Seller save(Seller seller) {
            sellers.put(seller.sellerId(), seller);
            return seller;
        }

        @Override
        public Optional<Seller> findById(SellerId sellerId) {
            return Optional.ofNullable(sellers.get(sellerId));
        }
    }

    private static final class InMemoryListingRepository implements ListingSubmissionRepository {

        private final List<ListingSubmission> submissions = new ArrayList<>();
        private ListingSubmission savedSubmission;
        private SellerId lastLookupSellerId;
        private YearMonth lastLookupYearMonth;
        private Instant lastLookupCreatedAt;
        private ListingSubmissionId lastLookupSubmissionId;
        private SellerId lastListSellerId;
        private YearMonth lastListYearMonth;
        private ListingSubmissionPageRequest lastPageRequest;

        @Override
        public ListingSubmission save(ListingSubmission submission) {
            savedSubmission = submission;
            submissions.removeIf(existing ->
                    existing.submissionId().equals(submission.submissionId()));
            submissions.add(submission);
            return submission;
        }

        @Override
        public Optional<ListingSubmission> findBySellerAndMonthAndId(
                SellerId sellerId,
                YearMonth yearMonth,
                Instant createdAt,
                ListingSubmissionId submissionId) {
            lastLookupSellerId = sellerId;
            lastLookupYearMonth = yearMonth;
            lastLookupCreatedAt = createdAt;
            lastLookupSubmissionId = submissionId;

            return submissions.stream()
                    .filter(submission -> submission.sellerId().equals(sellerId))
                    .filter(submission -> submission.createdAt().equals(createdAt))
                    .filter(submission -> YearMonth.from(
                            submission.createdAt().atZone(ZoneOffset.UTC)).equals(yearMonth))
                    .filter(submission -> submission.submissionId().equals(submissionId))
                    .findFirst();
        }

        @Override
        public ListingSubmissionPage listBySellerAndMonth(
                SellerId sellerId,
                YearMonth yearMonth,
                ListingSubmissionPageRequest pageRequest) {
            lastListSellerId = sellerId;
            lastListYearMonth = yearMonth;
            lastPageRequest = pageRequest;

            List<ListingSubmission> items = submissions.stream()
                    .filter(submission -> submission.sellerId().equals(sellerId))
                    .filter(submission -> YearMonth.from(
                            submission.createdAt().atZone(ZoneOffset.UTC)).equals(yearMonth))
                    .limit(pageRequest.pageSize())
                    .toList();

            return new ListingSubmissionPage(items, null);
        }
    }
}
