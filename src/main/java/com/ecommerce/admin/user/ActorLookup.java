package com.ecommerce.admin.user;

import com.ecommerce.admin.role.domain.Role;
import com.ecommerce.admin.user.domain.User;
import com.ecommerce.admin.user.domain.UserRepository;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ActorLookup {

    private final UserRepository users;

    public ActorLookup(UserRepository users) {
        this.users = users;
    }

    public Actor find(Long userId) {
        return toActor(users.findByIdOrThrow(userId));
    }

    private static Actor toActor(User user) {
        return new Actor(
                user.getId(),
                user.getFirstName() + " " + user.getLastName(),
                user.hasRole(Role.ADMIN),
                Set.copyOf(user.getWarehouseIds()));
    }
}
