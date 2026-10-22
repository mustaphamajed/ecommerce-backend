package com.ecommerce.admin.auth.domain;

import com.ecommerce.admin.auth.exception.InvalidRefreshTokenException;
import com.ecommerce.admin.common.security.JwtConfig;
import com.ecommerce.admin.common.security.JwtProperties;
import com.ecommerce.admin.common.security.TokenType;
import com.ecommerce.admin.role.domain.Permission;
import com.ecommerce.admin.user.domain.User;
import com.ecommerce.admin.stock.domain.Warehouse;
import java.time.Duration;
import java.time.Instant;
import javax.crypto.SecretKey;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final JwtEncoder encoder;
    private final JwtDecoder refreshTokenDecoder;
    private final JwtProperties properties;

    public TokenService(JwtEncoder encoder, SecretKey jwtSigningKey, JwtProperties properties) {
        this.encoder = encoder;
        this.refreshTokenDecoder = JwtConfig.decoderFor(jwtSigningKey, TokenType.REFRESH);
        this.properties = properties;
    }

    public AuthTokens issueTokens(User user) {
        return new AuthTokens(issueAccessToken(user), issueRefreshToken(user));
    }

    public Long readRefreshToken(String token) {
        try {
            return Long.valueOf(refreshTokenDecoder.decode(token).getSubject());
        } catch (JwtException | NumberFormatException e) {
            throw new InvalidRefreshTokenException();
        }
    }

    private IssuedToken issueAccessToken(User user) {
        var claims = baseClaims(user, TokenType.ACCESS, properties.accessTokenTtl())
                .claim("email", user.getEmail())
                .claim("name", user.getFirstName() + " " + user.getLastName())
                .claim(JwtConfig.PERMISSIONS_CLAIM,
                        user.getPermissions().stream().map(Permission::authority).sorted().toList())
                .claim("warehouses", user.getWarehouses().stream().map(Warehouse::getId).sorted().toList())
                .build();
        return new IssuedToken(encode(claims), properties.accessTokenTtl());
    }

    private IssuedToken issueRefreshToken(User user) {
        var claims = baseClaims(user, TokenType.REFRESH, properties.refreshTokenTtl()).build();
        return new IssuedToken(encode(claims), properties.refreshTokenTtl());
    }

    private JwtClaimsSet.Builder baseClaims(User user, TokenType type, Duration ttl) {
        var now = Instant.now();
        return JwtClaimsSet.builder()
                .issuer("admin-backend")
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plus(ttl))
                .claim(TokenType.CLAIM, type.claimValue());
    }

    private String encode(JwtClaimsSet claims) {
        var header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
