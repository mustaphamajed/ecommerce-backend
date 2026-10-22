package com.ecommerce.admin.user.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.ecommerce.admin.role.domain.Role;
import com.ecommerce.admin.user.exception.LastAdminException;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class LastAdminGuardTest {

    private static final long USER_ID = 7L;

    @Mock
    private UserRepository repository;

    @InjectMocks
    private LastAdminGuard guard;

    @Test
    void blocksRemovingTheLastEnabledAdmin() {
        var admin = user(Role.ADMIN);
        when(repository.countOtherEnabledUsersWithRole(Role.ADMIN, USER_ID)).thenReturn(0L);

        assertThatThrownBy(() -> guard.ensureAnotherAdminRemains(admin))
                .isInstanceOf(LastAdminException.class)
                .hasMessage("At least one enabled administrator is required");
    }

    @Test
    void allowsItWhenAnotherEnabledAdminExists() {
        var admin = user(Role.ADMIN);
        when(repository.countOtherEnabledUsersWithRole(Role.ADMIN, USER_ID)).thenReturn(1L);

        assertThatCode(() -> guard.ensureAnotherAdminRemains(admin)).doesNotThrowAnyException();
        verify(repository).countOtherEnabledUsersWithRole(Role.ADMIN, USER_ID);
    }

    @Test
    void ignoresUsersWhoAreNotAdmins() {
        var stockManager = user("STOCK_MANAGER");

        assertThatCode(() -> guard.ensureAnotherAdminRemains(stockManager)).doesNotThrowAnyException();
        verifyNoInteractions(repository);
    }

    @Test
    void ignoresAdminsWhoAreAlreadyDisabled() {
        var disabledAdmin = user(Role.ADMIN);
        disabledAdmin.disable();

        assertThatCode(() -> guard.ensureAnotherAdminRemains(disabledAdmin)).doesNotThrowAnyException();
        verifyNoInteractions(repository);
    }

    private static User user(String roleName) {
        var user = new User("someone@shop.local", "hash", "Some", "One");
        ReflectionTestUtils.setField(user, "id", USER_ID);
        user.assignRoles(Set.of(new Role(roleName, null)));
        return user;
    }
}
