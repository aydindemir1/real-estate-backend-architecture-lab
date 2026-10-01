package com.aydindemir.property.shared.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Document(collection = "properties")
public class PropertyDocument {

    @Id
    private String id;
    private String sellerId;
    private String agentId;
    private String title;
    private String description;
    private String propertyType;
    private AddressValue address;
    private GeoLocationValue geoLocation;
    private BigDecimal priceAmount;
    private String currency;
    private BigDecimal areaSquareMeters;
    private int roomCount;
    private Set<String> features;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant publishedAt;

    @Version
    private Long version;

    public PropertyDocument() {
    }

    public PropertyDocument(String id, String sellerId, String agentId, String title, String description,
                            String propertyType, AddressValue address, GeoLocationValue geoLocation,
                            BigDecimal priceAmount, String currency, BigDecimal areaSquareMeters,
                            int roomCount, Set<String> features, String status, Instant createdAt,
                            Instant updatedAt, Instant publishedAt, Long version) {
        this.id = id;
        this.sellerId = sellerId;
        this.agentId = agentId;
        this.title = title;
        this.description = description;
        this.propertyType = propertyType;
        this.address = address;
        this.geoLocation = geoLocation;
        this.priceAmount = priceAmount;
        this.currency = currency;
        this.areaSquareMeters = areaSquareMeters;
        this.roomCount = roomCount;
        this.features = features;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.publishedAt = publishedAt;
        this.version = version;
    }

    public String getId() { return id; }
    public String getSellerId() { return sellerId; }
    public String getAgentId() { return agentId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPropertyType() { return propertyType; }
    public AddressValue getAddress() { return address; }
    public GeoLocationValue getGeoLocation() { return geoLocation; }
    public BigDecimal getPriceAmount() { return priceAmount; }
    public String getCurrency() { return currency; }
    public BigDecimal getAreaSquareMeters() { return areaSquareMeters; }
    public int getRoomCount() { return roomCount; }
    public Set<String> getFeatures() { return features; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getPublishedAt() { return publishedAt; }
    public Long getVersion() { return version; }

    public record AddressValue(String city, String district, String line1) {}
    public record GeoLocationValue(BigDecimal latitude, BigDecimal longitude) {}
}
