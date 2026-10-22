package com.ecommerce.admin.user;

import java.util.Set;

public record Actor(Long id, String fullName, boolean admin, Set<Long> warehouseIds) {

    public boolean canWorkIn(Long warehouseId) {
        return admin || warehouseIds.contains(warehouseId);
    }
}
