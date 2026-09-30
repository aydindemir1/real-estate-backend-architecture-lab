package com.aydindemir.seller.application.command;

import com.aydindemir.seller.domain.model.PropertyDraftData;
import com.aydindemir.seller.domain.model.SellerId;

import java.util.Objects;

public record CreateListingSubmissionCommand(
        SellerId sellerId,
        PropertyDraftData propertyDraftData) {

    public CreateListingSubmissionCommand {
        Objects.requireNonNull(sellerId, "sellerId must not be null");
        Objects.requireNonNull(propertyDraftData, "propertyDraftData must not be null");
    }
}
