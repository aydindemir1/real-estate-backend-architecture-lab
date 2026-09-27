package com.aydindemir.agent.application.exception;

import com.aydindemir.agent.domain.model.LicenseNumber;

public class DuplicateLicenseNumberException extends RuntimeException {

    public DuplicateLicenseNumberException(LicenseNumber licenseNumber) {
        super("License number is already in use: " + licenseNumber.value());
    }
}
