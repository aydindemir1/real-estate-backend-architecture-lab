package com.aydindemir.property.shared.domain.model;

import com.aydindemir.property.shared.domain.exception.InvalidPropertyStateException;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;

public final class Property {

    private final PropertyId propertyId;
    private final SellerId sellerId;
    private final AgentId agentId;
    private String title;
    private String description;
    private final String propertyType;
    private Address address;
    private GeoLocation geoLocation;
    private Money price;
    private Area area;
    private int roomCount;
    private Set<String> features;
    private PropertyStatus status;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant publishedAt;
    private Long version;

    private Property(
            PropertyId propertyId,
            SellerId sellerId,
            AgentId agentId,
            String title,
            String description,
            String propertyType,
            Address address,
            GeoLocation geoLocation,
            Money price,
            Area area,
            int roomCount,
            Set<String> features,
            PropertyStatus status,
            Instant createdAt,
            Instant updatedAt,
            Instant publishedAt,
            Long version) {
        this.propertyId = Objects.requireNonNull(propertyId, "propertyId must not be null");
        this.sellerId = Objects.requireNonNull(sellerId, "sellerId must not be null");
        this.agentId = agentId;
        this.title = requireText(title, "title");
        this.description = description == null ? "" : description.trim();
        this.propertyType = requireText(propertyType, "propertyType");
        this.address = Objects.requireNonNull(address, "address must not be null");
        this.geoLocation = geoLocation;
        this.price = Objects.requireNonNull(price, "price must not be null");
        this.area = Objects.requireNonNull(area, "area must not be null");
        if (roomCount <= 0) {
            throw new IllegalArgumentException("roomCount must be greater than zero");
        }
        this.roomCount = roomCount;
        this.features = features == null ? Set.of() : Set.copyOf(features);
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        this.publishedAt = publishedAt;
        this.version = version;
    }

    public static Property createDraft(
            PropertyId propertyId,
            SellerId sellerId,
            AgentId agentId,
            String title,
            String description,
            String propertyType,
            Address address,
            GeoLocation geoLocation,
            Money price,
            Area area,
            int roomCount,
            Set<String> features,
            Clock clock) {
        Instant now = Instant.now(clock);
        return new Property(
                propertyId, sellerId, agentId, title, description, propertyType, address, geoLocation,
                price, area, roomCount, features, PropertyStatus.DRAFT, now, now, null, null);
    }

    public static Property reconstitute(
            PropertyId propertyId,
            SellerId sellerId,
            AgentId agentId,
            String title,
            String description,
            String propertyType,
            Address address,
            GeoLocation geoLocation,
            Money price,
            Area area,
            int roomCount,
            Set<String> features,
            PropertyStatus status,
            Instant createdAt,
            Instant updatedAt,
            Instant publishedAt,
            Long version) {
        return new Property(
                propertyId, sellerId, agentId, title, description, propertyType, address, geoLocation,
                price, area, roomCount, features, status, createdAt, updatedAt, publishedAt, version);
    }

    public void publish(Clock clock) {
        if (status != PropertyStatus.DRAFT) {
            throw new InvalidPropertyStateException("Only a DRAFT property can be published");
        }
        Instant now = Instant.now(clock);
        status = PropertyStatus.PUBLISHED;
        publishedAt = now;
        updatedAt = now;
    }

    public PropertyId propertyId() { return propertyId; }
    public SellerId sellerId() { return sellerId; }
    public AgentId agentId() { return agentId; }
    public String title() { return title; }
    public String description() { return description; }
    public String propertyType() { return propertyType; }
    public Address address() { return address; }
    public GeoLocation geoLocation() { return geoLocation; }
    public Money price() { return price; }
    public Area area() { return area; }
    public int roomCount() { return roomCount; }
    public Set<String> features() { return features; }
    public PropertyStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public Instant publishedAt() { return publishedAt; }
    public Long version() { return version; }

    public void assignVersion(Long version) {
        this.version = version;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }
}
