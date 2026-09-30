package com.aydindemir.seller.domain.exception;

import com.aydindemir.seller.domain.model.SellerId;
import com.aydindemir.seller.domain.model.SellerStatus;

public class SellerNotActiveException extends RuntimeException {

    public SellerNotActiveException(SellerId sellerId, SellerStatus status) {
        super("Seller " + sellerId + " must be ACTIVE to submit a listing, current status: " + status);
    }
}
