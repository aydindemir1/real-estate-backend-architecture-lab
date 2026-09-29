package com.aydindemir.buyer.adapter.out.persistence.couchbase.document;

public record NotificationSettingsDocument(
        boolean emailEnabled,
        boolean pushEnabled,
        boolean smsEnabled
) {
}
