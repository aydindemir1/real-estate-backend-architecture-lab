package com.aydindemir.agent.domain.model;

import com.aydindemir.agent.domain.exception.InvalidAgencyInfoException;

public record AgencyInfo(
        String agencyName,
        String registrationNumber,
        String officePhone
) {

    public AgencyInfo {
        if (agencyName == null) {
            throw new InvalidAgencyInfoException("Agency name must not be null");
        }

        agencyName = agencyName.trim();

        if (agencyName.isBlank()) {
            throw new InvalidAgencyInfoException("Agency name must not be blank");
        }

        registrationNumber = normalizeOptional(registrationNumber);
        officePhone = normalizeOptional(officePhone);
    }

    private static String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        return normalized.isBlank() ? null : normalized;
    }
}
