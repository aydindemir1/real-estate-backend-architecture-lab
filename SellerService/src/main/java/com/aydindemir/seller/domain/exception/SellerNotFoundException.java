package com.aydindemir.seller.domain.exception;

import com.aydindemir.seller.domain.model.SellerId;

public class SellerNotFoundException extends RuntimeException {

    public SellerNotFoundException(SellerId sellerId) {
        super("Seller not found: " + sellerId);
    }
}
