package com.ecommerce.admin.warehouse.api;

import com.ecommerce.admin.warehouse.query.WarehouseQueryService;
import com.ecommerce.admin.warehouse.query.WarehouseView;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Warehouses", description = "Warehouse list")
@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final WarehouseQueryService queries;

    public WarehouseController(WarehouseQueryService queries) {
        this.queries = queries;
    }

    @GetMapping
    public List<WarehouseView> list() {
        return queries.findAll();
    }
}
