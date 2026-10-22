package com.ecommerce.admin.role.command.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.ecommerce.admin.role.domain.Permission;
import com.ecommerce.admin.role.domain.Role;
import com.ecommerce.admin.role.domain.RoleRepository;
import com.ecommerce.admin.role.exception.AdminLockoutException;
import com.ecommerce.admin.role.exception.BuiltInRoleException;
import com.ecommerce.admin.role.exception.DuplicateRoleNameException;
import java.util.EnumSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UpdateRoleHandlerTest {

    private static final long ROLE_ID = 3L;

    @Mock
    private RoleRepository repository;

    @InjectMocks
    private UpdateRoleHandler handler;

    @Test
    void adminRoleCannotLoseUserOrRoleManagement() {
        givenRole(builtIn(Role.ADMIN));

        var withoutRoleManage = EnumSet.of(Permission.USER_MANAGE, Permission.PRODUCT_READ);

        assertThatThrownBy(() -> handler.handle(command(Role.ADMIN, withoutRoleManage)))
                .isInstanceOf(AdminLockoutException.class)
                .hasMessageContaining("role:manage")
                .hasMessageNotContaining("user:manage");
    }

    @Test
    void adminRoleCanChangeOtherPermissions() {
        var admin = builtIn(Role.ADMIN);
        givenRole(admin);
        var permissions = EnumSet.of(Permission.USER_MANAGE, Permission.ROLE_MANAGE, Permission.STOCK_READ);

        handler.handle(command(Role.ADMIN, permissions));

        assertThat(admin.getPermissions()).containsExactlyInAnyOrderElementsOf(permissions);
    }

    @Test
    void builtInRolesKeepTheirName() {
        givenRole(builtIn("VIEWER"));

        assertThatThrownBy(() -> handler.handle(command("READER", Set.of(Permission.PRODUCT_READ))))
                .isInstanceOf(BuiltInRoleException.class);
    }

    @Test
    void customRolesCanBeRenamedAndGetNewPermissions() {
        var support = new Role("SUPPORT", null);
        givenRole(support);

        handler.handle(command("CUSTOMER_SUPPORT", Set.of(Permission.USER_READ)));

        assertThat(support.getName()).isEqualTo("CUSTOMER_SUPPORT");
        assertThat(support.getPermissions()).containsExactly(Permission.USER_READ);
    }

    @Test
    void namesMustBeUnique() {
        givenRole(new Role("SUPPORT", null));
        when(repository.existsByNameIgnoreCaseAndIdNot("VIEWER", ROLE_ID)).thenReturn(true);

        assertThatThrownBy(() -> handler.handle(command("VIEWER", Set.of(Permission.USER_READ))))
                .isInstanceOf(DuplicateRoleNameException.class);
    }

    private void givenRole(Role role) {
        ReflectionTestUtils.setField(role, "id", ROLE_ID);
        when(repository.findByIdOrThrow(ROLE_ID)).thenReturn(role);
    }

    private static Role builtIn(String name) {
        var role = new Role(name, null);
        ReflectionTestUtils.setField(role, "builtIn", true);
        return role;
    }

    private static UpdateRoleCommand command(String name, Set<Permission> permissions) {
        return new UpdateRoleCommand(ROLE_ID, name, null, permissions);
    }
}
