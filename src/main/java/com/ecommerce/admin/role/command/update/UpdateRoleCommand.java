package com.ecommerce.admin.role.command.update;

import com.ecommerce.admin.common.Strings;
import com.ecommerce.admin.role.domain.Permission;
import com.ecommerce.admin.role.domain.RoleNames;
import java.util.Set;

public record UpdateRoleCommand(Long id, String name, String description, Set<Permission> permissions) {

    public UpdateRoleCommand {
        name = RoleNames.normalize(name);
        description = Strings.trimToNull(description);
        permissions = Set.copyOf(permissions);
    }
}
