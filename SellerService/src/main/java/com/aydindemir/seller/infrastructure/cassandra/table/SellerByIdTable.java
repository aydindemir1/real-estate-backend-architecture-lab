package com.aydindemir.seller.infrastructure.cassandra.table;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("seller_by_id")
public record SellerByIdTable(
        @PrimaryKey("seller_id")
        UUID sellerId,
        @Column("user_id")
        UUID userId,
        @Column("display_name")
        String displayName,
        String status,
        @Column("created_at")
        Instant createdAt,
        @Column("updated_at")
        Instant updatedAt) {
}
