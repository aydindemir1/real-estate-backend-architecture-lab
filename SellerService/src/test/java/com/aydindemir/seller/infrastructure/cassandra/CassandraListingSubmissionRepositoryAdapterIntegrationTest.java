package com.aydindemir.seller.infrastructure.cassandra;

import com.aydindemir.seller.domain.model.ListingSubmission;
import com.aydindemir.seller.domain.model.ListingSubmissionId;
import com.aydindemir.seller.domain.model.ListingSubmissionStatus;
import com.aydindemir.seller.domain.model.PropertyDraftData;
import com.aydindemir.seller.domain.model.SellerId;
import com.aydindemir.seller.domain.repository.ListingSubmissionPage;
import com.aydindemir.seller.domain.repository.ListingSubmissionPageRequest;
import com.aydindemir.seller.infrastructure.cassandra.adapter.CassandraListingSubmissionRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CassandraListingSubmissionRepositoryAdapterIntegrationTest
        extends CassandraIntegrationTestSupport {

    @Autowired
    private CassandraListingSubmissionRepositoryAdapter adapter;

    @Test
    void saveAndExactLookupShouldRoundTripSubmission() {
        SellerId sellerId = SellerId.newId();
        ListingSubmission original = submission(
                sellerId,
                Instant.parse("2026-09-30T12:00:00Z"),
                "Exact lookup");

        adapter.save(original);

        ListingSubmission loaded = adapter.findBySellerAndMonthAndId(
                        sellerId,
                        YearMonth.of(2026, 9),
                        original.createdAt(),
                        original.submissionId())
                .orElseThrow();

        assertThat(loaded.submissionId()).isEqualTo(original.submissionId());
        assertThat(loaded.sellerId()).isEqualTo(sellerId);
        assertThat(loaded.status()).isEqualTo(ListingSubmissionStatus.CREATED);
        assertThat(loaded.createdAt()).isEqualTo(original.createdAt());
        assertThat(loaded.propertyDraftData()).isEqualTo(original.propertyDraftData());
    }

    @Test
    void listBySellerAndMonthShouldReturnNewestFirst() {
        SellerId sellerId = SellerId.newId();

        ListingSubmission oldest = submission(
                sellerId,
                Instant.parse("2026-09-05T10:00:00Z"),
                "Oldest");
        ListingSubmission middle = submission(
                sellerId,
                Instant.parse("2026-09-15T10:00:00Z"),
                "Middle");
        ListingSubmission newest = submission(
                sellerId,
                Instant.parse("2026-09-25T10:00:00Z"),
                "Newest");

        adapter.save(oldest);
        adapter.save(newest);
        adapter.save(middle);

        ListingSubmissionPage page = adapter.listBySellerAndMonth(
                sellerId,
                YearMonth.of(2026, 9),
                ListingSubmissionPageRequest.firstPage(10));

        assertThat(page.items())
                .extracting(item -> item.propertyDraftData().title())
                .containsExactly("Newest", "Middle", "Oldest");
    }

    @Test
    void listBySellerAndMonthShouldNotCrossMonthlyPartition() {
        SellerId sellerId = SellerId.newId();

        adapter.save(submission(
                sellerId,
                Instant.parse("2026-09-30T23:59:00Z"),
                "September"));
        adapter.save(submission(
                sellerId,
                Instant.parse("2026-10-01T00:01:00Z"),
                "October"));

        ListingSubmissionPage september = adapter.listBySellerAndMonth(
                sellerId,
                YearMonth.of(2026, 9),
                ListingSubmissionPageRequest.firstPage(10));

        ListingSubmissionPage october = adapter.listBySellerAndMonth(
                sellerId,
                YearMonth.of(2026, 10),
                ListingSubmissionPageRequest.firstPage(10));

        assertThat(september.items())
                .extracting(item -> item.propertyDraftData().title())
                .containsExactly("September");
        assertThat(october.items())
                .extracting(item -> item.propertyDraftData().title())
                .containsExactly("October");
    }

    @Test
    void pagingShouldContinueWithinSameSellerMonthPartition() {
        SellerId sellerId = SellerId.newId();

        adapter.save(submission(
                sellerId,
                Instant.parse("2026-09-10T10:00:00Z"),
                "First"));
        adapter.save(submission(
                sellerId,
                Instant.parse("2026-09-20T10:00:00Z"),
                "Second"));
        adapter.save(submission(
                sellerId,
                Instant.parse("2026-09-30T10:00:00Z"),
                "Third"));

        ListingSubmissionPage firstPage = adapter.listBySellerAndMonth(
                sellerId,
                YearMonth.of(2026, 9),
                ListingSubmissionPageRequest.firstPage(2));

        assertThat(firstPage.items())
                .extracting(item -> item.propertyDraftData().title())
                .containsExactly("Third", "Second");
        assertThat(firstPage.nextPageState()).isNotBlank();

        ListingSubmissionPage secondPage = adapter.listBySellerAndMonth(
                sellerId,
                YearMonth.of(2026, 9),
                new ListingSubmissionPageRequest(2, firstPage.nextPageState()));

        assertThat(secondPage.items())
                .extracting(item -> item.propertyDraftData().title())
                .containsExactly("First");
        assertThat(secondPage.nextPageState()).isNull();
    }

    private ListingSubmission submission(
            SellerId sellerId,
            Instant createdAt,
            String title) {
        return ListingSubmission.rehydrate(
                ListingSubmissionId.newId(),
                sellerId,
                new PropertyDraftData(
                        title,
                        "Integration test",
                        "APARTMENT",
                        "Kocaeli",
                        "Gebze",
                        "Örnek Mah. No:1",
                        new BigDecimal("3500000"),
                        "TRY",
                        new BigDecimal("120"),
                        3),
                ListingSubmissionStatus.CREATED,
                createdAt,
                createdAt);
    }
}
