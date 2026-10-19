package com.ecommerce.admin.common;

public final class Strings {

    private Strings() {
    }

    public static String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
