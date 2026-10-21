package com.ecommerce.admin.role.command.create;

import com.ecommerce.admin.common.Strings;
import com.ecommerce.admin.role.domain.Permission;
import com.ecommerce.admin.role.domain.RoleNames;
import java.util.Set;

public record CreateRoleCommand(String name, String description, Set<Permission> permissions) {

    public CreateRoleCommand {
        name = RoleNames.normalize(name);
        description = Strings.trimToNull(description);
        permissions = Set.copyOf(permissions);
    }
}
