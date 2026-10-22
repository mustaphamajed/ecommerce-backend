package com.ecommerce.admin.user.query;

import com.ecommerce.admin.user.WarehouseSummary;
import java.time.Instant;
import java.util.List;

public record UserView(
        Long id,
        String email,
        String firstName,
        String lastName,
        boolean enabled,
        List<RoleSummary> roles,
        List<WarehouseSummary> warehouses,
        Instant createdAt,
        Instant updatedAt) {
}
