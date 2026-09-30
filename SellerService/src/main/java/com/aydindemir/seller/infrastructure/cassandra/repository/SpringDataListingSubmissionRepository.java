package com.aydindemir.seller.infrastructure.cassandra.repository;

import com.aydindemir.seller.infrastructure.cassandra.table.ListingSubmissionBySellerMonthTable;
import org.springframework.data.cassandra.core.mapping.MapId;
import org.springframework.data.cassandra.repository.MapIdCassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataListingSubmissionRepository
        extends MapIdCassandraRepository<ListingSubmissionBySellerMonthTable> {

    @Query("""
            SELECT * FROM listing_submissions_by_seller_and_month
            WHERE seller_id = :sellerId
              AND year_month = :yearMonth
            """)
    Slice<ListingSubmissionBySellerMonthTable> findBySellerAndMonth(
            @Param("sellerId") UUID sellerId,
            @Param("yearMonth") String yearMonth,
            Pageable pageable);

    @Query("""
            SELECT * FROM listing_submissions_by_seller_and_month
            WHERE seller_id = :sellerId
              AND year_month = :yearMonth
              AND created_at = :createdAt
              AND submission_id = :submissionId
            """)
    Optional<ListingSubmissionBySellerMonthTable> findExact(
            @Param("sellerId") UUID sellerId,
            @Param("yearMonth") String yearMonth,
            @Param("createdAt") Instant createdAt,
            @Param("submissionId") UUID submissionId);
}
