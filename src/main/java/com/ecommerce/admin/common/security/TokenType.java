package com.ecommerce.admin.common.security;

public enum TokenType {
    ACCESS,
    REFRESH;

    public static final String CLAIM = "type";

    public String claimValue() {
        return name().toLowerCase();
    }
}
