package com.ecommerce.admin.stock.query;

import com.ecommerce.admin.stock.domain.Warehouse;

public record WarehouseView(Long id, String code, String name) {

    public static WarehouseView from(Warehouse warehouse) {
        return new WarehouseView(warehouse.getId(), warehouse.getCode(), warehouse.getName());
    }
}
