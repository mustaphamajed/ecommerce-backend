package com.ecommerce.admin.user.command.password;

import com.ecommerce.admin.common.CommandHandler;
import com.ecommerce.admin.user.domain.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ResetUserPasswordHandler implements CommandHandler<ResetUserPasswordCommand, Void> {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public ResetUserPasswordHandler(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Void handle(ResetUserPasswordCommand command) {
        var user = repository.findByIdOrThrow(command.userId());
        user.changePasswordHash(passwordEncoder.encode(command.password()));
        return null;
    }
}
