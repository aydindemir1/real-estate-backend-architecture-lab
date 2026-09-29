package com.aydindemir.buyer.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoomRangeTest {

    @Test
    void shouldCreateValidRoomRange() {
        RoomRange range = new RoomRange(1, 4);

        assertThat(range.min()).isEqualTo(1);
        assertThat(range.max()).isEqualTo(4);
    }

    @Test
    void shouldAllowZeroAndEqualValues() {
        RoomRange range = new RoomRange(0, 0);

        assertThat(range.min()).isZero();
        assertThat(range.max()).isZero();
    }

    @Test
    void shouldRejectNegativeMin() {
        assertThatThrownBy(() -> new RoomRange(-1, 2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectMaxLowerThanMin() {
        assertThatThrownBy(() -> new RoomRange(3, 2))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
