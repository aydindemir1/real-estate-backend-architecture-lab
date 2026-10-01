package com.aydindemir.property.shared.persistence.adapter;

import com.aydindemir.property.shared.domain.exception.PropertyConcurrentModificationException;
import com.aydindemir.property.shared.domain.model.Property;
import com.aydindemir.property.shared.domain.model.PropertyId;
import com.aydindemir.property.shared.domain.repository.PropertyRepository;
import com.aydindemir.property.shared.persistence.mapper.PropertyDocumentMapper;
import com.aydindemir.property.shared.persistence.repository.SpringDataPropertyRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MongoPropertyRepositoryAdapter implements PropertyRepository {

    private final SpringDataPropertyRepository repository;
    private final PropertyDocumentMapper mapper;

    public MongoPropertyRepositoryAdapter(SpringDataPropertyRepository repository, PropertyDocumentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Property save(Property property) {
        try {
            return mapper.toDomain(repository.save(mapper.toDocument(property)));
        } catch (OptimisticLockingFailureException ex) {
            throw new PropertyConcurrentModificationException(property.propertyId().toString(), ex);
        }
    }

    @Override
    public Optional<Property> findById(PropertyId propertyId) {
        return repository.findById(propertyId.toString()).map(mapper::toDomain);
    }
}
