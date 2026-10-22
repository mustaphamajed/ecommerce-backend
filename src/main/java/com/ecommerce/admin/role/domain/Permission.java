package com.ecommerce.admin.role.domain;

import java.util.Arrays;
import java.util.Optional;

public enum Permission {
    PRODUCT_READ("product:read"),
    PRODUCT_WRITE("product:write"),
    STOCK_READ("stock:read"),
    STOCK_ADJUST("stock:adjust"),
    USER_READ("user:read"),
    USER_MANAGE("user:manage"),
    ROLE_MANAGE("role:manage"),
    AUDIT_READ("audit:read"),
    SYSTEM_MONITOR("system:monitor");

    private final String authority;

    Permission(String authority) {
        this.authority = authority;
    }

    public String authority() {
        return authority;
    }

    public static Optional<Permission> fromAuthority(String authority) {
        return Arrays.stream(values()).filter(permission -> permission.authority.equals(authority)).findFirst();
    }
}
