package com.aydindemir.seller.application.service;

import com.aydindemir.seller.application.command.CreateSellerCommand;
import com.aydindemir.seller.application.query.GetSellerQuery;
import com.aydindemir.seller.domain.exception.SellerNotFoundException;
import com.aydindemir.seller.domain.model.Seller;
import com.aydindemir.seller.domain.repository.SellerRepository;

import java.util.Objects;

public final class SellerApplicationService {

    private final SellerRepository sellerRepository;

    public SellerApplicationService(SellerRepository sellerRepository) {
        this.sellerRepository = Objects.requireNonNull(sellerRepository, "sellerRepository must not be null");
    }

    public SellerResult createSeller(CreateSellerCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        Seller seller = Seller.create(command.userId(), command.displayName());
        Seller savedSeller = sellerRepository.save(seller);
        return SellerResult.from(savedSeller);
    }

    public SellerResult getSeller(GetSellerQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        Seller seller = sellerRepository.findById(query.sellerId())
                .orElseThrow(() -> new SellerNotFoundException(query.sellerId()));

        return SellerResult.from(seller);
    }
}
