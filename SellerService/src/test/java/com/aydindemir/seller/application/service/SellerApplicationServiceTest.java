package com.aydindemir.seller.application.service;

import com.aydindemir.seller.application.command.CreateSellerCommand;
import com.aydindemir.seller.application.query.GetSellerQuery;
import com.aydindemir.seller.domain.exception.SellerNotFoundException;
import com.aydindemir.seller.domain.model.Seller;
import com.aydindemir.seller.domain.model.SellerId;
import com.aydindemir.seller.domain.model.UserId;
import com.aydindemir.seller.domain.repository.SellerRepository;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SellerApplicationServiceTest {

    @Test
    void createsSellerAndReturnsResult() {
        InMemorySellerRepository repository = new InMemorySellerRepository();
        SellerApplicationService service = new SellerApplicationService(repository);
        UserId userId = UserId.of(UUID.randomUUID());

        SellerResult result = service.createSeller(
                new CreateSellerCommand(userId, "Aydın Demir"));

        assertEquals(userId, result.userId());
        assertEquals("Aydın Demir", result.displayName());
        assertEquals(result.sellerId(), repository.savedSeller.sellerId());
    }

    @Test
    void getsExistingSeller() {
        InMemorySellerRepository repository = new InMemorySellerRepository();
        Seller seller = Seller.create(UserId.of(UUID.randomUUID()), "Aydın Demir");
        repository.save(seller);
        SellerApplicationService service = new SellerApplicationService(repository);

        SellerResult result = service.getSeller(new GetSellerQuery(seller.sellerId()));

        assertEquals(seller.sellerId(), result.sellerId());
        assertEquals(seller.userId(), result.userId());
    }

    @Test
    void throwsWhenSellerDoesNotExist() {
        SellerApplicationService service =
                new SellerApplicationService(new InMemorySellerRepository());

        assertThrows(
                SellerNotFoundException.class,
                () -> service.getSeller(new GetSellerQuery(SellerId.newId())));
    }

    private static final class InMemorySellerRepository implements SellerRepository {

        private final Map<SellerId, Seller> sellers = new HashMap<>();
        private Seller savedSeller;

        @Override
        public Seller save(Seller seller) {
            savedSeller = seller;
            sellers.put(seller.sellerId(), seller);
            return seller;
        }

        @Override
        public Optional<Seller> findById(SellerId sellerId) {
            return Optional.ofNullable(sellers.get(sellerId));
        }
    }
}
