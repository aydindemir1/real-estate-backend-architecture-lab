package com.aydindemir.seller.application.command;

import com.aydindemir.seller.domain.model.UserId;

import java.util.Objects;

public record CreateSellerCommand(
        UserId userId,
        String displayName) {

    public CreateSellerCommand {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(displayName, "displayName must not be null");

        displayName = displayName.trim();
        if (displayName.isEmpty()) {
            throw new IllegalArgumentException("displayName must not be blank");
        }
    }
}
