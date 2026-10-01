package com.aydindemir.property.shared.persistence.mapper;

import com.aydindemir.property.shared.domain.model.Address;
import com.aydindemir.property.shared.domain.model.AgentId;
import com.aydindemir.property.shared.domain.model.Area;
import com.aydindemir.property.shared.domain.model.GeoLocation;
import com.aydindemir.property.shared.domain.model.Money;
import com.aydindemir.property.shared.domain.model.Property;
import com.aydindemir.property.shared.domain.model.PropertyId;
import com.aydindemir.property.shared.domain.model.PropertyStatus;
import com.aydindemir.property.shared.domain.model.SellerId;
import com.aydindemir.property.shared.persistence.document.PropertyDocument;
import org.springframework.stereotype.Component;

import java.util.Currency;

@Component
public class PropertyDocumentMapper {

    public PropertyDocument toDocument(Property property) {
        var address = new PropertyDocument.AddressValue(
                property.address().city(),
                property.address().district(),
                property.address().line1());

        var geoLocation = property.geoLocation() == null ? null :
                new PropertyDocument.GeoLocationValue(
                        property.geoLocation().latitude(),
                        property.geoLocation().longitude());

        return new PropertyDocument(
                property.propertyId().toString(),
                property.sellerId().toString(),
                property.agentId() == null ? null : property.agentId().toString(),
                property.title(),
                property.description(),
                property.propertyType(),
                address,
                geoLocation,
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

    public Property toDomain(PropertyDocument document) {
        var address = new Address(
                document.getAddress().city(),
                document.getAddress().district(),
                document.getAddress().line1());

        var geo = document.getGeoLocation() == null ? null :
                new GeoLocation(
                        document.getGeoLocation().latitude(),
                        document.getGeoLocation().longitude());

        return Property.reconstitute(
                PropertyId.from(document.getId()),
                SellerId.from(document.getSellerId()),
                document.getAgentId() == null ? null : AgentId.from(document.getAgentId()),
                document.getTitle(),
                document.getDescription(),
                document.getPropertyType(),
                address,
                geo,
                new Money(document.getPriceAmount(), Currency.getInstance(document.getCurrency())),
                new Area(document.getAreaSquareMeters()),
                document.getRoomCount(),
                document.getFeatures(),
                PropertyStatus.valueOf(document.getStatus()),
                document.getCreatedAt(),
                document.getUpdatedAt(),
                document.getPublishedAt(),
                document.getVersion());
    }
}
