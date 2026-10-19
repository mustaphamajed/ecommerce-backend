package com.ecommerce.admin.user.command.disable;

import com.ecommerce.admin.common.CommandHandler;
import com.ecommerce.admin.user.domain.LastAdminGuard;
import com.ecommerce.admin.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DisableUserHandler implements CommandHandler<DisableUserCommand, Void> {

    private final UserRepository repository;
    private final LastAdminGuard lastAdminGuard;

    public DisableUserHandler(UserRepository repository, LastAdminGuard lastAdminGuard) {
        this.repository = repository;
        this.lastAdminGuard = lastAdminGuard;
    }

    @Override
    public Void handle(DisableUserCommand command) {
        var user = repository.findByIdOrThrow(command.userId());
        lastAdminGuard.ensureAnotherAdminRemains(user);
        user.disable();
        return null;
    }
}
