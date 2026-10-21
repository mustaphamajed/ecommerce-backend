package com.ecommerce.admin.role.domain;

import java.util.Locale;

public final class RoleNames {

    private RoleNames() {
    }

    public static String normalize(String name) {
        return name.trim().replaceAll("[\\s-]+", "_").toUpperCase(Locale.ROOT);
    }
}
