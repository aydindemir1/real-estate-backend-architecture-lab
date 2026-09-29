package com.aydindemir.buyer.adapter.out.persistence.couchbase.repository;

import com.aydindemir.buyer.adapter.out.persistence.couchbase.document.BuyerPreferencesDocument;
import org.springframework.data.couchbase.repository.CouchbaseRepository;

public interface SpringDataBuyerPreferencesRepository
        extends CouchbaseRepository<BuyerPreferencesDocument, String> {
}
