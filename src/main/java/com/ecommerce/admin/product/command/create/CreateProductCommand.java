package com.ecommerce.admin.product.command.create;

import com.ecommerce.admin.common.Strings;
import java.math.BigDecimal;
import java.util.Locale;

public record CreateProductCommand(String sku, String name, String description, BigDecimal price, Long categoryId) {

    public CreateProductCommand {
        sku = sku.trim().toUpperCase(Locale.ROOT);
        name = name.trim();
        description = Strings.trimToNull(description);
    }
}
