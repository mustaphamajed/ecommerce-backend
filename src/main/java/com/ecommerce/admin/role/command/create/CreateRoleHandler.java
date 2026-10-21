package com.ecommerce.admin.role.command.create;

import com.ecommerce.admin.common.CommandHandler;
import com.ecommerce.admin.role.domain.Role;
import com.ecommerce.admin.role.domain.RoleRepository;
import com.ecommerce.admin.role.exception.DuplicateRoleNameException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateRoleHandler implements CommandHandler<CreateRoleCommand, Long> {

    private final RoleRepository repository;

    public CreateRoleHandler(RoleRepository repository) {
        this.repository = repository;
    }

    @Override
    public Long handle(CreateRoleCommand command) {
        if (repository.existsByNameIgnoreCase(command.name())) {
            throw new DuplicateRoleNameException(command.name());
        }
        var role = new Role(command.name(), command.description());
        role.replacePermissions(command.permissions());
        return repository.save(role).getId();
    }
}
