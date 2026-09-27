package com.aydindemir.agent.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.aydindemir.agent.domain.exception.InvalidAgencyInfoException;

class AgencyInfoTest {

    @Test
    void shouldCreateAgencyInfoAndNormalizeOptionalFields() {
        AgencyInfo agencyInfo = new AgencyInfo(
                "  Example Realty  ",
                "  REG-001  ",
                "  +90 555 000 00 00  "
        );

        assertThat(agencyInfo.agencyName()).isEqualTo("Example Realty");
        assertThat(agencyInfo.registrationNumber()).isEqualTo("REG-001");
        assertThat(agencyInfo.officePhone()).isEqualTo("+90 555 000 00 00");
    }

    @Test
    void shouldNormalizeBlankOptionalFieldsToNull() {
        AgencyInfo agencyInfo = new AgencyInfo("Example Realty", "   ", " ");

        assertThat(agencyInfo.registrationNumber()).isNull();
        assertThat(agencyInfo.officePhone()).isNull();
    }

    @Test
    void shouldRejectNullAgencyName() {
        assertThatThrownBy(() -> new AgencyInfo(null, null, null))
                .isInstanceOf(InvalidAgencyInfoException.class);
    }

    @Test
    void shouldRejectBlankAgencyName() {
        assertThatThrownBy(() -> new AgencyInfo("   ", null, null))
                .isInstanceOf(InvalidAgencyInfoException.class);
    }
}
