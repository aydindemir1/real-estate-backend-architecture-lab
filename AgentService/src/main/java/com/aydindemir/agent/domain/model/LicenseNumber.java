package com.aydindemir.agent.domain.model;

import com.aydindemir.agent.domain.exception.InvalidLicenseNumberException;

public record LicenseNumber(String value) {

    public LicenseNumber {
        if (value == null) {
            throw new InvalidLicenseNumberException("License number must not be null");
        }

        value = value.trim();

        if (value.isBlank()) {
            throw new InvalidLicenseNumberException("License number must not be blank");
        }
    }
}
