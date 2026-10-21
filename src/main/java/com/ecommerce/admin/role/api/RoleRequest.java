package com.ecommerce.admin.role.api;

import com.ecommerce.admin.role.domain.Permission;
import com.ecommerce.admin.role.exception.UnknownPermissionException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

public record RoleRequest(
        @NotBlank
        @Size(max = 50)
        @Pattern(regexp = "^\\s*[A-Za-z][A-Za-z0-9 _-]*\\s*$",
                message = "must start with a letter and contain only letters, digits, spaces, dashes and underscores")
        String name,
        @Size(max = 500) String description,
        @NotEmpty(message = "select at least one permission") Set<@NotNull String> permissions) {

    public Set<Permission> toPermissions() {
        var resolved = EnumSet.noneOf(Permission.class);
        var unknown = new TreeSet<String>();
        for (String authority : permissions) {
            Permission.fromAuthority(authority).ifPresentOrElse(resolved::add, () -> unknown.add(authority));
        }
        if (!unknown.isEmpty()) {
            throw new UnknownPermissionException(unknown);
        }
        return resolved;
    }
}
