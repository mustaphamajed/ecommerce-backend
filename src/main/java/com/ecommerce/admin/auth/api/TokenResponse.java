package com.ecommerce.admin.auth.api;

import com.ecommerce.admin.auth.domain.IssuedToken;

public record TokenResponse(String accessToken, String tokenType, long expiresIn) {

    public static TokenResponse from(IssuedToken accessToken) {
        return new TokenResponse(accessToken.value(), "Bearer", accessToken.expiresIn().toSeconds());
    }
}
