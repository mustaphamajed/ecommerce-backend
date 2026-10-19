package com.ecommerce.admin.common;

public interface CommandHandler<C, R> {

    R handle(C command);
}
