package com.ecommerce.admin.user.command.update;

import java.util.Locale;

public record UpdateUserCommand(Long id, String email, String firstName, String lastName) {

    public UpdateUserCommand {
        email = email.trim().toLowerCase(Locale.ROOT);
        firstName = firstName.trim();
        lastName = lastName.trim();
    }
}
