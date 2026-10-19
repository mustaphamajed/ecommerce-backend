package com.ecommerce.admin.user.bootstrap;

import com.ecommerce.admin.role.domain.Role;
import com.ecommerce.admin.role.domain.RoleRepository;
import com.ecommerce.admin.user.domain.User;
import com.ecommerce.admin.user.domain.UserRepository;
import com.ecommerce.admin.warehouse.domain.WarehouseRepository;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final WarehouseRepository warehouseRepository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;

    public AdminBootstrap(
            UserRepository userRepository,
            RoleRepository roleRepository,
            WarehouseRepository warehouseRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap.admin.email}") String email,
            @Value("${app.bootstrap.admin.password}") String password) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.warehouseRepository = warehouseRepository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.password = password;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.count() > 0) {
            return;
        }
        if (email.isBlank() || password.isBlank()) {
            log.warn("No users exist and no bootstrap admin is configured: nobody can log in");
            return;
        }
        var adminRole = roleRepository.findByName(Role.ADMIN)
                .orElseThrow(() -> new IllegalStateException("The ADMIN role is missing, check data.sql"));
        var admin = new User(email, passwordEncoder.encode(password), "Admin", "Admin");
        admin.assignRoles(Set.of(adminRole));
        admin.assignWarehouses(new HashSet<>(warehouseRepository.findAll()));
        userRepository.save(admin);
        log.info("Created the initial admin user {}", admin.getEmail());
    }
}
