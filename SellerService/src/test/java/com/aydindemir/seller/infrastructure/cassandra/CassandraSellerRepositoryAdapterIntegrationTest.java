package com.aydindemir.seller.infrastructure.cassandra;

import com.aydindemir.seller.domain.model.Seller;
import com.aydindemir.seller.domain.model.SellerStatus;
import com.aydindemir.seller.domain.model.UserId;
import com.aydindemir.seller.infrastructure.cassandra.adapter.CassandraSellerRepositoryAdapter;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CassandraSellerRepositoryAdapterIntegrationTest
        extends CassandraIntegrationTestSupport {

    @Autowired
    private CassandraSellerRepositoryAdapter adapter;

    @Autowired
    private CqlSession cqlSession;

    @Test
    void saveAndLoadShouldRoundTripSellerAggregate() {
        UserId userId = UserId.of(UUID.randomUUID());
        Seller original = Seller.create(userId, "Aydın Demir");

        Seller saved = adapter.save(original);
        Seller loaded = adapter.findById(saved.sellerId()).orElseThrow();

        assertThat(saved.sellerId()).isEqualTo(original.sellerId());
        assertThat(loaded.sellerId()).isEqualTo(original.sellerId());
        assertThat(loaded.userId()).isEqualTo(userId);
        assertThat(loaded.displayName()).isEqualTo("Aydın Demir");
        assertThat(loaded.status()).isEqualTo(SellerStatus.ACTIVE);
        assertThat(loaded.createdAt()).isEqualTo(original.createdAt());
        assertThat(loaded.updatedAt()).isEqualTo(original.updatedAt());
    }

    @Test
    void sellerByIdTableShouldPersistExpectedPhysicalColumns() {
        Seller seller = Seller.create(
                UserId.of(UUID.randomUUID()),
                "Physical Model Seller");

        adapter.save(seller);

        Row row = cqlSession.execute(
                        "SELECT seller_id, user_id, display_name, status, created_at, updated_at "
                                + "FROM seller_service.seller_by_id WHERE seller_id = ?",
                        seller.sellerId().value())
                .one();

        assertThat(row).isNotNull();
        assertThat(row.getUuid("seller_id")).isEqualTo(seller.sellerId().value());
        assertThat(row.getUuid("user_id")).isEqualTo(seller.userId().value());
        assertThat(row.getString("display_name")).isEqualTo("Physical Model Seller");
        assertThat(row.getString("status")).isEqualTo("ACTIVE");
        assertThat(row.getInstant("created_at")).isEqualTo(seller.createdAt());
        assertThat(row.getInstant("updated_at")).isEqualTo(seller.updatedAt());
    }
}
