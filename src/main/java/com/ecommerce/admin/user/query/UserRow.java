package com.ecommerce.admin.user.query;

import java.time.Instant;

public record UserRow(
        Long id,
        String email,
        String firstName,
        String lastName,
        boolean enabled,
        Instant createdAt,
        Instant updatedAt) {
}
