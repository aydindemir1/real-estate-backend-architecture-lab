package com.aydindemir.agent.domain.model;

import com.aydindemir.agent.domain.exception.InvalidAgentStateException;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Agent {

    private final AgentId id;
    private final UserId userId;
    private final LicenseNumber licenseNumber;
    private final AgencyInfo agencyInfo;
    private AgentStatus status;
    private AvailabilityStatus availability;
    private final Instant createdAt;
    private Instant updatedAt;
    private final long version;

    private Agent(
            AgentId id,
            UserId userId,
            LicenseNumber licenseNumber,
            AgencyInfo agencyInfo,
            AgentStatus status,
            AvailabilityStatus availability,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        this.id = Objects.requireNonNull(id, "Agent id must not be null");
        this.userId = Objects.requireNonNull(userId, "User id must not be null");
        this.licenseNumber = Objects.requireNonNull(licenseNumber, "License number must not be null");
        this.agencyInfo = Objects.requireNonNull(agencyInfo, "Agency info must not be null");
        this.status = Objects.requireNonNull(status, "Agent status must not be null");
        this.availability = Objects.requireNonNull(availability, "Availability status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at must not be null");

        if (version < 0) {
            throw new IllegalArgumentException("Version must not be negative");
        }

        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("Updated at must not be before created at");
        }

        validateState(status, availability);
        this.version = version;
    }

    public static Agent create(
            UserId userId,
            LicenseNumber licenseNumber,
            AgencyInfo agencyInfo,
            Clock clock
    ) {
        Objects.requireNonNull(clock, "Clock must not be null");
        Instant now = Instant.now(clock);

        return new Agent(
                new AgentId(UUID.randomUUID()),
                userId,
                licenseNumber,
                agencyInfo,
                AgentStatus.ACTIVE,
                AvailabilityStatus.OFFLINE,
                now,
                now,
                0L
        );
    }

    public static Agent reconstitute(
            AgentId id,
            UserId userId,
            LicenseNumber licenseNumber,
            AgencyInfo agencyInfo,
            AgentStatus status,
            AvailabilityStatus availability,
            Instant createdAt,
            Instant updatedAt,
            long version
    ) {
        return new Agent(
                id,
                userId,
                licenseNumber,
                agencyInfo,
                status,
                availability,
                createdAt,
                updatedAt,
                version
        );
    }

    public void suspend(Clock clock) {
        Objects.requireNonNull(clock, "Clock must not be null");

        if (status == AgentStatus.SUSPENDED) {
            return;
        }

        if (status == AgentStatus.INACTIVE) {
            throw new InvalidAgentStateException("Inactive agent cannot be suspended");
        }

        status = AgentStatus.SUSPENDED;
        availability = AvailabilityStatus.OFFLINE;
        touch(clock);
    }

    public void activate(Clock clock) {
        Objects.requireNonNull(clock, "Clock must not be null");

        if (status == AgentStatus.ACTIVE) {
            return;
        }

        if (status == AgentStatus.INACTIVE) {
            throw new InvalidAgentStateException("Inactive agent cannot be activated");
        }

        status = AgentStatus.ACTIVE;
        availability = AvailabilityStatus.OFFLINE;
        touch(clock);
    }

    public void deactivate(Clock clock) {
        Objects.requireNonNull(clock, "Clock must not be null");

        if (status == AgentStatus.INACTIVE) {
            return;
        }

        status = AgentStatus.INACTIVE;
        availability = AvailabilityStatus.OFFLINE;
        touch(clock);
    }

    public void changeAvailability(AvailabilityStatus newAvailability, Clock clock) {
        Objects.requireNonNull(newAvailability, "Availability status must not be null");
        Objects.requireNonNull(clock, "Clock must not be null");

        if (availability == newAvailability) {
            return;
        }

        if (status != AgentStatus.ACTIVE && newAvailability != AvailabilityStatus.OFFLINE) {
            throw new InvalidAgentStateException(
                    "Only active agents can be available or busy"
            );
        }

        availability = newAvailability;
        touch(clock);
    }

    private static void validateState(AgentStatus status, AvailabilityStatus availability) {
        if (status != AgentStatus.ACTIVE && availability != AvailabilityStatus.OFFLINE) {
            throw new InvalidAgentStateException(
                    "Suspended or inactive agent must be offline"
            );
        }
    }

    private void touch(Clock clock) {
        updatedAt = Instant.now(clock);
    }

    public AgentId id() {
        return id;
    }

    public UserId userId() {
        return userId;
    }

    public LicenseNumber licenseNumber() {
        return licenseNumber;
    }

    public AgencyInfo agencyInfo() {
        return agencyInfo;
    }

    public AgentStatus status() {
        return status;
    }

    public AvailabilityStatus availability() {
        return availability;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public long version() {
        return version;
    }
}
