package com.aydindemir.property.getbyid;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record GetPropertyResponse(
        String propertyId,
        String sellerId,
        String agentId,
        String title,
        String description,
        String propertyType,
        GetPropertyResult.AddressResult address,
        GetPropertyResult.GeoLocationResult geoLocation,
        BigDecimal priceAmount,
        String currency,
        BigDecimal areaSquareMeters,
        int roomCount,
        Set<String> features,
        String status,
        Instant createdAt,
        Instant updatedAt,
        Instant publishedAt,
        Long version) {

    static GetPropertyResponse from(GetPropertyResult result) {
        return new GetPropertyResponse(
                result.propertyId(), result.sellerId(), result.agentId(), result.title(), result.description(),
                result.propertyType(), result.address(), result.geoLocation(), result.priceAmount(),
                result.currency(), result.areaSquareMeters(), result.roomCount(), result.features(),
                result.status(), result.createdAt(), result.updatedAt(), result.publishedAt(), result.version());
    }
}
