package com.aydindemir.seller.application.query;

import com.aydindemir.seller.domain.model.SellerId;

import java.time.YearMonth;
import java.util.Objects;

public record ListSellerSubmissionsQuery(
        SellerId sellerId,
        YearMonth yearMonth,
        int pageSize,
        String pageState) {

    public static final int MAX_PAGE_SIZE = 100;

    public ListSellerSubmissionsQuery {
        Objects.requireNonNull(sellerId, "sellerId must not be null");
        Objects.requireNonNull(yearMonth, "yearMonth must not be null");

        if (pageSize <= 0 || pageSize > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                    "pageSize must be between 1 and " + MAX_PAGE_SIZE);
        }

        if (pageState != null && pageState.isBlank()) {
            pageState = null;
        }
    }
}
