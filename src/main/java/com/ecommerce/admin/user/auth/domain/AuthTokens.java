package com.ecommerce.admin.user.auth.domain;

public record AuthTokens(IssuedToken accessToken, IssuedToken refreshToken) {
}
