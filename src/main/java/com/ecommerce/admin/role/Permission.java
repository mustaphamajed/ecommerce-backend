package com.ecommerce.admin.role;


public enum Permission {
    PRODUCT_READ("product:read"),
    PRODUCT_WRITE("product:write"),
    STOCK_READ("stock:read"),
    STOCK_ADJUST("stock:adjust"),
    USER_READ("user:read"),
    USER_MANAGE("user:manage"),
    ROLE_MANAGE("role:manage"),
    AUDIT_READ("audit:read");

    private final String authority;

    Permission(String authority) {
        this.authority = authority;
    }

    public String authority() {
        return authority;
    }
}
