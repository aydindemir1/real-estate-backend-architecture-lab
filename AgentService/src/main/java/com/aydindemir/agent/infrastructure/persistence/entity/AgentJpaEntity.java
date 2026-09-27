package com.aydindemir.agent.infrastructure.persistence.entity;

import com.aydindemir.agent.domain.model.AgentStatus;
import com.aydindemir.agent.domain.model.AvailabilityStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "agents",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_agents_user_id", columnNames = "user_id"),
                @UniqueConstraint(name = "uk_agents_license_number", columnNames = "license_number")
        }
)
public class AgentJpaEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "id", nullable = false, length = 36)
    private UUID id;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "user_id", nullable = false, length = 36)
    private UUID userId;

    @Column(name = "license_number", nullable = false, length = 128)
    private String licenseNumber;

    @Column(name = "agency_name", nullable = false, length = 255)
    private String agencyName;

    @Column(name = "agency_registration_number", length = 128)
    private String agencyRegistrationNumber;

    @Column(name = "office_phone", length = 64)
    private String officePhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private AgentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", nullable = false, length = 32)
    private AvailabilityStatus availabilityStatus;

    @JdbcTypeCode(SqlTypes.TIMESTAMP_WITH_TIMEZONE)
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6)")
    private Instant createdAt;

    @JdbcTypeCode(SqlTypes.TIMESTAMP_WITH_TIMEZONE)
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP(6)")
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    protected AgentJpaEntity() {
    }

    public AgentJpaEntity(
            UUID id,
            UUID userId,
            String licenseNumber,
            String agencyName,
            String agencyRegistrationNumber,
            String officePhone,
            AgentStatus status,
            AvailabilityStatus availabilityStatus,
            Instant createdAt,
            Instant updatedAt,
            Long version
    ) {
        this.id = id;
        this.userId = userId;
        this.licenseNumber = licenseNumber;
        this.agencyName = agencyName;
        this.agencyRegistrationNumber = agencyRegistrationNumber;
        this.officePhone = officePhone;
        this.status = status;
        this.availabilityStatus = availabilityStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public String getAgencyRegistrationNumber() {
        return agencyRegistrationNumber;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public AgentStatus getStatus() {
        return status;
    }

    public AvailabilityStatus getAvailabilityStatus() {
        return availabilityStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }
}
