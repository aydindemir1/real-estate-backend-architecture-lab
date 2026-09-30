package com.aydindemir.seller.domain.repository;

import com.aydindemir.seller.domain.model.Seller;
import com.aydindemir.seller.domain.model.SellerId;
import com.aydindemir.seller.domain.model.UserId;

import java.util.Optional;

public interface SellerRepository {

    Seller save(Seller seller);

    Optional<Seller> findById(SellerId sellerId);

    boolean existsByUserId(UserId userId);
}
