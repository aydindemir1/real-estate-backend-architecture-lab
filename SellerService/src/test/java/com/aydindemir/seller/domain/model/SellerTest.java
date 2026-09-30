package com.aydindemir.seller.domain.model;

import com.aydindemir.seller.domain.exception.SellerNotActiveException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SellerTest {

    @Test
    void activeSellerCanSubmitListing() {
        Seller seller = Seller.create(UserId.of(UUID.randomUUID()), "Aydın Demir");

        assertEquals(SellerStatus.ACTIVE, seller.status());
        assertDoesNotThrow(seller::assertCanSubmitListing);
    }

    @Test
    void suspendedSellerCannotSubmitListing() {
        Seller seller = Seller.create(UserId.of(UUID.randomUUID()), "Aydın Demir");
        seller.changeStatus(SellerStatus.SUSPENDED);

        assertThrows(SellerNotActiveException.class, seller::assertCanSubmitListing);
    }

    @Test
    void inactiveSellerCannotSubmitListing() {
        Seller seller = Seller.create(UserId.of(UUID.randomUUID()), "Aydın Demir");
        seller.changeStatus(SellerStatus.INACTIVE);

        assertThrows(SellerNotActiveException.class, seller::assertCanSubmitListing);
    }
}
