package com.ecommerce.admin.user.auth.api;

import com.ecommerce.admin.user.auth.domain.IssuedToken;

public record TokenResponse(String accessToken, String tokenType, long expiresIn) {

    public static TokenResponse from(IssuedToken accessToken) {
        return new TokenResponse(accessToken.value(), "Bearer", accessToken.expiresIn().toSeconds());
    }
}
