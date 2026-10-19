package com.ecommerce.admin.auth.domain;

public record AuthTokens(IssuedToken accessToken, IssuedToken refreshToken) {
}
