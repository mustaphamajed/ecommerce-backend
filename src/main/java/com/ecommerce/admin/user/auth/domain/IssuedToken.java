package com.ecommerce.admin.user.auth.domain;

import java.time.Duration;

public record IssuedToken(String value, Duration expiresIn) {
}
