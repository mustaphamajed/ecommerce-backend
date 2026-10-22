package com.ecommerce.admin.user.domain;

import com.ecommerce.admin.role.RoleLookup;
import com.ecommerce.admin.role.domain.Role;
import com.ecommerce.admin.user.WarehouseDirectory;
import com.ecommerce.admin.user.WarehouseSummary;
import com.ecommerce.admin.user.exception.UnknownRolesException;
import com.ecommerce.admin.user.exception.UnknownWarehousesException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class UserAssignments {

    private final RoleLookup roleLookup;
    private final WarehouseDirectory warehouseDirectory;

    public UserAssignments(RoleLookup roleLookup, WarehouseDirectory warehouseDirectory) {
        this.roleLookup = roleLookup;
        this.warehouseDirectory = warehouseDirectory;
    }

    public Set<Role> resolveRoles(Set<Long> roleIds) {
        var roles = new HashSet<>(roleLookup.findAllById(roleIds));
        if (roles.size() != roleIds.size()) {
            var unknownIds = new HashSet<>(roleIds);
            unknownIds.removeAll(roles.stream().map(Role::getId).collect(Collectors.toSet()));
            throw new UnknownRolesException(unknownIds);
        }
        return roles;
    }

    public Set<Long> resolveWarehouses(Set<Long> warehouseIds) {
        var knownIds = warehouseDirectory.findByIds(warehouseIds).stream()
                .map(WarehouseSummary::id)
                .collect(Collectors.toSet());
        if (knownIds.size() != warehouseIds.size()) {
            var unknownIds = new HashSet<>(warehouseIds);
            unknownIds.removeAll(knownIds);
            throw new UnknownWarehousesException(unknownIds);
        }
        return Set.copyOf(warehouseIds);
    }
}
