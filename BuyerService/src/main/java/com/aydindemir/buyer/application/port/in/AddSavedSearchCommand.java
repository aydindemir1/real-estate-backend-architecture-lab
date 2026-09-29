package com.aydindemir.buyer.application.port.in;

import com.aydindemir.buyer.domain.model.BuyerId;
import com.aydindemir.buyer.domain.model.SavedSearch;

import java.util.Objects;

public record AddSavedSearchCommand(
        BuyerId buyerId,
        SavedSearch savedSearch
) {

    public AddSavedSearchCommand {
        Objects.requireNonNull(buyerId, "buyerId must not be null");
        Objects.requireNonNull(savedSearch, "savedSearch must not be null");
    }
}
