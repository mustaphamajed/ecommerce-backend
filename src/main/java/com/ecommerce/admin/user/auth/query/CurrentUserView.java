package com.ecommerce.admin.user.auth.query;

import java.util.List;

public record CurrentUserView(
        Long id,
        String email,
        String firstName,
        String lastName,
        List<String> roles,
        List<String> permissions,
        List<Long> warehouseIds) {
}
