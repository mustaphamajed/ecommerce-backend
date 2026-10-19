package com.ecommerce.admin.user.api;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record AssignWarehousesRequest(@NotNull Set<@NotNull Long> warehouseIds) {
}
