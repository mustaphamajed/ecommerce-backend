package com.ecommerce.admin.role.command.delete;

import com.ecommerce.admin.common.CommandHandler;
import com.ecommerce.admin.role.domain.RoleRepository;
import com.ecommerce.admin.role.exception.BuiltInRoleException;
import com.ecommerce.admin.role.exception.RoleInUseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteRoleHandler implements CommandHandler<DeleteRoleCommand, Void> {

    private final RoleRepository repository;

    public DeleteRoleHandler(RoleRepository repository) {
        this.repository = repository;
    }

    @Override
    public Void handle(DeleteRoleCommand command) {
        var role = repository.findByIdOrThrow(command.roleId());
        if (role.isBuiltIn()) {
            throw BuiltInRoleException.cannotDelete(role.getName());
        }
        long userCount = repository.countUsersWithRole(role.getId());
        if (userCount > 0) {
            throw new RoleInUseException(role.getName(), userCount);
        }
        repository.delete(role);
        return null;
    }
}
