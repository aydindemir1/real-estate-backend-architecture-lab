package com.aydindemir.buyer.adapter.out.persistence.couchbase.adapter;

import com.aydindemir.buyer.adapter.out.persistence.couchbase.document.BuyerPreferencesDocument;
import com.aydindemir.buyer.adapter.out.persistence.couchbase.mapper.BuyerPreferencesDocumentMapper;
import com.aydindemir.buyer.adapter.out.persistence.couchbase.repository.SpringDataBuyerPreferencesRepository;
import com.aydindemir.buyer.application.port.out.LoadBuyerPreferencesPort;
import com.aydindemir.buyer.application.port.out.SaveBuyerPreferencesPort;
import com.aydindemir.buyer.domain.model.BuyerId;
import com.aydindemir.buyer.domain.model.BuyerPreferences;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class CouchbaseBuyerPreferencesAdapter
        implements SaveBuyerPreferencesPort, LoadBuyerPreferencesPort {

    private final SpringDataBuyerPreferencesRepository repository;
    private final BuyerPreferencesDocumentMapper mapper;

    public CouchbaseBuyerPreferencesAdapter(
            SpringDataBuyerPreferencesRepository repository,
            BuyerPreferencesDocumentMapper mapper
    ) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
        this.mapper = Objects.requireNonNull(mapper, "mapper must not be null");
    }

    @Override
    public BuyerPreferences save(BuyerPreferences preferences) {
        Objects.requireNonNull(preferences, "preferences must not be null");

        BuyerPreferencesDocument saved = repository.save(mapper.toDocument(preferences));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<BuyerPreferences> load(BuyerId buyerId) {
        Objects.requireNonNull(buyerId, "buyerId must not be null");

        String documentId = BuyerPreferencesDocument.documentId(buyerId.value().toString());

        return repository.findById(documentId)
                .map(mapper::toDomain);
    }
}
