package com.ecommerce.admin.common.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public class TokenTypeValidator implements OAuth2TokenValidator<Jwt> {

    private final TokenType expectedType;

    public TokenTypeValidator(TokenType expectedType) {
        this.expectedType = expectedType;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        if (expectedType.claimValue().equals(jwt.getClaimAsString(TokenType.CLAIM))) {
            return OAuth2TokenValidatorResult.success();
        }
        return OAuth2TokenValidatorResult.failure(
                new OAuth2Error("invalid_token", "Expected a " + expectedType.claimValue() + " token", null));
    }
}
