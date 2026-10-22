package com.ecommerce.admin.user.auth.command.login;

import java.util.Locale;

public record LoginCommand(String email, String password) {

    public LoginCommand {
        email = email.trim().toLowerCase(Locale.ROOT);
    }
}
