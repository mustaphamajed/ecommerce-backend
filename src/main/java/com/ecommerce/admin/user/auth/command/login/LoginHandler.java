package com.ecommerce.admin.user.auth.command.login;

import com.ecommerce.admin.user.auth.domain.AuthTokens;
import com.ecommerce.admin.user.auth.domain.TokenService;
import com.ecommerce.admin.user.auth.exception.AccountDisabledException;
import com.ecommerce.admin.user.auth.exception.InvalidCredentialsException;
import com.ecommerce.admin.common.CommandHandler;
import com.ecommerce.admin.user.domain.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LoginHandler implements CommandHandler<LoginCommand, AuthTokens> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final String unknownUserHash;

    public LoginHandler(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.unknownUserHash = passwordEncoder.encode("unknown-user-placeholder");
    }

    @Override
    public AuthTokens handle(LoginCommand command) {
        var user = userRepository.findByEmail(command.email()).orElse(null);
        String hash = user != null ? user.getPasswordHash() : unknownUserHash;
        boolean passwordMatches = passwordEncoder.matches(command.password(), hash);
        if (user == null || !passwordMatches) {
            throw new InvalidCredentialsException();
        }
        if (!user.isEnabled()) {
            throw new AccountDisabledException();
        }
        return tokenService.issueTokens(user);
    }
}
