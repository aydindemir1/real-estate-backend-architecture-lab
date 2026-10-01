package com.aydindemir.property.getbyid;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record GetPropertyResult(
        String propertyId,
        String sellerId,
        String agentId,
        String title,
        String description,
        String propertyType,
        AddressResult address,
        GeoLocationResult geoLocation,
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

    public record AddressResult(String city, String district, String line1) {}
    public record GeoLocationResult(BigDecimal latitude, BigDecimal longitude) {}
}
