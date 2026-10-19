package com.ecommerce.admin.auth.command.refresh;

import com.ecommerce.admin.auth.domain.AuthTokens;
import com.ecommerce.admin.auth.domain.TokenService;
import com.ecommerce.admin.auth.exception.InvalidRefreshTokenException;
import com.ecommerce.admin.common.CommandHandler;
import com.ecommerce.admin.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RefreshTokensHandler implements CommandHandler<RefreshTokensCommand, AuthTokens> {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    public RefreshTokensHandler(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @Override
    public AuthTokens handle(RefreshTokensCommand command) {
        if (command.refreshToken() == null || command.refreshToken().isBlank()) {
            throw new InvalidRefreshTokenException();
        }
        Long userId = tokenService.readRefreshToken(command.refreshToken());
        var user = userRepository.findById(userId)
                .filter(found -> found.isEnabled())
                .orElseThrow(InvalidRefreshTokenException::new);
        return tokenService.issueTokens(user);
    }
}
