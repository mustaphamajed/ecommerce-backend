package com.ecommerce.admin.auth.domain;

import java.time.Duration;

public record IssuedToken(String value, Duration expiresIn) {
}
