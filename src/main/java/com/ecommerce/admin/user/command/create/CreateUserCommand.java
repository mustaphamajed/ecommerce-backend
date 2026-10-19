package com.ecommerce.admin.user.command.create;

import java.util.Locale;
import java.util.Set;

public record CreateUserCommand(
        String email,
        String firstName,
        String lastName,
        String password,
        Set<Long> roleIds,
        Set<Long> warehouseIds) {

    public CreateUserCommand {
        email = email.trim().toLowerCase(Locale.ROOT);
        firstName = firstName.trim();
        lastName = lastName.trim();
        roleIds = roleIds == null ? Set.of() : Set.copyOf(roleIds);
        warehouseIds = warehouseIds == null ? Set.of() : Set.copyOf(warehouseIds);
    }
}
