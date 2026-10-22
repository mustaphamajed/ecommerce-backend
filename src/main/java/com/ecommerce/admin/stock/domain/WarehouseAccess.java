package com.ecommerce.admin.stock.domain;

import com.ecommerce.admin.role.domain.Role;
import com.ecommerce.admin.stock.exception.WarehouseAccessDeniedException;
import com.ecommerce.admin.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class WarehouseAccess {

    public boolean canAdjust(User actor, Warehouse warehouse) {
        return actor.hasRole(Role.ADMIN)
                || actor.getWarehouses().stream().anyMatch(assigned -> assigned.getId().equals(warehouse.getId()));
    }

    public void ensureCanAdjust(User actor, Warehouse warehouse) {
        if (!canAdjust(actor, warehouse)) {
            throw new WarehouseAccessDeniedException(warehouse.getCode());
        }
    }
}
