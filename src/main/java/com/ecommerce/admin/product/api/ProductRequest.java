package com.ecommerce.admin.product.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank
        @Size(max = 50)
        @Pattern(regexp = "^\\s*[A-Za-z0-9._-]+\\s*$", message = "must contain only letters, digits, dots, dashes and underscores")
        String sku,
        @NotBlank @Size(max = 200) String name,
        @Size(max = 5000) String description,
        @NotNull @DecimalMin("0.01") @Digits(integer = 10, fraction = 2) BigDecimal price,
        Long categoryId) {
}
