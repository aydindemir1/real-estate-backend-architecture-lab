package com.aydindemir.seller.infrastructure.cassandra.mapper;

import com.aydindemir.seller.domain.model.Seller;
import com.aydindemir.seller.domain.model.SellerId;
import com.aydindemir.seller.domain.model.SellerStatus;
import com.aydindemir.seller.domain.model.UserId;
import com.aydindemir.seller.infrastructure.cassandra.table.SellerByIdTable;

import java.util.Objects;

public final class SellerCassandraMapper {

    public SellerByIdTable toTable(Seller seller) {
        Objects.requireNonNull(seller, "seller must not be null");

        return new SellerByIdTable(
                seller.sellerId().value(),
                seller.userId().value(),
                seller.displayName(),
                seller.status().name(),
                seller.createdAt(),
                seller.updatedAt());
    }

    public Seller toDomain(SellerByIdTable table) {
        Objects.requireNonNull(table, "table must not be null");

        return Seller.rehydrate(
                SellerId.of(table.sellerId()),
                UserId.of(table.userId()),
                table.displayName(),
                SellerStatus.valueOf(table.status()),
                table.createdAt(),
                table.updatedAt());
    }
}
