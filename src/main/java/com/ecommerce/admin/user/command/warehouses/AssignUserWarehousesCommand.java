package com.ecommerce.admin.user.command.warehouses;

import java.util.Set;

public record AssignUserWarehousesCommand(Long userId, Set<Long> warehouseIds) {

    public AssignUserWarehousesCommand {
        warehouseIds = Set.copyOf(warehouseIds);
    }
}
