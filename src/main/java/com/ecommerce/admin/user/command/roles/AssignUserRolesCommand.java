package com.ecommerce.admin.user.command.roles;

import java.util.Set;

public record AssignUserRolesCommand(Long userId, Set<Long> roleIds) {

    public AssignUserRolesCommand {
        roleIds = Set.copyOf(roleIds);
    }
}
