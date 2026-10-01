package com.aydindemir.property.publish;

import com.aydindemir.property.shared.domain.model.PropertyId;

public record PublishPropertyCommand(PropertyId propertyId) {
}
