package com.ecommerce.admin.user.command.enable;

import com.ecommerce.admin.common.CommandHandler;
import com.ecommerce.admin.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EnableUserHandler implements CommandHandler<EnableUserCommand, Void> {

    private final UserRepository repository;

    public EnableUserHandler(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Void handle(EnableUserCommand command) {
        repository.findByIdOrThrow(command.userId()).enable();
        return null;
    }
}
