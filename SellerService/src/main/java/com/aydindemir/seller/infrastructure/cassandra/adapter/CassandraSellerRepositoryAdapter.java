package com.aydindemir.seller.infrastructure.cassandra.adapter;

import com.aydindemir.seller.domain.model.Seller;
import com.aydindemir.seller.domain.model.SellerId;
import com.aydindemir.seller.domain.repository.SellerRepository;
import com.aydindemir.seller.infrastructure.cassandra.mapper.SellerCassandraMapper;
import com.aydindemir.seller.infrastructure.cassandra.repository.SpringDataSellerByIdRepository;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

@Repository
public class CassandraSellerRepositoryAdapter implements SellerRepository {

    private final SpringDataSellerByIdRepository repository;
    private final SellerCassandraMapper mapper;

    public CassandraSellerRepositoryAdapter(SpringDataSellerByIdRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
        this.mapper = new SellerCassandraMapper();
    }

    @Override
    public Seller save(Seller seller) {
        return mapper.toDomain(repository.save(mapper.toTable(seller)));
    }

    @Override
    public Optional<Seller> findById(SellerId sellerId) {
        Objects.requireNonNull(sellerId, "sellerId must not be null");

        return repository.findById(sellerId.value())
                .map(mapper::toDomain);
    }
}
