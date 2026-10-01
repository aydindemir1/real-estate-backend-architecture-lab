package com.aydindemir.property.getbyid;

import com.aydindemir.property.shared.domain.exception.PropertyNotFoundException;
import com.aydindemir.property.shared.domain.model.Property;
import com.aydindemir.property.shared.domain.repository.PropertyRepository;
import org.springframework.stereotype.Service;

@Service
public class GetPropertyHandler {

    private final PropertyRepository repository;

    public GetPropertyHandler(PropertyRepository repository) {
        this.repository = repository;
    }

    public GetPropertyResult handle(GetPropertyQuery query) {
        Property property = repository.findById(query.propertyId())
                .orElseThrow(() -> new PropertyNotFoundException(query.propertyId().toString()));
        return toResult(property);
    }

    private GetPropertyResult toResult(Property property) {
        var address = new GetPropertyResult.AddressResult(
                property.address().city(),
                property.address().district(),
                property.address().line1());
        var geo = property.geoLocation() == null ? null :
                new GetPropertyResult.GeoLocationResult(
                        property.geoLocation().latitude(),
                        property.geoLocation().longitude());

        return new GetPropertyResult(
                property.propertyId().toString(),
                property.sellerId().toString(),
                property.agentId() == null ? null : property.agentId().toString(),
                property.title(),
                property.description(),
                property.propertyType(),
                address,
                geo,
                property.price().amount(),
                property.price().currency().getCurrencyCode(),
                property.area().squareMeters(),
                property.roomCount(),
                property.features(),
                property.status().name(),
                property.createdAt(),
                property.updatedAt(),
                property.publishedAt(),
                property.version());
    }
}
