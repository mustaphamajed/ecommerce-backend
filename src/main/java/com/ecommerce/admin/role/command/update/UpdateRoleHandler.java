package com.ecommerce.admin.role.command.update;

import com.ecommerce.admin.common.CommandHandler;
import com.ecommerce.admin.role.domain.Permission;
import com.ecommerce.admin.role.domain.Role;
import com.ecommerce.admin.role.domain.RoleRepository;
import com.ecommerce.admin.role.exception.AdminLockoutException;
import com.ecommerce.admin.role.exception.BuiltInRoleException;
import com.ecommerce.admin.role.exception.DuplicateRoleNameException;
import java.util.EnumSet;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateRoleHandler implements CommandHandler<UpdateRoleCommand, Void> {

    private static final Set<Permission> ADMIN_REQUIRED_PERMISSIONS =
            EnumSet.of(Permission.USER_MANAGE, Permission.ROLE_MANAGE);

    private final RoleRepository repository;

    public UpdateRoleHandler(RoleRepository repository) {
        this.repository = repository;
    }

    @Override
    public Void handle(UpdateRoleCommand command) {
        var role = repository.findByIdOrThrow(command.id());
        if (role.isBuiltIn() && !role.getName().equals(command.name())) {
            throw BuiltInRoleException.cannotRename(role.getName());
        }
        if (repository.existsByNameIgnoreCaseAndIdNot(command.name(), command.id())) {
            throw new DuplicateRoleNameException(command.name());
        }
        if (role.getName().equals(Role.ADMIN)) {
            ensureAdminKeepsRequiredPermissions(command.permissions());
        }
        role.rename(command.name(), command.description());
        role.replacePermissions(command.permissions());
        return null;
    }

    private void ensureAdminKeepsRequiredPermissions(Set<Permission> permissions) {
        var missing = EnumSet.copyOf(ADMIN_REQUIRED_PERMISSIONS);
        missing.removeAll(permissions);
        if (!missing.isEmpty()) {
            throw new AdminLockoutException(missing);
        }
    }
}
