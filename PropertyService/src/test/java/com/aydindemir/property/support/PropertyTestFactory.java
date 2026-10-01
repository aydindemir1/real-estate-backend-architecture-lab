package com.aydindemir.property.support;

import com.aydindemir.property.shared.domain.model.Address;
import com.aydindemir.property.shared.domain.model.AgentId;
import com.aydindemir.property.shared.domain.model.Area;
import com.aydindemir.property.shared.domain.model.GeoLocation;
import com.aydindemir.property.shared.domain.model.Money;
import com.aydindemir.property.shared.domain.model.Property;
import com.aydindemir.property.shared.domain.model.PropertyId;
import com.aydindemir.property.shared.domain.model.SellerId;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.Currency;
import java.util.Set;
import java.util.UUID;

public final class PropertyTestFactory {

    private PropertyTestFactory() {
    }

    public static Property draft(Clock clock) {
        return Property.createDraft(
                PropertyId.newId(),
                new SellerId(UUID.randomUUID()),
                new AgentId(UUID.randomUUID()),
                "Kadikoy apartment",
                "Bright apartment near transit",
                "APARTMENT",
                new Address("Istanbul", "Kadikoy", "Example Street 10"),
                new GeoLocation(new BigDecimal("40.9900"), new BigDecimal("29.0300")),
                new Money(new BigDecimal("6500000"), Currency.getInstance("TRY")),
                new Area(new BigDecimal("125.50")),
                3,
                Set.of("BALCONY", "ELEVATOR"),
                clock);
    }
}
