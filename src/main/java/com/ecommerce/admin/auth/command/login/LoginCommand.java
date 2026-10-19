package com.ecommerce.admin.auth.command.login;

import java.util.Locale;

public record LoginCommand(String email, String password) {

    public LoginCommand {
        email = email.trim().toLowerCase(Locale.ROOT);
    }
}
