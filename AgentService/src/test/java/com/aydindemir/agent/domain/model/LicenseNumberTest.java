package com.aydindemir.agent.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.aydindemir.agent.domain.exception.InvalidLicenseNumberException;

class LicenseNumberTest {

    @Test
    void shouldCreateLicenseNumberAndTrimOuterWhitespace() {
        LicenseNumber licenseNumber = new LicenseNumber("  TR-AG-12345  ");

        assertThat(licenseNumber.value()).isEqualTo("TR-AG-12345");
    }

    @Test
    void shouldRejectNullLicenseNumber() {
        assertThatThrownBy(() -> new LicenseNumber(null))
                .isInstanceOf(InvalidLicenseNumberException.class);
    }

    @Test
    void shouldRejectBlankLicenseNumber() {
        assertThatThrownBy(() -> new LicenseNumber("   "))
                .isInstanceOf(InvalidLicenseNumberException.class);
    }
}
