package com.ecommerce.admin.user.command.password;

public record ResetUserPasswordCommand(Long userId, String password) {
}
