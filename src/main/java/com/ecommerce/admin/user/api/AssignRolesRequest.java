package com.ecommerce.admin.user.api;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record AssignRolesRequest(@NotEmpty Set<@NotNull Long> roleIds) {
}
