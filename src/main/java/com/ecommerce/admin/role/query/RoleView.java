package com.ecommerce.admin.role.query;

import com.ecommerce.admin.role.domain.Permission;
import com.ecommerce.admin.role.domain.Role;
import java.util.List;

public record RoleView(Long id, String name, String description, boolean builtIn, List<String> permissions) {

    public static RoleView from(Role role) {
        return new RoleView(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.isBuiltIn(),
                role.getPermissions().stream().map(Permission::authority).sorted().toList());
    }
}
