package com.aydindemir.property.shared.domain.repository;

import com.aydindemir.property.shared.domain.model.Property;
import com.aydindemir.property.shared.domain.model.PropertyId;

import java.util.Optional;

public interface PropertyRepository {
    Property save(Property property);
    Optional<Property> findById(PropertyId propertyId);
}
