package com.aydindemir.seller.infrastructure.cassandra.table;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("seller_by_id")
public record SellerByIdTable(
        @PrimaryKey("seller_id")
        UUID sellerId,
        UUID userId,
        String displayName,
        String status,
        Instant createdAt,
        Instant updatedAt) {
}
