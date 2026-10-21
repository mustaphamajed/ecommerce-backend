package com.ecommerce.admin.demo;

import com.ecommerce.admin.category.command.create.CreateCategoryCommand;
import com.ecommerce.admin.category.command.create.CreateCategoryHandler;
import com.ecommerce.admin.category.domain.Category;
import com.ecommerce.admin.category.domain.CategoryRepository;
import com.ecommerce.admin.product.command.archive.ArchiveProductCommand;
import com.ecommerce.admin.product.command.archive.ArchiveProductHandler;
import com.ecommerce.admin.product.command.create.CreateProductCommand;
import com.ecommerce.admin.product.command.create.CreateProductHandler;
import com.ecommerce.admin.product.domain.ProductRepository;
import com.ecommerce.admin.role.domain.Role;
import com.ecommerce.admin.role.domain.RoleRepository;
import com.ecommerce.admin.user.command.create.CreateUserCommand;
import com.ecommerce.admin.user.command.create.CreateUserHandler;
import com.ecommerce.admin.user.command.disable.DisableUserCommand;
import com.ecommerce.admin.user.command.disable.DisableUserHandler;
import com.ecommerce.admin.user.domain.UserRepository;
import com.ecommerce.admin.warehouse.domain.Warehouse;
import com.ecommerce.admin.warehouse.domain.WarehouseRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(2)
@ConditionalOnProperty(name = "app.seed.demo-data", havingValue = "true")
public class DemoDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    private final WarehouseRepository warehouseRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CreateUserHandler createUser;
    private final DisableUserHandler disableUser;
    private final CreateCategoryHandler createCategory;
    private final CreateProductHandler createProduct;
    private final ArchiveProductHandler archiveProduct;
    private final String demoPassword;

    public DemoDataSeeder(
            WarehouseRepository warehouseRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            CreateUserHandler createUser,
            DisableUserHandler disableUser,
            CreateCategoryHandler createCategory,
            CreateProductHandler createProduct,
            ArchiveProductHandler archiveProduct,
            @Value("${app.seed.demo-password}") String demoPassword) {
        this.warehouseRepository = warehouseRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.createUser = createUser;
        this.disableUser = disableUser;
        this.createCategory = createCategory;
        this.createProduct = createProduct;
        this.archiveProduct = archiveProduct;
        this.demoPassword = demoPassword;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        var warehouseIds = seedWarehouses();
        seedUsers(warehouseIds);
        seedCatalog();
    }

    private Map<String, Long> seedWarehouses() {
        Map<String, Long> idsByCode = new HashMap<>();
        int created = 0;
        for (DemoWarehouse demo : DemoAccounts.WAREHOUSES) {
            var existing = warehouseRepository.findByCode(demo.code());
            if (existing.isEmpty()) {
                created++;
            }
            var warehouse = existing.orElseGet(
                    () -> warehouseRepository.save(new Warehouse(demo.code(), demo.name())));
            idsByCode.put(warehouse.getCode(), warehouse.getId());
        }
        log.info("Demo warehouses: {} created, {} already present", created, DemoAccounts.WAREHOUSES.size() - created);
        return idsByCode;
    }

    private void seedUsers(Map<String, Long> warehouseIds) {
        Map<String, Long> roleIds = roleRepository.findAll().stream()
                .collect(Collectors.toMap(Role::getName, Role::getId));
        int created = 0;
        for (DemoUser demo : DemoAccounts.USERS) {
            if (userRepository.existsByEmail(demo.email().toLowerCase(Locale.ROOT))) {
                continue;
            }
            Long userId = createUser.handle(new CreateUserCommand(
                    demo.email(),
                    demo.firstName(),
                    demo.lastName(),
                    demoPassword,
                    idsOf(demo.roles(), roleIds),
                    idsOf(demo.warehouseCodes(), warehouseIds)));
            if (!demo.enabled()) {
                disableUser.handle(new DisableUserCommand(userId));
            }
            created++;
        }
        log.info("Demo users: {} created, {} already present", created, DemoAccounts.USERS.size() - created);
    }

    private void seedCatalog() {
        if (productRepository.count() > 0) {
            log.info("Demo catalog skipped: products already exist");
            return;
        }
        Map<String, Long> categoryIds = new HashMap<>();
        for (DemoCategory category : DemoCatalog.CATEGORIES) {
            categoryIds.put(category.name(), findOrCreateCategory(category));
        }
        for (DemoProduct product : DemoCatalog.PRODUCTS) {
            Long productId = createProduct.handle(new CreateProductCommand(
                    product.sku(),
                    product.name(),
                    product.description(),
                    product.price(),
                    categoryIds.get(product.category())));
            if (product.archived()) {
                archiveProduct.handle(new ArchiveProductCommand(productId));
            }
        }
        log.info("Demo catalog: {} products in {} categories created",
                DemoCatalog.PRODUCTS.size(), DemoCatalog.CATEGORIES.size());
    }

    private Long findOrCreateCategory(DemoCategory category) {
        return categoryRepository.findByNameIgnoreCase(category.name())
                .map(Category::getId)
                .orElseGet(() -> createCategory.handle(
                        new CreateCategoryCommand(category.name(), category.description(), null)));
    }

    private static Set<Long> idsOf(List<String> keys, Map<String, Long> idsByKey) {
        return keys.stream()
                .map(key -> {
                    Long id = idsByKey.get(key);
                    if (id == null) {
                        throw new IllegalStateException("Unknown demo reference: " + key);
                    }
                    return id;
                })
                .collect(Collectors.toSet());
    }
}
