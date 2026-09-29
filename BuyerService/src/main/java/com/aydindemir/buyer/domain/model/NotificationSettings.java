package com.aydindemir.buyer.domain.model;

public record NotificationSettings(
        boolean emailEnabled,
        boolean pushEnabled,
        boolean smsEnabled
) {
}
