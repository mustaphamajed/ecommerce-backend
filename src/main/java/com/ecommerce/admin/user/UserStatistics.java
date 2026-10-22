package com.ecommerce.admin.user;

import com.ecommerce.admin.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserStatistics {

    private final UserRepository users;

    public UserStatistics(UserRepository users) {
        this.users = users;
    }

    public UserCounts counts() {
        return new UserCounts(users.countByEnabled(true), users.countByEnabled(false));
    }
}
