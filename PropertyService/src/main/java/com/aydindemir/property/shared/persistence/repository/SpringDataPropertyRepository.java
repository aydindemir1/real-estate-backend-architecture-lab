package com.aydindemir.property.shared.persistence.repository;

import com.aydindemir.property.shared.persistence.document.PropertyDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataPropertyRepository extends MongoRepository<PropertyDocument, String> {
}
