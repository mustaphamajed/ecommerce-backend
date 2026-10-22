package com.ecommerce.admin.stock;

import com.ecommerce.admin.stock.domain.Warehouse;
import com.ecommerce.admin.stock.domain.WarehouseRepository;
import com.ecommerce.admin.user.WarehouseDirectory;
import com.ecommerce.admin.user.WarehouseSummary;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class StockWarehouseDirectory implements WarehouseDirectory {

    private final WarehouseRepository warehouses;

    public StockWarehouseDirectory(WarehouseRepository warehouses) {
        this.warehouses = warehouses;
    }

    @Override
    public List<WarehouseSummary> findAll() {
        return warehouses.findAll().stream().map(StockWarehouseDirectory::toSummary).toList();
    }

    @Override
    public List<WarehouseSummary> findByIds(Collection<Long> ids) {
        return warehouses.findAllById(ids).stream().map(StockWarehouseDirectory::toSummary).toList();
    }

    private static WarehouseSummary toSummary(Warehouse warehouse) {
        return new WarehouseSummary(warehouse.getId(), warehouse.getCode(), warehouse.getName());
    }
}
