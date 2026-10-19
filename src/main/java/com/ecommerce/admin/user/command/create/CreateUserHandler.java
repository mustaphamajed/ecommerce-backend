package com.ecommerce.admin.user.command.create;

import com.ecommerce.admin.common.CommandHandler;
import com.ecommerce.admin.user.domain.User;
import com.ecommerce.admin.user.domain.UserAssignments;
import com.ecommerce.admin.user.domain.UserRepository;
import com.ecommerce.admin.user.exception.DuplicateEmailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateUserHandler implements CommandHandler<CreateUserCommand, Long> {

    private final UserRepository repository;
    private final UserAssignments assignments;
    private final PasswordEncoder passwordEncoder;

    public CreateUserHandler(UserRepository repository, UserAssignments assignments, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.assignments = assignments;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Long handle(CreateUserCommand command) {
        if (repository.existsByEmail(command.email())) {
            throw new DuplicateEmailException(command.email());
        }
        var user = new User(
                command.email(),
                passwordEncoder.encode(command.password()),
                command.firstName(),
                command.lastName());
        user.assignRoles(assignments.resolveRoles(command.roleIds()));
        user.assignWarehouses(assignments.resolveWarehouses(command.warehouseIds()));
        return repository.save(user).getId();
    }
}
