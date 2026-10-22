package com.ecommerce.admin.user.domain;

import com.ecommerce.admin.role.domain.Role;
import com.ecommerce.admin.role.domain.RoleRepository;
import com.ecommerce.admin.user.exception.UnknownRolesException;
import com.ecommerce.admin.user.exception.UnknownWarehousesException;
import com.ecommerce.admin.stock.domain.Warehouse;
import com.ecommerce.admin.stock.domain.WarehouseRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class UserAssignments {

    private final RoleRepository roleRepository;
    private final WarehouseRepository warehouseRepository;

    public UserAssignments(RoleRepository roleRepository, WarehouseRepository warehouseRepository) {
        this.roleRepository = roleRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public Set<Role> resolveRoles(Set<Long> roleIds) {
        var roles = new HashSet<>(roleRepository.findAllById(roleIds));
        if (roles.size() != roleIds.size()) {
            var unknownIds = new HashSet<>(roleIds);
            unknownIds.removeAll(roles.stream().map(Role::getId).collect(Collectors.toSet()));
            throw new UnknownRolesException(unknownIds);
        }
        return roles;
    }

    public Set<Warehouse> resolveWarehouses(Set<Long> warehouseIds) {
        var warehouses = new HashSet<>(warehouseRepository.findAllById(warehouseIds));
        if (warehouses.size() != warehouseIds.size()) {
            var unknownIds = new HashSet<>(warehouseIds);
            unknownIds.removeAll(warehouses.stream().map(Warehouse::getId).collect(Collectors.toSet()));
            throw new UnknownWarehousesException(unknownIds);
        }
        return warehouses;
    }
}
