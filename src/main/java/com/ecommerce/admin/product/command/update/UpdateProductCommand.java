package com.ecommerce.admin.product.command.update;

import com.ecommerce.admin.common.Strings;
import java.math.BigDecimal;
import java.util.Locale;

public record UpdateProductCommand(
        Long id, String sku, String name, String description, BigDecimal price, Long categoryId) {

    public UpdateProductCommand {
        sku = sku.trim().toUpperCase(Locale.ROOT);
        name = name.trim();
        description = Strings.trimToNull(description);
    }
}
