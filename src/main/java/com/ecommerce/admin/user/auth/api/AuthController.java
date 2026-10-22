package com.ecommerce.admin.user.auth.api;

import com.ecommerce.admin.user.auth.command.login.LoginCommand;
import com.ecommerce.admin.user.auth.command.login.LoginHandler;
import com.ecommerce.admin.user.auth.command.refresh.RefreshTokensCommand;
import com.ecommerce.admin.user.auth.command.refresh.RefreshTokensHandler;
import com.ecommerce.admin.user.auth.domain.AuthTokens;
import com.ecommerce.admin.user.auth.query.CurrentUserQueryService;
import com.ecommerce.admin.user.auth.query.CurrentUserView;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "Login, token refresh, logout and the current user")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LoginHandler login;
    private final RefreshTokensHandler refreshTokens;
    private final CurrentUserQueryService currentUser;
    private final RefreshTokenCookie refreshTokenCookie;

    public AuthController(
            LoginHandler login,
            RefreshTokensHandler refreshTokens,
            CurrentUserQueryService currentUser,
            RefreshTokenCookie refreshTokenCookie) {
        this.login = login;
        this.refreshTokens = refreshTokens;
        this.currentUser = currentUser;
        this.refreshTokenCookie = refreshTokenCookie;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return withTokens(login.handle(new LoginCommand(request.email(), request.password())));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @CookieValue(name = RefreshTokenCookie.NAME, required = false) String refreshToken) {
        return withTokens(refreshTokens.handle(new RefreshTokensCommand(refreshToken)));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.clear().toString())
                .build();
    }

    @GetMapping("/me")
    public CurrentUserView me(@AuthenticationPrincipal Jwt jwt) {
        return currentUser.findById(Long.valueOf(jwt.getSubject()));
    }

    private ResponseEntity<TokenResponse> withTokens(AuthTokens tokens) {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.create(tokens.refreshToken()).toString())
                .body(TokenResponse.from(tokens.accessToken()));
    }
}
