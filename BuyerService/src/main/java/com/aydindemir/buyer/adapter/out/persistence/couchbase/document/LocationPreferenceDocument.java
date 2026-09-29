package com.aydindemir.buyer.adapter.out.persistence.couchbase.document;

public record LocationPreferenceDocument(
        String city,
        String district
) {
}
