package com.aydindemir.buyer.domain.model;

public record RoomRange(int min, int max) {

    public RoomRange {
        if (min < 0) {
            throw new IllegalArgumentException("min must be greater than or equal to zero");
        }
        if (max < min) {
            throw new IllegalArgumentException("max must be greater than or equal to min");
        }
    }
}
