package com.ecommerce.admin.user.command.update;

import com.ecommerce.admin.common.CommandHandler;
import com.ecommerce.admin.user.domain.UserRepository;
import com.ecommerce.admin.user.exception.DuplicateEmailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateUserHandler implements CommandHandler<UpdateUserCommand, Void> {

    private final UserRepository repository;

    public UpdateUserHandler(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Void handle(UpdateUserCommand command) {
        var user = repository.findByIdOrThrow(command.id());
        if (repository.existsByEmailAndIdNot(command.email(), command.id())) {
            throw new DuplicateEmailException(command.email());
        }
        user.updateProfile(command.email(), command.firstName(), command.lastName());
        return null;
    }
}
