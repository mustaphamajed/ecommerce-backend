package com.ecommerce.admin.user.auth.api;

import com.ecommerce.admin.user.auth.domain.IssuedToken;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenCookie {

    public static final String NAME = "refresh_token";

    private static final String PATH = "/api/auth";

    private final boolean secure;

    public RefreshTokenCookie(@Value("${app.auth.refresh-cookie.secure}") boolean secure) {
        this.secure = secure;
    }

    public ResponseCookie create(IssuedToken refreshToken) {
        return build(refreshToken.value(), refreshToken.expiresIn());
    }

    public ResponseCookie clear() {
        return build("", Duration.ZERO);
    }

    private ResponseCookie build(String value, Duration maxAge) {
        return ResponseCookie.from(NAME, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path(PATH)
                .maxAge(maxAge)
                .build();
    }
}
