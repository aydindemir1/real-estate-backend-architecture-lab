package com.aydindemir.seller.infrastructure.cassandra.table;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Table("listing_submissions_by_seller_and_month")
public record ListingSubmissionBySellerMonthTable(
        @PrimaryKeyColumn(
                name = "seller_id",
                ordinal = 0,
                type = PrimaryKeyType.PARTITIONED)
        UUID sellerId,

        @PrimaryKeyColumn(
                name = "year_month",
                ordinal = 1,
                type = PrimaryKeyType.PARTITIONED)
        String yearMonth,

        @PrimaryKeyColumn(
                name = "created_at",
                ordinal = 2,
                type = PrimaryKeyType.CLUSTERED,
                ordering = Ordering.DESCENDING)
        Instant createdAt,

        @PrimaryKeyColumn(
                name = "submission_id",
                ordinal = 3,
                type = PrimaryKeyType.CLUSTERED)
        UUID submissionId,

        String status,
        @Column("updated_at")
        Instant updatedAt,

        String title,
        String description,
        @Column("property_type")
        String propertyType,
        String city,
        String district,
        @Column("address_line")
        String addressLine,
        @Column("price_amount")
        BigDecimal priceAmount,
        String currency,
        BigDecimal area,
        @Column("room_count")
        int roomCount) {
}
