package com.aydindemir.buyer.adapter.out.persistence.couchbase.repository;

import com.aydindemir.buyer.adapter.out.persistence.couchbase.document.BuyerPreferencesDocument;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.data.couchbase.repository.Scope;

@Scope("buyer_service")
@Collection("preferences")
public interface SpringDataBuyerPreferencesRepository
        extends CouchbaseRepository<BuyerPreferencesDocument, String> {
}
