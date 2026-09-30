package com.aydindemir.seller.infrastructure.cassandra.repository;

import com.aydindemir.seller.infrastructure.cassandra.table.SellerByIdTable;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

public interface SpringDataSellerByIdRepository
        extends CassandraRepository<SellerByIdTable, UUID> {
}
