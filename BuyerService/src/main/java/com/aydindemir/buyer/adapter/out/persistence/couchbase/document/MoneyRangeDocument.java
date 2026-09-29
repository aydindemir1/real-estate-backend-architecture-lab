package com.aydindemir.buyer.adapter.out.persistence.couchbase.document;

import java.math.BigDecimal;

public record MoneyRangeDocument(
        BigDecimal min,
        BigDecimal max,
        String currency
) {
}
